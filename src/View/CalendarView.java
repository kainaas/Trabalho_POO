package View;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFrame;

import Controller.Controller;
import Model.CalendarModel;

//TODO: passar o PropertyChanceListener para os paineis
public class CalendarView extends JFrame implements PropertyChangeListener {
    TopPanel topPanel;

    @Override 
    public void propertyChange(PropertyChangeEvent evt) {

        switch(evt.getPropertyName()) {

            case "currentMode":
                //updateViewMode();
                break;

            case "currentViewDate":
                //updateDate();
                break;

            case "darkMode":
                //updateTheme();
                break;

            case "eventAdded":
            case "eventRemoved":
                //updateEvents();
                break;
        }
    }
}
