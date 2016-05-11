package fr.guddy.androidstarter.mvp.repoDetail;

import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;

import fr.guddy.androidstarter.persistence.entities.RepoEntity;

public interface RepoDetailMvp {
    //region Model
    final class Model {
        public final RepoEntity repo;

        public Model(final RepoEntity poRepo) {
            repo = poRepo;
        }
    }
    //endregion

    //region View
    interface View extends MvpLceView<Model> {
        void showEmpty();
    }
    //endregion

    //region Presenter
    interface Presenter extends MvpPresenter<View> {
        void loadRepo(final long plRepoId, final boolean pbPullToRefresh);
    }
    //endregion
}
