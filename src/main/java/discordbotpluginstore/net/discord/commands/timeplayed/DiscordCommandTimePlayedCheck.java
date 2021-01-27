package discordbotpluginstore.net.discord.commands.timeplayed;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import discordbotpluginstore.net.database.MCPlayer;
import discordbotpluginstore.net.discord.commands.CommandUtilities;
import discordbotpluginstore.net.minecraft.commands.store.StoreUtils;

public class DiscordCommandTimePlayedCheck extends Command {

	String feature = "discord_command_time_played";
	
	public DiscordCommandTimePlayedCheck() {
		this.name = "check";
	}
	
	@Override
	protected void execute(CommandEvent event) {	
		
		if (!event.getArgs().isBlank()) {
			String discordId = CommandUtilities.getIdFromMention(event.getArgs().trim());
			
			// If player attempts to check another user. 
			if (CommandUtilities.getUserWithDiscordId(discordId) != null) {
				MCPlayer p = CommandUtilities.getUserWithDiscordId(discordId);
				
				String msg = String.format("""
						```yaml
						%s: %s
						JoinDate: %s
						LastPlayed: %s
						```
						""",
						p.getMinecraftName(),
						StoreUtils.getTimedPlayedDate(p.getTimePlayed()),
						p.getJoinDate(),
						p.getLastPlayedDate());
				event.reply(msg);
				return;
			}
		}
		
		// if attempt fails or user wants to check self.
		MCPlayer p = CommandUtilities.getUserWithDiscordId(event.getAuthor().getId());
		String msg = String.format("""
				```yaml
				%s: %s
				JoinDate: %s
				LastPlayed: %s
				```
				""",
				p.getMinecraftName(),
				StoreUtils.getTimedPlayedDate(p.getTimePlayed()),
				p.getJoinDate(),
				p.getLastPlayedDate());
		event.reply(msg);
		
	}

}
