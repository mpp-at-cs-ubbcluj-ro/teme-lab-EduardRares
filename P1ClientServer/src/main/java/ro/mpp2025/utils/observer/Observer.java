package ro.mpp2025.utils.observer;

import ro.mpp2025.utils.events.Event;

public interface Observer<E extends Event> {
    void update(E event);
}
