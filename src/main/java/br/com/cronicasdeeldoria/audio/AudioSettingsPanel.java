package br.com.cronicasdeeldoria.audio;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Interface de configuração de áudio para o jogo.
 * Permite ao usuário ajustar volumes e configurações de áudio.
 */
public class AudioSettingsPanel extends JPanel {
    
    private final AudioManager audioManager;
    private final AudioConfigLoader configLoader;
    
    // Componentes da interface
    private JSlider masterVolumeSlider;
    private JSlider musicVolumeSlider;
    private JSlider sfxVolumeSlider;
    private JCheckBox muteCheckBox;
    private JCheckBox musicEnabledCheckBox;
    private JCheckBox sfxEnabledCheckBox;
    private JButton testMusicButton;
    private JButton testSfxButton;
    private JButton resetButton;
    private JButton applyButton;
    
    // Labels para mostrar valores
    private JLabel masterVolumeLabel;
    private JLabel musicVolumeLabel;
    private JLabel sfxVolumeLabel;
    
    public AudioSettingsPanel() {
        this.audioManager = AudioManager.getInstance();
        this.configLoader = AudioConfigLoader.getInstance();
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadCurrentSettings();
    }
    
    /**
     * Inicializa os componentes da interface.
     */
    private void initializeComponents() {
        // Sliders de volume
        masterVolumeSlider = new JSlider(0, 100, (int) (audioManager.getMasterVolume() * 100));
        musicVolumeSlider = new JSlider(0, 100, (int) (audioManager.getMusicVolume() * 100));
        sfxVolumeSlider = new JSlider(0, 100, (int) (audioManager.getSfxVolume() * 100));
        
        // Checkboxes
        muteCheckBox = new JCheckBox("Silenciar tudo", audioManager.isMuted());
        musicEnabledCheckBox = new JCheckBox("Música habilitada", audioManager.isMusicEnabled());
        sfxEnabledCheckBox = new JCheckBox("Efeitos sonoros habilitados", audioManager.isSfxEnabled());
        
        // Botões
        testMusicButton = new JButton("Testar Música");
        testSfxButton = new JButton("Testar SFX");
        resetButton = new JButton("Restaurar Padrões");
        applyButton = new JButton("Aplicar");
        
        // Labels de valores
        masterVolumeLabel = new JLabel("70%");
        musicVolumeLabel = new JLabel("60%");
        sfxVolumeLabel = new JLabel("80%");
        
        updateVolumeLabels();
    }
    
