package com.apps.golomb.muzix.customviews;

/**
 * Created by tomer on 11/10/2016.
 * Represent a Active / unselected event.
 */
public class ActiveEvent {
    private static ActiveEvent instance = new ActiveEvent();

    public static ActiveEvent fireEvent() {
        return instance;
    }
}
