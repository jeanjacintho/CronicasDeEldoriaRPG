package br.com.cronicasdeeldoria.game.font;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;

/**
 * Gerenciador de fontes customizadas para o jogo.
 * Permite carregar, registrar e aplicar uma fonte customizada globalmente em todos os componentes Swing.
 * Por padrão, utiliza a fonte dogicapixel.ttf localizada em /resources/font/.
 */
public class FontManager {
    private static Font customFont;

    static {
        try {
            InputStream is = FontManager.class.getResourceAsStream("/font/dogicapixel.ttf");
            if (is != null) {
                customFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(16f);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(customFont);
            } else {
                customFont = new Font("SansSerif", Font.PLAIN, 16);
            }
        } catch (Exception e) {
            e.printStackTrace();
            customFont = new Font("SansSerif", Font.PLAIN, 16);
        }
    }

    /**
     * Retorna a fonte customizada com o tamanho especificado.
     * @param size Tamanho da fonte desejado.
     * @return Fonte customizada no tamanho informado.
     */
    public static Font getFont(float size) {
        return customFont.deriveFont(size);
    }

    /**
     * Retorna a fonte customizada padrão (tamanho 16).
     * @return Fonte customizada padrão.
     */
    public static Font getDefaultFont() {
        return customFont;
    }

    /**
     * Aplica a fonte informada globalmente em todos os componentes Swing,
     * sobrescrevendo a fonte padrão do UIManager.
     * Recomenda-se chamar este método no início da aplicação, antes de criar qualquer janela.
     * @param font Fonte a ser aplicada globalmente.
     */
    public static void setUIFont(Font font) {
        javax.swing.UIDefaults defaults = javax.swing.UIManager.getDefaults();
        java.util.Enumeration<Object> keys = defaults.keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = defaults.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource) {
                javax.swing.UIManager.put(key, new javax.swing.plaf.FontUIResource(font));
            }
        }
    }
}
