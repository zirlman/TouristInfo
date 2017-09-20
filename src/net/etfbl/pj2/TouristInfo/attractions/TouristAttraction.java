package net.etfbl.pj2.TouristInfo.attractions;

import java.io.Serializable;

public class TouristAttraction implements Serializable {
    protected String name;
    protected String location;
    protected String className;     // Koristi se za jednostavan unos sadrzaja u attractionColumn

    public TouristAttraction(String n, String l) {
        name = n;
        location = l;
    }

    @Override
    public String toString() {
        return name + " " + location;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public String getClassName() {
        return className;
    }
}
