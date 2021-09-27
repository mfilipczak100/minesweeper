package pl.filipczak.app.printer;

public class SimpleBoardPrinter implements BoardPrinter{

    @Override
    public void printLongSpace() {
        System.out.print("    ");
    }

    @Override
    public void printMediumSpace() {
        System.out.print("   ");
    }

    @Override
    public void printShortSpace() {
        System.out.print("  ");
    }

    @Override
    public void printFlag() {
        System.out.print("F   ");
    }

    @Override
    public void printHiddenField() {
        System.out.print("_   ");
    }

    @Override
    public void printBomb() {
        System.out.print("B   ");
    }

    @Override
    public void printNumber(int number) {
        System.out.print(number+"   ");
    }
}
