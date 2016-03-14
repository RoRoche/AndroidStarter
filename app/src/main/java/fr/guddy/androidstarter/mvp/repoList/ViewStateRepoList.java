package fr.guddy.androidstarter.mvp.repoList;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby.mvp.viewstate.RestorableViewState;

import java.io.Serializable;

import icepick.Icepick;
import icepick.Icicle;

public class ViewStateRepoList implements RestorableViewState<ViewRepoList> {

    //region Data to retain
    @Icicle
    public Serializable data;
    //endregion

    //region ViewState
    @Override
    public void apply(final ViewRepoList poView, final boolean pbRetained) {
        if (data instanceof ModelRepoList) {
            final ModelRepoList loData = (ModelRepoList) data;
            poView.setData(loData);
            if (loData.repos == null || loData.repos.isEmpty()) {
                poView.showEmpty();
            } else {
                poView.showContent();
            }
        }
    }
    //endregion

    //region RestorableViewState
    @Override
    public void saveInstanceState(@NonNull final Bundle poOut) {
        Icepick.saveInstanceState(this, poOut);
    }

    @Override
    public RestorableViewState<ViewRepoList> restoreInstanceState(final Bundle poIn) {
        if (poIn == null) {
            return null;
        }
        Icepick.restoreInstanceState(this, poIn);
        return this;
    }
    //endregion
}
