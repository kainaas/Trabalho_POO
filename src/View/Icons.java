package View;

import java.awt.Image;
import javax.swing.ImageIcon;

/**
 * Names of the PNG files that hold each icon. The same file name exists under
 * both the {@code lightMode} and {@code darkMode} folders.
 */
enum IconFiles {
    /** Pencil icon for the "edit" action. */
    EDIT_ICON("editIcon.png"),
    /** Plus icon for the "create" action. */
    CREATE_ICON("createIcon.png"),
    /** Moon icon for switching to the dark theme. */
    SWITCH_DARK("switchDark.png"),
    /** Sun icon for switching to the light theme. */
    SWITCH_LIGHT("switchLight.png");

    private final String fileString;

    private IconFiles(String fileString) {
        this.fileString = fileString;
    }

    /** @return the file name of the icon */
    public String getFileString() {
        return fileString;
    }
}

/**
 * Helper that loads and scales the application icons, picking the colour variant
 * that matches the active theme.
 */
public class Icons {
    private static final String DARK_MODE_PATH = "./iconImages/darkMode/";
    private static final String LIGHT_MODE_PATH = "./iconImages/lightMode/";

    private static ImageIcon getIconScaled(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(path);
        Image scaledIcon = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledIcon);
    }

    /**
     * Loads the dark-mode (light-coloured) variant of an icon.
     *
     * @param file   the icon
     * @param width  target width in pixels
     * @param height target height in pixels
     * @return the scaled icon
     */
    public static ImageIcon darkIcon(IconFiles file, int width, int height) {
        return getIconScaled(DARK_MODE_PATH.concat(file.getFileString()), width, height);
    }

    /**
     * Loads the light-mode (dark-coloured) variant of an icon.
     *
     * @param file   the icon
     * @param width  target width in pixels
     * @param height target height in pixels
     * @return the scaled icon
     */
    public static ImageIcon lightIcon(IconFiles file, int width, int height) {
        return getIconScaled(LIGHT_MODE_PATH.concat(file.getFileString()), width, height);
    }

    /**
     * Returns the theme-appropriate variant of an icon: the dark-mode
     * (light-coloured) icon when {@code dark} is {@code true}, otherwise the
     * light-mode (dark-coloured) one.
     *
     * @param file   the icon
     * @param dark   whether the dark theme is active
     * @param width  target width in pixels
     * @param height target height in pixels
     * @return the scaled, theme-appropriate icon
     */
    public static ImageIcon themed(IconFiles file, boolean dark, int width, int height) {
        return dark ? darkIcon(file, width, height) : lightIcon(file, width, height);
    }
}
