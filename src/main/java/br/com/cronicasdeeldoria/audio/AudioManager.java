package br.com.cronicasdeeldoria.audio;

import javax.sound.sampled.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Sistema avançado de gerenciamento de áudio para diferentes contextos do jogo.
 * Gerencia reprodução de música de fundo, efeitos sonoros e transições suaves.
 */
public class AudioManager {
    
    private static AudioManager instance;
    private final ExecutorService audioExecutor;
    private final Map<String, AudioClip> audioClips;
    private final Map<AudioContext, String> contextMusic;
    private final Map<String, Clip> activeClips;
    
    private AudioContext currentContext;
    private Clip currentMusicClip;
    private float masterVolume;
    private float musicVolume;
    private float sfxVolume;
    private boolean isMuted;
    private boolean isMusicEnabled;
    private boolean isSfxEnabled;
    
    // Configurações de áudio
    private static final float DEFAULT_MASTER_VOLUME = 0.7f;
    private static final float DEFAULT_MUSIC_VOLUME = 0.6f;
    private static final float DEFAULT_SFX_VOLUME = 0.8f;
    private static final int MAX_CONCURRENT_SFX = 10;
    
    private AudioManager() {
        this.audioExecutor = Executors.newFixedThreadPool(4);
        this.audioClips = new ConcurrentHashMap<>();
        this.contextMusic = new HashMap<>();
        this.activeClips = new ConcurrentHashMap<>();
        
        this.masterVolume = DEFAULT_MASTER_VOLUME;
        this.musicVolume = DEFAULT_MUSIC_VOLUME;
        this.sfxVolume = DEFAULT_SFX_VOLUME;
        this.isMuted = false;
        this.isMusicEnabled = true;
        this.isSfxEnabled = true;
        
        initializeContextMusic();
        loadAudioResources();
    }
    
