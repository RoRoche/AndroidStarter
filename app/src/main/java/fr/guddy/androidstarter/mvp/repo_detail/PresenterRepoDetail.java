package fr.guddy.androidstarter.mvp.repo_detail;

import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import javax.inject.Inject;

import autodagger.AutoExpose;
import fr.guddy.androidstarter.persistence.entities.RepoEntity;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@AutoExpose(MvpRepoDetail.class)
public final class PresenterRepoDetail extends MvpBasePresenter<MvpRepoDetail.View> implements MvpRepoDetail.Presenter {

    //region Fields
    private final MvpRepoDetail.Interactor mInteractor;
    private Subscription mSubscriptionGetRepo;
    //endregion

    //region Constructor
    @Inject
    public PresenterRepoDetail(@NonNull final MvpRepoDetail.Interactor poInteractor) {
        mInteractor = poInteractor;
    }
    //endregion

    //region Overridden method
    @Override
    public void detachView(final boolean pbRetainInstance) {
        super.detachView(pbRetainInstance);
        if (!pbRetainInstance) {
            unsubscribe();
        }
    }
    //endregion

    //region Visible API
    public void loadRepo(final long plRepoId, final boolean pbPullToRefresh) {
        final MvpRepoDetail.View loView = getView();
        if (isViewAttached() && loView != null) {
            loView.showLoading(pbPullToRefresh);
        }

        unsubscribe();

        mSubscriptionGetRepo = mInteractor.getRepoById(plRepoId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        // onNext
                        (final RepoEntity poRepo) -> {
                            if (isViewAttached()) {
                                loView.setData(new MvpRepoDetail.Model(poRepo));
                                if (poRepo == null) {
                                    loView.showEmpty();
                                } else {
                                    loView.showContent();
                                }
                            }
                        },
                        // onError
                        (final Throwable poException) -> {
                            if (isViewAttached()) {
                                loView.showError(poException, false);
                            }
                            unsubscribe();
                        },
                        // onCompleted
                        this::unsubscribe
                );
    }
    //endregion

    //region Specific job
    private void unsubscribe() {
        if (mSubscriptionGetRepo != null && !mSubscriptionGetRepo.isUnsubscribed()) {
            mSubscriptionGetRepo.unsubscribe();
        }

        mSubscriptionGetRepo = null;
    }
    //endregion
}
