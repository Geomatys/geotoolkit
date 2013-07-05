/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.metadata.iso.spatial;

import java.util.Collection;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import net.jcip.annotations.ThreadSafe;

import org.opengis.metadata.spatial.GCP;
import org.opengis.metadata.spatial.GCPCollection;
import org.opengis.referencing.ReferenceSystem;
import org.opengis.util.InternationalString;

import org.geotoolkit.xml.Namespaces;


/**
 * Information about a control point collection.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.03
 * @module
 *
 * @deprecated Moved to the {@link org.apache.sis.metadata.iso} package.
 */
@ThreadSafe
@XmlType(name = "MI_GCPCollection_Type", propOrder={
    "collectionIdentification",
    "collectionName",
    "coordinateReferenceSystem",
    "GCPs"
})
@XmlRootElement(name = "MI_GCPCollection", namespace = Namespaces.GMI)
public class DefaultGCPCollection extends AbstractGeolocationInformation implements GCPCollection {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -5267006706468159746L;

    /**
     * Identifier of the GCP collection.
     */
    private Integer collectionIdentification;

    /**
     * Name of the GCP collection.
     */
    private InternationalString collectionName;

    /**
     * Coordinate system in which the ground control points are defined.
     */
    private ReferenceSystem coordinateReferenceSystem;

    /**
     * Ground control point(s) used in the collection.
     */
    private Collection<GCP> GCPs;

    /**
     * Constructs an initially empty ground control point collection.
     */
    public DefaultGCPCollection() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     */
    public DefaultGCPCollection(final GCPCollection source) {
        super(source);
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
    public static DefaultGCPCollection castOrCopy(final GCPCollection object) {
        return (object == null) || (object instanceof DefaultGCPCollection)
                ? (DefaultGCPCollection) object : new DefaultGCPCollection(object);
    }

    /**
     * Returns the identifier of the GCP collection.
     */
    @Override
    @XmlElement(name = "collectionIdentification", namespace = Namespaces.GMI, required = true)
    public synchronized Integer getCollectionIdentification() {
        return collectionIdentification;
    }

    /**
     * Sets the identifier of the GCP collection.
     *
     * @param newValue The new collection identifier value.
     */
    public synchronized void setCollectionIdentification(final Integer newValue) {
        checkWritePermission();
        collectionIdentification = newValue;
    }

    /**
     * Returns the name of the GCP collection.
     */
    @Override
    @XmlElement(name = "collectionName", namespace = Namespaces.GMI, required = true)
    public synchronized InternationalString getCollectionName() {
        return collectionName;
    }

    /**
     * Sets the name of the GCP collection.
     *
     * @param newValue The new collection name.
     */
    public synchronized void setCollectionName(final InternationalString newValue) {
        checkWritePermission();
        collectionName = newValue;
    }

    /**
     * Returns the coordinate system in which the ground control points are defined.
     */
    @Override
    @XmlElement(name = "coordinateReferenceSystem", namespace = Namespaces.GMI, required = true)
    public synchronized ReferenceSystem getCoordinateReferenceSystem() {
        return coordinateReferenceSystem;
    }

    /**
     * Sets the coordinate system in which the ground control points are defined.
     *
     * @param newValue The new coordinate reference system value.
     */
    public synchronized void setCoordinateReferenceSystem(final ReferenceSystem newValue) {
        checkWritePermission();
        coordinateReferenceSystem = newValue;
    }

    /**
     * Returns the ground control point(s) used in the collection.
     */
    @Override
    @XmlElement(name = "gcp", namespace = Namespaces.GMI, required = true)
    public synchronized Collection<GCP> getGCPs() {
        return GCPs = nonNullCollection(GCPs, GCP.class);
    }

    /**
     * Sets the ground control point(s) used in the collection.
     *
     * @param newValues The new ground control points values.
     */
    public synchronized void setGCPs(final Collection<? extends GCP> newValues) {
        GCPs = copyCollection(newValues, GCPs, GCP.class);
    }
}
