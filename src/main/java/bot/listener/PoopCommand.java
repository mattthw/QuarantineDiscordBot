package bot.listener;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.regex.Pattern;

@Slf4j
public class PoopCommand extends ListenerAdapter {
    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        poopOnFriendsChance(event);
    }

    private void poopOnFriendsChance(GuildMessageReceivedEvent event) {
        String input = event.getMessage().getContentRaw();
        String[] args = input.split(" ");
        boolean commandMatch = Pattern.compile(Pattern.quote("!poop"), Pattern.CASE_INSENSITIVE).matcher(args[0]).find();
        if (commandMatch) {
            double chance = Math.random();
            double threshold = 0.01;
            if (args[1] != null) {
                threshold = Double.parseDouble(args[1]);
                log.info(String.format("using custom threshold: %s", threshold));
            }
            if (chance <= threshold) {
                log.info(String.format("%n%s rolled a %.2f out of %.3f, I am gonna poop all over him", event.getMessage().getAuthor().getName(), chance, threshold));
                event.getMessage().addReaction("💩").queue();
            } else {
                log.info(String.format("%n%s rolled a %.2f out of %.3f, I will not poop on him", event.getMessage().getAuthor().getName(), chance, threshold));
                event.getMessage().addReaction("👎").queue();
            }
        }
    }
}
