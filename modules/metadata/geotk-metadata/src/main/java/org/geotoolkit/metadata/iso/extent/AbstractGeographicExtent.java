/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2011, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.metadata.iso.extent;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opengis.metadata.extent.GeographicExtent;

import org.geotoolkit.lang.ThreadSafe;
import org.geotoolkit.metadata.iso.MetadataEntity;


/**
 * Base class for geographic area of the dataset.
 *
 * @author Martin Desruisseaux (IRD)
 * @author Touraïvane (IRD)
 * @author Cédric Briançon (Geomatys)
 * @version 3.03
 *
 * @since 2.1
 * @module
 */
@ThreadSafe
@XmlType(name = "AbstractEX_GeographicExtent_Type")
@XmlSeeAlso({DefaultGeographicBoundingBox.class, DefaultBoundingPolygon.class, DefaultGeographicDescription.class})
@XmlRootElement(name = "EX_GeographicExtent")
public class AbstractGeographicExtent extends MetadataEntity implements GeographicExtent {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -8844015895495563161L;

    /**
     * Indication of whether the bounding polygon encompasses an area covered by the data
     * (<cite>inclusion</cite>) or an area where data is not present (<cite>exclusion</cite>).
     */
    private Boolean inclusion;

    /**
     * Constructs an initially empty geographic extent.
     */
    public AbstractGeographicExtent() {
    }

    /**
     * Constructs a geographic extent initialized to the same values than the specified one.
     *
     * @param source The metadata to copy.
     *
     * @since 2.2
     */
    public AbstractGeographicExtent(final GeographicExtent source) {
        super(source);
    }

    /**
     * Constructs a geographic extent initialized with the specified inclusion value.
     *
     * @param inclusion Whether the bounding polygon encompasses an area covered by the data.
     */
    public AbstractGeographicExtent(final boolean inclusion) {
        setInclusion(Boolean.valueOf(inclusion));
    }

    /**
     * Indication of whether the bounding polygon encompasses an area covered by the data
     * (<cite>inclusion</cite>) or an area where data is not present (<cite>exclusion</cite>).
     *
     * @return {@code true} for inclusion, or {@code false} for exclusion.
     */
    @Override
    @XmlElement(name = "extentTypeCode")
    public synchronized Boolean getInclusion() {
        return inclusion;
    }

    /**
     * Sets whether the bounding polygon encompasses an area covered by the data
     * (<cite>inclusion</cite>) or an area where data is not present (<cite>exclusion</cite>).
     *
     * @param newValue {@code true} if the bounding polygon encompasses an area covered by the data.
     */
    public synchronized void setInclusion(final Boolean newValue) {
        checkWritePermission();
        inclusion = newValue;
    }
}
