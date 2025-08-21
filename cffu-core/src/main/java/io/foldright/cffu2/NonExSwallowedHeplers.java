package io.foldright.cffu2;

import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import static io.foldright.cffu2.LLCF.safeAddSuppressedEx;


// =============================================================================
// Little classified lambdas to better support monitoring
// =============================================================================

class NonExSwallowedFunction<X extends Throwable, T> implements Function<X, T> {
    private final Function<X, T> fn;
    private final boolean addSuppressedToOriginalEx;

    private NonExSwallowedFunction(Function<X, T> fn, boolean addSuppressedToOriginalEx) {
        this.fn = fn;
        this.addSuppressedToOriginalEx = addSuppressedToOriginalEx;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    static <F extends Function> F wrap(F fn, boolean addSuppressedToOriginalEx) {
        return (F) new NonExSwallowedFunction(fn, addSuppressedToOriginalEx);
    }

    @Override
    public T apply(@Nullable X originalEx) {
        try {
            return fn.apply(originalEx);
        } catch (Throwable newEx) {
            if (originalEx != null) {
                // when exceptions occur in this exception process function,
                // the error context is preserved by calling addSuppressed
                if (addSuppressedToOriginalEx) safeAddSuppressedEx(newEx, originalEx);
                else safeAddSuppressedEx(originalEx, newEx);
            }
            throw newEx;
        }
    }
}

class NonExSwallowedBiFunction<T, X extends Throwable, U> implements BiFunction<T, X, U> {
    private final BiFunction<T, X, U> fn;
    private final boolean addSuppressedToOriginalEx;

    private NonExSwallowedBiFunction(BiFunction<T, X, U> fn, boolean addSuppressedToOriginalEx) {
        this.fn = fn;
        this.addSuppressedToOriginalEx = addSuppressedToOriginalEx;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    static <F extends BiFunction> F wrap(F fn, boolean addSuppressedToOriginalEx) {
        return (F) new NonExSwallowedBiFunction(fn, addSuppressedToOriginalEx);
    }

    @Override
    public U apply(T v, @Nullable X originalEx) {
        try {
            return fn.apply(v, originalEx);
        } catch (Throwable newEx) {
            if (originalEx != null) {
                // when exceptions occur in this exception process function,
                // the error context is preserved by calling addSuppressed
                if (addSuppressedToOriginalEx) safeAddSuppressedEx(newEx, originalEx);
                else safeAddSuppressedEx(originalEx, newEx);
            }
            throw newEx;
        }
    }
}

class NonExSwallowedBiConsumer<T, X extends Throwable> implements BiConsumer<T, X> {
    private final BiConsumer<T, X> action;
    private final boolean addSuppressedToOriginalEx;

    private NonExSwallowedBiConsumer(BiConsumer<T, X> action, boolean addSuppressedToOriginalEx) {
        this.action = action;
        this.addSuppressedToOriginalEx = addSuppressedToOriginalEx;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    static <F extends BiConsumer> F wrap(F action, boolean addSuppressedToOriginalEx) {
        return (F) new NonExSwallowedBiConsumer(action, addSuppressedToOriginalEx);
    }

    @Override
    public void accept(T v, @Nullable X originalEx) {
        try {
            action.accept(v, originalEx);
        } catch (Throwable newEx) {
            if (originalEx != null) {
                // when exceptions occur in this exception process function,
                // the error context is preserved by calling addSuppressed
                if (addSuppressedToOriginalEx) safeAddSuppressedEx(newEx, originalEx);
                else safeAddSuppressedEx(originalEx, newEx);
            }
            throw newEx;
        }
    }
}
