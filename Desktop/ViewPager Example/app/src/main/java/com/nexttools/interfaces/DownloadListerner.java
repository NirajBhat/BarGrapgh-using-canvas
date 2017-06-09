package com.nexttools.interfaces;

/**
 * Created by next on 25/5/17.
 */
public interface DownloadListerner {
    public void downloadCompletedListerner(boolean taskCompleted);

    public void progressListerner(int progress);
}
