package discordbotpluginstore.net.discord.commands.general;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import discordbotpluginstore.net.discord.DiscordBot;
import discordbotpluginstore.net.discord.commands.CommandUtils;

public class DiscordCommandBotTest extends Command{
	
	private String feature = "discord_command_test";
	
	public DiscordCommandBotTest() {
		this.name = DiscordBot.configuration.getFeatures().get(feature).getName();
		this.aliases  = DiscordBot.configuration.getFeatures().get(feature).getAliases();
	}
	
	@Override
	protected void execute(CommandEvent event) {
		if (!CommandUtils.fullUsageCheck(event, feature)) {
			return;
		}
		
		event.reply("You didn't break anything <:feelsokayman:595089254615482388>");
	}
}
