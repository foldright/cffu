package io.foldright.cffu;

import edu.umd.cs.findbugs.annotations.NonNull;
import org.jetbrains.annotations.Contract;

import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.Executor;

@ThreadSafe
public final class CffuFactoryBuilder {
    // FIXME make defaultExecutor field compulsory??
    private volatile Executor defaultExecutor;

    private volatile boolean forbidObtrudeMethods = false;

    private CffuFactoryBuilder() {
    }

    ////////////////////////////////////////////////////////////////////////////////
    // Builder Methods
    ////////////////////////////////////////////////////////////////////////////////

    @NonNull
    @Contract(pure = true)
    public static CffuFactoryBuilder newCffuFactoryBuilder() {
        return new CffuFactoryBuilder();
    }

    @NonNull
    @Contract(pure = true)
    public CffuFactoryBuilder defaultExecutor(@NonNull Executor defaultExecutor) {
        this.defaultExecutor = defaultExecutor;
        return this;
    }

    @NonNull
    @Contract(pure = true)
    public CffuFactoryBuilder forbidObtrudeMethods(boolean forbid) {
        this.forbidObtrudeMethods = forbid;
        return this;
    }

    @NonNull
    @Contract(pure = true)
    public CffuFactory build() {
        return new CffuFactory(defaultExecutor, forbidObtrudeMethods);
    }
}
