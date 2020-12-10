/*
 * Copyright (c) Jake Dean, 2020.
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package comms;

import comms.protocol.ProtocolManager;
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
import java.util.stream.Collectors;


/**
 * A socket structure for cross-communication to and from the Python backend. This system uses a series of
 * {@link comms.protocol.Protocol} instances to manage the formal encoding and decoding process of messages. Each
 * Protocol implementation distributes encoded and decoded {@link comms.MessageContainer.Message} instances from and
 * to {@link Event} instances, respectively.
 * <p>
 * This class is responsible for processing Protocol instances, ensuring their delivery across the network to the
 * python backend. Furthermore, {@link ServerSocket} instances serve to listen for incoming traffic. When new traffic
 * is received, the appropriate Protocol is deciphered by analysing the Message's headers in the
 * {@link ProtocolManager}.
 * <p>
 * {@link comms.MessageContainer.Message} instances to be sent over the network are submitted to this class, and
 * placed in to a queue. This queue is emptied when it reaches a given capacity - emptying simply meaning that an
 * attempt is made to transfer them across the network.
 *
 * @see comms.protocol.Protocol
 * @see ProtocolManager
 * @see comms.MessageContainer.Message
 * @see MessageContainer
 * @see Event
 */
public class Bridge {
    /**
     * The queue of encoded {@link comms.MessageContainer.Message} instances awaiting to be transmitted over the
     * network.
     */
    private static final ArrayList<MessageContainer.Message> queue = new ArrayList<>();
    /**
     * The 'server' port, used for receiving inbound network traffic.
     */
    private static int inboundPort = 5001; //Default
    /**
     * The 'client' port, used for sending outbound network traffic.
     */
    private static int outboundPort = 5000; //Default
    /**
     *
     */
    private static String address = "127.0.0.1"; //Default
    /**
     * The millisecond delay period between refreshing inbound connections for new messages. This prevents the Thread
     * being overworked unnecessarily, although induces a slight delay in receiving messages. By default, this is set
     * to 100ms, or 1/10th of a second.
     */
    private static int refreshTimer = 100;
    /**
     * The threshold that the {@link #queue} size must meet or exceed before it is flushed to the outbound socket.
     */
    private static int queueThreshold = 1;
    /*How many entries in the queue must be present before transmitting as many as possible. In CONTINUOUS mode, this
     will act as a 'burst' threshold. In PONG mode, this will simply act as a standard FIFO queue. */
    private static ServerSocket inboundSocket; //The 'server', receiving Messages

    /**
     * The {@link Mode} used to transmit {@link comms.MessageContainer.Message} instances.
     *
     * @see Mode
     */
    private static Mode transferMode = Mode.CONTINUOUS;

    /**
     * @return Int - The {@link #queueThreshold} threshold value.
     */
    public static int getQueueThreshold() {
        return queueThreshold;
    }

    public static void setQueueThreshold(int queueThreshold) {
        Bridge.queueThreshold = queueThreshold;
    }

    /**
     * Launches the server instance and begins listening for inbound traffic on localhost, using the given inbound port.
     *
     * @throws IOException - If the {@link ServerSocket} could not launch successfully. This could be caused by
     *                     numerous issues, such as being unable to bind to the {@link #inboundPort}.
     * @see InetAddress
     * @see #inboundPort
     */
    public static void open() throws IOException {
        inboundSocket = new ServerSocket(inboundPort, 0, InetAddress.getByName(address));
        beginListening();
    }

    public static int getRefreshTimer() {
        return refreshTimer;
    }

    public static void setRefreshTimer(int refreshTimer) {
        Bridge.refreshTimer = refreshTimer;
    }

    public static int getInboundPort() {
        return inboundPort;
    }

    public static void setInboundPort(int inboundPort) {
        Bridge.inboundPort = inboundPort;
    }

    public static int getOutboundPort() {
        return outboundPort;
    }

    public static void setOutboundPort(int outboundPort) {
        Bridge.outboundPort = outboundPort;
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


    /**
     * Terminates the server instance... terminates listening for any incoming traffic.
     *
     * @throws IOException If the {@link #inboundSocket} could not be closed.
     */
    public static void close() throws IOException {
        if (inboundSocket != null) {
            inboundSocket.close();
        }
    }

    /**
     * This method is used when intending to send a new {@link comms.MessageContainer.Message} across the Bridge. The
     * Message is appended to the {@link #queue}.
     *
     * @param message {@link comms.MessageContainer.Message} - The Message instance to be sent.
     *
     * @throws IOException Thrown if the Bridge is not currently initialised/not open.
     */
    public static void send(MessageContainer.Message message) throws IOException {
        if (!isOpen()) throw new BridgeClosedException("The Bridge has not been opened!");

        queue.add(message);
        checkQueue();
    }

    /**
     * Checks the {@link #queue} to see if its size exceeds the {@link #queueThreshold}. If so, the chosen
     * transmission {@link Mode} is executed.
     *
     * @throws IOException Thrown if there was an issue in opening the outbound {@link Socket}.
     * @see Socket
     */
    private static void checkQueue() throws IOException {
        if (queue.size() >= queueThreshold) {

            Socket out = new Socket(address, outboundPort);
            PrintWriter writer = new PrintWriter(out.getOutputStream(), true);

            for (MessageContainer.Message message : queue) {
                System.out.println("Sending Message: [" + message.getId() + "] " + message.getMessage());
                writer.write(message.getMessage());
                writer.flush();

                Event.Manager.fire(new BridgeMessageSendEvent(message));
            }

            queue.clear();
            writer.close();
            out.close();
        }
    }

    /**
     * @return Boolean - If the {@link #inboundSocket} is not null; if the inboundSocket is instantiated.
     */
    public static boolean isOpen() {
        return inboundSocket != null;
    }

    /**
     * This method handles the intermittent reading of inbound network traffic. Once a message is received, its
     * relevant {@link comms.protocol.Protocol} is determined by the {@link ProtocolManager}. Afterwards, the
     * Protocol decodes this {@link comms.MessageContainer.Message} in to an {@link Event} instance, which is
     * redistributed via the {@link Event.Manager}.
     *
     * @see Event
     * @see Event.Manager
     * @see comms.protocol.Protocol
     * @see ProtocolManager
     * @see comms.MessageContainer.Message
     */
    private static void beginListening() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            if (!isOpen()) return;

            try {
                System.out.println("Began listening...");
                Socket accept = inboundSocket.accept();
                System.out.println("Received Message @" + System.currentTimeMillis());

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(accept.getInputStream()));
                String message = bufferedReader.lines().collect(Collectors.joining());
                System.out.println("Received new Bridge Message: " + message);

                ProtocolManager.decodeFor(new MessageContainer.Message(message));
                Event.Manager.fire(new BridgeMessageReceiveEvent(new MessageContainer.Message(message)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, 0, refreshTimer, TimeUnit.MILLISECONDS);

    }

    /**
     * The {@link Mode} used to transmit {@link comms.MessageContainer.Message} instances. The two modes are
     * CONTINUOUS and PONG:
     * <p>
     * CONTINUOUS:
     * The queue is emptied in full once the threshold is met. This mode is a 'floodgate' of sorts.
     * <p>
     * PONG:
     * The queue will only send one {@link comms.MessageContainer.Message} once the threshold is reached. After this,
     * the system waits until a response is received. This process repeats until the queue threshold is not met or
     * the queue is empty.
     */
    public enum Mode {
        CONTINUOUS, //Allows for continuously sending packets
        PONG //Must receive a response for sending the next packet //TODO impl.
    }
}
