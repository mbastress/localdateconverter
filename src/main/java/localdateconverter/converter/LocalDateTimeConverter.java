package localdateconverter.converter;

import java.lang.reflect.Type;

import org.granite.messaging.amf.io.convert.Converter;
import org.granite.messaging.amf.io.convert.Converters;
import org.granite.messaging.amf.io.convert.Reverter;
import org.granite.util.ClassUtil;

/**
 * A converter/reverter for Joda-Time LocalDateTime instances in messages
 * serialized to/deserialized from a Flex client.  Built on the GraniteDS
 * data services framework.
 */
public class LocalDateTimeConverter extends Converter implements Reverter {

    public LocalDateTimeConverter(final Converters converters) {
        super(converters);
    }

    @Override
    protected boolean internalCanConvert(Object value, Type targetType) {
        return org.joda.time.LocalDateTime.class == ClassUtil.classOfType(targetType)
                && (value == null || value instanceof localdateconverter.model.LocalDateTime);
    }

    @Override
    protected org.joda.time.LocalDateTime internalConvert(Object value, Type targetType) {
        return value == null ? null : new org.joda.time.LocalDateTime(
                ((localdateconverter.model.LocalDateTime) value).getDate());
    }

    public boolean canRevert(Object value) {
        return value != null && value.getClass() == org.joda.time.LocalDateTime.class;
    }

    public localdateconverter.model.LocalDateTime revert(Object value) {
        return new localdateconverter.model.LocalDateTime(((org.joda.time.LocalDateTime) value)
                .toDateTime().toDate());
    }

}
