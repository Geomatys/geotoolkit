/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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

import org.opengis.metadata.lineage.NominalResolution;

import org.geotoolkit.lang.ThreadSafe;
import org.geotoolkit.lang.ValueRange;
import org.geotoolkit.metadata.iso.MetadataEntity;
import org.geotoolkit.xml.Namespaces;


/**
 * Distance between consistent parts of (centre, left side, right side) adjacent pixels.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.07
 *
 * @since 3.03
 * @module
 */
@ThreadSafe
@XmlType(propOrder={
    "scanningResolution",
    "groundResolution"
})
@XmlRootElement(name = "LE_NominalResolution", namespace = Namespaces.GMI)
public class DefaultNominalResolution extends MetadataEntity implements NominalResolution {
    /**
     * Serial number for interoperability with different versions.
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
     * @param source The metadata to copy.
     */
    public DefaultNominalResolution(final NominalResolution source) {
        super(source);
    }

    /**
     * Returns the distance between consistent parts of (centre, left side, right side)
     * adjacent pixels in the scan plane.
     */
    @Override
    @ValueRange(minimum=0, isMinIncluded=false)
    @XmlElement(name = "scanningResolution")
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
    @XmlElement(name = "groundResolution")
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
