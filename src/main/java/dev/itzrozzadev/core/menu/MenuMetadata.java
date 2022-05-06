package dev.itzrozzadev.core.menu;


import dev.itzrozzadev.core.plugin.BasePlugin;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MenuMetadata {

	CURRENT_MENU(BasePlugin.getNamed() + "_current_menu"),
	PREVIOUS_MENU(BasePlugin.getNamed() + "_previous_menu"),
	LAST_CLOSED_MENU(BasePlugin.getNamed() + "_last_closed_menu"),

	;
	private final String key;
}
