package org.example.ui;

import javax.swing.*;
import java.awt.*;
import org.example.Main;
import org.example.Types.*;

public class LoginPanel extends JPanel {
    public LoginPanel(Main mainApp) {
        setLayout(new GridLayout(3, 2, 5, 5));

        JLabel userLabel = new JLabel("Email:");
        JTextField userField = new JTextField();

        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField();

        JButton loginButton = new JButton("Login");

        loginButton.addActionListener(_ -> {
            String email = userField.getText();
            String password = new String(passField.getPassword());
            String uid = mainApp.db.validate(email, password);
            if (uid == null) {
                JOptionPane.showMessageDialog(this,
                        "Invalid username or password!",
                        "Login Error",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                User user = mainApp.db.getUserProfile(uid);

                // Save user details to mainApp
                mainApp.setUser(user);
                HomePanel homePanel = new HomePanel(mainApp);
                mainApp.addPanel(homePanel, "home");
                mainApp.showScreen("home");
            }
        });

        add(userLabel);
        add(userField);
        add(passLabel);
        add(passField);
        add(new JLabel()); // empty cell
        add(loginButton);
    }
}
