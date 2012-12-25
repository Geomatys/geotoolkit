/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.referencing.datum;

import java.util.Map;
import java.util.Objects;
import java.util.Collections;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.jcip.annotations.Immutable;

import org.opengis.referencing.datum.VerticalDatum;
import org.opengis.referencing.datum.VerticalDatumType;

import org.geotoolkit.io.wkt.Formatter;
import org.geotoolkit.util.Strings;
import org.geotoolkit.util.ComparisonMode;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.internal.jaxb.MarshalContext;
import org.geotoolkit.internal.referencing.VerticalDatumTypes;

import static org.geotoolkit.util.Utilities.hash;
import static org.apache.sis.util.ArgumentChecks.ensureNonNull;


/**
 * A textual description and/or a set of parameters identifying a particular reference level
 * surface used as a zero-height surface. The description includes its position with respect
 * to the Earth for any of the height types recognized by this standard. There are several
 * types of vertical datums, and each may place constraints on the
 * {@linkplain org.opengis.referencing.cs.CoordinateSystemAxis coordinate system axis} with which
 * it is combined to create a {@linkplain org.opengis.referencing.crs.VerticalCRS vertical CRS}.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 1.2
 * @module
 */
@Immutable
@XmlType(name = "VerticalDatumType")
@XmlRootElement(name = "VerticalDatum")
public class DefaultVerticalDatum extends AbstractDatum implements VerticalDatum {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 380347456670516572L;

    /**
     * A copy of the list of vertical types.
     */
    private static final VerticalDatumType[] TYPES = VerticalDatumTypes.values();

    /**
     * Mapping between {@linkplain VerticalDatumType vertical datum type} and the numeric
     * values used in legacy specification (OGC 01-009).
     */
    private static final short[] LEGACY_CODES = new short[TYPES.length];
    static {
        LEGACY_CODES[VerticalDatumType .GEOIDAL      .ordinal()] = 2005; // CS_VD_GeoidModelDerived
        LEGACY_CODES[VerticalDatumTypes.ELLIPSOIDAL  .ordinal()] = 2002; // CS_VD_Ellipsoidal
        LEGACY_CODES[VerticalDatumType .DEPTH        .ordinal()] = 2006; // CS_VD_Depth
        LEGACY_CODES[VerticalDatumType .BAROMETRIC   .ordinal()] = 2003; // CS_VD_AltitudeBarometric
        LEGACY_CODES[VerticalDatumTypes.ORTHOMETRIC  .ordinal()] = 2001; // CS_VD_Orthometric
        LEGACY_CODES[VerticalDatumType .OTHER_SURFACE.ordinal()] = 2000; // CS_VD_Other
    }

    /**
     * The type of this vertical datum. Consider this field as final.
     * If {@code null}, a value will be inferred from the name by {@link #type()}.
     */
    private VerticalDatumType type;

    /**
     * Default vertical datum for {@linkplain VerticalDatumType#BAROMETRIC barometric heights}.
     *
     * @since 3.14
     */
    public static final DefaultVerticalDatum BAROMETRIC =
            new DefaultVerticalDatum(name(Vocabulary.Keys.BAROMETRIC_ALTITUDE), VerticalDatumType.BAROMETRIC);

    /**
     * Default vertical datum for {@linkplain VerticalDatumType#GEOIDAL geoidal heights}.
     */
    public static final DefaultVerticalDatum GEOIDAL =
            new DefaultVerticalDatum(name(Vocabulary.Keys.GEOIDAL), VerticalDatumType.GEOIDAL);

    /**
     * Default vertical datum for ellipsoidal heights. Ellipsoidal heights
     * are measured along the normal to the ellipsoid used in the definition
     * of horizontal datum.
     */
    public static final DefaultVerticalDatum ELLIPSOIDAL =
            new DefaultVerticalDatum(name(Vocabulary.Keys.ELLIPSOIDAL), VerticalDatumTypes.ELLIPSOIDAL);

    /**
     * Default vertical datum for {@linkplain VerticalDatumType#OTHER_SURFACE other surface}.
     *
     * @since 3.14
     */
    public static final DefaultVerticalDatum OTHER_SURFACE =
            new DefaultVerticalDatum(name(Vocabulary.Keys.OTHER), VerticalDatumType.OTHER_SURFACE);

