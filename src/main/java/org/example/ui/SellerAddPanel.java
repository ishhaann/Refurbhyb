package org.example.ui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.example.Main;
import org.example.Types;

public class SellerAddPanel extends JPanel {

    public SellerAddPanel(Main mainApp) {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel title = new JLabel("📦 Add Item", SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        // Center panel for input fields
        JPanel inputPanel = new JPanel(new GridLayout(9, 2, 10, 10));

        // Image
        JLabel imageLabel = new JLabel("Image:");
        JButton browseBtn = new JButton("Choose File");
        JLabel selectedFile = new JLabel("No file chosen");
        browseBtn.addActionListener(_ -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                selectedFile.setText(file.getAbsolutePath());
            }
        });

        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.add(browseBtn, BorderLayout.WEST);
        imagePanel.add(selectedFile, BorderLayout.CENTER);

        // Name
        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField();

        // Model
        JLabel modelLabel = new JLabel("Model:");
        JTextField modelField = new JTextField();

        // Category
        JLabel categoryLabel = new JLabel("Category:");
        Types.KeyValue[] categories = mainApp.db.getCategories();
        JComboBox<Types.KeyValue> categoryBox = new JComboBox<>(categories);

        // Description
        JLabel descLabel = new JLabel("Description:");
        JTextField descField = new JTextField();

        // Price
        JLabel priceLabel = new JLabel("Price:");
        JTextField priceField = new JTextField();

        // Warranty
        JLabel warrantyLabel = new JLabel("Warranty (YYYY-MM-DD):");
        JTextField warrantyField = new JTextField();

        // Condition
        JLabel conditionLabel = new JLabel("Condition:");
        String[] conditions = mainApp.db.getConditions();
        JComboBox<String> conditionBox = new JComboBox<>(conditions);

        // Quantity
        JLabel quantityLabel = new JLabel("Quantity:");
        JTextField quantityField = new JTextField();

        // Add components to grid
        inputPanel.add(imageLabel);
        inputPanel.add(imagePanel);

        inputPanel.add(nameLabel);
        inputPanel.add(nameField);

        inputPanel.add(modelLabel);
        inputPanel.add(modelField);

        inputPanel.add(categoryLabel);
        inputPanel.add(categoryBox);

        inputPanel.add(descLabel);
        inputPanel.add(descField);

        inputPanel.add(priceLabel);
        inputPanel.add(priceField);

        inputPanel.add(warrantyLabel);
        inputPanel.add(warrantyField);

        inputPanel.add(conditionLabel);
        inputPanel.add(conditionBox);

        inputPanel.add(quantityLabel);
        inputPanel.add(quantityField);

        add(inputPanel, BorderLayout.CENTER);

        // Bottom panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addItemBtn = new JButton("➕ Add Item");
        addItemBtn.addActionListener(_ -> {
            // Get values
            String imagePath = selectedFile.getText();
            String name = nameField.getText().trim();
            String model = modelField.getText().trim();
            int category = categoryBox.getSelectedIndex();
            String desc = descField.getText().trim();
            String priceText = priceField.getText().trim();
            String warranty = warrantyField.getText().trim();
            String condition = (String) conditionBox.getSelectedItem();
            String quantityText = quantityField.getText().trim();

            if (name.isEmpty() || priceText.isEmpty() || quantityText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name, Price and Quantity are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int price = Integer.parseInt(priceText);
                int quantity = Integer.parseInt(quantityText);
                System.out.println(Paths.get(imagePath));
                byte[] fileBytes = Files.readAllBytes(Paths.get(imagePath));

                String itemId = mainApp.db.addItem(name, model, category, desc, price, warranty, condition, mainApp.user.uid(), quantity);
                mainApp.db.uploadFile(itemId, fileBytes);

                JOptionPane.showMessageDialog(this, "Item added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

                // Reset fields
                selectedFile.setText("No file chosen");
                nameField.setText("");
                modelField.setText("");
                descField.setText("");
                priceField.setText("");
                warrantyField.setText("");
                quantityField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Price and Quantity must be a number", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IOException ex){
                JOptionPane.showMessageDialog(this, "File: "+ ex, "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        // Back button
        JButton backBtn = new JButton("⬅ Back");
        backBtn.addActionListener(_ -> mainApp.showScreen("home"));
        bottomPanel.add(backBtn, BorderLayout.SOUTH);
        bottomPanel.add(addItemBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }
}
