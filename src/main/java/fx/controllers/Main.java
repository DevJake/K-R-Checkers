 /*
  * Copyright (c) Jake Dean, 2020.
  *
  * This work is licensed under the Creative Commons
  * Attribution-NonCommercial-NoDerivatives 4.0 International License.
  * To view a copy of this license, visit http://creativecommons
  * .org/licenses/by-nc-nd/4.0/ or send a letter to
  * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
  */

 package fx.controllers;

 import comms.Bridge;
 import comms.BridgeListener;
 import comms.protocol.AIMakeMoveProtocol;
 import comms.protocol.BoardUpdateProtocol;
 import comms.protocol.PlayerMakeMoveProtocol;
 import comms.protocol.ProtocolManager;
 import ent.Board;
 import ent.GameManager;
 import ent.Player;
 import err.EventProtocolMismatchException;
 import event.Event;
 import javafx.application.Application;
 import javafx.fxml.FXMLLoader;
 import javafx.scene.Scene;
 import javafx.scene.control.Label;
 import javafx.scene.layout.GridPane;
 import javafx.scene.layout.Pane;
 import javafx.scene.paint.Color;
 import javafx.stage.Stage;

 import java.io.File;
 import java.io.IOException;
 import java.net.URISyntaxException;
 import java.util.ArrayList;
 import java.util.Arrays;

 /**
  * The Main class. As with all JavaFX applications, this is required, as it's responsible for constructing and
  * displaying the GUI.
  */
 public class Main extends Application {
     public static Board mainBoard;
     public static GameManager gameManager;
     private static Label humanScore;
     private static Label computerScore;

     public static Label getHumanScore() {
         return humanScore;
     }

     public static void setHumanScore(int newScore) {
         Main.humanScore.setText(String.valueOf(newScore));
     }

     public static Label getComputerScore() {
         return computerScore;
     }

     public static void setComputerScore(int newScore) {
         Main.computerScore.setText(String.valueOf(newScore));
     }

     public static void main(String[] args) throws IOException, EventProtocolMismatchException, URISyntaxException {
//         PrintUtil.asFormatted(mainBoard);

         ProtocolManager.registerProtocol(new AIMakeMoveProtocol());
         ProtocolManager.registerProtocol(new BoardUpdateProtocol());
         ProtocolManager.registerProtocol(new PlayerMakeMoveProtocol());

         Event.Manager.registerListener(new BridgeListener());

         new Thread(() -> {
             try {
                 bootPyServer();
                 Bridge.open();
             } catch (IOException | URISyntaxException e) {
                 e.printStackTrace();
             }

         }).start();


         launch(args);

//         new BoardUpdateProtocol("boardupdate", "");
//
//         Bridge.send(ProtocolManager.encodeFor(new BoardUpdateEvent(mainBoard, mainBoard)));

//         bootPyServer();
     }

     //JavaFX does not support JavaFX without this method.

     /**
      * String-formats JavaFX Color classes in to strings that can be passed
      * in to JavaFX Node instances.
      *
      * @param color {@link Color} - The Color class to be converted.
      *
      * @return {@link String} - The formatted string representation of the Color
      */
     public static String toRGBString(Color color) {
         return String.format("rgba(%d, %d, %d, %f)",
                 ((int) (255 * color.getRed())),
                 ((int) (255 * color.getGreen())),
                 ((int) (255 * color.getBlue())),
                 color.getOpacity());
     }

     /**
      * Boots the python server. This method is complex because the python server files are shipped *inside* the fat
      * jar. This results in them being very difficult to execute.
      * <p>
      * The work around to painfully extracting then executing them is to launch a new cmd prompt and pass in their
      * location... which can be difficult to get, as their location is not static.
      */
     public static void bootPyServer() throws URISyntaxException, IOException {
         String jarPath =
                 new File(System.getProperty("java.class.path")).getAbsoluteFile().getParentFile().getAbsolutePath();

//         System.out.println(jarPath);

         ProcessBuilder builder = new ProcessBuilder("cmd", "/k", "Start", "cmd", "/k", "python", jarPath + "/python" +
                 "/Boot.py");
//
         Process process = builder.start();
         System.out.println("Booted the python server!");


         Runtime.getRuntime().addShutdownHook(new Thread(() -> {
             System.out.println("Calling shutdown hook...");
             process.destroy();
         }));
     }

     /**
      * Starts the GUI.
      * <p>
      * This method is also used to perform essential game setup code, like constructing the board, removing rows of
      * tiles that need to be removed, and other configuration settings.
      *
      * @param primaryStage The Stage to be displayed.
      *
      * @throws IOException Throws if the FXMLLoader fails to load the fxml file describing the Scene for the Stage.
      */
     @Override
     public void start(Stage primaryStage) throws Exception {
         FXMLLoader loader = new FXMLLoader(getClass().getResource("/Main" +
                 ".fxml"));
         Scene s = new Scene(loader.load());

         primaryStage.setScene(s);
         primaryStage.setResizable(true);
         primaryStage.setHeight(800);
         primaryStage.setWidth(800);
         primaryStage.setTitle("Checkers");
         primaryStage.setMinWidth(200);
         primaryStage.setMinHeight(200);
         primaryStage.setMaxWidth(1080);
         primaryStage.setMaxHeight(1080);
         primaryStage.minHeightProperty().bind(primaryStage.widthProperty().multiply(1));
         primaryStage.maxHeightProperty().bind(primaryStage.widthProperty().multiply(1));

         primaryStage.show();

         Pane canvas = (Pane) s.lookup("#canvas");
         GridPane gridPane = (GridPane) s.lookup("#gridPane");
         gridPane.setGridLinesVisible(true);


         Menu menu = new Menu();
         menu.start(new Stage());


//         humanScore = (Label) s.lookup("#playerScore");
//         computerScore = (Label) s.lookup("#computerScore");


         Player.Defaults.HUMAN.getPlayer().setColour(Color.rgb(255, 204, 223));
         Player.Defaults.COMPUTER.getPlayer().setColour(Color.rgb(255, 0, 128));

         mainBoard = new Board.Builder()
                 .setEvenTilesColour(Color.rgb(150, 150, 150))
                 .setOddTilesColour(Color.rgb(245, 175, 200))
                 .setShowLabels(true)
                 .build(new ArrayList<>(gridPane.getChildren()));


         mainBoard.init(canvas);
         mainBoard.getRow(3).forEach(it -> {
             it.getPiece().setPlayer(null);
             it.deleteOccupyingPiece(mainBoard.isShowLabels());
         });
         mainBoard.getRow(4).forEach(it -> {
             it.getPiece().setPlayer(null);
             it.deleteOccupyingPiece(Main.mainBoard.isShowLabels());
         });

//         mainBoard.getTileAtIndex(2, 4).setPiece(new Piece(2, 4, Color.ORANGE, Player.Defaults.COMPUTER.getPlayer(),
//                 Piece.Type.KING));
//         mainBoard.getTileAtIndex(2, 4).init(); //How to add your own custom piece

         try {
             //This is an example of manually moving pieces around the board
             // and simulating a capture
//             mainBoard.getManager().makeMove(mainBoard.getTileAtIndex(3, 3).getPiece(), 4, 4);
//             mainBoard.getManager().makeMove(mainBoard.getTileAtIndex(5, 5).getPiece(), 3, 3);
//             mainBoard.getManager().makeMove(mainBoard.getTileAtIndex(0, 2).getPiece(), 1, 3);
//             mainBoard.getManager().makeMove(mainBoard.getTileAtIndex(3, 5).getPiece(), 2, 4);
         } catch (RuntimeException exception) {
             exception.printStackTrace();
         }

         gameManager = new GameManager(mainBoard, Arrays.asList(new Player[]{Player.Defaults.HUMAN.getPlayer(),
                 Player.Defaults.COMPUTER.getPlayer()}.clone()), canvas);

         gameManager.beginGame();


//         System.out.println(mainBoard);
         //System.out.println(Player.Defaults.HUMAN.getPlayer().getCapturedPieces());
         //System.out.println(Player.Defaults.COMPUTER.getPlayer().getCapturedPieces());

//         mainBoard.renderAllLabels();

         //System.out.println(mainBoard.getTiles().get(1));

     }
 }
