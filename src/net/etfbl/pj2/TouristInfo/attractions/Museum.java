package net.etfbl.pj2.TouristInfo.attractions;

import net.etfbl.pj2.TouristInfo.enums.PayMethod;

import java.time.LocalDate;
import java.util.Random;

public class Museum extends TouristAttraction {
    private String flyer;
    private PayMethod payMethod;
    private int price;

    public Museum(String n, String l, String f) {
        super(n, l);
        flyer = f;
        payMethod = ((LocalDate.now().getDayOfWeek().getValue() % 2) == 1) ? PayMethod.PAID : PayMethod.FREE;   // Ako je neparan dan ulaz se naplacuje
        price = (payMethod == PayMethod.PAID) ? new Random().nextInt(71) + 10 : 0;                      // Ako je neparan dan, cijena se automatski generise

        className = getClass().getSimpleName();
    }

    public int getPrice() {
        return price;
    }

    public String getFlyer() {
        return flyer;
    }

    public void setFlyer(String flyer) {
        this.flyer = flyer;
    }
}
