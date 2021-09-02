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
        super.onGuildMessageReceived(event);
    }

    private void poopOnFriendsChance(GuildMessageEvent m) {
        String input = m.getMessage().getContentRaw();
        String[] args = input.split(" ");
        boolean commandMatch = Pattern.compile(Pattern.quote("!poop"), Pattern.CASE_INSENSITIVE).matcher(input).find();
        if (commandMatch) {
            double chance = Math.random();
            double threshold = 0.01;
            if (args.length == 2) {
                threshold = Double.parseDouble(args[1]);
            }
            if (chance <= threshold) {
                log.info(String.format("%n%s rolled a %.2f out of %.2f, I am gonna poop all over him", m.getUsername(), chance, threshold));
                m.getMessage().addReaction("💩").queue();
                m.getMessage().addReaction("🥳").queue();
            } else {
                log.info(String.format("%n%s rolled a %.2f out of %.2f, I will not poop on him", m.getUsername(), chance, threshold));
                m.getMessage().addReaction("\uD83C\uDDEB").queue(); // F
                m.getMessage().addReaction("\uD83C\uDDE6").queue(); // A
                m.getMessage().addReaction("\uD83C\uDDEE").queue(); // I
                m.getMessage().addReaction("\uD83C\uDDF1").queue(); // L
            }
        }
    }
}
