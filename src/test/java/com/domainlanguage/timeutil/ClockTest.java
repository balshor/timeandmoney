/**
 * Copyright (c) 2004 Domain Language, Inc. (http://domainlanguage.com)
 * This free software is distributed under the "MIT" licence. See file licence.txt. 
 * For more information, see http://timeandmoney.sourceforge.net.
 */

package com.domainlanguage.timeutil;

import java.util.TimeZone;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.time.TimePoint;
import com.domainlanguage.time.TimeSource;

public class ClockTest extends TestCase {

  TimePoint dec1_5am_gmt = TimePoint.atGMT(2004, 12, 1, 5, 0);
  TimeZone gmt = TimeZone.getTimeZone("Universal");
  TimeZone pt = TimeZone.getTimeZone("America/Los_Angeles");
  TimeZone ct = TimeZone.getTimeZone("America/Chicago");
  TimeSource dummySourceDec1_5h = this.dummyTimeSource(this.dec1_5am_gmt);

  public void tearDown() {
    Clock.reset();
  }

  public void testNow() {
    Clock.setTimeSource(this.dummySourceDec1_5h);
    Assert.assertEquals(this.dec1_5am_gmt, Clock.now());
  }

  //[ 1466694 ] Clock.now() should use default TimeSource
  public void testNowDoesntBreak() {
    Exception possibleNullPointerException = null;
    try {
      Clock.now();
    } catch (Exception exceptionalEvent) {
      possibleNullPointerException = exceptionalEvent;
    }
    Assert.assertNull(possibleNullPointerException);
  }

  public void testToday() {
    Clock.setTimeSource(this.dummySourceDec1_5h);

    Clock.setDefaultTimeZone(this.gmt);
    Assert.assertEquals(CalendarDate.date(2004, 12, 1), Clock.today());
    Assert.assertEquals(this.dec1_5am_gmt, Clock.now());

    Clock.setDefaultTimeZone(this.pt);
    Assert.assertEquals(CalendarDate.date(2004, 11, 30), Clock.today());
    Assert.assertEquals(this.dec1_5am_gmt, Clock.now());

  }

  public void testTodayWithoutTimeZone() {
    Clock.setTimeSource(this.dummySourceDec1_5h);

    try {
      Clock.today();
      Assert.fail("Clock cannot answer today() without a timezone.");
    } catch (RuntimeException e) {
      Assert.assertTrue("Correctly threw exception", true);
    }

  }

  private TimeSource dummyTimeSource(final TimePoint returnValueForNow) {
    return new TimeSource() {
      public TimePoint now() {
        return returnValueForNow;
      }
    };
  }
}
