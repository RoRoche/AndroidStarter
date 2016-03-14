package fr.guddy.androidstarter.tests.mock;

import fr.guddy.androidstarter.ApplicationAndroidStarter;
import fr.guddy.androidstarter.DaggerApplicationAndroidStarterComponent;
import fr.guddy.androidstarter.di.modules.ModuleAsync;
import fr.guddy.androidstarter.di.modules.ModuleBus;
import fr.guddy.androidstarter.di.modules.ModuleContext;
import fr.guddy.androidstarter.di.modules.ModuleEnvironment;
import fr.guddy.androidstarter.di.modules.ModuleTransformer;

public class MockApplication extends ApplicationAndroidStarter {

    //region Fields
    private ModuleBus mModuleBus;
    private MockModuleRest mModuleRest;
    private ModuleEnvironment mModuleEnvironment;
    //endregion

    //region Singleton
    protected static MockApplication sSharedMockApplication;

    public static MockApplication sharedMockApplication() {
        return sSharedMockApplication;
    }
    //endregion

    //region Lifecycle
    @Override
    public void onCreate() {
        super.onCreate();
        sSharedMockApplication = this;
    }
    //endregion

    //region Overridden method
    @Override
    protected void buildComponent() {
        mModuleBus = new ModuleBus();
        mModuleRest = new MockModuleRest();
        mModuleEnvironment = new MockModuleEnvironment();

        mComponentApplication = DaggerApplicationAndroidStarterComponent.builder()
                .moduleAsync(new ModuleAsync())
                .moduleBus(mModuleBus)
                .moduleContext(new ModuleContext(getApplicationContext()))
                .moduleDatabase(new MockModuleDatabase())
                .moduleEnvironment(mModuleEnvironment)
                .moduleRest(mModuleRest)
                .moduleTransformer(new ModuleTransformer())
                .build();
    }
    //endregion

    //region Getters
    public MockModuleRest getModuleRest() {
        return mModuleRest;
    }

    public ModuleBus getModuleBus() {
        return mModuleBus;
    }

    public ModuleEnvironment getModuleEnvironment() {
        return mModuleEnvironment;
    }
    //endregion
}
