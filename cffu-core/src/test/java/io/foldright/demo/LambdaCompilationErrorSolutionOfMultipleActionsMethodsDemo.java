package io.foldright.demo;

import io.foldright.cffu.CompletableFutureUtils;

import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.completedFuture;


@SuppressWarnings("Convert2MethodRef")
public class LambdaCompilationErrorSolutionOfMultipleActionsMethodsDemo {
    private static final CompletableFuture<Integer> cf = completedFuture(42);

    public static void main(String[] args) {
        ////////////////////////////////////////////////////////////////////////////////
        // `mSupplyAsync` is OK for lambda arguments
        ////////////////////////////////////////////////////////////////////////////////

        CompletableFutureUtils.mSupplyAsync(() -> 42, () -> 43);
        CompletableFutureUtils.mSupplyAsync(r -> returnVoid(), () -> 42, () -> 43);

        ////////////////////////////////////////////////////////////////////////////////
        // `mRunAsync` is OK for lambda arguments
        ////////////////////////////////////////////////////////////////////////////////

        CompletableFutureUtils.mRunAsync(() -> returnVoid(), () -> returnVoid());
        CompletableFutureUtils.mRunAsync(r -> returnVoid(), () -> returnVoid(), () -> returnVoid());

        ////////////////////////////////////////////////////////////////////////////////
        // `thenMApplyAsync`
        ////////////////////////////////////////////////////////////////////////////////

        // `thenMApplyAsync` without Executor parameter is OK for lambda arguments
        CompletableFutureUtils.thenMApplyAsync(cf, x -> 42, x -> 43);

        // ❗️ Executor lambda argument without "Lambda Parameters Type" causes below compilation error:
        //      reference to thenMApplyAsync is ambiguous. 🚨
        //        both method <T,U>thenMApplyAsync(CompletableFuture, Function...)
        //        and  method <T,U>thenMApplyAsync(CompletableFuture, Executor, Function...)
        //        match
        // CompletableFutureUtils.thenMApplyAsync(cf, r -> returnVoid(), x -> 42, x -> 43);
        //                                           ^^^
        //
        // ✅ Declaring "Lambda Parameters Type" fixes the compilation error.
        //
        // Declare the lambda parameter type(Runnable) in order to treat lambda as Executor:
        CompletableFutureUtils.thenMApplyAsync(cf, (Runnable r) -> returnVoid(), x -> 42, x -> 43);
        //                                          ^^^^^^^^
        CompletableFutureUtils.thenMApplyAsync(cf, Runnable::run, x -> 42, x -> 43);
        //                                         ^^^^^^^^

        ////////////////////////////////////////////////////////////////////////////////
        // `thenMAcceptAsync`
        ////////////////////////////////////////////////////////////////////////////////

        // ❗️ second lambda argument without "Lambda Parameters Type" causes below compilation error:
        //      reference to thenMAcceptAsync is ambiguous. 🚨
        //        both method <T>thenMAcceptAsync(CompletableFuture, Consumer...)
        //        and  method <T>thenMAcceptAsync(CompletableFuture, Executor, Consumer...)
        //        match
        // CompletableFutureUtils.thenMAcceptAsync(cf, x -> returnVoid(), x -> returnVoid());
        //                                            ^^^
        //
        // ✅ Declaring "Lambda Parameters Type" fixes the compilation error.
        //
        // declare the lambda parameter type(Integer, result type of CF) in order to treat lambda as Consumer:
        CompletableFutureUtils.thenMAcceptAsync(cf, (Integer x) -> returnVoid(), x -> returnVoid());
        //                                           ^^^^^^^
        // declare the lambda parameter type(Runnable) in order to treat lambda as Executor:
        CompletableFutureUtils.thenMAcceptAsync(cf, (Runnable r) -> returnVoid(), x -> returnVoid(), x -> returnVoid());
        //                                           ^^^^^^^^
        CompletableFutureUtils.thenMAcceptAsync(cf, Runnable::run, x -> returnVoid(), x -> returnVoid());
        //                                          ^^^^^^^^

        ////////////////////////////////////////////////////////////////////////////////
        // `thenMRunAsync` is OK for lambda arguments
        ////////////////////////////////////////////////////////////////////////////////

        CompletableFutureUtils.thenMRunAsync(cf, () -> returnVoid(), () -> returnVoid());
        CompletableFutureUtils.thenMRunAsync(cf, r -> returnVoid(), () -> returnVoid(), () -> returnVoid());
    }

    private static void returnVoid() {
    }
}
