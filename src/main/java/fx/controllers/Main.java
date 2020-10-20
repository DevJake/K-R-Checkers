 /*
  * Copyright (c) Jake Dean, 2020.
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
 import javafx.scene.layout.BorderPane;
 import javafx.scene.layout.HBox;
 import javafx.scene.layout.Pane;
 import javafx.scene.layout.Priority;
 import javafx.stage.Stage;

 import java.io.IOException;

 public class Main extends Application {

     public static void main(String[] args) throws IOException {
         launch(args);
     }

     @Override
     public void start(Stage primaryStage) throws IOException {
         FXMLLoader loader = new FXMLLoader(getClass().getResource("/Main.fxml"));
         Scene s = new Scene(loader.load());

         primaryStage.setScene(s);
         primaryStage.setResizable(true);
         primaryStage.show();

         Pane mainPane = (Pane) s.lookup("#pane");
         BorderPane borderPane = (BorderPane) s.lookup("#border");

         mainPane.prefWidthProperty().bind(mainPane.widthProperty());
         mainPane.prefHeightProperty().bind(mainPane.heightProperty());
     }
 }
