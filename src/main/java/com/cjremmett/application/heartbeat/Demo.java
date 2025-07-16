package com.cjremmett.application.heartbeat;

import java.util.ArrayList;

// Creating a thread by extending the Thread class
class MyThread extends Thread {
	@Override
	public void run() {
		ArrayList<String> test = new ArrayList<String>();
		System.out.println("Thread " + Thread.currentThread().getName() + " is running");
	}
}

// Creating a thread by implementing the Runnable interface
class MyRunnable implements Runnable {
	@Override
	public void run() {
		System.out.println("Thread " + Thread.currentThread().getName() + " is running");
	}
}

public class Demo {
	public static void main(String[] args) {
		// Creating and starting threads
		MyThread thread1 = new MyThread();
		thread1.setName("MyThread-1");
		MyThread thread2 = new MyThread();
		thread2.setName("MyThread-2");

		MyRunnable runnable1 = new MyRunnable();
		Thread thread3 = new Thread(runnable1);
		thread3.setName("MyThread-3");
		Thread thread4 = new Thread(runnable1);
		thread4.setName("MyThread-4");

		thread1.start();
		thread2.start();
		thread3.start();
		thread4.start();
	}
}