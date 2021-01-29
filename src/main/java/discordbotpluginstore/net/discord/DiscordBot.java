package discordbotpluginstore.net.discord;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.security.auth.login.LoginException;

import org.bukkit.Bukkit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;

import discordbotpluginstore.net.Launcher;
import discordbotpluginstore.net.discord.commands.DiscordCommandHelp;
import discordbotpluginstore.net.discord.commands.bank.DiscordCommandBank;
import discordbotpluginstore.net.discord.commands.config.DiscordCommandConfiguration;
import discordbotpluginstore.net.discord.commands.general.DiscordCommandBotTest;
import discordbotpluginstore.net.discord.commands.general.DiscordCommandWhoIs;
import discordbotpluginstore.net.discord.commands.restart.DiscordCommandRestart;
import discordbotpluginstore.net.discord.commands.store.DiscordCommandLink;
import discordbotpluginstore.net.discord.commands.store.DiscordCommandReceipt;
import discordbotpluginstore.net.discord.commands.timeplayed.DiscordCommandTimePlayed;
import discordbotpluginstore.net.logger.Logger;
import discordbotpluginstore.net.logger.Logger.Level;
import discordbotpluginstore.net.minecraft.commands.store.MinecraftCommandBank;
import discordbotpluginstore.net.minecraft.commands.store.MinecraftCommandTimePlayed;
import discordbotpluginstore.net.relay.MessageRelay;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class DiscordBot {

	/**
	 * Holds the Bot Configuration
	 */
	public static DiscordConfiguration configuration;
	
	/**
	 * JDA API
	 */
	public static JDA jda = null;
	
	/**
	 * Command builder jdautilities
	 */
	private CommandClientBuilder builder;
	
	/**
	 * intents of the discord bot
	 */
	private ArrayList<GatewayIntent> intents = new ArrayList<>();
	
	public static String prefix = "!s ";
	
	public DiscordBot() {
		// Load Configuration
		loadConfiguration();
		
		 if (configuration.getApi().get("discord_client_id") == null 
	        		|| configuration.getApi().get("discord_client_token") == null
	        		|| configuration.getOwnerId() == null) {
	        	Logger.log(Level.FATAL, "Discord id or token or owner id is not set. Check the discordbot.yaml keys: discord_client_id, discord_client_token, and owner_id values.");
	        	Logger.log(Level.FATAL, "Exiting...");
	        	System.exit(1);
	        }
		
		
		for (GatewayIntent gt : GatewayIntent.values()) {
			intents.add(0, gt);
		}
		
		try {
			jda = JDABuilder.create(configuration.getApi().get("discord_client_token"), intents).build().awaitReady();
		} catch (LoginException | InterruptedException e) {
			System.out.println("Discord bot failed to initialize: " + e.toString());
			return;
		}
	
		// create a command builder to register commands
		builder = new CommandClientBuilder();
		
		// Sets the owner id (commands can be set to owner/co-owner only)
		builder.setOwnerId(configuration.getOwnerId());
		
		// Sets the co owners. #setCoOwnerIds takes a variable argument so converting from List to string[] is necessary.
		builder.setCoOwnerIds(configuration.getCoOwnerIds().toArray(new String[configuration.getCoOwnerIds().size()]));
		
		// Sets the command prefix (e.g. !c isLive Name)
		builder.setPrefix(prefix);
		
		// Sets the default help command
		builder.setHelpConsumer(new DiscordCommandHelp());
		
		// Register the commands to the builder.
		registerCommands(builder);
		
	    // Register Spigot Events.
		registerEvents();
		
		// For displaying currently live streamer as a status. Not currently working
		//TODO implement
		jda.getPresence().setActivity(Activity.of(ActivityType.DEFAULT, "!s help"));
		jda.getSelfUser().getManager().setName(configuration.getBot().get("name"));
	}
	
	
	
	private void registerCommands(CommandClientBuilder builder) {
		// adds command to builder
		builder.addCommands(
				new DiscordCommandBotTest(), 
				new DiscordCommandRestart(),
				new DiscordCommandConfiguration(),
				new DiscordCommandLink(),
				new DiscordCommandTimePlayed(),
				new DiscordCommandBank(),
				new DiscordCommandReceipt(),
				new DiscordCommandWhoIs()
				);
		
		// built command client
		CommandClient cmdClient = builder.build();
		
		// adds command client to listener. Commands use events will now be checked for on the discord server.
		jda.addEventListener(cmdClient);
		
		Launcher.getPlugin(Launcher.class).getCommand("bank").setExecutor(new MinecraftCommandBank());
		Launcher.getPlugin(Launcher.class).getCommand("timeplayed").setExecutor(new MinecraftCommandTimePlayed());
	}
	
	private void registerEvents() {
		MessageRelay mr = new MessageRelay();
		jda.addEventListener(mr);
		Bukkit.getServer().getPluginManager().registerEvents(mr, Launcher.getPlugin(Launcher.class));
	}
	
	 /**
     * Load the Configuration
     */
    public static void loadConfiguration() {
    	
    	File discordBotConfig = new File(Launcher.uwd + File.separator + "plugins" + File.separator + "discordbotpluginstore" + File.separator + "configs" + File.separator + "discordbot.yaml");
    	
    	if (new File(Launcher.uwd + File.separator + "plugins"+ File.separator + "discordbotpluginstore" + File.separator + "configs").mkdirs()) {
    		Logger.log(Level.WARN, "Generating configs directory...");
    	}
    	
    	try {
    		
    		if (!discordBotConfig.exists()) {
        		generateConfig(discordBotConfig);
        	}
        	
            InputStream is = new BufferedInputStream(new FileInputStream(discordBotConfig));
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            configuration = mapper.readValue(is, DiscordConfiguration.class);
            
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.log(Level.FATAL, "Unable to load configuration... Exiting.");
            System.exit(1);
        }
    	
    }
    
    /**
     * Generates config file.
     */
    public static void generateConfig(File dir) {
    	
        try {
        	Logger.log(Level.WARN, "Missing discordbot.yaml. Generating new config...");
        	ClassLoader classloader = DiscordBot.class.getClassLoader();
        	
        	// copies twitchbot.yaml template to current working directory.
        	InputStream original = classloader.getResourceAsStream("discordbot.yaml");
            Path copy = Paths.get(dir.toURI());
          
            Logger.log(Level.WARN, "Generating config at " + copy);
            Files.copy(original, copy);
            
		} catch (IOException e) {
			e.printStackTrace();
			Logger.log(Level.ERROR, "Failed to generate discordbot.yaml...");
		}
    }
}
