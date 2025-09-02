package br.com.cronicasdeeldoria.game.ui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Mapeia as teclas usando imagens GIF separadas
 */
public class KeyboardMapper {
    
    private Map<String, AnimatedKeySprite> keySprites;
    private KeyboardMappingLoader mappingLoader;
    
    public KeyboardMapper() {
        this.keySprites = new HashMap<>();
        this.mappingLoader = new KeyboardMappingLoader();
        initializeKeyMappings();
    }
    
    /**
     * Inicializa o mapeamento das teclas
     */
    private void initializeKeyMappings() {
        // Carregar mapeamentos do arquivo JSON
        Map<String, KeyboardMappingLoader.KeyMapping> mappings = mappingLoader.getAllMappings();
        for (KeyboardMappingLoader.KeyMapping mapping : mappings.values()) {
            loadKeyImage(mapping.getKey(), mapping.getImagePath());
        }
    }
    
    /**
     * Carrega uma imagem de tecla específica
     * @param keyName Nome da tecla
     * @param imagePath Caminho para a imagem
     */
    public void loadKeyImage(String keyName, String imagePath) {
        try {
            ImageIcon imageIcon = new ImageIcon(getClass().getResource(imagePath));
            if (imageIcon.getImage() != null) {
                AnimatedKeySprite animatedSprite = new AnimatedKeySprite(imageIcon);
                keySprites.put(keyName, animatedSprite);
            } else {
                System.err.println("Erro: Não foi possível carregar a imagem " + imagePath);
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar imagem da tecla " + keyName + ": " + e.getMessage());
        }
    }
    
    /**
     * Obtém o sprite de uma tecla específica
     * @param keyName Nome da tecla
     * @return Sprite da tecla ou null se não encontrada
     */
    public BufferedImage getKeySprite(String keyName) {
        AnimatedKeySprite animatedSprite = keySprites.get(keyName);
        if (animatedSprite != null) {
            return animatedSprite.getCurrentFrame();
        }
        return null;
    }
    
    /**
     * Obtém o ImageIcon de uma tecla específica (para animação)
     * @param keyName Nome da tecla
     * @return ImageIcon da tecla ou null se não encontrada
     */
    public ImageIcon getKeyImageIcon(String keyName) {
        AnimatedKeySprite animatedSprite = keySprites.get(keyName);
        if (animatedSprite != null) {
            return animatedSprite.getImageIcon();
        }
        return null;
    }
    
    /**
     * Verifica se uma tecla está mapeada
     * @param keyName Nome da tecla
     * @return true se a tecla está mapeada
     */
    public boolean hasKey(String keyName) {
        return keySprites.containsKey(keyName);
    }
    
    /**
     * Obtém todas as teclas mapeadas
     * @return Conjunto de nomes das teclas
     */
    public java.util.Set<String> getMappedKeys() {
        return keySprites.keySet();
    }
    
    /**
     * Adiciona uma tecla dinamicamente
     * @param keyName Nome da tecla
     * @param imagePath Caminho para a imagem
     * @return true se a tecla foi adicionada com sucesso
     */
    public boolean addKeyDynamically(String keyName, String imagePath) {
        try {
            loadKeyImage(keyName, imagePath);
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao adicionar tecla dinâmica: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Obtém informações sobre um mapeamento específico
     * @param keyName Nome da tecla
     * @return Informações do mapeamento ou null se não encontrado
     */
    public KeyboardMappingLoader.KeyMapping getMappingInfo(String keyName) {
        return mappingLoader.getMapping(keyName);
    }
    
    /**
     * Classe interna para gerenciar sprites animados
     */
    private static class AnimatedKeySprite {
        private ImageIcon imageIcon;
        private Timer animationTimer;
        private BufferedImage currentFrame;
        
        public AnimatedKeySprite(ImageIcon imageIcon) {
            this.imageIcon = imageIcon;
            this.currentFrame = new BufferedImage(
                imageIcon.getIconWidth(), 
                imageIcon.getIconHeight(), 
                BufferedImage.TYPE_INT_ARGB
            );
            
            // Criar timer para atualizar a animação
            this.animationTimer = new Timer(100, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateFrame();
                }
            });
            animationTimer.start();
        }
        
        private void updateFrame() {
            Graphics2D g2d = currentFrame.createGraphics();
            g2d.drawImage(imageIcon.getImage(), 0, 0, null);
            g2d.dispose();
        }
        
        public BufferedImage getCurrentFrame() {
            return currentFrame;
        }
        
        public ImageIcon getImageIcon() {
            return imageIcon;
        }
    }
}
