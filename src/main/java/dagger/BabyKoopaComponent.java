package dagger;

import bot.BabyKoopaApp;

import javax.inject.Singleton;

@Singleton
@Component(modules = {DependenciesModule.class})
public interface BabyKoopaComponent {

    void inject(BabyKoopaApp app);
}
