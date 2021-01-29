package discordbotpluginstore.net.discord.commands.store;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import discordbotpluginstore.net.database.JsonDB;
import discordbotpluginstore.net.database.MCPlayer;
import discordbotpluginstore.net.discord.DiscordUtils;
import discordbotpluginstore.net.discord.commands.CommandUtils;

public class DiscordCommandReceipt extends Command {

	public DiscordCommandReceipt() {
		this.name = "receipt";
	}
	
	@Override
	protected void execute(CommandEvent event) {
		
		if (event.getArgs().isBlank()) {
			DiscordUtils.sendTimedMessaged(event, "Invalid Arguments: receipt [true, false]", 5000, false);
		}
		
		if ("true".equals(event.getArgs().trim())) {
			MCPlayer p = CommandUtils.getUserWithDiscordId(event.getMember().getId());
			
			p.setSendReceipts(true);
			JsonDB.database.upsert(p);
			
			DiscordUtils.sendTimedMessaged(event, "You will now receive transaction receipts.", 5000, false);
		}
		
		if ("false".equals(event.getArgs().trim())) {
			MCPlayer p = CommandUtils.getUserWithDiscordId(event.getMember().getId());
			
			p.setSendReceipts(false);
			JsonDB.database.upsert(p);
			
			DiscordUtils.sendTimedMessaged(event, "You will no longer receive transaction receipts.", 5000, false);
		}
	}

}
