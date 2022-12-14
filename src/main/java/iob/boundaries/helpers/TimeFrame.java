package iob.boundaries.helpers;

import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;

public enum TimeFrame {
    LAST_HOUR(1),
    LAST_24_HOURS(24),
    LAST_7_DAYS(7 * 24),
    LAST_30_DAYS(30 * 24);

    private final int hours;

    TimeFrame(int hours) {
        this.hours = hours;
    }

    public Date getStartDate() {
        return DateUtils.addHours(new Date(), -this.hours);
    }

    public Date getEndDate() {
        return DateUtils.addHours(new Date(), this.hours);
    }
}