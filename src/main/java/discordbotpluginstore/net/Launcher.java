package discordbotpluginstore.net;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.bukkit.plugin.java.JavaPlugin;
import org.fusesource.jansi.AnsiConsole;

import discordbotpluginstore.net.database.JsonDB;
import discordbotpluginstore.net.discord.DiscordBot;
import discordbotpluginstore.net.discord.DiscordUtils;
import discordbotpluginstore.net.logger.Logger;
import discordbotpluginstore.net.logger.Logger.Level;
import discordbotpluginstore.net.relay.MessageRelay;

public class Launcher extends JavaPlugin {

	/**
	 * User working directory/Current working directory.
	 */
	public static String uwd = System.getProperty("user.dir");
	public static DiscordBot discordBot;
	
	public static ExecutorService pool;
	
	@Override
	public void onEnable() {
		// allows ANSI escape sequences to format console output for loggers. a.k.a. PRETTY COLORS
		AnsiConsole.systemInstall();
		
		// initialize the database.
		JsonDB.init();
		
		// pool for running time checks on people to add currency every minute.
		pool = Executors.newCachedThreadPool();
		
		discordBot = new DiscordBot();
		
		DiscordUtils.sendRelayMessage(DiscordUtils.format(MessageRelay.minecraftPrefix, "bold") + " STORE OPENED <:green_circle:690600732113502239>");
	}
	
	@Override
	public void onDisable() {
		DiscordUtils.sendRelayMessage(DiscordUtils.format(MessageRelay.minecraftPrefix, "bold") + " STORE CLOSED <:red_circle:690600907666358314>");

		try {
			Logger.log(Level.WARN, "Awaiting pool task termination...");
			boolean result = pool.awaitTermination(10, TimeUnit.SECONDS);
			
			if (result) {
				Logger.log(Level.WARN, "Pool terminated successfully.");
			} else {
				Logger.log(Level.WARN, "Pool termination exceeded timeout and force shutdown.");
				pool.shutdownNow();
			}
			
		} catch (InterruptedException e) {
			Logger.log(Level.WARN, "Shutdown thread is interrupted.");
		}
		
		DiscordBot.jda.shutdownNow();
	}
}
