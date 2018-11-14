package com.zcool.inkstone.ext.backstack;

import java.util.LinkedList;

public class BackStack {

    public interface BackLayer {
        boolean onBackPressed();
    }

    private final LinkedList<BackLayer> mBackLayers = new LinkedList<>();

    public boolean onBackPressed() {
        BackLayer last = mBackLayers.peekLast();
        if (last != null) {
            return last.onBackPressed();
        }

        return false;
    }

    public boolean isEmpty() {
        return mBackLayers.isEmpty();
    }

    public void add(BackLayer backLayer) {
        mBackLayers.add(backLayer);
    }

    public void remove(BackLayer backLayer) {
        mBackLayers.remove(backLayer);
    }

}
