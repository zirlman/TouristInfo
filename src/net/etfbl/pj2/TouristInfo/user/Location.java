package net.etfbl.pj2.TouristInfo.user;

import java.util.Objects;

public class Location {
    int col;
    int row;

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

    @Override
    public boolean equals(Object other) {
        return col == ((Location) other).getCol() && row == ((Location) other).getRow();
    }

    @Override
    public int hashCode() {
        return Objects.hash(col, row);
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
