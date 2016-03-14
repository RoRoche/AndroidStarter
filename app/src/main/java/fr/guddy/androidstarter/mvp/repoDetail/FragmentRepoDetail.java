package fr.guddy.androidstarter.mvp.repoDetail;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hannesdorfmann.fragmentargs.FragmentArgs;
import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs;
import com.hannesdorfmann.mosby.mvp.MvpFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import fr.guddy.androidstarter.R;
import fr.guddy.androidstarter.persistence.entities.RepoEntity;
import pl.aprilapps.switcher.Switcher;

@FragmentWithArgs
public class FragmentRepoDetail
        extends MvpFragment<ViewRepoDetail, PresenterRepoDetail>
        implements ViewRepoDetail {

    //region FragmentArgs
    @Arg
    Long mItemId;
    //endregion

    //region Fields
    private Switcher mSwitcher;
    @Bind(R.id.FragmentRepoDetail_TextView_Description)
    TextView mTextViewDescription;
    @Bind(R.id.FragmentRepoDetail_TextView_Url)
    TextView mTextViewUrl;
    //endregion

    //region Injected views
    @Bind(R.id.FragmentRepoDetail_TextView_Empty)
    TextView mTextViewEmpty;
    @Bind(R.id.FragmentRepoDetail_TextView_Error)
    TextView mTextViewError;
    @Bind(R.id.FragmentRepoDetail_ProgressBar_Loading)
    ProgressBar mProgressBarLoading;
    @Bind(R.id.FragmentRepoDetail_ContentView)
    LinearLayout mContentView;
    //endregion

    //region Default constructor
    public FragmentRepoDetail() {
    }
    //endregion

    //region Lifecycle
    @Override
    public void onCreate(final Bundle poSavedInstanceState) {
        super.onCreate(poSavedInstanceState);
        FragmentArgs.inject(this);
    }

    @Override
    public View onCreateView(final LayoutInflater poInflater, final ViewGroup poContainer,
                             final Bundle savedInstanceState) {
        return poInflater.inflate(R.layout.fragment_repo_detail, poContainer, false);
    }

    @Override
    public void onViewCreated(final View poView, final Bundle poSavedInstanceState) {
        super.onViewCreated(poView, poSavedInstanceState);

        ButterKnife.bind(this, poView);

        mSwitcher = new Switcher.Builder()
                .withEmptyView(mTextViewEmpty)
                .withProgressView(mProgressBarLoading)
                .withErrorView(mTextViewError)
                .withContentView(mContentView)
                .build();

        loadData(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
    //endregion

    //region MvpFragment
    @NonNull
    @Override
    public PresenterRepoDetail createPresenter() {
        return new PresenterRepoDetail();
    }
    //endregion

    //region ViewRepoDetail
    @Override
    public void showEmpty() {
        mSwitcher.showEmptyView();
    }

    @Override
    public void showContent() {
        mSwitcher.showContentView();
    }

    @Override
    public void showLoading(final boolean pbPullToRefresh) {
        mSwitcher.showProgressView();
    }

    @Override
    public void showError(final Throwable poThrowable, final boolean pbPullToRefresh) {
        mSwitcher.showErrorView();
    }

    @Override
    public void setData(final ModelRepoDetail poData) {
        configureViewWithRepo(poData.repo);

        final Activity loActivity = this.getActivity();
        final CollapsingToolbarLayout loAppBarLayout = (CollapsingToolbarLayout) loActivity.findViewById(R.id.ActivityRepoDetail_ToolbarLayout);
        if (loAppBarLayout != null) {
            loAppBarLayout.setTitle(poData.repo.name);
        }
    }

    @Override
    public void loadData(final boolean pbPullToRefresh) {
        if (mItemId == null) {
            mSwitcher.showErrorView();
        } else {
            getPresenter().loadRepo(mItemId, pbPullToRefresh);
        }
    }
    //endregion

    //region Specific method
    private void configureViewWithRepo(@NonNull final RepoEntity poRepo) {
        mTextViewDescription.setText(poRepo.description);
        mTextViewUrl.setText(poRepo.url);
    }
    //endregion
}
