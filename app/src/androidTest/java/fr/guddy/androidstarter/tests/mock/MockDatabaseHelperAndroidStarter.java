package fr.guddy.androidstarter.tests.mock;

import android.content.Context;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import fr.guddy.androidstarter.persistence.DatabaseHelperAndroidStarter;

@Singleton
public class MockDatabaseHelperAndroidStarter extends DatabaseHelperAndroidStarter {
    private static final String DATABASE_NAME = "mock_android_starter.db";
    private static final int DATABASE_VERSION = 1;

    //region Constructor
    public MockDatabaseHelperAndroidStarter(@NonNull final Context poContext) {
        super(poContext, DATABASE_NAME, null, DATABASE_VERSION);
    }
    //endregion
}
