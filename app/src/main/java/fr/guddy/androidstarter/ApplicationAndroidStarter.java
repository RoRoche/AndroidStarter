package fr.guddy.androidstarter;

import android.app.Application;
import android.os.StrictMode;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.novoda.merlin.Merlin;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

import autodagger.AutoComponent;
import autodagger.AutoInjector;
import fr.guddy.androidstarter.di.modules.ModuleAsync;
import fr.guddy.androidstarter.di.modules.ModuleBus;
import fr.guddy.androidstarter.di.modules.ModuleContext;
import fr.guddy.androidstarter.di.modules.ModuleDatabase;
import fr.guddy.androidstarter.di.modules.ModuleEnvironment;
import fr.guddy.androidstarter.di.modules.ModuleRest;
import fr.guddy.androidstarter.di.modules.ModuleTransformer;
import fr.guddy.androidstarter.mvp.repo_detail.MvpRepoDetail;

@AutoComponent(
        modules = {
                ModuleAsync.class,
                ModuleBus.class,
                ModuleContext.class,
                ModuleDatabase.class,
                ModuleEnvironment.class,
                ModuleRest.class,
                ModuleTransformer.class
        },
        subcomponents = {
                MvpRepoDetail.class
        }
)
@Singleton
@AutoInjector(ApplicationAndroidStarter.class)
public class ApplicationAndroidStarter extends Application {
    private static final String TAG = ApplicationAndroidStarter.class.getSimpleName();

    //region Singleton
    protected static ApplicationAndroidStarter sSharedApplication;

    public static ApplicationAndroidStarter sharedApplication() {
        return sSharedApplication;
    }
    //endregion

    //region Fields (components)
    protected ApplicationAndroidStarterComponent mComponentApplication;
    //endregion

    //region Injected fields
    @Inject
    Merlin merlin;
    //endregion

    //region Overridden methods
    @Override
    public void onCreate() {
        super.onCreate();
        sSharedApplication = this;

        Logger.init(TAG)
                .logLevel(LogLevel.FULL);

        buildComponent();

        mComponentApplication.inject(this);
        merlin.bind();

        final StrictMode.ThreadPolicy loStrictModeThreadPolicy = new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyDeath()
                .build();
        StrictMode.setThreadPolicy(loStrictModeThreadPolicy);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        sSharedApplication = null;
        OpenHelperManager.releaseHelper();
        merlin.unbind();
    }
    //endregion

    //region Getters
    public ApplicationAndroidStarterComponent componentApplication() {
        return mComponentApplication;
    }
    //endregion

    //region Protected methods
    protected void buildComponent() {
        mComponentApplication = DaggerApplicationAndroidStarterComponent.builder()
                .moduleAsync(new ModuleAsync())
                .moduleBus(new ModuleBus())
                .moduleContext(new ModuleContext(getApplicationContext()))
                .moduleDatabase(new ModuleDatabase())
                .moduleEnvironment(new ModuleEnvironment())
                .moduleRest(new ModuleRest())
                .moduleTransformer(new ModuleTransformer())
                .build();
    }
    //endregion
}
