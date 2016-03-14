package fr.guddy.androidstarter.bus;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

public class BusMainThread extends Bus {
    //region Field
    private final Handler handler = new Handler(Looper.getMainLooper());
    //endregion

    //region Constructor
    public BusMainThread(final String psName) {
        super(psName);
    }
    //endregion

    //region Overridden method
    @Override
    public void post(final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.post(event);
        } else {
            handler.post(() -> BusMainThread.super.post(event));
        }
    }
    //endregion
}
