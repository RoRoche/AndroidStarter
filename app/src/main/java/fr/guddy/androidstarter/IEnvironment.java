package fr.guddy.androidstarter;

import okhttp3.logging.HttpLoggingInterceptor;

public interface IEnvironment {

    HttpLoggingInterceptor.Level getHttpLoggingInterceptorLevel();

    boolean isDebugDrawerEnabled();

}
