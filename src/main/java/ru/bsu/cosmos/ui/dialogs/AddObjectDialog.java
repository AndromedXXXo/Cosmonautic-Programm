package ru.bsu.cosmos.ui.dialogs;

import ru.bsu.cosmos.model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Универсальный диалог для добавления или редактирования объектов
 * любого из шести типов. Форма строится динамически в зависимости от типа.
 */
public class AddObjectDialog extends JDialog {

    private final ObjectType type;
    private final SpaceObject original;
    private SpaceObject result;
    private final Map<String, JTextField> commonFields = new LinkedHashMap<>();
    private final Map<String, JComponent> typeFields = new LinkedHashMap<>();

    public AddObjectDialog(Window owner, ObjectType type, SpaceObject toEdit) {
        super(owner, toEdit == null ? "Новый объект — " + type.getRussianName()
                                    : "Редактирование — " + toEdit.getName(),
              ModalityType.APPLICATION_MODAL);
        this.type = type;
        this.original = toEdit;
        buildUI();
        if (toEdit != null) prefill(toEdit);
        pack();
        setMinimumSize(new Dimension(420, getHeight()));
        setLocationRelativeTo(owner);
    }

    private void buildUI() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(16, 16, 16, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        int row = 0;
        row = addField(form, gbc, row, "Название",          new JTextField(22), true);
        row = addField(form, gbc, row, "Дата открытия (ГГГГ-ММ-ДД)", new JTextField(22), true);
        row = addField(form, gbc, row, "Расстояние от Земли (св. лет)", new JTextField(22), true);
        row = addField(form, gbc, row, "Масса (кг)",        new JTextField(22), true);
        row = addField(form, gbc, row, "Описание",          new JTextField(22), true);

        // Поля, зависящие от типа
        switch (type) {
            case STAR:
                row = addTypeField(form, gbc, row, "Спектральный класс", new JTextField(22));
                row = addTypeField(form, gbc, row, "Светимость (L⊙)", new JTextField(22));
                row = addTypeField(form, gbc, row, "Температура (K)", new JTextField(22));
                break;
            case PLANET:
                row = addTypeField(form, gbc, row, "Период обращения (сут.)", new JTextField(22));
                row = addTypeField(form, gbc, row, "Имеется атмосфера", new JCheckBox());
                row = addTypeField(form, gbc, row, "Кол-во спутников", new JTextField(22));
                row = addTypeField(form, gbc, row, "Родительская звезда", new JTextField(22));
                break;
            case SATELLITE:
                row = addTypeField(form, gbc, row, "Родительское тело", new JTextField(22));
                row = addTypeField(form, gbc, row, "Радиус орбиты (км)", new JTextField(22));
                row = addTypeField(form, gbc, row, "Период обращения (сут.)", new JTextField(22));
                break;
            case ASTEROID:
                row = addTypeField(form, gbc, row, "Диаметр (км)", new JTextField(22));
                row = addTypeField(form, gbc, row, "Группа", new JTextField(22));
                break;
            case COMET:
                row = addTypeField(form, gbc, row, "Период обращения (лет)", new JTextField(22));
                row = addTypeField(form, gbc, row, "Длина хвоста (км)", new JTextField(22));
                break;
            case SPACECRAFT:
                row = addTypeField(form, gbc, row, "Дата запуска (ГГГГ-ММ-ДД)", new JTextField(22));
                row = addTypeField(form, gbc, row, "Агентство", new JTextField(22));
                row = addTypeField(form, gbc, row, "Тип миссии", new JTextField(22));
                row = addTypeField(form, gbc, row, "Активен", new JCheckBox());
                break;
        }

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        JButton okBtn = new JButton("Сохранить");
        JButton cancelBtn = new JButton("Отмена");
        buttons.add(cancelBtn);
        buttons.add(okBtn);

        getContentPane().setLayout(new BorderLayout());
        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        okBtn.addActionListener(e -> trySave());
        cancelBtn.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(okBtn);
    }