    /**
     * Configura o layout da interface.
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Painel principal
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Seção de Volume
        JPanel volumePanel = createVolumePanel();
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(volumePanel, gbc);
        
        // Seção de Configurações
        JPanel settingsPanel = createSettingsPanel();
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(settingsPanel, gbc);
        
        // Seção de Testes
        JPanel testPanel = createTestPanel();
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(testPanel, gbc);
        
        // Seção de Botões
        JPanel buttonPanel = createButtonPanel();
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(buttonPanel, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    /**
     * Cria o painel de configuração de volume.
     */
    private JPanel createVolumePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), 
            "Controle de Volume", 
            TitledBorder.LEFT, 
            TitledBorder.TOP
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Volume Master
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Volume Master:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(masterVolumeSlider, gbc);
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        panel.add(masterVolumeLabel, gbc);
        
        // Volume Música
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Volume Música:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(musicVolumeSlider, gbc);
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        panel.add(musicVolumeLabel, gbc);
        
        // Volume SFX
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Volume SFX:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(sfxVolumeSlider, gbc);
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        panel.add(sfxVolumeLabel, gbc);
        
        return panel;
    }
    
    /**
     * Cria o painel de configurações gerais.
     */
    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), 
            "Configurações", 
            TitledBorder.LEFT, 
            TitledBorder.TOP
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(muteCheckBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(musicEnabledCheckBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(sfxEnabledCheckBox, gbc);
        
        return panel;
    }
    
    /**
     * Cria o painel de testes de áudio.
     */
    private JPanel createTestPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), 
            "Testes", 
            TitledBorder.LEFT, 
            TitledBorder.TOP
        ));
        
        panel.add(testMusicButton);
        panel.add(testSfxButton);
        
        return panel;
    }
    
    /**
     * Cria o painel de botões de ação.
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        panel.add(resetButton);
        panel.add(applyButton);
        
        return panel;
    }
    
    /**
     * Configura os event listeners.
     */
    private void setupEventListeners() {
        // Sliders de volume
        masterVolumeSlider.addChangeListener(e -> {
            updateVolumeLabels();
            audioManager.setMasterVolume(masterVolumeSlider.getValue() / 100.0f);
        });
        
        musicVolumeSlider.addChangeListener(e -> {
            updateVolumeLabels();
            audioManager.setMusicVolume(musicVolumeSlider.getValue() / 100.0f);
        });
        
        sfxVolumeSlider.addChangeListener(e -> {
            updateVolumeLabels();
            audioManager.setSfxVolume(sfxVolumeSlider.getValue() / 100.0f);
        });
        
        // Checkboxes
        muteCheckBox.addActionListener(e -> {
            audioManager.setMuted(muteCheckBox.isSelected());
            updateControlsState();
        });
        
        musicEnabledCheckBox.addActionListener(e -> {
            audioManager.setMusicEnabled(musicEnabledCheckBox.isSelected());
        });
        
        sfxEnabledCheckBox.addActionListener(e -> {
            audioManager.setSfxEnabled(sfxEnabledCheckBox.isSelected());
        });
        
        // Botões
        testMusicButton.addActionListener(e -> testMusic());
        testSfxButton.addActionListener(e -> testSoundEffect());
        resetButton.addActionListener(e -> resetToDefaults());
        applyButton.addActionListener(e -> applySettings());
    }
    
    /**
     * Atualiza os labels de volume.
     */
    private void updateVolumeLabels() {
        masterVolumeLabel.setText(masterVolumeSlider.getValue() + "%");
        musicVolumeLabel.setText(musicVolumeSlider.getValue() + "%");
        sfxVolumeLabel.setText(sfxVolumeSlider.getValue() + "%");
    }
    
    /**
     * Atualiza o estado dos controles baseado nas configurações.
     */
    private void updateControlsState() {
        boolean enabled = !muteCheckBox.isSelected();
        masterVolumeSlider.setEnabled(enabled);
        musicVolumeSlider.setEnabled(enabled);
        sfxVolumeSlider.setEnabled(enabled);
        musicEnabledCheckBox.setEnabled(enabled);
        sfxEnabledCheckBox.setEnabled(enabled);
    }
    
    /**
     * Carrega as configurações atuais do AudioManager.
     */
    private void loadCurrentSettings() {
        masterVolumeSlider.setValue((int) (audioManager.getMasterVolume() * 100));
        musicVolumeSlider.setValue((int) (audioManager.getMusicVolume() * 100));
        sfxVolumeSlider.setValue((int) (audioManager.getSfxVolume() * 100));
        
        muteCheckBox.setSelected(audioManager.isMuted());
        musicEnabledCheckBox.setSelected(audioManager.isMusicEnabled());
        sfxEnabledCheckBox.setSelected(audioManager.isSfxEnabled());
        
        updateVolumeLabels();
        updateControlsState();
    }
    
    /**
     * Testa a reprodução de música.
     */
    private void testMusic() {
        // Reproduzir música de teste baseada no contexto atual
        AudioContext currentContext = audioManager.getCurrentContext();
        if (currentContext != null) {
            String musicFile = configLoader.getMusicFile(currentContext.name().toLowerCase());
            if (musicFile != null) {
                audioManager.playMusic(musicFile, false);
            }
        }
    }
    
    /**
     * Testa a reprodução de efeito sonoro.
     */
    private void testSoundEffect() {
        audioManager.playSoundEffect("notification");
    }
    
    /**
     * Restaura as configurações padrão.
     */
    private void resetToDefaults() {
        AudioConfigLoader.AudioSettings defaultSettings = configLoader.getAudioSettings();
        
        masterVolumeSlider.setValue((int) (defaultSettings.masterVolume * 100));
        musicVolumeSlider.setValue((int) (defaultSettings.musicVolume * 100));
        sfxVolumeSlider.setValue((int) (defaultSettings.sfxVolume * 100));
        
        muteCheckBox.setSelected(false);
        musicEnabledCheckBox.setSelected(true);
        sfxEnabledCheckBox.setSelected(true);
        
        updateVolumeLabels();
        updateControlsState();
    }
    
    /**
     * Aplica as configurações atuais.
     */
    private void applySettings() {
        // As configurações já são aplicadas em tempo real pelos listeners
        // Este método pode ser usado para salvar as configurações em arquivo
        JOptionPane.showMessageDialog(this, 
            "Configurações de áudio aplicadas com sucesso!", 
            "Configurações", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Cria e exibe uma janela de configuração de áudio.
     */
    public static void showAudioSettingsDialog(Component parent) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), 
            "Configurações de Áudio", true);
        
        AudioSettingsPanel settingsPanel = new AudioSettingsPanel();
        dialog.add(settingsPanel);
        
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }
}
