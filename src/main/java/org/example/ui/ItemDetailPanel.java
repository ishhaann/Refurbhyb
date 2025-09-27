package org.example.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.example.Main;
import org.example.Types;

public class ItemDetailPanel extends JPanel {

    public ItemDetailPanel(Main mainApp, Types.Item item) {

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Item name
        JLabel nameLabel = new JLabel(item.name(), SwingConstants.CENTER);
        add(nameLabel, BorderLayout.NORTH);

        // Center content
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        // Item image
        String[] fileIds = mainApp.db.getFileIds(item.id());
        byte[] imageData = mainApp.db.getFile(fileIds[0]);
        JLabel imageLabel;
        try {
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageData));
            Image scaled = bufferedImage.getScaledInstance(-1, -1, Image.SCALE_SMOOTH);
            imageLabel = new JLabel(new ImageIcon(scaled));
            imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        } catch (IOException ex) {
            imageLabel = new JLabel(new ImageIcon(
                    new java.awt.image.BufferedImage(200, 200, java.awt.image.BufferedImage.TYPE_INT_RGB)));
            JOptionPane.showMessageDialog(this, "Image: " + ex, "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Price
        JLabel priceLabel = new JLabel("Price: ₹" + item.price());
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Description
        JLabel descLabel = new JLabel("<html><b>Description:</b> " + item.description() + "</html>");
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        descLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Seller details
        JLabel sellerLabel = new JLabel("<html><b>Seller:</b> " + item.seller() + "</html>");
        sellerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sellerLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        centerPanel.add(imageLabel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(priceLabel);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(descLabel);
        centerPanel.add(sellerLabel);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom controls
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));

        JButton backBtn = new JButton("⬅ Back");
        backBtn.addActionListener(e -> mainApp.showScreen("home"));

        JButton buyBtn = new JButton("🛒 Buy Now");
        buyBtn.addActionListener(_ -> {
            BuyPanel buyPanel = new BuyPanel(mainApp, item);
            mainApp.addPanel(buyPanel, "buyPanel");
            mainApp.showScreen("buyPanel");
        });

        bottomPanel.add(backBtn);
        bottomPanel.add(buyBtn);

        add(bottomPanel, BorderLayout.SOUTH);
    }
}