    private int addField(JPanel panel, GridBagConstraints gbc, int row,
                         String label, JTextField field, boolean common) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(new JLabel(label + ":"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(field, gbc);
        commonFields.put(label, field);
        return row + 1;
    }

    private int addTypeField(JPanel panel, GridBagConstraints gbc, int row,
                             String label, JComponent comp) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(new JLabel(label + ":"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(comp, gbc);
        typeFields.put(label, comp);
        return row + 1;
    }

    private void prefill(SpaceObject o) {
        commonFields.get("Название").setText(o.getName());
        commonFields.get("Дата открытия (ГГГГ-ММ-ДД)").setText(
            o.getDiscoveryDate() == null ? "" : o.getDiscoveryDate().toString());
        commonFields.get("Расстояние от Земли (св. лет)").setText(String.valueOf(o.getDistanceLy()));
        commonFields.get("Масса (кг)").setText(String.valueOf(o.getMassKg()));
        commonFields.get("Описание").setText(o.getDescription());
        if (o instanceof Star) {
            Star s = (Star) o;
            setText("Спектральный класс", s.getSpectralClass());
            setText("Светимость (L⊙)", String.valueOf(s.getLuminositySolar()));
            setText("Температура (K)", String.valueOf(s.getSurfaceTemperatureK()));
        } else if (o instanceof Planet) {
            Planet p = (Planet) o;
            setText("Период обращения (сут.)", String.valueOf(p.getOrbitalPeriodDays()));
            ((JCheckBox) typeFields.get("Имеется атмосфера")).setSelected(p.isHasAtmosphere());
            setText("Кол-во спутников", String.valueOf(p.getMoonsCount()));
            setText("Родительская звезда", p.getParentStarName());
        } else if (o instanceof Satellite) {
            Satellite s = (Satellite) o;
            setText("Родительское тело", s.getParentBodyName());
            setText("Радиус орбиты (км)", String.valueOf(s.getOrbitalRadiusKm()));
            setText("Период обращения (сут.)", String.valueOf(s.getOrbitalPeriodDays()));
        } else if (o instanceof Asteroid) {
            Asteroid a = (Asteroid) o;
            setText("Диаметр (км)", String.valueOf(a.getDiameterKm()));
            setText("Группа", a.getAsteroidGroup());
        } else if (o instanceof Comet) {
            Comet c = (Comet) o;
            setText("Период обращения (лет)", String.valueOf(c.getOrbitalPeriodYears()));
            setText("Длина хвоста (км)", String.valueOf(c.getTailLengthKm()));
        } else if (o instanceof Spacecraft) {
            Spacecraft sc = (Spacecraft) o;
            setText("Дата запуска (ГГГГ-ММ-ДД)",
                sc.getLaunchDate() == null ? "" : sc.getLaunchDate().toString());
            setText("Агентство", sc.getAgency());
            setText("Тип миссии", sc.getMissionType());
            ((JCheckBox) typeFields.get("Активен")).setSelected(sc.isActive());
        }
    }

    private void setText(String key, String value) {
        JComponent c = typeFields.get(key);
        if (c instanceof JTextField tf) tf.setText(value == null ? "" : value);
    }

    private String text(String key) {
        return commonFields.get(key).getText().trim();
    }

    private String typeText(String key) {
        JComponent c = typeFields.get(key);
        return c instanceof JTextField tf ? tf.getText().trim() : "";
    }

    private void trySave() {
        try {
            String name = text("Название");
            LocalDate discoveryDate = parseDate(text("Дата открытия (ГГГГ-ММ-ДД)"));
            double distance = parseDouble(text("Расстояние от Земли (св. лет)"));
            double mass = parseDouble(text("Масса (кг)"));
            String descr = text("Описание");

            SpaceObject obj;
            switch (type) {
                case STAR:
                    obj = new Star(name, discoveryDate, distance, mass, descr,
                        typeText("Спектральный класс"),
                        parseDouble(typeText("Светимость (L⊙)")),
                        parseDouble(typeText("Температура (K)")));
                    break;
                case PLANET:
                    obj = new Planet(name, discoveryDate, distance, mass, descr,
                        parseDouble(typeText("Период обращения (сут.)")),
                        ((JCheckBox) typeFields.get("Имеется атмосфера")).isSelected(),
                        (int) parseDouble(typeText("Кол-во спутников")),
                        typeText("Родительская звезда"));
                    break;
                case SATELLITE:
                    obj = new Satellite(name, discoveryDate, distance, mass, descr,
                        typeText("Родительское тело"),
                        parseDouble(typeText("Радиус орбиты (км)")),
                        parseDouble(typeText("Период обращения (сут.)")));
                    break;
                case ASTEROID:
                    obj = new Asteroid(name, discoveryDate, distance, mass, descr,
                        parseDouble(typeText("Диаметр (км)")),
                        typeText("Группа"));
                    break;
                case COMET:
                    obj = new Comet(name, discoveryDate, distance, mass, descr,
                        parseDouble(typeText("Период обращения (лет)")),
                        parseDouble(typeText("Длина хвоста (км)")));
                    break;
                case SPACECRAFT:
                    obj = new Spacecraft(name, discoveryDate, distance, mass, descr,
                        parseDate(typeText("Дата запуска (ГГГГ-ММ-ДД)")),
                        typeText("Агентство"),
                        typeText("Тип миссии"),
                        ((JCheckBox) typeFields.get("Активен")).isSelected());
                    break;
                default:
                    obj = null;
            }
            this.result = obj;
            dispose();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Некорректные данные: " + ex.getMessage(),
                "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
        }
    }

    private double parseDouble(String s) {
        if (s == null || s.isBlank()) return 0;
        try { return Double.parseDouble(s.replace(',', '.')); }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("ожидалось число, получено «" + s + "»");
        }
    }

    private LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        try { return LocalDate.parse(s); }
        catch (DateTimeParseException e) {
            throw new IllegalArgumentException("дата должна быть в формате ГГГГ-ММ-ДД");
        }
    }

    public SpaceObject getResult() { return result; }
}
