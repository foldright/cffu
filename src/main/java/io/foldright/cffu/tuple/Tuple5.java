package io.foldright.cffu.tuple;

import java.util.Objects;

/**
 * Tuple5, contains 5 elements.
 */
public final class Tuple5<T1, T2, T3, T4, T5> {
    public final T1 _1;
    public final T2 _2;
    public final T3 _3;
    public final T4 _4;
    public final T5 _5;

    public static <T1, T2, T3, T4, T5> Tuple5<T1, T2, T3, T4, T5> of(T1 _1, T2 _2, T3 _3, T4 _4, T5 _5) {
        return new Tuple5<>(_1, _2, _3, _4, _5);
    }

    private Tuple5(T1 _1, T2 _2, T3 _3, T4 _4, T5 _5) {
        this._1 = _1;
        this._2 = _2;
        this._3 = _3;
        this._4 = _4;
        this._5 = _5;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple5<?, ?, ?, ?, ?> tuple5 = (Tuple5<?, ?, ?, ?, ?>) o;
        return Objects.equals(_1, tuple5._1) && Objects.equals(_2, tuple5._2)
                && Objects.equals(_3, tuple5._3) && Objects.equals(_4, tuple5._4)
                && Objects.equals(_5, tuple5._5);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_1, _2, _3, _4, _5);
    }

    @Override
    public String toString() {
        return "Tuple5(" + _1 + ", " + _2 + ", " + _3 + ", " + _4 + ", " + _5 + ')';
    }
}
