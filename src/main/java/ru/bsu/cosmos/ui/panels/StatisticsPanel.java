package ru.bsu.cosmos.ui.panels;

import ru.bsu.cosmos.model.ObjectType;
import ru.bsu.cosmos.service.StatisticsService;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.function.Consumer;

/** Панель статистики каталога. */
public class StatisticsPanel extends BasePanel {

    private final StatisticsService statisticsService;
    private final JTextArea reportArea;
    private final ChartPanel chartPanel;

    public StatisticsPanel(StatisticsService statisticsService, Consumer<String> statusListener) {
        super(statusListener);
        this.statisticsService = statisticsService;

        add(createTitle("Статистика каталога"), BorderLayout.NORTH);

        reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        reportArea.setBackground(new Color(252, 253, 255));

        chartPanel = new ChartPanel();
        chartPanel.setPreferredSize(new Dimension(360, 0));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            new JScrollPane(reportArea), chartPanel);
        split.setResizeWeight(0.6);
        split.setBorder(null);
        add(split, BorderLayout.CENTER);

        JButton refreshBtn = createButton("Обновить статистику");
        refreshBtn.addActionListener(e -> refresh());
        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        south.setOpaque(false);
        south.add(refreshBtn);
        add(south, BorderLayout.SOUTH);

        refresh();
    }

    @Override
    public void refresh() {
        reportArea.setText(statisticsService.buildSummaryReport());
        reportArea.setCaretPosition(0);
        chartPanel.setData(statisticsService.countByType());
        chartPanel.repaint();
        setStatus("Статистика обновлена");
    }

    /** Внутренняя панель с круговой диаграммой по типам объектов. */
    private static class ChartPanel extends JPanel {
        private Map<ObjectType, Long> data;
        private static final Color[] COLORS = {
            new Color(220, 90, 70),  new Color(70, 140, 220),
            new Color(80, 180, 120), new Color(220, 170, 60),
            new Color(170, 100, 200), new Color(100, 200, 200)
        };

        ChartPanel() {
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createTitledBorder("Распределение по типам"));
        }

        void setData(Map<ObjectType, Long> data) { this.data = data; }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (data == null || data.isEmpty()) return;
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            long total = data.values().stream().mapToLong(Long::longValue).sum();
            if (total == 0) return;

            int side = Math.min(getWidth(), getHeight() - 40) - 60;
            int x = (getWidth() - side) / 2;
            int y = 30;

            double startAngle = 0;
            int idx = 0;
            for (Map.Entry<ObjectType, Long> e : data.entrySet()) {
                if (e.getValue() == 0) { idx++; continue; }
                double arc = 360.0 * e.getValue() / total;
                g2.setColor(COLORS[idx % COLORS.length]);
                g2.fillArc(x, y, side, side, (int) startAngle, (int) Math.ceil(arc));
                startAngle += arc;
                idx++;
            }

            // Легенда
            int legendY = y + side + 10;
            int legendX = 16;
            int colWidth = (getWidth() - 32) / 2;
            idx = 0;
            int colCount = 0;
            for (Map.Entry<ObjectType, Long> e : data.entrySet()) {
                int cx = legendX + (colCount % 2) * colWidth;
                int cy = legendY + (colCount / 2) * 18;
                g2.setColor(COLORS[idx % COLORS.length]);
                g2.fillRect(cx, cy, 12, 12);
                g2.setColor(Color.DARK_GRAY);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                g2.drawString(e.getKey().getRussianName() + " (" + e.getValue() + ")",
                    cx + 18, cy + 11);
                idx++;
                colCount++;
            }
        }
    }
}
