package fr.guddy.androidstarter.tests.runner;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.test.runner.AndroidJUnitRunner;

import fr.guddy.androidstarter.tests.mock.MockApplication;

public class AndroidStarterTestRunner extends AndroidJUnitRunner {

    //region Overridden method
    @Override
    public void onCreate(final Bundle poArguments) {
        MultiDex.install(this.getTargetContext());
        super.onCreate(poArguments);
    }

    @Override
    public Application newApplication(
            final ClassLoader poClassLoader,
            final String psClassName,
            final Context poContext) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return super.newApplication(poClassLoader, MockApplication.class.getName(), poContext);
    }
    //endregion

}
