package pl.filipczak.app;

public class Tile {
    private int id;
    private int row;
    private int column;
    private boolean isHidden=true;
    private boolean haveBomb=false;
    private boolean isFlagged=false;
    private int minesNear;

    public Tile(int id, int row, int column) {
        this.id = id;
        this.row = row;
        this.column = column;
    }

    public int getId() {
        return id;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public boolean getHaveBomb() {
        return haveBomb;
    }

    public boolean isFlagged() {
        return isFlagged;
    }

    public int getMinesNear() {
        return minesNear;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    public void setHaveBomb(boolean haveBomb) {
        this.haveBomb = haveBomb;
    }

    public void setFlagged(boolean flagged) {
        isFlagged = flagged;
    }

    public void setMinesNear(int minesNear) {
        this.minesNear = minesNear;
    }
}
