package net.etfbl.pj2.TouristInfo.user;

public class Location {
    private int col;
    private int row;

    public Location() {
        col = row = 0;
    }

    public Location(int c, int r) {
        col = c;
        row = r;
    }

    @Override
    public String toString() {
        return "[" + row + "," + col + "]";
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public void setRow(int row) {
        this.row = row;
    }
}
