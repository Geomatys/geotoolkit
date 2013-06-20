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
package org.geotoolkit.metadata.iso.extent;

import java.util.Collection;
import java.util.Date;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.jcip.annotations.ThreadSafe;

import org.opengis.metadata.extent.GeographicExtent;
import org.opengis.metadata.extent.SpatialTemporalExtent;


/**
 * Boundary enclosing the dataset, expressed as the closed set of
 * (<var>x</var>,<var>y</var>) coordinates of the polygon. The last
 * point replicates first point.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Touraïvane (IRD)
 * @author Cédric Briançon (Geomatys)
 * @version 3.19
 *
 * @since 2.1
 * @module
 *
 * @deprecated Moved to the {@link org.apache.sis.metadata.iso} package.
 */
@Deprecated
@ThreadSafe
@XmlType(name = "EX_SpatialTemporalExtent_Type")
@XmlRootElement(name = "EX_SpatialTemporalExtent")
public class DefaultSpatialTemporalExtent extends DefaultTemporalExtent implements SpatialTemporalExtent {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 821702768255546660L;

    /**
     * The spatial extent component of composite
     * spatial and temporal extent.
     */
    private Collection<GeographicExtent> spatialExtent;

    /**
     * Constructs an initially empty spatial-temporal extent.
     */
    public DefaultSpatialTemporalExtent() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     *
     * @since 2.4
     */
    public DefaultSpatialTemporalExtent(final SpatialTemporalExtent source) {
        super(source);
    }

    /**
     * Creates a spatial-temporal extent initialized to the specified values.
     *
     * @param startTime     The start date and time for the content of the dataset.
     * @param endTime       The end date and time for the content of the dataset.
     * @param spatialExtent The spatial extent component of composite spatial and temporal extent.
     */
    public DefaultSpatialTemporalExtent(final Date startTime, final Date endTime,
                                        final Collection<? extends GeographicExtent> spatialExtent)
    {
        super(startTime, endTime);
        setSpatialExtent(spatialExtent);
    }

    /**
     * Returns a Geotk metadata implementation with the same values than the given arbitrary
     * implementation. If the given object is {@code null}, then this method returns {@code null}.
     * Otherwise if the given object is already a Geotk implementation, then the given object is
     * returned unchanged. Otherwise a new Geotk implementation is created and initialized to the
     * attribute values of the given object, using a <cite>shallow</cite> copy operation
     * (i.e. attributes are not cloned).
     *
     * @param  object The object to get as a Geotk implementation, or {@code null} if none.
     * @return A Geotk implementation containing the values of the given object (may be the
     *         given object itself), or {@code null} if the argument was null.
     *
     * @since 3.18
     */
    public static DefaultSpatialTemporalExtent castOrCopy(final SpatialTemporalExtent object) {
        return (object == null) || (object instanceof DefaultSpatialTemporalExtent)
                ? (DefaultSpatialTemporalExtent) object : new DefaultSpatialTemporalExtent(object);
    }

    /**
     * Returns the spatial extent component of composite spatial and temporal extent.
     *
     * @return The list of geographic extents (never {@code null}).
     */
    @Override
    @XmlElement(name = "spatialExtent", required = true)
    public synchronized Collection<GeographicExtent> getSpatialExtent() {
        return spatialExtent = nonNullCollection(spatialExtent, GeographicExtent.class);
    }

    /**
     * Sets the spatial extent component of composite spatial and temporal extent.
     *
     * @param newValues The new spatial extent.
     */
    public synchronized void setSpatialExtent(final Collection<? extends GeographicExtent> newValues) {
        spatialExtent = copyCollection(newValues, spatialExtent, GeographicExtent.class);
    }
}
