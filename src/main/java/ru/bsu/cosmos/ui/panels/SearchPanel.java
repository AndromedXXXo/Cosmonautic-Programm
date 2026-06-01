package ru.bsu.cosmos.ui.panels;

import ru.bsu.cosmos.model.ObjectType;
import ru.bsu.cosmos.model.SpaceObject;
import ru.bsu.cosmos.service.SearchService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

/** Панель поиска и фильтрации объектов. */
public class SearchPanel extends BasePanel {

    private final SearchService searchService;
    private final DefaultTableModel tableModel;
    private final JComboBox<Object> typeBox;
    private final JTextField nameField;
    private final JTextField maxDistanceField;
    private final JTextField minMassField;
    private final JComboBox<String> sortBox;

    public SearchPanel(SearchService searchService, Consumer<String> statusListener) {
        super(statusListener);
        this.searchService = searchService;

        add(createTitle("Поиск и фильтрация"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Тип объекта:"), gbc);
        gbc.gridx = 1; gbc.gridy = row;
        Object[] types = new Object[ObjectType.values().length + 1];
        types[0] = "(любой)";
        for (int i = 0; i < ObjectType.values().length; i++) types[i + 1] = ObjectType.values()[i];
        typeBox = new JComboBox<>(types);
        form.add(typeBox, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Подстрока в названии:"), gbc);
        gbc.gridx = 1; gbc.gridy = row;
        nameField = new JTextField(20);
        form.add(nameField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Макс. расстояние (св. лет):"), gbc);
        gbc.gridx = 1; gbc.gridy = row;
        maxDistanceField = new JTextField(20);
        form.add(maxDistanceField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Мин. масса (кг):"), gbc);
        gbc.gridx = 1; gbc.gridy = row;
        minMassField = new JTextField(20);
        form.add(minMassField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Сортировать по:"), gbc);
        gbc.gridx = 1; gbc.gridy = row;
        sortBox = new JComboBox<>(new String[]{"Названию", "Массе", "Расстоянию", "Дате открытия"});
        form.add(sortBox, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JButton applyBtn = createButton("Применить");
        JButton resetBtn = createButton("Сбросить");
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setOpaque(false);
        btnRow.add(applyBtn);
        btnRow.add(resetBtn);
        form.add(btnRow, gbc);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(form, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
            new Object[]{"ID", "Тип", "Название", "Расстояние, св. лет",
                "Масса, кг", "Дата открытия"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(tableModel);
        table.setRowHeight(26);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        center.add(new JScrollPane(table), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        applyBtn.addActionListener(e -> applyFilter());
        resetBtn.addActionListener(e -> resetForm());

        refresh();
    }

    private void resetForm() {
        typeBox.setSelectedIndex(0);
        nameField.setText("");
        maxDistanceField.setText("");
        minMassField.setText("");
        sortBox.setSelectedIndex(0);
        applyFilter();
    }

    private void applyFilter() {
        Object sel = typeBox.getSelectedItem();
        ObjectType type = (sel instanceof ObjectType) ? (ObjectType) sel : null;
        String name = nameField.getText();
        Double maxDist = parseDoubleOrNull(maxDistanceField.getText());
        Double minMass = parseDoubleOrNull(minMassField.getText());

        List<SpaceObject> filtered = searchService.filter(type, name, maxDist, minMass);

        Comparator<SpaceObject> cmp;
        String sortKey = (String) sortBox.getSelectedItem();
        if ("Массе".equals(sortKey)) cmp = SearchService.byMass();
        else if ("Расстоянию".equals(sortKey)) cmp = SearchService.byDistance();
        else if ("Дате открытия".equals(sortKey)) cmp = SearchService.byDiscoveryDate();
        else cmp = SearchService.byName();
        filtered = searchService.sort(filtered, cmp);

        tableModel.setRowCount(0);
        for (SpaceObject o : filtered) {
            tableModel.addRow(new Object[]{
                o.getId(), o.getType().getRussianName(), o.getName(),
                String.format("%.4f", o.getDistanceLy()),
                String.format("%.3e", o.getMassKg()),
                o.getDiscoveryDate()
            });
        }
        setStatus("Найдено объектов: " + filtered.size());
    }

    private Double parseDoubleOrNull(String s) {
        if (s == null || s.isBlank()) return null;
        try { return Double.parseDouble(s.replace(',', '.').trim()); }
        catch (NumberFormatException ex) { return null; }
    }

    @Override
    public void refresh() {
        applyFilter();
    }
}
