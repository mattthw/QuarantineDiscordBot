package bot.listener;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ReactionPlanter extends ListenerAdapter {

    static Map<String, Double> poopChance = new HashMap<>();

    static {
        poopChance.put("JWalshington", 0.2);
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        try {
            GuildMessageEvent m = new GuildMessageEvent(event);
            steveIsABitch(event, m);
            poopOnFriendsChance(m);

        } catch (Exception e) {
            log.error("Exception when adding reaction to message!", e);
        }
    }

    private void poopOnFriendsChance(GuildMessageEvent m) {
        // poop on friends
        double chance = Math.random();
        double threshold = poopChance.getOrDefault(m.getUsername(), 0.01);
        if (chance <= threshold) {
            log.info(String.format("%n%s rolled a %.2f out of %.2f, I am gonna poop all over him", m.getUsername(), chance, threshold));
            m.getMessage().addReaction("💩").queue();
        } else {
            log.info(String.format("%n%s rolled a %.2f out of %.2f, I will not poop on him", m.getUsername(), chance, threshold));
        }
    }

    private void steveIsABitch(@Nonnull GuildMessageReceivedEvent event, GuildMessageEvent m) {
        // steve is a bitch
        if (m.getChannel().getName().contains("crypto") && m.getAuthor().getName().contains("StockSteve")) {
            log.info(String.format("Adding reaction to message '%s'", m.getMessage().getContentStripped()));
            Emote emote = event.getGuild().getEmotesByName("btc", true).iterator().next();
            m.getMessage().addReaction(emote).queue();
            TextChannel channel = event.getGuild().getTextChannelsByName("crypto", true).get(0);
            channel.sendMessage(m.getMessage()).queue();
        }
    }


}