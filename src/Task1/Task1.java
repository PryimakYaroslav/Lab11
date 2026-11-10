
package Task1;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Task1 {
    public static void main(String[] args) {
        LoginFacade facade = new LoginFacade();

        NormalLogin normal = new NormalLogin(facade);
        ReconnectLogin reconnect = new ReconnectLogin(facade);

        // Звичайний вхід гравця
        normal.enter("Ярик");

        // Перепідключення після розриву з'єднання
        reconnect.enter("Ярик");

        // Окреме перезавантаження інвентарю
        InventoryLoader inventory = new InventoryLoader();
        inventory.reload("Ярик");
    }
}

// Журнал подій
class GameLog {
    private static final DateTimeFormatter FORMAT =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    static void event(String source, String message) {
        System.out.printf("[%s] [%s] %s%n",
                LocalTime.now().format(FORMAT), source, message);
    }
}

// Підсистема автентифікації
class AuthService {
    void authenticate(String player) {
        GameLog.event("AuthService", "Автентифіковано: " + player);
    }
}

// Завантаження персонажа
class CharacterLoader {
    void load(String player) {
        GameLog.event("CharacterLoader", "Персонажа завантажено: " + player);
    }
}

// Завантаження інвентарю
class InventoryLoader {
    void reload(String player) {
        GameLog.event("InventoryLoader", "Інвентар завантажено: " + player);
    }
}

// Вибір сервера локації
class LocationServerSelector {
    void select(String player) {
        GameLog.event("LocationServerSelector",
                "Сервер локації вибрано для " + player);
    }
}

// Підключення до чату
class ChatConnector {
    void connect(String player) {
        GameLog.event("ChatConnector",
                "Гравця підключено до чату: " + player);
    }
}

// Нарахування щоденного бонусу
class DailyBonusService {
    void award(String player) {
        GameLog.event("DailyBonusService",
                "Щоденний бонус перевірено/нараховано: " + player);
    }
}

// FACADE — об'єднує всі підсистеми
class LoginFacade {
    private final AuthService auth = new AuthService();
    private final CharacterLoader characters = new CharacterLoader();
    private final InventoryLoader inventory = new InventoryLoader();
    private final LocationServerSelector locations = new LocationServerSelector();
    private final ChatConnector chat = new ChatConnector();
    private final DailyBonusService bonus = new DailyBonusService();

    public void enterWorld(String player) {
        GameLog.event("LoginFacade", "Початок входу: " + player);

        auth.authenticate(player);
        characters.load(player);
        inventory.reload(player);
        locations.select(player);
        chat.connect(player);
        bonus.award(player);

        GameLog.event("LoginFacade", "Вхід завершено: " + player);
    }
}

// Звичайний вхід через Facade
class NormalLogin {
    private final LoginFacade facade;

    NormalLogin(LoginFacade facade) {
        this.facade = facade;
    }

    void enter(String player) {
        GameLog.event("NormalLogin", "Звичайний вхід");
        facade.enterWorld(player);
    }
}

// Перепідключення через той самий Facade
class ReconnectLogin {
    private final LoginFacade facade;

    ReconnectLogin(LoginFacade facade) {
        this.facade = facade;
    }

    void enter(String player) {
        GameLog.event("ReconnectLogin", "Перепідключення після розриву");
        facade.enterWorld(player);
    }
}
