package fr.guddy.androidstarter.di.modules;

import android.content.Context;
import android.support.annotation.NonNull;

import com.novoda.merlin.Merlin;
import com.novoda.merlin.MerlinsBeard;
import com.squareup.picasso.Picasso;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import fr.guddy.androidstarter.IEnvironment;
import fr.guddy.androidstarter.rest.GitHubService;
import io.palaima.debugdrawer.picasso.PicassoModule;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Module
public class ModuleRest {

    private final static int CACHE_SIZE_BYTES = 1024 * 1024 * 2;

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient(@NonNull final IEnvironment poEnvironment, @NonNull final Context poContext) {
        final HttpLoggingInterceptor loHttpLoggingInterceptor = new HttpLoggingInterceptor();
        loHttpLoggingInterceptor.setLevel(poEnvironment.getHttpLoggingInterceptorLevel());
        return new OkHttpClient.Builder()
                .addInterceptor(loHttpLoggingInterceptor)
                .cache(new Cache(poContext.getCacheDir(), CACHE_SIZE_BYTES))
                .build();
    }

    @Provides
    @Singleton
    public GitHubService provideGithubService(@NonNull final OkHttpClient poOkHttpClient) {
        final Retrofit loRetrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com")
                .client(poOkHttpClient)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        return loRetrofit.create(GitHubService.class);
    }

    @Provides
    @Singleton
    public Merlin provideMerlin(@NonNull final Context poContext) {
        return new Merlin.Builder()
                .withConnectableCallbacks()
                .withDisconnectableCallbacks()
                .withBindableCallbacks()
                .withLogging(true)
                .build(poContext);
    }

    @Provides
    @Singleton
    public MerlinsBeard provideMerlinsBeard(@NonNull final Context poContext) {
        return MerlinsBeard.from(poContext);
    }

    @Provides
    @Singleton
    public Picasso providePicasso(@NonNull final Context poContext) {
        final Picasso loPicasso = Picasso.with(poContext);
        loPicasso.setIndicatorsEnabled(true);
        loPicasso.setLoggingEnabled(true);
        return loPicasso;
    }

    @Provides
    @Singleton
    public PicassoModule providePicassoModule(@NonNull final Picasso poPicasso) {
        return new PicassoModule(poPicasso);
    }
}
