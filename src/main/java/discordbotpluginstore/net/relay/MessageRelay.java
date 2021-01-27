package discordbotpluginstore.net.relay;

import java.net.MalformedURLException;
import java.net.URL;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import discordbotpluginstore.net.Launcher;
import discordbotpluginstore.net.database.JsonDB;
import discordbotpluginstore.net.database.MCPlayer;
import discordbotpluginstore.net.logger.Logger;
import discordbotpluginstore.net.logger.Logger.Level;
import discordbotpluginstore.net.minecraft.commands.store.StoreUtils;
import discordbotpluginstore.net.tasks.CurrencyEarnTask;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.md_5.bungee.api.ChatColor;

public class MessageRelay extends ListenerAdapter implements Listener {

	public static String discordPrefix = "Discord:";
	public static String minecraftPrefix = "Minecraft:";
	
	@EventHandler
	public void onPlayerLogin(PlayerJoinEvent e) {
		
		// If players collection doesn't exist make one
		if (!JsonDB.database.collectionExists(MCPlayer.class)) {
			JsonDB.database.createCollection(MCPlayer.class);
		}
		
		// Check if player exists and generate if not. 
		if (JsonDB.database.findById(e.getPlayer().getUniqueId().toString(), MCPlayer.class) == null) {
			Logger.log(Level.INFO, "New player entry made...");
			MCPlayer p = new MCPlayer();
			p.setUuid(e.getPlayer().getUniqueId().toString());
			p.setMinecraftName(e.getPlayer().getName());
			p.setDiscordId(null);
			p.setJoinDate(StoreUtils.getCurrentDateTime());
			p.setLastPlayedDate(StoreUtils.getCurrentDateTime());
			p.setTimePlayed(0);
			p.setIronCoins(0);
			p.setGoldCoins(0);
			p.setDiamondCoins(0);
			p.setSendReceipts(true);
			
			JsonDB.database.upsert(p);
		}
		
		
		MCPlayer p = JsonDB.database.findById(e.getPlayer().getUniqueId().toString(), MCPlayer.class);
		
		// Update lastlogin date of player.
		p.setLastPlayedDate(StoreUtils.getCurrentDateTime());
		JsonDB.database.upsert(p);
		
		// Check if discord and mincraft accounts are linked.
		if (p.getDiscordId() != null) {
			Logger.log(Level.INFO, "Player linked.");
			String message = "&2Discord Account Linked. Earning coins every 5 minutes.";
			e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', message));
			Launcher.pool.execute(new CurrencyEarnTask(p.getUuid()));
			
		} else {
			
			Logger.log(Level.INFO, "Player not linked.");
			try {
				URL invite = new URL("https://discord.gg/wEmD7hSZBf");
				
				String message = "&4Discord Account Not Linked.";
				String message2 = "&4To utilize the server store and earn coins, join the discord server:&n" 
				+ invite 
				+ "&r&4 and use command \"!s link " + e.getPlayer().getName() +" \" to link accounts.";
				
				e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', message));
				e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', message2));
				
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	@EventHandler
	public void onPlayerLogout(PlayerQuitEvent e) {
		MCPlayer p = JsonDB.database.findById(e.getPlayer().getUniqueId().toString(), MCPlayer.class);
		p.setLastPlayedDate(StoreUtils.getCurrentDateTime());
	}
}
