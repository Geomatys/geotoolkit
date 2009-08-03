/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.metadata.iso.spatial;

import java.util.Collection;

import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.opengis.metadata.quality.DataQuality;
import org.opengis.metadata.spatial.GeolocationInformation;

import org.geotoolkit.metadata.iso.MetadataEntity;


/**
 * Information used to determine geographic location corresponding to image location.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.03
 *
 * @since 3.03
 * @module
 */
@XmlType(propOrder={
    "qualityInfo"
})
@XmlRootElement(name = "MI_GeolocationInformation")
public class AbstractGeolocationInformation extends MetadataEntity implements GeolocationInformation {
    /**
     * Serial number for interoperability with different versions.
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

    /**
     * Sets the {@code xmlMarshalling} flag to {@code true}, since the marshalling
     * process is going to be done. This method is automatically called by JAXB when
     * the marshalling begins.
     *
     * @param marshaller Not used in this implementation.
     */
    @SuppressWarnings("unused")
    private void beforeMarshal(Marshaller marshaller) {
        xmlMarshalling(true);
    }

    /**
     * Sets the {@code xmlMarshalling} flag to {@code false}, since the marshalling
     * process is finished. This method is automatically called by JAXB when the
     * marshalling ends.
     *
     * @param marshaller Not used in this implementation.
     */
    @SuppressWarnings("unused")
    private void afterMarshal(Marshaller marshaller) {
        xmlMarshalling(false);
    }
}
