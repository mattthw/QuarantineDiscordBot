package bot.listener;

import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.gson.GsonFactory;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
public class CryptoPrice extends ListenerAdapter {

    public static final Gson GSON = new Gson();
    public static final String DELIMINATOR = " ";
    private static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static JsonFactory JSON_FACTORY = new GsonFactory();

    private final String cryptoKey;

    private static List<String> coinList = new ArrayList<>();

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        try {
            boolean channelMatch = event.getChannel().getName().contains("crypto");
            String input = event.getMessage().getContentRaw();

            updateCoinList();
            boolean isCommand = input.startsWith("!");
            String inputSymbol = input.substring(1).split(DELIMINATOR)[0].toUpperCase();
            String flatCoinList = coinList.toString();
            boolean validSymbol = Pattern.compile(Pattern.quote(inputSymbol), Pattern.CASE_INSENSITIVE).matcher(flatCoinList).find();
            boolean handledByStockSteve = input.equals("!ETH") || input.equals("!BTC");

            if (isCommand && channelMatch && validSymbol && !handledByStockSteve) {
                log.info("Looking up coin!");
                double price = getEtheriumPriceInUSD(inputSymbol);
                String response = "";
                String[] args = input.split(DELIMINATOR);
                // !eth shares 3 goal 42000
                if (args.length == 5 && args[1].contains("shares") && args[3].contains("goal")) {
                    long shares = Long.parseLong(args[2]);
                    double goal = Double.parseDouble(args[4].replaceAll(",","").replaceAll("\\$", ""));
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
                    response = String.format("%s: $%s USD.%nGoal %.2f%% complete.%n%s", inputSymbol, price, progress*100, progressBar);
                } else {
                    response = String.format("%s: $%s USD", inputSymbol, price);
                }
                log.debug(response);
                event.getChannel().sendMessage(response).queue();
            }
        } catch (Exception e) {
            log.error("Exception when looking up currency!", e);
        }
    }

    /**
     * fetches Etherium price in USD from cryptocompare.com
     * @return price of ETH in $USD
     * @throws IOException if IO exception
     * @param cryptoSymbol symbol of coin in caps
     */
    private double getEtheriumPriceInUSD(String cryptoSymbol) throws IOException {
        HttpRequestFactory factory = HTTP_TRANSPORT.createRequestFactory(
                request -> request.setParser(new JsonObjectParser(JSON_FACTORY))
        );

        GenericUrl url = new GenericUrl();
        url.setScheme("https");
        url.setHost("min-api.cryptocompare.com/");
        url.setPathParts(Arrays.asList("data", "price"));
        url.put("fsym", cryptoSymbol);
        url.put("tsyms", "USD");
        log.debug("url: " + url.build());
        HttpRequest request = factory.buildGetRequest(url);
        HttpResponse response = request.execute();

        String result = IOUtils.toString(response.getContent(), StandardCharsets.UTF_8);
        log.debug("result: " + result);
        JsonObject json = GSON.fromJson(result, JsonObject.class);
        double id = json.get("USD").getAsDouble();
        log.info("{} price is ${}", cryptoSymbol, id);
        return id;
    }

    private void updateCoinList() throws IOException {
        if (!coinList.isEmpty()) return;
        HttpRequestFactory factory = HTTP_TRANSPORT.createRequestFactory(
                request -> request.setParser(new JsonObjectParser(JSON_FACTORY))
        );

        GenericUrl url = new GenericUrl();
        url.setScheme("https");
        url.setHost("min-api.cryptocompare.com/");
        url.setPathParts(Arrays.asList("data", "blockchain", "list"));
        url.put("api_key", cryptoKey);
        log.debug("url: " + url.build());
        HttpRequest request = factory.buildGetRequest(url);
        HttpResponse response = request.execute();

        String result = IOUtils.toString(response.getContent(), StandardCharsets.UTF_8);
        log.debug("result: " + result);
        JsonObject json = GSON.fromJson(result, JsonObject.class);
        JsonObject data = json.get("Data").getAsJsonObject();
        Set<String> keys = data.keySet();
        log.info("result: " + keys.toString());
        coinList.addAll(keys);
    }
}
