package fr.guddy.androidstarter.rest.error_handling;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RetrofitException extends RuntimeException {

    public static RetrofitException httpError(@NonNull final String psUrl, @NonNull final Response poResponse, @NonNull final Retrofit poRetrofit) {
        final String lsMessage = poResponse.code() + " " + poResponse.message();
        return new RetrofitException(lsMessage, psUrl, poResponse, Kind.HTTP, null, poRetrofit);
    }

    public static RetrofitException networkError(@NonNull final IOException poException) {
        return new RetrofitException(poException.getMessage(), null, null, Kind.NETWORK, poException, null);
    }

    public static RetrofitException unexpectedError(@NonNull final Throwable poException) {
        return new RetrofitException(poException.getMessage(), null, null, Kind.UNEXPECTED, poException, null);
    }

    /**
     * Identifies the event kind which triggered a {@link RetrofitException}.
     */
    public enum Kind {
        /**
         * An {@link IOException} occurred while communicating to the server.
         */
        NETWORK,
        /**
         * A non-200 HTTP status code was received from the server.
         */
        HTTP,
        /**
         * An internal error occurred while attempting to execute a request. It is best practice to
         * re-throw this exception so your application crashes.
         */
        UNEXPECTED
    }

    private final String mUrl;
    private final Response mResponse;
    private final Kind mKind;
    private final Retrofit mRetrofit;

    RetrofitException(@NonNull final String psMessage,
                      @Nullable final String psUrl,
                      @Nullable final Response poResponse,
                      @NonNull final Kind poKind,
                      @Nullable final Throwable poException,
                      @Nullable final Retrofit poRetrofit) {
        super(psMessage, poException);
        mUrl = psUrl;
        mResponse = poResponse;
        mKind = poKind;
        mRetrofit = poRetrofit;
    }

    /**
     * The request URL which produced the error.
     */
    public String getUrl() {
        return mUrl;
    }

    /**
     * Response object containing status code, headers, body, etc.
     */
    public Response getResponse() {
        return mResponse;
    }

    /**
     * The event kind which triggered this error.
     */
    public Kind getKind() {
        return mKind;
    }

    /**
     * The Retrofit this request was executed on
     */
    public Retrofit getRetrofit() {
        return mRetrofit;
    }

    /**
     * HTTP response body converted to specified {@code poType}. {@code null} if there is no
     * response.
     *
     * @throws IOException if unable to convert the body to the specified {@code poType}.
     */
    public <T> T getErrorBodyAs(@NonNull final Class<T> poType) throws IOException {
        if (mResponse == null || mResponse.errorBody() == null || mRetrofit == null) {
            return null;
        }
        final Converter<ResponseBody, T> loConverter = mRetrofit.responseBodyConverter(poType, new Annotation[0]);
        return loConverter.convert(mResponse.errorBody());
    }
}