package Main;

import Model.*;
import Controller.*;
import View.*;

import javax.swing.JFrame;
import java.awt.BorderLayout;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        TopPanel painel = new TopPanel();
        frame.setSize(1920, 1080);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(painel, BorderLayout.NORTH);
        frame.setVisible(true);
    }
}