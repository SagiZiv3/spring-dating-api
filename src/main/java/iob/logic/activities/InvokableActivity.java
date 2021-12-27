package iob.logic.activities;

import iob.boundaries.ActivityBoundary;

public interface InvokableActivity {
    Object invoke(ActivityBoundary activityBoundary);
}