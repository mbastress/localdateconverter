package localdateconverter.converter;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.granite.messaging.amf.io.convert.Converters;
import org.joda.time.Chronology;
import org.joda.time.DateTimeZone;
import org.joda.time.chrono.ISOChronology;
import org.mockito.Mockito;
import org.testng.annotations.Test;

public class LocalDateTimeConverterTest {

    static final int YEAR = 2011;
    static final int MONTH = 3; // 1=Jan, 2=Feb, etc...
    static final int DATE = 30;
    static final int HOUR_OF_DAY = 14;
    static final int MINUTE = 21;
    static final int SECOND = 44;

    final LocalDateTimeConverter converter = new LocalDateTimeConverter(Mockito
            .mock(Converters.class));

    @Test
    public void testInternalCanConvertObjectType() {
        assert converter.internalCanConvert(new localdateconverter.model.LocalDateTime(new Date()),
                org.joda.time.LocalDateTime.class);
        assert !converter.internalCanConvert(new localdateconverter.model.LocalDateTime(new Date()),
                org.joda.time.LocalDate.class);
        assert !converter.internalCanConvert(new localdateconverter.model.LocalDateTime(new Date()),
                org.joda.time.DateTime.class);
        assert !converter.internalCanConvert(new localdateconverter.model.LocalDateTime(new Date()),
                String.class);
        assert !converter.internalCanConvert("hello", String.class);
        assert !converter.internalCanConvert("hello", org.joda.time.LocalDateTime.class);
        assert converter.internalCanConvert(null, org.joda.time.LocalDateTime.class);
        assert !converter.internalCanConvert(new localdateconverter.model.LocalDateTime(new Date()),
                Object.class);
    }

    @Test
    public void testInternalConvertObjectType() {
        final Calendar calendarIn = new GregorianCalendar(YEAR, MONTH - 1, // Calendar month is zero-based
                DATE, HOUR_OF_DAY, MINUTE, SECOND);
        localdateconverter.model.LocalDateTime input = new localdateconverter.model.LocalDateTime(
                calendarIn.getTime());
        assert converter.internalCanConvert(input, org.joda.time.LocalDateTime.class);
        final org.joda.time.LocalDateTime converted = converter.internalConvert(input,
                org.joda.time.LocalDateTime.class);
        assert converted.getYear() == YEAR;
        assert converted.getMonthOfYear() == MONTH;
        assert converted.getDayOfMonth() == DATE;
        assert converted.getHourOfDay() == HOUR_OF_DAY;
        assert converted.getMinuteOfHour() == MINUTE;
        assert converted.getSecondOfMinute() == SECOND;
    }

    @Test
    public void testCanRevert() {
        assert converter.canRevert(new org.joda.time.LocalDateTime());
        assert !converter.canRevert(new org.joda.time.LocalDate());
        assert !converter.canRevert(new org.joda.time.DateTime());
        assert !converter.canRevert(null);
        assert !converter.canRevert(new Object());
    }

    @Test
    public void testRevert() {
        testRevertForTimeZone(ISOChronology.getInstanceUTC());
        testRevertForTimeZone(ISOChronology.getInstance(DateTimeZone.forTimeZone(TimeZone.getTimeZone("America/New_York"))));
    }

    private void testRevertForTimeZone(Chronology tz) {
        org.joda.time.LocalDateTime input = new org.joda.time.LocalDateTime(YEAR, MONTH, DATE, HOUR_OF_DAY, MINUTE,
                SECOND, 0, tz);
        localdateconverter.model.LocalDateTime converted = converter.revert(input);
        Calendar calendarOut = GregorianCalendar.getInstance();
        calendarOut.setTime(converted.getDate());
        assert calendarOut.get(Calendar.YEAR) == YEAR;
        assert calendarOut.get(Calendar.MONTH) == MONTH - 1; // Calendar month is zero-based
        assert calendarOut.get(Calendar.DATE) == DATE;
        assert calendarOut.get(Calendar.HOUR_OF_DAY) == HOUR_OF_DAY;
        assert calendarOut.get(Calendar.MINUTE) == MINUTE;
        assert calendarOut.get(Calendar.SECOND) == SECOND;
    }

}
