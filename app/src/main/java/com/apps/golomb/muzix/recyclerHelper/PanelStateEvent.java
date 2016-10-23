package com.apps.golomb.muzix.recyclerHelper;

/**
 * Created by tomer on 15/10/2016.
 * This class represent a drag event on an item.
 */

public class PanelStateEvent {
    private final Event event;

    private PanelStateEvent(Event event) {
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }

    public enum Event{
        START,END
    }

    private static PanelStateEvent startInstance = new PanelStateEvent(Event.START);
    private static PanelStateEvent endInstance = new PanelStateEvent(Event.END);

    public static PanelStateEvent getInstance(Event event){
        if(event.equals(Event.START)){
            return startInstance;
        }
        return endInstance;
    }
}
