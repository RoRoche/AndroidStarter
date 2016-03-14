package fr.guddy.androidstarter.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import fr.guddy.androidstarter.bus.BusManager;

@Module
public class ModuleBus {

    @Provides
    @Singleton
    public BusManager provideBusManager() {
        return new BusManager();
    }
}
