package org.example.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import org.example.Main;
import org.example.Types;

public class Search extends JPanel {
    int pageNo = 0;
    int itemCount = 50;
    List<Types.Item> items;
    String keyword;
    Integer price;

    JButton nextButton;
    JButton prevButton;
    JLabel pageLabel;
    JPanel itemsPanel;
    JScrollPane scrollPane;

    public Search(Main mainApp, String keyword, Integer price) {
        this.keyword = keyword;
        this.price = price;

        setLayout(new BorderLayout(10, 10));

        // Item container
        itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        scrollPane = new JScrollPane(itemsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);

        // Pagination
        JPanel pagination = new JPanel(new FlowLayout());
        nextButton = new JButton("Next");
        prevButton = new JButton("Prev");
        pageLabel = new JLabel("" + (pageNo + 1));
        pagination.add(prevButton);
        pagination.add(pageLabel);
        pagination.add(nextButton);
        add(pagination, BorderLayout.SOUTH);

        // Load items
        items = mainApp.db.searchItem(keyword, pageNo * itemCount, itemCount, price);
        displayItems(mainApp);

        // Pagination actions
        nextButton.addActionListener(_ -> {
            pageNo++;
            refreshItems(mainApp);
        });
        prevButton.addActionListener(_ -> {
            if (pageNo > 0)
                pageNo--;
            refreshItems(mainApp);
        });
    }

    private void displayItems(Main mainApp) {
        itemsPanel.removeAll();

        for (Types.Item item : items) {
            JPanel itemPanel = new JPanel();
            itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
            itemPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.GRAY, 1),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)));
            itemPanel.setBackground(Color.WHITE);

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
            imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel nameLabel = new JLabel(item.name());
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel priceLabel = new JLabel("₹" + item.price());
            priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            itemPanel.add(imageLabel);
            itemPanel.add(Box.createVerticalStrut(5));
            itemPanel.add(nameLabel);
            itemPanel.add(priceLabel);
            itemPanel.add(Box.createVerticalStrut(10));

            // Event handler for product click
            itemPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    ItemDetailPanel detailPanel = new ItemDetailPanel(mainApp, item);
                    mainApp.addPanel(detailPanel, "itemDetail");
                    mainApp.showScreen("itemDetail");
                }
            });

            itemsPanel.add(itemPanel);
            itemsPanel.add(Box.createVerticalStrut(10));
        }

        itemsPanel.revalidate();
        itemsPanel.repaint();

        pageLabel.setText("" + (pageNo + 1));
    }

    private void refreshItems(Main mainApp) {
        items = mainApp.db.searchItem(keyword, pageNo * itemCount, itemCount, price);
        displayItems(mainApp);
    }
}
