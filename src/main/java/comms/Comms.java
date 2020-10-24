/*
 * Copyright (c) Jake Dean, 2020.
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package comms;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;

/*
 * A class for cross-communication between the Java front-end and the Python backend.
 */
public class Comms {
    private static final ArrayList<String> queue = new ArrayList<>();
    private static final int port = 8000;
    private static final String address = "127.0.0.1";
    private static int queueThreshold = 1;
    /*How many entries in the queue must be present before transmitting as many as possible. In CONTINUOUS mode, this
     will act as a 'burst' threshold. In PONG mode, this will simply act as a standard FIFO queue. */

    private static ServerSocket socket;

    public static int getQueueThreshold() {
        return queueThreshold;
    }

    public static void setQueueThreshold(int queueThreshold) {
        Comms.queueThreshold = queueThreshold;
    }

    /*
        Launch the server instance, and begin listening
         */
    public static void boot() throws IOException {
        socket = new ServerSocket(port, 0, InetAddress.getByName(address));
    }

    /*
    Terminate the server instance; terminate listening for incoming traffic
     */
    public static void terminate() throws IOException {
        if (socket != null) {
            socket.close();
        }
    }

    public enum Mode {
        CONTINUOUS, //Allows for continuously sending packets
        PONG //Must receive a response for sending the next packet
    }

    /*
    Encodes information for outbound transfers.
     */
    public class Encoder {

    }

    /*
    Decodes information from inbound transfers.
     */
    public class Decoder {

    }
}
