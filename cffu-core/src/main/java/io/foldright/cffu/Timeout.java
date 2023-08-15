package io.foldright.cffu;

import edu.umd.cs.findbugs.annotations.NonNull;

import javax.annotation.concurrent.Immutable;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


@Immutable
class Timeout {
    private final long timeout;
    @NonNull
    private final TimeUnit unit;

    public Timeout(long timeout, @NonNull TimeUnit unit) {
        this.timeout = timeout;
        this.unit = unit;
    }

    public long getTimeout() {
        return timeout;
    }

    @NonNull
    public TimeUnit getUnit() {
        return unit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Timeout timeout1 = (Timeout) o;
        return timeout == timeout1.timeout && unit == timeout1.unit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(timeout, unit);
    }
}
