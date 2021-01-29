package discordbotpluginstore.net.discord.commands.bank;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import discordbotpluginstore.net.database.MCPlayer;
import discordbotpluginstore.net.discord.DiscordUtils;
import discordbotpluginstore.net.discord.commands.CommandUtils;
import discordbotpluginstore.net.minecraft.MinecraftUtils;
import discordbotpluginstore.net.minecraft.commands.store.StoreUtils;
import net.md_5.bungee.api.ChatColor;

public class DiscordCommandBankGive extends Command {

	String feature = "discord_command_bank";
	
	public DiscordCommandBankGive() {
		this.name = "give";
	}
	
	@Override
	protected void execute(CommandEvent event) {
		
		String[] args = event.getArgs().split("\\s+");
		
		if (args.length < 4) {
			DiscordUtils.sendTimedMessaged(event, "Invalid Arguments: !s bank give <mcname or discordname#1234> <# of iron coins> <# of gold coins> <# of diamond coins>", 6000, false);
			return; 
		}
		
		// Sent amounts
		int ironCoins = Integer.valueOf(args[1]);
		int goldCoins = Integer.valueOf(args[2]);
		int diamondCoins = Integer.valueOf(args[3]);
		
		// Get player and check if they can afford to send set amount.
		MCPlayer p1 = CommandUtils.getUserWithDiscordId(event.getMember().getId());
		if ((p1.getIronCoins() < ironCoins) || (p1.getGoldCoins() < goldCoins) || (p1.getDiamondCoins() < diamondCoins)) {
			DiscordUtils.sendTimedMessaged(event, "You do not have enough coins to give that amount. ", 5000, false);
			return;
		}
		
		if ((ironCoins <= 0 && goldCoins <= 0 && diamondCoins <= 0)) {
			DiscordUtils.sendTimedMessaged(event, "You can't give a person nothing... ", 5000, false);
			return;
		}
		
		String discordId = CommandUtils.getIdFromMention(args[0]);
		
		// discord @
		if (CommandUtils.getUserWithDiscordId(discordId) != null) {
			MCPlayer p2 = CommandUtils.getUserWithDiscordId(discordId);
			
			StoreUtils.pay(ironCoins, goldCoins, diamondCoins, p2.getUuid());
			
			StoreUtils.charge(ironCoins, goldCoins, diamondCoins, p1.getUuid());
			
			String msg = String.format("""
					```yaml
					TRANSACTION COMPLETE: You sent %s | %s Iron Coins | %s Gold Coins | %s Diamond Coins |
					```
					""",
					p2.getMinecraftName(), ironCoins, goldCoins, diamondCoins);
			
			String msg2 = String.format("""
					```yaml
					TRANSACTION RECEIPT: %s gave you | %s Iron Coins | %s Gold Coins | %s Diamond Coins |
					```
					""",
					p1.getMinecraftName(), ironCoins, goldCoins, diamondCoins);
			
			// Verification message and send self receipt. 
			DiscordUtils.sendTimedMessaged(event, msg, 5000, false);
			StoreUtils.sendReceipt(p1, msg.replace("COMPLETE", "RECEIPT"));
			
			// Send receiver a receipt.
			StoreUtils.sendReceipt(p2, msg2);
			return;
		}
		
		// Minecraft username
		if (CommandUtils.getUserWithMinecraftName(args[0]) != null) {
			MCPlayer p2 = CommandUtils.getUserWithMinecraftName(args[0]);
			
			StoreUtils.pay(ironCoins, goldCoins, diamondCoins, p2.getUuid());
			StoreUtils.charge(ironCoins, goldCoins, diamondCoins, p1.getUuid());
			
			
			// sender receipt
			String senderReceipt = String.format("""
					```yaml
					TRANSACTION COMPLETE: You sent %s | %s Iron Coins | %s Gold Coins | %s Diamond Coins |
					```
					""",
					p2.getMinecraftName(), ironCoins, goldCoins, diamondCoins);
			
			// Verification message and send self receipt.
			DiscordUtils.sendTimedMessaged(event, senderReceipt, 5000, false);
			StoreUtils.sendReceipt(p1, senderReceipt.replace("COMPLETE", "RECEIPT"));
			
			// Discord private receipt
			String receiverDiscordReceipt = String.format("""
					```yaml
					TRANSACTION RECEIPT: %s gave you | %s Iron Coins | %s Gold Coins | %s Diamond Coins |
					```
					""",
					p1.getMinecraftName(), ironCoins, goldCoins, diamondCoins);
			
			DiscordUtils.sendPlayerMessageWithDiscordId(p2.getDiscordId(), receiverDiscordReceipt);
			StoreUtils.sendReceipt(p2, receiverDiscordReceipt);
			
			// In game minecraft formatted receipt.
			String receiverMinecraftReceipt = String.format("&aTRANSACTION RECEIPT: %s gave you | &7%s Iron Coins&2 | &6%s Gold Coins&2 | &b%s Diamond Coins&2 |",
					p1.getMinecraftName(), ironCoins, goldCoins, diamondCoins);
			
			MinecraftUtils.sendPlayerMessageWithName(p2.getMinecraftName(), ChatColor.translateAlternateColorCodes('&', receiverMinecraftReceipt));
			return;
		}
	}
	
	

}
