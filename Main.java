package org.example;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Java Development Kit (семинары)
 * Урок 5. Многопоточность
 * Калинин Павел
 * 26.10.2023
 * Задание: Накормить пятерых философов с помощью пятью чужих немытых вилок.
 * ● Есть пять философов (потоки), которые могут либо
 * обедать (выполнение кода) либо размышлять (ожидание).
 * ● Каждый философ должен пообедать три раза.
 * ● Каждый прием пищи длиться 500 миллисекунд
 * ● После каждого приема пищи философ должен размышлять
 * ● Не должно возникнуть общей блокировки
 * ● Философы не должны есть больше одного раза подряд
 */

public class Main {
    public static void main(String[] args) {
        int QTY_PH_FORK = 5; // ● Есть пять философов
        int QTY_EAT = 3;     // ● Каждый философ должен пообедать три раза.
        Object[] forks = new Object[QTY_PH_FORK];
        for (int i = 0; i < QTY_PH_FORK; i++)
            forks[i] = new Object();
        class Ph extends Thread {
            static AtomicInteger counter = new AtomicInteger(0);
            static String HH_mm_ss_S = "HH:mm:ss.SSS";
            int n; // Номер философа по порядку.
            int leftFolk, rightFolk; // Относительно философа.

            public Ph() {
                n = counter.getAndIncrement();
                leftFolk = n;                                // Философы с вилками нумеруются по часовой стрелки.
                rightFolk = (n==0)? QTY_PH_FORK - 1 : n - 1; // Философы с вилками нумеруются по часовой стрелки.
            }

            public void run() {
                for (int i = 0; i < QTY_EAT; i++) { // ● Каждый философ должен пообедать три раза.
                    synchronized (forks[leftFolk]) {
                        synchronized (forks[rightFolk]) {
                            System.out.println(n + " Философ начал прием пищи    №" + i + "   "
                                    + (new SimpleDateFormat(HH_mm_ss_S)).format(new Date())
                                    + "   вилки "+ leftFolk+ " и " + rightFolk + " взял");
                            try {
                                sleep(500); // ● Каждый прием пищи длиться 500 миллисекунд.
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            System.out.println(n + " Философ закончил прием пищи №" + i + "   "
                                    + (new SimpleDateFormat(HH_mm_ss_S)).format(new Date())
                                    + "   вилки "+ leftFolk+ " и " + rightFolk + " положил");
                        }
                    }

                    // Поразмышлять после приема пищи.
                    // ● Философы не должны есть больше одного раза подряд.
                    System.out.println(n + " Философ начал размышлять    №" + i + "   " + (new SimpleDateFormat(HH_mm_ss_S)).format(new Date()));
                    try {
                        sleep(10); // ● После каждого приема пищи философ должен размышлять.
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println(n + " Философ закончил размышлять №" + i + "   " + (new SimpleDateFormat(HH_mm_ss_S)).format(new Date()));
                }
            }
        }

        // ExecutorService threadPool = Executors.newSingleThreadExecutor();
        // ExecutorService threadPool = Executors.newFixedThreadPool(QTY_PH_FORK); // вероятность общей блокировки - пронаблюдал
        // Разрубим Гордиев узел двумя символами :)
        ExecutorService threadPool = Executors.newFixedThreadPool(QTY_PH_FORK-1); // ● Не должно возникнуть общей блокировки
        for (int i = 0; i < QTY_PH_FORK; i++)
            threadPool.execute(new Ph());
        threadPool.shutdown();
    }
}