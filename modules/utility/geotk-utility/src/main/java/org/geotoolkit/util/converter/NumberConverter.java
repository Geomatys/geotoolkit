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
import net.jcip.annotations.Immutable;
import org.apache.sis.util.Numbers;


/**
 * Handles conversions from {@link java.lang.Number} to various objects.
 *
 * @author Justin Deoliveira (TOPP)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.02
 *
 * @since 2.4
 * @module
 *
 * @deprecated Replaced by Apache SIS {@link org.apache.sis.util.ObjectConverters}.
 */
@Deprecated
@Immutable
abstract class NumberConverter<T> extends SimpleConverter<Number,T> implements Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -8715054480508622025L;

    /**
     * Returns the source class, which is always {@link Number}.
     */
    @Override
    public final Class<Number> getSourceClass() {
        return Number.class;
    }

    /**
     * Returns {@code false} since subclasses do not preserve order.
     */
    @Override
    public boolean isOrderPreserving() {
        return false;
    }


    /**
     * Converter from numbers to comparables. This special case exists because {@link Number}
     * does not implement {@link java.lang.Comparable} directly, but all known subclasses do.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.01
     *
     * @since 3.01
     */
    @Immutable
    static final class Comparable extends NumberConverter<java.lang.Comparable<?>> {
        private static final long serialVersionUID = 3716134638218072176L;
        public static final Comparable INSTANCE = new Comparable();
        private Comparable() {
        }

        @Override
        @SuppressWarnings({"rawtypes","unchecked"})
        public Class<java.lang.Comparable<?>> getTargetClass() {
            return (Class) java.lang.Comparable.class;
        }

        @Override
        public java.lang.Comparable<?> convert(final Number source) throws NonconvertibleObjectException {
            if (source == null || source instanceof java.lang.Comparable<?>) {
                return (java.lang.Comparable<?>) source;
            }
            return source.doubleValue();
        }

        /** Returns the singleton instance on deserialization. */
        protected Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }
    }


    /**
     * Converter from numbers to strings.
     *
     * @author Justin Deoliveira (TOPP)
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.00
     *
     * @since 2.4
     */
    @Immutable
    static final class String extends NumberConverter<java.lang.String> {
        private static final long serialVersionUID = 1460382215827540172L;
        public static final String INSTANCE = new String();
        private String() {
        }

        @Override
        public Class<java.lang.String> getTargetClass() {
            return java.lang.String.class;
        }

        @Override
        public java.lang.String convert(final Number source) throws NonconvertibleObjectException {
            return (source != null) ? source.toString() : null;
        }

        /** Returns the singleton instance on deserialization. */
        protected Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }
    }


    /**
     * Converter from numbers to doubles.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.00
     *
     * @since 3.00
     */
    @Immutable
    static final class Double extends NumberConverter<java.lang.Double> {
        private static final long serialVersionUID = 1643009985070268985L;
        public static final Double INSTANCE = new Double();
        private Double() {
        }

        @Override
        public Class<java.lang.Double> getTargetClass() {
            return java.lang.Double.class;
        }

        @Override
        public java.lang.Double convert(final Number source) throws NonconvertibleObjectException {
            return (source != null) ? java.lang.Double.valueOf(source.doubleValue()) : null;
        }

        /** Returns the singleton instance on deserialization. */
        protected Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }
    }


    /**
     * Converter from numbers to floats.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.00
     *
     * @since 3.00
     */
    @Immutable
    static final class Float extends NumberConverter<java.lang.Float> {
        private static final long serialVersionUID = -5900985555014433974L;
        public static final Float INSTANCE = new Float();
        private Float() {
        }

        @Override
        public Class<java.lang.Float> getTargetClass() {
            return java.lang.Float.class;
        }

        @Override
        public java.lang.Float convert(final Number source) throws NonconvertibleObjectException {
            return (source != null) ? java.lang.Float.valueOf(source.floatValue()) : null;
        }

        /** Returns the singleton instance on deserialization. */
        protected Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }
    }


    /**
     * Converter from numbers to longs.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.00
     *
     * @since 3.00
     */
    @Immutable
    static final class Long extends NumberConverter<java.lang.Long> {
        private static final long serialVersionUID = -5320144566275003574L;
        public static final Long INSTANCE = new Long();
        private Long() {
        }

        @Override
        public Class<java.lang.Long> getTargetClass() {
            return java.lang.Long.class;
        }

        @Override
        public java.lang.Long convert(final Number source) throws NonconvertibleObjectException {
            return (source != null) ? java.lang.Long.valueOf(source.longValue()) : null;
        }

        /** Returns the singleton instance on deserialization. */
        protected Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }
    }


    /**
     * Converter from numbers to integers.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.00
     *
     * @since 3.00
     */
    @Immutable
    static final class Integer extends NumberConverter<java.lang.Integer> {
        private static final long serialVersionUID = 2661178278691398269L;
        public static final Integer INSTANCE = new Integer();
        private Integer() {
        }

        @Override
        public Class<java.lang.Integer> getTargetClass() {
            return java.lang.Integer.class;
        }

        @Override
        public java.lang.Integer convert(final Number source) throws NonconvertibleObjectException {
            return (source != null) ? java.lang.Integer.valueOf(source.intValue()) : null;
        }

        /** Returns the singleton instance on deserialization. */
        protected Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }
    }


    /**
     * Converter from numbers to shorts.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.00
     *
     * @since 3.00
     */
    @Immutable
    static final class Short extends NumberConverter<java.lang.Short> {
        private static final long serialVersionUID = -5943559376400249179L;
        public static final Short INSTANCE = new Short();
        private Short() {
        }

        @Override
        public Class<java.lang.Short> getTargetClass() {
            return java.lang.Short.class;
        }

        @Override
        public java.lang.Short convert(final Number source) throws NonconvertibleObjectException {
            return (source != null) ? java.lang.Short.valueOf(source.shortValue()) : null;
        }

        /** Returns the singleton instance on deserialization. */
        protected Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }
    }


    /**
     * Converter from numbers to shorts.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.00
     *
     * @since 3.00
     */
    @Immutable
    static final class Byte extends NumberConverter<java.lang.Byte> {
        private static final long serialVersionUID = 1381038535870541045L;
        public static final Byte INSTANCE = new Byte();
        private Byte() {
        }

        @Override
        public Class<java.lang.Byte> getTargetClass() {
            return java.lang.Byte.class;
        }

        @Override
        public java.lang.Byte convert(final Number source) throws NonconvertibleObjectException {
            return (source != null) ? java.lang.Byte.valueOf(source.byteValue()) : null;
        }

        /** Returns the singleton instance on deserialization. */
        protected Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }
    }


    /**
     * Converter from numbers to booleans. Values in the range (-1..1) exclusive
     * and NaN values are understood as "false", and all other values as "true".
     *
     * @author Justin Deoliveira (TOPP)
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.00
     *
     * @since 2.4
     */
    @Immutable
    static final class Boolean extends NumberConverter<java.lang.Boolean> {
        private static final long serialVersionUID = -7522980351031833731L;
        public static final Boolean INSTANCE = new Boolean();
        private Boolean() {
        }

        @Override
        public Class<java.lang.Boolean> getTargetClass() {
            return java.lang.Boolean.class;
        }

        @Override
        public java.lang.Boolean convert(final Number source) throws NonconvertibleObjectException {
            return (source != null) ? java.lang.Boolean.valueOf(source.intValue() != 0) : null;
        }

        /** Returns the singleton instance on deserialization. */
        protected Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }
    }


    /**
     * Converter from numbers to {@link java.math.BigDecimal}.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.02
     *
     * @since 3.02
     */
    @Immutable
    static final class BigDecimal extends NumberConverter<java.math.BigDecimal> {
        private static final long serialVersionUID = -6318144992861058878L;
        public static final BigDecimal INSTANCE = new BigDecimal();
        private BigDecimal() {
        }

        @Override
        public Class<java.math.BigDecimal> getTargetClass() {
            return java.math.BigDecimal.class;
        }

        @Override
        public java.math.BigDecimal convert(final Number source) throws NonconvertibleObjectException {
            if (source == null) {
                return null;
            }
            if (source instanceof java.math.BigDecimal) {
                return (java.math.BigDecimal) source;
            }
            if (source instanceof java.math.BigInteger) {
                return new java.math.BigDecimal((java.math.BigInteger) source);
            }
            if (Numbers.isInteger(source.getClass())) {
                return java.math.BigDecimal.valueOf(source.longValue());
            }
            return java.math.BigDecimal.valueOf(source.doubleValue());
        }

        /** Returns the singleton instance on deserialization. */
        protected Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }
    }


    /**
     * Converter from numbers to {@link java.math.BigInteger}.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.02
     *
     * @since 3.02
     */
    @Immutable
    static final class BigInteger extends NumberConverter<java.math.BigInteger> {
        private static final long serialVersionUID = 5940724099300523246L;
        public static final BigInteger INSTANCE = new BigInteger();
        private BigInteger() {
        }

        @Override
        public Class<java.math.BigInteger> getTargetClass() {
            return java.math.BigInteger.class;
        }

        @Override
        public java.math.BigInteger convert(final Number source) throws NonconvertibleObjectException {
            if (source == null) {
                return null;
            }
            if (source instanceof java.math.BigInteger) {
                return (java.math.BigInteger) source;
            }
            if (source instanceof java.math.BigDecimal) {
                return ((java.math.BigDecimal) source).toBigInteger();
            }
            return java.math.BigInteger.valueOf(source.longValue());
        }

        /** Returns the singleton instance on deserialization. */
        protected Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }
    }


    /**
     * Converter from numbers to colors. Colors with an alpha of 0 (which would normally
     * be fully transparent pixel) are interpreted as color without alpha component.
     *
     * @author Justin Deoliveira (TOPP)
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.00
     *
     * @since 2.4
     */
    @Immutable
    static final class Color extends NumberConverter<java.awt.Color> {
        private static final long serialVersionUID = 8866612442279600953L;
        public static final Color INSTANCE = new Color();
        private Color() {
        }

        @Override
        public Class<java.awt.Color> getTargetClass() {
            return java.awt.Color.class;
        }

        @Override
        public java.awt.Color convert(final Number source) throws NonconvertibleObjectException {
            if (source == null) {
                return null;
            }
            final int rgba = source.intValue();
            final int alpha = rgba & 0xFF000000;
            return new java.awt.Color(rgba, alpha != 0);
        }

        /** Returns the singleton instance on deserialization. */
        protected Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }
    }
}