    /**
     * Constructs a new object in which every attributes are set to a default value.
     * <strong>This is not a valid object.</strong> This constructor is strictly
     * reserved to JAXB, which will assign values to the fields using reflexion.
     */
    private DefaultVerticalDatum() {
        super(org.geotoolkit.internal.referencing.NilReferencingObject.INSTANCE);
        // We need to let the DefaultVerticalDatum fields unitialized,
        // because afterUnmarshal will check for null values.
    }

    /**
     * Constructs a vertical datum from a name.
     *
     * @param name   The datum name.
     * @param type   The type of this vertical datum.
     */
    public DefaultVerticalDatum(final String name, final VerticalDatumType type) {
        this(Collections.singletonMap(NAME_KEY, name), type);
    }

    /**
     * Constructs a new datum with the same values than the specified one.
     * This copy constructor provides a way to convert an arbitrary implementation into a
     * Geotk one or a user-defined one (as a subclass), usually in order to leverage
     * some implementation-specific API. This constructor performs a shallow copy,
     * i.e. the properties are not cloned.
     *
     * @param datum The datum to copy.
     *
     * @since 2.2
     */
    public DefaultVerticalDatum(final VerticalDatum datum) {
        super(datum);
        type = datum.getVerticalDatumType();
    }

    /**
     * Constructs a vertical datum from a set of properties. The properties map is given
     * unchanged to the {@linkplain AbstractDatum#AbstractDatum(Map) super-class constructor}.
     *
     * @param properties Set of properties. Should contains at least {@code "name"}.
     * @param type       The type of this vertical datum.
     */
    public DefaultVerticalDatum(final Map<String,?> properties, final VerticalDatumType type) {
        super(properties);
        this.type = type;
        ensureNonNull("type", type);
    }

    /**
     * Returns a Geotk datum implementation with the same values than the given arbitrary
     * implementation. If the given object is {@code null}, then this method returns {@code null}.
     * Otherwise if the given object is already a Geotk implementation, then the given object is
     * returned unchanged. Otherwise a new Geotk implementation is created and initialized to the
     * attribute values of the given object.
     *
     * @param  object The object to get as a Geotk implementation, or {@code null} if none.
     * @return A Geotk implementation containing the values of the given object (may be the
     *         given object itself), or {@code null} if the argument was null.
     *
     * @since 3.18
     */
    public static DefaultVerticalDatum castOrCopy(final VerticalDatum object) {
        return (object == null) || (object instanceof DefaultVerticalDatum)
                ? (DefaultVerticalDatum) object : new DefaultVerticalDatum(object);
    }

    /**
     * Returns {@code true} if the given string was found in the given name, either at the
     * name beginning or after a space. We don't test the character after the given string
     * because the word may be incomplete (e.g. {@code "geoid"} vs {@code "geoidal"}).
     *
     * @param  name      The datum name, in lower cases and mostly US-ASCII characters.
     * @param  candidate The word to search, in lower cases and mostly US-ASCII characters.
     * @return {@code true} if the given string was found in the given name at a word boundary.
     */
    private static boolean find(final StringBuilder name, final String candidate) {
        final int i = name.indexOf(candidate);
        return (i == 0) || (i >= 0 && Character.isWhitespace(name.charAt(i-1)));
    }

    /**
     * Returns the type of this datum, or infers the type from the datum name if no type were
     * specified. The later case occurs after unmarshalling, since GML 3.2 does not contain any
     * attribute for the datum type. It may also happen if the datum were created using reflection.
     * <p>
     * The algorithm used here is determined on a heuristic basis and may be changed in any
     * future version. If the type can not be determined, default on the ellipsoidal type
     * since it will usually implies no additional calculation.
     * <p>
     * No synchronization needed; this is not a problem if this value is computed twice.
     * This method returns only existing immutable instances.
     *
     * @since 3.20
     */
    private VerticalDatumType type() {
        VerticalDatumType type = this.type;
        if (type == null) {
            type = VerticalDatumTypes.ELLIPSOIDAL;
            final String s = getName(null);
            if (s != null) {
                final StringBuilder name = new StringBuilder(s);
                Strings.toASCII(name);
                for (int i=name.length(); --i>=0;) {
                    // Following algorithm doesn't work for all Locale, but it should
                    // be okay since we removed many non-ASCII characters above.
                    name.setCharAt(i, Character.toLowerCase(name.charAt(i)));
                }
                     if (find(name, "depth"))      type = VerticalDatumType.DEPTH;
                else if (find(name, "geoid"))      type = VerticalDatumType.GEOIDAL;
                else if (find(name, "barometric")) type = VerticalDatumType.BAROMETRIC;
            }
            this.type = type;
        }
        return type;
    }

