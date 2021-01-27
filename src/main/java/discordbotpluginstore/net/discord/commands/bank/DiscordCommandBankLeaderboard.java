package discordbotpluginstore.net.discord.commands.bank;

import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import discordbotpluginstore.net.database.JsonDB;
import discordbotpluginstore.net.database.MCPlayer;
import discordbotpluginstore.net.minecraft.commands.store.StoreUtils;

public class DiscordCommandBankLeaderboard extends Command {

	public DiscordCommandBankLeaderboard() {
		this.name = "leaderboard";
		this.aliases = new String[] {"top"};
	}
	
	@Override
	protected void execute(CommandEvent event) {
		Comparator<MCPlayer> comparator = new Comparator<MCPlayer>() {

			@Override
			public int compare(MCPlayer o1, MCPlayer o2) {
				int p1Wealth = (o1.getIronCoins()) 
						+ (o1.getGoldCoins() * StoreUtils.GOLD_COIN) 
						+ (o1.getDiamondCoins() * (StoreUtils.DIAMOND_COIN * StoreUtils.DIAMOND_COIN));
				
				int p2Wealth = (o2.getIronCoins()) 
						+ (o2.getGoldCoins() * StoreUtils.GOLD_COIN) 
						+ (o2.getDiamondCoins() * (StoreUtils.DIAMOND_COIN * StoreUtils.DIAMOND_COIN));
				
				return (int) (p2Wealth - p1Wealth);
			}
		};
		
		List<MCPlayer> players = JsonDB.database.getCollection(MCPlayer.class);
		
		players.sort(comparator);
		
		String message = "```yaml\nBank Wealth Leaderboard\n";
		int i = 0;
		int j = players.size()-1;
		
		while (i < 10 && i < j) {
			MCPlayer p = players.get(i);
			
			String rank = StringUtils.rightPad((i+1) + ".", (Integer.toString(i+1).length() + (3 - Integer.toString(i+1).length())));
			String name = StringUtils.rightPad(p.getMinecraftName() + ":", p.getMinecraftName().length() + (17 - (p.getMinecraftName().length())));
			
			message += String.format("%s %s | %s Iron Coins | %s Gold Coins | %s Diamond Coins \n",
					rank,
					name,
					p.getIronCoins(),
					p.getGoldCoins(),
					p.getDiamondCoins());
			i++;
		}
		event.reply(message + "```");
	}
	
	

}
