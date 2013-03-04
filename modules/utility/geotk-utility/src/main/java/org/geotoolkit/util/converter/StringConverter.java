/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.util.converter;

import java.io.Serializable;
import java.io.ObjectStreamException;
import java.nio.charset.UnsupportedCharsetException;
import java.net.URISyntaxException;
import java.net.MalformedURLException;
import net.jcip.annotations.Immutable;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Locales;
import org.apache.sis.util.iso.Types;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.internal.InternalUtilities;
import org.geotoolkit.util.SimpleInternationalString;


/**
 * Handles conversions from {@link java.lang.String} to various objects.
 *
 * @author Justin Deoliveira (TOPP)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.02
 *
 * @since 2.4
 * @module
 */
@Immutable
abstract class StringConverter<T> extends SimpleConverter<String,T> implements Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -3397013355582381432L;

    /**
     * Returns the source class, which is always {@link String}.
     */
    @Override
    public final Class<String> getSourceClass() {
        return String.class;
    }

    /**
     * Returns {@code true} since subclasses can not convert any {@link String} instances.
     */
    @Override
    public boolean hasRestrictions() {
        return true;
    }

    /**
     * Returns {@code true} since subclasses do not preserve order.
     */
    @Override
    public boolean isOrderPreserving() {
        return false;
    }


    /**
     * Converter from {@link java.lang.String} to {@link java.lang.Number}.
     * The finest suitable kind of number will be selected.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.02
     *
     * @since 3.00
     */
    @Immutable
    static final class Number extends StringConverter<java.lang.Number> {
        private static final long serialVersionUID = 1557277544742023571L;
        public static final Number INSTANCE = new Number();
        private Number() {
        }

        @Override
        public Class<java.lang.Number> getTargetClass() {
            return java.lang.Number.class;
        }

        @Override
        public java.lang.Number convert(String source) throws NonconvertibleObjectException {
            if (source == null) {
                return null;
            }
            source = source.trim();
            try {
                return Numbers.finestNumber(source);
            } catch (NumberFormatException e) {
                throw new NonconvertibleObjectException(e);
            }
        }

        /** Returns the singleton instance on deserialization. */
        protected Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }
    }


    /**
     * Converter from {@link java.lang.String} to {@link java.lang.Double}.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.02
     *
     * @since 3.00
     */
    @Immutable
    static final class Double extends StringConverter<java.lang.Double> {
        private static final long serialVersionUID = -9094071164371643060L;
        public static final Double INSTANCE = new Double();
        private Double() {
        }

        @Override
        public Class<java.lang.Double> getTargetClass() {
            return java.lang.Double.class;
        }

        @Override
        public java.lang.Double convert(String source) throws NonconvertibleObjectException {
            if (source == null) {
                return null;
            }
            source = source.trim();
            try {
                return java.lang.Double.parseDouble(source);
            } catch (NumberFormatException e) {
                throw new NonconvertibleObjectException(e);
            }
        }

        /** Returns the singleton instance on deserialization. */
        protected Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }
    }


    /**
     * Converter from {@link java.lang.String} to {@link java.lang.Float}.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.02
     *
     * @since 3.00
     */
    @Immutable
    static final class Float extends StringConverter<java.lang.Float> {
        private static final long serialVersionUID = -2815192289550338333L;
        public static final Float INSTANCE = new Float();
        private Float() {
        }

        @Override
        public Class<java.lang.Float> getTargetClass() {
            return java.lang.Float.class;
        }

        @Override
        public java.lang.Float convert(String source) throws NonconvertibleObjectException {
            if (source == null) {
                return null;
            }
            source = source.trim();
            try {
                return java.lang.Float.parseFloat(source);
            } catch (NumberFormatException e) {
                throw new NonconvertibleObjectException(e);
            }
        }

        /** Returns the singleton instance on deserialization. */
        protected Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }
    }


    /**
     * Converter from {@link java.lang.String} to {@link java.lang.Long}.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.02
     *
     * @since 3.00
     */
    @Immutable
    static final class Long extends StringConverter<java.lang.Long> {
        private static final long serialVersionUID = -2171263041723939779L;
        public static final Long INSTANCE = new Long();
        private Long() {
        }

        @Override
        public Class<java.lang.Long> getTargetClass() {
            return java.lang.Long.class;
        }

        @Override
        public java.lang.Long convert(String source) throws NonconvertibleObjectException {
            if (source == null) {
                return null;
            }
            source = source.trim();
            try {
                return java.lang.Long.parseLong(source);
            } catch (NumberFormatException e) {
                throw new NonconvertibleObjectException(e);
            }
        }

        /** Returns the singleton instance on deserialization. */
        protected Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }
    }


    /**
     * Converter from {@link java.lang.String} to {@link java.lang.Integer}.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.02
     *
     * @since 3.00
     */
    @Immutable
    static final class Integer extends StringConverter<java.lang.Integer> {
        private static final long serialVersionUID = 763211364703205967L;
        public static final Integer INSTANCE = new Integer();
        private Integer() {
        }

        @Override
        public Class<java.lang.Integer> getTargetClass() {
            return java.lang.Integer.class;
        }

        @Override
        public java.lang.Integer convert(String source) throws NonconvertibleObjectException {
            if (source == null) {
                return null;
            }
            source = source.trim();
            try {
                return java.lang.Integer.parseInt(source);
            } catch (NumberFormatException e) {
                throw new NonconvertibleObjectException(e);
            }
        }

        /** Returns the singleton instance on deserialization. */
        protected Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }
    }


    /**
     * Converter from {@link java.lang.String} to {@link java.lang.Short}.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.02
     *
     * @since 3.00
     */
    @Immutable
    static final class Short extends StringConverter<java.lang.Short> {
        private static final long serialVersionUID = -1770870328699572960L;
        public static final Short INSTANCE = new Short();
        private Short() {
        }

        @Override
        public Class<java.lang.Short> getTargetClass() {
            return java.lang.Short.class;
        }

        @Override
        public java.lang.Short convert(String source) throws NonconvertibleObjectException {
            if (source == null) {
                return null;
            }
            source = source.trim();
            try {
                return java.lang.Short.parseShort(source);
            } catch (NumberFormatException e) {
                throw new NonconvertibleObjectException(e);
            }
        }

        /** Returns the singleton instance on deserialization. */
        protected Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }
    }


    /**
     * Converter from {@link java.lang.String} to {@link java.lang.Byte}.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.02
     *
     * @since 3.00
     */
    @Immutable
    static final class Byte extends StringConverter<java.lang.Byte> {
        private static final long serialVersionUID = 2084870859391804185L;
        public static final Byte INSTANCE = new Byte();
        private Byte() {
        }

        @Override
        public Class<java.lang.Byte> getTargetClass() {
            return java.lang.Byte.class;
        }

        @Override
        public java.lang.Byte convert(String source) throws NonconvertibleObjectException {
            if (source == null) {
                return null;
            }
            source = source.trim();
            try {
                return java.lang.Byte.parseByte(source);
            } catch (NumberFormatException e) {
                throw new NonconvertibleObjectException(e);
            }
        }

        /** Returns the singleton instance on deserialization. */
        protected Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }
    }


    /**
     * Converter from {@link java.lang.String} to {@link java.math.BigDecimal}.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.02
     *
     * @since 3.02
     */
    @Immutable
    static final class BigDecimal extends StringConverter<java.math.BigDecimal> {
        private static final long serialVersionUID = -8597497425876120213L;
        public static final BigDecimal INSTANCE = new BigDecimal();
        private BigDecimal() {
        }

        @Override
        public Class<java.math.BigDecimal> getTargetClass() {
            return java.math.BigDecimal.class;
        }

        @Override
        public java.math.BigDecimal convert(String source) throws NonconvertibleObjectException {
            if (source == null) {
                return null;
            }
            source = source.trim();
            try {
                return new java.math.BigDecimal(source);
            } catch (NumberFormatException e) {
                throw new NonconvertibleObjectException(e);
            }
        }

        /** Returns the singleton instance on deserialization. */
        protected Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }
    }


    /**
     * Converter from {@link java.lang.String} to {@link java.math.BigInteger}.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.02
     *
     * @since 3.02
     */
    @Immutable
    static final class BigInteger extends StringConverter<java.math.BigInteger> {
        private static final long serialVersionUID = 8658903031519526466L;
        public static final BigInteger INSTANCE = new BigInteger();
        private BigInteger() {
        }

        @Override
        public Class<java.math.BigInteger> getTargetClass() {
            return java.math.BigInteger.class;
        }

        @Override
        public java.math.BigInteger convert(String source) throws NonconvertibleObjectException {
            if (source == null) {
                return null;
            }
            source = source.trim();
            try {
                return new java.math.BigInteger(source);
            } catch (NumberFormatException e) {
                throw new NonconvertibleObjectException(e);
            }
        }

        /** Returns the singleton instance on deserialization. */
        protected Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }
    }


    /**
     * Converter from {@link java.lang.String} to {@link java.lang.Boolean}.
     * Conversion table:
     *
     * <table>
     *    <tr><th>source</th>          <th>target</th></tr>
     *    <tr><td>{@code "true"}  </td><td>{@link java.lang.Boolean#TRUE}  </td></tr>
     *    <tr><td>{@code "false"} </td><td>{@link java.lang.Boolean#FALSE} </td></tr>
     *    <tr><td>{@code "yes"}   </td><td>{@link java.lang.Boolean#TRUE}  </td></tr>
     *    <tr><td>{@code "no"}    </td><td>{@link java.lang.Boolean#FALSE} </td></tr>
     *    <tr><td>{@code "on"}    </td><td>{@link java.lang.Boolean#TRUE}  </td></tr>
     *    <tr><td>{@code "off"}   </td><td>{@link java.lang.Boolean#FALSE} </td></tr>
     *    <tr><td>{@code "1"}     </td><td>{@link java.lang.Boolean#TRUE}  </td></tr>
     *    <tr><td>{@code "0"}     </td><td>{@link java.lang.Boolean#FALSE} </td></tr>
     * </table>
     *
     * @author Justin Deoliveira (TOPP)
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.00
     *
     * @since 2.4
     */
    @Immutable
    static final class Boolean extends StringConverter<java.lang.Boolean> {
        private static final long serialVersionUID = -27525398425996373L;
        public static final Boolean INSTANCE = new Boolean();
        private Boolean() {
        }

        @Override
        public Class<java.lang.Boolean> getTargetClass() {
            return java.lang.Boolean.class;
        }

        @Override
        public java.lang.Boolean convert(String source) throws NonconvertibleObjectException {
            if (source == null) {
                return null;
            }
            source = source.trim();
            if (source.equalsIgnoreCase("true") ||
                source.equalsIgnoreCase("yes")  ||
                source.equalsIgnoreCase("on"))
            {
                return java.lang.Boolean.TRUE;
            }
            if (source.equalsIgnoreCase("false") ||
                source.equalsIgnoreCase("no")    ||
                source.equalsIgnoreCase("off"))
            {
                return java.lang.Boolean.FALSE;
            }
            final int n;
            try {
                n = java.lang.Integer.parseInt(source);
            } catch (NumberFormatException e) {
                throw new NonconvertibleObjectException(e);
            }
            return java.lang.Boolean.valueOf(n != 0);
        }

        /** Returns the singleton instance on deserialization. */
        protected Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }
    }


    /**
     * Converter from {@link java.lang.String} to {@link java.awt.Color}.
     * If the string begins with a leading {@code '#'} character (as in HTML codes),
     * then the remaining is understood as an hexadecimal number.
     *
     * @author Justin Deoliveira (TOPP)
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.19
     *
     * @since 2.4
     */
    @Immutable
    static final class Color extends StringConverter<java.awt.Color> {
        private static final long serialVersionUID = 5294622747871370401L;
        public static final Color INSTANCE = new Color();
        private Color() {
        }

        @Override
        public Class<java.awt.Color> getTargetClass() {
            return java.awt.Color.class;
        }

        @Override
        public java.awt.Color convert(String source) throws NonconvertibleObjectException {
            if (source == null) {
                return null;
            }
            source = source.trim();
            try {
                return new java.awt.Color(InternalUtilities.parseColor(source), true);
            } catch (NumberFormatException e) {
                throw new NonconvertibleObjectException(e);
            }
        }

        /** Returns the singleton instance on deserialization. */
        protected Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }
    }


    /**
     * Converter from {@link java.lang.String} to {@link java.util.Locale}.
     * Examples of locale in string form: {@code "fr"}, {@code "fr_CA"}.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.02
     *
     * @since 2.0
     */
    @Immutable
    static final class Locale extends StringConverter<java.util.Locale> {
        private static final long serialVersionUID = -2888932450292616036L;
        public static final Locale INSTANCE = new Locale();
        private Locale() {
        }

        /**
         * The source class, which is Java {@code Locale}.
         */
        @Override
        public Class<java.util.Locale> getTargetClass() {
            return java.util.Locale.class;
        }

        @Override
        public java.util.Locale convert(String source) throws NonconvertibleObjectException {
            if (source == null) {
                return null;
            }
            source = source.trim();
            try {
                return Locales.parse(source);
            } catch (IllegalArgumentException e) {
                throw new NonconvertibleObjectException(Errors.format(Errors.Keys.ILLEGAL_LANGUAGE_CODE_$1, source), e);
            }
        }

        /** Returns the singleton instance on deserialization. */
        protected Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }
    }


    /**
     * Converter from {@link java.lang.String} to {@link java.nio.charset.Charset}.
     *
     * @author Justin Deoliveira (TOPP)
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.02
     *
     * @since 2.4
     */
    @Immutable
    static final class Charset extends StringConverter<java.nio.charset.Charset> {
        private static final long serialVersionUID = 4539755855992944656L;
        public static final Charset INSTANCE = new Charset();
        private Charset() {
        }

        @Override
        public Class<java.nio.charset.Charset> getTargetClass() {
            return java.nio.charset.Charset.class;
        }

        @Override
        public java.nio.charset.Charset convert(String source) throws NonconvertibleObjectException {
            if (source == null) {
                return null;
            }
            source = source.trim();
            try {
                return java.nio.charset.Charset.forName(source);
            }
            catch (UnsupportedCharsetException e) {
                throw new NonconvertibleObjectException(e);
            }
        }

        /** Returns the singleton instance on deserialization. */
        protected Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }
    }


    /**
     * Converter from {@link java.lang.String} to {@link org.opengis.util.InternationalString}.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.02
     *
     * @since 3.02
     */
    @Immutable
    static final class InternationalString extends StringConverter<org.opengis.util.InternationalString> {
        private static final long serialVersionUID = 730809620191573819L;
        public static final InternationalString INSTANCE = new InternationalString();
        private InternationalString() {
        }

        @Override
        public Class<org.opengis.util.InternationalString> getTargetClass() {
            return org.opengis.util.InternationalString.class;
        }

        @Override
        public org.opengis.util.InternationalString convert(final String source) {
            if (source == null) {
                return null;
            }
            return new SimpleInternationalString(source);
        }

        @Override
        public boolean hasRestrictions() {
            return false;
        }

        /** Returns the singleton instance on deserialization. */
        protected Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }
    }


    /**
     * Converter from {@link java.lang.String} to {@link java.io.File}.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.00
     *
     * @since 3.00
     */
    @Immutable
    static final class File extends StringConverter<java.io.File> {
        private static final long serialVersionUID = 6445208470928432376L;
        public static final File INSTANCE = new File();
        private File() {
        }

        @Override
        public Class<java.io.File> getTargetClass() {
            return java.io.File.class;
        }

        @Override
        public java.io.File convert(final String source) {
            if (source == null) {
                return null;
            }
            return new java.io.File(source);
        }

        /** Returns the singleton instance on deserialization. */
        protected Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }
    }


    /**
     * Converter from {@link java.lang.String} to {@link java.net.URI}.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.00
     *
     * @since 3.00
     */
    @Immutable
    static final class URI extends StringConverter<java.net.URI> {
        private static final long serialVersionUID = -2804405634789179706L;
        public static final URI INSTANCE = new URI();
        private URI() {
        }

        @Override
        public Class<java.net.URI> getTargetClass() {
            return java.net.URI.class;
        }

        @Override
        public java.net.URI convert(final String source) throws NonconvertibleObjectException {
            if (source == null) {
                return null;
            }
            try {
                return new java.net.URI(IOUtilities.encodeURI(source));
            } catch (URISyntaxException e) {
                throw new NonconvertibleObjectException(e);
            }
        }

        /** Returns the singleton instance on deserialization. */
        protected Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }
    }


    /**
     * Converter from {@link java.lang.String} to {@link java.net.URL}.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.00
     *
     * @since 3.00
     */
    @Immutable
    static final class URL extends StringConverter<java.net.URL> {
        private static final long serialVersionUID = 2303928306635765592L;
        public static final URL INSTANCE = new URL();
        private URL() {
        }

        @Override
        public Class<java.net.URL> getTargetClass() {
            return java.net.URL.class;
        }

        @Override
        public java.net.URL convert(final String source) throws NonconvertibleObjectException {
            if (source == null) {
                return null;
            }
            try {
                return new java.net.URL(source);
            } catch (MalformedURLException e) {
                throw new NonconvertibleObjectException(e);
            }
        }

        /** Returns the singleton instance on deserialization. */
        protected Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }
    }


    /**
     * Converter from {@link java.lang.String} to {@link org.opengis.util.CodeList}.
     * This converter is particular in that it requires the target class in argument
     * to the constructor.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.02
     *
     * @since 3.02
     */
    @Immutable
    static final class CodeList<T extends org.opengis.util.CodeList<T>> extends StringConverter<T> {
        private static final long serialVersionUID = 3289083947166861278L;

        /** The type of the code list. */
        private final Class<T> targetType;

        /** Creates a new converter for the given code list. */
        static <T extends org.opengis.util.CodeList<T>> CodeList<T> create(final Class<T> targetType) {
            return new CodeList<T>(targetType);
        }

        private CodeList(final Class<T> targetType) {
            this.targetType = targetType;
        }

        @Override
        public Class<? extends T> getTargetClass() {
            return targetType;
        }

        @Override
        public T convert(final String source) {
            if (source == null) {
                return null;
            }
            return Types.forCodeName(targetType, source, true);
        }
    }
}
