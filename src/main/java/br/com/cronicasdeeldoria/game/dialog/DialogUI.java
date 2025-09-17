package br.com.cronicasdeeldoria.game.dialog;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;

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
    
    // Sistema de paginação local
    private List<String> currentTextPages;
    private int currentPageIndex;
    
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
        this.backgroundColor = new Color(50, 40, 60, 250); // Fundo principal
        this.textColor = Color.WHITE; // Texto branco
        this.selectedOptionColor = Color.WHITE; // Opção selecionada branca
        this.unselectedOptionColor = new Color(200, 200, 200); // Cinza claro
        this.borderColor = new Color(100, 80, 120, 200); // Borda roxa
        this.shadowColor = new Color(0, 0, 0, 100); // Sombra da caixa
        this.highlightColor = new Color(255, 255, 255, 120); // Destaque mais visível

        // Configurar dimensões - Box com largura da tela e posicionado na parte inferior
        this.dialogBoxWidth = gamePanel.getWidth() - 40; // Margem de 20px de cada lado
        this.dialogBoxHeight = 280;
        this.dialogBoxX = 20; // Margem esquerda
        this.dialogBoxY = gamePanel.getHeight() - dialogBoxHeight - 20; // Margem inferior de 20px
        this.portraitSize = 100;
        this.textPadding = 25;
        this.optionSpacing = 30;
        this.borderRadius = 15;
        
        // Inicializar sistema de paginação local
        this.currentTextPages = new ArrayList<>();
        this.currentPageIndex = 0;
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

        // Overlay semi-transparente
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, gamePanel.getWidth(), gamePanel.getHeight());

        // Atualizar dimensões se necessário
        updateDimensions();

        // Desenhar balão de diálogo principal
        drawDialogBubble(g2, currentDialog);

        // Desenhar retrato do personagem
        drawPortrait(g2, currentDialog);

        drawSpeakerName(g2, currentDialog);

        drawDialogText(g2, dialogManager);

        // Desenhar opções com indicadores visuais (apenas na última página)
        if (isOnLastPage()) {
            drawOptions(g2, dialogManager);
        }
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
     * Desenha o texto do diálogo com suporte a paginação local.
     * @param g2 Contexto gráfico
     * @param dialogManager Gerenciador de diálogos
     */
    private void drawDialogText(Graphics2D g2, DialogManager dialogManager) {
        Dialog dialog = dialogManager.getCurrentDialog();
        if (dialog == null) return;
        
        // SÓ processar texto em páginas se ainda não foi processado
        if (currentTextPages.isEmpty()) {
            processTextIntoPages(dialog.getText());
        }
        
        g2.setFont(dialogFont);
        
        int textX = dialogBoxX + portraitSize + 40;
        int textY = dialogBoxY + 70;
        int maxWidth = dialogBoxWidth - portraitSize - 100; // Ajustado para nova largura
        
        // Obter texto da página atual
        String currentPageText = getCurrentPageText();
       
        // Desenhar texto da página atual
        drawTextLines(g2, currentPageText, textX, textY, maxWidth);
        
        // Desenhar indicadores de paginação se necessário
        drawPaginationIndicatorsLocal(g2);
    }
    
    /**
     * Processa o texto em páginas localmente - VERSÃO ULTRA SIMPLIFICADA.
     * @param text Texto completo
     */
    private void processTextIntoPages(String text) {
        currentTextPages.clear();
        currentPageIndex = 0;
        
        // Configurações ajustadas para largura da tela - mais caracteres por página
        int maxCharsPerPage = 200; // Aumentado para aproveitar a largura da tela
        
        // Dividir texto em páginas baseado em caracteres
        for (int i = 0; i < text.length(); i += maxCharsPerPage) {
            int endIndex = Math.min(i + maxCharsPerPage, text.length());
            String pageText = text.substring(i, endIndex);
            
            // Tentar quebrar em palavra completa se possível
            if (endIndex < text.length()) {
                int lastSpace = pageText.lastIndexOf(' ');
                if (lastSpace > maxCharsPerPage * 0.6) { // Se encontrou espaço em posição razoável
                    pageText = pageText.substring(0, lastSpace);
                    i = i + lastSpace - maxCharsPerPage; // Ajustar índice
                }
            }
            
            currentTextPages.add(pageText.trim());
        }
        
        // Se não há páginas, criar uma página vazia
        if (currentTextPages.isEmpty()) {
            currentTextPages.add("");
        }
    }
    
    /**
     * Obtém o texto da página atual.
     * @return Texto da página atual
     */
    private String getCurrentPageText() {
        if (currentTextPages.isEmpty() || currentPageIndex >= currentTextPages.size()) {
            return "";
        }
        return currentTextPages.get(currentPageIndex);
    }
    
    /**
     * Verifica se tem múltiplas páginas.
     * @return true se tem mais de uma página
     */
    private boolean hasMultiplePages() {
        return currentTextPages.size() > 1;
    }
    
    /**
     * Verifica se está na última página.
     * @return true se está na última página
     */
    public boolean isOnLastPage() {
        boolean isLast = currentPageIndex >= currentTextPages.size() - 1;
        return isLast;
    }

    /**
     * Verifica se está na primeira página.
     * @return true se está na primeira página
     */
    public boolean isOnFirstPage() {
        boolean isFirst = currentPageIndex <= 0;
        return isFirst;
    }
    
    /**
     * Avança para a próxima página.
     * @return true se conseguiu avançar
     */
    public boolean nextPage() {
        if (currentPageIndex < currentTextPages.size() - 1) {
            currentPageIndex++;
            return true;
        }
        return false;
    }

    /**
     * Volta para a página anterior.
     * @return true se conseguiu voltar
     */
    public boolean previousPage() {
        if (currentPageIndex > 0) {
            currentPageIndex--;
            return true;
        }
        return false;
    }
    
    /**
     * Desenha as linhas de texto - VERSÃO SIMPLIFICADA.
     * @param g2 Contexto gráfico
     * @param text Texto a desenhar
     * @param x Posição X
     * @param y Posição Y
     * @param maxWidth Largura máxima
     */
    private void drawTextLines(Graphics2D g2, String text, int x, int y, int maxWidth) {
        if (text == null || text.isEmpty()) return;
        
        FontMetrics fm = g2.getFontMetrics();
        int lineHeight = fm.getHeight() + 3;
        int lineY = y;
        
        // Dividir texto em linhas baseado na largura máxima
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();
        
        for (String word : words) {
            String testLine = currentLine.length() > 0 ? 
                currentLine + " " + word : word;
            
            int textWidth = fm.stringWidth(testLine);
            
            if (textWidth > maxWidth && currentLine.length() > 0) {
                // Desenhar linha atual
                g2.setColor(new Color(0, 0, 0, 100)); // Sombra
                g2.drawString(currentLine.toString(), x + 1, lineY + 1);
                g2.setColor(textColor); // Texto principal
                g2.drawString(currentLine.toString(), x, lineY);
                
                // Começar nova linha
                currentLine = new StringBuilder(word);
                lineY += lineHeight;
            } else {
                currentLine = new StringBuilder(testLine);
            }
        }
        
        // Desenhar última linha
        if (currentLine.length() > 0) {
            g2.setColor(new Color(0, 0, 0, 100)); // Sombra
            g2.drawString(currentLine.toString(), x + 1, lineY + 1);
            g2.setColor(textColor); // Texto principal
            g2.drawString(currentLine.toString(), x, lineY);
        }
    }

    /**
     * Desenha os indicadores de paginação local.
     * @param g2 Contexto gráfico
     */
    private void drawPaginationIndicatorsLocal(Graphics2D g2) {
        
        g2.setFont(FontManager.getFont(12f));
        
        int currentPage = currentPageIndex;
        int totalPages = currentTextPages.size();
        
        // Posição dos indicadores (canto inferior direito da área de texto)
        int indicatorX = dialogBoxX + dialogBoxWidth - 100;
        int indicatorY = dialogBoxY + dialogBoxHeight - 120;
        
        // Fundo dos indicadores
        g2.setColor(new Color(0, 0, 0, 100));
        g2.fillRoundRect(indicatorX - 10, indicatorY - 15, 90, 25, 5, 5);
        
        // Borda dos indicadores
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(1));
        g2.drawRoundRect(indicatorX - 10, indicatorY - 15, 90, 25, 5, 5);
        
        // Texto da página atual
        g2.setColor(textColor);
        String pageText = String.format("Página %d/%d", currentPage + 1, totalPages);
        g2.drawString(pageText, indicatorX, indicatorY);
        
        // Indicador de "pressione A/D para continuar" se não for a última página
        if (currentPage < totalPages - 1) {
            g2.setFont(FontManager.getFont(10f));
            g2.setColor(new Color(255, 255, 0, 200)); // Amarelo para destacar
            String continueText = "Pressione A/D para passar páginas";
            int continueX = dialogBoxX + textPadding;
            int continueY = indicatorY + 20;
            g2.drawString(continueText, continueX, continueY);
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
        this.dialogBoxWidth = gamePanel.getWidth() - 40; // Margem de 20px de cada lado
        this.dialogBoxX = 20; // Margem esquerda
        this.dialogBoxY = gamePanel.getHeight() - dialogBoxHeight - 20; // Margem inferior de 20px
    }
    
    /**
     * Reseta a paginação para um novo diálogo.
     */
    public void resetPagination() {
        this.currentTextPages.clear();
        this.currentPageIndex = 0;
    }
}
