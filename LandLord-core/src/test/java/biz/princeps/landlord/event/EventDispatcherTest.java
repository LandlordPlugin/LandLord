package biz.princeps.landlord.event;

import biz.princeps.landlord.api.event.EventDispatcher;
import biz.princeps.landlord.api.event.EventOptions;
import biz.princeps.landlord.api.event.EventPriority;
import biz.princeps.landlord.api.event.LandLordEvent;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Consumer;

import static biz.princeps.landlord.api.event.EventOptions.forPriority;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EventDispatcherTest {

  @Test
  void testPriorities() {
    // arrange 1
    List<EventOptions> options = new ArrayList<>();
    for (EventPriority value : EventPriority.values()) {
      options.add(forPriority(value));
    }
    class CustomEvent implements LandLordEvent {
      final List<EventPriority> list = new ArrayList<>();
    }
    // act 1: add listeners in priority order
    EventDispatcher dispatcher = new SimpleEventDispatcher();
    for (EventOptions option : options) {
      dispatcher.registerListener(option, CustomEvent.class, customEvent -> customEvent.list.add(option.priority()));
    }
    CustomEvent event = dispatcher.fire(new CustomEvent());
    // assert 1: result order is equal to the order of the priorities
    assertEquals(List.copyOf(EnumSet.allOf(EventPriority.class)), event.list);

    // act 2: add listeners in reversed priority order
    dispatcher = new SimpleEventDispatcher();
    Collections.reverse(options);
    for (EventOptions option : options) {
      dispatcher.registerListener(option, CustomEvent.class, customEvent -> customEvent.list.add(option.priority()));
    }
    event = dispatcher.fire(new CustomEvent());
    // assert 2: result order is equal to the order of the priorities
    assertEquals(List.copyOf(EnumSet.allOf(EventPriority.class)), event.list);
  }

  @Test
  void testMultipleListenersOnSamePriority() {
    // arrange
    class EventConsumer implements Consumer<EmptyEvent> {
      boolean called = false;
      final static List<EventConsumer> instances = new ArrayList<>();
      EventConsumer() {
        instances.add(this);
      }
      @Override
      public void accept(EmptyEvent customEvent) {
        called = true;
      }
    }
    EventDispatcher dispatcher = new SimpleEventDispatcher();
    dispatcher.registerListener(EmptyEvent.class, new EventConsumer());
    dispatcher.registerListener(EmptyEvent.class, new EventConsumer());
    dispatcher.registerListener(EmptyEvent.class, new EventConsumer());
    // act
    dispatcher.fire(new EmptyEvent());
    // assert
    assertEquals(3, EventConsumer.instances.size());
    for (EventConsumer instance : EventConsumer.instances) {
      assertTrue(instance.called);
    }
  }

  @Test
  void testFireNullEvent() {
    // arrange
    EventDispatcher dispatcher = new SimpleEventDispatcher();
    // act & assert
    assertThrows(NullPointerException.class, () -> dispatcher.fire(null));
  }

  @Test
  void testRegisterNullListener() {
    // arrange
    EventDispatcher dispatcher = new SimpleEventDispatcher();
    // act & assert
    assertThrows(NullPointerException.class, () -> dispatcher.registerListener(EmptyEvent.class, null));
  }

  @Test
  void testRegisterNullEventType() {
    // arrange
    EventDispatcher dispatcher = new SimpleEventDispatcher();
    // act & assert
    assertThrows(NullPointerException.class, () -> dispatcher.registerListener(null, event -> {}));
  }

  @Test
  void testFireReturnsSameInstance() {
    // arrange
    EventDispatcher dispatcher = new SimpleEventDispatcher();
    EmptyEvent event = new EmptyEvent();
    // act & assert
    assertSame(event, dispatcher.fire(event));
  }

  static class EmptyEvent implements LandLordEvent {

  }
}
