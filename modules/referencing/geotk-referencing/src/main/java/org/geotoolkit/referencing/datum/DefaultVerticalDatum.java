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
import java.util.Collections;
import javax.xml.bind.annotation.XmlTransient;
import org.opengis.referencing.datum.VerticalDatum;
import org.opengis.referencing.datum.VerticalDatumType;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.internal.referencing.VerticalDatumTypes;

import static org.geotoolkit.referencing.datum.AbstractDatum.name;


/**
 * A textual description and/or a set of parameters identifying a particular reference level
 * surface used as a zero-height surface. The description includes its position with respect
 * to the Earth for any of the height types recognized by this standard. There are several
 * types of vertical datums, and each may place constraints on the
 * {@linkplain org.opengis.referencing.cs.CoordinateSystemAxis coordinate system axis} with which
 * it is combined to create a {@linkplain org.opengis.referencing.crs.VerticalCRS vertical CRS}.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 4.00
 *
 * @since 1.2
 * @module
 *
 * @deprecated Moved to Apache SIS.
 */
@Deprecated
@XmlTransient
public class DefaultVerticalDatum extends org.apache.sis.referencing.datum.DefaultVerticalDatum {
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
    }

    /**
     * Constructs a vertical datum from a set of properties. The properties map is given
     * unchanged to the {@linkplain AbstractDatum#AbstractDatum(Map) super-class constructor}.
     *
     * @param properties Set of properties. Should contains at least {@code "name"}.
     * @param type       The type of this vertical datum.
     */
    public DefaultVerticalDatum(final Map<String,?> properties, final VerticalDatumType type) {
        super(properties, type);
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
        return org.apache.sis.internal.referencing.VerticalDatumTypes.fromLegacy(code);
    }
}
