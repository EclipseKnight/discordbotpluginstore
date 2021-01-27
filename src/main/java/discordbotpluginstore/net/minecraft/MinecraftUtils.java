package discordbotpluginstore.net.minecraft;

import java.util.UUID;

import org.bukkit.Bukkit;

public class MinecraftUtils {

	public static void sendPlayerMessageWithName(String mcname, String message) {
		if (Bukkit.getPlayer(mcname) != null) {
			Bukkit.getPlayer(mcname).sendMessage(message);
		}
		
	}
	
	public static void sendPlayerMessageWithUUID(String uuid, String message) {
		if (Bukkit.getPlayer(UUID.fromString(uuid)) != null ) {
			Bukkit.getPlayer(UUID.fromString(uuid)).sendMessage(message);
		}
	}
	
}
