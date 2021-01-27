package discordbotpluginstore.net.database;

import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;

@Document(collection = "players", schemaVersion = "1.0")
public class MCPlayer {

	@Id
	private String uuid;
	
	private String minecraftName;
	private String discordId;
	
	private int ironCoins;
	private int goldCoins;
	private int diamondCoins;
	
	long timePlayed;
	String joinDate;
	String lastPlayedDate;
	
	boolean sendReceipts;
	
	
	public String getUuid() {
		return uuid;
	}
	
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public String getMinecraftName() {
		return minecraftName;
	}
	
	public void setMinecraftName(String minecraftName) {
		this.minecraftName = minecraftName;
	}
	
	public String getDiscordId() {
		return discordId;
	}
	
	public void setDiscordId(String discordId) {
		this.discordId = discordId;
	}
	
	public int getIronCoins() {
		return ironCoins;
	}
	
	public void setIronCoins(int ironCoins) {
		this.ironCoins = ironCoins;
	}
	
	public int getGoldCoins() {
		return goldCoins;
	}
	
	public void setGoldCoins(int goldCoins) {
		this.goldCoins = goldCoins;
	}
	
	public int getDiamondCoins() {
		return diamondCoins;
	}
	
	public void setDiamondCoins(int diamondCoins) {
		this.diamondCoins = diamondCoins;
	}
	
	public long getTimePlayed() {
		return timePlayed;
	}
	
	public void setTimePlayed(long timePlayed) {
		this.timePlayed = timePlayed;
	}
	
	public String getJoinDate() {
		return joinDate;
	}
	
	public void setJoinDate(String joinDate) {
		this.joinDate = joinDate;
	}
	
	public String getLastPlayedDate() {
		return lastPlayedDate;
	}
	
	public void setLastPlayedDate(String lastPlayedDate) {
		this.lastPlayedDate = lastPlayedDate;
	}
	
	public boolean isSendReceipts() {
		return sendReceipts;
	}
	
	public void setSendReceipts(boolean sendReceipts) {
		this.sendReceipts = sendReceipts;
	}
}
