package com.zcool.inkstone.lang;

public class SimpleAbortSignal implements AbortSignal {

    private boolean mAbort;

    public void setAbort() {
        if (!mAbort) {
            mAbort = true;
        }
    }

    @Override
    public boolean isAbort() {
        return mAbort;
    }

}
