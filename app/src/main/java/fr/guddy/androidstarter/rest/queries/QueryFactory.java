package fr.guddy.androidstarter.rest.queries;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import fr.guddy.androidstarter.services.ServiceQueryExecutorIntentBuilder;

@Singleton
public class QueryFactory {
    //region Build methods
    public QueryGetRepos buildQueryGetRepos(@NonNull final String psUser, final boolean pbPullToRefresh) {
        return new QueryGetRepos(psUser, pbPullToRefresh);
    }
    //endregion

    //region Start methods
    public void startQuery(@NonNull final Context poContext, @NonNull final AbstractQuery poQuery) {
        final Intent loIntent = new ServiceQueryExecutorIntentBuilder(poQuery).build(poContext);
        poContext.startService(loIntent);
    }

    public void startQueryGetRepos(@NonNull final Context poContext, @NonNull final String psUser, final boolean pbPullToRefresh) {
        final QueryGetRepos loQuery = buildQueryGetRepos(psUser, pbPullToRefresh);
        startQuery(poContext, loQuery);
    }
    //endregion
}
