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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import net.jcip.annotations.ThreadSafe;

import org.opengis.metadata.quality.DataQuality;
import org.opengis.metadata.spatial.GCPCollection;
import org.opengis.metadata.spatial.GeolocationInformation;

import org.geotoolkit.metadata.iso.MetadataEntity;
import org.geotoolkit.xml.Namespaces;


/**
 * Information used to determine geographic location corresponding to image location.
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
@XmlType(name = "AbstractMI_GeolocationInformation_Type", propOrder={
    "qualityInfo"
})
@XmlRootElement(name = "MI_GeolocationInformation", namespace = Namespaces.GMI)
@XmlSeeAlso(DefaultGCPCollection.class)
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
     * @param source The metadata to copy, or {@code null} if none.
     */
    public AbstractGeolocationInformation(final GeolocationInformation source) {
        super(source);
    }

    /**
     * Returns a Geotk metadata implementation with the same values than the given arbitrary
     * implementation. If the given object is {@code null}, then this method returns {@code null}.
     * Otherwise if the given object is already a Geotk implementation, then the given object is
     * returned unchanged. Otherwise a new Geotk implementation is created and initialized to the
     * attribute values of the given object, using a <cite>shallow</cite> copy operation
     * (i.e. attributes are not cloned).
     * <p>
     * This method checks for the {@link GCPCollection} sub-interface. If that interface is
     * found, then this method delegates to the corresponding {@code castOrCopy} static method.
     *
     * @param  object The object to get as a Geotk implementation, or {@code null} if none.
     * @return A Geotk implementation containing the values of the given object (may be the
     *         given object itself), or {@code null} if the argument was null.
     *
     * @since 3.18
     */
    public static AbstractGeolocationInformation castOrCopy(final GeolocationInformation object) {
        if (object instanceof GCPCollection) {
            return DefaultGCPCollection.castOrCopy((GCPCollection) object);
        }
        return (object == null) || (object instanceof AbstractGeolocationInformation)
                ? (AbstractGeolocationInformation) object : new AbstractGeolocationInformation(object);
    }

    /**
     * Returns an overall assessment of quality of geolocation information.
     */
    @Override
    @XmlElement(name = "qualityInfo", namespace = Namespaces.GMI)
    public synchronized Collection<DataQuality> getQualityInfo() {
        return qualityInfo = nonNullCollection(qualityInfo, DataQuality.class);
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
