package fr.guddy.androidstarter.di.modules;

import android.content.Context;
import android.support.annotation.NonNull;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ModuleAsync {

    @Provides
    @Singleton
    public JobManager provideJobManager(@NonNull final Context poContext) {
        final Configuration loConfiguration = new Configuration.Builder(poContext)
                .minConsumerCount(1) //always keep at least one consumer alive
                .maxConsumerCount(3) //up to 3 consumers at a time
                .loadFactor(3) //3 jobs per consumer
                .consumerKeepAlive(120) //wait 2 minute
                .build();
        return new JobManager(loConfiguration);
    }
}
