package edu.training.wearbountyhunter;

/**
 * Created by brachialste on 29/11/16.
 */

public interface OnTaskCompleted {
    public void onTaskCompleted(Object feed);
    public void onTaskError(Object feed);
}