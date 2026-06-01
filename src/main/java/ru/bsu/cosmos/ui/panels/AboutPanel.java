package ru.bsu.cosmos.ui.panels;

import javax.swing.*;
import java.awt.*;

public class AboutPanel extends BasePanel {

    public AboutPanel() {
        super(null);
        add(createTitle("О программе"), BorderLayout.NORTH);

        JTextArea info = new JTextArea(
            "Программа «Космонавтика»\n" +
            "Версия 1.0\n\n" +
            "Каталог космических объектов с поддержкой:\n" +
            "  • CRUD-операций над объектами шести типов\n" +
            "    (звёзды, планеты, спутники, астероиды, кометы, космические аппараты);\n" +
            "  • поиска и многопараметрической фильтрации;\n" +
            "  • сортировки и анализа статистики каталога;\n" +
            "  • полиморфного хранения данных в формате JSON.\n\n" +
            "Реализована на языке Java 17 (Swing + Gson) в рамках курсовой работы\n" +
            "по дисциплине «Объектно-ориентированное программирование».\n\n" +
            "Автор: Муштенко Андрей Алексеевич, группа 12002453\n" +
            "Научный руководитель: Фёдоров Вячеслав Игоревич");
        info.setEditable(false);
        info.setBackground(new Color(245, 247, 252));
        info.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        add(info, BorderLayout.CENTER);
    }

    @Override public void refresh() {}
}
