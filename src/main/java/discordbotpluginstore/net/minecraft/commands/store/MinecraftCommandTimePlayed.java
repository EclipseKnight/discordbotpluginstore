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
import discordbotpluginstore.net.discord.commands.CommandUtilities;
import discordbotpluginstore.net.logger.Logger;
import discordbotpluginstore.net.logger.Logger.Level;
import net.md_5.bungee.api.ChatColor;

public class MinecraftCommandTimePlayed implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!command.getName().equals("timeplayed") || !(sender instanceof Player)) {
			return false;
		}	
		
		Player player = (Player) sender;
		
		if (!CommandUtilities.fullUsageCheck(player.getUniqueId().toString(), "discord_command_time_played")) {
			Logger.log(Level.INFO, "Cannot use command.");
			return true;
		}
		
		// Check if enough args.
		if (!(args.length >= 1)) {	
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cInvalid Arguments: /timeplayed [check, top]"));
			return true;
		}
		
		if ("check".equals(args[0])) {
			
			// check target timeplayed
			if (args.length > 1 && CommandUtilities.getUserWithMinecraftName(args[1]) != null) {
				MCPlayer p = CommandUtilities.getUserWithMinecraftName(args[1]);
				String msg = String.format("&6%s: &e%s&r \n&6JoinDate: &e%s&r \n&6LastPlayed: &e%s&r", 
						p.getMinecraftName(),
						StoreUtils.getTimedPlayedDate(p.getTimePlayed()),
						p.getJoinDate(),
						p.getTimePlayed());
				
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
				return true;
			}
			
			// check self timeplayed
			MCPlayer p = CommandUtilities.getUserWithMinecraftId(player.getUniqueId().toString());
			String msg = String.format("&6%s: &e%s&r \n&6JoinDate: &e%s&r \n&6LastPlayed: &e%s&r", 
					p.getMinecraftName(),
					StoreUtils.getTimedPlayedDate(p.getTimePlayed()),
					p.getJoinDate(),
					p.getTimePlayed());
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
			return true;
		}
		
		if ("top".equals(args[0])) {
			Comparator<MCPlayer> comparator = new Comparator<MCPlayer>() {

				@Override
				public int compare(MCPlayer o1, MCPlayer o2) {
					return (int) (o2.getTimePlayed() - o1.getTimePlayed());
				}
			};
			
			List<MCPlayer> players = JsonDB.database.getCollection(MCPlayer.class);
			
			players.sort(comparator);
			
			String message = "&3&n&lTime Played Leaderboard&r\n";
			int i = 0;
			int j = players.size()-1;
			
			while (i < 10 && i < j) {
				MCPlayer p = players.get(i);
				
				String rank = StringUtils.rightPad((i+1) + ".", (Integer.toString(i+1).length() + (3 - Integer.toString(i+1).length())));
				String name = StringUtils.rightPad(p.getMinecraftName() + ":", p.getMinecraftName().length() + (16 - (p.getMinecraftName().length())));
			
				message += String.format( "&6%s &3%s: &e%s&r &2| &3LastPlayed: &e%s&r\n",
						rank,
						name,
						StoreUtils.getTimedPlayedDate(p.getTimePlayed()),
						p.getLastPlayedDate().toLowerCase());
				i++;
			}
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
		}
		
		return true;
	}
	
}
