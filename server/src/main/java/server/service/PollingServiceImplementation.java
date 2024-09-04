package server.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Service
public class PollingServiceImplementation<T> implements PollingService<T> {

    private final Map<Object, Consumer<T>> listeners = new HashMap<>();

    /**
     * Adds a listener to the known listeners.
     *
     * @param listener Listener.
     * @return Key of the added listener.
     */
    @Override
    public Object addListener(Consumer<T> listener) {
        var key = new Object(); // Objects are never equal to each other!
        listeners.put(key, listener);
        return key;
    }

    /**
     * Removes a listener from the known listeners.
     *
     * @param key Key of the listener.
     */
    @Override
    public void removeListener(Object key) {
        listeners.remove(key);
    }

    /**
     * Sends data to the known listeners.
     *
     * @param data Data.
     */
    @Override
    public void sendToListeners(T data) {
        listeners.forEach((key, listener) -> listener.accept(data));
    }
}
