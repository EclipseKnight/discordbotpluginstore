package discordbotpluginstore.net.minecraft.commands.store;

import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import discordbotpluginstore.net.database.JsonDB;
import discordbotpluginstore.net.database.MCPlayer;
import discordbotpluginstore.net.discord.DiscordUtils;
import discordbotpluginstore.net.discord.commands.CommandUtils;
import discordbotpluginstore.net.logger.Logger;
import discordbotpluginstore.net.logger.Logger.Level;
import discordbotpluginstore.net.minecraft.MinecraftUtils;
import net.md_5.bungee.api.ChatColor;

public class MinecraftCommandBank implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!command.getName().equals("bank") || !(sender instanceof Player)) {
			return false;
		}
		
		Player player = (Player) sender;
		
		// Check if player can use the command.
		if (!CommandUtils.fullUsageCheck(player.getUniqueId().toString(), "discord_command_bank")) {
			Logger.log(Level.INFO, "Cannot use command.");
			return true;
		}
		
		// Check if enough args.
		if (!(args.length >= 1)) {	
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cInvalid Arguments: /bank [check, give]"));
			return true;
		}
		
		// Checks if the command is the "check" cmd.
		if ("check".equals(args[0])) {
			MCPlayer p = CommandUtils.getUserWithMinecraftName(player.getName());
			
			String msg = String.format("&2Bank: | &7%s Iron Coins&2 | &6%s Gold Coins&2 | &b%s Diamond Coins&2 |",
					p.getIronCoins(), p.getGoldCoins(), p.getDiamondCoins());
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
			return true;
		}
		
		// "give" cmd.
		if ("give".equals(args[0])) {
			if (args.length < 4) {
				String msg = "&cInvalid Arguments: /bank give <mcname> <# of iron coins> <# of gold coins> <# of diamond coins>";
				
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
				
				return true;
			}
			
			int ironCoins = Integer.valueOf(args[2]);
			int goldCoins = Integer.valueOf(args[3]);
			int diamondCoins = Integer.valueOf(args[4]);
			
			
			MCPlayer p1 = CommandUtils.getUserWithMinecraftName(player.getName());
			if ((p1.getIronCoins() < ironCoins) || (p1.getGoldCoins() < goldCoins) || (p1.getDiamondCoins() < diamondCoins)) {
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou do not have enough coins to give that amount. "));
				return true;
			}
			
			if ((ironCoins <= 0 && goldCoins <= 0 && diamondCoins <= 0)) {
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou can't give a person nothing..."));
				return true;
			}
			
			if (CommandUtils.getUserWithMinecraftName(args[1]) != null) {
				MCPlayer p2 = CommandUtils.getUserWithMinecraftName(args[1]);
				
				StoreUtils.pay(ironCoins, goldCoins, diamondCoins, p2.getUuid());
				
				StoreUtils.charge(ironCoins, goldCoins, diamondCoins, p1.getUuid());
				
				// Send sender message.
				String msg1 = String.format("&aTRANSACTION COMPLETE: You sent %s | &7%s Iron Coins&2 | &6%s Gold Coins&2 | &b%s Diamond Coins&2 |",
						p2.getMinecraftName(), ironCoins, goldCoins, diamondCoins);
			
				String receipt = String.format("""
						```yaml
						TRANSACTION RECEIPT: You sent %s | %s Iron Coins | %s Gold Coins | %s Diamond Coins |
						```
						""",
						p2.getMinecraftName(), ironCoins, goldCoins, diamondCoins);
				
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg1));
				DiscordUtils.sendPlayerMessageWithDiscordId(p1.getDiscordId(), receipt);
				StoreUtils.sendReceipt(p1, receipt);
				
				
				// Send recipient message.
				String msg2 = String.format("&aTRANSACTION RECEIPT: %s gave you | &7%s Iron Coins&2 | &6%s Gold Coins&2 | &b%s Diamond Coins&2 |",
						player.getName(), ironCoins, goldCoins, diamondCoins);
				
				String receipt2 = String.format("""
						```yaml
						TRANSACTION RECEIPT: %s gave you | %s Iron Coins | %s Gold Coins | %s Diamond Coins |
						```
						""",
						p1.getMinecraftName(), ironCoins, goldCoins, diamondCoins);
				
				MinecraftUtils.sendPlayerMessageWithName(p2.getMinecraftName(), ChatColor.translateAlternateColorCodes('&', msg2));
				StoreUtils.sendReceipt(p2, receipt2);
				
				return true;
			}
			return true;
		}
		
		// eat cmd
		if ("eat".equals(args[0])) {
			
			MCPlayer p = CommandUtils.getUserWithMinecraftId(player.getUniqueId().toString());
			
			if (p.getIronCoins() <= 0) {
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8You look in your bank and find nothing but dust and a lonesome cobweb in the corner... "
						+ "You have no coins to eat and you slowly starve..."));
			}
			
			player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&7Hungry? Just eat a coin! ...wait ...don't actually ea- &2| &c-1 &7iron coin &2| &a+1 &6food &2|"));
			StoreUtils.charge(1, 0, 0, p.getUuid());
			
			player.setFoodLevel(player.getFoodLevel()+1);
			return true;
		}
		
		if ("top".equals(args[0]) || "leaderboard".equals(args[0])) {
			
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
			
			String message = "&3&n&lBank Wealth Leaderboard&r\n";
			int i = 0;
			int j = players.size()-1;
			
			while (i < 10 && i < j) {
				MCPlayer p = players.get(i);
				
				String rank = StringUtils.rightPad((i+1) + ".", (Integer.toString(i+1).length() + (3 - Integer.toString(i+1).length())));
				String name = StringUtils.rightPad(p.getMinecraftName() + ":", p.getMinecraftName().length() + (16 - (p.getMinecraftName().length())));
				
				message += String.format("&6%s &e%s &2| &7%s Iron Coins &2| &6%s Gold Coins &2| &b%s Diamond Coins &2|&r \n",
						rank,
						name,
						p.getIronCoins(),
						p.getGoldCoins(),
						p.getDiamondCoins());
				i++;
			}
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
			
		}
		
		return true;
	}
}
