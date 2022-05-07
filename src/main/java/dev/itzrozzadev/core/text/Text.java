package dev.itzrozzadev.core.text;

import dev.itzrozzadev.core.entity.EntityUtil;
import dev.itzrozzadev.core.messages.Messenger;
import dev.itzrozzadev.core.number.Number;
import dev.itzrozzadev.core.version.MinecraftVersion;
import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class Text {

	private static final Pattern COLOR_AND_DECORATION_REGEX = Pattern.compile("([&" + ChatColor.COLOR_CHAR + "])[\\da-fk-orA-FK-OR]");
	public static final Pattern HEX_COLOR_REGEX = Pattern.compile("(?<!\\\\)(\\{|&|)#((?:[\\da-fA-F]{3}){2})(}|)");
	private static final Pattern RGB_X_COLOR_REGEX = Pattern.compile("(" + ChatColor.COLOR_CHAR + "x)(" + ChatColor.COLOR_CHAR + "[\\da-fA-F]){6}");


	public static String colorize(final String... texts) {
		return colorize(String.join("\n", texts));
	}

	public static List<String> colorize(final List<String> list) {
		final List<String> copy = new ArrayList<>(list);

		for (int i = 0; i < copy.size(); i++) {
			final String message = copy.get(i);

			if (message != null)
				copy.set(i, colorize(message));
		}

		return copy;
	}


	public String colorize(final String text) {
		if (text == null || text.isEmpty())
			return "";
		final char[] letters = text.toCharArray();

		for (int index = 0; index < letters.length - 1; index++) {
			if (letters[index] == '&' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx".indexOf(letters[index + 1]) > -1) {
				letters[index] = ChatColor.COLOR_CHAR;

				letters[index + 1] = Character.toLowerCase(letters[index + 1]);
			}
		}
		String result = new String(letters);

		// RGB colors - return the closest color for legacy MC versions
		final Matcher match = HEX_COLOR_REGEX.matcher(result);

		while (match.find()) {
			final String matched = match.group();
			final String colorCode = match.group(2);
			String replacement = "";

			try {
				replacement = TextColor.of("#" + colorCode).toString();

			} catch (final IllegalArgumentException ignored) {
			}

			result = result.replaceAll(Pattern.quote(matched), replacement);
		}

		result = result.replace("\\#", "#");

		return result;
	}

	public String stripColors(String text) {
		if (text == null || text.isEmpty())
			return text;

		// Replace & color codes
		Matcher matcher = COLOR_AND_DECORATION_REGEX.matcher(text);

		while (matcher.find())
			text = matcher.replaceAll("");

		// Replace hex colors, both raw and parsed
		if (MinecraftVersion.atLeast(MinecraftVersion.V.v1_16)) {
			matcher = HEX_COLOR_REGEX.matcher(text);

			while (matcher.find())
				text = matcher.replaceAll("");

			matcher = RGB_X_COLOR_REGEX.matcher(text);

			while (matcher.find())
				text = matcher.replaceAll("");

			text = text.replace(ChatColor.COLOR_CHAR + "x", "");
		}

		return text;
	}

	public static <T> String join(final T[] array) {
		return array == null ? "null" : join(Arrays.asList(array));
	}

	public static <T> String join(final Iterable<T> array) {
		return array == null ? "null" : join(array, ", ");
	}

	public static <T> String join(final T[] array, final String delimiter) {
		return join(array, delimiter, object -> object == null ? "" : simplify(object));
	}

	public static <T> String join(final Iterable<T> array, final String delimiter) {
		return join(array, delimiter, object -> object == null ? "" : simplify(object));
	}


	public static <T> String join(final T[] array, final Stringer<T> stringer) {
		return join(array, ", ", stringer);
	}

	public static <T> String join(final T[] array, final String delimiter, final Stringer<T> stringer) {
		if(array == null) {
			Messenger.log("(!) Array is null");
			return "null";
		};

		return join(Arrays.asList(array), delimiter, stringer);
	}


	public static <T> String join(final Iterable<T> array, final Stringer<T> stringer) {
		return join(array, ", ", stringer);
	}

	public static <T> String join(final Iterable<T> array, final String delimiter, final Stringer<T> stringer) {
		final Iterator<T> it = array.iterator();
		StringBuilder message = new StringBuilder();

		while (it.hasNext()) {
			final T next = it.next();

			if (next != null)
				message.append(stringer.toString(next)).append(it.hasNext() ? delimiter : "");
		}

		return message.toString();
	}

	public static String simplify(Object arg) {
		if (arg instanceof Entity)
			return EntityUtil.getName((Entity) arg);

		else if (arg instanceof CommandSender)
			return ((CommandSender) arg).getName();

		else if (arg instanceof World)
			return ((World) arg).getName();

		else if (arg instanceof Location)
			return shortLocation((Location) arg);

		else if (arg.getClass() == double.class || arg.getClass() == float.class)
			return Number.formatTwoDecimalPlaces((double) arg);

		else if (arg instanceof Collection)
			return Text.join((Collection<?>) arg, ", ", Text::simplify);

		else if (arg instanceof ChatColor)
			return ((Enum<?>) arg).name().toLowerCase();

		else if (arg instanceof TextColor)
			return ((TextColor) arg).getName();

		else if (arg instanceof Enum)
			return ((Enum<?>) arg).toString().toLowerCase();

		try {
			if (arg instanceof net.md_5.bungee.api.ChatColor)
				return ((net.md_5.bungee.api.ChatColor) arg).getName();
		} catch (final Exception ignored) {

		}

		return arg.toString();
	}

	public static String shortLocation(final Location loc) {
		if (loc == null)
			return "Location(null)";

		if(loc.getWorld() == null) return "Location(null, " + loc.getX() + ", " + loc.getY() + ", " + loc.getZ() + ")";

		return Replacer.replace("{world} [{x}, {y}, {z}]",
				"world", loc.getWorld().getName(),
				"x", ""+loc.getBlockX(),
				"y", ""+loc.getBlockY(),
				"z", ""+loc.getBlockZ());
	}
	public interface Stringer<T> {
		String toString(T object);
	}

	public String formatLowercaseRemoveUnderscores(final String name) {
		return name.toLowerCase().replace("_", " ");
	}


	public static String consoleLine() {
		return "!-----------------------------------------------------!";
	}

	public static String consoleLineUnderlined() {
		return "______________________________________________________________";
	}

	public static String chatLine() {
		return "*---------------------------------------------------*";
	}

	public static String chatLineUnderlined() {
		return "&m-----------------------------------------------------";
	}

}
