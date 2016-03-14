package fr.guddy.androidstarter.tests.mock;

import android.content.Context;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import fr.guddy.androidstarter.di.modules.ModuleDatabase;
import fr.guddy.androidstarter.persistence.DatabaseHelperAndroidStarter;

@Module
public class MockModuleDatabase extends ModuleDatabase{

    @Provides
    @Singleton
    public DatabaseHelperAndroidStarter provideDatabaseHelperAndroidStarter(@NonNull final Context poContext) {
        return new MockDatabaseHelperAndroidStarter(poContext);
    }
}
