package fr.guddy.androidstarter.mvp.repoList;

import android.content.Context;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.orhanobut.logger.Logger;
import com.squareup.otto.Subscribe;

import java.util.List;

import javax.inject.Inject;

import autodagger.AutoInjector;
import fr.guddy.androidstarter.ApplicationAndroidStarter;
import fr.guddy.androidstarter.BuildConfig;
import fr.guddy.androidstarter.bus.BusManager;
import fr.guddy.androidstarter.persistence.dao.DAORepo;
import fr.guddy.androidstarter.persistence.entities.RepoEntity;
import fr.guddy.androidstarter.rest.queries.QueryFactory;
import fr.guddy.androidstarter.rest.queries.QueryGetRepos;
import hugo.weaving.DebugLog;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@AutoInjector(ApplicationAndroidStarter.class)
public class PresenterRepoList extends MvpBasePresenter<RepoListMvp.View> implements RepoListMvp.Presenter {
    private static final String TAG = PresenterRepoList.class.getSimpleName();
    private static final boolean DEBUG = true;

    //region Injected fields
    @Inject
    Context context;
    @Inject
    BusManager busManager;
    @Inject
    DAORepo daoRepo;
    @Inject
    QueryFactory queryFactory;
    //endregion

    //region Fields
    private Subscription mSubscriptionGetRepos;
    //endregion

    //region Constructor
    public PresenterRepoList() {
        ApplicationAndroidStarter.sharedApplication().componentApplication().inject(this);
    }
    //endregion

    //region Overridden methods
    @Override
    public void attachView(final RepoListMvp.View poView) {
        super.attachView(poView);

        try {
            busManager.registerSubscriberToBusMainThread(this);
        } catch (final Exception loException) {
            if (BuildConfig.DEBUG && DEBUG) {
                Logger.t(TAG).e(loException, "");
            }
        }
    }

    @Override
    public void detachView(final boolean pbRetainInstance) {
        super.detachView(pbRetainInstance);

        if (!pbRetainInstance) {
            unsubscribe();
        }

        try {
            busManager.unregisterSubscriberFromBusMainThread(this);
        } catch (final Exception loException) {
            if (BuildConfig.DEBUG && DEBUG) {
                Logger.t(TAG).e(loException, "");
            }
        }
    }
    //endregion

    //region Visible API
    @Override
    public void loadRepos(final boolean pbPullToRefresh) {
        startQueryGetRepos(pbPullToRefresh);
    }
    //endregion

    //region Reactive job
    private void getRepos(final boolean pbPullToRefresh) {
        unsubscribe();

        final RepoListMvp.View loView = getView();
        if (loView == null) {
            return;
        }

        mSubscriptionGetRepos = rxGetRepos()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        // onNext
                        (final List<RepoEntity> ploRepos) -> {
                            if (isViewAttached()) {
                                loView.setData(new RepoListMvp.Model(ploRepos));
                                if (ploRepos == null || ploRepos.isEmpty()) {
                                    loView.showEmpty();
                                } else {
                                    loView.showContent();
                                }
                            }
                        },
                        // onError
                        (final Throwable poException) -> {
                            if (isViewAttached()) {
                                loView.showError(poException, pbPullToRefresh);
                            }
                            unsubscribe();
                        },
                        // onCompleted
                        this::unsubscribe
                );
    }
    //endregion

    //region Database job
    private Observable<List<RepoEntity>> rxGetRepos() {
        return daoRepo.rxQueryForAll();
    }
    //endregion

    //region Network job
    private void startQueryGetRepos(final boolean pbPullToRefresh) {
        final RepoListMvp.View loView = getView();
        if (isViewAttached() && loView != null) {
            loView.showLoading(pbPullToRefresh);
        }

        queryFactory.startQueryGetRepos(context, "RoRoche", pbPullToRefresh);
    }
    //endregion

    //region Event management
    @DebugLog
    @Subscribe
    public void onEventQueryGetRepos(@NonNull final QueryGetRepos.EventQueryGetReposDidFinish poEvent) {
        if (poEvent.success) {
            getRepos(poEvent.pullToRefresh);
        } else {
            final RepoListMvp.View loView = getView();
            if (isViewAttached() && loView != null) {
                loView.showError(poEvent.throwable, poEvent.pullToRefresh);
            }
        }
    }
    //endregion

    //region Specific job
    private void unsubscribe() {
        if (mSubscriptionGetRepos != null && !mSubscriptionGetRepos.isUnsubscribed()) {
            mSubscriptionGetRepos.unsubscribe();
        }

        mSubscriptionGetRepos = null;
    }
    //endregion
}
