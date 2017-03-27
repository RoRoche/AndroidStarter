package fr.guddy.androidstarter.rest.queries;

import com.j256.ormlite.dao.Dao;
import com.mobandme.android.transformer.Transformer;
import com.orhanobut.logger.Logger;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import autodagger.AutoInjector;
import fr.guddy.androidstarter.ApplicationAndroidStarter;
import fr.guddy.androidstarter.BuildConfig;
import fr.guddy.androidstarter.bus.BusManager;
import fr.guddy.androidstarter.bus.event.AbstractEventQueryDidFinish;
import fr.guddy.androidstarter.di.modules.ModuleTransformer;
import fr.guddy.androidstarter.persistence.dao.DAORepo;
import fr.guddy.androidstarter.persistence.entities.RepoEntity;
import fr.guddy.androidstarter.rest.GitHubService;
import fr.guddy.androidstarter.rest.dto.DTORepo;
import retrofit2.Call;
import retrofit2.Response;

@AutoInjector(ApplicationAndroidStarter.class)
public class QueryGetRepos extends AbstractQuery {
    private static final String TAG = QueryGetRepos.class.getSimpleName();
    private static final boolean DEBUG = true;

    //region Injected fields
    @Inject
    transient GitHubService gitHubService;
    @Inject
    transient BusManager busManager;
    @Inject
    transient DAORepo daoRepo;
    @Inject
    @Named(ModuleTransformer.TRANSFORMER_REPO)
    transient Transformer transformerRepo;
    //endregion

    //region Fields
    public final boolean pullToRefresh;
    public final String user;
    public List<DTORepo> results;
    //endregion

    //region Constructor matching super
    public QueryGetRepos(final String psUser, final boolean pbPullToRefresh) {
        super(Priority.MEDIUM);
        user = psUser;
        pullToRefresh = pbPullToRefresh;
    }
    //endregion

    //region Overridden method
    @Override
    public void inject() {
        ApplicationAndroidStarter.sharedApplication().componentApplication().inject(this);
    }

    @Override
    protected void execute() throws Exception {
        final Call<List<DTORepo>> loCall = gitHubService.listRepos(user);
        final Response<List<DTORepo>> loExecute = loCall.execute();

        if (isCached(loExecute)) {
            // not modified, no need to do anything
            return;
        }

        results = loExecute.body();

        final int liDeleted = daoRepo.deleteBuilder().delete();

        if (BuildConfig.DEBUG && DEBUG) {
            Logger.t(TAG).d("deleted row count = %d", liDeleted);
        }

        int liCount = 0;
        for (final DTORepo loDTORepo : results) {
            final RepoEntity loRepo = transformerRepo.transform(loDTORepo, RepoEntity.class);
            loRepo.avatarUrl = loDTORepo.owner.avatarUrl;
            final Dao.CreateOrUpdateStatus loStatus = daoRepo.createOrUpdate(loRepo);
            if (loStatus.isCreated() || loStatus.isUpdated()) {
                ++liCount;
            }
        }

        if (BuildConfig.DEBUG && DEBUG) {
            Logger.t(TAG).d("created or updated row count = %d", liCount);
        }
    }

    @Override
    protected void postEventQueryFinished() {
        final EventQueryGetReposDidFinish loEvent = new EventQueryGetReposDidFinish(this, mSuccess, mErrorType, mThrowable, pullToRefresh, results);
        busManager.postEventOnMainThread(loEvent);
        busManager.postEventOnAnyThread(loEvent);
    }

    @Override
    public void postEventQueryFinishedNoNetwork() {
        final EventQueryGetReposDidFinish loEvent = new EventQueryGetReposDidFinish(this, false, AbstractEventQueryDidFinish.ErrorType.NETWORK_UNREACHABLE, null, pullToRefresh, null);
        busManager.postEventOnMainThread(loEvent);
        busManager.postEventOnAnyThread(loEvent);
    }
    //endregion

    //region Dedicated EventQueryDidFinish
    public static final class EventQueryGetReposDidFinish extends AbstractEventQueryDidFinish<QueryGetRepos> {
        public final boolean pullToRefresh;
        public final List<DTORepo> results;

        public EventQueryGetReposDidFinish(final QueryGetRepos poQuery, final boolean pbSuccess, final ErrorType poErrorType, final Throwable poThrowable, final boolean pbPullToRefresh, final List<DTORepo> ploResults) {
            super(poQuery, pbSuccess, poErrorType, poThrowable);
            pullToRefresh = pbPullToRefresh;
            results = ploResults;
        }
    }
    //endregion
}
