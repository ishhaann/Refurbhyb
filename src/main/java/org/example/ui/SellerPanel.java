package org.example.ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import org.example.Main;
import org.example.Types;

public class SellerPanel extends JPanel {

    public SellerPanel(Main mainApp) {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel title = new JLabel("📦 Seller Home", SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        // Center panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1, 20, 20));

        JButton ordersBtn = new JButton("🛒 Orders");
        JButton addItemBtn = new JButton("➕ Add Item");
        JButton myItemsBtn = new JButton("📦 My Items");
        JButton logoutBtn = new JButton("➜] Logout");

        buttonPanel.add(ordersBtn);
        buttonPanel.add(addItemBtn);
        buttonPanel.add(myItemsBtn);
        buttonPanel.add(logoutBtn);

        add(buttonPanel, BorderLayout.CENTER);

        // Button actions
        ordersBtn.addActionListener(_ -> {
            List<Types.OrderItem> sellerOrders = mainApp.db.getUserOrders(null, mainApp.user.uid());
            SellerOrdersPanel sellerOrdersPanel = new SellerOrdersPanel(mainApp, sellerOrders);
            mainApp.addPanel(sellerOrdersPanel, "sellerOrders");
            mainApp.showScreen("sellerOrders");
        });

        addItemBtn.addActionListener(_ -> {
            mainApp.addPanel(new SellerAddPanel(mainApp), "addItem");
            mainApp.showScreen("addItem");
        });

        myItemsBtn.addActionListener(_ -> {
            List<Types.Item> items = mainApp.db.getItems(mainApp.user.uid());
            mainApp.addPanel(new SellerMyItemsPanel(mainApp, items), "myItems");
            mainApp.showScreen("myItems");
        });

        logoutBtn.addActionListener(_ -> {
            mainApp.user = null;
            mainApp.showScreen("welcome");
        });
    }
}
