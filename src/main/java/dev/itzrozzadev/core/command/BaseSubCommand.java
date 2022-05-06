package dev.itzrozzadev.core.command;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.util.Arrays;

public abstract class BaseSubCommand extends BaseCommand {

	@Getter
	private final String[] subLabels;

	@Setter(value = AccessLevel.PROTECTED)
	@Getter(value = AccessLevel.PROTECTED)
	private String subLabel;


	protected BaseSubCommand(final BaseCommandGroup parent, final String sublabel) {
		super(parent.getLabel());

		this.subLabels = sublabel.split("([|/])");
		if (subLabels.length == 0) {
			Bukkit.getLogger().severe("(!) Please set at least one subLabel for the command: " + sublabel);
			return;
		}

		this.subLabel = subLabels[0];
		setPermission(getPermission() + getLabel().toLowerCase());

	}

	@Override
	public final boolean equals(final Object obj) {
		return obj instanceof BaseSubCommand && Arrays.equals(((BaseSubCommand) obj).subLabels, this.subLabels);
	}
}
