package discordbotpluginstore.net.discord.commands.timeplayed;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import discordbotpluginstore.net.discord.DiscordBot;
import discordbotpluginstore.net.discord.DiscordUtils;
import discordbotpluginstore.net.discord.commands.CommandUtils;

public class DiscordCommandTimePlayed extends Command {

	String feature = "discord_command_time_played";
	
	public DiscordCommandTimePlayed() {
		this.name = DiscordBot.configuration.getFeatures().get("discord_command_time_played").getName();
		this.children = new Command[] {new DiscordCommandTimePlayedLeaderboard(), new DiscordCommandTimePlayedCheck()};
	}
	
	@Override
	protected void execute(CommandEvent event) {
		if (!CommandUtils.fullUsageCheck(event, feature)) {
			return;
		}
		
		DiscordUtils.sendTimedMessaged(event, "Invalid Arguments: !s timeplayed [check, top]", 5000, false);
		
	}
}
