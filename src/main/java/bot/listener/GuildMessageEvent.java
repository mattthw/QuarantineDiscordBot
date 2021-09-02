package bot.listener;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

@Data
public class GuildMessageEvent {
    @NonNull
    private final GuildMessageReceivedEvent event;

    Message message;
    Member member;
    User author;
    String username;
    String discriminator;
    MessageChannel channel;
    String content;
    String nickname;
    Role role;

    public GuildMessageEvent(GuildMessageReceivedEvent event) {
        this.event = event;

        message = event.getMessage();
        member = event.getMember();
        author = event.getAuthor();
        username = author.getName();
        discriminator = author.getDiscriminator();
        channel = event.getChannel();
        content = message.getContentRaw();
        nickname = member.getNickname();
        role = event.getGuild().getPublicRole();
    }
}
