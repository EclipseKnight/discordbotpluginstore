package discordbotpluginstore.net.discord.commands.restart;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import discordbotpluginstore.net.discord.DiscordBot;
import discordbotpluginstore.net.discord.commands.CommandUtilities;

public class DiscordCommandRestart extends Command{

	private String feature = "discord_command_restart";
	
	public DiscordCommandRestart() {
		this.name = DiscordBot.configuration.getFeatures().get(feature).getName();
		this.children = new Command[] {new DiscordCommandRestartServer(), new DiscordCommandRestartBot()};
	}
	
	@Override
	protected void execute(CommandEvent event) {
		
		if (!CommandUtilities.fullUsageCheck(event, feature)) {
			return;
		}
		
		event.reply("Invalid Arguments: restart [server, bot]");
	}
}
