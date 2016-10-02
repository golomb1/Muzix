package com.apps.golomb.muzix.utils;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by tomer on 06/09/2016.
 * Util class for static functions
 */
public class Utils {
    public static String formatMillis(int millis){
        return String.format(Locale.getDefault(),"%d:%d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        );
    }
}
