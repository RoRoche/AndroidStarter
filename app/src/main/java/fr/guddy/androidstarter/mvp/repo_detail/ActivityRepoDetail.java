package fr.guddy.androidstarter.mvp.repo_detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.guddy.androidstarter.R;
import fr.guddy.androidstarter.mvp.repo_list.ActivityRepoList;
import se.emilsjolander.intentbuilder.Extra;
import se.emilsjolander.intentbuilder.IntentBuilder;

@IntentBuilder
public class ActivityRepoDetail extends AppCompatActivity {

    //region Extra
    @Extra
    Long mItemId;
    //endregion

    //region Injected views
    @BindView(R.id.ActivityRepoDetail_Toolbar)
    Toolbar mToolbar;
    //endregion

    //region Lifecycle
    @Override
    protected void onCreate(final Bundle poSavedInstanceState) {
        super.onCreate(poSavedInstanceState);
        setContentView(R.layout.activity_repo_detail);

        ButterKnife.bind(this);

        ActivityRepoDetailIntentBuilder.inject(getIntent(), this);

        setSupportActionBar(mToolbar);

        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (poSavedInstanceState == null) {

            final FragmentRepoDetail loFragment = new FragmentRepoDetailBuilder(mItemId).build();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.ActivityDetailRepo_container, loFragment)
                    .commit();
        }
    }
    //endregion

    //region Menu
    @Override
    public boolean onOptionsItemSelected(final MenuItem poItem) {
        final int liId = poItem.getItemId();
        if (liId == android.R.id.home) {
            NavUtils.navigateUpTo(this, new Intent(this, ActivityRepoList.class));
            return true;
        }
        return super.onOptionsItemSelected(poItem);
    }
    //endregion
}
