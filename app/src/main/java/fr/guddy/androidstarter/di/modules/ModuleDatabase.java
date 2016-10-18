package fr.guddy.androidstarter.di.modules;

import android.content.Context;
import android.support.annotation.NonNull;

import com.j256.ormlite.android.DatabaseTableConfigUtil;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;
import com.orhanobut.logger.Logger;

import java.sql.SQLException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import fr.guddy.androidstarter.BuildConfig;
import fr.guddy.androidstarter.persistence.DatabaseHelperAndroidStarter;
import fr.guddy.androidstarter.persistence.dao.DAORepo;
import fr.guddy.androidstarter.persistence.entities.RepoEntity;

@Module
public class ModuleDatabase {
    private static final String TAG = ModuleDatabase.class.getSimpleName();
    private static final boolean DEBUG = true;

    @Provides
    @Singleton
    public DatabaseHelperAndroidStarter provideDatabaseHelperAndroidStarter(@NonNull final Context poContext) {
        return new DatabaseHelperAndroidStarter(poContext);
    }

    @Provides
    @Singleton
    public DAORepo provideDAORepo(@NonNull final DatabaseHelperAndroidStarter poDatabaseHelperAndroidStarter) {
        try {
            final ConnectionSource loConnectionSource = poDatabaseHelperAndroidStarter.getConnectionSource();
            final DatabaseTableConfig<RepoEntity> loTableConfig = DatabaseTableConfigUtil.fromClass(loConnectionSource, RepoEntity.class);
            if (loTableConfig != null) {
                return new DAORepo(loConnectionSource, loTableConfig);
            } else {
                return new DAORepo(loConnectionSource);
            }
        } catch (final SQLException loException) {
            if (BuildConfig.DEBUG && DEBUG) {
                Logger.t(TAG).e(loException, "");
            }
        }
        return null;
    }
}
