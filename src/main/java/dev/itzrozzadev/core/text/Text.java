package dev.itzrozzadev.core.text;

import dev.itzrozzadev.core.version.MinecraftVersion;
import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
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

	public String formatUnderscores(final String name) {
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
