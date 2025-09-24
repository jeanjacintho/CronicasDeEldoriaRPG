package br.com.cronicasdeeldoria.game.environment;

import com.google.gson.Gson;
import javax.swing.ImageIcon;
import java.awt.Graphics2D;
import java.io.InputStream;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class EnvironmentEffectManager {
    private static final String BASE_PATH = "/sprites/world/effects/";
    private final Random random = new Random();

    private EnvironmentEffectConfig config;
    private final Map<String, ImageIcon> cache = new HashMap<>();
    private final List<EnvironmentEffect> activeEffects = new ArrayList<>();

    public void loadConfig(String resourcePath) throws Exception {
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) throw new IllegalStateException("Config not found: " + resourcePath);
            Gson gson = new Gson();
            this.config = gson.fromJson(new java.io.InputStreamReader(is), EnvironmentEffectConfig.class);
        }
    }

    public void selectEffectForMap(String mapName, Season currentSeason, LocalTime currentTime) {
        activeEffects.clear();
        if (config == null || config.mapRules == null) return;

        String key = mapName == null ? "" : mapName;
        List<EnvironmentEffectConfig.EffectRule> rules = config.mapRules.get(key);
        if ((rules == null || rules.isEmpty()) && key.contains("/")) {
            String normalized = key.substring(key.lastIndexOf('/') + 1);
            rules = config.mapRules.get(normalized);
        }
        if (rules == null || rules.isEmpty()) return;

        List<EnvironmentEffectConfig.EffectRule> eligible = new ArrayList<>();
        for (EnvironmentEffectConfig.EffectRule rule : rules) {
            if (rule.seasons != null && !rule.seasons.isEmpty()) {
                boolean seasonOk = rule.seasons.stream().anyMatch(s -> s.equalsIgnoreCase(currentSeason.name()));
                if (!seasonOk) continue;
            }
            if (isWithinTime(rule.startTime, rule.endTime, currentTime)) {
                eligible.add(rule);
            }
        }

        if (eligible.isEmpty()) return;

        List<EnvironmentEffectConfig.EffectRule> selected = new ArrayList<>();
        for (EnvironmentEffectConfig.EffectRule rule : eligible) {
            int p = rule.probability <= 0 ? 0 : Math.min(100, rule.probability);
            if (p >= 100 || random.nextInt(100) < p) {
                selected.add(rule);
            }
        }

        if (selected.isEmpty()) {
            EnvironmentEffectConfig.EffectRule top = eligible.stream()
                    .max(Comparator.comparingInt(r -> r.probability))
                    .orElse(eligible.get(0));
            selected.add(top);
        }

        for (EnvironmentEffectConfig.EffectRule rule : selected) {
            addEffectFromRule(key, rule.effectId, rule.drawMode, rule.alpha);
            if (rule.also != null) {
                for (EnvironmentEffectConfig.AlsoEffect also : rule.also) {
                    if (also == null) continue;
                    String amode = also.drawMode;
                    Float aalpha = also.alpha;
                    addEffectFromRule(key, also.effectId, amode, aalpha);
                }
            }
        }
    }

    private void addEffectFromRule(String mapKey, String effectId, String drawModeText, Float alphaValue) {
        if (effectId == null || effectId.isBlank()) return;
        ImageIcon icon = loadIcon(effectId);
        EnvironmentEffect.DrawMode drawMode = parseMode(drawModeText);
        float alpha = alphaValue != null ? Math.max(0f, Math.min(1f, alphaValue)) : 1.0f;
        EnvironmentEffect effect = new EnvironmentEffect(effectId, icon, drawMode, alpha);
        activeEffects.add(effect);
    }

    public void draw(Graphics2D g2, int screenWidth, int screenHeight) {
        if (activeEffects.isEmpty()) return;
        for (EnvironmentEffect effect : activeEffects) {
            effect.draw(g2, screenWidth, screenHeight);
        }
    }

    public void drawWorldRelative(Graphics2D g2, int worldOriginX, int worldOriginY, int screenWidth, int screenHeight) {
        if (activeEffects.isEmpty()) return;
        for (EnvironmentEffect effect : activeEffects) {
            effect.drawWorldRelative(g2, worldOriginX, worldOriginY, screenWidth, screenHeight);
        }
    }

    private ImageIcon loadIcon(String effectId) {
        if (effectId == null) return null;
        return cache.computeIfAbsent(effectId, id -> {
            String trimmed = id.trim();
            String base;
            String ext = null;
            int dot = trimmed.lastIndexOf('.');
            if (dot > 0 && dot < trimmed.length() - 1) {
                base = trimmed.substring(0, dot);
                ext = trimmed.substring(dot + 1).toLowerCase(Locale.ROOT);
            } else {
                base = trimmed;
            }

            ImageIcon icon = null;

            if (ext != null) {
                icon = tryLoad(BASE_PATH + base + "." + ext);
            } else {
                icon = tryLoad(BASE_PATH + base);
            }

            if (icon == null || icon.getIconWidth() <= 0) {
                icon = tryLoad(BASE_PATH + base + ".gif");
            }
            if (icon == null || icon.getIconWidth() <= 0) {
                icon = tryLoad(BASE_PATH + base + ".png");
            }

            if (icon == null || icon.getIconWidth() <= 0) {
                System.err.println("[EnvEffect] Icon not found for id='" + id + "' (base='" + base + "') under " + BASE_PATH);
            }
            return icon;
        });
    }

    private ImageIcon tryLoad(String resourcePath) {
        java.net.URL url = getClass().getResource(resourcePath);
        if (url == null) return null;
        return new ImageIcon(url);
    }

    private boolean isWithinTime(String start, String end, LocalTime t) {
        if (t == null) t = LocalTime.now();
        if ((start == null || start.isBlank()) && (end == null || end.isBlank())) return true;
        LocalTime s = start != null && !start.isBlank() ? parseTime(start) : LocalTime.MIN;
        LocalTime e = end != null && !end.isBlank() ? parseTime(end) : LocalTime.MAX;
        if (e.isAfter(s) || e.equals(s)) {
            return !t.isBefore(s) && t.isBefore(e);
        } else { // Escurecer tela
            return !t.isBefore(s) || t.isBefore(e);
        }
    }

    private LocalTime parseTime(String value) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
        if (value == null || value.isBlank()) return LocalTime.MIN;
        String v = value.trim();
        if ("24:00".equals(v) || "24:00:00".equals(v)) {
            return LocalTime.MAX;
        }
        return LocalTime.parse(v, fmt);
    }

    private EnvironmentEffect.DrawMode parseMode(String mode) {
        if (mode == null) return EnvironmentEffect.DrawMode.STRETCH;
        try {
            return EnvironmentEffect.DrawMode.valueOf(mode.toUpperCase());
        } catch (Exception e) {
            return EnvironmentEffect.DrawMode.STRETCH;
        }
    }

    public boolean hasActiveEffect() {
        return !activeEffects.isEmpty();
    }
}


