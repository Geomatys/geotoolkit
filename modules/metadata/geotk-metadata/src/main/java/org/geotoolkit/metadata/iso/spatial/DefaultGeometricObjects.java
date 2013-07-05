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
package org.geotoolkit.metadata.iso.spatial;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import net.jcip.annotations.ThreadSafe;

import org.opengis.metadata.spatial.GeometricObjects;
import org.opengis.metadata.spatial.GeometricObjectType;

import org.geotoolkit.lang.ValueRange;
import org.geotoolkit.metadata.iso.MetadataEntity;


/**
 * Number of objects, listed by geometric object type, used in the dataset.
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
@ThreadSafe
@XmlType(name = "MD_GeometricObjects_Type", propOrder={
    "geometricObjectType",
    "geometricObjectCount"
})
@XmlRootElement(name = "MD_GeometricObjects")
public class DefaultGeometricObjects extends MetadataEntity implements GeometricObjects {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 8755950031078638313L;

    /**
     * Total number of the point or vector object type occurring in the dataset.
     */
    private GeometricObjectType geometricObjectType;

    /**
     * Total number of the point or vector object type occurring in the dataset.
     */
    private Integer geometricObjectCount;

    /**
     * Constructs an initially empty geometric objects.
     */
    public DefaultGeometricObjects() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     *
     * @since 2.4
     */
    public DefaultGeometricObjects(final GeometricObjects source) {
        super(source);
    }

    /**
     * Creates a geometric object initialized to the given type.
     *
     * @param geometricObjectType Total number of the point or vector
     *          object type occurring in the dataset.
     */
    public DefaultGeometricObjects(final GeometricObjectType geometricObjectType) {
        setGeometricObjectType(geometricObjectType);
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
    public static DefaultGeometricObjects castOrCopy(final GeometricObjects object) {
        return (object == null) || (object instanceof DefaultGeometricObjects)
                ? (DefaultGeometricObjects) object : new DefaultGeometricObjects(object);
    }

    /**
     * Returns the total number of the point or vector object type occurring in the dataset.
     */
    @Override
    @XmlElement(name = "geometricObjectType", required = true)
    public synchronized GeometricObjectType getGeometricObjectType() {
        return geometricObjectType;
    }

    /**
     * Sets the total number of the point or vector object type occurring in the dataset.
     *
     * @param newValue The new geometric object type.
     */
    public synchronized void setGeometricObjectType(final GeometricObjectType newValue) {
        checkWritePermission();
        geometricObjectType = newValue;
    }

    /**
     * Returns the total number of the point or vector object type occurring in the dataset.
     */
    @Override
    @ValueRange(minimum=0)
    @XmlElement(name = "geometricObjectCount")
    public synchronized Integer getGeometricObjectCount() {
        return geometricObjectCount;
    }

    /**
     * Sets the total number of the point or vector object type occurring in the dataset.
     *
     * @param newValue The geometric object count.
     */
    public synchronized void setGeometricObjectCount(final Integer newValue) {
        checkWritePermission();
        geometricObjectCount = newValue;
    }
}
