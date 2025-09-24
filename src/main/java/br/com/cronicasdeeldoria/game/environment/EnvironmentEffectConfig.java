package br.com.cronicasdeeldoria.game.environment;

import java.util.List;
import java.util.Map;

public class EnvironmentEffectConfig {
    public static class AlsoEffect {
        public String effectId;
        public String drawMode;
        public Float alpha;
    }

    public static class EffectRule {
        public String effectId;          // ex., "rain", "snow"
        public List<String> seasons;     // SPRING, SUMMER, AUTUMN, WINTER
        public String startTime;         // "HH:mm"
        public String endTime;           // "HH:mm"
        public int probability;          // 0..100
        public String drawMode;          // STRETCH, TILE, CENTER
        public Float alpha;              // 0..1
        public List<AlsoEffect> also;    // Adicionar efeitos secundários
    }

    public Map<String, List<EffectRule>> mapRules; // mapName
}



