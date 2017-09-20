package net.etfbl.pj2.TouristInfo.attractions;

import net.etfbl.pj2.TouristInfo.enums.PayMethod;

import java.util.Random;

public class AmusementPark extends TouristAttraction {
    private PayMethod payMethod = PayMethod.PAID;
    private int price;

    public AmusementPark(String n, String l, int p) {
        super(n, l);
        price = p % 81;
        className = getClass().getSimpleName();
    }

    public AmusementPark(String n, String l) {
        super(n, l);
        price = new Random().nextInt(71) + 10;
        className = getClass().getSimpleName();
    }

    public PayMethod getPayMethod() {
        return payMethod;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
