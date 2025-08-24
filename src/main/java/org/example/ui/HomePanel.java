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

        // Top panel for search
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("🔍");
        topPanel.add(searchField);
        topPanel.add(searchButton);

        add(topPanel, BorderLayout.NORTH);

        // Center panel for results or welcome message
        resultsContainer = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Welcome, " + mainApp.user.name());
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        resultsContainer.add(welcomeLabel, BorderLayout.CENTER);

        add(resultsContainer, BorderLayout.CENTER);

        // Search action
        searchButton.addActionListener(_ -> {
            String keyword = searchField.getText().trim();
            if (!keyword.isEmpty()) {
                showSearchResults(keyword);
            }
        });
    }

    private void showSearchResults(String keyword) {
        // Remove old results
        resultsContainer.removeAll();

        // Create new Search panel
        Search searchPanel = new Search(mainApp, keyword, null);
        resultsContainer.add(searchPanel, BorderLayout.CENTER);

        // Refresh UI
        resultsContainer.revalidate();
        resultsContainer.repaint();
    }
}
