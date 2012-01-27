package localdateconverter.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class LocalDateTimeTest {

    static final int YEAR = 2005;
    static final int MONTH = 5; // zero-based
    static final int DATE = 30;
    static final int HOUR_OF_DAY = 14;
    static final int MINUTE = 21;
    static final int SECOND = 44;

    public void testDateForward() {
        Calendar calendar = new GregorianCalendar(YEAR, MONTH, DATE, HOUR_OF_DAY, MINUTE, SECOND);
        Date date = calendar.getTime();
        String serialized = LocalDateTime.serialize(date, null);
        LocalDateTime literal = new LocalDateTime(date);
        Assert.assertEquals(serialized, literal.serialized);
        Assert.assertEquals(date, literal.getDate());
    }

    public void testDateReverse() {
        Calendar calendar = new GregorianCalendar(YEAR, MONTH, DATE, HOUR_OF_DAY, MINUTE, SECOND);
        Date date = calendar.getTime();
        String serialized = new SimpleDateFormat(LocalDateTime.FORMAT).format(date);
        LocalDateTime literal = new LocalDateTime(
                LocalDateTime.parse(serialized, null));
        Assert.assertEquals(serialized, literal.serialized);
        Assert.assertEquals(date, literal.getDate());
    }

    public void testSameDatesDifferentTimeZonesSerializeToTheSameString() {
        String serializedEST = getNewSerializedTestDateForTimeZone(TimeZone.getTimeZone("America/New_York"));
        String serializedUTC = getNewSerializedTestDateForTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        Assert.assertEquals(serializedEST, serializedUTC);
    }

    private String getNewSerializedTestDateForTimeZone(final TimeZone timeZone) {
        Calendar calendar = new GregorianCalendar(YEAR, MONTH, DATE, HOUR_OF_DAY, MINUTE, SECOND);
        calendar.setTimeZone(timeZone);
        Date date = calendar.getTime();
        LocalDateTime literal = new LocalDateTime(date, timeZone);
        return literal.serialized;
    }

    public void testSerializeAcrossTimeZones() {

        // serialize in EDT
        Calendar calendarEDT = new GregorianCalendar(YEAR, MONTH, DATE, HOUR_OF_DAY, MINUTE, SECOND);
        calendarEDT.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        String serializedEDT = new LocalDateTime(calendarEDT.getTime(), TimeZone
                .getTimeZone("America/New_York")).serialized;

        // deserialize in UTC
        LocalDateTime literal = new LocalDateTime(
                LocalDateTime.parse(serializedEDT, TimeZone.getTimeZone("Etc/UTC")));
        Calendar calendarUTC = new GregorianCalendar(TimeZone.getTimeZone("Etc/UTC"));
        calendarUTC.setTime(literal.getDate());

        // they are different times in absolute terms
        Assert.assertTrue(calendarEDT.getTimeInMillis() != calendarUTC.getTimeInMillis());

        // but the same date irrespective of time zone
        Assert.assertEquals(calendarEDT.get(Calendar.YEAR), YEAR);
        Assert.assertEquals(calendarEDT.get(Calendar.MONTH), MONTH);
        Assert.assertEquals(calendarEDT.get(Calendar.DATE), DATE);
        Assert.assertEquals(calendarEDT.get(Calendar.HOUR_OF_DAY), HOUR_OF_DAY);
        Assert.assertEquals(calendarEDT.get(Calendar.MINUTE), MINUTE);
        Assert.assertEquals(calendarEDT.get(Calendar.SECOND), SECOND);
    }
}
