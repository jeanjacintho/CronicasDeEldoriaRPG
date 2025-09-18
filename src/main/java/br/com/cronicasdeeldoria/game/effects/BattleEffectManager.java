package br.com.cronicasdeeldoria.game.effects;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Manages configurable battle overlay effects (animated GIFs/images) per class and action.
 */
public class BattleEffectManager {

  public enum TargetType { PLAYER, MONSTER }

  public static class ActiveEffect {
    public final TargetType targetType;
    public final String effectKey;
    public final Image image;
    public final long startTimeMs;
    public final long durationMs;

    public ActiveEffect(TargetType targetType, String effectKey, Image image, long durationMs) {
      this.targetType = targetType;
      this.effectKey = effectKey;
      this.image = image;
      this.startTimeMs = System.currentTimeMillis();
      this.durationMs = durationMs;
    }

    public boolean isExpired() {
      return System.currentTimeMillis() - startTimeMs > durationMs;
    }
  }

  private final Map<String, EffectConfig> classToConfigPlayer = new HashMap<>();
  private final Map<String, EffectConfig> classToConfigMonster = new HashMap<>();
  private final Map<String, String> mapToBackground = new HashMap<>();

  private ActiveEffect playerEffect;
  private ActiveEffect monsterEffect;

  public void loadConfig(String resourcePath) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
      if (is == null) {
        throw new IOException("Efeitos de batalha não encontrados: " + resourcePath);
      }
      JsonNode root = mapper.readTree(is);

      // Player configs
      JsonNode playerNode = root.path("player");
      Iterator<String> pFields = playerNode.fieldNames();
      while (pFields.hasNext()) {
        String cls = pFields.next();
        classToConfigPlayer.put(cls.toLowerCase(), EffectConfig.fromJson(playerNode.get(cls)));
      }

      // Monster configs
      JsonNode monsterNode = root.path("monster");
      Iterator<String> mFields = monsterNode.fieldNames();
      while (mFields.hasNext()) {
        String cls = mFields.next();
        classToConfigMonster.put(cls.toLowerCase(), EffectConfig.fromJson(monsterNode.get(cls)));
      }

