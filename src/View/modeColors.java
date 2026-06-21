package View;

import java.awt.Color;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;

/**
 * Class that contais the colors that each color theme uses.
 * Tem dois temas prontos (claro e escuro) e o metodo of() devolve
 * o tema certo dependendo do darkMode.
 */
public class modeColors {
    public final Color background;
    public final Color panel;
    public final Color text;
    public final Color subtleText;
    public final Color gridLine;
    public final Color today;
    public final Color hasEvents;
    public final Color selected;
    public final Color accent;
    public final Color button;
    public final Color buttonText;
    public final Color buttonSelected;

    private modeColors(Color background, Color panel, Color text, Color subtleText,
            Color gridLine, Color today, Color hasEvents, Color selected, Color accent,
            Color button, Color buttonText, Color buttonSelected) {
        this.background = background;
        this.panel = panel;
        this.text = text;
        this.subtleText = subtleText;
        this.gridLine = gridLine;
        this.today = today;
        this.hasEvents = hasEvents;
        this.selected = selected;
        this.accent = accent;
        this.button = button;
        this.buttonText = buttonText;
        this.buttonSelected = buttonSelected;
    }

    public static final modeColors LIGHT = new modeColors(
        new Color(245, 245, 245),
        new Color(255, 255, 255),
        new Color(30, 30, 30),
        new Color(120, 120, 120),
        new Color(210, 210, 210),
        new Color(255, 236, 179),
        new Color(208, 233, 255),
        new Color(120, 170, 220),
        new Color(70, 130, 200),
        new Color(230, 230, 230),
        new Color(30, 30, 30),
        new Color(70, 130, 200));

    public static final modeColors DARK = new modeColors(
        new Color(40, 42, 48),
        new Color(54, 57, 64),
        new Color(230, 230, 230),
        new Color(160, 160, 160),
        new Color(75, 78, 85),
        new Color(120, 100, 45),
        new Color(50, 90, 130),
        new Color(80, 120, 165),
        new Color(95, 155, 220),
        new Color(60, 63, 70),
        new Color(230, 230, 230),
        new Color(95, 155, 220));

    public static modeColors of(boolean darkMode) {
        return darkMode ? DARK : LIGHT;
    }

    /**
     * Aplica o tema atual a um botao (ou toggle), garantindo que ele seja
     * totalmente pintado com a cor do tema mesmo no Look and Feel padrao
     * (Metal), o que evita as "faixas brancas" no modo escuro. Quando
     * {@code selected} e verdadeiro, usa a cor de destaque.
     */
    public void styleButton(AbstractButton b, boolean selected) {
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorderPainted(true);
        b.setFocusPainted(false);
        b.setBackground(selected ? buttonSelected : button);
        b.setForeground(selected ? Color.WHITE : buttonText);
        b.setBorder(BorderFactory.createLineBorder(gridLine));
    }
}
