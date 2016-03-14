package fr.guddy.androidstarter.tests.mock;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import fr.guddy.androidstarter.Environment;
import fr.guddy.androidstarter.di.modules.ModuleEnvironment;

@Module
public class MockModuleEnvironment extends ModuleEnvironment {

    @Provides
    @Singleton
    public Environment provideEnvironment() {
        return Environment.TEST;
    }
}
