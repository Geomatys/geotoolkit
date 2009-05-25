/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.metadata.iso.extent;

import java.util.Collection;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opengis.geometry.Geometry;
import org.opengis.metadata.extent.BoundingPolygon;


/**
 * Boundary enclosing the dataset, expressed as the closed set of
 * (<var>x</var>,<var>y</var>) coordinates of the polygon. The last
 * point replicates first point.
 *
 * @author Martin Desruisseaux (IRD)
 * @author Touraïvane (IRD)
 * @author Cédric Briançon (Geomatys)
 * @version 3.00
 *
 * @since 2.1
 * @module
 */
@XmlType(name = "EX_BoundingPolygon")
@XmlRootElement(name = "EX_BoundingPolygon")
public class DefaultBoundingPolygon extends AbstractGeographicExtent implements BoundingPolygon {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = 8174011874910887918L;

    /**
     * The sets of points defining the bounding polygon.
     */
    private Collection<Geometry> polygons;

    /**
     * Constructs an initially empty bounding polygon.
     */
    public DefaultBoundingPolygon() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy.
     *
     * @since 2.4
     */
    public DefaultBoundingPolygon(final BoundingPolygon source) {
        super(source);
    }

    /**
     * Creates a bounding polygon initialized to the specified value.
     *
     * @param polygons The sets of points defining the bounding polygon.
     */
    public DefaultBoundingPolygon(final Collection<Geometry> polygons) {
        setPolygons(polygons);
    }

    /**
     * Returns the sets of points defining the bounding polygon.
     */
    @Override
/// @XmlElement(name = "polygon", required = true)
    public synchronized Collection<Geometry> getPolygons() {
        return polygons = nonNullCollection(polygons, Geometry.class);
    }

    /**
     * Sets the sets of points defining the bounding polygon.
     *
     * @param newValues The new polygons.
     */
    public synchronized void setPolygons(final Collection<? extends Geometry> newValues) {
        polygons = copyCollection(newValues, polygons, Geometry.class);
    }
}
