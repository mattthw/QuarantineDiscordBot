package bot.listener;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Objects;

@Slf4j
public class WelvinTheReplier extends ListenerAdapter {

    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        Message message = getMessageFromReaction(event);

        if (message.getAuthor().isBot() || event.getUser().isBot()){
            return;
        }

        if (doesReactionEmoteContain(message, "🥜")) {
            log.info(String.format("nutting on @%s.", message.getAuthor().getName()));
            MessageEmbed embed = createMessageEmbed(message.getAuthor());
            message.replyEmbeds(embed).queue();
        }
    }

    @NotNull
    private MessageEmbed createMessageEmbed(User messageAuthor) {
        return new EmbedBuilder()
                        .setTitle("DEEZ NUTZ")
                        .setAuthor("Welvin Da Great")
                        .setColor(Color.BLACK)
                        .setImage("https://c.tenor.com/eriZo2IOW8MAAAAM/deez-ha.gif")
                        .setDescription(String.format("you noob <@%s>", messageAuthor.getId()))
                        .build();
    }

    /**
     * check message reactions for specific emote. return true if present.
     * @param message message in question
     * @param emote emote to check for
     * @return true if emote is present
     */
    private boolean doesReactionEmoteContain(Message message, String emote) {
        MessageReaction m = message.getReactions().parallelStream()
                    .filter(r -> {
                        log.info(String.format("Reaction name: '%s'", r.getReactionEmote().getEmoji()));
                        return Objects.equals(r.getReactionEmote().getEmoji(), emote);
                    })
                    .findFirst().orElse(null);
        return m != null;
    }

    /**
     * Fetch message belonging to the reactionEvent, since the event does not contain the message info by default.
     * @param event event
     * @return Message
     */
    private Message getMessageFromReaction(@Nonnull GuildMessageReactionAddEvent event) {
        Message message;
        try {
            message =  event.retrieveMessage().submit().get();
        } catch (Exception e) {
            log.error("could not get reaction message!", e);
            throw new IllegalStateException("could not get reaction message!", e);
        }
        return message;
    }
}
