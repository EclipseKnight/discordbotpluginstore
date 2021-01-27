package discordbotpluginstore.net.discord.commands.bank;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import discordbotpluginstore.net.discord.DiscordBot;
import discordbotpluginstore.net.discord.commands.CommandUtilities;

public class DiscordCommandBank extends Command {

	String feature = "discord_command_bank";
	
	public DiscordCommandBank() {
		this.name = DiscordBot.configuration.getFeatures().get(feature).getName();
		this.children = new Command[] {new DiscordCommandBankCheck(), 
				new DiscordCommandBankGive(), 
				new DiscordCommandBankEat(), 
				new DiscordCommandBankLeaderboard()};
	}

	@Override
	protected void execute(CommandEvent event) {
		
		if (!CommandUtilities.fullUsageCheck(event, feature)) {
			return;
		}
		
		event.reply("Invalid Arguments: !s bank [check, give, top].");
	}
}
