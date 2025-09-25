package org.example.ui;

import javax.swing.*;
import java.awt.*;
import org.example.Main;

public class SignUp extends JPanel {
    public SignUp(Main mainApp) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top panel: Back button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backBtn = new JButton("⬅ Back");
        backBtn.addActionListener(_ -> mainApp.showScreen("welcome"));
        topPanel.add(backBtn);
        add(topPanel, BorderLayout.NORTH);

        // Center panel
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));

        JLabel userLabel = new JLabel("Email:");
        JTextField userField = new JTextField();

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField();

        JLabel phoneLabel = new JLabel("Phone Number:");
        JTextField phoneField = new JTextField();

        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField();

        JLabel addressLabel = new JLabel("Address:");
        JTextField addressField = new JTextField();

        JButton signUp = new JButton("Sign Up");

        signUp.addActionListener(_ -> {
            String email = userField.getText();
            String name = nameField.getText();
            String phoneNo = phoneField.getText();
            String password = new String(passField.getPassword());
            String address = addressField.getText();

            mainApp.db.createUser(email, name, phoneNo, password, address);

            mainApp.showScreen("login");
        });

        formPanel.add(userLabel);
        formPanel.add(userField);
        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(phoneLabel);
        formPanel.add(phoneField);
        formPanel.add(passLabel);
        formPanel.add(passField);
        formPanel.add(addressLabel);
        formPanel.add(addressField);
        formPanel.add(new JLabel());
        formPanel.add(signUp);

        add(formPanel, BorderLayout.CENTER);
    }
}
