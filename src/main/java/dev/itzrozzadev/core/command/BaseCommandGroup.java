package dev.itzrozzadev.core.command;

import dev.itzrozzadev.core.messages.Messenger;
import dev.itzrozzadev.core.task.Task;
import dev.itzrozzadev.core.text.Replacer;
import dev.itzrozzadev.core.text.Text;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public abstract class BaseCommandGroup {

	private final List<BaseSubCommand> subCommands = new ArrayList<>();
	private BaseCommand mainCommand;
	@Getter
	private String label;
	@Getter
	private List<String> aliases;

	@Getter
	protected CommandSender sender;

	protected abstract void registerSubCommands();

	protected final void registerSubcommand(final BaseSubCommand command) {
		if (mainCommand == null) {
			Bukkit.getLogger().severe("(!) Main command not registered");
			return;
		}
		if (subCommands.contains(command)) {
			Bukkit.getLogger().severe("(!) Subcommand /" + mainCommand.getLabel() + " " + command.getSubLabel() + " already registered");
			return;
		}

		subCommands.add(command);
	}

	protected String getHelpHeader() {
		return "&f&l" + label + " Help Menu";
	}

	protected List<String> getHelpLabel() {
		return Arrays.asList("help", "?");
	}

	protected String getSubCommandHelp() {
		return ">> /{label} {sublabel} {usage} - {description}";
	}

	protected String getNoPermissionSubcommands() {
		return Messenger.getErrorPrefix() + "&cNo Permission";
	}


	protected BaseCommandGroup(final String label, final List<String> aliases) {
		this.label = label;
		this.aliases = aliases;
	}

	protected BaseCommandGroup(final String labelAndAliases) {
		final String[] split = labelAndAliases.split("([|/])");
		this.label = split[0];
		this.aliases = split.length > 0 ? Arrays.asList(Arrays.copyOfRange(split, 1, split.length)) : new ArrayList<>();
	}

	public final void register() {
		if (isRegistered()) {
			Bukkit.getLogger().severe("(!) Main command already registered as " + mainCommand.getLabel());
			return;
		}

		mainCommand = new MainCommand(label);

		if (aliases != null)
			mainCommand.setAliases(aliases);

		mainCommand.register();
		registerSubCommands();

		// Sort A-Z
		subCommands.sort(Comparator.comparing(BaseSubCommand::getSubLabel));

		checkRegistered();
	}

	public final boolean isRegistered() {
		return mainCommand != null;
	}

	private String colorizeUsage(final String message) {
		return message == null ? "" : message;
	}

	private void checkRegistered() {
		final List<String> aliases = new ArrayList<>();

		for (final BaseSubCommand subCommand : subCommands)
			for (final String alias : subCommand.getSubLabels()) {
				if (aliases.contains(alias)) {
					Bukkit.getLogger().severe("(!) Subcommand /" + mainCommand.getLabel() + " " + alias + " already registered");
					return;
				}
				aliases.add(alias);
			}
	}


	public final class MainCommand extends BaseCommand {

		private MainCommand(final String label) {
			super(label);
			setPermission(null);
			setAutoHandleHelp(false);
		}

		@Override
		protected void onCommand() {
			BaseCommandGroup.this.sender = this.sender;
			if (args.length == 0) {
				sendSubCommandHelp();
				return;
			}

			final String argument = args[0];
			final BaseSubCommand command = getSubcommand(argument);

			if (command != null) {
				final String oldSublabel = command.getSubLabel();

				try {
					command.setSubLabel(args[0]);
					command.execute(sender, getLabel(), args.length == 1 ? new String[]{} : Arrays.copyOfRange(args, 1, args.length));
				} finally {
					command.setSubLabel(oldSublabel);
				}
			} else if (!getHelpLabel().isEmpty() && getHelpLabel().contains(argument)) {
				sendSubCommandHelp();
			} else {
				sendSubCommandHelp();
			}
		}


		private void sendSubCommandHelp() {
			Task.runAsync(() -> {
				if (subCommands.isEmpty()) {
					Messenger.send(sender, Messenger.getErrorPrefix() + "No sub commands found!");
					return;
				}
				final List<String> helpMessage = new ArrayList<>();
				helpMessage.add(Text.chatLineUnderlined());
				helpMessage.add(getHelpHeader());
				for (final BaseSubCommand subCommand : subCommands) {
					if (hasPerm(subCommand.getPermission())) {
						subCommand.sender = sender;
						final String usage = colorizeUsage(subCommand.getUsage());
						final String description = subCommand.getDescription() != null ? subCommand.getDescription() : "";
						helpMessage.add(Replacer.replace(getSubCommandHelp(),
								"label", getLabel(),
								"sublabel", subCommand.getSubLabel(),
								"usage", usage,
								"description", description.isEmpty() ? "" : description)
						);

						Messenger.log("Label: " + getLabel() + " Sublabel: " + subCommand.getSubLabel() + " Usage: " + usage + " Description: " + description);
					}
				}

				if (helpMessage.isEmpty()) {
					Messenger.send(sender, getNoPermissionSubcommands());
				} else {
					Task.runLater(() -> Messenger.send(sender, helpMessage));
				}
			});
		}

		@Override
		public List<String> tabComplete() {
			if (args.length == 1)
				return tabCompleteSubcommands(sender, args[0]);

			if (args.length > 1) {
				final BaseSubCommand command = getSubcommand(args[0]);

				if (command != null)
					return command.tabComplete(sender, getLabel(), Arrays.copyOfRange(args, 1, args.length));
			}

			return null;
		}

		private List<String> tabCompleteSubcommands(final CommandSender sender, String param) {
			param = param.toLowerCase();

			final List<String> tab = new ArrayList<>();

			for (final BaseSubCommand subcommand : subCommands) {
				if (hasPerm(subcommand.getPermission()))
					for (final String label : subcommand.getSubLabels())
						if (!label.trim().isEmpty() && label.startsWith(param))
							tab.add(label);

				return tab;
			}
			return new ArrayList<>();
		}

		private BaseSubCommand getSubcommand(final String label) {
			for (final BaseSubCommand command : subCommands) {

				for (final String alias : command.getSubLabels())
					if (alias.equalsIgnoreCase(label))
						return command;
			}

			return null;
		}
	}
}