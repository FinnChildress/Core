package dev.itzrozzadev.core.text;

import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class Replacer {

	public String replace(String text, final String... replacements) {
		final Map<String, String> map = new HashMap<>();
		for (int i = 0; i < replacements.length; i += 2) {
			map.put(replacements[i], replacements[i + 1]);
		}
		for (final Map.Entry<String, String> entry : map.entrySet()) {
			text = text.replace("{" + entry.getKey() + "}", entry.getValue());
		}
		return text;

	}

}
