package biz.princeps.landlord.api.event;

public sealed interface EventOptions {
  static EventOptions forPriority(EventPriority priority) {
    return new EventOptionsImpl(priority);
  }

  static EventOptions defaultOptions() {
    return EventOptionsImpl.DEFAULT;
  }

  EventPriority priority();
}
record EventOptionsImpl(
    EventPriority priority
) implements EventOptions {
  static final EventOptions DEFAULT = EventOptions.forPriority(EventPriority.MIDDLE);
}
