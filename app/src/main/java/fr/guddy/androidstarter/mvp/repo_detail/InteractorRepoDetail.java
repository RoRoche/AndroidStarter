package fr.guddy.androidstarter.mvp.repo_detail;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import autodagger.AutoExpose;
import fr.guddy.androidstarter.persistence.dao.DAORepo;
import fr.guddy.androidstarter.persistence.entities.RepoEntity;
import rx.Observable;

@AutoExpose(MvpRepoDetail.class)
public class InteractorRepoDetail implements MvpRepoDetail.Interactor {
    //region Injected fields
    private final DAORepo mDaoRepo;
    //endregion

    //region Constructor
    @Inject
    public InteractorRepoDetail(@NonNull final DAORepo poDaoRepo) {
        mDaoRepo = poDaoRepo;
    }
    //endregion

    //region Database job
    @Override
    public Observable<RepoEntity> getRepoById(final long plRepoId) {
        return mDaoRepo.rxQueryForId(plRepoId);
    }
    //endregion
}
