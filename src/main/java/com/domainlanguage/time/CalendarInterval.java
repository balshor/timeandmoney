/**
 * Copyright (c) 2004 Domain Language, Inc. (http://domainlanguage.com) This
 * free software is distributed under the "MIT" licence. See file licence.txt.
 * For more information, see http://timeandmoney.sourceforge.net.
 */

package com.domainlanguage.time;

import java.util.*;

import com.domainlanguage.intervals.*;
import com.domainlanguage.util.*;

public abstract class CalendarInterval extends Interval<CalendarDate> {

    public static CalendarInterval inclusive(CalendarDate start, CalendarDate end) {
        return ConcreteCalendarInterval.from(start, end);
    }

    public static CalendarInterval inclusive(int startYear, int startMonth, int startDay, int endYear, int endMonth, int endDay) {
        CalendarDate startDate = CalendarDate.from(startYear, startMonth, startDay);
        CalendarDate endDate = CalendarDate.from(endYear, endMonth, endDay);
        return ConcreteCalendarInterval.from(startDate, endDate);
    }

    public static CalendarInterval month(int year, int month) {
        CalendarDate startDate = CalendarDate.date(year, month, 1);
        CalendarDate endDate = startDate.plusMonths(1).plusDays(-1);
        return inclusive(startDate, endDate);
    }

    public static CalendarInterval year(int year) {
        CalendarDate startDate = CalendarDate.date(year, 1, 1);
        CalendarDate endDate = CalendarDate.date(year + 1, 1, 1).plusDays(-1);
        return inclusive(startDate, endDate);
    }

    public static CalendarInterval startingFrom(CalendarDate start, Duration length) {
        // Uses the common default for calendar intervals, [start, end].
        return inclusive(start, start.plus(length).plusDays(-1));
    }
    public static CalendarInterval everFrom(CalendarDate startDate) {
        return inclusive(startDate, null);
    }
    public static CalendarInterval everPreceding(CalendarDate endDate) {
        return inclusive(null, endDate);
    }

    public abstract TimeInterval asTimeInterval(TimeZone zone);

    @Override
    public CalendarInterval newOfSameType(CalendarDate lower, boolean isLowerClosed, CalendarDate upper, boolean isUpperClosed) {
        CalendarDate includedLower = isLowerClosed ? lower : lower.plusDays(1);
        CalendarDate includedUpper = isUpperClosed ? upper : upper.plusDays(-1);
        return inclusive(includedLower, includedUpper);
    }

    public boolean includesLowerLimit() {
        return true;
    }

    public boolean includesUpperLimit() {
        return true;
    }

    public CalendarDate start() {
        return lowerLimit();
    }

    public CalendarDate end() {
        return upperLimit();
    }

    public boolean equals(Object object) {
        try {
            return equals((CalendarInterval)object);
        } catch(ClassCastException ex) {
            return false;
        }
    }
    public boolean equals(CalendarInterval other) {
        boolean lowerEquals = (!this.hasLowerLimit() && !other.hasLowerLimit()) || (this.hasLowerLimit() && this.lowerLimit().equals(other.lowerLimit()));
        boolean upperEquals = (!this.hasUpperLimit() && !other.hasUpperLimit()) || (this.hasUpperLimit() && this.upperLimit().equals(other.upperLimit()));
        return lowerEquals && upperEquals;
    }

    public int hashCode() {
        int code = 27;
        code *= (lowerLimit() != null) ? lowerLimit().hashCode() : 27;
        code *= (upperLimit() != null) ? upperLimit().hashCode() : 27;
        return code;
    }

    public Duration length() {
        return Duration.days(lengthInDaysInt());
    }

    public Duration lengthInMonths() {
        return Duration.months(lengthInMonthsInt());
    }

    public int lengthInMonthsInt() {
        Calendar calStart = start().asJavaCalendarUniversalZoneMidnight();
        Calendar calEnd = end().plusDays(1).asJavaCalendarUniversalZoneMidnight();
        int yearDiff = calEnd.get(Calendar.YEAR) - calStart.get(Calendar.YEAR);
        int monthDiff = yearDiff * 12 + calEnd.get(Calendar.MONTH) - calStart.get(Calendar.MONTH);
        return monthDiff;
    }

    public int lengthInDaysInt() {
        Calendar calStart = start().asJavaCalendarUniversalZoneMidnight();
        Calendar calEnd = end().plusDays(1).asJavaCalendarUniversalZoneMidnight();
        long diffMillis = calEnd.getTimeInMillis() - calStart.getTimeInMillis();
        return (int) (diffMillis / TimeUnitConversionFactors.millisecondsPerDay);
    }

    public Iterator<CalendarInterval> subintervalIterator(Duration subintervalLength) {
        //assert TimeUnit.day.compareTo(subintervalLength.normalizedUnit()) <=
        // 0;
        if (TimeUnit.day.compareTo(subintervalLength.normalizedUnit()) > 0) {
            throw new IllegalArgumentException("CalendarIntervals must be a whole number of days or months.");
        }

        final Interval<CalendarDate> totalInterval = this;
        final Duration segmentLength = subintervalLength;
        return new ImmutableIterator<CalendarInterval>() {
            CalendarInterval next = segmentLength.startingFrom(start());

            public boolean hasNext() {
                return totalInterval.covers(next);
            }

            public CalendarInterval next() {
                if (!hasNext())
                    return null;
                CalendarInterval current = next;
                next = segmentLength.startingFrom(next.end().plusDays(1));
                return current;
            }
        };
    }

    public Iterator<CalendarDate> daysIterator() {
        final CalendarDate start = lowerLimit();
        final CalendarDate end = upperLimit();
        return new ImmutableIterator<CalendarDate>() {
            CalendarDate next = start;

            public boolean hasNext() {
                return !next.isAfter(end);
            }

            public CalendarDate next() {
                CalendarDate current = next;
                next = next.plusDays(1);
                return current;
            }
        };
    }
    public Iterator<CalendarDate> daysInReverseIterator() {
        final CalendarDate start = upperLimit();
        final CalendarDate end = lowerLimit();
        return new ImmutableIterator<CalendarDate>() {
            CalendarDate next = start;

            public boolean hasNext() {
                return !next.isBefore(end);
            }

            public CalendarDate next() {
                CalendarDate current = next;
                next = next.plusDays(-1);
                return current;
            }
        };
    }



    public boolean expires(CalendarInterval other) {
        return this.start().equals(other.start()) && !other.hasUpperLimit() && this.hasUpperLimit();
    }

}