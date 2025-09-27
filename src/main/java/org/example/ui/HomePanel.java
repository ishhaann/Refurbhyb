package org.example.ui;

import javax.swing.*;
import java.awt.*;
import org.example.Main;

public class HomePanel extends JPanel {
    private JPanel resultsContainer;
    private Main mainApp;

    public HomePanel(Main mainApp) {
        this.mainApp = mainApp;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Container for top panels
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        add(topContainer, BorderLayout.NORTH);

        // Top panel 1
        JPanel quickPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton myOrdersButton = new JButton("📦 My Orders");
        JButton logOutButton = new JButton("LogOut");

        quickPanel.add(myOrdersButton);
        quickPanel.add(logOutButton);

        topContainer.add(quickPanel);

        // Top panel 2
        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        JTextField searchField = new JTextField();
        JButton searchButton = new JButton("🔍");
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        topContainer.add(searchPanel);
        topContainer.add(Box.createVerticalStrut(10));

        // Center panel
        resultsContainer = new JPanel();
        resultsContainer.setLayout(new BoxLayout(resultsContainer, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(resultsContainer);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);

        JLabel welcomeLabel = new JLabel("Welcome, " + mainApp.user.name());
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultsContainer.add(Box.createVerticalStrut(20));
        resultsContainer.add(welcomeLabel);
        resultsContainer.add(Box.createVerticalStrut(20));

        // Button actions
        searchButton.addActionListener(_ -> {
            String keyword = searchField.getText().trim();
            if (!keyword.isEmpty()) {
                showSearchResults(keyword);
            }
        });

        myOrdersButton.addActionListener(_ -> {
            MyOrdersPanel myOrders = new MyOrdersPanel(mainApp);
            mainApp.addPanel(myOrders, "MyOrder");
            mainApp.showScreen("MyOrder");
        });

        logOutButton.addActionListener(_ -> {
            mainApp.user = null;
            mainApp.showScreen("welcome");
        });
    }

    private void showSearchResults(String keyword) {
        resultsContainer.removeAll();
        Search searchPanel = new Search(mainApp, keyword, null);
        searchPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultsContainer.add(Box.createVerticalStrut(10));
        resultsContainer.add(searchPanel);
        resultsContainer.add(Box.createVerticalGlue());
        resultsContainer.revalidate();
        resultsContainer.repaint();
    }
}
