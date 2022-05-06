package dev.itzrozzadev.core.command;

import dev.itzrozzadev.core.exception.BaseException;
import dev.itzrozzadev.core.reflection.ReflectionUtil;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;

import java.lang.reflect.Field;
import java.util.Map;

@UtilityClass
public class CommandRegistry {

	public static void registerCommand(final Command command) {
		final CommandMap commandMap = getCommandMap();
		commandMap.register(command.getLabel(), command);

		if (!command.isRegistered()) {
			Bukkit.getLogger().warning("(!) Command /" + command.getLabel() + " could not have been registered properly!");
		}
	}

	public static void unregisterCommand(final String label) {
		unregisterCommand(label, true);
	}

	public static void unregisterCommand(final String label, final boolean removeAliases) {
		try {
			// Unregister the commandMap from the command itself.
			final PluginCommand command = Bukkit.getPluginCommand(label);

			if (command != null) {
				final Field commandField = Command.class.getDeclaredField("commandMap");
				commandField.setAccessible(true);

				if (command.isRegistered())
					command.unregister((CommandMap) commandField.get(command));
			}

			// Delete command + aliases from server's command map.
			final Field f = SimpleCommandMap.class.getDeclaredField("knownCommands");
			f.setAccessible(true);

			final Map<String, Command> commandMap = (Map<String, Command>) f.get(getCommandMap());

			commandMap.remove(label);

			if (command != null && removeAliases)
				for (final String alias : command.getAliases())
					commandMap.remove(alias);

		} catch (final ReflectiveOperationException ex) {
			ex.printStackTrace();
			throw new BaseException("Failed to unregister command /" + label);
		}
	}


	public static SimpleCommandMap getCommandMap() {
		final Class<?> craftServer = ReflectionUtil.getOBCClass("CraftServer");

		try {
			return (SimpleCommandMap) craftServer.getDeclaredMethod("getCommandMap").invoke(Bukkit.getServer());

		} catch (final ReflectiveOperationException ex) {

			try {
				return ReflectionUtil.getFieldContent(Bukkit.getServer(), "commandMap");

			} catch (final Throwable ex2) {
				ex2.printStackTrace();
				throw new BaseException("(!) Unable to get the command map");
			}
		}
	}


}
