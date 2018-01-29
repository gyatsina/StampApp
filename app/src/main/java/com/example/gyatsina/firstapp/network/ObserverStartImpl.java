package com.example.gyatsina.firstapp.network;


import io.reactivex.observers.ResourceSingleObserver;

class ObserverStartImpl implements ObserverState {

    private ResourceSingleObserver mObserver;

    ObserverStartImpl(ResourceSingleObserver observer) {
        mObserver = observer;
    }

    @Override
    public boolean isSubscribed() {
        return mObserver != null && !mObserver.isDisposed();
    }

    @Override
    public void unSubscribe() {
        if (mObserver != null) {
            mObserver.dispose();
        }
        mObserver = null;
    }
}
