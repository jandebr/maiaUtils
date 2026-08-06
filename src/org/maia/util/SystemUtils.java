package org.maia.util;

import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
		printAllStackTraces(null);
	}

	public static void printAllStackTraces(StackTraceFilter filter) {
		System.out.println("=== STACK TRACES >>");
		Map<Thread, StackTraceElement[]> stackTraces = Thread.getAllStackTraces();
		for (Thread thread : stackTraces.keySet()) {
			StackTraceElement[] stackTrace = stackTraces.get(thread);
			if (filter == null || filter.accept(thread, stackTrace)) {
				String heading = thread.toString();
				System.out.println(heading);
				System.out.println(StringUtils.repeat('-', heading.length()));
				for (int i = 0; i < stackTrace.length; i++) {
					System.out.println(stackTrace[i].toString());
				}
				System.out.println();
				System.out.println();
			}
		}
		System.out.println("<< STACK TRACES ===");
	}

	public static void printAllStackTracesPeriodically(int secondsInterval) {
		printAllStackTracesPeriodically(secondsInterval, null);
	}

	public static void printAllStackTracesPeriodically(int secondsInterval, StackTraceFilter filter) {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					SystemUtils.sleep(secondsInterval * 1000L);
					SystemUtils.printAllStackTraces(filter);
				}
			}
		}, "StrackTracePrinter");
		t.setDaemon(true);
		t.start();
	}

	public static interface StackTraceFilter {

		boolean accept(Thread thread, StackTraceElement[] stackTrace);

	}

	public static class PackageStackTraceFilter implements StackTraceFilter {

		private Set<String> packagePrefixes;

		public PackageStackTraceFilter(String... packagePrefixes) {
			this.packagePrefixes = new HashSet<String>(Arrays.asList(packagePrefixes));
		}

		@Override
		public boolean accept(Thread thread, StackTraceElement[] stackTrace) {
			for (StackTraceElement elem : stackTrace) {
				if (acceptClassName(elem.getClassName()))
					return true;
			}
			return false;
		}

		private boolean acceptClassName(String className) {
			for (String prefix : getPackagePrefixes()) {
				if (className.startsWith(prefix))
					return true;
			}
			return false;
		}

		private Set<String> getPackagePrefixes() {
			return packagePrefixes;
		}

	}

}