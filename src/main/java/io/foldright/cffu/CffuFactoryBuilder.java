package io.foldright.cffu;

import edu.umd.cs.findbugs.annotations.ReturnValuesAreNonnullByDefault;
import org.jetbrains.annotations.Contract;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.Executor;

@ThreadSafe
@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
public final class CffuFactoryBuilder {
    // FIXME make defaultExecutor field compulsory??
    private volatile Executor defaultExecutor;

    private volatile boolean forbidObtrudeMethods = false;

    private CffuFactoryBuilder() {
    }

    ////////////////////////////////////////////////////////////////////////////////
    // Builder Methods
    ////////////////////////////////////////////////////////////////////////////////

    @Contract(pure = true)
    public static CffuFactoryBuilder newCffuFactoryBuilder() {
        return new CffuFactoryBuilder();
    }

    @Contract(pure = true)
    @SuppressWarnings("ConstantValue")
    public CffuFactoryBuilder defaultExecutor(Executor defaultExecutor) {
        if (defaultExecutor == null) throw new NullPointerException("defaultExecutor is null");
        this.defaultExecutor = defaultExecutor;
        return this;
    }

    @Contract(pure = true)
    public CffuFactoryBuilder forbidObtrudeMethods(boolean forbid) {
        this.forbidObtrudeMethods = forbid;
        return this;
    }

    @Contract(pure = true)
    public CffuFactory build() {
        return new CffuFactory(defaultExecutor, forbidObtrudeMethods);
    }
}
