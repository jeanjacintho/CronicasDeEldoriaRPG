package br.com.cronicasdeeldoria.game.dialog;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.List;

import javax.imageio.ImageIO;

import br.com.cronicasdeeldoria.game.GamePanel;
import br.com.cronicasdeeldoria.game.font.FontManager;

/**
 * Interface de usuário para renderização dos diálogos com balão visual.
 */
public class DialogUI {
    private GamePanel gamePanel;
    private Font dialogFont;
    private Font speakerFont;
    private Font optionFont;
    
    // Cores do tema
    private Color backgroundColor;
    private Color textColor;
    private Color selectedOptionColor;
    private Color unselectedOptionColor;
    private Color borderColor;
    private Color shadowColor;
    private Color highlightColor;

    // Dimensões da caixa de diálogo
    private int dialogBoxX;
    private int dialogBoxY;
    private int dialogBoxWidth;
    private int dialogBoxHeight;
    private int portraitSize;
    private int textPadding;
    private int optionSpacing;
    private int borderRadius;
    
    /**
     * Cria uma nova interface de diálogo.
     * @param gamePanel Painel do jogo
     */
    public DialogUI(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        this.dialogFont = FontManager.getFont(16f);
        this.speakerFont = FontManager.getFont(20f);
        this.optionFont = FontManager.getFont(14f);
        
        // Cores do tema
        this.backgroundColor = new Color(10, 10, 20, 250); // Preto mais escuro e opaco
        this.textColor = new Color(220, 220, 255); // Branco azulado
        this.selectedOptionColor = new Color(255, 215, 0); // Dourado
        this.unselectedOptionColor = new Color(180, 180, 200); // Cinza claro
        this.borderColor = new Color(100, 80, 120); // Roxo escuro
        this.shadowColor = new Color(0, 0, 0, 200); // Sombra mais escura
        this.highlightColor = new Color(255, 255, 255, 120); // Destaque mais visível

        // Configurar dimensões
        this.dialogBoxWidth = Math.min(600, gamePanel.getWidth() - 80);
        this.dialogBoxHeight = 280;
        this.dialogBoxX = (gamePanel.getWidth() - dialogBoxWidth) / 2;
        this.dialogBoxY = gamePanel.getHeight() - dialogBoxHeight - 60;
        this.portraitSize = 100;
        this.textPadding = 25;
        this.optionSpacing = 30;
        this.borderRadius = 15;
    }

    /**
     * Desenha a interface de diálogo.
     * @param g2 Contexto gráfico
     * @param dialogManager Gerenciador de diálogos
     */
    public void draw(Graphics2D g2, DialogManager dialogManager) {
        if (!dialogManager.isDialogActive()) return;

        Dialog currentDialog = dialogManager.getCurrentDialog();
        if (currentDialog == null) return;

        // Atualizar dimensões se necessário
        updateDimensions();

        // Desenhar balão de diálogo principal
        drawDialogBubble(g2, currentDialog);

        // Desenhar retrato do personagem
        drawPortrait(g2, currentDialog);

        drawSpeakerName(g2, currentDialog);

        drawDialogText(g2, currentDialog);

        // Desenhar opções com indicadores visuais
        drawOptions(g2, dialogManager);
    }

