package discordbotpluginstore.net.discord.commands;

import java.util.function.Consumer;

import com.jagrosh.jdautilities.command.CommandEvent;

import discordbotpluginstore.net.discord.DiscordBot;
import discordbotpluginstore.net.discord.DiscordUtils;

public class DiscordCommandHelp implements Consumer<CommandEvent> {

	@Override
	public void accept(CommandEvent event) {
		String[] commands = new String[] {
				"discord_command_test",
				"discord_command_restart",
				"discord_command_link",
				"discord_command_time_played",
				"discord_command_bank"
				};
		
		String message = "Commands you can use:\n";
		
		for (String c : commands) {
			
			if (canUse(event, c)) {
				message += DiscordBot.prefix
						+ DiscordBot.configuration.getFeatures().get(c).getDescription() + "\n";
			}
		}
		
		DiscordUtils.sendMessage(event, message, true);
	}
	
	private boolean canUse(CommandEvent event, String feature) {
		boolean result = true;
		
		if (!CommandUtils.isFeatureEnabled(feature)) {
			result = false;
		}
		
		if (!CommandUtils.canUseCommand(event, feature)) {
			result = false;
		}
		
		return result;
	}
}
