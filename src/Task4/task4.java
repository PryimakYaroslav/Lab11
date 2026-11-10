package Task4;

import java.util.HashMap;
import java.util.Map;

// Інтерфейс для роботи з локаціями
interface Location {
    void enter(Player player);
}

// Гравець
class Player {

    private final String name;
    private final int level;

    public Player(String name, int level) {
        this.name = name;
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }
}

// Справжня локація
class RealLocation implements Location {

    private final String name;

    public RealLocation(String name) {
        this.name = name;
        loadMap();
    }

    // Завантаження мапи
    private void loadMap() {
        System.out.println("Завантаження мапи: " + name);
    }

    @Override
    public void enter(Player player) {
        System.out.println(
                player.getName() + " увійшов у локацію " + name
        );
    }
}

// Proxy — замісник локації
class LocationProxy implements Location {

    private final String name;
    private final int requiredLevel;

    private RealLocation realLocation;

    public LocationProxy(String name, int requiredLevel) {
        this.name = name;
        this.requiredLevel = requiredLevel;
    }

    @Override
    public void enter(Player player) {

        // Перевірка рівня гравця
        if (player.getLevel() < requiredLevel) {
            System.out.println(
                    "Відмова: Локація доступна з "
                            + requiredLevel + " рівня"
            );
            return;
        }

        // Завантаження лише при першому вході
        if (realLocation == null) {
            realLocation = new RealLocation(name);
        }

        // Передача запиту справжній локації
        realLocation.enter(player);
    }
}

// Ігровий сервер
class GameServer {

    private final Map<String, Location> locations = new HashMap<>();

    // Додавання локації
    public void addLocation(String name, int requiredLevel) {
        locations.put(
                name,
                new LocationProxy(name, requiredLevel)
        );
    }

    // Вхід гравця в локацію
    public void enterLocation(String name, Player player) {

        Location location = locations.get(name);

        if (location == null) {
            System.out.println("Локацію не знайдено.");
            return;
        }

        location.enter(player);
    }
}

// Демонстрація роботи
public class task4 {

    public static void main(String[] args) {

        GameServer server = new GameServer();

        // Додавання п'яти локацій
        server.addLocation("Чорний ліс", 5);
        server.addLocation("Зелені рівнини", 1);
        server.addLocation("Пустеля", 10);
        server.addLocation("Крижані печери", 20);
        server.addLocation("Драконячі гори", 30);

        System.out.println("Сервер запущено. Локації зареєстровано.");

        // Створення гравців
        Player player1 = new Player("Артем", 10);
        Player player2 = new Player("Олег", 15);
        Player player3 = new Player("Іван", 3);

        // Перший вхід у Чорний ліс
        System.out.println("\n--- Перший вхід ---");
        server.enterLocation("Чорний ліс", player1);

        // Повторний вхід у Чорний ліс
        System.out.println("\n--- Повторний вхід ---");
        server.enterLocation("Чорний ліс", player2);

        // Спроба входу в Крижані печери
        System.out.println("\n--- Перевірка доступу ---");
        server.enterLocation("Крижані печери", player3);
    }
}