      // Battle backgrounds
      JsonNode backgroundsNode = root.path("battleBackgrounds");
      Iterator<String> bgFields = backgroundsNode.fieldNames();
      while (bgFields.hasNext()) {
        String mapKey = bgFields.next();
        String bgPath = backgroundsNode.get(mapKey).asText();
        mapToBackground.put(mapKey.toLowerCase(), bgPath);
      }
    }
  }

  public void triggerForPlayer(String playerClassName, String actionKey) {
    EffectConfig cfg = resolveConfig(classToConfigPlayer, playerClassName);
    EffectConfig.Trigger def = cfg.get(actionKey);
    if (def == null) return;
    Image img = loadImage(def.imagePath);
    if (img == null) return;
    playerEffect = new ActiveEffect(TargetType.PLAYER, actionKey, img, def.durationMs);
  }

  public void triggerForMonster(String monsterClassKey, String actionKey) {
    EffectConfig cfg = resolveConfig(classToConfigMonster, monsterClassKey);
    EffectConfig.Trigger def = cfg.get(actionKey);
    if (def == null) return;
    Image img = loadImage(def.imagePath);
    if (img == null) return;
    monsterEffect = new ActiveEffect(TargetType.MONSTER, actionKey, img, def.durationMs);
  }

  // Auxiliares de cross-trigger: use a configuração de um lado para renderizar no alvo oposto
  public void triggerForMonsterFromPlayer(String playerClassName, String actionKey) {
    EffectConfig cfg = resolveConfig(classToConfigPlayer, playerClassName);
    EffectConfig.Trigger def = cfg.get(actionKey);
    if (def == null) return;
    Image img = loadImage(def.imagePath);
    if (img == null) return;
    monsterEffect = new ActiveEffect(TargetType.MONSTER, actionKey, img, def.durationMs);
  }

  public void triggerForPlayerFromMonster(String monsterClassKey, String actionKey) {
    EffectConfig cfg = resolveConfig(classToConfigMonster, monsterClassKey);
    EffectConfig.Trigger def = cfg.get(actionKey);
    if (def == null) return;
    Image img = loadImage(def.imagePath);
    if (img == null) return;
    playerEffect = new ActiveEffect(TargetType.PLAYER, actionKey, img, def.durationMs);
  }

  public void clearAll() {
    playerEffect = null;
    monsterEffect = null;
  }

  public String getBattleBackgroundPath(String mapName) {
    if (mapName == null) return mapToBackground.get("default");
    
    // Tente a correspondência exata primeiro
    String bgPath = mapToBackground.get(mapName.toLowerCase());
    if (bgPath != null) {
      return bgPath;
    }
    
    // Correspondências parciais para mapas aninhados (e.x., "houses/player_house" -> "houses")
    String[] parts = mapName.split("/");
    for (String part : parts) {
      bgPath = mapToBackground.get(part.toLowerCase());
      if (bgPath != null) {
        return bgPath;
      }
    }
    
    // Fallback para default
    return mapToBackground.get("default");
  }

  public Image loadBattleBackground(String mapName) {
    String bgPath = getBattleBackgroundPath(mapName);
    if (bgPath == null) {
      return null;
    }
    
    Image img = loadImage(bgPath);
    return img;
  }

  public void drawOverlays(Graphics2D g2, java.awt.image.ImageObserver observer,
                           int playerX, int playerY, int monsterX, int monsterY,
                           int width, int height) {
    if (playerEffect != null) {
      if (playerEffect.isExpired()) {
        playerEffect = null;
      } else {
        g2.drawImage(playerEffect.image, playerX, playerY, width, height, observer);
      }
    }

    if (monsterEffect != null) {
      if (monsterEffect.isExpired()) {
        monsterEffect = null;
      } else {
        g2.drawImage(monsterEffect.image, monsterX, monsterY, width, height, observer);
      }
    }
  }

  private boolean lastPlayerArmorState = false;
  private boolean lastMonsterArmorState = false;
  
  // Cache para imagens de efeitos persistentes para evitar recarregar GIFs
  private Image cachedPlayerShieldImage = null;
  private Image cachedMonsterShieldImage = null;
  private Image cachedPlayerHotImage = null;
  private Image cachedMonsterDotImage = null;
  
  /**
   * Desenha efeitos persistentes com base em buffs ativos (como animação de escudo enquanto o buff de ARMADURA está ativo)
   */
  public void drawPersistentEffects(Graphics2D g2, java.awt.image.ImageObserver observer,
                                   int playerX, int playerY, int monsterX, int monsterY,
                                   int width, int height, br.com.cronicasdeeldoria.entity.character.player.Player player,
                                   br.com.cronicasdeeldoria.entity.character.npc.Npc monster) {
    
    // Desenhar animação de escudo para o jogador se o bônus de ARMADURA estiver ativo
    if (player != null && player.hasActiveBuff("ARMOR")) {
      // Registre somente quando o estado mudar
      if (!lastPlayerArmorState) {
        lastPlayerArmorState = true;
        
        // Carregue e armazene em cache a imagem do escudo somente quando o buff estiver ativado
        EffectConfig playerConfig = resolveConfig(classToConfigPlayer, player.getCharacterClass().getCharacterClassName());
        if (playerConfig != null) {
          EffectConfig.Trigger shieldTrigger = playerConfig.get("shield");
          if (shieldTrigger != null && shieldTrigger.imagePath != null) {
            cachedPlayerShieldImage = loadImage(shieldTrigger.imagePath);
          }
        }
      }
      
      if (cachedPlayerShieldImage != null) {
        g2.drawImage(cachedPlayerShieldImage, playerX, playerY, width, height, observer);
      }
    } else if (player != null) {
      if (lastPlayerArmorState) {
        lastPlayerArmorState = false;
        cachedPlayerShieldImage = null;
      }
    }
    
    // Desenhar animação de escudo para monstro se o bônus de ARMADURA estiver ativo
    if (monster != null && monster.hasActiveBuff("ARMOR")) {
      if (!lastMonsterArmorState) {
        lastMonsterArmorState = true;
        
        String monsterKey = deriveMonsterKey(monster);
        EffectConfig monsterConfig = resolveConfig(classToConfigMonster, monsterKey);
        if (monsterConfig != null) {
          EffectConfig.Trigger shieldTrigger = monsterConfig.get("shield");
          if (shieldTrigger != null && shieldTrigger.imagePath != null) {
            cachedMonsterShieldImage = loadImage(shieldTrigger.imagePath);
          }
        }
      }
      
      // Desenha a imagem em cache (o GIF continuará animando)
      if (cachedMonsterShieldImage != null) {
        g2.drawImage(cachedMonsterShieldImage, monsterX, monsterY, width, height, observer);
      }
    } else if (monster != null) {
      if (lastMonsterArmorState) {
        lastMonsterArmorState = false;
        cachedMonsterShieldImage = null;
      }
    }
    
    // Desenha efeito DOT para monstro se o bônus DOT estiver ativo
    if (monster != null && monster.hasActiveBuff("DOT")) {
      if (cachedMonsterDotImage == null) {
        String monsterKey = deriveMonsterKey(monster);
        EffectConfig monsterConfig = resolveConfig(classToConfigMonster, monsterKey);
        if (monsterConfig != null) {
          EffectConfig.Trigger dotTrigger = monsterConfig.get("dot");
          if (dotTrigger != null && dotTrigger.imagePath != null) {
            cachedMonsterDotImage = loadImage(dotTrigger.imagePath);
          }
        }
      }
      
      if (cachedMonsterDotImage != null) {
        g2.drawImage(cachedMonsterDotImage, monsterX, monsterY, width, height, observer);
      }
    } else if (monster != null) {
      cachedMonsterDotImage = null;
    }
    
    // Desenha efeito HOT para o jogador se o bônus HOT estiver ativo
    if (player != null && player.hasActiveBuff("HOT")) {
      if (cachedPlayerHotImage == null) {
        EffectConfig playerConfig = resolveConfig(classToConfigPlayer, player.getCharacterClass().getCharacterClassName());
        if (playerConfig != null) {
          EffectConfig.Trigger hotTrigger = playerConfig.get("heal");
          if (hotTrigger != null && hotTrigger.imagePath != null) {
            cachedPlayerHotImage = loadImage(hotTrigger.imagePath);
          }
        }
      }
      
      if (cachedPlayerHotImage != null) {
        g2.drawImage(cachedPlayerHotImage, playerX, playerY, width, height, observer);
      }
    } else if (player != null) {
      cachedPlayerHotImage = null;
    }
  }

  private EffectConfig resolveConfig(Map<String, EffectConfig> map, String key) {
    if (key == null) key = "default";
    EffectConfig cfg = map.get(key.toLowerCase());
    if (cfg == null) cfg = map.get("default");
    if (cfg == null) cfg = new EffectConfig();
    return cfg;
  }

  private Image loadImage(String path) {
    if (path == null) return null;
    try (InputStream is = getClass().getResourceAsStream(path)) {
      if (is == null) return null;
      // Prefer ImageIcon to preserve GIF animation frames/timing
      byte[] bytes = is.readAllBytes();
      ImageIcon icon = new ImageIcon(bytes);
      Image img = icon.getImage();
      if (img == null) {
        // Fallback to static image
        try (InputStream is2 = getClass().getResourceAsStream(path)) {
          BufferedImage bi = ImageIO.read(is2);
          return bi;
        }
      }
      return img;
    } catch (IOException e) {
      return null;
    }
  }

  private String deriveMonsterKey(br.com.cronicasdeeldoria.entity.character.npc.Npc npc) {
    if (npc == null) return "default";
    String name = npc.getName();
    if (name == null) name = "";
    name = name.toLowerCase();
    if (name.contains("orcboss")) return "orcboss";
    if (name.contains("orc")) return "orc";
    if (name.contains("frostbornboss")) return "frostbornboss";
    if (name.contains("frostborn")) return "frostborn";
    if (name.contains("wolfboss")) return "wolfboss";
    if (name.contains("wolf")) return "wolf";
    if (name.contains("skeletonboss")) return "skeletonboss";
    if (name.contains("skeleton")) return "skeleton";
    if (name.contains("suprememage") || name.contains("supreme") || name.contains("mage")) return "suprememage";
    return "default";
  }
}


