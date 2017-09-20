package net.etfbl.pj2.TouristInfo.attractions;

import net.etfbl.pj2.TouristInfo.enums.PayMethod;

public class HistoricalMonument extends TouristAttraction {
    private String description;
    private String image;
    private PayMethod payMethod = PayMethod.FREE;

    public HistoricalMonument(String n, String l, String d, String im) {
        super(n, l);
        description = d;
        image = im;
        className = getClass().getSimpleName();
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setPayMethod(PayMethod payMethod) {
        this.payMethod = payMethod;
    }

    public PayMethod getPayMethod() { // TODO: Mozda ce trebati brisati ovaj geter !!!
        return payMethod;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }
}
