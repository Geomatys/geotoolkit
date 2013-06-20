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

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.jcip.annotations.ThreadSafe;

import org.opengis.metadata.extent.GeographicExtent;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.metadata.extent.GeographicDescription;
import org.opengis.metadata.extent.BoundingPolygon;

import org.geotoolkit.metadata.iso.MetadataEntity;


/**
 * Base class for geographic area of the dataset.
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
@XmlType(name = "AbstractEX_GeographicExtent_Type")
@XmlRootElement(name = "EX_GeographicExtent")
@XmlSeeAlso({
    DefaultGeographicBoundingBox.class,
    DefaultBoundingPolygon.class,
    DefaultGeographicDescription.class
})
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
     * @param source The metadata to copy, or {@code null} if none.
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
     * Returns a Geotk metadata implementation with the same values than the given arbitrary
     * implementation. If the given object is {@code null}, then this method returns {@code null}.
     * Otherwise if the given object is already a Geotk implementation, then the given object is
     * returned unchanged. Otherwise a new Geotk implementation is created and initialized to the
     * attribute values of the given object, using a <cite>shallow</cite> copy operation
     * (i.e. attributes are not cloned).
     * <p>
     * This method checks for the {@link BoundingPolygon}, {@link GeographicBoundingBox} and
     * {@link GeographicDescription} sub-interfaces. If one of those interfaces is found, then
     * this method delegates to the corresponding {@code castOrCopy} static method. If the given object
     * implements more than one of the above-cited interfaces, then the {@code castOrCopy} method to be
     * used is unspecified.
     *
     * @param  object The object to get as a Geotk implementation, or {@code null} if none.
     * @return A Geotk implementation containing the values of the given object (may be the
     *         given object itself), or {@code null} if the argument was null.
     *
     * @since 3.18
     */
    public static AbstractGeographicExtent castOrCopy(final GeographicExtent object) {
        if (object instanceof BoundingPolygon) {
            return DefaultBoundingPolygon.castOrCopy((BoundingPolygon) object);
        }
        if (object instanceof GeographicBoundingBox) {
            return DefaultGeographicBoundingBox.castOrCopy((GeographicBoundingBox) object);
        }
        if (object instanceof GeographicDescription) {
            return DefaultGeographicDescription.castOrCopy((GeographicDescription) object);
        }
        return (object == null) || (object instanceof AbstractGeographicExtent)
                ? (AbstractGeographicExtent) object : new AbstractGeographicExtent(object);
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
