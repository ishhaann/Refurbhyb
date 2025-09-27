package org.example.ui;

import javax.swing.*;
import java.awt.*;
import org.example.Main;
import org.example.Types;

public class BuyPanel extends JPanel {
    private JTextField txnField;

    public BuyPanel(Main mainApp, Types.Item item) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        Types.User seller = mainApp.db.getUserProfile(item.seller());

        // Item name
        JLabel nameLabel = new JLabel(item.name(), SwingConstants.CENTER);
        add(nameLabel, BorderLayout.NORTH);

        // Center content
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel priceLabel = new JLabel("Price: ₹" + item.price());
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel emailLabel = new JLabel("Seller Email: " + seller.email());
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel phoneLabel = new JLabel("Seller Phone: " + seller.phoneNo());
        phoneLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel txnLabel = new JLabel("Enter Transaction ID:");
        txnLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        txnField = new JTextField(20);
        txnField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        centerPanel.add(priceLabel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(emailLabel);
        centerPanel.add(phoneLabel);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(txnLabel);
        centerPanel.add(txnField);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom controls
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));

        JButton backBtn = new JButton("⬅ Back");
        backBtn.addActionListener(_ -> mainApp.showScreen("itemDetail"));

        JButton buyBtn = new JButton("✅ Confirm Buy");
        buyBtn.addActionListener(_ -> {
            String txnId = txnField.getText().trim();
            if (txnId.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Please enter a valid Transaction ID.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Purchase confirmed!\nTransaction ID: " + txnId +
                                "\nSeller: " + seller.email(),
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );
                mainApp.db.placeOrder(mainApp.user.uid(), item.id(), 1, txnId);
                mainApp.showScreen("home");
            }
        });

        bottomPanel.add(backBtn);
        bottomPanel.add(buyBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }
}
