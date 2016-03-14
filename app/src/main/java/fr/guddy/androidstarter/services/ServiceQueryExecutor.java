package fr.guddy.androidstarter.services;

import android.app.IntentService;
import android.content.Intent;

import com.novoda.merlin.MerlinsBeard;
import com.path.android.jobqueue.JobManager;

import javax.inject.Inject;

import autodagger.AutoInjector;
import fr.guddy.androidstarter.ApplicationAndroidStarter;
import fr.guddy.androidstarter.rest.queries.AbstractQuery;
import hugo.weaving.DebugLog;
import se.emilsjolander.intentbuilder.Extra;
import se.emilsjolander.intentbuilder.IntentBuilder;

@IntentBuilder
@AutoInjector(ApplicationAndroidStarter.class)
public class ServiceQueryExecutor extends IntentService {
    private static final String TAG = ServiceQueryExecutor.class.getSimpleName();

    //region Extra fields
    @Extra
    AbstractQuery query;
    //endregion

    @Inject
    MerlinsBeard merlinsBeard;
    @Inject
    JobManager jobManager;

    //region Constructor matching super

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public ServiceQueryExecutor() {
        super(TAG);
    }
    //endregion

    //region Overridden methods
    @DebugLog
    @Override
    protected void onHandleIntent(final Intent poIntent) {
        ServiceQueryExecutorIntentBuilder.inject(poIntent, this);
        ApplicationAndroidStarter.sharedApplication().componentApplication().inject(this);

        // If query requires network, and if network is unreachable, and if the query must not persist
        if (query.requiresNetwork() &&
                !merlinsBeard.isConnected() &&
                !query.isPersistent()) {
            // then, we post an event to notify the job could not be done because of network connectivity
            query.inject();
            query.postEventQueryFinishedNoNetwork();
        } else {
            // otherwise, we can add the job
            jobManager.addJobInBackground(query);
        }
    }
    //endregion
}
