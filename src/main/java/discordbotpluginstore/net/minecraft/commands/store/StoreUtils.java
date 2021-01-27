package discordbotpluginstore.net.minecraft.commands.store;

import java.time.LocalDateTime;
import java.time.ZoneId;

import discordbotpluginstore.net.database.JsonDB;
import discordbotpluginstore.net.database.MCPlayer;
import discordbotpluginstore.net.discord.DiscordUtils;

public class StoreUtils {

	public final static int GOLD_COIN = 100; // 100 iron coins = 1 gold coin
	public final static int DIAMOND_COIN = 100; // 100 gold coins = 1 diamond coin
	
	public static void pay(int ironCoins, int goldCoins, int diamondCoins, String playerId) {
		MCPlayer p = JsonDB.database.findById(playerId, MCPlayer.class);
		
		int totalDiamondCoins = p.getDiamondCoins() + diamondCoins;
		int totalGoldCoins = p.getGoldCoins() + goldCoins;
		int totalIronCoins = p.getIronCoins() + ironCoins;
		
		// remaining iron coins after auto conversion. every 1000 coins after 1000, turns into a gold coin.
		int finalIronCoins = ((totalIronCoins - GOLD_COIN) % GOLD_COIN) + GOLD_COIN; 
		
		// gold coins converted from iron coins plus total gold coins.
		int finalGoldCoins = ((totalIronCoins - GOLD_COIN) / GOLD_COIN) + totalGoldCoins; 
		
		// diamond coins converted from gold coins plus total diamond coins.
		int finalDiamondCoins = (finalGoldCoins- DIAMOND_COIN) / DIAMOND_COIN + totalDiamondCoins;
		
		if (finalIronCoins < 0) finalIronCoins = 0;
		if (finalGoldCoins < 0) finalGoldCoins = 0;
		if (finalDiamondCoins < 0) finalDiamondCoins = 0;
		
		p.setIronCoins(finalIronCoins);
		p.setGoldCoins(finalGoldCoins);
		p.setDiamondCoins(finalDiamondCoins);
		
		JsonDB.database.upsert(p);
	}
	
	public static void charge(int ironCoins, int goldCoins, int diamondCoins, String playerId) {
		MCPlayer p = JsonDB.database.findById(playerId, MCPlayer.class);
		
		p.setIronCoins(p.getIronCoins() - ironCoins);
		p.setGoldCoins(p.getGoldCoins() - goldCoins);
		p.setDiamondCoins(p.getDiamondCoins() - diamondCoins);
		
		JsonDB.database.upsert(p);
	}
	
	public static void sendReceipt(MCPlayer player, String message) {
		if (player.isSendReceipts()) {
			DiscordUtils.sendPlayerMessageWithDiscordId(player.getDiscordId(), message);
		}
	}
	
	public static void updateTimePlayed(String playerId, long timePassed) {
		MCPlayer p = JsonDB.database.findById(playerId, MCPlayer.class);
		p.setTimePlayed(p.getTimePlayed() + timePassed);
		
		JsonDB.database.upsert(p);
	}
	
	//Auxiliary methods
	
	public static String getCurrentDateTime() {
		LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
		int year = now.getYear();
		String month = now.getMonth().name();
		int day = now.getDayOfMonth();
		int hour = now.getHour();
		int minute = now.getMinute();
		int second = now.getSecond();
		
		return String.format("%s-%s-%s @ %sh:%sm:%ss", year, month, day, hour, minute, second);
		
	}
	
	public static String getTimedPlayedDate(long timePlayed) {
		long seconds = timePlayed / 1000 % 60;
		long minutes = timePlayed / (60 * 1000) % 60;
		long hours = timePlayed / (60 * 60 * 1000) % 24;
		long days = timePlayed / (24 * 60 * 60 * 1000);
		
		return String.format("%sd:%sh:%sm:%ss", days, hours, minutes, seconds);
	}
}
