package net.etfbl.pj2.TouristInfo.attractions;

import net.etfbl.pj2.TouristInfo.enums.PayMethod;

public class Church extends TouristAttraction {
    private PayMethod payMethod;
    private int collectedMoney;

    public Church(String n, String l) {
        super(n, l);
        payMethod = PayMethod.DONATION;
        className = getClass().getSimpleName();
        collectedMoney = 0;
    }

    void donate(int donation) {
        collectedMoney += (donation > 0) ? donation : 0;
    }

    public PayMethod getPayMethod() {
        return payMethod;
    }

    public int getCollectedMoney() {
        return collectedMoney;
    }

    public void setCollectedMoney(int collectedMoney) {
        this.collectedMoney = collectedMoney;
    }
}
