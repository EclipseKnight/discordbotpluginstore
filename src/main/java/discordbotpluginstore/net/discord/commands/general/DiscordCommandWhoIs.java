package discordbotpluginstore.net.discord.commands.general;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import discordbotpluginstore.net.database.MCPlayer;
import discordbotpluginstore.net.discord.DiscordUtils;
import discordbotpluginstore.net.discord.commands.CommandUtils;
import discordbotpluginstore.net.minecraft.commands.store.StoreUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class DiscordCommandWhoIs extends Command {

	public DiscordCommandWhoIs() {
		this.name = "whois";
	}

	@Override
	protected void execute(CommandEvent event) {
		if (event.getArgs().isEmpty()) {
			DiscordUtils.sendTimedMessaged(event, "Invalid Arguments: !c whois <@user>", 5000, false);
			return;
		}
		
		String discordId = CommandUtils.getIdFromMention(event.getArgs().trim());
		String nickname = event.getArgs();
		
		
		MCPlayer p = null;
			
		if (CommandUtils.isSnowflake(discordId) && CommandUtils.getUserWithDiscordId(discordId) != null) {
			p = CommandUtils.getUserWithDiscordId(discordId);
		}
		
		if (p == null && CommandUtils.getUserWithMinecraftName(nickname) != null) {
			p = CommandUtils.getUserWithMinecraftName(nickname);
		}
		
		if (p == null) {
			DiscordUtils.sendTimedMessaged(event, "Player not found.", 5000, false);
			return;
		}
		
		if (p.getDiscordId() == null) {
			DiscordUtils.sendTimedMessaged(event, "Player info not accessible. Player is either not linked or left the server.", 5000, false);
			//TODO in event of a player leaving provide legacy data. 
			return;
		}
		
		Member m = event.getGuild().getMemberById(p.getDiscordId());
		
		EmbedBuilder eb = new EmbedBuilder();
		eb.setAuthor(m.getUser().getAsTag() + " [MC:" + p.getMinecraftName() + "]", m.getUser().getAvatarUrl(), m.getUser().getAvatarUrl());

		eb.setColor(m.getColor());
		eb.addField("<:timer:804215981328957441> Time Stats. . .", " ", false);
		eb.addField("Time Played:", StoreUtils.getTimedPlayedDate(p.getTimePlayed()), true);
		eb.addField("Join Date:", p.getJoinDate() + " UTC", true);
		eb.addField("Last Played:", p.getLastPlayedDate() + " UTC", true);
		eb.addField("<a:peepoMoneyRain:804218668933709854> Bank Wealth. . .", " ", false);
		eb.addField("Iron Coins:", p.getIronCoins() + "<:IronCoin:804499387727347742>", true);
		eb.addField("Gold Coins:", p.getGoldCoins() + "<:GoldCoin:804500134918225931>", true);
		eb.addField("Diamond Coins:", p.getDiamondCoins() + "<:DiamondCoin:804500157404151868>", true);
		MessageEmbed embed = eb.build();
		
		event.reply(embed);
	}
}
