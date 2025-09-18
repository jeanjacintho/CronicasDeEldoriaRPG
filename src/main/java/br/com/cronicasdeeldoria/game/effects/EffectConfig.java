package br.com.cronicasdeeldoria.game.effects;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EffectConfig {
  public static class Trigger {
    public final String imagePath;
    public final long durationMs;

    public Trigger(String imagePath, long durationMs) {
      this.imagePath = imagePath;
      this.durationMs = durationMs;
    }
  }

  private final Map<String, Trigger> actionToTrigger = new HashMap<>();

  public static EffectConfig fromJson(JsonNode node) {
    EffectConfig cfg = new EffectConfig();
    if (node == null) return cfg;
    Iterator<String> fields = node.fieldNames();
    while (fields.hasNext()) {
      String action = fields.next();
      JsonNode def = node.get(action);
      String imagePath = def.path("imagePath").asText(null);
      long durationMs = def.path("durationMs").asLong(600);
      cfg.actionToTrigger.put(action.toLowerCase(), new Trigger(imagePath, durationMs));
    }
    return cfg;
  }

  public Trigger get(String actionKey) {
    if (actionKey == null) return null;
    return actionToTrigger.get(actionKey.toLowerCase());
  }
}

