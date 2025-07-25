package io.foldright.cffu.internal;

import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static io.foldright.cffu.internal.CommonUtils.toArray;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;


class CommonUtilsTest {
    @Test
    void test_toArray() {
        assertNull(toArray(null, new Integer[0]));
        assertThat(toArray(asList(1, 2), new Integer[0]))
                .containsExactly(1, 2);
        assertThat(toArray(Stream.of(1, 2)::iterator, new Integer[0]))
                .containsExactly(1, 2);

        assertNull(toArray(null, Integer[]::new, (Integer x) -> x + 1));
        assertThat(toArray(asList(1, 2), Integer[]::new, x -> x + 1))
                .containsExactly(2, 3);
        assertThat(toArray(Stream.of(1, 2)::iterator, Integer[]::new, x -> x + 1))
                .containsExactly(2, 3);
    }
}
