 /*
  * Copyright (c) Jake Dean, 2020.
  *
  * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
  * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
  * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
  */

 package fx.controllers;

 import comms.Bridge;
 import comms.BridgeListener;
 import ent.Board;
 import err.BoardSpacingException;
 import err.EventProtocolMismatchException;
 import event.Event;
 import javafx.application.Application;
 import javafx.fxml.FXMLLoader;
 import javafx.scene.Scene;
 import javafx.scene.layout.BorderPane;
 import javafx.scene.layout.Pane;
 import javafx.stage.Stage;
 import util.PrintUtil;

 import java.io.File;
 import java.io.IOException;
 import java.net.URISyntaxException;

 public class Main extends Application {
     public static Board mainBoard;

     static {
         try {
             mainBoard = new Board.Builder().build();
         } catch (BoardSpacingException e) {
             e.printStackTrace();
         }
     }

     public static void main(String[] args) throws IOException, EventProtocolMismatchException, URISyntaxException {
//         launch(args);

//         System.out.println(mainBoard.toString());

         PrintUtil.asFormatted(mainBoard);

//         System.out.println(mainBoard.getTotalPieces());

         Event.Manager.registerListener(new BridgeListener());

//         File pyFile = new File("Main.py").getCanonicalFile();
//         Runtime.getRuntime().exec("python /c start python " + pyFile.getAbsolutePath());
//         Runtime.getRuntime().exec("cmd /k");

//         ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/K", "Start", "python", Main.class.getResource
//         ("/Main.py")));

//         String absolutePath = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI())
//         .getAbsolutePath();

         File pyMain = new File("./Main.py");
//         System.out.println(pyMain);

         ProcessBuilder builder = new ProcessBuilder("python", pyMain.getAbsolutePath());

         Process process = builder.start();


         Runtime.getRuntime().addShutdownHook(new Thread(() -> {
             System.out.println("Calling shutdown hook...");
             process.destroy();
         }));

         Bridge.open();

//         Bridge.send(ProtocolManager.encodeFor(new BoardUpdateEvent(mainBoard, mainBoard)));
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
