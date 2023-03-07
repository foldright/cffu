package io.foldright.cffu;

import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.Executor;

@ThreadSafe
public class CffuFactoryBuilder {
    // FIXME make defaultExecutor field compulsory??
    private volatile Executor defaultExecutor;

    private volatile boolean forbidObtrudeMethods = false;

    private CffuFactoryBuilder() {
    }

    ////////////////////////////////////////////////////////////////////////////////
    // Builder Methods
    ////////////////////////////////////////////////////////////////////////////////

    public static CffuFactoryBuilder newCffuFactoryBuilder() {
        return new CffuFactoryBuilder();
    }

    public CffuFactoryBuilder defaultExecutor(Executor defaultExecutor) {
        this.defaultExecutor = defaultExecutor;
        return this;
    }

    public CffuFactoryBuilder forbidObtrudeMethods(boolean forbid) {
        this.forbidObtrudeMethods = forbid;
        return this;
    }

    public CffuFactory build() {
        return new CffuFactory(defaultExecutor, forbidObtrudeMethods);
    }
}
