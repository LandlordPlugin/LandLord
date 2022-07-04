package biz.princeps.landlord.event;

import biz.princeps.landlord.api.event.EventDispatcher;
import biz.princeps.landlord.api.event.EventOptions;
import biz.princeps.landlord.api.event.LandLordEvent;
import biz.princeps.landlord.api.event.ListenerKey;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SimpleEventDispatcher implements EventDispatcher {
  private final Map<Class<?>, List<ListenerEntry>> listeners = new IdentityHashMap<>();
  @Override
  public <E extends LandLordEvent> ListenerKey registerListener(
      EventOptions options,
      Class<? extends E> eventType,
      Consumer<? super E> listener
  ) {
    // TODO verify eventType:
    //  - must have the correct superinterface
    //  - figure out if we can avoid the unchecked cast (doubt)
    @SuppressWarnings("unchecked")
    ListenerEntry entry = new ListenerEntry(options, eventType, (Consumer<? super LandLordEvent>) listener);
    List<ListenerEntry> entries = listeners.computeIfAbsent(eventType, c -> new ArrayList<>());
    entries.add(entry);
    entries.sort(Comparator.comparing(ListenerEntry::options, Comparator.comparing(EventOptions::priority)));
    return entry;
  }

  @Override
  public boolean unregisterListener(ListenerKey key) {
    if (!(key instanceof ListenerEntry entry)) {
      return false;
    }
    // assuming entry was created and added to the list in #registerListener
    return this.listeners.get(entry.eventType()).remove(entry);
  }

  @Override
  public <E extends LandLordEvent> E fire(E event) {
    Class<? extends LandLordEvent> eventType = event.getClass();
    for (ListenerEntry entry : this.listeners.getOrDefault(eventType, List.of())) {
      try {
        entry.listener().accept(event);
      } catch (ThreadDeath e) {
        throw e;
      } catch (Throwable t) {
        // TODO logging
      }
    }
    return event;
  }

  private record ListenerEntry(
      EventOptions options,
      Class<? extends LandLordEvent> eventType,
      Consumer<? super LandLordEvent> listener
  ) implements ListenerKey {

  }

}
