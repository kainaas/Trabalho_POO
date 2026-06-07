package View;
import javax.swing.ImageIcon;
import java.awt.Image;

/**
 * Sets the names that the files with the icons must have
 */
enum IconFiles {
    EDIT_ICON("editIcon.png"), 
    CREATE_ICON("createIcon.png"), 
    SWITCH_DARK("switchDark.png"), 
    SWITCH_LIGHT("switchLight.png");

    private String fileString;

    private IconFiles(String fileString) {
        this.fileString = fileString;
    }

    public String getFileString() {
        return fileString;
    }
}


/**
 * Facilitates the creation of icons of a desired size, with the desired theme
 */
public class Icons {
    static private final String darkModePath = "./iconImages/darkMode/";
    static private final String lightModePath = "./iconImages/lightMode/";

    static private ImageIcon getIconScaled(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(path);
        Image scaledIcon = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledIcon);
    }

    static public ImageIcon darkIcon(IconFiles file, int width, int height) {
        return getIconScaled(darkModePath.concat(file.getFileString()), width, height);
    }

    static public ImageIcon lightIcon(IconFiles file, int width, int height) {
        return getIconScaled(lightModePath.concat(file.getFileString()), width, height);
    }
}