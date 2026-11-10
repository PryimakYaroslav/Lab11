
package Task3;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Task3 {
    public static void main(String[] args) {
        CharacterStats hero = new BasicCharacter("Ярик", 20, 12, 10);
        CombatSystem combat = new CombatSystem();

        // Персонаж без ефектів
        combat.show(hero, "Без ефектів");

        // Накладаємо зілля сили та благословення
        hero = new StrengthPotion(hero);
        hero = new Blessing(hero);
        combat.show(hero, "Зілля сили + благословення");

        // Додаємо отруту і прокляття повільності
        hero = new Poison(hero);
        hero = new SlownessCurse(hero);
        combat.show(hero, "Отрута + прокляття повільності");

        // Виконуємо атаку
        combat.attack(hero, "Лісовий вовк");

        // Перевіряємо накладання однакових ефектів
        CharacterStats stacked = new StrengthPotion(
                new StrengthPotion(
                        new BasicCharacter("Тест", 20, 12, 10)
                )
        );
        combat.show(stacked, "Два зілля сили");

        // Демонструємо зняття ефекту
        CharacterStats base = new BasicCharacter("Перевірка", 20, 12, 10);
        CharacterStats withPotion = new StrengthPotion(base);

        combat.show(withPotion, "Ефект накладено");

        CharacterStats afterRemoval =
                ((CharacterEffect) withPotion).removeThisEffect();

        combat.show(afterRemoval, "Ефект знято");
    }
}

// Журнал подій
class EffectLog {
    private static final DateTimeFormatter FORMAT =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    static void event(String source, String message) {
        System.out.printf("[%s] [%s] %s%n",
                LocalTime.now().format(FORMAT), source, message);
    }
}

// Спільний інтерфейс персонажа
interface CharacterStats {
    String getName();
    int getAttack();
    int getDefense();
    int getSpeed();
}

// Звичайний персонаж без ефектів
class BasicCharacter implements CharacterStats {
    private final String name;
    private final int attack;
    private final int defense;
    private final int speed;

    BasicCharacter(String name, int attack, int defense, int speed) {
        this.name = name;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
    }

    public String getName() {
        return name;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public int getSpeed() {
        return speed;
    }
}

// Базовий декоратор
abstract class CharacterEffect implements CharacterStats {
    protected final CharacterStats wrapped;

    protected CharacterEffect(CharacterStats wrapped) {
        this.wrapped = wrapped;
    }

    public String getName() {
        return wrapped.getName();
    }

    public int getAttack() {
        return wrapped.getAttack();
    }

    public int getDefense() {
        return wrapped.getDefense();
    }

    public int getSpeed() {
        return wrapped.getSpeed();
    }

    // Знімає поточний зовнішній ефект
    public CharacterStats removeThisEffect() {
        return wrapped;
    }
}

// Зілля сили: +8 атаки
class StrengthPotion extends CharacterEffect {
    StrengthPotion(CharacterStats character) {
        super(character);
    }

    public int getAttack() {
        return wrapped.getAttack() + 8;
    }
}

// Благословення: +3 атаки та +4 захисту
class Blessing extends CharacterEffect {
    Blessing(CharacterStats character) {
        super(character);
    }

    public int getAttack() {
        return wrapped.getAttack() + 3;
    }

    public int getDefense() {
        return wrapped.getDefense() + 4;
    }
}

// Отрута: зменшує атаку та захист
class Poison extends CharacterEffect {
    Poison(CharacterStats character) {
        super(character);
    }

    public int getAttack() {
        return Math.max(0, wrapped.getAttack() - 2);
    }

    public int getDefense() {
        return Math.max(0, wrapped.getDefense() - 3);
    }
}

// Прокляття повільності: -4 швидкості
class SlownessCurse extends CharacterEffect {
    SlownessCurse(CharacterStats character) {
        super(character);
    }

    public int getSpeed() {
        return Math.max(0, wrapped.getSpeed() - 4);
    }
}

// Щит: +7 захисту
class Shield extends CharacterEffect {
    Shield(CharacterStats character) {
        super(character);
    }

    public int getDefense() {
        return wrapped.getDefense() + 7;
    }
}

// Бойова система
class CombatSystem {
    void show(CharacterStats character, String stage) {
        EffectLog.event("CombatSystem",
                stage + ": " + character.getName()
                        + " | атака=" + character.getAttack()
                        + " | захист=" + character.getDefense()
                        + " | швидкість=" + character.getSpeed());
    }

    void attack(CharacterStats attacker, String target) {
        EffectLog.event("CombatSystem",
                attacker.getName() + " атакує " + target
                        + " із силою " + attacker.getAttack()
                        + " (характеристики надає ланцюжок декораторів)");
    }
}
