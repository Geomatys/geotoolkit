/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.client.openapi;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeFeature;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class RFC3339JavaTimeModule extends SimpleModule {

    public RFC3339JavaTimeModule() {
        super("RFC3339JavaTimeModule");
        addDeserializer(Instant.class, RFC3339InstantDeserializer.INSTANT);
        addDeserializer(OffsetDateTime.class, RFC3339InstantDeserializer.OFFSET_DATE_TIME);
        addDeserializer(ZonedDateTime.class, RFC3339InstantDeserializer.ZONED_DATE_TIME);
    }

    public static final class RFC3339DateFormat extends DateFormat {

        private static final TimeZone TIMEZONE_Z = TimeZone.getTimeZone("UTC");

        private final StdDateFormat fmt = new StdDateFormat()
                .withTimeZone(TIMEZONE_Z)
                .withColonInTimeZone(true);

        public RFC3339DateFormat() {
            this.calendar = new GregorianCalendar();
            this.numberFormat = new DecimalFormat();
        }

        @Override
        public Date parse(String source) {
            return parse(source, new ParsePosition(0));
        }

        @Override
        public Date parse(String source, ParsePosition pos) {
            return fmt.parse(source, pos);
        }

        @Override
        public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
            return fmt.format(date, toAppendTo, fieldPosition);
        }

        @Override
        public Object clone() {
            return super.clone();
        }
    }

    public static final class RFC3339InstantDeserializer<T extends Temporal> extends InstantDeserializer<T> {

        private final static boolean DEFAULT_NORMALIZE_ZONE_ID = JavaTimeFeature.NORMALIZE_DESERIALIZED_ZONE_ID.enabledByDefault();
        private final static boolean DEFAULT_ALWAYS_ALLOW_STRINGIFIED_DATE_TIMESTAMPS
        = JavaTimeFeature.ALWAYS_ALLOW_STRINGIFIED_DATE_TIMESTAMPS.enabledByDefault();

        public static final RFC3339InstantDeserializer<Instant> INSTANT = new RFC3339InstantDeserializer<>(
            Instant.class, DateTimeFormatter.ISO_INSTANT,
            Instant::from,
            a -> Instant.ofEpochMilli( a.value ),
            a -> Instant.ofEpochSecond( a.integer, a.fraction ),
            null,
            true, // yes, replace zero offset with Z
            DEFAULT_NORMALIZE_ZONE_ID,
            DEFAULT_ALWAYS_ALLOW_STRINGIFIED_DATE_TIMESTAMPS
        );

        public static final RFC3339InstantDeserializer<OffsetDateTime> OFFSET_DATE_TIME = new RFC3339InstantDeserializer<>(
            OffsetDateTime.class, DateTimeFormatter.ISO_OFFSET_DATE_TIME,
            OffsetDateTime::from,
            a -> OffsetDateTime.ofInstant( Instant.ofEpochMilli( a.value ), a.zoneId ),
            a -> OffsetDateTime.ofInstant( Instant.ofEpochSecond( a.integer, a.fraction ), a.zoneId ),
            (d, z) -> ( d.isEqual( OffsetDateTime.MIN ) || d.isEqual( OffsetDateTime.MAX ) ?
            d :
            d.withOffsetSameInstant( z.getRules().getOffset( d.toLocalDateTime() ) ) ),
            true, // yes, replace zero offset with Z
            DEFAULT_NORMALIZE_ZONE_ID,
            DEFAULT_ALWAYS_ALLOW_STRINGIFIED_DATE_TIMESTAMPS
        );

        public static final RFC3339InstantDeserializer<ZonedDateTime> ZONED_DATE_TIME = new RFC3339InstantDeserializer<>(
            ZonedDateTime.class, DateTimeFormatter.ISO_ZONED_DATE_TIME,
            ZonedDateTime::from,
            a -> ZonedDateTime.ofInstant( Instant.ofEpochMilli( a.value ), a.zoneId ),
            a -> ZonedDateTime.ofInstant( Instant.ofEpochSecond( a.integer, a.fraction ), a.zoneId ),
            ZonedDateTime::withZoneSameInstant,
            false, // keep zero offset and Z separate since zones explicitly supported
            DEFAULT_NORMALIZE_ZONE_ID,
            DEFAULT_ALWAYS_ALLOW_STRINGIFIED_DATE_TIMESTAMPS
        );

        protected RFC3339InstantDeserializer(
                Class<T> supportedType,
                DateTimeFormatter formatter,
                Function<TemporalAccessor, T> parsedToValue,
                Function<InstantDeserializer.FromIntegerArguments, T> fromMilliseconds,
                Function<InstantDeserializer.FromDecimalArguments, T> fromNanoseconds,
                BiFunction<T, ZoneId, T> adjust,
                boolean replaceZeroOffsetAsZ,
                boolean normalizeZoneId,
                boolean readNumericStringsAsTimestamp) {
            super(
                    supportedType,
                    formatter,
                    parsedToValue,
                    fromMilliseconds,
                    fromNanoseconds,
                    adjust,
                    replaceZeroOffsetAsZ,
                    normalizeZoneId,
                    readNumericStringsAsTimestamp
            );
        }

        @Override
        protected T _fromString(JsonParser p, DeserializationContext ctxt, String string0) throws IOException {
            return super._fromString(p, ctxt, string0.replace( ' ', 'T' ));
        }
    }

}
