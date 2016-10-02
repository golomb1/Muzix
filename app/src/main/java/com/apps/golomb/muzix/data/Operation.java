package com.apps.golomb.muzix.data;

/**
 * Created by tomer on 06/09/2016.
 */
public interface Operation<T1, T2, R> {
    R operate(T1 t1,T2 t2);
}
