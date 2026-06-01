package ru.bsu.cosmos.ui.panels;

import ru.bsu.cosmos.model.ObjectType;
import ru.bsu.cosmos.model.SpaceObject;
import ru.bsu.cosmos.service.SpaceObjectRepository;
import ru.bsu.cosmos.service.SpaceObjectStorage;
import ru.bsu.cosmos.ui.dialogs.AddObjectDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

/** Панель каталога: таблица всех объектов + CRUD-операции. */
public class CatalogPanel extends BasePanel {

    private final SpaceObjectRepository repository;
    private final SpaceObjectStorage storage;
    private final DefaultTableModel tableModel;
    private final JTable table;

    public CatalogPanel(SpaceObjectRepository repository,
                        SpaceObjectStorage storage,
                        Consumer<String> statusListener) {
        super(statusListener);
        this.repository = repository;
        this.storage = storage;

        add(createTitle("Каталог космических объектов"), BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
            new Object[]{"ID", "Тип", "Название", "Расстояние, св. лет",
                "Масса, кг", "Дата открытия"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(26);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setAutoCreateRowSorter(true);
        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 12));
        buttons.setOpaque(false);
        JButton addBtn = createButton("Добавить объект");
        JButton editBtn = createButton("Редактировать");
        JButton delBtn = createButton("Удалить");
        JButton viewBtn = createButton("Подробнее");
        JButton refreshBtn = createButton("Обновить");
        buttons.add(addBtn);
        buttons.add(editBtn);
        buttons.add(delBtn);
        buttons.add(viewBtn);
        buttons.add(refreshBtn);
        add(buttons, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> onAdd());
        editBtn.addActionListener(e -> onEdit());
        delBtn.addActionListener(e -> onDelete());
        viewBtn.addActionListener(e -> onView());
        refreshBtn.addActionListener(e -> refresh());

        refresh();
    }

    @Override
    public void refresh() {
        tableModel.setRowCount(0);
        List<SpaceObject> all = repository.findAll();
        for (SpaceObject o : all) {
            tableModel.addRow(new Object[]{
                o.getId(),
                o.getType().getRussianName(),
                o.getName(),
                String.format("%.4f", o.getDistanceLy()),
                String.format("%.3e", o.getMassKg()),
                o.getDiscoveryDate()
            });
        }
        setStatus("Загружено объектов: " + all.size());
    }

    private SpaceObject selectedObject() {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        long id = (long) tableModel.getValueAt(table.convertRowIndexToModel(row), 0);
        return repository.findById(id).orElse(null);
    }

    private void onAdd() {
        ObjectType[] options = ObjectType.values();
        ObjectType chosen = (ObjectType) JOptionPane.showInputDialog(
            this, "Выберите тип объекта:", "Новый объект",
            JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (chosen == null) return;
        AddObjectDialog dlg = new AddObjectDialog(SwingUtilities.getWindowAncestor(this), chosen, null);
        dlg.setVisible(true);
        SpaceObject created = dlg.getResult();
        if (created != null) {
            repository.add(created);
            saveSilently();
            refresh();
            setStatus("Добавлен объект: " + created.getName());
        }
    }

    private void onEdit() {
        SpaceObject obj = selectedObject();
        if (obj == null) { showInfo("Выберите строку в таблице"); return; }
        AddObjectDialog dlg = new AddObjectDialog(SwingUtilities.getWindowAncestor(this), obj.getType(), obj);
        dlg.setVisible(true);
        SpaceObject edited = dlg.getResult();
        if (edited != null) {
            edited.setId(obj.getId());
            repository.update(edited);
            saveSilently();
            refresh();
            setStatus("Обновлён объект: " + edited.getName());
        }
    }

    private void onDelete() {
        SpaceObject obj = selectedObject();
        if (obj == null) { showInfo("Выберите строку в таблице"); return; }
        int r = JOptionPane.showConfirmDialog(this,
            "Удалить объект «" + obj.getName() + "»?",
            "Подтверждение", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            repository.deleteById(obj.getId());
            saveSilently();
            refresh();
            setStatus("Удалён объект: " + obj.getName());
        }
    }

    private void onView() {
        SpaceObject obj = selectedObject();
        if (obj == null) { showInfo("Выберите строку в таблице"); return; }
        JTextArea area = new JTextArea(obj.getShortInfo()
            + "\n\nРасстояние: " + String.format("%.4f", obj.getDistanceLy()) + " св. лет"
            + "\nМасса: " + String.format("%.3e", obj.getMassKg()) + " кг"
            + "\nДата открытия: " + obj.getDiscoveryDate()
            + "\n\nОписание:\n" + obj.getDescription());
        area.setEditable(false);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        JScrollPane sp = new JScrollPane(area);
        sp.setPreferredSize(new Dimension(500, 360));
        JOptionPane.showMessageDialog(this, sp, "Сведения об объекте",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void saveSilently() {
        try { storage.save(repository.findAll()); }
        catch (Exception ex) { setStatus("Ошибка сохранения: " + ex.getMessage()); }
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Информация", JOptionPane.INFORMATION_MESSAGE);
    }
}
