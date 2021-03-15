package discordbotpluginstore.net.discord.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;

import com.jagrosh.jdautilities.command.CommandEvent;

import discordbotpluginstore.net.database.JsonDB;
import discordbotpluginstore.net.database.MCPlayer;
import discordbotpluginstore.net.discord.DiscordBot;
import discordbotpluginstore.net.discord.DiscordUtils;
import discordbotpluginstore.net.logger.Logger;
import discordbotpluginstore.net.logger.Logger.Level;
import net.dv8tion.jda.api.entities.Role;
import net.md_5.bungee.api.ChatColor;

public class CommandUtils {
	
	public static boolean fullUsageCheck(CommandEvent event, String feature) {
		boolean result = true;
		String reply = "";
		
		if (!CommandUtils.isFeatureEnabled(feature)) {
			reply += "Command is disabled. ";
			result = false;
		}
		
		if (!CommandUtils.correctChannel(event, feature)) {
			reply += "Command is disabled in this channel. ";
			result = false;
		}
		
		if (!CommandUtils.canUseCommand(event, feature)) {
			reply += "Command is disabled for your role.";
			result = false;
		}
		
		if (!CommandUtils.isFeatureLinked(event, feature)) {
			reply += "Command requires you to be linked.";
			result = false;
		}
		
		if (!reply.isEmpty()) {
			DiscordUtils.sendTimedMessaged(event, reply, 3000, false);
		}
		
		return result;
	}
	
	public static boolean fullUsageCheck(String uuid, String feature) {
		boolean result = true;
		String reply = "";
		
		if (!CommandUtils.isFeatureEnabled(feature)) {
			reply += "Command is disabled. ";
			result = false;
		}
		
		if (!CommandUtils.isFeatureLinked(uuid, feature)) {
			reply += "Command requires you to be linked.";
			result = false;
		}
		
		if (!reply.isBlank()) {
			Bukkit.getPlayer(UUID.fromString(uuid)).sendMessage(ChatColor.translateAlternateColorCodes('&', "&c "+ reply));
		}
		
		
		return result;
	}
	
	public static boolean isFeatureEnabled(String feature) {
		return DiscordBot.configuration.getFeatures().get(feature).isEnabled();
	}
	
	
	public static boolean isFeatureLinked(String uuid, String feature) {
		if (DiscordBot.configuration.getFeatures().get(feature).isLinked()) {
			
			if (JsonDB.database.findById(uuid, MCPlayer.class) == null) {
				return false;
			}
			
			if (JsonDB.database.findById(uuid, MCPlayer.class).getDiscordId() == null) {
				return false;
			}
		}
		
		return true;
	}
	
	public static boolean isFeatureLinked(CommandEvent event, String feature) {
		if (DiscordBot.configuration.getFeatures().get(feature).isLinked()) {
			String jxQuery = String.format("/.[discordId='%s']", event.getMember().getId());
			
			if (JsonDB.database.find(jxQuery, MCPlayer.class) == null) {
				return false;
			}
		}

		return true;
	}
	
	public static boolean correctChannel(CommandEvent event, String feature) {
		List<String> channels = DiscordBot.configuration.getFeatures().get(feature).getChannels();
		
		if (channels == null || channels.get(0) == null) {
			return true;
		}
		
		if (channels.contains(event.getChannel().getId())) {
			return true;
		}
		
		return false;
	}
	
	public static boolean canUseCommand(CommandEvent event, String feature) {
		
		List<String> cmdRoles = DiscordBot.configuration.getFeatures().get(feature).getRoles();
		
		if (cmdRoles == null || cmdRoles.get(0) == null) {
			return true;
		}
		
		List<String> pRoles = new ArrayList<>();
		
		for (Role r : event.getMember().getRoles()) {
			pRoles.add(r.getId());
		}
		
		
		for (String pRole: pRoles) {
			for (String cmdRole: cmdRoles) {
				if (pRole.equals(cmdRole)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public static MCPlayer getUserWithDiscordId(String id) {
		if (id == null) {
			return null;
		}
		
		String jxQuery = String.format("/.[discordId='%s']", id);
		List<MCPlayer> players = JsonDB.database.find(jxQuery, MCPlayer.class);
		
		if (!players.isEmpty() && players != null) {
			return players.get(0);
		}
		
		return null;
	}
	
	public static MCPlayer getUserWithDiscordName(String effname) {
		if (effname == null) {
			return null;
		}
		
		effname = effname.toLowerCase();
		String jxQuery = String.format("/.[translate(discordEffName, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='%s']", effname);
		List<MCPlayer> players = JsonDB.database.find(jxQuery, MCPlayer.class);
		
		if (!players.isEmpty() && players != null) {
			return players.get(0);
		}
		
		return null;
	}
	
	public static MCPlayer getUserWithMinecraftName(String mcname) {
		if (mcname == null) {
			return null;
		}
		
		mcname = mcname.toLowerCase();
		String jxQuery = String.format("/.[translate(minecraftName, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='%s']", mcname);
		List<MCPlayer> players = JsonDB.database.find(jxQuery, MCPlayer.class);
		
		if (!players.isEmpty() && players != null) {
			return players.get(0);
		}
		
		return null;
	}
	
	public static MCPlayer getUserWithMinecraftId(String uuid) {
		MCPlayer p = JsonDB.database.findById(uuid, MCPlayer.class);
		
		if (p != null) {
			return p;
		}
		
		return null;
	}
	
	public static String getIdFromMention(String mention) {
		return mention.replaceAll("[\\\\<>@#&!]", "");
	}
	
	public static boolean isValidSnowflake(String input) {
		if (input.isEmpty()) {
			return false;
		}
		
        try {
            if (!input.startsWith("-")) // if not negative -> parse unsigned
                Long.parseUnsignedLong(input);
            else // if negative -> parse normal
                Long.parseLong(input);
        } catch (NumberFormatException ex) {
        	Logger.log(Level.WARN, "The specified ID is not a valid snowflake (%s). Expecting a valid long value!" + " ID:" + input);
            return false;
        }
        return true;
	}
}
