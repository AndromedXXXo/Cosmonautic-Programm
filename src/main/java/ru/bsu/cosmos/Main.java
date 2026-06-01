package ru.bsu.cosmos;

import ru.bsu.cosmos.service.*;
import ru.bsu.cosmos.ui.MainFrame;

import javax.swing.*;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Точка входа в программу «Космонавтика».
 */
public class Main {

    public static void main(String[] args) {
        Path dataFile = Paths.get("data", "catalog.json");
        SpaceObjectRepository repository = new SpaceObjectRepository();
        SpaceObjectStorage storage = new SpaceObjectStorage(dataFile);

        try {
            java.util.List<ru.bsu.cosmos.model.SpaceObject> saved = storage.load();
            if (saved.isEmpty()) {
                repository.addAll(DemoDataProvider.defaultCatalog());
                storage.save(repository.findAll());
            } else {
                repository.addAll(saved);
            }
        } catch (Exception e) {
            repository.addAll(DemoDataProvider.defaultCatalog());
        }

        SearchService searchService = new SearchService(repository);
        StatisticsService statisticsService = new StatisticsService(repository);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(repository, searchService, statisticsService, storage);
            frame.setVisible(true);
        });
    }
}
