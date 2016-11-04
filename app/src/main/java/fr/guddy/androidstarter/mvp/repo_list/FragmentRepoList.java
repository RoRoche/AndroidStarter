package fr.guddy.androidstarter.mvp.repo_list;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hannesdorfmann.mosby.mvp.viewstate.MvpViewStateFragment;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;
import com.orhanobut.logger.Logger;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import fr.guddy.androidstarter.BuildConfig;
import fr.guddy.androidstarter.R;
import fr.guddy.androidstarter.persistence.entities.RepoEntity;
import hugo.weaving.DebugLog;
import icepick.Icepick;
import io.nlopez.smartadapters.SmartAdapter;
import io.nlopez.smartadapters.utils.ViewEventListener;
import pl.aprilapps.switcher.Switcher;

public class FragmentRepoList
        extends MvpViewStateFragment<RepoListMvp.View, RepoListMvp.Presenter>
        implements RepoListMvp.View, ViewEventListener<RepoEntity>, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = FragmentRepoList.class.getSimpleName();
    private static final boolean DEBUG = true;

    //region Mock callback constant
    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static final Callbacks sDummyCallbacks = (final Long plId) -> {
    };
    //endregion

    //region Injected views
    @BindView(R.id.FragmentRepoList_TextView_Empty)
    TextView mTextViewEmpty;
    @BindView(R.id.FragmentRepoList_ProgressBar_Loading)
    ProgressBar mProgressBarLoading;
    @BindView(R.id.FragmentRepoList_RecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.FragmentRepoList_TextView_Error)
    TextView mTextViewError;

    @BindView(R.id.FragmentRepoList_SwipeRefreshLayout_Empty)
    SwipeRefreshLayout mSwipeRefreshLayoutEmpty;
    @BindView(R.id.FragmentRepoList_SwipeRefreshLayout_Error)
    SwipeRefreshLayout mSwipeRefreshLayoutError;
    @BindView(R.id.FragmentRepoList_SwipeRefreshLayout_Content)
    SwipeRefreshLayout mSwipeRefreshLayoutContent;
    @BindViews({
            R.id.FragmentRepoList_SwipeRefreshLayout_Empty,
            R.id.FragmentRepoList_SwipeRefreshLayout_Error,
            R.id.FragmentRepoList_SwipeRefreshLayout_Content
    })
    List<SwipeRefreshLayout> mSwipeRefreshLayouts;
    //endregion

    //region Fields
    static final ButterKnife.Setter<SwipeRefreshLayout, SwipeRefreshLayout.OnRefreshListener> SET_LISTENER =
            (@NonNull final SwipeRefreshLayout poView, @NonNull final SwipeRefreshLayout.OnRefreshListener poListener, final int piIndex)
                    ->
                    poView.setOnRefreshListener(poListener);

    static final ButterKnife.Action<SwipeRefreshLayout> STOP_REFRESHING =
            (@NonNull final SwipeRefreshLayout poView, final int piIndex)
                    ->
                    poView.setRefreshing(false);

    private Switcher mSwitcher;

    private Callbacks mCallbacks = sDummyCallbacks;
    private Unbinder mUnbinder;
    //endregion

    //region Constructor

    public FragmentRepoList() {
    }
    //endregion

    //region Lifecycle
    @Override
    public void onAttach(final Context poContext) {
        super.onAttach(poContext);

        // Activities containing this fragment must implement its callbacks.
        if (!(poContext instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) poContext;
    }

    @Override
    public View onCreateView(final LayoutInflater poInflater, final ViewGroup poContainer, final Bundle poSavedInstanceState) {
        View view = poInflater.inflate(R.layout.fragment_repo_list, poContainer, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(final View poView, final Bundle poSavedInstanceState) {
        super.onViewCreated(poView, poSavedInstanceState);
        Icepick.restoreInstanceState(this, poSavedInstanceState);

        mUnbinder = ButterKnife.bind(this, poView);

        ButterKnife.apply(mSwipeRefreshLayouts, SET_LISTENER, this);

        mSwitcher = new Switcher.Builder()
                .withEmptyView(mSwipeRefreshLayoutEmpty)
                .withProgressView(mProgressBarLoading)
                .withErrorView(mSwipeRefreshLayoutError)
                .withContentView(mSwipeRefreshLayoutContent)
                .build();
    }

    @Override
    public void onActivityCreated(final Bundle poSavedInstanceState) {
        super.onActivityCreated(poSavedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onSaveInstanceState(final Bundle poOutState) {
        super.onSaveInstanceState(poOutState);
        Icepick.saveInstanceState(this, poOutState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mUnbinder.unbind();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }
    //endregion

    //region ViewEventListener
    @DebugLog
    @Override
    public void onViewEvent(final int piActionID, final RepoEntity poRepo, final int piPosition, final View poView) {
        if (piActionID == CellRepo.ROW_PRESSED) {
            mCallbacks.onItemSelected(poRepo.getBaseId());
        }
    }
    //endregion

    //region MvpFragment
    @DebugLog
    @NonNull
    @Override
    public RepoListMvp.Presenter createPresenter() {
        return new PresenterRepoList();
    }
    //endregion

    //region ViewRepoList
    @DebugLog
    @Override
    public void showEmpty() {
        ButterKnife.apply(mSwipeRefreshLayouts, STOP_REFRESHING);
        mSwitcher.showEmptyView();
    }
    //endregion

    //region MvpLceView
    @DebugLog
    @Override
    public void showLoading(final boolean pbPullToRefresh) {
        if (!pbPullToRefresh) {
            mSwitcher.showProgressView();
        }
    }

    @DebugLog
    @Override
    public void showContent() {
        ButterKnife.apply(mSwipeRefreshLayouts, STOP_REFRESHING);
        mSwitcher.showContentView();
    }

    @DebugLog
    @Override
    public void showError(final Throwable poThrowable, final boolean pbPullToRefresh) {
        if (BuildConfig.DEBUG && DEBUG) {
            Logger.t(TAG).e(poThrowable, "");
        }

        ButterKnife.apply(mSwipeRefreshLayouts, STOP_REFRESHING);
        mSwitcher.showErrorView();
    }

    @DebugLog
    @Override
    public void setData(final RepoListMvp.Model poData) {
        ((RepoListMvp.ViewState) viewState).data = poData;

        SmartAdapter.items(poData.repos)
                .map(RepoEntity.class, CellRepo.class)
                .listener(FragmentRepoList.this)
                .into(mRecyclerView);
    }

    @DebugLog
    @Override
    public void loadData(final boolean pbPullToRefresh) {
        getPresenter().loadRepos(pbPullToRefresh);
    }
    //endregion

    //region MvpViewStateFragment
    @DebugLog
    @NonNull
    @Override
    public ViewState createViewState() {
        return new RepoListMvp.ViewState();
    }

    @DebugLog
    @Override
    public void onNewViewStateInstance() {
        loadData(false);
    }
    //endregion

    //region SwipeRefreshLayout.OnRefreshListener
    @Override
    public void onRefresh() {
        loadData(true);
    }
    //endregion

    //region Callback definition

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        void onItemSelected(final Long plId);
    }
    //endregion
}
