package br.com.cronicasdeeldoria.game.ui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;

/**
 * Sistema de renderização simplificado para teclas de interação
 */
public class InteractionRenderer {
    
    private Map<String, BufferedImage> keyImages;
    private Map<String, ImageIcon> animatedKeyImages;
    
    public InteractionRenderer() {
        this.keyImages = new HashMap<>();
        this.animatedKeyImages = new HashMap<>();
        loadKeyImages();
    }
    
    /**
     * Carrega as imagens das teclas
     */
    private void loadKeyImages() {
        try {
            // Carregar imagem E-keybind
            ImageIcon eKeyIcon = new ImageIcon(getClass().getResource("/ui/e-keybind.gif"));
            if (eKeyIcon.getImage() != null) {
                // Adicionar como ImageIcon animado
                animatedKeyImages.put("E", eKeyIcon);
                
                // Criar BufferedImage estático para fallback
                BufferedImage eKeyImage = new BufferedImage(
                    eKeyIcon.getIconWidth(), 
                    eKeyIcon.getIconHeight(), 
                    BufferedImage.TYPE_INT_ARGB
                );
                Graphics2D g2d = eKeyImage.createGraphics();
                g2d.drawImage(eKeyIcon.getImage(), 0, 0, null);
                g2d.dispose();
                
                keyImages.put("E", eKeyImage);
            } else {
                System.err.println("Erro: Não foi possível carregar e-keybind.gif");
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar imagens de teclas: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Renderiza uma tecla de interação para uma entidade
     * @param g2d Graphics2D para renderização
     * @param screenX Posição X na tela
     * @param screenY Posição Y na tela
     * @param key Tecla a ser renderizada
     * @param tileSize Tamanho do tile para escalar
     */
    public void renderInteractionKey(Graphics2D g2d, int screenX, int screenY, String key, int tileSize) {
        if (key == null || key.isEmpty()) return;
        
        // Tentar usar imagem animada primeiro
        ImageIcon animatedImage = animatedKeyImages.get(key);
        if (animatedImage != null) {
            renderAnimatedKey(g2d, screenX, screenY, animatedImage, tileSize);
            return;
        }
        
        // Fallback para imagem estática
        BufferedImage staticImage = keyImages.get(key);
        if (staticImage != null) {
            renderStaticKey(g2d, screenX, screenY, staticImage, tileSize);
        }
    }
    
    /**
     * Renderiza uma tecla animada
     */
    private void renderAnimatedKey(Graphics2D g2d, int screenX, int screenY, ImageIcon imageIcon, int tileSize) {
        // Posicionar a tecla acima da entidade
        int keySize = tileSize;
        int keyX = screenX + (tileSize - keySize) / 2;
        int keyY = screenY - keySize - 10; // 10 pixels acima da entidade
        
        // Aplicar transparência para melhor visibilidade
        Composite originalComposite = g2d.getComposite();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f));
        
        g2d.drawImage(imageIcon.getImage(), keyX, keyY, keySize, keySize, null);
        
        g2d.setComposite(originalComposite);
    }
    
    /**
     * Renderiza uma tecla estática
     */
    private void renderStaticKey(Graphics2D g2d, int screenX, int screenY, BufferedImage image, int tileSize) {
        // Posicionar a tecla acima da entidade
        int keySize = tileSize;
        int keyX = screenX + (tileSize - keySize) / 2;
        int keyY = screenY - keySize - 10; // 10 pixels acima da entidade
        
        // Aplicar transparência para melhor visibilidade
        Composite originalComposite = g2d.getComposite();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f));
        
        g2d.drawImage(image, keyX, keyY, keySize, keySize, null);
        
        g2d.setComposite(originalComposite);
    }
    
    /**
     * Verifica se uma tecla está disponível
     * @param key Tecla a verificar
     * @return true se a tecla está disponível
     */
    public boolean hasKey(String key) {
        return keyImages.containsKey(key) || animatedKeyImages.containsKey(key);
    }
    
    /**
     * Obtém todas as teclas disponíveis
     * @return Set de teclas disponíveis
     */
    public java.util.Set<String> getAvailableKeys() {
        java.util.Set<String> keys = new java.util.HashSet<>();
        keys.addAll(keyImages.keySet());
        keys.addAll(animatedKeyImages.keySet());
        return keys;
    }
}
