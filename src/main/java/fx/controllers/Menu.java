/*
 * Copyright (c) Candidate 181379, 2020.
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package fx.controllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class Menu extends Application {
    public static Label playerScore;
    public static Label computerScore;
    public static TextArea errorLog;

    public static Label getPlayerScore() {
        return playerScore;
    }

    public static Label getComputerScore() {
        return computerScore;
    }

    public static void setComputerScore(int newScore) {
        computerScore.setText(String.valueOf(newScore));
    }

    public static void setHumanScore(int newScore) {
        playerScore.setText(String.valueOf(newScore));
    }

    public static TextArea getErrorLog() {
        return errorLog;
    }

    public static void setErrorLog(String message) {
        errorLog.setText(message);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Menu.fxml"));
        Scene s = new Scene(loader.load());

        primaryStage.setScene(s);
        primaryStage.setResizable(true);
        primaryStage.setTitle("Menu");
//        primaryStage.minHeightProperty().bind(primaryStage.widthProperty().multiply(1));
//        primaryStage.maxHeightProperty().bind(primaryStage.widthProperty().multiply(1));
//        primaryStage.resizableProperty().setValue(true);

        primaryStage.show();


        playerScore = (Label) s.lookup("#playerScore");
        computerScore = (Label) s.lookup("#computerScore");
        errorLog = (TextArea) s.lookup("#errorLog");
    }
}