    /**
     * Desenha o balão de diálogo principal.
     * @param g2 Contexto gráfico
     * @param dialog Diálogo atual
     */
    private void drawDialogBubble(Graphics2D g2, Dialog dialog) {
        // Sombra do balão
        g2.setColor(shadowColor);
        g2.fillRoundRect(dialogBoxX + 4, dialogBoxY + 4, dialogBoxWidth, dialogBoxHeight, borderRadius, borderRadius);
        
        // Fundo principal do balão
        g2.setColor(backgroundColor);
        g2.fillRoundRect(dialogBoxX, dialogBoxY, dialogBoxWidth, dialogBoxHeight, borderRadius, borderRadius);
        
        // Borda externa
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(dialogBoxX, dialogBoxY, dialogBoxWidth, dialogBoxHeight, borderRadius, borderRadius);
        
        // Borda interna (destaque)
        g2.setColor(highlightColor);
        g2.setStroke(new BasicStroke(1));
        g2.drawRoundRect(dialogBoxX + 2, dialogBoxY + 2, dialogBoxWidth - 4, dialogBoxHeight - 4, borderRadius - 2, borderRadius - 2);
        
        // Linha separadora entre texto e opções
        int separatorY = dialogBoxY + dialogBoxHeight - 120;
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(dialogBoxX + textPadding, separatorY, dialogBoxX + dialogBoxWidth - textPadding, separatorY);
    }

