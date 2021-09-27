package pl.filipczak.app;

import java.io.*;
import java.util.Scanner;

public class Application {

    private String filePath = "wyniki.txt";

    public static void main(String[] args) {
        new Application().start();
    }

    public void start(){
        boolean end=false;
        while (!end){
            System.out.println("Wybierz:");
            System.out.println("1 - Nowa gra");
            System.out.println("2 - Wyniki");
            System.out.println("0 - Koniec");
            boolean verifiedNumber=false;
            Scanner scanner=new Scanner(System.in);
            int number=-1;
            while (!verifiedNumber){
                String line=scanner.nextLine();
                try{
                    number=Integer.parseInt(line);
                    if (number>=0&&number<=2){
                        verifiedNumber=true;
                    }
                }catch (Exception e){

                }
            }
            if (number==1){
                System.out.println("Wprowadz nazwe gracza");
                String name=scanner.nextLine();
                Difficulty difficulty=chooseDifficulty();
                Game game=new Game(difficulty);
                long time=game.startGame();
                saveRecordInAFile(name,time,difficulty);
            }else if (number==0){
                end=true;
            }else {
                loadScoresFromFile();
            }
        }
    }

    private Difficulty chooseDifficulty() {
        System.out.println("Wybierz poziom trudnosci:");
        System.out.println("1 - Latwy");
        System.out.println("2 - Sredni");
        System.out.println("3 - Trudny");
        Scanner scanner = new Scanner(System.in);
        boolean validatedNumber = false;
        int number = -1;
        while (!validatedNumber) {
            String line = scanner.nextLine();
            try {
                number = Integer.parseInt(line);
                if (number >= 0 && number <= 3) {
                    validatedNumber = true;
                }
            } catch (Exception e) {

            }
            if (!validatedNumber) {
                System.out.println("Wprowadz poprawny argument");
            }
        }
        if (number == 1) {
            return Difficulty.EASY;
        } else if (number == 2) {
            return Difficulty.MEDIUM;
        } else {
            return Difficulty.HARD;
        }
    }

    private void saveRecordInAFile(String name, long time, Difficulty difficulty) {
        try (
                FileWriter fileWriter = new FileWriter(filePath,true);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        ) {
            bufferedWriter.write(name + "\n");
            bufferedWriter.write(time + "\n");
            if (difficulty == Difficulty.EASY) {
                bufferedWriter.write("easy\n");
            } else if (difficulty == Difficulty.MEDIUM) {
                bufferedWriter.write("medium\n");
            } else {
                bufferedWriter.write("hard\n");
            }
            bufferedWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean verifyTime(String time) {
        boolean verified = true;
        try {
            long timeNumber = Long.parseLong(time);
        } catch (Exception e) {
            verified = false;
        }
        return verified;
    }

    private boolean verifyDifficulty(String difficulty) {
        if (difficulty.equals("easy") || difficulty.equals("medium") || difficulty.equals("hard")) {
            return true;
        } else {
            return false;
        }
    }

    private void loadScoresFromFile() {
        System.out.println();
        File file = new File(filePath);
        if (file.exists()) {
            boolean atLeastOneVerifiedRecord = false;
            String line;
            int step = 1;
            String name="";
            String time="";
            boolean timeVerified=false;
            String difficulty="";
            boolean difficultyVerified=false;
            try (
                    BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            ) {
                while ((line=bufferedReader.readLine())!=null){
                    if (step==1){
                        name=line;
                        step++;
                    }else if (step==2){
                        time=line;
                        timeVerified=verifyTime(time);
                        step++;
                    }else if (step==3){
                        difficulty=line;
                        difficultyVerified=verifyDifficulty(difficulty);
                        step++;
                    }else {
                        if (timeVerified&&difficultyVerified){
                            atLeastOneVerifiedRecord=true;
                            System.out.print(name+" - "+difficulty+" - ");
                            if (time.charAt(0)=='0'){
                                System.out.println("PRZEGRANA");
                            }else{
                                System.out.println("WYGRANA - "+time+" sekund");
                            }
                        }
                        step=1;
                    }
                }
                if (!atLeastOneVerifiedRecord){
                    System.out.println("Plik z wynikami zostal zmodyfikowany i nie posiada zadnych poprawnych wynikow.");
                }
            } catch (IOException e) {

            }
        } else {
            System.out.println("Jeszcze nie ma zadnych wynikow");
        }
        System.out.println();
    }
}
