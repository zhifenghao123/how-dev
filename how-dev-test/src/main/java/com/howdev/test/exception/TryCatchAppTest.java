package com.howdev.test.exception;

/**
 * TryCatchAppTest class
 *
 * @author haozhifeng
 * @date 2023/06/08
 */
public class TryCatchAppTest {
    public static void main(String[] args) {
        tryCatchFinallyWithoutReturn();
        System.out.println("------------------------");
        System.out.println(tryCatchFinallyWithReturn1());
        System.out.println("-----------------------");
        System.out.println(tryCatchFinallyWithReturn2());

        System.out.println("-----------------------");
        System.out.println(tryCatchFinallyWithReturn3());
        System.out.println("---------------");
        System.out.println(tryCatchFinallyWithReturn4());


    }

    public static void tryCatchFinallyWithoutReturn() {
        int i = 0;
        try {
            System.out.println("start execute try code clock.");
            i = 1 / i;
            System.out.println("end execute try code clock.");

        } catch (NullPointerException nullPointerException) {
            System.out.println("execute catch nullPointerException code clock.");
        } catch (Exception exception) {
            System.out.println("execute catch exception code clock.");
        } finally {
            System.out.println("execute finally code clock.");
        }
    }

    public static int tryCatchFinallyWithReturn1() {
        int i = 0;
        try {
            System.out.println("start execute try code clock.");
            i = 1 / i;
            System.out.println("end execute try code clock.");
            return i;

        } catch (NullPointerException nullPointerException) {
            System.out.println("execute catch nullPointerException code clock.");
            return -999;
        } catch (Exception exception) {
            System.out.println("execute catch exception code clock.");
            return -998;
        } finally {
            System.out.println("execute finally code clock.");
            return -100;
        }
    }

    public static int tryCatchFinallyWithReturn2() {
        int i = 0;
        try {
            System.out.println("start execute try code clock.");
            i = 1 / i;
            System.out.println("end execute try code clock.");
        } catch (NullPointerException nullPointerException) {
            System.out.println("execute catch nullPointerException code clock.");
            return -999;
        } catch (Exception exception) {
            System.out.println("execute catch exception code clock.");
            return -998;
        } finally {
            System.out.println("execute finally code clock.");
        }
        return i;
    }

    public static int tryCatchFinallyWithReturn3() {
        int i = 0;
        try {
            i++;
            i = i / 0;
            return i++;
        } catch (Exception e) {
            i++;
            return i++;
        } finally {
            return ++i;
        }
    }

    public static int tryCatchFinallyWithReturn4() {
        int result = 0;
        try {
            result = 200;
            throw new Exception();

        } catch (Exception e) {
            System.out.println("execute catch exception");
            result = -1;
        } finally {
            System.out.println("execute finally");
        }
        return result;
    }

}
