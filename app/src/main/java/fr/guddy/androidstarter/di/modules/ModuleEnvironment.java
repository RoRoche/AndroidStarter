package fr.guddy.androidstarter.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import fr.guddy.androidstarter.BuildConfig;
import fr.guddy.androidstarter.IEnvironment;

@Module
public class ModuleEnvironment {

    @Provides
    @Singleton
    public IEnvironment provideEnvironment() {
        return BuildConfig.ENVIRONMENT;
    }
}
