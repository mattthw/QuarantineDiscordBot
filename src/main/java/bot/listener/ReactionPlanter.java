package bot.listener;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ReactionPlanter extends ListenerAdapter {

    public static final String CHANNEL = "crypto";
    static Map<String, Double> poopChance = new HashMap<>();

    static {
        poopChance.put("JWalshington", 0.33);
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        try {
            steveIsTheWorst(event);
            poopOnFriendsChance(event);
        } catch (Exception e) {
            log.error("Exception when adding reaction to message!", e);
        }
    }

    private void poopOnFriendsChance(GuildMessageReceivedEvent event) {
        // poop on friends
        double chance = Math.random();
        String username = event.getMessage().getAuthor().getName();
        double threshold = poopChance.getOrDefault(username, 0.01);
        if (chance <= threshold) {
            log.info(String.format("%n%s rolled a %.2f out of %.2f, I am gonna poop all over him", username, chance, threshold));
//            event.getMessage().addReaction("💩").queue();
            Emote emote = event.getGuild().getEmotesByName("btc", true).iterator().next();
            event.getMessage().addReaction(emote).queue();
        } else {
            log.info(String.format("%n%s rolled a %.2f out of %.2f, I will not poop on him", username, chance, threshold));
        }
    }

    /**
     * Assert superiority as the best bot of the discord server.
     * @param event event
     */
    private void steveIsTheWorst(@Nonnull GuildMessageReceivedEvent event) {
        if (event.getChannel().getName().contains(CHANNEL)
                && event.getAuthor().isBot()
                && !event.getAuthor().getName().equalsIgnoreCase("BabyKoopaBot")) {

            log.info(String.format("Adding reaction to message '%s'", event.getMessage().getContentStripped()));
            Emote emote = event.getGuild().getEmotesByName("btc", true).iterator().next();
            event.getMessage().addReaction(emote).queue();

            if (event.getAuthor().getName().equalsIgnoreCase("StockSamuel")) {
                TextChannel channel = event.getGuild().getTextChannelsByName(CHANNEL, true).get(0);
                channel.sendMessage(event.getMessage()).queue();
            }
        }
    }


}
