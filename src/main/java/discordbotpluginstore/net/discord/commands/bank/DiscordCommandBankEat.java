package discordbotpluginstore.net.discord.commands.bank;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import discordbotpluginstore.net.database.JsonDB;
import discordbotpluginstore.net.database.MCPlayer;
import discordbotpluginstore.net.discord.commands.CommandUtils;

public class DiscordCommandBankEat extends Command {

	String feature = "discord_command_bank";
	
	public DiscordCommandBankEat() {
		this.name = "eat";
	}
	
	@Override
	protected void execute(CommandEvent event) {		
		MCPlayer p = CommandUtils.getUserWithDiscordId(event.getMember().getId());
		
		if (p.getIronCoins() <= 0) {
			event.reply("You look in your bank and find nothing but dust and a lonesome cobweb in the corner... "
					+ "You have no coins to eat and you slowly starve...");
		}
		
		event.reply("Hungry? Just eat a coin! ...wait ...don't actually ea- | -1 iron coin |");
		p.setIronCoins(p.getIronCoins() - 1);
		
		JsonDB.database.upsert(p);
	}

}
