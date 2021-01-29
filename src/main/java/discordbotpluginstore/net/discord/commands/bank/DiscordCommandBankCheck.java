package discordbotpluginstore.net.discord.commands.bank;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import discordbotpluginstore.net.database.MCPlayer;
import discordbotpluginstore.net.discord.commands.CommandUtils;

public class DiscordCommandBankCheck extends Command {
	
	public DiscordCommandBankCheck() {
		this.name = "check";
	}
	
	@Override
	protected void execute(CommandEvent event) {
		MCPlayer p = CommandUtils.getUserWithDiscordId(event.getMember().getId());
		
		String msg = String.format("""
				```yaml
				Bank: | %s Iron Coins | %s Gold Coins | %s Diamond Coins |
				```
				""", p.getIronCoins(), p.getGoldCoins(), p.getDiamondCoins());
		event.reply(msg);
	}
}
