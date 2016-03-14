package fr.guddy.androidstarter.bus;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import javax.inject.Singleton;

import fr.guddy.androidstarter.bus.event.AbstractEvent;

/**
 * Facade to fluidify event bus management.
 */
@Singleton
public final class BusManager {

    //region Inner synthesize job
    private static final Bus sBusAnyThread = new Bus(ThreadEnforcer.ANY, "ANY_THREAD");
    private static final BusMainThread sBusMainThread = new BusMainThread("MAIN_THREAD");
    //endregion

    //region Specific any thread job
    public void registerSubscriberToBusAnyThread(final Object poSubscriber) {
        if (poSubscriber != null) {
            sBusAnyThread.register(poSubscriber);
        }
    }

    public void unregisterSubscriberFromBusAnyThread(final Object poSubscriber) {
        if (poSubscriber != null) {
            sBusAnyThread.unregister(poSubscriber);
        }
    }

    public void postEventOnAnyThread(final AbstractEvent poEvent) {
        if (poEvent != null) {
            sBusAnyThread.post(poEvent);
        }
    }
    //endregion

    //region Specific main thread job
    public void registerSubscriberToBusMainThread(final Object poSubscriber) {
        if (poSubscriber != null) {
            sBusMainThread.register(poSubscriber);
        }
    }

    public void unregisterSubscriberFromBusMainThread(final Object poSubscriber) {
        if (poSubscriber != null) {
            sBusMainThread.unregister(poSubscriber);
        }
    }

    public void postEventOnMainThread(final AbstractEvent poEvent) {
        if (poEvent != null) {
            sBusMainThread.post(poEvent);
        }
    }
    //endregion
}
