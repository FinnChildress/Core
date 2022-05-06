package dev.itzrozzadev.core.messages;

import dev.itzrozzadev.core.text.Text;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@UtilityClass
public class Messenger {

	/**
	 * Predefined prefixes for different types of messages
	 */

	@Setter
	@Getter
	private String infoPrefix = "&9&l(i) &r";

	@Setter
	@Getter
	private String successPrefix = "&2&l(!) &r";

	@Setter
	@Getter
	private String warnPrefix = "&6&l(!) &r";

	@Setter
	@Getter
	private String errorPrefix = "&4&l(!) &r";

	@Setter
	@Getter
	private String questionPrefix = "&a&l(?) &r";

	@Setter
	@Getter
	private String announcePrefix = "&5&l(!) &r";

	public void send(final CommandSender sender, final String message) {
		if (sender instanceof Player) {
			send((Player) sender, message);
		} else {
			log(message);
		}
	}

	public void send(final CommandSender sender, final String... message) {
		if (sender instanceof Player) {
			send((Player) sender, message);
		} else {
			log(message);
		}
	}

	public void send(final CommandSender sender, final List<String> messages) {
		for (final String message : messages) {
			send(sender, message);
		}
	}

	public void send(final Player player, final String message) {
		player.sendMessage(Text.colorize(message));
	}

	public void send(final Player player, final String... messages) {
		for (final String message : messages) {
			send(player, message);
		}
	}

	public void sendInfo(final Player player, final String message) {
		send(player, infoPrefix + message);
	}

	public void sendInfo(final Player player, final String... messages) {
		for (final String message : messages) {
			sendInfo(player, message);
		}
	}

	public void sendSuccess(final Player player, final String message) {
		send(player, successPrefix + message);
	}

	public void sendSuccess(final Player player, final String... messages) {
		for (final String message : messages) {
			sendSuccess(player, message);
		}
	}

	public void sendWarn(final Player player, final String message) {
		send(player, warnPrefix + message);
	}

	public void sendWarn(final Player player, final String... messages) {
		for (final String message : messages) {
			sendWarn(player, message);
		}
	}

	public void sendError(final Player player, final String message) {
		send(player, errorPrefix + message);
	}

	public void sendError(final Player player, final String... messages) {
		for (final String message : messages) {
			sendError(player, message);
		}
	}

	public void sendQuestion(final Player player, final String message) {
		send(player, questionPrefix + message);
	}

	public void sendQuestion(final Player player, final String... messages) {
		for (final String message : messages) {
			sendQuestion(player, message);
		}
	}

	public void sendAnnounce(final Player player, final String message) {
		send(player, announcePrefix + message);
	}

	public void sendAnnounce(final Player player, final String... messages) {
		for (final String message : messages) {
			sendAnnounce(player, message);
		}
	}

	public void broadcast(final String message) {
		for (final Player player : Bukkit.getOnlinePlayers()) {
			send(player, message);
		}
	}

	public void broadcast(final String... messages) {
		for (final String message : messages) {
			broadcast(message);
		}
	}

	public void broadcastInfo(final String message) {
		broadcast(infoPrefix + message);
	}

	public void broadcastInfo(final String... messages) {
		for (final String message : messages) {
			broadcastInfo(message);
		}
	}

	public void broadcastSuccess(final String message) {
		broadcast(successPrefix + message);
	}

	public void broadcastSuccess(final String... messages) {
		for (final String message : messages) {
			broadcastSuccess(message);
		}
	}

	public void broadcastWarn(final String message) {
		broadcast(warnPrefix + message);
	}

	public void broadcastWarn(final String... messages) {
		for (final String message : messages) {
			broadcastWarn(message);
		}
	}

	public void broadcastError(final String message) {
		broadcast(errorPrefix + message);
	}

	public void broadcastError(final String... messages) {
		for (final String message : messages) {
			broadcastError(message);
		}
	}

	public void broadcastQuestion(final String message) {
		broadcast(questionPrefix + message);
	}

	public void broadcastQuestion(final String... messages) {
		for (final String message : messages) {
			broadcastQuestion(message);
		}
	}

	public void broadcastAnnounce(final String message) {
		broadcast(announcePrefix + message);
	}

	public void broadcastAnnounce(final String... messages) {
		for (final String message : messages) {
			broadcastAnnounce(message);
		}
	}


	public void log(String message) {
		final CommandSender console = Bukkit.getConsoleSender();
		if (Text.stripColors(message).replace(" ", "").isEmpty()) {
			console.sendMessage("  ");
			return;
		}
		message = Text.colorize(message);
		console.sendMessage(message);

	}

	public void log(final String... message) {
		for (final String msg : message) {
			log(msg);
		}
	}
}

