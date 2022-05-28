package dev.itzrozzadev.core.text;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public class Replacer {

	public List<String> replace(final List<String> text, final String... replacements) {
		final List<String> replacedText = new ArrayList<>();

		for (final String string : text) {
			replacedText.add(replace(string, replacements));
		}
		return replacedText;
	}

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
