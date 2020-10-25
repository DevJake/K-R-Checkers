/*
 * Copyright (c) Jake Dean, 2020.
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package comms;

import err.BridgeClosedException;
import event.BridgeMessageReceiveEvent;
import event.BridgeMessageSendEvent;
import event.Event;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * A socket structure for cross-communication to and from the Python backend. TODO
 */
public class Bridge {
    private static final ArrayList<Message> queue = new ArrayList<>();
    private static int port = 5000; //Default
    private static String address = "localhost"; //Default
    private static int refreshTimer = 100; //Delay period between refreshing inbound connections for new Messages
    private static int queueThreshold = 1;
    /*How many entries in the queue must be present before transmitting as many as possible. In CONTINUOUS mode, this
     will act as a 'burst' threshold. In PONG mode, this will simply act as a standard FIFO queue. */
    private static Socket outboundSocket; //The 'client', sending Messages out
    private static ServerSocket inboundSocket; //The 'server', receiving Messages

    private static Mode transferMode = Mode.CONTINUOUS;

    public static int getQueueThreshold() {
        return queueThreshold;
    }

    public static void setQueueThreshold(int queueThreshold) {
        Bridge.queueThreshold = queueThreshold;
    }

    public static void setRefreshTimer(int refreshTimer) {
        Bridge.refreshTimer = refreshTimer;
    }

    /*
        Launch the server instance, and begin listening
         */
    public static void open() throws IOException {
        inboundSocket = new ServerSocket(port);
        outboundSocket = new Socket(InetAddress.getLocalHost(), 5000);
        beginListening();
    }

    public static int getPort() {
        return port;
    }

    public static void setPort(int port) {
        Bridge.port = port;
    }

    public static String getAddress() {
        return address;
    }

    public static void setAddress(String address) {
        Bridge.address = address;
    }

    public static Mode getTransferMode() {
        return transferMode;
    }

    public static void setTransferMode(Mode transferMode) {
        Bridge.transferMode = transferMode;
    }

    /*
            Terminate the server instance; terminate listening for incoming traffic
             */
    public static void close() throws IOException {
        if (outboundSocket != null) {
            outboundSocket.close();
        }
    }

    public static void send(Message message) throws IOException {
        if (!isOpen()) throw new BridgeClosedException("The Bridge has not been opened!");

        queue.add(message);
        checkQueue();
    }

    private static void checkQueue() throws IOException {
        if (queue.size() >= queueThreshold) {
            PrintWriter out = new PrintWriter(outboundSocket.getOutputStream(), true);

            for (Message message : queue) {
                out.write(message.getMessage());
                out.flush();

                Event.Manager.fire(new BridgeMessageSendEvent(message));
            }

            queue.clear();
            out.close();
        }
    }

    public static boolean isOpen() {
        return outboundSocket != null && inboundSocket != null;
    }

    private static void beginListening() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            if (!isOpen()) return;

            try {
                Socket accept = inboundSocket.accept();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(accept.getInputStream()));
                Event.Manager.fire(
                        new BridgeMessageReceiveEvent(
                                new MessageEncoder(bufferedReader.readLine())
                                        .setState(Message.State.INBOUND)
                                        .encode()));
                //TODO add MessageDecoder class; remove #setState method
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, 0, refreshTimer, TimeUnit.MILLISECONDS);

//        while (true) {
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(accept.getInputStream()));
//            System.out.println(bufferedReader.readLine());
//            accept = x.accept();
//        }
    }

    public enum Mode {
        CONTINUOUS, //Allows for continuously sending packets
        PONG //Must receive a response for sending the next packet //TODO impl.
    }

//    /*
//    Decodes information from inbound transfers.
//     */
//    public class Decoder {
//        public class DecodedMessage {
//            private final String message;
//
//            private DecodedMessage(String message) {
//                this.message = message;
//            }
//
//            public String getMessage() {
//                return message;
//            }
//        }
//    }
}
