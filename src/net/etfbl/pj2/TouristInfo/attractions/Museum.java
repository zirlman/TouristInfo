package net.etfbl.pj2.TouristInfo.attractions;

import net.etfbl.pj2.TouristInfo.enums.PayMethod;

import java.time.LocalDate;
import java.util.Random;

public class Museum extends TouristAttraction {
    private String flier;
    private PayMethod payMethod;
    private int price;

    public Museum(String n, String l, String f) {
        super(n, l);
        flier = f;
        payMethod = ((LocalDate.now().getDayOfWeek().getValue() % 2) == 1) ? PayMethod.PAID : PayMethod.FREE;   // Ako je neparan dan ulaz se naplacuje
        price = (payMethod == PayMethod.PAID) ? new Random().nextInt(71) + 10 : 0;                      // Ako je neparan dan, cijena se automatski generise

        className = getClass().getSimpleName();
    }

    public int getPrice() {
        return price;
    }

    public String getFlier() {
        return flier;
    }

    public PayMethod getPayMethod() {
        return payMethod;
    }

    public void setFlier(String flier) {
        this.flier = flier;
    }
}
