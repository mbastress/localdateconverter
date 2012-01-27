package localdateconverter.model;

import com.google.common.base.Objects;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Internally stores a date as a string without a time zone component.  A local
 * date is used when there is a fixed notion of date and/or time which
 * remains independent of the local time zone (e.g. a date and time associated
 * with an event at a known or implied location, such as date of birth).
 *
 * This class facilitates serialization of Joda-Time LocalDateTime instances to
 * clients written in languages that cannot correctly convert a date internally
 * stored in milliseconds since the Java epoch.  This may be the case if they
 * do not have a complete daylight savings or time zone database.
 *
 * Not thread-safe.
 *
 * @author Matt Bastress
 */
public class LocalDateTime implements Serializable {

    /** The format used to serialize dates internally. */
    static final String FORMAT = "MM/dd/yyyy HH:mm:ss.SSS";

    /** Internal String representation of the date. */
    String serialized;

    /** Cached date value that does not persist through serialization. */
    private transient Date cached;

    /** The default time zone can be overridden by setting this variable. */
    transient TimeZone timeZone;

    /** Constructor for serialization. */
    protected LocalDateTime() {
    }

    /**
     * Constructor used internally for testing.
     *
     * This constructor accepts a timezone which is used in serializing and
     * parsing the string representation of the date. Allows overriding the JVM
     * default time zone.
     *
     * @param date
     * @param timeZone
     */
    LocalDateTime(Date date, TimeZone timeZone) {
        this.timeZone = timeZone;
        setDate(date);
    }

    /**
     * Constructs an instance which internally serializes a date in the default
     * time zone.
     *
     * @param date
     */
    public LocalDateTime(Date date) {
        this(date, null);
    }

    protected void setDate(Date date) {
        cached = date;
        serialized = serialize(date, timeZone);
    }

    /**
     * Converts the instance to a date object in the default time zone.
     *
     * @return The date as a {@link Date} type.
     */
    public Date getDate() {
        if (cached == null) {
            cached = parse(serialized, timeZone);
        }
        return cached;
    }

    /**
     * Serializes a date using a date formatter set to a given time zone.
     *
     * @param date
     *            Date to serialize.
     * @param timeZone
     *            Time zone to use for serialization.
     * @return A String representation of the date.
     */
    static String serialize(Date date, TimeZone timeZone) {
        return newDateFormat(timeZone).format(date);
    }

    /**
     * Parses a serialized representation of a date, using the given time zone.
     *
     * @param serialized
     *            String representing the date, which must be in the pattern
     *            specified by TimeZoneFree.FORMAT.
     * @param timeZone
     *            Time zone to use for parsing.
     * @return A {@link Date} object.
     */
    static Date parse(String serialized, TimeZone timeZone) {
        try {
            return newDateFormat(timeZone).parse(serialized);
        } catch (ParseException e) {
            throw new DeserializationException(
                    "Serialized literal date does not match the pattern " + FORMAT + ": "
                            + serialized, e);
        }
    }

    /**
     * Creates a new DateFormat instance in the specified time zone that uses
     * LocalDateTime.FORMAT as the pattern.
     *
     * @param timeZone
     *            Time zone to use.
     * @return A DateFormat to use for parsing or serialization.
     */
    private static DateFormat newDateFormat(TimeZone timeZone) {
        DateFormat dateFormat = new SimpleDateFormat(FORMAT);
        if (timeZone != null) {
            dateFormat.setTimeZone(timeZone);
        }
        return dateFormat;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (serialized == null ? 0 : serialized.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof LocalDateTime))
            return false;
        LocalDateTime other = (LocalDateTime) obj;
        if (serialized == null) {
            if (other.serialized != null)
                return false;
        } else if (!serialized.equals(other.serialized))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return buildToString().toString();
    }

    protected Objects.ToStringHelper buildToString() {
        return Objects.toStringHelper(this).add("serialized", serialized);
    }

    /**
     * An exception related to deserializing a LocalDateTime instance.
     *
     * @author Matt Bastress
     */
    public static class DeserializationException extends RuntimeException {
        public DeserializationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
