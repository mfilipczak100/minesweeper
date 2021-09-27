package pl.filipczak.app.printer;

public interface BoardPrinter {
    void printLongSpace();
    void printMediumSpace();
    void printShortSpace();
    void printFlag();
    void printHiddenField();
    void printBomb();
    void printNumber(int number);
}
