package bot;

import bot.listener.CryptoPrice;
import bot.listener.PoopCommand;
import bot.listener.ReactionPlanter;
import bot.listener.WelvinTheReplier;
import dagger.BabyKoopaComponent;
import dagger.DaggerBabyKoopaComponent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;

import javax.inject.Inject;
import javax.inject.Named;

import static dagger.DependenciesModule.CRYPTO_KEY;
import static dagger.DependenciesModule.SMS_KEY;

@Slf4j
public class BabyKoopaApp {

    @Inject JDA jda;
    @Inject @Named(SMS_KEY) String smsKey;
    @Inject @Named(CRYPTO_KEY) String cryptoKey;

    private BabyKoopaApp() {
        BabyKoopaComponent component = DaggerBabyKoopaComponent.builder().build();
        component.inject(this);
    }

    private void create() throws InterruptedException {
        log.info("Starting discord bot.");
        jda.awaitReady();

        // listeners
        log.info("adding reaction listener");
        jda.addEventListener(new ReactionPlanter());
        log.info("adding deez nuts listener");
        jda.addEventListener(new WelvinTheReplier());
        log.info("adding ETH forwarder");
        jda.addEventListener(new CryptoPrice(cryptoKey));
        log.info("adding PoopCommand listener");
        jda.addEventListener(new PoopCommand());
        log.info("adding sms listener");
        //jda.addEventListener(new SMSNotifier(smsKey));
    }

    public static void main(String[] args) {
        try {
            BabyKoopaApp app = new BabyKoopaApp();
            app.create();
        } catch (Exception e) {
            log.error("Error starting discord bot!", e);
        }
    }
}
