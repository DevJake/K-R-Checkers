 /*
  * Copyright (c) Jake Dean, 2020.
  *
  * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
  * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
  * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
  */

 package fx.controllers;

 import ent.Board;
 import err.BoardSpacingException;
 import javafx.application.Application;
 import javafx.fxml.FXMLLoader;
 import javafx.scene.Scene;
 import javafx.scene.layout.BorderPane;
 import javafx.scene.layout.Pane;
 import javafx.stage.Stage;
 import util.PrintUtil;

 import java.io.IOException;

 public class Main extends Application {
     public static Board mainBoard;

     static {
         try {
             mainBoard = new Board.Builder().build();
         } catch (BoardSpacingException e) {
             e.printStackTrace();
         }
     }

     public static void main(String[] args) throws IOException {
//         launch(args);

         System.out.println(mainBoard.toString());

         PrintUtil.asFormatted(mainBoard);

         System.out.println(mainBoard.getTotalPieces());
     }

     @Override
     public void start(Stage primaryStage) throws IOException {
         FXMLLoader loader = new FXMLLoader(getClass().getResource("/Main.fxml"));
         Scene s = new Scene(loader.load());

         primaryStage.setScene(s);
         primaryStage.setResizable(false);
         primaryStage.setHeight(1000);
         primaryStage.setWidth(1900);
         primaryStage.show();

         Pane mainPane = (Pane) s.lookup("#pane");
         BorderPane borderPane = (BorderPane) s.lookup("#border");

         mainPane.prefWidthProperty().bind(mainPane.widthProperty());
         mainPane.prefHeightProperty().bind(mainPane.heightProperty());
     }
 }
