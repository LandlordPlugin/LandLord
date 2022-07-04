package biz.princeps.landlord.api.event;

public enum EventPriority {
  EARLIEST,
  EARLY,
  MIDDLE, // TODO better name?
  LATE,
  LATEST,
  MONITOR
}
