package fr.guddy.androidstarter.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.orhanobut.logger.Logger;

import java.sql.SQLException;

import javax.inject.Singleton;

import fr.guddy.androidstarter.BuildConfig;
import fr.guddy.androidstarter.R;
import fr.guddy.androidstarter.persistence.entities.RepoEntity;
import hugo.weaving.DebugLog;

@Singleton
public class DatabaseHelperAndroidStarter extends OrmLiteSqliteOpenHelper {
    private static final String TAG = DatabaseHelperAndroidStarter.class.getSimpleName();
    private static final boolean DEBUG = true;

    private static final String DATABASE_NAME = "android_starter.db";
    private static final int DATABASE_VERSION = 1;

    //region Constructor
    public DatabaseHelperAndroidStarter(@NonNull final Context poContext) {
        super(poContext, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
    }

    protected DatabaseHelperAndroidStarter(@NonNull final Context poContext,
                                           @NonNull final String psDatabaseName,
                                           final SQLiteDatabase.CursorFactory poFactory,
                                           final int piDatabaseVersion) {
        super(poContext, psDatabaseName, poFactory, piDatabaseVersion, R.raw.ormlite_config);
    }
    //endregion

    //region Methods to override
    @DebugLog
    @Override
    public void onCreate(@NonNull final SQLiteDatabase poDatabase, @NonNull final ConnectionSource poConnectionSource) {
        try {
            TableUtils.createTable(poConnectionSource, RepoEntity.class);
        } catch (final SQLException loException) {
            if (BuildConfig.DEBUG && DEBUG) {
                Logger.t(TAG).e(loException, "");
            }
        }
    }

    @DebugLog
    @Override
    public void onUpgrade(@NonNull final SQLiteDatabase poDatabase, @NonNull final ConnectionSource poConnectionSource, final int piOldVersion, final int piNewVersion) {
        try {
            TableUtils.dropTable(poConnectionSource, RepoEntity.class, true);
        } catch (final SQLException loException) {
            if (BuildConfig.DEBUG && DEBUG) {
                Logger.t(TAG).e(loException, "");
            }
        }
        onCreate(poDatabase, poConnectionSource);
    }
    //endregion
}
