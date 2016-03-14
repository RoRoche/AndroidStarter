package fr.guddy.androidstarter.rest.errorHandling;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.concurrent.Executor;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ErrorHandlingExecutorCallAdapterFactory extends CallAdapter.Factory {

    private final Executor mCallbackExecutor;

    public ErrorHandlingExecutorCallAdapterFactory(final Executor poCallbackExecutor) {
        mCallbackExecutor = poCallbackExecutor;
    }

    @Override
    public CallAdapter<?> get(final Type poType, final Annotation[] paoAnnotations, final Retrofit poRetrofit) {
        if (getRawType(poType) != Call.class) {
            return null;
        }
        final Type loResponseType = getCallResponseType(poType);
        return new CallAdapter<Call<?>>() {
            @Override
            public Type responseType() {
                return loResponseType;
            }

            @Override
            public <R> Call<R> adapt(final Call<R> poCall) {
                return new ExecutorCallbackCall<>(mCallbackExecutor, poCall, poRetrofit);
            }
        };
    }

    static Type getCallResponseType(final Type poReturnType) {
        if (!(poReturnType instanceof ParameterizedType)) {
            throw new IllegalArgumentException(
                    "Call return type must be parameterized as Call<Foo> or Call<? extends Foo>");
        }
        final Type loResponseType = getSingleParameterUpperBound((ParameterizedType) poReturnType);

        // Ensure the Call response type is not Response, we automatically deliver the Response object.
        if (getRawType(loResponseType) == Response.class) {
            throw new IllegalArgumentException(
                    "Call<T> cannot use Response as its generic parameter. "
                            + "Specify the response body type only (e.g., Call<TweetResponse>).");
        }
        return loResponseType;
    }

    public static Type getSingleParameterUpperBound(final ParameterizedType poType) {
        final Type[] laoTypes = poType.getActualTypeArguments();
        if (laoTypes.length != 1) {
            throw new IllegalArgumentException(
                    "Expected one type argument but got: " + Arrays.toString(laoTypes));
        }
        final Type laoParamType = laoTypes[0];
        if (laoParamType instanceof WildcardType) {
            return ((WildcardType) laoParamType).getUpperBounds()[0];
        }
        return laoParamType;
    }

    static final class ExecutorCallbackCall<T> implements Call<T> {
        private final Executor mCallbackExecutor;
        private final Call<T> mDelegate;
        private final Retrofit mRetrofit;

        ExecutorCallbackCall(final Executor poCallbackExecutor, final Call<T> poDelegate, final Retrofit poRetrofit) {
            mCallbackExecutor = poCallbackExecutor;
            mDelegate = poDelegate;
            mRetrofit = poRetrofit;
        }

        @Override
        public void enqueue(final Callback<T> poCallback) {
            mDelegate.enqueue(new ExecutorCallback<>(mCallbackExecutor, poCallback, mRetrofit));
        }

        @Override
        public boolean isExecuted() {
            return mDelegate.isExecuted();
        }

        @Override
        public Response<T> execute() throws IOException {
            return mDelegate.execute();
        }

        @Override
        public void cancel() {
            mDelegate.cancel();
        }

        @Override
        public boolean isCanceled() {
            return mDelegate.isCanceled();
        }

        @SuppressWarnings("CloneDoesntCallSuperClone") // Performing deep clone.
        @Override
        public Call<T> clone() {
            return new ExecutorCallbackCall<>(mCallbackExecutor, mDelegate.clone(), mRetrofit);
        }

        @Override
        public Request request() {
            return mDelegate.request();
        }
    }

    static final class ExecutorCallback<T> implements Callback<T> {
        private final Executor mCallbackExecutor;
        private final Callback<T> mDelegate;
        private final Retrofit mRetrofit;

        ExecutorCallback(final Executor poCallbackExecutor, final Callback<T> poDelegate, final Retrofit poRetrofit) {
            mCallbackExecutor = poCallbackExecutor;
            mDelegate = poDelegate;
            mRetrofit = poRetrofit;
        }

        @Override
        public void onResponse(final Call<T> poCall, final Response<T> poResponse) {
            if (poResponse.isSuccess()) {
                mCallbackExecutor.execute(() -> mDelegate.onResponse(poCall, poResponse));
            } else {
                mCallbackExecutor.execute(() -> mDelegate.onFailure(poCall, RetrofitException.httpError(poResponse.raw().request().url().toString(), poResponse, mRetrofit)));
            }
        }

        @Override
        public void onFailure(final Call<T> poCall, final Throwable poThrowable) {
            RetrofitException loException;
            if (poThrowable instanceof IOException) {
                loException = RetrofitException.networkError((IOException) poThrowable);
            } else {
                loException = RetrofitException.unexpectedError(poThrowable);
            }
            final RetrofitException loFinalException = loException;
            mCallbackExecutor.execute(() -> mDelegate.onFailure(poCall, loFinalException));
        }
    }

    public static class MainThreadExecutor implements Executor {
        private final Handler mHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull final Runnable poRunnable) {
            mHandler.post(poRunnable);
        }
    }
}
