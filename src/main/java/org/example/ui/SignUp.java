package org.example.ui;

import javax.swing.*;
import java.awt.*;
import org.example.Main;

public class SignUp extends JPanel {
    public SignUp(Main mainApp) {
        setLayout(new GridLayout(6, 2, 5, 5));

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

        signUp.addActionListener(e -> {
            String email = userField.getText();
            String name = nameField.getText();
            String phoneNo = phoneField.getText();
            String password = new String(passField.getPassword());
            String address = addressField.getText();

            mainApp.db.createUser(email, name, phoneNo, password, address);

            mainApp.showScreen("login");
        });

        add(userLabel);
        add(userField);
        add(nameLabel);
        add(nameField);
        add(phoneLabel);
        add(phoneField);
        add(passLabel);
        add(passField);
        add(addressLabel);
        add(addressField);
        add(new JLabel()); // empty cell
        add(signUp);
    }
}
