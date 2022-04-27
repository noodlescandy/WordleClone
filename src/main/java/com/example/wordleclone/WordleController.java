package com.example.wordleclone;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.Locale;

public class WordleController {
    @FXML
    private VBox rows;
    @FXML
    private VBox keyboard;
    @FXML
    private StackPane notificationBox;
    private int currentBox = 0; // initial locations
    private int currentRow = 0;
    private String currentWord = "";
    private String correctWord;
    private ArrayList<String> acceptedWords = new ArrayList<>();
    private final Color ldGray = Color.rgb(135, 138, 140);
    private final Color llGray = Color.rgb(211, 214, 218);
    private final Color gray = Color.rgb(120, 124, 126);
    private final Color green = Color.rgb(106, 170, 100);
    private final Color yellow = Color.rgb(201, 180, 88);
    private boolean gameOver = false;


    @FXML
    /* handles onscreen keyboard buttons */
    protected void keyClick(ActionEvent actionEvent){
        Node source = (Node) actionEvent.getSource(); // find what button was pressed
        String id = source.getId(); // id types: keyA, enterKey, backKey
        switch (id.charAt(0)) {
            case 'k' -> // normal key button
                    letterInput(id.substring(3));
            case 'e' -> // enter key
                    nextRow();
            case 'b' -> // backspace key
                    backspace();
        }
    }

    /* handles keyboard typing */
    public void keyboardPress(KeyCode key) {
        if(key.isLetterKey()){ // only input letter if it is a letter
            String id = key.getName();
            letterInput(id);
        }
        else{
            switch (key.getName()) {
                case "BACK_SPACE", "Backspace" -> backspace();
                case "ENTER" -> nextRow();
            }
        }
    }

    private void letterInput(String letter) {
        if(currentBox != 5 && !gameOver){ // all five letters not entered
            Group box = getBox(currentBox);
            ((Rectangle)box.getChildren().get(0)).setStroke(ldGray); // set stroke of Rectangle to darker
            ((Text) box.getChildren().get(1)).setText(letter);
            currentWord += letter;
            currentBox++;
        }
    }

    private void backspace(){
        if(currentBox != 0 && !gameOver){ // at least one letter has been entered
            Group box = getBox(currentBox - 1);
            ((Text) box.getChildren().get(1)).setText("");
            ((Rectangle) box.getChildren().get(0)).setStroke(llGray); // revert color of stroke
            currentWord = currentWord.substring(0, currentWord.length()-1);
            currentBox--;
        }
    }



    // gets the text of the box that can have text entered
    private Group getBox(int boxIndex){
        HBox row = (HBox) rows.getChildren().get(currentRow);
        return (Group) row.getChildren().get(boxIndex);
    }

    private void nextRow(){
        if(currentBox == 5){ // all five letters have been entered
            if(isValidWord()) {
                checkWord();
                if (currentRow != 5) { // not on last row
                    currentRow++;
                    currentBox = 0;
                    currentWord = "";
                } else { // last row, you lost
                    if(!gameOver) // deal with getting it in 6
                        displayNotification(correctWord, true);
                    gameOver = true;
                }
            }
            else{
                displayNotification("Not in word list", false);
            }
        }
    }

    public void setWord(String word){
        correctWord = word;
        System.out.println(correctWord);
    }