    /**
     * The type of this vertical datum.
     *
     * @return The type of this vertical datum.
     */
    @Override
    public VerticalDatumType getVerticalDatumType() {
        return type();
    }

    /**
     * Returns the type to be marshalled to XML. This element was present in GML 3.0 and 3.1,
     * but has been removed from GML 3.2.
     *
     * @todo Disabled for now - we need to define an adapter.
     */
    @XmlElement(name = "verticalDatumType")
    private VerticalDatumType getMarshalled() {
        return (MarshalContext.versionGML(MarshalContext.GML_3_2)) ? null : getVerticalDatumType();
    }

    /**
     * Invoked by JAXB only. The vertical datum type is set only if it has
     * not already been specified.
     */
    private void setMarshalled(final VerticalDatumType t) {
        if (type != null) {
            throw new IllegalStateException();
        }
        type = t;
    }

    /**
     * Returns the legacy code for the datum type.
     */
    @Override
    final int getLegacyDatumType() {
        final VerticalDatumType type = getVerticalDatumType();
        final int ordinal = type.ordinal();
        if (ordinal>=0 && ordinal<LEGACY_CODES.length) {
            assert type.equals(TYPES[ordinal]) : type;
            return LEGACY_CODES[ordinal];
        }
        return 0;
    }

    /**
     * Returns the vertical datum type from a legacy code. The legacy codes were defined in
     * <A HREF="http://www.opengis.org/docs/01-009.pdf">Coordinate Transformation Services</A>
     * (OGC 01-009), which also defined the
     * <A HREF="http://www.geoapi.org/snapshot/javadoc/org/opengis/referencing/doc-files/WKT.html"><cite>Well
     * Known Text</cite> (WKT)</A> format. This method is used for WKT parsing.
     *
     * @param  code The legacy vertical datum code.
     * @return The vertical datum type, or {@code null} if the code is unrecognized.
     */
    public static VerticalDatumType getVerticalDatumTypeFromLegacyCode(final int code) {
        for (int i=0; i<LEGACY_CODES.length; i++) {
            if (LEGACY_CODES[i] == code) {
                return TYPES[i];
            }
        }
        return null;
    }

    /**
     * Compare this vertical datum with the specified object for equality.
     *
     * @param  object The object to compare to {@code this}.
     * @param  mode {@link ComparisonMode#STRICT STRICT} for performing a strict comparison, or
     *         {@link ComparisonMode#IGNORE_METADATA IGNORE_METADATA} for comparing only properties
     *         relevant to transformations.
     * @return {@code true} if both objects are equal.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true; // Slight optimization.
        }
        if (super.equals(object, mode)) {
            switch (mode) {
                case STRICT: {
                    final DefaultVerticalDatum that = (DefaultVerticalDatum) object;
                    return Objects.equals(this.type(), that.type());
                }
                default: {
                    final VerticalDatum that = (VerticalDatum) object;
                    return Objects.equals(getVerticalDatumType(), that.getVerticalDatumType());
                }
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int computeHashCode() {
        return hash(type(), super.computeHashCode());
    }

    /**
     * Formats the inner part of a
     * <A HREF="http://www.geoapi.org/snapshot/javadoc/org/opengis/referencing/doc-files/WKT.html#VERT_DATUM"><cite>Well
     * Known Text</cite> (WKT)</A> element.
     *
     * @param  formatter The formatter to use.
     * @return The WKT element name, which is "VERT_DATUM"
     */
    @Override
    public String formatWKT(final Formatter formatter) {
        super.formatWKT(formatter);
        return "VERT_DATUM";
    }
}
