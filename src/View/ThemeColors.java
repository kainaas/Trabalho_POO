package View;

import java.awt.Color;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;

/**
 * Colour palette used by the interface. Two ready-made themes are provided
 * (light and dark); {@link #of(boolean)} returns the right one for the current
 * dark-mode flag.
 */
public class ThemeColors {
    /** Window background. */
    public final Color background;
    /** Background of panels and input fields. */
    public final Color panel;
    /** Primary text colour. */
    public final Color text;
    /** Secondary, lower-contrast text colour. */
    public final Color subtleText;
    /** Colour of grid and border lines. */
    public final Color gridLine;
    /** Highlight colour for today's cell. */
    public final Color today;
    /** Highlight colour for days that have events. */
    public final Color hasEvents;
    /** Highlight colour for the selected day. */
    public final Color selected;
    /** Accent colour. */
    public final Color accent;
    /** Default button background. */
    public final Color button;
    /** Default button text colour. */
    public final Color buttonText;
    /** Background of a selected/active button. */
    public final Color buttonSelected;

    private ThemeColors(Color background, Color panel, Color text, Color subtleText,
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

    /** Light theme. */
    public static final ThemeColors LIGHT = new ThemeColors(
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

    /** Dark theme. */
    public static final ThemeColors DARK = new ThemeColors(
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

    /**
     * Returns the palette for the requested theme.
     *
     * @param darkMode {@code true} for the dark theme
     * @return the matching palette
     */
    public static ThemeColors of(boolean darkMode) {
        return darkMode ? DARK : LIGHT;
    }

    /**
     * Applies the current theme to a button (or toggle), forcing it to be fully
     * painted with the theme colour even under the default (Metal) Look and Feel.
     * This is what prevents the "white bands" that used to appear on buttons in
     * dark mode. When {@code selected} is {@code true}, the accent background is
     * used instead.
     *
     * @param b        the button to style
     * @param selected whether the button is in its selected/active state
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
