package fr.guddy.androidstarter;

import okhttp3.logging.HttpLoggingInterceptor;

public enum Environment implements IEnvironment {
    DEV {
        @Override
        public HttpLoggingInterceptor.Level getHttpLoggingInterceptorLevel() {
            return HttpLoggingInterceptor.Level.BODY;
        }

        @Override
        public boolean isDebugDrawerEnabled() {
            return true;
        }
    },
    PROD {
        @Override
        public HttpLoggingInterceptor.Level getHttpLoggingInterceptorLevel() {
            return HttpLoggingInterceptor.Level.NONE;
        }

        @Override
        public boolean isDebugDrawerEnabled() {
            return false;
        }
    },
    TEST {
        @Override
        public HttpLoggingInterceptor.Level getHttpLoggingInterceptorLevel() {
            return HttpLoggingInterceptor.Level.BODY;
        }

        @Override
        public boolean isDebugDrawerEnabled() {
            return false;
        }
    }
}
