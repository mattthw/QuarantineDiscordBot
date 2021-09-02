package dagger;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.security.auth.login.LoginException;
import java.io.File;

@Slf4j
@Module
public class DependenciesModule {

    public static final String DISCORD_TOKEN = "discord.botToken";
    public static final String CONFIGURATION_PROPERTIES = "configuration.properties";
    public static final String DISCORD_APPLICATION_ID = "discord.applicationId";

    @Singleton
    @Provides
    static Configuration providesConfiguration() {
        Configurations configs = new Configurations();
        try {
            return configs.properties(new File(CONFIGURATION_PROPERTIES));
        } catch (ConfigurationException e) {
            log.error("Failed to read file into a Configuration!", e);
            throw new IllegalStateException("Cannot start without valid configuration!", e);
        }
    }

    @Singleton
    @Provides
    @Named(DISCORD_TOKEN)
    static String providesToken(Configuration configuration) {
        return configuration.getString(DISCORD_TOKEN);
    }

    @Singleton
    @Provides
    @Named(DISCORD_APPLICATION_ID)
    static String providesApplicationId(Configuration configuration) {
        String token = configuration.getString(DISCORD_APPLICATION_ID);
        log.debug("token is: {}", token);
        return token;
    }

    @Singleton
    @Provides
    static JDA providesJDA(@Named(DISCORD_TOKEN) String token) {
        JDABuilder jdaBuilder = JDABuilder.createDefault(token);
        jdaBuilder.setActivity(Activity.playing("🌚"));
        try {
            return jdaBuilder.build();
        } catch (LoginException e) {
            String msg = "Failed to login to JDA using token: " + token;
            log.error(msg, e);
            throw new IllegalStateException(msg, e);
        }
    }
}
