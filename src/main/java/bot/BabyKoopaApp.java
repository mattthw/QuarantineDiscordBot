package bot;

import bot.listener.EtheriumListener;
import bot.listener.PoopCommand;
import bot.listener.ReactionPlanter;
import bot.listener.WelvinTheReplier;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import dagger.BabyKoopaComponent;
import dagger.DaggerBabyKoopaComponent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;

import javax.inject.Inject;

@Slf4j
public class BabyKoopaApp {

    @Inject JDA jda;

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
        jda.addEventListener(new EtheriumListener());
        log.info("adding PoopCommand listener");
        jda.addEventListener(new PoopCommand());
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
