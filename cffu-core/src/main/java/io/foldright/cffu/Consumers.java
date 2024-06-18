package io.foldright.cffu;


public interface Consumers {

    @FunctionalInterface
    interface Consumer1<R1> {

        void accept(R1 r1);

        default void accept_(Object[] objects) {
            accept((R1) objects[0]);
        }
    }

    @FunctionalInterface
    interface Consumer2<R1, R2> {

        void accept(R1 r1, R2 r2);

        default void accept_(Object[] objects) {
            accept((R1) objects[0], (R2) objects[1]);
        }
    }

    @FunctionalInterface
    interface Consumer3<R1, R2, R3> {

        void accept(R1 r1, R2 r2, R3 r3);

        default void accept_(Object[] objects) {
            accept((R1) objects[0], (R2) objects[1], (R3) objects[2]);
        }
    }

    @FunctionalInterface
    interface Consumer4<R1, R2, R3, R4> {

        void accept(R1 r1, R2 r2, R3 r3, R4 r4);

        default void accept_(Object[] objects) {
            accept((R1) objects[0], (R2) objects[1], (R3) objects[2], (R4) objects[3]);
        }
    }

    @FunctionalInterface
    interface Consumer5<R1, R2, R3, R4, R5> {

        void accept(R1 r1, R2 r2, R3 r3, R4 r4, R5 r5);

        default void accept_(Object[] objects) {
            accept((R1) objects[0], (R2) objects[1], (R3) objects[2], (R4) objects[3], (R5) objects[4]);
        }
    }

    @FunctionalInterface
    interface Consumer6<R1, R2, R3, R4, R5, R6> {

        void accept(R1 r1, R2 r2, R3 r3, R4 r4, R5 r5, R6 r6);

        default void accept_(Object[] objects) {
            accept((R1) objects[0], (R2) objects[1], (R3) objects[2], (R4) objects[3], (R5) objects[4], (R6) objects[5]);
        }
    }

    @FunctionalInterface
    interface Consumer7<R1, R2, R3, R4, R5, R6, R7> {

        void accept(R1 r1, R2 r2, R3 r3, R4 r4, R5 r5, R6 r6, R7 r7);

        default void accept_(Object[] objects) {
            accept((R1) objects[0], (R2) objects[1], (R3) objects[2], (R4) objects[3], (R5) objects[4], (R6) objects[5], (R7) objects[6]);
        }
    }

    @FunctionalInterface
    interface Consumer8<R1, R2, R3, R4, R5, R6, R7, R8> {

        void accept(R1 r1, R2 r2, R3 r3, R4 r4, R5 r5, R6 r6, R7 r7, R8 r8);

        default void accept_(Object[] objects) {
            accept((R1) objects[0], (R2) objects[1], (R3) objects[2], (R4) objects[3], (R5) objects[4], (R6) objects[5], (R7) objects[6], (R8) objects[7]);
        }
    }

    @FunctionalInterface
    interface Consumer9<R1, R2, R3, R4, R5, R6, R7, R8, R9> {

        void accept(R1 r1, R2 r2, R3 r3, R4 r4, R5 r5, R6 r6, R7 r7, R8 r8, R9 r9);

        default void accept_(Object[] objects) {
            accept((R1) objects[0], (R2) objects[1], (R3) objects[2], (R4) objects[3], (R5) objects[4], (R6) objects[5], (R7) objects[6], (R8) objects[7], (R9) objects[8]);
        }
    }

    @FunctionalInterface
    interface Consumer10<R1, R2, R3, R4, R5, R6, R7, R8, R9, R10> {

        void accept(R1 r1, R2 r2, R3 r3, R4 r4, R5 r5, R6 r6, R7 r7, R8 r8, R9 r9, R10 r10);

        default void accept_(Object[] objects) {
            accept((R1) objects[0], (R2) objects[1], (R3) objects[2], (R4) objects[3], (R5) objects[4], (R6) objects[5], (R7) objects[6], (R8) objects[7], (R9) objects[8], (R10) objects[9]);
        }
    }

    @FunctionalInterface
    interface Consumer11<R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11> {

        void accept(R1 r1, R2 r2, R3 r3, R4 r4, R5 r5, R6 r6, R7 r7, R8 r8, R9 r9, R10 r10, R11 r11);

        default void accept_(Object[] objects) {
            accept((R1) objects[0], (R2) objects[1], (R3) objects[2], (R4) objects[3], (R5) objects[4], (R6) objects[5], (R7) objects[6], (R8) objects[7], (R9) objects[8], (R10) objects[9], (R11) objects[10]);
        }
    }

    @FunctionalInterface
    interface Consumer12<R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12> {

        void accept(R1 r1, R2 r2, R3 r3, R4 r4, R5 r5, R6 r6, R7 r7, R8 r8, R9 r9, R10 r10, R11 r11, R12 r12);

        default void accept_(Object[] objects) {
            accept((R1) objects[0], (R2) objects[1], (R3) objects[2], (R4) objects[3], (R5) objects[4], (R6) objects[5], (R7) objects[6], (R8) objects[7], (R9) objects[8], (R10) objects[9], (R11) objects[10], (R12) objects[11]);
        }
    }

    @FunctionalInterface
    interface Consumer13<R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12, R13> {

        void accept(R1 r1, R2 r2, R3 r3, R4 r4, R5 r5, R6 r6, R7 r7, R8 r8, R9 r9, R10 r10, R11 r11, R12 r12, R13 r13);

        default void accept_(Object[] objects) {
            accept((R1) objects[0], (R2) objects[1], (R3) objects[2], (R4) objects[3], (R5) objects[4], (R6) objects[5], (R7) objects[6], (R8) objects[7], (R9) objects[8], (R10) objects[9], (R11) objects[10], (R12) objects[11], (R13) objects[12]);
        }
    }

    @FunctionalInterface
    interface Consumer14<R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12, R13, R14> {

        void accept(R1 r1, R2 r2, R3 r3, R4 r4, R5 r5, R6 r6, R7 r7, R8 r8, R9 r9, R10 r10, R11 r11, R12 r12, R13 r13, R14 r14);

        default void accept_(Object[] objects) {
            accept((R1) objects[0], (R2) objects[1], (R3) objects[2], (R4) objects[3], (R5) objects[4], (R6) objects[5], (R7) objects[6], (R8) objects[7], (R9) objects[8], (R10) objects[9], (R11) objects[10], (R12) objects[11], (R13) objects[12], (R14) objects[13]);
        }
    }

    @FunctionalInterface
    interface Consumer15<R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12, R13, R14, R15> {

        void accept(R1 r1, R2 r2, R3 r3, R4 r4, R5 r5, R6 r6, R7 r7, R8 r8, R9 r9, R10 r10, R11 r11, R12 r12, R13 r13, R14 r14, R15 r15);

        default void accept_(Object[] objects) {
            accept((R1) objects[0], (R2) objects[1], (R3) objects[2], (R4) objects[3], (R5) objects[4], (R6) objects[5], (R7) objects[6], (R8) objects[7], (R9) objects[8], (R10) objects[9], (R11) objects[10], (R12) objects[11], (R13) objects[12], (R14) objects[13], (R15) objects[14]);
        }
    }
}
