/**
 * Copyright (c) 2004 Domain Language, Inc. (http://domainlanguage.com)
 * This free software is distributed under the "MIT" licence. See file licence.txt. 
 * For more information, see http://timeandmoney.sourceforge.net.
 */

package com.domainlanguage.time;

import java.util.Iterator;

import com.domainlanguage.basic.*;


public class TimeInterval extends ConcreteInterval {
	public static final TimeInterval ALWAYS = over(TimePoint.FAR_PAST, TimePoint.FAR_FUTURE);

	
	public static TimeInterval over(TimePoint start, boolean closedStart, TimePoint end, boolean closedEnd) {
		return new TimeInterval(start, closedStart, end, closedEnd);
	}

	public static TimeInterval over(TimePoint start, TimePoint end) {
		//Uses the common default for time intervals, [start, end).
		return over(start, true, end, false);
	}

	public static TimeInterval startingFrom(TimePoint start, boolean startClosed, Duration length, boolean endClosed) {
		TimePoint end = start.plus(length);
		return over(start, startClosed, end, endClosed);
	}
	
	public static TimeInterval startingFrom(TimePoint start, Duration length) {
		//Uses the common default for time intervals, [start, end).
		return startingFrom(start, true, length, false);
	}

	public static TimeInterval preceding(TimePoint end, boolean startClosed, Duration length, boolean endClosed) {
		TimePoint start = end.minus(length);
		return over(start, startClosed, end, endClosed);
	}
	
	public static TimeInterval preceding(TimePoint end, Duration length) {
		//Uses the common default for time intervals, [start, end).
		return preceding(end, true, length, false);
	}

	public static TimeInterval closed(TimePoint start, TimePoint end) {
		return over(start, true, end, true);
	}

	public static TimeInterval open(TimePoint start, TimePoint end) {
		return over(start, false, end, false);
	}
	
	public static TimeInterval everFrom(TimePoint start) {
		return over(start, TimePoint.FAR_FUTURE);
	} 

	public static TimeInterval everPreceding(TimePoint end) {
		return over(TimePoint.FAR_PAST, end);
	} 

	public TimeInterval(TimePoint lower, boolean lowerIncluded, TimePoint upper, boolean upperIncluded) {
//		assert lower.compareTo(upper) < 0;  //This should really be an Interval invariant.
		super(lower, lowerIncluded, upper, upperIncluded);
	}

	public Interval newOfSameType(Comparable lower, boolean isLowerClosed, Comparable upper, boolean isUpperClosed) {
		return new TimeInterval((TimePoint)lower, isLowerClosed, (TimePoint)upper, isUpperClosed);
	}
	
	
	
	public boolean isBefore(TimePoint point) {
		return isBelow(point);
	}

	public boolean isAfter(TimePoint point) {
		return isAbove(point);
	}

	public Duration length() {
		long difference = end().millisecondsFromEpoc - start().millisecondsFromEpoc;
		return Duration.milliseconds(difference);
	}
	
	public Iterator daysIterator() {
		return new Iterator() {
			TimePoint next = start();
			public boolean hasNext() {
				return end().isAfter(next);
			}	
			public Object next() {
				Object current = next;
				next = next.nextDay();
				return current;
			}
			public void remove() {}
		};
	}

	public Iterator iterator(Duration subintervalLength) {
		final Duration segmentLength = subintervalLength;
		final Interval totalInterval = this;
		return new Iterator() {
			TimeInterval next = segmentLength.startingFrom(start());
			public boolean hasNext() {
				return totalInterval.includes(next);
			}	
			public Object next() {
				if (!hasNext()) return null;
				Object current = next;
				next = segmentLength.startingFrom(next.end());
				return current;
			}
			public void remove() {}
		};
	}

	public TimePoint start() {
		return (TimePoint) lowerLimit();
	}
	public TimePoint end() {
		return (TimePoint) upperLimit();
	}
	
	public TimeInterval intersect(TimeInterval interval) {
		return (TimeInterval)intersect((Interval)interval);
	}
}
 