package org.example;

import javax.swing.*;
import java.awt.*;
import org.example.ui.*;
import org.example.Types.*;

public class Main {
    private JFrame frame;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    public Database db;
    public User user;

    public Main() {
        db = new Database("jdbc:mysql://localhost:3306/refurby?user=root&password=mysql");
        db.getConditions();
        frame = new JFrame("Simple App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Screens
         WelcomePanel welcomePanel = new WelcomePanel(this);
        LoginPanel loginPanel = new LoginPanel(this);
        // Add Home after login
        // HomePanel homePanel = new HomePanel(this);
        SignUp signupPanel = new SignUp(this);

        mainPanel.add(welcomePanel, "welcome");
        mainPanel.add(loginPanel, "login");
        // mainPanel.add(homePanel, "home");
        mainPanel.add(signupPanel, "signup");

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    public void showScreen(String name) {
        cardLayout.show(mainPanel, name);
    }

    public void addPanel(JPanel panel, String name) {
        mainPanel.add(panel, name);
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}


class WelcomePanel extends JPanel {
    public WelcomePanel(Main mainApp) {
        setLayout(new FlowLayout());

        JLabel label = new JLabel("Welcome! Please login.");
        JButton loginButton = new JButton("Login");
        JButton signupButton = new JButton("SignUp");

        loginButton.addActionListener(_ -> {
            mainApp.showScreen("login");
        });
        signupButton.addActionListener(_ -> {
            mainApp.showScreen("signup");
        });
        add(label);
        add(loginButton);
        add(signupButton);
    }
}
