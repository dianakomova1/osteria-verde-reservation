package ru.tabletime.table;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TableDataInitializer implements CommandLineRunner {
    private final RestaurantTableRepository repository;

    public TableDataInitializer(RestaurantTableRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }

        repository.saveAll(List.of(
                new RestaurantTable(1, 2, "У окна"),
                new RestaurantTable(2, 2, "У окна"),
                new RestaurantTable(3, 4, "Основной зал"),
                new RestaurantTable(4, 4, "Основной зал"),
                new RestaurantTable(5, 4, "На мягких диванах"),
                new RestaurantTable(6, 6, "На втором этаже"),
                new RestaurantTable(7, 6, "На веранде"),
                new RestaurantTable(8, 8, "Основной зал"),
                new RestaurantTable(9, 12, "На втором этаже")
        ));
    }
}
