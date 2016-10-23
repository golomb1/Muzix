package com.apps.golomb.muzix.recyclerHelper;

/**
 * Created by tomer on 15/10/2016.
 * This class represent a drag event on an item.
 */

public class DragItemEvent {
    private final Event event;

    private DragItemEvent(Event event) {
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }

    public enum Event{
        START,END
    }

    private static DragItemEvent startInstance = new DragItemEvent(Event.START);
    private static DragItemEvent endInstance = new DragItemEvent(Event.END);

    public static DragItemEvent getInstance(Event event){
        if(event.equals(Event.START)){
            return startInstance;
        }
        return endInstance;
    }
}
