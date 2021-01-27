package discordbotpluginstore.net.tasks;

import java.util.UUID;

import org.bukkit.Bukkit;

import discordbotpluginstore.net.logger.Logger;
import discordbotpluginstore.net.logger.Logger.Level;
import discordbotpluginstore.net.minecraft.MinecraftUtils;
import discordbotpluginstore.net.minecraft.commands.store.StoreUtils;
import net.md_5.bungee.api.ChatColor;

public class CurrencyEarnTask implements Runnable {
	
	long earnRate = 300000;  //5min
	
	int totalIronEarned = 0;
	int totalGoldEarned = 0;
	int totalDiamondEarned = 0;
	
	int ironPayout = 5;
	int goldPayout = 0;
	int diamondPayout = 0;
	
	int numberOfPayouts = 0;
	private String playerId;
	private String playerName;
	
	public CurrencyEarnTask(String playerId) {
		this.playerId = playerId;
	}
	
	@Override
	public void run() {
		playerName = Bukkit.getPlayer(UUID.fromString(playerId)).getName();
		
		long firstTime = 0;
		long lastTime = System.currentTimeMillis();
		long passedTime = 0;
		long unprocessedTime = 0;
		
		boolean run = (Bukkit.getPlayer(UUID.fromString(playerId)) != null) ? true : false;
		
		while (run) {
			
			firstTime = System.currentTimeMillis();
			passedTime = firstTime - lastTime;
			lastTime = firstTime;
			
			unprocessedTime += passedTime;
			
			while (unprocessedTime >= earnRate) {
				unprocessedTime -= earnRate;
				
				// pay the player
				StoreUtils.pay(ironPayout, goldPayout, diamondPayout, playerId);
				// set total earned during session. Task duration.
				addTotalEarned(ironPayout, goldPayout, diamondPayout);
				// increment number of payouts
				numberOfPayouts++;
				
				// notify player of earnings for the first time and every 6th time after that. 
				if (numberOfPayouts % 6 == 0 || numberOfPayouts == 1) {
					String message = String.format("Coins earned this session: | %s Iron Coins | %s Gold Coins | %s Diamond Coins | %s numberOfPayouts",
							totalIronEarned, totalGoldEarned, totalDiamondEarned, numberOfPayouts);
					
					Logger.log(Level.SUCCESS, message);
					MinecraftUtils.sendPlayerMessageWithUUID(playerId, ChatColor.translateAlternateColorCodes('&', "&2" + message));
				}
				
				Logger.log(Level.SUCCESS, String.format("Payed %s a total of | %s Iron Coins | %s Gold Coins | %s Diamond Coins | %s numberOfPayouts",
						Bukkit.getPlayer(UUID.fromString(playerId)).getName(), totalIronEarned, totalGoldEarned, totalDiamondEarned, numberOfPayouts));
			}
			
			// update players total play time.
			if (unprocessedTime >= 30000) {
				StoreUtils.updateTimePlayed(playerId, passedTime);
				
			}
			
			// sleep to reduce load on server. 
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// Thread likely interrupted by a server shutdown or crash. 
				Logger.log(Level.WARN, "Interrupted thread while sleeping.\n" + playerName + " is no longer earning coins.");
				StoreUtils.updateTimePlayed(playerId, unprocessedTime);
				run = false;
			}
			
			if (Thread.interrupted()) {
				Logger.log(Level.INFO, "Interrupted thread. " + playerName + " is no longer earning coins.");
				StoreUtils.updateTimePlayed(playerId, unprocessedTime);
				run = false;
			}
			
			//Check if player is still online.
			run = (Bukkit.getPlayer(UUID.fromString(playerId)) != null) ? true : false;
		}
		
		// Player went offline
		Logger.log(Level.INFO, playerName + " is no longer earning coins.");
		StoreUtils.updateTimePlayed(playerId, unprocessedTime);
		
	}
	
	private void addTotalEarned(int ironCoins, int goldCoins, int diamondCoins) {
		totalIronEarned += ironCoins;
		totalGoldEarned += goldCoins;
		totalDiamondEarned += diamondCoins;
		
		totalIronEarned = ((totalIronEarned - StoreUtils.GOLD_COIN) % StoreUtils.GOLD_COIN) + StoreUtils.GOLD_COIN;
		totalGoldEarned = ((totalIronEarned - StoreUtils.GOLD_COIN) / StoreUtils.GOLD_COIN) + totalGoldEarned;
		totalGoldEarned = ((totalGoldEarned - StoreUtils.DIAMOND_COIN) % StoreUtils.DIAMOND_COIN);
		totalDiamondEarned = ((totalGoldEarned - StoreUtils.DIAMOND_COIN) / StoreUtils.DIAMOND_COIN) + totalDiamondEarned;
		
		if (totalIronEarned < 0) totalIronEarned = 0;
		if (totalGoldEarned < 0) totalGoldEarned = 0;
		if (totalDiamondEarned < 0) totalDiamondEarned = 0;
	}
	
}
