package fr.guddy.androidstarter.tests.rest;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;

import com.github.polok.localify.LocalifyClient;
import com.squareup.otto.Subscribe;

import org.frutilla.FrutillaTestRunner;
import org.frutilla.annotations.Frutilla;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import fr.guddy.androidstarter.di.modules.ModuleAsync;
import fr.guddy.androidstarter.di.modules.ModuleBus;
import fr.guddy.androidstarter.di.modules.ModuleEnvironment;
import fr.guddy.androidstarter.rest.GitHubService;
import fr.guddy.androidstarter.rest.dto.DTORepo;
import fr.guddy.androidstarter.rest.queries.QueryGetRepos;
import fr.guddy.androidstarter.test.R;
import fr.guddy.androidstarter.tests.mock.MockApplication;
import fr.guddy.androidstarter.tests.mock.MockModuleRest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Response;

import static com.google.common.truth.Truth.assertThat;

@RunWith(FrutillaTestRunner.class)
public class TestREST {

    //region Fields
    private LocalifyClient mLocalifyClient;
    private Context mContextTarget;
    private CountDownLatch mCountDownLatch;
    private QueryGetRepos.EventQueryGetReposDidFinish mEvent;
    private ModuleAsync mModuleAsync;
    private ModuleBus mModuleBus;
    private ModuleEnvironment mModuleEnvironment;
    private MockModuleRest mModuleRest;
    private MockWebServer mMockWebServer;
    //endregion

    //region Test lifecycle
    @Before
    public void setUp() throws Exception {
        mContextTarget = InstrumentationRegistry.getTargetContext();

        mModuleAsync = MockApplication.sharedMockApplication().getModuleAsync();
        mModuleBus = MockApplication.sharedMockApplication().getModuleBus();
        mModuleRest = MockApplication.sharedMockApplication().getModuleRest();
        mModuleRest.setUp();
        mModuleEnvironment = MockApplication.sharedMockApplication().getModuleEnvironment();

        mModuleBus.provideBusManager().registerSubscriberToBusAnyThread(this);

        mLocalifyClient = new LocalifyClient.Builder()
                .withResources(InstrumentationRegistry.getContext().getResources())
                .build();

        mMockWebServer = mModuleRest.getMockWebServer();
    }

    @After
    public void tearDown() throws Exception {
        mModuleBus.provideBusManager().unregisterSubscriberFromBusAnyThread(this);
        try {
            mMockWebServer.shutdown();
        } catch (@NonNull final Exception loException) {
            loException.printStackTrace();
        }
    }
    //endregion

    //region Test methods
    @Frutilla(
            Given = "A single GitHub repo from a API",
            When = "Execute query",
            Then = "It should have got a repo named \"git-consortium\""
    )
    @Test
    public void test_CallSyncListRepos_WithOneRepo_ReturnsOneRepo() throws IOException {
        GitHubService loGitHubService;
        Given:
        {
            final String lsOneRepoJSONData = mLocalifyClient.localify().loadRawFile(R.raw.repos_octocat);
            final MockResponse loMockResponseWithOneRepo = new MockResponse().setResponseCode(200);
            loMockResponseWithOneRepo.setBody(lsOneRepoJSONData);
            mMockWebServer.enqueue(loMockResponseWithOneRepo);
            try {
                mMockWebServer.start(4000);
            } catch (@NonNull final Exception loException) {
                loException.printStackTrace();
            }
            loGitHubService = mModuleRest.provideGithubService(mModuleRest.provideOkHttpClient(mModuleEnvironment.provideEnvironment(), mContextTarget));
        }

        Response<List<DTORepo>> loResponseWithOneRepo;
        When:
        {
            loResponseWithOneRepo = loGitHubService
                    .listRepos("test")
                    .execute();
        }

        Then:
        {
            final List<DTORepo> lloResults = loResponseWithOneRepo.body();
            assertThat(lloResults).hasSize(1);
            assertThat(lloResults.get(0).name).isEqualTo("git-consortium");
            assertThat(mMockWebServer.getRequestCount()).isEqualTo(1);
        }
    }

    @Frutilla(
            Given = "A single GitHub repo from a API",
            When = "Execute query asynchronously",
            Then = "It should have got a repo named \"git-consortium\""
    )
    @Test
    public void test_CallAsyncListRepos_WithOneRepo_ReturnsOneRepo() throws IOException, InterruptedException {
        Given:
        {
            final String lsOneRepoJSONData = mLocalifyClient.localify().loadRawFile(R.raw.repos_octocat);
            final MockResponse loMockResponseWithOneRepo = new MockResponse().setResponseCode(200);
            loMockResponseWithOneRepo.setBody(lsOneRepoJSONData);
            mMockWebServer.enqueue(loMockResponseWithOneRepo);
            try {
                mMockWebServer.start(4000);
            } catch (@NonNull final Exception loException) {
                loException.printStackTrace();
            }
        }

        When:
        {
            mModuleAsync.provideJobManager(mContextTarget).addJobInBackground(new QueryGetRepos("test", false));
            mCountDownLatch = new CountDownLatch(1);
            mCountDownLatch.await();
        }

        Then:
        {
            assertThat(mEvent).isNotNull();
            assertThat(mEvent.success).isTrue();
            assertThat(mEvent.results).hasSize(1);
            assertThat(mEvent.results.get(0).name).isEqualTo("git-consortium");
        }
    }
    //endregion

    //region Event subscription
    @Subscribe
    public void onEventQueryGetReposDidFinish(final QueryGetRepos.EventQueryGetReposDidFinish poEvent) {
        mEvent = poEvent;
        mCountDownLatch.countDown();
    }
    //endregion
}