    // checks current word against the correct word, not checking if valid word
    private void checkWord(){
        // check currentWord vs correctWord
        int[] values = new int[5];
        StringBuilder checkedLetters = new StringBuilder(); // letters that have been used already
        ArrayList<ArrayList<Integer>> actionIndexes = new ArrayList<>();
        ArrayList<Integer> differences = new ArrayList<>();

        // go through currentWord letter by letter
        int greenCount = 0;
        for(int i = 0; i < 5; i++){
            char currentLetter = currentWord.charAt(i); // letter being considered
            int indInActual = correctWord.indexOf(currentLetter); // place of letter in the real word
            if(indInActual != -1){
                // if letter has not yet been checked
                if(checkedLetters.toString().indexOf(currentLetter) == -1) {
                    // get count of this letter in correct word and currentWord
                    // number of the currentLetter provided that are in the actual word
                    int actualCount = 0;
                    // number of the currentLetter provided that are in the guessed word
                    int providedCount = 0;
                    ArrayList<Integer> corIndexes = new ArrayList<>(); // indexes of the letter in currentWord
                    for (int j = 0; j < 5; j++) {
                        if (correctWord.charAt(j) == currentLetter)
                            actualCount++;
                        if (currentWord.charAt(j) == currentLetter){
                            providedCount++;
                            corIndexes.add(j);
                        }
                    }
                    if(actualCount < providedCount){ // will need to remove some # of yellows from word
                        actionIndexes.add(corIndexes);
                        differences.add(providedCount - actualCount);
                    }
                    checkedLetters.append(currentLetter); // letter has been checked
                }
                if(currentWord.charAt(i) == correctWord.charAt(i)){ // works for multiple of same letter
                    values[i] = 2; // green
                    greenCount++;
                }
                else
                    values[i] = 1; // yellow
            }
        }
        if(actionIndexes.size() != 0){ // if there are letters to deal with
            for(int i = 0; i < actionIndexes.size(); i++){
                for(int j = actionIndexes.get(i).size()-1; j > -1; j--){
                    if(differences.get(i) != 0) { // are there still yellows to remove?
                        int index = actionIndexes.get(i).get(j);
                        if (values[index] != 2) { // removing a yellow, not green
                            differences.set(i, differences.get(i) - 1); // 1 less to remove
                            values[index]--; // set yellow box to gray
                        }
                    }
                }
            }
        }
        setBoxColors(values);
        setButtonColors(values);
        if(greenCount == 5){
            String winMessage = "";
            System.out.println(currentRow);
            switch (currentRow){
                case 0 -> winMessage = "Genius";
                case 1 -> winMessage = "Magnificent";
                case 2 -> winMessage = "Impressive";
                case 3 -> winMessage = "Splendid";
                case 4 -> winMessage = "Great";
                case 5 -> winMessage = "Phew";
            }
            displayNotification(winMessage, true);
            gameOver = true;
        }
    }

    private void setButtonColors(int[] values){
        // set colors of keyButtons based on values corresponding to currentWord
        for(int i = 0; i < 5; i++){
            char letter = currentWord.charAt(i);
            Button key = (Button) keyboard.lookup("#key" + letter);
            key.setTextFill(Color.WHITE);
            if(key.getUserData() == null) {
                switch (values[i]) {
                    case 0 -> key.setStyle("-fx-background-color: #787C7E; "); // gray
                    case 1 -> key.setStyle("-fx-background-color: #C9B458; "); // yellow
                    case 2 -> {
                        key.setStyle("-fx-background-color: #6AAA64; "); // green
                        key.setUserData(true); // cannot be changed from green to yellow
                    }
                }
            }

        }
    }

    private void setBoxColors(int[] values){
        HBox row = (HBox) rows.getChildren().get(currentRow);
        for(int i = 0; i < row.getChildren().size(); i++){
            Group box = (Group) row.getChildren().get(i);
            ((Text) box.getChildren().get(1)).setFill(Color.WHITE);
            Rectangle rect = (Rectangle) box.getChildren().get(0);
            rect.setStroke(Color.WHITE);
            switch (values[i]){
                case 0 -> rect.setFill(gray);
                case 1 -> rect.setFill(yellow);
                case 2 -> rect.setFill(green);
            }
        }
    }

    // checks currentWord, making sure it matches one of the allowed guesses
    private boolean isValidWord(){
        return acceptedWords.contains(currentWord.toLowerCase());
    }

    public void setAcceptedWords(ArrayList<String> acceptedWords) {
        this.acceptedWords = acceptedWords;
    }


    private void displayNotification(String text, boolean isPersistent){
        // separate thread so it doesn't hold up the screen
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Text textBox = (Text) notificationBox.getChildren().get(1);
                Rectangle rectangle = (Rectangle) notificationBox.getChildren().get(0);
                textBox.setText(text);
                textBox.setTextAlignment(TextAlignment.CENTER);
                double width = text.length()*6.5+15;
                rectangle.setWidth(width);
                Runnable show = new Runnable() {
                    @Override
                    public void run() {
                        notificationBox.setVisible(true);
                    }
                };
                Runnable hide = new Runnable() {
                    @Override
                    public void run() {
                        if (!isPersistent) {
                            notificationBox.setVisible(false);
                        }
                    }
                };
                Platform.runLater(show);
                try{
                    Thread.sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Platform.runLater(hide);
            }
        });
        thread.setDaemon(true);
        thread.start();
    }
}