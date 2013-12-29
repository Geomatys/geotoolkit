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
import org.opengis.referencing.datum.EngineeringDatum;
import org.geotoolkit.resources.Vocabulary;

import static org.geotoolkit.referencing.datum.AbstractDatum.name;


/**
 * Defines the origin of an engineering coordinate reference system. An engineering datum is used
 * in a region around that origin. This origin can be fixed with respect to the earth (such as a
 * defined point at a construction site), or be a defined point on a moving vehicle (such as on a
 * ship or satellite).
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.19
 *
 * @since 1.2
 * @module
 *
 * @deprecated Moved to Apache SIS.
 */
@Deprecated
@XmlTransient
public class DefaultEngineeringDatum extends org.apache.sis.referencing.datum.DefaultEngineeringDatum {
    /**
     * An engineering datum for unknown coordinate reference system. Such CRS are usually
     * assumed Cartesian, but will not have any transformation path to other CRS.
     *
     * @see org.geotoolkit.referencing.crs.DefaultEngineeringCRS#CARTESIAN_2D
     * @see org.geotoolkit.referencing.crs.DefaultEngineeringCRS#CARTESIAN_3D
     */
    public static final DefaultEngineeringDatum UNKNOWN =
            new DefaultEngineeringDatum(name(Vocabulary.Keys.UNKNOWN));

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
    public DefaultEngineeringDatum(final EngineeringDatum datum) {
        super(datum);
    }

    /**
     * Constructs an engineering datum from a name.
     *
     * @param name The datum name.
     */
    public DefaultEngineeringDatum(final String name) {
        this(Collections.singletonMap(NAME_KEY, name));
    }

    /**
     * Constructs an engineering datum from a set of properties. The properties map is given
     * unchanged to the {@linkplain AbstractDatum#AbstractDatum(Map) super-class constructor}.
     *
     * @param properties Set of properties. Should contains at least {@code "name"}.
     */
    public DefaultEngineeringDatum(final Map<String,?> properties) {
        super(properties);
    }
}
