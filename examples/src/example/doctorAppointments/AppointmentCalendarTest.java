/**
 * Copyright (c) 2004 Domain Language, Inc. (http://domainlanguage.com)
 * This free software is distributed under the "MIT" licence. See file licence.txt. 
 * For more information, see http://timeandmoney.sourceforge.net.
 */

package example.doctorAppointments;

import java.util.*;

import junit.framework.*;

import com.domainlanguage.time.*;


public class AppointmentCalendarTest extends TestCase {
	
	public void testEventsForDate() {
		TimeZone pt = TimeZone.getTimeZone("America/Los_Angeles");

		TimePoint jun7at10 = TimePoint.at(2004, 6, 7, 10, 0, 0, 0, pt);
		CalendarEvent shortEvent = new CalendarEvent();
		TimeInterval shortTime = TimeInterval.startingFrom(jun7at10, Duration.hours(3));
		shortEvent.setTimeInterval(shortTime);
		
		TimePoint jun9at13 = TimePoint.at(2004, 6, 9, 13, 0, 0, 0, pt);
		CalendarEvent longEvent = new CalendarEvent();
		TimeInterval longTime = TimeInterval.over(jun7at10, jun9at13);
		longEvent.setTimeInterval(longTime);
		
		EventCalendar cal = new EventCalendar(pt);
		cal.add(shortEvent);
		cal.add(longEvent);
		
		assertEquals(2, cal.eventsFor(CalendarDate.date(2004, 6, 7)).size());
		assertEquals(1, cal.eventsFor(CalendarDate.date(2004, 6, 8)).size());
		assertEquals(0, cal.eventsFor(CalendarDate.date(2004, 6, 6)).size());
		
	}
	
}