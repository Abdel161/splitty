package server.service;

import java.util.function.Consumer;

public interface PollingService<T> {

    /**
     * Adds a listener to the known listeners.
     *
     * @param listener Listener.
     * @return Key of the added listener.
     */
    Object addListener(Consumer<T> listener);

    /**
     * Removes a listener from the known listeners.
     *
     * @param key Key of the listener.
     */
    void removeListener(Object key);

    /**
     * Sends data to the known listeners.
     *
     * @param data Data.
     */
    void sendToListeners(T data);
}
