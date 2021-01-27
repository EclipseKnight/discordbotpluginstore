package discordbotpluginstore.net.discord.commands.store;

import java.util.UUID;

import org.bukkit.Bukkit;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import discordbotpluginstore.net.Launcher;
import discordbotpluginstore.net.database.JsonDB;
import discordbotpluginstore.net.database.MCPlayer;
import discordbotpluginstore.net.discord.DiscordBot;
import discordbotpluginstore.net.discord.commands.CommandUtilities;
import discordbotpluginstore.net.logger.Logger;
import discordbotpluginstore.net.logger.Logger.Level;
import net.dv8tion.jda.api.entities.Role;

public class DiscordCommandLink extends Command {
	
	private String feature = "discord_command_link";
	
	public DiscordCommandLink() {
		this.name = DiscordBot.configuration.getFeatures().get(feature).getName();
		this.aliases  = DiscordBot.configuration.getFeatures().get(feature).getAliases();
	}
	
	@Override
	protected void execute(CommandEvent event) {
		if (!CommandUtilities.fullUsageCheck(event, feature)) {
			return;
		}
		
		// Checks if user is already linked
		String jxQuery = String.format("/.[discordId='%s']", event.getMember().getId());
		if (JsonDB.database.find(jxQuery, MCPlayer.class) != null && JsonDB.database.find(jxQuery, MCPlayer.class).size() > 0) {
			MCPlayer p = JsonDB.database.find(jxQuery, MCPlayer.class).get(0);
			
			event.reply("You are already linked as minecraft:" + p.getMinecraftName() + " & discord:" + event.getMember().getEffectiveName());
			event.reply("To relink, ask Eclipse");
			return;	
		}
		
		String[] args = event.getArgs().split("\\s+");
		
		if (event.getArgs().isBlank()) {
			event.reply("Invalid Arguments: !s link [minecraft name]");
			return;
		}
		
		if (CommandUtilities.getUserWithMinecraftName(args[0]) != null) {
			Logger.log(Level.INFO, "User found.");
			MCPlayer p = JsonDB.database.find(jxQuery, MCPlayer.class).get(0);
			p.setDiscordId(event.getMember().getId());
			JsonDB.database.upsert(p);
			
			event.reply("You are now linked as minecraft:" + p.getMinecraftName() + " & discord:" + event.getMember().getEffectiveName());
			
			if (DiscordBot.configuration.getFeatures().get("discord_command_link").getRoles().get(0) != null) {
				Role role = event.getGuild().getRoleById(DiscordBot.configuration.getFeatures().get("discord_command_link").getRoles().get(0));
				event.getGuild().addRoleToMember(p.getDiscordId(), role).queue();
			}

			
			if (Bukkit.getServer().getPlayer(UUID.fromString(p.getUuid())) != null) {
				Bukkit.getScheduler().callSyncMethod(Launcher.getPlugin(Launcher.class), 
						() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kick " + p.getMinecraftName() + "Account Linked. Rejoin to begin earning coins."));
			}
			
		} else {
			event.reply("Cannot find user in database. Make sure you have logged into the server with the account.");
		}
	}
}
