/*
 * Copyright (c) Jake Dean, 2020.
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package event;

/**
 * By subclassing this class, methods can be selectively overridden and provided with an implementation. This allows
 * for a dynamic structure for listening to {@link Event} instances, which are distributed via the
 * {@link Event.Manager}.
 * <p>
 * It is *essential* for a subclass to register an instance of itself with the Event Manager, so that fired events
 * can be correctly distributed.
 *
 * @see Event
 * @see Event.Manager
 * @see Event.Manager#registerListener(EventListener)
 */
public class EventListener {
    /**
     * @param event {@link BridgeMessageReceiveEvent} - The BridgeMessageReceiveEvent instance.
     */
    public void onBridgeMessageReceived(BridgeMessageReceiveEvent event) {
    }

    /**
     * @param event {@link BridgeMessageSendEvent} - The BridgeMessageSendEvent instance.
     */
    public void onBridgeMessageSend(BridgeMessageSendEvent event) {
    }

    /**
     * @param event {@link PlayerMakeMoveEvent} - The PlayerMakeMoveEvent instance.
     */
    public void onPlayerMakeMove(PlayerMakeMoveEvent event) {
    }

    /**
     * @param event {@link AIPlayMoveEvent} - The AIPlayMoveEvent instance.
     */
    public void onAIPlayMove(AIPlayMoveEvent event) {
    }

    /**
     * @param event {@link BoardUpdateEvent} - The BoardUpdateEvent instance.
     */
    public void onBoardUpdate(BoardUpdateEvent event) {
    }
}
