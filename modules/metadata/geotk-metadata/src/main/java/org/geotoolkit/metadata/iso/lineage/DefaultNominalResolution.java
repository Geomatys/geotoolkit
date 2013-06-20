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
package org.geotoolkit.metadata.iso.lineage;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import net.jcip.annotations.ThreadSafe;

import org.opengis.metadata.lineage.NominalResolution;

import org.geotoolkit.lang.ValueRange;
import org.geotoolkit.metadata.iso.MetadataEntity;
import org.geotoolkit.xml.Namespaces;


/**
 * Distance between consistent parts of (centre, left side, right side) adjacent pixels.
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
@Deprecated
@ThreadSafe
@XmlType(name = "LE_NominalResolution_Type", propOrder={
    "scanningResolution",
    "groundResolution"
})
@XmlRootElement(name = "LE_NominalResolution", namespace = Namespaces.GMI)
public class DefaultNominalResolution extends MetadataEntity implements NominalResolution {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -3337606314192690008L;

    /**
     * Distance between consistent parts of (centre, left side, right side) adjacent pixels
     * in the scan plane.
     */
    private Double scanningResolution;

    /**
     * Distance between consistent parts of (centre, left side, right side) adjacent pixels
     * in the object space.
     */
    private Double groundResolution;

    /**
     * Constructs an initially empty nominal resolution.
     */
    public DefaultNominalResolution() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     */
    public DefaultNominalResolution(final NominalResolution source) {
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
    public static DefaultNominalResolution castOrCopy(final NominalResolution object) {
        return (object == null) || (object instanceof DefaultNominalResolution)
                ? (DefaultNominalResolution) object : new DefaultNominalResolution(object);
    }

    /**
     * Returns the distance between consistent parts of (centre, left side, right side)
     * adjacent pixels in the scan plane.
     */
    @Override
    @ValueRange(minimum=0, isMinIncluded=false)
    @XmlElement(name = "scanningResolution", namespace = Namespaces.GMI, required = true)
    public synchronized Double getScanningResolution() {
        return scanningResolution;
    }

    /**
     * Sets the distance between consistent parts of (centre, left side, right side) adjacent
     * pixels in the scan plane.
     *
     * @param newValue The new scanning resolution value.
     */
    public synchronized void setScanningResolution(final Double newValue) {
        checkWritePermission();
        scanningResolution = newValue;
    }

    /**
     * Returns the distance between consistent parts of (centre, left side, right side) adjacent
     * pixels in the object space.
     */
    @Override
    @ValueRange(minimum=0, isMinIncluded=false)
    @XmlElement(name = "groundResolution", namespace = Namespaces.GMI, required = true)
    public synchronized Double getGroundResolution() {
        return groundResolution;
    }

    /**
     * Sets the distance between consistent parts of (centre, left side, right side) adjacent pixels
     * in the object space.
     *
     * @param newValue The new ground resolution value.
     */
    public synchronized void setGroundResolution(final Double newValue) {
        checkWritePermission();
        groundResolution = newValue;
    }
}
