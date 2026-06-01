package ru.bsu.cosmos.ui.panels;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Абстрактная базовая панель — общая функциональность всех панелей раздела.
 * Демонстрирует ООП-паттерн «Шаблонный метод».
 */
public abstract class BasePanel extends JPanel {

    protected final Consumer<String> statusListener;

    protected BasePanel(Consumer<String> statusListener) {
        this.statusListener = statusListener;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 24, 20, 24));
        setBackground(new Color(245, 247, 252));
    }

    /** Создаёт заголовок раздела (общий стиль). */
    protected JLabel createTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 20));
        label.setForeground(new Color(20, 32, 64));
        label.setBorder(new EmptyBorder(0, 0, 12, 0));
        return label;
    }

    /** Создаёт стилизованную кнопку. */
    protected JButton createButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(new Color(60, 90, 140));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    protected void setStatus(String message) {
        if (statusListener != null) statusListener.accept(message);
    }

    /** Обновляет данные панели (вызывается при переключении вкладок). */
    public abstract void refresh();
}
