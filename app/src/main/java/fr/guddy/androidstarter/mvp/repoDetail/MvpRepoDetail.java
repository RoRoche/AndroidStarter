package fr.guddy.androidstarter.mvp.repoDetail;

import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;

import autodagger.AutoSubcomponent;
import dagger.Provides;
import fr.guddy.androidstarter.di.scopes.FragmentScope;
import fr.guddy.androidstarter.persistence.dao.DAORepo;
import fr.guddy.androidstarter.persistence.entities.RepoEntity;
import rx.Observable;

@AutoSubcomponent(
        modules = {MvpRepoDetail.Module.class}
)
@FragmentScope
public interface MvpRepoDetail {
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

    //region Interactor
    interface Interactor {
        Observable<RepoEntity> getRepoById(final long plRepoId);
    }
    //endregion

    //region Module
    @dagger.Module
    class Module {
        @Provides
        @FragmentScope
        public MvpRepoDetail.Interactor provideInteractor(@NonNull final DAORepo poDao) {
            return new InteractorRepoDetail(poDao);
        }

        @Provides
        @FragmentScope
        public MvpRepoDetail.Presenter providePresenter(@NonNull final MvpRepoDetail.Interactor poInteractor) {
            return new PresenterRepoDetail(poInteractor);
        }
    }
    //endregion
}
