package bot.listener;

import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.gson.GsonFactory;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.regex.Pattern;

@Slf4j
public class EtheriumListener extends ListenerAdapter {

    static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    static JsonFactory JSON_FACTORY = new GsonFactory();

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        try {
            boolean channelMatch = event.getChannel().getName().contains("crypto");
            String input = event.getMessage().getContentRaw();
            boolean commandMatch = Pattern.compile(Pattern.quote("!ETH"), Pattern.CASE_INSENSITIVE).matcher(input).find();
            boolean exactCaseSensitiveCommandMatch = input.equals("!ETH");

            if (channelMatch && commandMatch && !exactCaseSensitiveCommandMatch) {
                log.info("Fixing the !ETH command");
                double price = getEtheriumPriceInUSD();
                String response = "";
                String[] args = input.split(" ");
                // !eth shares 3 goal 42000
                if (args.length == 5 && args[1].contains("shares") && args[3].contains("goal")) {
                    int shares = Integer.parseInt(args[2]);
                    double goal = Double.parseDouble(args[4].replace(",",""));
                    double progress = price*shares / goal;
                    StringBuilder progressBar = new StringBuilder();
                    progressBar.append("[");
                    for (int i = 0; i < 10; i++) {
                        if (i < progress*10) {
                            progressBar.append("▮");
                        } else {
                            progressBar.append("▯");
                        }
                    }
                    progressBar.append("]");
                    response = String.format("ETH: $%s USD.%nGoal %.2f%% complete.%n%s", price, progress*100, progressBar);
                } else {
                    int hotdogs = (int) (price / 1.50);
                    response = String.format("ETH: $%s USD, or %s Costco Hotdogs.", price, hotdogs);
                }

                log.debug(response);
                event.getMessage().reply(response).queue();
            }
        } catch (Exception e) {
            log.error("Exception when forwarding !ETH message!", e);
        }
    }

    /**
     * fetches Etherium price in USD from cryptocompare.com
     * @return price of ETH in $USD
     * @throws IOException if IO exception
     */
    private double getEtheriumPriceInUSD() throws IOException {
        HttpRequestFactory factory = HTTP_TRANSPORT.createRequestFactory(
                request -> request.setParser(new JsonObjectParser(JSON_FACTORY))
        );

        GenericUrl url = new GenericUrl("https://min-api.cryptocompare.com/data/price");
        url.setScheme("https");
        url.setHost("min-api.cryptocompare.com/");
        url.setPathParts(Arrays.asList("data", "price"));
        url.put("fsym", "ETH");
        url.put("tsyms", "USD");
        log.debug("url: " + url.build());
        HttpRequest request = factory.buildGetRequest(url);
        HttpResponse response = request.execute();

        Gson gson = new Gson();
        String result = IOUtils.toString(response.getContent(), StandardCharsets.UTF_8);
        log.debug("result: " + result);
        JsonObject json = gson.fromJson(result, JsonObject.class);
        double id = json.get("USD").getAsDouble();
        log.info("Etherium price is ${}", id);
        return id;
    }
}
