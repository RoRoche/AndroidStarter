package fr.guddy.androidstarter.mvp.repoList;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import autodagger.AutoInjector;
import butterknife.ButterKnife;
import fr.guddy.androidstarter.ApplicationAndroidStarter;
import fr.guddy.androidstarter.IEnvironment;
import fr.guddy.androidstarter.R;
import fr.guddy.androidstarter.mvp.repoDetail.ActivityRepoDetailIntentBuilder;
import io.palaima.debugdrawer.DebugDrawer;
import io.palaima.debugdrawer.commons.BuildModule;
import io.palaima.debugdrawer.commons.DeviceModule;
import io.palaima.debugdrawer.commons.NetworkModule;
import io.palaima.debugdrawer.commons.SettingsModule;
import io.palaima.debugdrawer.fps.FpsModule;
import io.palaima.debugdrawer.picasso.PicassoModule;
import io.palaima.debugdrawer.scalpel.ScalpelModule;
import jp.wasabeef.takt.Takt;

@AutoInjector(ApplicationAndroidStarter.class)
public class ActivityRepoList extends AppCompatActivity
        implements FragmentRepoList.Callbacks {

    //region Fields
    private DebugDrawer mDebugDrawer;
    //endregion

    //region Injected fields
    @Inject
    Picasso mPicasso;
    @Inject
    IEnvironment mEnvironment;
    //endregion

    //region Lifecycle
    @Override
    protected void onCreate(final Bundle poSavedInstanceState) {
        super.onCreate(poSavedInstanceState);
        setContentView(R.layout.activity_repo_list);

        ApplicationAndroidStarter.sharedApplication().componentApplication().inject(this);

        ButterKnife.bind(this);

        if(mEnvironment.isDebugDrawerEnabled()) {
            mDebugDrawer = new DebugDrawer.Builder(this).modules(
                    new FpsModule(Takt.stock(getApplication())),
                    new ScalpelModule(this),
                    new PicassoModule(mPicasso),
                    new DeviceModule(this),
                    new BuildModule(this),
                    new NetworkModule(this),
                    new SettingsModule(this)
            ).build();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mDebugDrawer != null) {
            mDebugDrawer.onStart();
        }
    }

    @Override protected void onResume() {
        super.onResume();

        if(mDebugDrawer != null) {
            mDebugDrawer.onResume();
        }
    }

    @Override protected void onPause() {
        super.onPause();

        if(mDebugDrawer != null) {
            mDebugDrawer.onPause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(mDebugDrawer != null) {
            mDebugDrawer.onStop();
        }
    }
    //endregion

    //region FragmentRepoList.Callbacks

    /**
     * Callback method from {@link FragmentRepoList.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(final Long plId) {
        startActivity(new ActivityRepoDetailIntentBuilder(plId).build(this));
    }
    //endregion
}
