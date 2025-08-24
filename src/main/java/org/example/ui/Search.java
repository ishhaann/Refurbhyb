package org.example.ui;

import javax.swing.*;
import java.awt.*;
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

    public Search(Main mainApp, String keyword, Integer price) {
        this.keyword = keyword;
        this.price = price;
        setLayout(new FlowLayout());

        // Get search results from DB
        items = mainApp.db.searchItem(keyword, pageNo * itemCount, itemCount, price);

        // Display items
        for (Types.Item item : items) {
            add(new JLabel(item.name()));
        }

        // Pagination
        nextButton = new JButton("Next");
        prevButton = new JButton("Prev");
        pageLabel = new JLabel("" + (pageNo + 1));

        add(prevButton);
        add(pageLabel);
        add(nextButton);

        // Next / Prev actions
        nextButton.addActionListener(e -> nextPage(mainApp));
        prevButton.addActionListener(e -> prevPage(mainApp));
    }

    private void nextPage(Main mainApp) {
        pageNo++;
        refreshItems(mainApp);
    }

    private void prevPage(Main mainApp) {
        if (pageNo > 0) pageNo--;
        refreshItems(mainApp);
    }

    private void refreshItems(Main mainApp) {
        removeAll();
        items = mainApp.db.searchItem(keyword, pageNo * itemCount, itemCount, price);
        for (Types.Item item : items) add(new JLabel(item.name()));
        if (pageNo > 0){
            add(prevButton);
        }
        pageLabel.setText("" + (pageNo + 1));
        add(pageLabel);
        add(nextButton);
        revalidate();
        repaint();
    }
}
