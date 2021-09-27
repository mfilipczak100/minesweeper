package pl.filipczak.app;
import pl.filipczak.app.printer.BoardPrinter;
import pl.filipczak.app.printer.HardDifficultyBoardPrinter;
import pl.filipczak.app.printer.SimpleBoardPrinter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Game {

    private Difficulty difficulty;
    private List<Tile> tiles=new ArrayList<>();
    private int rows;
    private int columns;
    private int bombs;
    private int hints;
    private boolean won=false;
    private boolean lost=false;
    private int previousRow=-1;
    private int previousColumn=-1;
    private int bombsRemaining;
    private BoardPrinter boardPrinter;

    public Game(Difficulty difficulty){
        this.difficulty=difficulty;
        calculateRowsAndColumns();
        createTiles();
        placeBombs();
        calculateMinesNearForTiles();
        setHintsBasedOnDifficulty();
        bombsRemaining=bombs;
        if (difficulty==Difficulty.HARD){
            boardPrinter=new HardDifficultyBoardPrinter();
        }else {
            boardPrinter=new SimpleBoardPrinter();
        }
    }

    public long startGame(){
        LocalDateTime time=LocalDateTime.now();
        while (!won&&!lost){
            System.out.println("Pozostalo "+bombsRemaining+" bomb");
            printBoard();
            int option=chooseOption();
            if (option==1){
                selectTile();
            }else if (option==2){
                if (bombsRemaining>0){
                    flagTile();
                }else {
                    System.out.println("Nie mozesz oflagowac pola bo nie ma juz wiecej bomb");
                }
            }else if (option==3){
                useHint();
            }else if (option==4){
                autoSolve();
                lost=true;
            }else{
                lost=true;
            }
            won=checkIfWon();
            if (won){
                printBoard();
            }
        }
        if (won&&!lost){
            System.out.println();
            System.out.println("Gratulacje! Wygrales");
            System.out.println();
            LocalDateTime finishTime=LocalDateTime.now();
            long elapsedTime=Duration.between(time,finishTime).getSeconds();
            return elapsedTime;
        }else {
            System.out.println();
            System.out.println("Przegrales!");
            System.out.println();
            return 0;
        }
    }

    private void calculateRowsAndColumns() {
        if (difficulty==Difficulty.EASY){
            rows=8;
            columns=8;
            bombs=10;
        }else if (difficulty==Difficulty.MEDIUM){
            rows=16;
            columns=16;
            bombs=40;
        }else {
            rows=16;
            columns=30;
            bombs=99;
        }
    }

    private void createTiles() {
        int id=1;
        for (int i=1;i<=rows;i++){
            for (int j=1;j<=columns;j++){
                Tile tile=new Tile(id,i,j);
                tiles.add(tile);
                id++;
            }
        }
    }

    private void placeBombs() {
        List<Tile> tilesCopy = new ArrayList<>(tiles);
        int bombsToPlant=bombs;
        while (bombsToPlant!=0){
            int randomNumber=(int)(Math.random()*tilesCopy.size());
            Tile tile=tilesCopy.get(randomNumber);
            tile.setHaveBomb(true);
            tilesCopy.remove(randomNumber);
            bombsToPlant--;
        }
    }

    private void calculateMinesNearForTiles(){
        for (int i=0;i<tiles.size();i++){
            int row=tiles.get(i).getRow();
            int column=tiles.get(i).getColumn();
            int mines=0;
            for (int j=0;j<tiles.size();j++){
                if (!(tiles.get(j).getRow()==row&&tiles.get(j).getColumn()==column)){
                    if (tiles.get(j).getRow()>=row-1&&tiles.get(j).getRow()<=row+1&&tiles.get(j).getColumn()>=column-1&&tiles.get(j).getColumn()<=column+1){
                        if (tiles.get(j).getHaveBomb()){
                            mines++;
                        }
                    }
                }
            }
            tiles.get(i).setMinesNear(mines);
        }
    }

    private void setHintsBasedOnDifficulty(){
        if (difficulty==Difficulty.EASY){
            hints=3;
        }else if (difficulty==Difficulty.MEDIUM){
            hints=1;
        }else{
            hints=0;
        }
    }

    private void autoSolve(){
        for (int i=0;i<tiles.size();i++){
            if (tiles.get(i).isHidden()){
                tiles.get(i).setHidden(false);
            }
        }
    }

    private boolean checkIfWon(){
        boolean isWon=true;
        for (int i=0;i<tiles.size();i++){
            if (tiles.get(i).isHidden()&&!tiles.get(i).isFlagged()){
                isWon=false;
            }
        }
        return isWon;
    }

    private Pair selectRowAndColumn(){
        String rowLetter="";
        int rowNumber=-1;
        boolean rowVerified=false;
        String columnLetter="";
        int columnNumber=-1;
        boolean columnVerified=false;
        Scanner scanner=new Scanner(System.in);
        while (!rowVerified){
            System.out.println("Wybierz wiersz:");
            rowLetter=scanner.nextLine();
            try {
                rowNumber = Integer.parseInt(rowLetter);
                if (rowNumber>0&&rowNumber<=rows){
                    rowVerified=true;
                }
            }catch (Exception e){

            }
        }
        while (!columnVerified){
            System.out.println("Wybierz kolumne:");
            columnLetter=scanner.nextLine();
            try{
                columnNumber=Integer.parseInt(columnLetter);
                if (columnNumber>0&&columnNumber<=columns){
                    columnVerified=true;
                }
            }catch (Exception e){

            }
        }
        return new Pair(rowNumber,columnNumber);
    }

    private void selectTile(){
        Pair pair=selectRowAndColumn();
        for (int i=0;i<tiles.size();i++){
            if (pair.getRow()==tiles.get(i).getRow()&&pair.getColumn()==tiles.get(i).getColumn()){
                if (tiles.get(i).isFlagged()){
                    System.out.println("Nie mozesz wybrac pola oznaczonego flaga. Musisz najpierw zdjac flage.");
                }else if (tiles.get(i).getHaveBomb()){
                    tiles.get(i).setHidden(false);
                    lost=true;
                }else if (!tiles.get(i).isHidden()){
                    System.out.println("Nie mozesz zaznaczyc wybranego pola.");
                }else if (tiles.get(i).isHidden()&&!tiles.get(i).isFlagged()){
                    tiles.get(i).setHidden(false);
                    if (tiles.get(i).getMinesNear()==0){
                        showNeighbouringTiles(pair.getRow(),pair.getColumn());
                    }
                    previousRow=pair.getRow();
                    previousColumn=pair.getColumn();
                }
            }
        }
    }

    private void flagTile(){
        Pair pair=selectRowAndColumn();
        for (int i=0;i<tiles.size();i++){
            if (pair.getRow()==tiles.get(i).getRow()&&pair.getColumn()==tiles.get(i).getColumn()){
                if (!tiles.get(i).isHidden()){
                    System.out.println("Nie mozesz oflagowac odkrytego pola.");
                }else if (tiles.get(i).isHidden()&&tiles.get(i).isFlagged()){
                    tiles.get(i).setFlagged(false);
                    bombsRemaining++;
                }else if (tiles.get(i).isHidden()&&!tiles.get(i).isFlagged()){
                    tiles.get(i).setFlagged(true);
                    bombsRemaining--;
                }
            }
        }
    }

    private void useHint(){
        if (previousRow==-1||previousColumn==-1){
            System.out.println("Zeby moc skorzystac z podpowiedzi musisz najpierw wybrac pole");
        }else {
            if (hints>0){
                boolean foundBomb=false;
                int distance=1;
                while (!foundBomb&&(distance<=rows||distance<=columns)){
                    for (int i=0;i<tiles.size();i++){
                        if (tiles.get(i).getRow()!=previousRow&&tiles.get(i).getColumn()!=previousColumn&&!foundBomb&&tiles.get(i).getHaveBomb()&&tiles.get(i).isHidden()&&!tiles.get(i).isFlagged()){
                            if (tiles.get(i).getRow()>=previousRow-distance&&tiles.get(i).getRow()<=previousRow+distance){
                                if (tiles.get(i).getColumn()>=previousColumn-distance&&tiles.get(i).getColumn()<=previousColumn+distance){
                                    tiles.get(i).setHidden(false);
                                    foundBomb=true;
                                    hints--;
                                    bombsRemaining--;
                                    System.out.println();
                                    System.out.println("Podpowiedz ujawnila bombe.");
                                    System.out.println("row "+tiles.get(i).getRow()+" - column "+tiles.get(i).getColumn());
                                }
                            }
                        }
                    }
                    distance++;
                }
            }else {
                System.out.println("Nie masz juz wskazowek do wykorzystania");
            }
        }
    }

    private void showNeighbouringTiles(int row,int column){
        for (int i=0;i<tiles.size();i++){
            if (tiles.get(i).getRow()>=row-1&&tiles.get(i).getRow()<=row+1&&tiles.get(i).getColumn()>=column-1&&tiles.get(i).getColumn()<=column+1&&tiles.get(i).isHidden()){
                tiles.get(i).setHidden(false);
                if (tiles.get(i).getMinesNear()==0){
                    showNeighbouringTiles(tiles.get(i).getRow(),tiles.get(i).getColumn());
                }
            }
        }
    }

    private int chooseOption(){
        System.out.println("Co chcesz zrobic?");
        System.out.println("1 - Wybierz pole");
        System.out.println("2 - Oznacz pole flaga/Zdejmij flage");
        System.out.println("3 - Podpowiedz");
        System.out.println("4 - Automatyczne rozwiazanie");
        System.out.println("0 - Wyjscie");
        Scanner scanner=new Scanner(System.in);
        boolean verifiedNumber=false;
        int number=-1;
        while (!verifiedNumber){
            String line=scanner.nextLine();
            try{
                number=Integer.parseInt(line);
                if (number>=0&&number<=4){
                    verifiedNumber=true;
                }
            }catch (Exception e){

            }
        }
        return number;
    }

    private void printBoard(){
        int index=0;
        System.out.println();
        System.out.println();

        for (int i=0;i<rows+2;i++){
            for (int j=0;j<columns+2;j++){
                if ((j==0&&i==0)||(i==1)||(j==1)){
                    boardPrinter.printLongSpace();
                }else if (j==0){
                    System.out.print(i-1);
                    if (i-1<10){
                        boardPrinter.printMediumSpace();
                    }else {
                        boardPrinter.printShortSpace();
                    }
                }else if (i==0){
                    System.out.print(j-1);
                    if (j-1<10){
                        boardPrinter.printMediumSpace();
                    }else {
                        boardPrinter.printShortSpace();
                    }
                }else {
                    if (tiles.get(index).isHidden()){
                        if (tiles.get(index).isFlagged()){
                            boardPrinter.printFlag();
                        }else {
                            boardPrinter.printHiddenField();
                        }
                    }else {
                        if (tiles.get(index).getHaveBomb()){
                            boardPrinter.printBomb();
                        }else {
                            boardPrinter.printNumber(tiles.get(index).getMinesNear());
                        }
                    }
                    index++;
                }
            }
            System.out.println();
            System.out.println();
        }
    }
}
