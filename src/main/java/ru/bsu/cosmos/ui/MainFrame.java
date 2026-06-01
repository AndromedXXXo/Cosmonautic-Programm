package ru.bsu.cosmos.ui;

import ru.bsu.cosmos.service.*;
import ru.bsu.cosmos.ui.panels.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Главное окно приложения «Космонавтика».
 * Содержит шапку, боковое меню навигации, область контента
 * с CardLayout и строку состояния.
 */
public class MainFrame extends JFrame {

    private static final Color HEADER_COLOR = new Color(20, 32, 64);
    private static final Color NAV_COLOR    = new Color(35, 52, 88);
    private static final Color BUTTON_COLOR = new Color(60, 90, 140);
    private static final Color FG_COLOR     = Color.WHITE;

    private final JPanel contentPanel;
    private final CardLayout cardLayout;
    private final JLabel statusBar;

    public MainFrame(SpaceObjectRepository repository,
                     SearchService searchService,
                     StatisticsService statisticsService,
                     SpaceObjectStorage storage) {
        setTitle("Космонавтика — каталог космических объектов");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 720);
        setMinimumSize(new Dimension(1000, 600));
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // Шапка
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_COLOR);
        header.setBorder(new EmptyBorder(16, 24, 16, 24));
        JLabel title = new JLabel("КОСМОНАВТИКА");
        title.setForeground(FG_COLOR);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        JLabel version = new JLabel("v 1.0");
        version.setForeground(new Color(200, 210, 230));
        version.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        header.add(title, BorderLayout.WEST);
        header.add(version, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Боковое меню
        JPanel nav = new JPanel();
        nav.setBackground(NAV_COLOR);
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBorder(new EmptyBorder(20, 12, 20, 12));
        nav.setPreferredSize(new Dimension(220, 0));
        add(nav, BorderLayout.WEST);

        // Контент
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(245, 247, 252));
        add(contentPanel, BorderLayout.CENTER);

        // Строка состояния
        statusBar = new JLabel(" Готово");
        statusBar.setOpaque(true);
        statusBar.setBackground(new Color(220, 225, 235));
        statusBar.setBorder(new EmptyBorder(6, 12, 6, 12));
        add(statusBar, BorderLayout.SOUTH);

        // Панели
        CatalogPanel catalogPanel = new CatalogPanel(repository, storage, this::setStatus);
        SearchPanel searchPanel = new SearchPanel(searchService, this::setStatus);
        StatisticsPanel statsPanel = new StatisticsPanel(statisticsService, this::setStatus);
        AboutPanel aboutPanel = new AboutPanel();

        contentPanel.add(catalogPanel, "catalog");
        contentPanel.add(searchPanel, "search");
        contentPanel.add(statsPanel, "stats");
        contentPanel.add(aboutPanel, "about");

        addNavButton(nav, "Каталог", () -> { cardLayout.show(contentPanel, "catalog"); catalogPanel.refresh(); setStatus("Раздел: Каталог"); });
        addNavButton(nav, "Поиск и фильтр", () -> { cardLayout.show(contentPanel, "search"); searchPanel.refresh(); setStatus("Раздел: Поиск и фильтр"); });
        addNavButton(nav, "Статистика", () -> { cardLayout.show(contentPanel, "stats"); statsPanel.refresh(); setStatus("Раздел: Статистика"); });
        addNavButton(nav, "О программе", () -> { cardLayout.show(contentPanel, "about"); setStatus("Раздел: О программе"); });

        cardLayout.show(contentPanel, "catalog");
    }

    private void addNavButton(JPanel nav, String text, Runnable action) {
        JButton b = new JButton(text);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setBackground(BUTTON_COLOR);
        b.setForeground(FG_COLOR);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(e -> action.run());
        nav.add(b);
        nav.add(Box.createRigidArea(new Dimension(0, 8)));
    }

    public void setStatus(String message) {
        statusBar.setText(" " + message);
    }
}
