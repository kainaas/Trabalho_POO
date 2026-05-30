import java.awt.BorderLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;

public class TopPanel extends JPanel {
    
    JToggleButton dayView, weekView, monthView, yearView, darkMode, lightMode;
    JButton createButton, editButton;
    JLabel labelView;
    JTextArea searchBar;
    ButtonGroup chooseView, chooseDarkMode;

    ImageIcon dark, light, edit, create, search;

    public TopPanel() {
        super();
        //setLayout();
        createToggleButtons();
        addToggleButtons();
    }

    private void createToggleButtons() {
        this.dayView = new JToggleButton("Day", true); //starts viewing day
        this.weekView = new JToggleButton("Week", false);
        this.monthView = new JToggleButton("Month", false);
        this.yearView = new JToggleButton("Year", false);
        
        this.chooseView = new ButtonGroup();
        this.chooseView.add(dayView);
        this.chooseView.add(weekView);
        this.chooseView.add(monthView);
        this.chooseView.add(yearView);

        this.dark = new ImageIcon("../imgs/hihihiha.jpg");
        this.light = new ImageIcon("../imgs/hihihiha.jpg");
        
        this.darkMode = new JToggleButton(this.dark, true);
        this.lightMode = new JToggleButton(this.light, false);

        this.chooseDarkMode = new ButtonGroup();
        this.chooseDarkMode.add(this.darkMode);
        this.chooseDarkMode.add(this.lightMode);
    }

    private void addToggleButtons() {
        this.add(this.dayView);
        this.add(this.weekView);
        this.add(this.monthView);
        this.add(this.yearView);
        this.add(this.darkMode);
        this.add(this.lightMode);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        TopPanel painel = new TopPanel();
        frame.setSize(1920, 1080);
        frame.add(painel, BorderLayout.NORTH);
        frame.setVisible(true);
    }
}
