package io.foldright.cffu.tuple;

import java.util.Objects;

/**
 * Tuple2, aka Pair.
 */
public final class Tuple2<T1, T2> {
    public final T1 _1;
    public final T2 _2;

    public static <T, U> Tuple2<T, U> of(T _1, U _2) {
        return new Tuple2<>(_1, _2);
    }

    private Tuple2(T1 _1, T2 _2) {
        this._1 = _1;
        this._2 = _2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple2<?, ?> tuple2 = (Tuple2<?, ?>) o;
        return Objects.equals(_1, tuple2._1) && Objects.equals(_2, tuple2._2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_1, _2);
    }

    @Override
    public String toString() {
        return "Tuple2(" + _1 + ", " + _2 + ')';
    }
}
