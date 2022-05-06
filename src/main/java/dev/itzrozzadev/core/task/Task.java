package dev.itzrozzadev.core.task;

import dev.itzrozzadev.core.plugin.BasePlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

public class Task {


	public static <T extends Runnable> BukkitTask runLater(final T task) {
		return runLater(1, task);
	}

	public static BukkitTask runLater(final int delayTicks, final Runnable task) {
		final BukkitScheduler scheduler = Bukkit.getScheduler();
		final JavaPlugin instance = BasePlugin.getInstance();

		try {
			return runIfDisabled(task) ? null : delayTicks == 0 ? task instanceof BukkitRunnable ? ((BukkitRunnable) task).runTask(instance) : scheduler.runTask(instance, task) : task instanceof BukkitRunnable ? ((BukkitRunnable) task).runTaskLater(instance, delayTicks) : scheduler.runTaskLater(instance, task, delayTicks);
		} catch (final NoSuchMethodError err) {

			return runIfDisabled(task) ? null
					: delayTicks == 0
					? task instanceof BukkitRunnable ? ((BukkitRunnable) task).runTask(instance) : getTaskFromId(scheduler.scheduleSyncDelayedTask(instance, task))
					: task instanceof BukkitRunnable ? ((BukkitRunnable) task).runTaskLater(instance, delayTicks) : getTaskFromId(scheduler.scheduleSyncDelayedTask(instance, task, delayTicks));
		}
	}

	public static BukkitTask runAsync(final Runnable task) {
		return runLaterAsync(0, task);
	}

	public static BukkitTask runLaterAsync(final Runnable task) {
		return runLaterAsync(0, task);
	}

	public static BukkitTask runLaterAsync(final int delayTicks, final Runnable task) {
		final BukkitScheduler scheduler = Bukkit.getScheduler();
		final JavaPlugin instance = BasePlugin.getInstance();

		try {
			return runIfDisabled(task) ? null : delayTicks == 0 ? task instanceof BukkitRunnable ? ((BukkitRunnable) task).runTaskAsynchronously(instance) : scheduler.runTaskAsynchronously(instance, task) : task instanceof BukkitRunnable ? ((BukkitRunnable) task).runTaskLaterAsynchronously(instance, delayTicks) : scheduler.runTaskLaterAsynchronously(instance, task, delayTicks);

		} catch (final NoSuchMethodError err) {
			return runIfDisabled(task) ? null
					: delayTicks == 0
					? getTaskFromId(scheduler.scheduleAsyncDelayedTask(instance, task))
					: getTaskFromId(scheduler.scheduleAsyncDelayedTask(instance, task, delayTicks));
		}
	}

	public static BukkitTask runTimer(final int repeatTicks, final Runnable task) {
		return runTimer(0, repeatTicks, task);
	}

	public static BukkitTask runTimer(final int delayTicks, final int repeatTicks, final Runnable task) {

		try {
			return runIfDisabled(task) ? null : task instanceof BukkitRunnable ? ((BukkitRunnable) task).runTaskTimer(BasePlugin.getInstance(), delayTicks, repeatTicks) : Bukkit.getScheduler().runTaskTimer(BasePlugin.getInstance(), task, delayTicks, repeatTicks);

		} catch (final NoSuchMethodError err) {
			return runIfDisabled(task) ? null
					: getTaskFromId(Bukkit.getScheduler().scheduleSyncRepeatingTask(BasePlugin.getInstance(), task, delayTicks, repeatTicks));
		}
	}

	public static BukkitTask runTimerAsync(final int repeatTicks, final Runnable task) {
		return runTimerAsync(0, repeatTicks, task);
	}

	public static BukkitTask runTimerAsync(final int delayTicks, final int repeatTicks, final Runnable task) {

		try {
			return runIfDisabled(task) ? null : task instanceof BukkitRunnable ? ((BukkitRunnable) task).runTaskTimerAsynchronously(BasePlugin.getInstance(), delayTicks, repeatTicks) : Bukkit.getScheduler().runTaskTimerAsynchronously(BasePlugin.getInstance(), task, delayTicks, repeatTicks);

		} catch (final NoSuchMethodError err) {
			return runIfDisabled(task) ? null
					: getTaskFromId(Bukkit.getScheduler().scheduleAsyncRepeatingTask(BasePlugin.getInstance(), task, delayTicks, repeatTicks));
		}
	}

	private static BukkitTask getTaskFromId(final int taskId) {

		for (final BukkitTask task : Bukkit.getScheduler().getPendingTasks())
			if (task.getTaskId() == taskId)
				return task;

		return null;
	}

	private static boolean runIfDisabled(final Runnable run) {
		if (!BasePlugin.getInstance().isEnabled()) {
			run.run();
			return true;
		}
		return false;
	}
}
