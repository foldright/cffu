package io.foldright.cffu.tuple;

import java.util.Objects;


/**
 * Tuple4, contains 4 elements.
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 */
public final class Tuple4<T1, T2, T3, T4> {
    public final T1 _1;
    public final T2 _2;
    public final T3 _3;
    public final T4 _4;

    public static <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4> of(T1 _1, T2 _2, T3 _3, T4 _4) {
        return new Tuple4<>(_1, _2, _3, _4);
    }

    private Tuple4(T1 _1, T2 _2, T3 _3, T4 _4) {
        this._1 = _1;
        this._2 = _2;
        this._3 = _3;
        this._4 = _4;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple4<?, ?, ?, ?> tuple4 = (Tuple4<?, ?, ?, ?>) o;
        return Objects.equals(_1, tuple4._1) && Objects.equals(_2, tuple4._2)
                && Objects.equals(_3, tuple4._3) && Objects.equals(_4, tuple4._4);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_1, _2, _3, _4);
    }

    @Override
    public String toString() {
        return "Tuple4(" + _1 + ", " + _2 + ", " + _3 + ", " + _4 + ')';
    }
}
