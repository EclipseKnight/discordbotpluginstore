package discordbotpluginstore.net.discord.commands.restart;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import discordbotpluginstore.net.Launcher;

public class DiscordCommandRestartServer extends Command {

	public DiscordCommandRestartServer() {
		this.name = "server";
	}
	
	@Override
	protected void execute(CommandEvent event) {
		Plugin discordBotPlugin = Bukkit.getPluginManager().getPlugin("MinecraftDiscordBotPlugin");
		Bukkit.getPluginManager().disablePlugin(discordBotPlugin);
		
		Bukkit.getScheduler().callSyncMethod(Launcher.getPlugin(Launcher.class), 
				() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spigot:restart"));
	}
}