    /**
     * Desenha o retrato do personagem.
     * @param g2 Contexto gráfico
     * @param dialog Diálogo atual
     */
    private void drawPortrait(Graphics2D g2, Dialog dialog) {
        if (dialog.getPortraitSprite() == null) return;

        try {
            String spritePath = "/sprites/npc/" + dialog.getPortraitSprite() + "/" + 
                               dialog.getPortraitSprite() + "_front.png";
            InputStream is = getClass().getResourceAsStream(spritePath);
            
            if (is != null) {
                BufferedImage portrait = ImageIO.read(is);
                
                int portraitX = dialogBoxX + 20;
                int portraitY = dialogBoxY + 20;
                
                // Sombra do retrato
                g2.setColor(new Color(0, 0, 0, 100));
                g2.fillRoundRect(portraitX + 2, portraitY + 2, portraitSize + 4, portraitSize + 4, 8, 8);
                
                // Fundo do retrato (moldura)
                g2.setColor(borderColor);
                g2.fillRoundRect(portraitX - 2, portraitY - 2, portraitSize + 4, portraitSize + 4, 8, 8);
                
                // Destaque da moldura
                g2.setColor(highlightColor);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(portraitX - 2, portraitY - 2, portraitSize + 4, portraitSize + 4, 8, 8);
                
                // Desenhar retrato
                g2.drawImage(portrait, portraitX, portraitY, portraitSize, portraitSize, null);
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar retrato: " + e.getMessage());
        }
    }

    /**
     * Desenha o nome do personagem.
     * @param g2 Contexto gráfico
     * @param dialog Diálogo atual
     */
    private void drawSpeakerName(Graphics2D g2, Dialog dialog) {
        g2.setFont(speakerFont);
        
        int nameX = dialogBoxX + portraitSize + 40;
        int nameY = dialogBoxY + 35;
        
        // Sombra do nome
        g2.setColor(new Color(0, 0, 0, 150));
        g2.drawString(dialog.getSpeakerName(), nameX + 2, nameY + 2);
        
        // Nome principal
        g2.setColor(selectedOptionColor);
        g2.drawString(dialog.getSpeakerName(), nameX, nameY);
        
        // Linha decorativa abaixo do nome
        FontMetrics fm = g2.getFontMetrics();
        int nameWidth = fm.stringWidth(dialog.getSpeakerName());
        g2.setColor(borderColor);
        g2.setStroke(new java.awt.BasicStroke(2));
        g2.drawLine(nameX, nameY + 5, nameX + nameWidth, nameY + 5);
    }

    /**
     * Desenha o texto do diálogo.
     * @param g2 Contexto gráfico
     * @param dialog Diálogo atual
     */
    private void drawDialogText(Graphics2D g2, Dialog dialog) {
        g2.setFont(dialogFont);
        
        int textX = dialogBoxX + portraitSize + 40;
        int textY = dialogBoxY + 70;
        int maxWidth = dialogBoxWidth - portraitSize - 80;
        
        // Quebrar texto em múltiplas linhas se necessário
        String[] words = dialog.getText().split(" ");
        StringBuilder currentLine = new StringBuilder();
        int lineY = textY;
        
        for (String word : words) {
            String testLine = currentLine.length() > 0 ? 
                currentLine + " " + word : word;
            
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(testLine);
            
            if (textWidth > maxWidth && currentLine.length() > 0) {
                // Desenhar linha atual com sombra
                g2.setColor(new Color(0, 0, 0, 100));
                g2.drawString(currentLine.toString(), textX + 1, lineY + 1);
                g2.setColor(textColor);
                g2.drawString(currentLine.toString(), textX, lineY);
                
                currentLine = new StringBuilder(word);
                lineY += fm.getHeight() + 3;
            } else {
                currentLine = new StringBuilder(testLine);
            }
        }
        
        // Desenhar última linha
        if (currentLine.length() > 0) {
            g2.setColor(new Color(0, 0, 0, 100));
            g2.drawString(currentLine.toString(), textX + 1, lineY + 1);
            g2.setColor(textColor);
            g2.drawString(currentLine.toString(), textX, lineY);
        }
    }

    /**
     * Desenha as opções de diálogo com indicadores visuais.
     * @param g2 Contexto gráfico
     * @param dialogManager Gerenciador de diálogos
     */
    private void drawOptions(Graphics2D g2, DialogManager dialogManager) {
        List<DialogOption> options = dialogManager.getCurrentAvailableOptions();
        if (options.isEmpty()) return;

        g2.setFont(optionFont);
        
        int optionsStartY = dialogBoxY + dialogBoxHeight - 100;
        int selectedIndex = dialogManager.getSelectedOptionIndex();
        int optionHeight = 25;
        
        for (int i = 0; i < options.size(); i++) {
            DialogOption option = options.get(i);
            
            int optionY = optionsStartY + (i * optionSpacing);
            int optionX = dialogBoxX + textPadding;
            int optionWidth = dialogBoxWidth - (textPadding * 2);
            
            // Desenhar fundo da opção se selecionada
            if (i == selectedIndex) {
                // Fundo da opção selecionada
                g2.setColor(new Color(255, 215, 0, 50));
                g2.fillRoundRect(optionX - 5, optionY - 15, optionWidth + 10, optionHeight, 8, 8);
                
                // Borda da opção selecionada
                g2.setColor(selectedOptionColor);
                g2.setStroke(new java.awt.BasicStroke(2));
                g2.drawRoundRect(optionX - 5, optionY - 15, optionWidth + 10, optionHeight, 8, 8);
            }
            
            // Desenhar indicador de seleção (seta)
            if (i == selectedIndex) {
                g2.setColor(selectedOptionColor);
                g2.setFont(FontManager.getFont(18f));
                g2.drawString(">", optionX - 20, optionY);
                g2.setFont(optionFont);
            } else {
                g2.setColor(new Color(150, 150, 150));
                g2.drawString("•", optionX - 15, optionY);
            }
            
            // Cor do texto baseada na disponibilidade
            if (i == selectedIndex) {
                g2.setColor(selectedOptionColor);
            } else if (option.isAvailable()) {
                g2.setColor(unselectedOptionColor);
            } else {
                g2.setColor(new Color(100, 100, 100));
            }
            
            // Desenhar texto da opção
            g2.drawString(option.getText(), optionX, optionY);
            
            // Desenhar número da opção
            g2.setColor(new Color(200, 200, 200, 150));
            g2.setFont(FontManager.getFont(12f));
            g2.drawString(String.valueOf(i + 1), optionX + optionWidth - 20, optionY);
            g2.setFont(optionFont);
        }
    }

    /**
     * Atualiza as dimensões da interface baseada no tamanho do painel.
     */
    public void updateDimensions() {
        this.dialogBoxWidth = Math.min(600, gamePanel.getWidth() - 80);
        this.dialogBoxX = (gamePanel.getWidth() - dialogBoxWidth) / 2;
        this.dialogBoxY = gamePanel.getHeight() - dialogBoxHeight - 60;
    }
}
