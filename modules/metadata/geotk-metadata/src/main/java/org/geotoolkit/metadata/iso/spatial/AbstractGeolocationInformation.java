/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.opengis.metadata.quality.DataQuality;
import org.opengis.metadata.spatial.GeolocationInformation;

import org.geotoolkit.lang.ThreadSafe;
import org.geotoolkit.metadata.iso.MetadataEntity;
import org.geotoolkit.xml.Namespaces;


/**
 * Information used to determine geographic location corresponding to image location.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.07
 *
 * @since 3.03
 * @module
 */
@ThreadSafe
@XmlType(propOrder={
    "qualityInfo"
})
@XmlRootElement(name = "MI_GeolocationInformation", namespace = Namespaces.GMI)
@XmlSeeAlso({
    DefaultGCPCollection.class
})
public class AbstractGeolocationInformation extends MetadataEntity implements GeolocationInformation {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -2929163425440282342L;

    /**
     * Provides an overall assessment of quality of geolocation information.
     */
    private Collection<DataQuality> qualityInfo;

    /**
     * Constructs an initially empty geolocation information.
     */
    public AbstractGeolocationInformation() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy.
     */
    public AbstractGeolocationInformation(final GeolocationInformation source) {
        super(source);
    }

    /**
     * Returns an overall assessment of quality of geolocation information.
     */
    @Override
    @XmlElement(name = "qualityInfo")
    public synchronized Collection<DataQuality> getQualityInfo() {
        return xmlOptional(qualityInfo = nonNullCollection(qualityInfo, DataQuality.class));
    }

    /**
     * Sets an overall assessment of quality of geolocation information.
     *
     * @param newValues The new quality information values.
     */
    public synchronized void setQualityInfo(Collection<? extends DataQuality> newValues) {
        qualityInfo = copyCollection(newValues, qualityInfo, DataQuality.class);
    }
}
