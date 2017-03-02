package fr.guddy.androidstarter.tests.mock;

import android.content.Context;
import android.support.annotation.NonNull;

import com.squareup.picasso.Picasso;

import dagger.Module;
import fr.guddy.androidstarter.di.modules.ModuleRest;
import fr.guddy.androidstarter.rest.GitHubService;
import io.palaima.debugdrawer.picasso.PicassoModule;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static org.mockito.Mockito.mock;

@Module
public class MockModuleRest extends ModuleRest {
    //region Fields
    private MockWebServer mMockWebServer;
    private final Picasso mPicasso = mock(Picasso.class);
    private final PicassoModule mPicassoModule = mock(PicassoModule.class);
    //endregion

    //region Constructor
    public MockModuleRest() {
        mMockWebServer = new MockWebServer();
    }
    //endregion

    //region Modules
    @Override
    public GitHubService provideGithubService(@NonNull final OkHttpClient poOkHttpClient) {
        final Retrofit loRetrofit = new Retrofit.Builder()
                .baseUrl(mMockWebServer.url("/").toString())
                .client(poOkHttpClient)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        return loRetrofit.create(GitHubService.class);
    }

    @Override
    public Picasso providePicasso(@NonNull final Context poContext) {
        return mPicasso;
    }

    @Override
    public PicassoModule providePicassoModule(@NonNull final Picasso poPicasso) {
        return mPicassoModule;
    }
    //endregion

    //region Getters
    public MockWebServer getMockWebServer() {
        return mMockWebServer;
    }

    public Picasso getPicasso() {
        return mPicasso;
    }
    //endregion

    //region Visible API
    public void setUp() {
        mMockWebServer = new MockWebServer();
    }
    //endregion
}
