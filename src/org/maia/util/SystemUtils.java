package org.maia.util;

import java.lang.management.ManagementFactory;
import java.util.Map;

import javax.swing.SwingUtilities;

import com.sun.management.OperatingSystemMXBean;

public class SystemUtils {

	private SystemUtils() {
	}

	@SuppressWarnings("deprecation")
	public static double getCpuLoad() {
		OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
		return Math.max(0, osBean.getSystemCpuLoad());
	}

	public static long getTotalMemoryInBytes() {
		return Runtime.getRuntime().totalMemory();
	}

	public static long getFreeMemoryInBytes() {
		return Runtime.getRuntime().freeMemory();
	}

	public static long getUsedMemoryInBytes() {
		return getTotalMemoryInBytes() - getFreeMemoryInBytes();
	}

	public static long getMaxMemoryInBytes() {
		return Runtime.getRuntime().maxMemory();
	}

	public static void runOutsideAwtEventDispatchThread(Runnable task) {
		if (SwingUtilities.isEventDispatchThread()) {
			new Thread(task).start();
		} else {
			task.run();
		}
	}

	public static void sleep(long milliseconds) {
		if (milliseconds > 0L) {
			try {
				Thread.sleep(milliseconds);
			} catch (InterruptedException e) {
			}
		}
	}

	public static void sleepNanos(long nanoseconds) {
		if (nanoseconds > 0L) {
			try {
				Thread.sleep(nanoseconds / 1000000L, (int) (nanoseconds % 1000000L));
			} catch (InterruptedException e) {
			}
		}
	}

	public static void releaseMemory() {
		System.gc();
	}

	public static void exit() {
		System.exit(0);
	}

	public static void printAllStackTraces() {
		System.out.println("=== STACK TRACES >>");
		Map<Thread, StackTraceElement[]> stackTraces = Thread.getAllStackTraces();
		for (Thread thread : stackTraces.keySet()) {
			String heading = thread.toString();
			System.out.println(heading);
			System.out.println(StringUtils.repeat('-', heading.length()));
			StackTraceElement[] stackTrace = stackTraces.get(thread);
			for (int i = 0; i < stackTrace.length; i++) {
				System.out.println(stackTrace[i].toString());
			}
			System.out.println();
			System.out.println();
		}
		System.out.println("<< STACK TRACES ===");
	}

	public static void printAllStackTracesPeriodically(int secondsInterval) {
		new StrackTracePrinter(secondsInterval).start();
	}

	private static class StrackTracePrinter extends Thread {

		private int secondsInterval;

		public StrackTracePrinter(int secondsInterval) {
			super("StrackTracePrinter");
			setDaemon(true);
			this.secondsInterval = secondsInterval;
		}

		@Override
		public void run() {
			while (true) {
				SystemUtils.sleep(getSecondsInterval() * 1000L);
				SystemUtils.printAllStackTraces();
			}
		}

		private int getSecondsInterval() {
			return secondsInterval;
		}

	}

}