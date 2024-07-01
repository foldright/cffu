package io.foldright.cffu;


import java.util.concurrent.ThreadLocalRandom;


public class LDemo {
    public static void main(String[] args) {
        if(ThreadLocalRandom.current().nextBoolean()) {
            System.setProperty("cffu.uncaught.exception.report", "full");
            System.out.println("set full to cffu.uncaught.exception.report");
        }
        ExceptionReporter.reportException("hello", new RuntimeException("Bang!"));
    }
}
