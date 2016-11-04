package fr.guddy.androidstarter.mvp.repo_list;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;
import com.hannesdorfmann.mosby.mvp.viewstate.RestorableViewState;

import java.io.Serializable;
import java.util.List;

import fr.guddy.androidstarter.persistence.entities.RepoEntity;
import icepick.Icepick;
import icepick.Icicle;

public interface RepoListMvp {

    //region Model
    final class Model implements Serializable {

        public final List<RepoEntity> repos;

        public Model(final List<RepoEntity> ploRepos) {
            repos = ploRepos;
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
        void loadRepos(final boolean pbPullToRefresh);
    }
    //endregion

    //region ViewState
    final class ViewState implements RestorableViewState<View> {

        //region Data to retain

        @Icicle
        public Serializable data;
        //endregion

        //region ViewState
        @Override
        public void apply(final View poView, final boolean pbRetained) {
            if (data instanceof Model) {
                final Model loData = (Model) data;
                poView.setData(loData);
                if (loData.repos == null || loData.repos.isEmpty()) {
                    poView.showEmpty();
                } else {
                    poView.showContent();
                }
            } else {
                poView.showError(null, false);
            }
        }
        //endregion

        //region RestorableViewState
        @Override
        public void saveInstanceState(@NonNull final Bundle poOut) {
            Icepick.saveInstanceState(this, poOut);
        }

        @Override
        public RestorableViewState<View> restoreInstanceState(final Bundle poIn) {
            if (poIn == null) {
                return null;
            }
            Icepick.restoreInstanceState(this, poIn);
            return this;
        }
        //endregion
    }
    //endregion
}
