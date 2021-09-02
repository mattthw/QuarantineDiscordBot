package bot.listener;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class SMSNotifier extends ListenerAdapter {

    @Nonnull
    private final String apiKey;

    static Map<String, String> userPhones = new HashMap<>();
    static {

    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        List<User> usersMentioned = event.getMessage().getMentionedUsers();
        String channel = event.getChannel().getName();
        for (User user : usersMentioned) {
            String userName = user.getName();
            String phone = userPhones.get(userName);
            log.info(String.format("SMSNotifier: user: %s channel: %s phone: %s", userName, channel, phone));
            if (null != phone) {
                sendSms(userName, phone, channel);
            }
        }
    }

    private void sendSms(String userName, String phone, String channel) {
        try {

            // build data
            String formattedMessage = String.format(
                    "Ahoy there matey!! You've (@%s) been summoned in the Quarantine Soiree channel #%s!",
                    userName,
                    channel
            );
            final NameValuePair[] data = {
                    new BasicNameValuePair("phone", phone),
                    new BasicNameValuePair("message", formattedMessage),
                    new BasicNameValuePair("key", apiKey)
            };

            HttpClient httpClient = HttpClients.createMinimal();
            HttpPost httpPost = new HttpPost("https://textbelt.com/text");
            httpPost.setEntity(new UrlEncodedFormEntity(Arrays.asList(data)));
            HttpResponse httpResponse = httpClient.execute(httpPost);

            String responseString = EntityUtils.toString(httpResponse.getEntity());
            JsonObject response = new Gson().fromJson(responseString, JsonObject.class);
            log.info("response: \n{}", response);
        } catch (Exception e) {
            log.error("Failed to send SMS message!", e);
        }
    }
}
