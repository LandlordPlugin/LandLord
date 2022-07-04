package biz.princeps.landlord.api.event;

import java.util.function.Consumer;

/**
 * The {@link EventDispatcher} provides methods to manage listeners and fire events.
 */
public interface EventDispatcher {

  default <E extends LandLordEvent> ListenerKey registerListener(
      Class<? extends E> eventType,
      Consumer<? super E> listener
  ) {
    return registerListener(EventOptions.defaultOptions(), eventType, listener);
  }

  <E extends LandLordEvent> ListenerKey registerListener(
      EventOptions options,
      Class<? extends E> eventType,
      Consumer<? super E> listener
  );

  boolean unregisterListener(ListenerKey key);

  <E extends LandLordEvent> E fire(E event);

}
