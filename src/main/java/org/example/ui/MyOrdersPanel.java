package org.example.ui;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.example.Main;
import org.example.Types;

public class MyOrdersPanel extends JPanel {

    public MyOrdersPanel(Main mainApp) {
        List<Types.OrderItem> orders = mainApp.db.getUserOrders(mainApp.user.uid(), null);

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("📦 My Orders", SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        if (orders.isEmpty()) {
            JLabel emptyLabel = new JLabel("No orders yet.");
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            listPanel.add(emptyLabel);
        } else {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

            for (Types.OrderItem order : orders) {
                Types.Item item = mainApp.db.getItem(order.item_id());
                JPanel orderPanel = new JPanel();
                orderPanel.setLayout(new BoxLayout(orderPanel, BoxLayout.Y_AXIS));
                orderPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.GRAY, 1),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));

                // Item Name
                JLabel itemLabel = new JLabel("Item: " + item.name());

                // Payment status
                String paymentStatus = order.payment_success() ? "✅ Payment Accepted" : "❌ Payment Not verified";
                JLabel paymentLabel = new JLabel(paymentStatus);

                // Shipping status
                JLabel shipLabel;
                if (order.shipped()) {
                    shipLabel = new JLabel("Shipped ✔ | Tracking: " + order.tracking_id() +
                                           " via " + order.shipping_partner());
                } else {
                    shipLabel = new JLabel("Not Shipped Yet ❌");
                }

                // Date
                JLabel dateLabel = new JLabel("Ordered on: " + order.created_at().format(fmt));

                orderPanel.add(itemLabel);
                orderPanel.add(paymentLabel);
                orderPanel.add(shipLabel);
                orderPanel.add(dateLabel);

                listPanel.add(orderPanel);
                listPanel.add(Box.createVerticalStrut(10));
            }
        }

        JScrollPane scrollPane = new JScrollPane(listPanel);
        add(scrollPane, BorderLayout.CENTER);

        // Back button
        JButton backBtn = new JButton("⬅ Back");
        backBtn.addActionListener(_ -> mainApp.showScreen("home"));
        add(backBtn, BorderLayout.SOUTH);
    }
}
