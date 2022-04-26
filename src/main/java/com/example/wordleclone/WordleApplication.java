package com.example.wordleclone;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class WordleApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        ArrayList<String> answers = loadWords("src/main/resources/com/example/wordleclone/wordle-nyt-answers-alphabetical.txt");

        String word = generateWord(answers);

        Font.loadFont(getClass().getResourceAsStream("ClearSans-Regular.ttf"), 20);

        FXMLLoader fxmlLoader = new FXMLLoader(WordleApplication.class.getResource("wordle-view.fxml"));
        Parent root = fxmlLoader.load(); // highest level of the fxml hierarchy
        WordleController controller = fxmlLoader.getController(); // controls the ui stuff
        Scene scene = new Scene(root);

        scene.setOnKeyPressed(e -> controller.keyboardPress(e.getCode())); // keyboard handler

        ArrayList<String> acceptedWords = loadWords("src/main/resources/com/example/wordleclone/wordle-nyt-allowed-guesses.txt");
        acceptedWords.addAll(answers);
        controller.setAcceptedWords(acceptedWords);
        controller.setWord(word.toUpperCase());
        stage.setTitle("Wordle");
        stage.setScene(scene);
        stage.show();
    }

    // generates a word for wordle
    public String generateWord(ArrayList<String> answers){

        Random rand = new Random();
        return answers.get(rand.nextInt(answers.size())); // return a word from the list
    }

    public static void main(String[] args) {
        launch();
    }

    public ArrayList<String> loadWords(String fileName) throws IOException {
        File file = new File(fileName);
        FileReader reader = new FileReader(file);
        BufferedReader buf = new BufferedReader(reader);
        String line;
        ArrayList<String> words = new ArrayList<>();
        while((line = buf.readLine()) != null){
            words.add(line);
        }
        return words;
    }
}