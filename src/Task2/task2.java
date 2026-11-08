package Task2;

import java.math.BigDecimal;

// Результат покупки у форматі магазину
class PaymentResult {
    private final boolean successful;
    private final String message;

    public PaymentResult(boolean successful, String message) {
        this.successful = successful;
        this.message = message;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public String getMessage() {
        return message;
    }
}

// Власний інтерфейс магазину
interface PaymentGateway {
    PaymentResult purchase(BigDecimal amountUah, int gameCurrency);
}

// Перша стороння платіжна платформа
class ExternalPaymentPlatform {

    public String executePayment(int amountCents, String currencyCode) {
        System.out.println("[Platform 1] executePayment()");
        System.out.println("Сума: " + amountCents + " центів");
        System.out.println("Валюта: " + currencyCode);

        return "SUCCESS:PAYMENT_123";
    }
}

// Адаптер першої платформи
class PaymentAdapter implements PaymentGateway {

    private final ExternalPaymentPlatform platform;

    public PaymentAdapter(ExternalPaymentPlatform platform) {
        this.platform = platform;
    }

    @Override
    public PaymentResult purchase(BigDecimal amountUah, int gameCurrency) {
        // Перетворення гривень у копійки
        int amountCents = amountUah
                .multiply(BigDecimal.valueOf(100))
                .intValueExact();

        // Виклик сторонньої платформи
        String response = platform.executePayment(amountCents, "UAH");

        // Перетворення відповіді у формат магазину
        if (response.startsWith("SUCCESS")) {
            return new PaymentResult(true, "Платіж успішний");
        }

        return new PaymentResult(false, "Помилка платежу");
    }
}

// Друга умовна платіжна платформа
class SecondPaymentPlatform {

    public int makeTransaction(int amountInMinorUnits, String currency) {
        System.out.println("[Platform 2] makeTransaction()");
        System.out.println("Сума: " + amountInMinorUnits + " копійок");
        System.out.println("Валюта: " + currency);

        return 200; // 200 означає успішну оплату
    }
}

// Адаптер другої платформи
class SecondPaymentAdapter implements PaymentGateway {

    private final SecondPaymentPlatform platform;

    public SecondPaymentAdapter(SecondPaymentPlatform platform) {
        this.platform = platform;
    }

    @Override
    public PaymentResult purchase(BigDecimal amountUah, int gameCurrency) {
        int amountInMinorUnits = amountUah
                .multiply(BigDecimal.valueOf(100))
                .intValueExact();

        int response = platform.makeTransaction(
                amountInMinorUnits, "UAH"
        );

        if (response == 200) {
            return new PaymentResult(true, "Платіж успішний");
        }

        return new PaymentResult(false, "Помилка платежу");
    }
}

// Магазин гри
class GameStore {

    private final PaymentGateway paymentGateway;
    private int playerBalance = 0;

    public GameStore(PaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    public void buyCurrency(BigDecimal price, int amount) {
        System.out.println("\n--- Покупка ігрової валюти ---");
        System.out.println("Ціна: " + price + " грн");
        System.out.println("Кількість: " + amount);

        PaymentResult result = paymentGateway.purchase(price, amount);

        System.out.println("Результат магазину: " + result.getMessage());

        if (result.isSuccessful()) {
            playerBalance += amount;
            System.out.println("Валюту нараховано гравцю!");
        } else {
            System.out.println("Покупку скасовано.");
        }

        System.out.println("Баланс гравця: " + playerBalance);
    }
}

// Демонстрація
public class task2 {

    public static void main(String[] args) {

        // Перша платформа
        PaymentGateway firstAdapter =
                new PaymentAdapter(new ExternalPaymentPlatform());

        GameStore store1 = new GameStore(firstAdapter);

        store1.buyCurrency(new BigDecimal("100.00"), 500);

        // Друга платформа
        PaymentGateway secondAdapter =
                new SecondPaymentAdapter(new SecondPaymentPlatform());

        GameStore store2 = new GameStore(secondAdapter);

        store2.buyCurrency(new BigDecimal("100.00"), 500);
    }
}
