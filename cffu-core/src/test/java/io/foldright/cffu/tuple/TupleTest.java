package io.foldright.cffu.tuple;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


@SuppressWarnings({"AssertBetweenInconvertibleTypes", "EqualsWithItself", "SimplifiableAssertion", "ConstantValue", "EqualsBetweenInconvertibleTypes"})
public class TupleTest {
    int e1 = 1;
    String e2 = "2";
    double e3 = 3.14;
    int e4 = 4;
    char e5 = '5';

    String value = "42";

    @Test
    void tuple2() {
        final Tuple2<Integer, String> tuple = Tuple2.of(e1, e2);
        assertEquals(e1, tuple._1);
        assertEquals(e2, tuple._2);

        assertEquals(Tuple2.of(e1, e2), tuple);
        assertTrue(tuple.equals(tuple));
        assertNotEquals(Tuple2.of(e1 + 1, e2), tuple);
        assertNotEquals(Tuple2.of(e1, e2 + "X"), tuple);

        assertFalse(tuple.equals("foo"));
        assertFalse(tuple.equals(null));

        assertEquals("Tuple2(1, 2)", tuple.toString());

        Map<Tuple2<Integer, String>, String> map = new HashMap<>();
        map.put(tuple, value);
        assertEquals(value, map.get(tuple));
    }

    @Test
    void tuple3() {
        final Tuple3<Integer, String, Double> tuple = Tuple3.of(e1, e2, e3);
        assertEquals(e1, tuple._1);
        assertEquals(e2, tuple._2);
        assertEquals(e3, tuple._3);

        assertEquals(Tuple3.of(e1, e2, e3), tuple);
        assertTrue(tuple.equals(tuple));
        assertNotEquals(Tuple3.of(e1 + 1, e2, e3), tuple);
        assertNotEquals(Tuple3.of(e1, e2 + "X", e3), tuple);
        assertNotEquals(Tuple3.of(e1, e2, e3 + 1), tuple);

        assertFalse(tuple.equals("foo"));
        assertFalse(tuple.equals(null));

        assertEquals("Tuple3(1, 2, 3.14)", tuple.toString());

        Map<Tuple3<Integer, String, Double>, String> map = new HashMap<>();
        map.put(tuple, value);
        assertEquals(value, map.get(tuple));
    }

    @Test
    void tuple4() {
        final Tuple4<Integer, String, Double, Integer> tuple = Tuple4.of(e1, e2, e3, e4);
        assertEquals(e1, tuple._1);
        assertEquals(e2, tuple._2);
        assertEquals(e3, tuple._3);
        assertEquals(e4, tuple._4);

        assertEquals(Tuple4.of(e1, e2, e3, e4), tuple);
        assertTrue(tuple.equals(tuple));
        assertNotEquals(Tuple4.of(e1 + 1, e2, e3, e4), tuple);
        assertNotEquals(Tuple4.of(e1, e2 + "X", e3, e4), tuple);
        assertNotEquals(Tuple4.of(e1, e2, e3 + 1, e4), tuple);
        assertNotEquals(Tuple4.of(e1, e2, e3, e4 + 1), tuple);

        assertFalse(tuple.equals("foo"));
        assertFalse(tuple.equals(null));

        assertEquals("Tuple4(1, 2, 3.14, 4)", tuple.toString());

        Map<Tuple4<Integer, String, Double, Integer>, String> map = new HashMap<>();
        map.put(tuple, value);
        assertEquals(value, map.get(tuple));
    }

    @Test
    void tuple5() {
        final Tuple5<Integer, String, Double, Integer, Character> tuple = Tuple5.of(e1, e2, e3, e4, e5);
        assertEquals(e1, tuple._1);
        assertEquals(e2, tuple._2);
        assertEquals(e3, tuple._3);
        assertEquals(e4, tuple._4);
        assertEquals(e5, tuple._5);

        assertEquals(Tuple5.of(e1, e2, e3, e4, e5), tuple);
        assertTrue(tuple.equals(tuple));
        assertNotEquals(Tuple5.of(e1 + 1, e2, e3, e4, e5), tuple);
        assertNotEquals(Tuple5.of(e1, e2 + "X", e3, e4, e5), tuple);
        assertNotEquals(Tuple5.of(e1, e2, e3 + 1, e4, e5), tuple);
        assertNotEquals(Tuple5.of(e1, e2, e3, e4 + 1, e5), tuple);
        assertNotEquals(Tuple5.of(e1, e2, e3, e4, '6'), tuple);

        assertFalse(tuple.equals("foo"));
        assertFalse(tuple.equals(null));

        assertEquals("Tuple5(1, 2, 3.14, 4, 5)", tuple.toString());

        Map<Tuple5<Integer, String, Double, Integer, Character>, String> map = new HashMap<>();
        map.put(tuple, value);
        assertEquals(value, map.get(tuple));
    }
}
