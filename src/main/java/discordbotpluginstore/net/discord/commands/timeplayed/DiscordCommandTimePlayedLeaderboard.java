package discordbotpluginstore.net.discord.commands.timeplayed;



import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import discordbotpluginstore.net.database.JsonDB;
import discordbotpluginstore.net.database.MCPlayer;
import discordbotpluginstore.net.minecraft.commands.store.StoreUtils;

public class DiscordCommandTimePlayedLeaderboard extends Command {

	
	public DiscordCommandTimePlayedLeaderboard() {
		this.name = "leaderboard";
		this.aliases = new String[] {"top"};
	}
	
	@Override
	protected void execute(CommandEvent event) {
		
		Comparator<MCPlayer> comparator = new Comparator<MCPlayer>() {

			@Override
			public int compare(MCPlayer o1, MCPlayer o2) {
				return (int) (o2.getTimePlayed() - o1.getTimePlayed());
			}
		};
		
		List<MCPlayer> players = JsonDB.database.getCollection(MCPlayer.class);
		
		players.sort(comparator);
		
		String message = "```yaml\nTime Played Leaderboard\n";
		int i = 0;
		int j = players.size()-1;
		
		while (i < 10 && i < j) {
			MCPlayer p = players.get(i);
			
			String rank = StringUtils.rightPad((i+1) + ".", (Integer.toString(i+1).length() + (3 - Integer.toString(i+1).length())));
			String name = StringUtils.rightPad(p.getMinecraftName() + ":", p.getMinecraftName().length() + (16 - (p.getMinecraftName().length())));
			
			message += String.format( "%s %s %s | LastPlayed: %s UTC\n",
					rank,
					name,
					StoreUtils.getTimedPlayedDate(p.getTimePlayed()),
					p.getLastPlayedDate().toLowerCase());
			i++;
		}
		event.reply(message + "```");
	}

}
