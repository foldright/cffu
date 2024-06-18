package io.foldright.cffu;


public interface Functions {

    @FunctionalInterface
    interface Function1<R1, U> {

        U apply(R1 r1);

        default U apply_(Object[] objects) {
            return apply((R1) objects[0]);
        }
    }

    @FunctionalInterface
    interface Function2<R1, R2, U> {

        U apply(R1 r1, R2 r2);

        default U apply_(Object[] objects) {
            return apply((R1) objects[0], (R2) objects[1]);
        }
    }

    @FunctionalInterface
    interface Function3<R1, R2, R3, U> {

        U apply(R1 r1, R2 r2, R3 r3);

        default U apply_(Object[] objects) {
            return apply((R1) objects[0], (R2) objects[1], (R3) objects[2]);
        }
    }

    @FunctionalInterface
    interface Function4<R1, R2, R3, R4, U> {

        U apply(R1 r1, R2 r2, R3 r3, R4 r4);

        default U apply_(Object[] objects) {
            return apply((R1) objects[0], (R2) objects[1], (R3) objects[2], (R4) objects[3]);
        }
    }

    @FunctionalInterface
    interface Function5<R1, R2, R3, R4, R5, U> {

        U apply(R1 r1, R2 r2, R3 r3, R4 r4, R5 r5);

        default U apply_(Object[] objects) {
            return apply((R1) objects[0], (R2) objects[1], (R3) objects[2], (R4) objects[3], (R5) objects[4]);
        }
    }

    @FunctionalInterface
    interface Function6<R1, R2, R3, R4, R5, R6, U> {

        U apply(R1 r1, R2 r2, R3 r3, R4 r4, R5 r5, R6 r6);

        default U apply_(Object[] objects) {
            return apply((R1) objects[0], (R2) objects[1], (R3) objects[2], (R4) objects[3], (R5) objects[4], (R6) objects[5]);
        }
    }

    @FunctionalInterface
    interface Function7<R1, R2, R3, R4, R5, R6, R7, U> {

        U apply(R1 r1, R2 r2, R3 r3, R4 r4, R5 r5, R6 r6, R7 r7);

        default U apply_(Object[] objects) {
            return apply((R1) objects[0], (R2) objects[1], (R3) objects[2], (R4) objects[3], (R5) objects[4], (R6) objects[5], (R7) objects[6]);
        }
    }

    @FunctionalInterface
    interface Function8<R1, R2, R3, R4, R5, R6, R7, R8, U> {

        U apply(R1 r1, R2 r2, R3 r3, R4 r4, R5 r5, R6 r6, R7 r7, R8 r8);

        default U apply_(Object[] objects) {
            return apply((R1) objects[0], (R2) objects[1], (R3) objects[2], (R4) objects[3], (R5) objects[4], (R6) objects[5], (R7) objects[6], (R8) objects[7]);
        }
    }

    @FunctionalInterface
    interface Function9<R1, R2, R3, R4, R5, R6, R7, R8, R9, U> {

        U apply(R1 r1, R2 r2, R3 r3, R4 r4, R5 r5, R6 r6, R7 r7, R8 r8, R9 r9);

        default U apply_(Object[] objects) {
            return apply((R1) objects[0], (R2) objects[1], (R3) objects[2], (R4) objects[3], (R5) objects[4], (R6) objects[5], (R7) objects[6], (R8) objects[7], (R9) objects[8]);
        }
    }

    @FunctionalInterface
    interface Function10<R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, U> {

        U apply(R1 r1, R2 r2, R3 r3, R4 r4, R5 r5, R6 r6, R7 r7, R8 r8, R9 r9, R10 r10);

        default U apply_(Object[] objects) {
            return apply((R1) objects[0], (R2) objects[1], (R3) objects[2], (R4) objects[3], (R5) objects[4], (R6) objects[5], (R7) objects[6], (R8) objects[7], (R9) objects[8], (R10) objects[9]);
        }
    }

    @FunctionalInterface
    interface Function11<R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, U> {

        U apply(R1 r1, R2 r2, R3 r3, R4 r4, R5 r5, R6 r6, R7 r7, R8 r8, R9 r9, R10 r10, R11 r11);

        default U apply_(Object[] objects) {
            return apply((R1) objects[0], (R2) objects[1], (R3) objects[2], (R4) objects[3], (R5) objects[4], (R6) objects[5], (R7) objects[6], (R8) objects[7], (R9) objects[8], (R10) objects[9], (R11) objects[10]);
        }
    }

    @FunctionalInterface
    interface Function12<R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12, U> {

        U apply(R1 r1, R2 r2, R3 r3, R4 r4, R5 r5, R6 r6, R7 r7, R8 r8, R9 r9, R10 r10, R11 r11, R12 r12);

        default U apply_(Object[] objects) {
            return apply((R1) objects[0], (R2) objects[1], (R3) objects[2], (R4) objects[3], (R5) objects[4], (R6) objects[5], (R7) objects[6], (R8) objects[7], (R9) objects[8], (R10) objects[9], (R11) objects[10], (R12) objects[11]);
        }
    }

    @FunctionalInterface
    interface Function13<R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12, R13, U> {

        U apply(R1 r1, R2 r2, R3 r3, R4 r4, R5 r5, R6 r6, R7 r7, R8 r8, R9 r9, R10 r10, R11 r11, R12 r12, R13 r13);

        default U apply_(Object[] objects) {
            return apply((R1) objects[0], (R2) objects[1], (R3) objects[2], (R4) objects[3], (R5) objects[4], (R6) objects[5], (R7) objects[6], (R8) objects[7], (R9) objects[8], (R10) objects[9], (R11) objects[10], (R12) objects[11], (R13) objects[12]);
        }
    }

    @FunctionalInterface
    interface Function14<R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12, R13, R14, U> {

        U apply(R1 r1, R2 r2, R3 r3, R4 r4, R5 r5, R6 r6, R7 r7, R8 r8, R9 r9, R10 r10, R11 r11, R12 r12, R13 r13, R14 r14);

        default U apply_(Object[] objects) {
            return apply((R1) objects[0], (R2) objects[1], (R3) objects[2], (R4) objects[3], (R5) objects[4], (R6) objects[5], (R7) objects[6], (R8) objects[7], (R9) objects[8], (R10) objects[9], (R11) objects[10], (R12) objects[11], (R13) objects[12], (R14) objects[13]);
        }
    }

    @FunctionalInterface
    interface Function15<R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12, R13, R14, R15, U> {

        U apply(R1 r1, R2 r2, R3 r3, R4 r4, R5 r5, R6 r6, R7 r7, R8 r8, R9 r9, R10 r10, R11 r11, R12 r12, R13 r13, R14 r14, R15 r15);

        default U apply_(Object[] objects) {
            return apply((R1) objects[0], (R2) objects[1], (R3) objects[2], (R4) objects[3], (R5) objects[4], (R6) objects[5], (R7) objects[6], (R8) objects[7], (R9) objects[8], (R10) objects[9], (R11) objects[10], (R12) objects[11], (R13) objects[12], (R14) objects[13], (R15) objects[14]);
        }
    }
}
