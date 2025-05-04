import java.util.LinkedList;

class BarberShop {
    private final int maxChairs;
    private final LinkedList<Customer> waitingCustomers = new LinkedList<>();
    private boolean barberBusy = false;

    public BarberShop(int chairs) {
        this.maxChairs = chairs;
    }

    // دخول الزبون إلى الصالون
    public synchronized void enter(Customer customer) {
        if (waitingCustomers.size() >= maxChairs) {
            System.out.println(customer.getName() + " leaves (no free chair).");
            return;
        }

        waitingCustomers.addLast(customer);
        System.out.println(customer.getName() + " sits. Waiting: " + waitingCustomers.size());

        // إيقاظ الحلاق في حال كان نائمًا
        notify();
    }

    // استدعاء الزبون التالي من قبل الحلاق
    public synchronized Customer nextCustomer() {
        while (waitingCustomers.isEmpty()) {
            try {
                System.out.println("Barber sleeps...");
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        Customer c = waitingCustomers.removeFirst();
        System.out.println("Barber is cutting " + c.getName() + "'s hair.");
        barberBusy = true;
        return c;
    }

    // بعد انتهاء الخدمة
    public synchronized void finish(Customer customer) {
        System.out.println(customer.getName() + " is done.");
        barberBusy = false;
    }
}

class Customer extends Thread {
    private final BarberShop shop;

    public Customer(BarberShop shop, int id) {
        super("Customer-" + id);
        this.shop = shop;
    }

    public void run() {
        shop.enter(this);
    }
}

class Barber extends Thread {
    private final BarberShop shop;

    public Barber(BarberShop shop) {
        super("Barber");
        this.shop = shop;
    }

    public void run() {
        while (true) {
            Customer customer = shop.nextCustomer();
            try {
                Thread.sleep((int)(Math.random() * 500));  // مدة الحلاقة
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            shop.finish(customer);
        }
    }
}

public class SleepingBarberMonitor {
    public static void main(String[] args) {
        int chairs = 3;
        int totalCustomers = 10;

        BarberShop shop = new BarberShop(chairs);
        new Barber(shop).start();

        for (int i = 1; i <= totalCustomers; i++) {
            new Customer(shop, i).start();
            try {
                Thread.sleep(150); // وصول الزبائن بفاصل زمني
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

