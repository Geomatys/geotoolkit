/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.cs;

import java.util.Map;
import javax.measure.unit.SI;
import javax.xml.bind.annotation.XmlTransient;
import org.opengis.referencing.cs.TimeCS;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.util.InternationalString;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.apache.sis.measure.Units;
import org.geotoolkit.resources.Vocabulary;

import static java.util.Collections.singletonMap;
import static org.geotoolkit.referencing.cs.AbstractCS.name;


/**
 * A one-dimensional coordinate system containing a single time axis, used to describe the
 * temporal position of a point in the specified time units from a specified time origin.
 * A {@code TimeCS} shall have one {@linkplain #getAxis axis}.
 *
 * <TABLE CELLPADDING='6' BORDER='1'>
 * <TR BGCOLOR="#EEEEFF"><TH NOWRAP>Used with CRS type(s)</TH></TR>
 * <TR><TD>
 *   {@link org.geotoolkit.referencing.crs.DefaultTemporalCRS Temporal}
 * </TD></TR></TABLE>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.19
 *
 * @since 2.0
 * @module
 *
 * @deprecated Moved to Apache SIS.
 */
@Deprecated
@XmlTransient
public class DefaultTimeCS extends org.apache.sis.referencing.cs.DefaultTimeCS {
    /**
     * A one-dimensional temporal CS with
     * <var>{@linkplain DefaultCoordinateSystemAxis#TIME time}</var>,
     * axis in {@linkplain javax.measure.unit.NonSI#DAY day} units.
     *
     * @see org.geotoolkit.referencing.crs.DefaultTemporalCRS#JULIAN
     * @see org.geotoolkit.referencing.crs.DefaultTemporalCRS#MODIFIED_JULIAN
     * @see org.geotoolkit.referencing.crs.DefaultTemporalCRS#TRUNCATED_JULIAN
     * @see org.geotoolkit.referencing.crs.DefaultTemporalCRS#DUBLIN_JULIAN
     */
    public static final DefaultTimeCS DAYS;

    /**
     * A one-dimensional temporal CS with
     * <var>{@linkplain DefaultCoordinateSystemAxis#TIME time}</var>,
     * axis in {@linkplain javax.measure.unit.SI#SECOND second} units.
     *
     * @see org.geotoolkit.referencing.crs.DefaultTemporalCRS#UNIX
     *
     * @since 2.5
     */
    public static final DefaultTimeCS SECONDS;

    /**
     * A one-dimensional temporal CS with
     * <var>{@linkplain DefaultCoordinateSystemAxis#TIME time}</var>,
     * axis in millisecond units.
     *
     * @see org.geotoolkit.referencing.crs.DefaultTemporalCRS#JAVA
     *
     * @since 2.5
     */
    public static final DefaultTimeCS MILLISECONDS;

    /**
     * Creates the constants, reusing some intermediate constructs for efficiency.
     */
    static {
        final Map<String,Object> properties = name(Vocabulary.Keys.TEMPORAL);
        CoordinateSystemAxis axis = DefaultCoordinateSystemAxis.TIME;
        DAYS = new DefaultTimeCS(properties, axis);
        // Recycle the InternationalString instance created by DefaultCoordinateSystemAxis.TIME.
        // Its value is "Time" (English) or "Temps" (French).
        final InternationalString name = axis.getAlias().iterator().next().toInternationalString();
        axis = new DefaultCoordinateSystemAxis(name, "t", AxisDirection.FUTURE, SI.SECOND);
        SECONDS = new DefaultTimeCS(properties, axis);
        axis = new DefaultCoordinateSystemAxis(name, "t", AxisDirection.FUTURE, Units.MILLISECOND);
        MILLISECONDS = new DefaultTimeCS(properties, axis);
    }

    /**
     * Constructs a new coordinate system with the same values than the specified one.
     * This copy constructor provides a way to convert an arbitrary implementation into a
     * Geotk one or a user-defined one (as a subclass), usually in order to leverage
     * some implementation-specific API. This constructor performs a shallow copy,
     * i.e. the properties are not cloned.
     *
     * @param cs The coordinate system to copy.
     *
     * @since 2.2
     */
    public DefaultTimeCS(final TimeCS cs) {
        super(cs);
    }

    /**
     * Constructs a coordinate system from a name.
     *
     * @param name  The coordinate system name.
     * @param axis  The axis.
     */
    public DefaultTimeCS(final String name, final CoordinateSystemAxis axis) {
        super(singletonMap(NAME_KEY, name), axis);
        // Units are checked by super-class constructor.
    }

    /**
     * Constructs a coordinate system from a set of properties.
     * The properties map is given unchanged to the
     * {@linkplain AbstractCS#AbstractCS(Map,CoordinateSystemAxis[]) super-class constructor}.
     *
     * @param properties Set of properties. Should contains at least {@code "name"}.
     * @param axis       The axis.
     */
    public DefaultTimeCS(final Map<String,?> properties, final CoordinateSystemAxis axis) {
        super(properties, axis);
        // Units are checked by super-class constructor.
    }
}
