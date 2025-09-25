package org.example.ui;

import javax.swing.*;
import java.awt.*;
import org.example.Main;
import org.example.Types.*;

public class LoginPanel extends JPanel {
    public LoginPanel(Main mainApp) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top panel: Back button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backBtn = new JButton("⬅ Back");
        backBtn.addActionListener(_ -> mainApp.showScreen("welcome"));
        topPanel.add(backBtn);
        add(topPanel, BorderLayout.NORTH);

        // Center panel: Login form
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));

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
                mainApp.setUser(user);

                String[] options = {"Buyer", "Seller"};
                int choice = JOptionPane.showOptionDialog(
                    null,
                    "Login as:",
                    "Select Role",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
                );

                JPanel homePanel;
                if(choice == 0){
                    homePanel = new HomePanel(mainApp);
                } else {
                    homePanel = new SellerPanel(mainApp);
                }

                mainApp.addPanel(homePanel, "home");
                mainApp.showScreen("home");
            }
        });

        formPanel.add(userLabel);
        formPanel.add(userField);
        formPanel.add(passLabel);
        formPanel.add(passField);
        formPanel.add(new JLabel());
        formPanel.add(loginButton);

        add(formPanel, BorderLayout.CENTER);
    }
}
