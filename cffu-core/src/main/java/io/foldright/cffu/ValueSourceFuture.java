package io.foldright.cffu;

import java.util.concurrent.Future;


public abstract class ValueSourceFuture<T> extends FutureWrapper<T> implements ValueSource<T> {
        protected ValueSourceFuture(Future<T> wrappedFuture) {
            super(wrappedFuture);
        }
}
