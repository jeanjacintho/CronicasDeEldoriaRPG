package rpgx.events;

import java.util.ArrayList;
import java.util.List;

public class EventBus {
    private final List<EventListener> listeners = new ArrayList<>();
    public void subscribe(EventListener l) { listeners.add(l); }
    public void publish(GameEvent e) { for (var l : listeners) l.onEvent(e); }
}