    public static synchronized AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }
    
    /**
     * Inicializa a configuração de música para cada contexto do jogo.
     */
    private void initializeContextMusic() {
        AudioConfigLoader configLoader = AudioConfigLoader.getInstance();
        
        // Música de mapas - usando configuração do JSON
        contextMusic.put(AudioContext.CITY, configLoader.getMusicFile("city"));
        contextMusic.put(AudioContext.FOREST, configLoader.getMusicFile("forest"));
        contextMusic.put(AudioContext.DUNGEON1, configLoader.getMusicFile("dungeon1"));
        contextMusic.put(AudioContext.DUNGEON2, configLoader.getMusicFile("dungeon2"));
        contextMusic.put(AudioContext.DUNGEON3, configLoader.getMusicFile("dungeon3"));
        contextMusic.put(AudioContext.DUNGEON4, configLoader.getMusicFile("dungeon4"));
        contextMusic.put(AudioContext.CAVE, configLoader.getMusicFile("cave"));
        contextMusic.put(AudioContext.CASTLE, configLoader.getMusicFile("castle"));
        contextMusic.put(AudioContext.PLAYER_HOUSE, configLoader.getMusicFile("player_house"));
        
        // Música de batalha - usando configuração do JSON
        contextMusic.put(AudioContext.BATTLE_NORMAL, configLoader.getMusicFile("battle_normal"));
        contextMusic.put(AudioContext.BATTLE_BOSS, configLoader.getMusicFile("battle_boss"));
        contextMusic.put(AudioContext.BATTLE_FINAL_BOSS, configLoader.getMusicFile("battle_final_boss"));
        contextMusic.put(AudioContext.BATTLE_MINIBOSS, configLoader.getMusicFile("battle_miniboss"));
        
        // Contextos especiais - usando configuração do JSON
        contextMusic.put(AudioContext.MENU, configLoader.getMusicFile("menu"));
        contextMusic.put(AudioContext.VICTORY, configLoader.getMusicFile("victory"));
        contextMusic.put(AudioContext.DEFEAT, configLoader.getMusicFile("defeat"));
        contextMusic.put(AudioContext.SILENCE, null);
        
        // Carregar configurações de áudio do JSON
        AudioConfigLoader.AudioSettings settings = configLoader.getAudioSettings();
        this.masterVolume = settings.masterVolume;
        this.musicVolume = settings.musicVolume;
        this.sfxVolume = settings.sfxVolume;
    
    }
    
    /**
     * Carrega recursos de áudio em memória para reprodução rápida.
     */
    private void loadAudioResources() {
        // Carregar efeitos sonoros comuns
        loadAudioClip("button_click", "/audio/sfx/button_click.wav");
        loadAudioClip("item_pickup", "/audio/sfx/item_pickup.wav");
        loadAudioClip("item_equip", "/audio/sfx/070_Equip_10.wav");
        loadAudioClip("item_buy", "/audio/sfx/079_Buy_sell_01.wav");
        loadAudioClip("potion_heal", "/audio/sfx/02_Heal_02.wav");
        loadAudioClip("door_open", "/audio/sfx/door_open.wav");
        loadAudioClip("teleport", "/audio/sfx/teleport.wav");
        loadAudioClip("battle_start", "/audio/sfx/battle_start.wav");
        loadAudioClip("battle_end", "/audio/sfx/battle_end.wav");
        loadAudioClip("player_attack", "/audio/sfx/56_Attack_03.wav");
        loadAudioClip("player_flee", "/audio/sfx/51_Flee_02.wav");
        loadAudioClip("player_block", "/audio/sfx/39_Block_03.wav");
        loadAudioClip("level_up", "/audio/sfx/level-win-6416.wav");
        loadAudioClip("quest_complete", "/audio/sfx/quest_complete.wav");
        loadAudioClip("error", "/audio/sfx/error.wav");
        loadAudioClip("notification", "/audio/sfx/notification.wav");
    }
    
    /**
     * Carrega um arquivo de áudio e armazena em memória.
     */
    private void loadAudioClip(String name, String resourcePath) {
        try {
            InputStream audioStream = getClass().getResourceAsStream(resourcePath);
            if (audioStream != null) {
                AudioClip clip = new AudioClip(audioStream);
                audioClips.put(name, clip);
            }
        } catch (Exception e) {
            System.err.println("Error loading audio clip " + name + ": " + e.getMessage());
        }
    }
    
    /**
     * Muda o contexto de áudio e reproduz a música apropriada.
     */
    public void changeContext(AudioContext newContext) {
        
        // Parar música atual com fade out
        stopCurrentMusic();
        
        // Atualizar contexto
        currentContext = newContext;
        
        // Reproduzir nova música com fade in
        playContextMusic();
        
    }
    
    /**
     * Reproduz a música do contexto atual.
     */
    private void playContextMusic() {
        
        String musicFile = contextMusic.get(currentContext);
        
        if (musicFile == null) {
            return;
        }
        
        // Obter volume específico do contexto
        AudioConfigLoader configLoader = AudioConfigLoader.getInstance();
        String contextName = currentContext.name().toLowerCase();
        float contextVolume = configLoader.getMusicVolume(contextName);
        
        playMusic(musicFile, true, contextVolume);
    }
    
    /**
     * Reproduz música de fundo com fade in suave.
     */
    public void playMusic(String musicFile, boolean loop) {
        playMusic(musicFile, loop, musicVolume);
    }
    
    /**
     * Reproduz música de fundo com volume específico.
     */
    public void playMusic(String musicFile, boolean loop, float specificVolume) {
        
        if (!isMusicEnabled || isMuted) {
            return;
        }
        
        audioExecutor.submit(() -> {
            try {
                
                // Parar música atual se estiver tocando
                if (currentMusicClip != null && currentMusicClip.isRunning()) {
                    currentMusicClip.stop();
                    currentMusicClip.close();
                }
                
                // Carregar nova música
                InputStream audioStream = getClass().getResourceAsStream(musicFile);
                if (audioStream == null) {
                    return;
                }
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioStream);
                currentMusicClip = AudioSystem.getClip();
                currentMusicClip.open(audioInputStream);
                
                // Configurar volume usando o volume específico do contexto (sem master volume)
                FloatControl volumeControl = (FloatControl) currentMusicClip.getControl(FloatControl.Type.MASTER_GAIN);
                float volume = calculateVolumeDirect(specificVolume);
                volumeControl.setValue(volume);
                
                // Configurar loop
                if (loop) {
                    currentMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
                }
                
                currentMusicClip.start();
                
            } catch (Exception e) {
                System.err.println("Error playing music " + musicFile + ": " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    
    /**
     * Reproduz efeito sonoro.
     */
    public void playSoundEffect(String soundName) {
        playSoundEffect(soundName, 1.0f);
    }
    
    /**
     * Reproduz efeito sonoro com volume personalizado.
     */
    public void playSoundEffect(String soundName, float volumeMultiplier) {
        if (!isSfxEnabled || isMuted) {
            return;
        }
        
        AudioClip clip = audioClips.get(soundName);
        if (clip == null) {
            System.err.println("Sound effect not found: " + soundName);
            return;
        }
        
        audioExecutor.submit(() -> {
            try {
                // Limitar número de efeitos sonoros simultâneos
                if (activeClips.size() >= MAX_CONCURRENT_SFX) {
                    stopOldestSfx();
                }
                
                Clip sfxClip = AudioSystem.getClip();
                sfxClip.open(clip.getAudioFormat(), clip.getAudioData(), 0, clip.getAudioData().length);
                
                // Configurar volume
                try {
                    FloatControl volumeControl = (FloatControl) sfxClip.getControl(FloatControl.Type.MASTER_GAIN);
                    float volume = calculateVolume(sfxVolume * volumeMultiplier);
                    volumeControl.setValue(volume);
                } catch (IllegalArgumentException e) {
                    // Se MASTER_GAIN não estiver disponível, tentar VOLUME
                    try {
                        FloatControl volumeControl = (FloatControl) sfxClip.getControl(FloatControl.Type.VOLUME);
                        float volume = calculateVolume(sfxVolume * volumeMultiplier);
                        volumeControl.setValue(volume);
                    } catch (IllegalArgumentException e2) {
                        // Se nenhum controle de volume estiver disponível, apenas reproduzir sem ajuste
                    }
                }
                
                // Armazenar referência para controle
                String clipId = soundName + "_" + System.currentTimeMillis();
                activeClips.put(clipId, sfxClip);
                
                // Reproduzir
                sfxClip.start();
                
                // Remover da lista quando terminar
                sfxClip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        activeClips.remove(clipId);
                        sfxClip.close();
                    }
                });
                
            } catch (Exception e) {
                System.err.println("Error playing sound effect " + soundName + ": " + e.getMessage());
            }
        });
    }
    
    /**
     * Para a música atual imediatamente.
     */
    private void stopCurrentMusic() {
        if (currentMusicClip != null && currentMusicClip.isRunning()) {
            currentMusicClip.stop();
            currentMusicClip.close();
        }
    }
    
    /**
     * Para toda a música e efeitos sonoros.
     */
    public void stopAllAudio() {
        stopCurrentMusic();
        
        // Parar todos os efeitos sonoros
        for (Clip clip : activeClips.values()) {
            if (clip.isRunning()) {
                clip.stop();
                clip.close();
            }
        }
        activeClips.clear();
    }
    
    /**
     * Para o efeito sonoro mais antigo para liberar recursos.
     */
    private void stopOldestSfx() {
        String oldestClipId = activeClips.keySet().iterator().next();
        Clip oldestClip = activeClips.remove(oldestClipId);
        if (oldestClip != null && oldestClip.isRunning()) {
            oldestClip.stop();
            oldestClip.close();
        }
    }
    
    
    
    /**
     * Calcula o volume baseado nas configurações.
     */
    private float calculateVolume(float baseVolume) {
        float finalVolume = baseVolume * masterVolume;
        return 20f * (float) Math.log10(Math.max(0.001f, finalVolume));
    }
    
    /**
     * Calcula o volume sem aplicar o master volume (para volumes específicos de contexto).
     */
    private float calculateVolumeDirect(float baseVolume) {
        return 20f * (float) Math.log10(Math.max(0.001f, baseVolume));
    }
    
    // Getters e Setters para configurações
    public float getMasterVolume() {
        return masterVolume;
    }
    
    public void setMasterVolume(float masterVolume) {
        this.masterVolume = Math.max(0.0f, Math.min(1.0f, masterVolume));
        updateAllVolumes();
    }
    
    public float getMusicVolume() {
        return musicVolume;
    }
    
    public void setMusicVolume(float musicVolume) {
        this.musicVolume = Math.max(0.0f, Math.min(1.0f, musicVolume));
        updateMusicVolume();
    }
    
    public float getSfxVolume() {
        return sfxVolume;
    }
    
    public void setSfxVolume(float sfxVolume) {
        this.sfxVolume = Math.max(0.0f, Math.min(1.0f, sfxVolume));
        updateSfxVolume();
    }
    
    public boolean isMuted() {
        return isMuted;
    }
    
    public void setMuted(boolean muted) {
        this.isMuted = muted;
        if (muted) {
            stopAllAudio();
        } else {
            playContextMusic();
        }
    }
    
    public boolean isMusicEnabled() {
        return isMusicEnabled;
    }
    
    public void setMusicEnabled(boolean musicEnabled) {
        this.isMusicEnabled = musicEnabled;
        if (!musicEnabled) {
            stopCurrentMusic();
        } else {
            playContextMusic();
        }
    }
    
    public boolean isSfxEnabled() {
        return isSfxEnabled;
    }
    
    public void setSfxEnabled(boolean sfxEnabled) {
        this.isSfxEnabled = sfxEnabled;
    }
    
    public AudioContext getCurrentContext() {
        return currentContext;
    }
    
    /**
     * Atualiza o volume de todos os clips ativos.
     */
    private void updateAllVolumes() {
        updateMusicVolume();
        updateSfxVolume();
    }
    
    /**
     * Atualiza o volume da música atual.
     */
    private void updateMusicVolume() {
        if (currentMusicClip != null && currentMusicClip.isRunning()) {
            FloatControl volumeControl = (FloatControl) currentMusicClip.getControl(FloatControl.Type.MASTER_GAIN);
            volumeControl.setValue(calculateVolume(musicVolume));
        }
    }
    
    /**
     * Atualiza o volume de todos os efeitos sonoros ativos.
     */
    private void updateSfxVolume() {
        for (Clip clip : activeClips.values()) {
            if (clip.isRunning()) {
                FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                volumeControl.setValue(calculateVolume(sfxVolume));
            }
        }
    }
    
    /**
     * Adiciona música personalizada para um contexto específico.
     */
    public void setContextMusic(AudioContext context, String musicFile) {
        contextMusic.put(context, musicFile);
    }
    
    /**
     * Classe interna para armazenar dados de áudio em memória.
     */
    private static class AudioClip {
        private final AudioFormat audioFormat;
        private final byte[] audioData;
        
        public AudioClip(InputStream inputStream) throws Exception {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inputStream);
            this.audioFormat = audioInputStream.getFormat();
            this.audioData = audioInputStream.readAllBytes();
            audioInputStream.close();
        }
        
        public AudioFormat getAudioFormat() {
            return audioFormat;
        }
        
        public byte[] getAudioData() {
            return audioData;
        }
    }
}
