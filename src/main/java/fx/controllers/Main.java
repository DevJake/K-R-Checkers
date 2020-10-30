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
 import comms.protocol.BoardUpdateProtocol;
 import comms.protocol.ProtocolManager;
 import ent.Board;
 import ent.Player;
 import ent.Tile;
 import err.EventProtocolMismatchException;
 import event.BoardUpdateEvent;
 import event.Event;
 import javafx.application.Application;
 import javafx.fxml.FXMLLoader;
 import javafx.scene.Scene;
 import javafx.scene.layout.GridPane;
 import javafx.scene.paint.Color;
 import javafx.stage.Stage;
 import util.PrintUtil;

 import java.io.IOException;
 import java.net.URISyntaxException;
 import java.util.ArrayList;

 public class Main extends Application {
     public static Board mainBoard;

     public static void main(String[] args) throws IOException, EventProtocolMismatchException, URISyntaxException {
         launch(args);


         PrintUtil.asFormatted(mainBoard);

         Event.Manager.registerListener(new BridgeListener());

//         File pyFile = new File("Main.py").getCanonicalFile();
//         Runtime.getRuntime().exec("python /c start python " + pyFile.getAbsolutePath());
//         Runtime.getRuntime().exec("cmd /k");

//         ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/K", "Start", "python", Main.class.getResource
//         ("/Main.py")));

//         String absolutePath = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI())
//         .getAbsolutePath();


//         File pyMain = new File("./Main.py");

//         ProcessBuilder builder = new ProcessBuilder("python", pyMain.getAbsolutePath());

//         Process process = builder.start();


//         Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//             System.out.println("Calling shutdown hook...");
//             process.destroy();
//         }));

         Bridge.open();

         new BoardUpdateProtocol("boardupdate", "");

         Bridge.send(ProtocolManager.encodeFor(new BoardUpdateEvent(mainBoard, mainBoard)));
     }

     //JavaFX does not support JavaFX without this method.
     public static String toRGBString(Color color) {
         return String.format("rgba(%d, %d, %d, %f)",
                 ((int) (255 * color.getRed())),
                 ((int) (255 * color.getGreen())),
                 ((int) (255 * color.getBlue())),
                 color.getOpacity());
     }

     @Override
     public void start(Stage primaryStage) throws IOException, InterruptedException {
         FXMLLoader loader = new FXMLLoader(getClass().getResource("/Main.fxml"));
         Scene s = new Scene(loader.load());

         primaryStage.setScene(s);
         primaryStage.setResizable(false);
         primaryStage.setHeight(800);
         primaryStage.setWidth(800);
         primaryStage.setTitle("Checkers");
         primaryStage.show();

         GridPane gridPane = (GridPane) s.lookup("#gridPane");
         gridPane.setGridLinesVisible(true);

         mainBoard = new Board.Builder()
                 .setEvenTilesColour(Color.HOTPINK)
                 .setOddTilesColour(Color.CORNFLOWERBLUE)
                 .build(new ArrayList<>(gridPane.getChildren()));


         mainBoard.init();
         mainBoard.getRow(3).forEach(Tile::delete);
         mainBoard.getRow(4).forEach(Tile::delete);
         mainBoard.getPieceAtIndex(3, 3).init();

         try {
             mainBoard.getManager().makeMove(mainBoard.getPieceAtIndex(3, 3).getPiece(), 4, 4);

             Thread.sleep(2000);

             mainBoard.getManager().makeMove(mainBoard.getPieceAtIndex(5, 5).getPiece(), 3, 3);
         } catch (RuntimeException exception) {
             exception.printStackTrace();
         }

         System.out.println(mainBoard);
         System.out.println(Player.Defaults.HUMAN.getPlayer().getCapturedPieces());
         System.out.println(Player.Defaults.COMPUTER.getPlayer().getCapturedPieces());

     }
 }
