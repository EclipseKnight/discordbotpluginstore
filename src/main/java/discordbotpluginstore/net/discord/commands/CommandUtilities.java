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
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.md_5.bungee.api.ChatColor;

public class CommandUtilities {
	
	public static boolean fullUsageCheck(CommandEvent event, String feature) {
		boolean result = true;
		String reply = "";
		
		if (!CommandUtilities.isFeatureEnabled(feature)) {
			reply += "Command is disabled. ";
			result = false;
		}
		
		if (!CommandUtilities.correctChannel(event, feature)) {
			reply += "Command is disabled in this channel. ";
			result = false;
		}
		
		if (!CommandUtilities.canUseCommand(event, feature)) {
			reply += "Command is disabled for your role.";
			result = false;
		}
		
		if (!CommandUtilities.isFeatureLinked(event, feature)) {
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
		
		if (!CommandUtilities.isFeatureEnabled(feature)) {
			reply += "Command is disabled. ";
			result = false;
		}
		
		if (!CommandUtilities.isFeatureLinked(uuid, feature)) {
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
		String jxQuery = String.format("/.[discordId='%s']", id);
		List<MCPlayer> players = JsonDB.database.find(jxQuery, MCPlayer.class);
		
		if (!players.isEmpty() && players != null) {
			return players.get(0);
		}
		
		return null;
	}
	
	public static MCPlayer getUserWithDiscordTag(String name) {
		Guild g = DiscordBot.jda.getGuildById(DiscordBot.configuration.getGuildId());
		
		Member m;
		
		try {
			m = g.getMemberByTag(name);
		} catch(IllegalArgumentException e) {
			return null;
		}
		
		
		String jxQuery = String.format("/.[discordId='%s']", m.getId());
		List<MCPlayer> players = JsonDB.database.find(jxQuery, MCPlayer.class);
		
		if (!players.isEmpty() && players != null) {
			return players.get(0);
		}
		
		return null;
	}
	
	public static MCPlayer getUserWithMinecraftName(String mcname) {
		String jxQuery = String.format("/.[minecraftName='%s']", mcname);
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
}
