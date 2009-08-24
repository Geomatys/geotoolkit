/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.metadata.iso.extent;

import java.util.Collection;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opengis.metadata.extent.Extent;
import org.opengis.metadata.extent.VerticalExtent;
import org.opengis.metadata.extent.TemporalExtent;
import org.opengis.metadata.extent.BoundingPolygon;
import org.opengis.metadata.extent.GeographicExtent;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.util.InternationalString;
import org.geotoolkit.metadata.iso.MetadataEntity;


/**
 * Information about spatial, vertical, and temporal extent.
 * This interface has four optional attributes
 * ({@linkplain #getGeographicElements geographic elements},
 *  {@linkplain #getTemporalElements temporal elements}, and
 *  {@linkplain #getVerticalElements vertical elements}) and an element called
 *  {@linkplain #getDescription description}.
 *  At least one of the four shall be used.
 *
 * @author Martin Desruisseaux (IRD)
 * @author Touraïvane (IRD)
 * @author Cédric Briançon (Geomatys)
 * @version 3.03
 *
 * @since 2.1
 * @module
 */
@XmlType(propOrder={
    "description",
    "geographicElements",
    "temporalElements",
    "verticalElements"
})
@XmlRootElement(name = "EX_Extent")
public class DefaultExtent extends MetadataEntity implements Extent {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = 7812213837337326257L;

    /**
     * A geographic extent ranging from 180°W to 180°E and 90°S to 90°N.
     *
     * @since 2.2
     */
    public static final Extent WORLD;
    static {
        final DefaultExtent world = new DefaultExtent();
        world.getGeographicElements().add(DefaultGeographicBoundingBox.WORLD);
        world.freeze();
        WORLD = world;
    }

    /**
     * Returns the spatial and temporal extent for the referring object.
     */
    private InternationalString description;

    /**
     * Provides geographic component of the extent of the referring object
     */
    private Collection<GeographicExtent> geographicElements;

    /**
     * Provides temporal component of the extent of the referring object
     */
    private Collection<TemporalExtent> temporalElements;

    /**
     * Provides vertical component of the extent of the referring object
     */
    private Collection<VerticalExtent> verticalElements;

    /**
     * Constructs an initially empty extent.
     */
    public DefaultExtent() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy.
     *
     * @since 2.4
     */
    public DefaultExtent(final Extent source) {
        super(source);
    }

    /**
     * Returns the spatial and temporal extent for the referring object.
     */
    @Override
    @XmlElement(name = "description")
    public synchronized InternationalString getDescription() {
        return description;
    }

    /**
     * Sets the spatial and temporal extent for the referring object.
     *
     * @param newValue The new description.
     */
    public synchronized void setDescription(final InternationalString newValue) {
        checkWritePermission();
        description = newValue;
    }

    /**
     * Provides geographic component of the extent of the referring object
     */
    @Override
    @XmlElement(name = "geographicElement")
    public synchronized Collection<GeographicExtent> getGeographicElements() {
        return xmlOptional(geographicElements = nonNullCollection(geographicElements, GeographicExtent.class));
    }

    /**
     * Sets geographic component of the extent of the referring object.
     *
     * @param newValues The new geographic elements.
     */
    public synchronized void setGeographicElements(final Collection<? extends GeographicExtent> newValues) {
        geographicElements = copyCollection(newValues, geographicElements, GeographicExtent.class);
    }

    /**
     * Provides temporal component of the extent of the referring object.
     */
    @Override
    @XmlElement(name = "temporalElement")
    public synchronized Collection<TemporalExtent> getTemporalElements() {
        return xmlOptional(temporalElements = nonNullCollection(temporalElements, TemporalExtent.class));
    }

    /**
     * Sets temporal component of the extent of the referring object.
     *
     * @param newValues The new temporal elements.
     */
    public synchronized void setTemporalElements(final Collection<? extends TemporalExtent> newValues) {
        temporalElements = copyCollection(newValues, temporalElements, TemporalExtent.class);
    }

    /**
     * Provides vertical component of the extent of the referring object.
     */
    @Override
    @XmlElement(name = "verticalElement")
    public synchronized Collection<VerticalExtent> getVerticalElements() {
        return xmlOptional(verticalElements = nonNullCollection(verticalElements, VerticalExtent.class));
    }

    /**
     * Sets vertical component of the extent of the referring object.
     *
     * @param newValues The new vertical elements.
     */
    public synchronized void setVerticalElements(final Collection<? extends VerticalExtent> newValues) {
        verticalElements = copyCollection(newValues, verticalElements, VerticalExtent.class);
    }

    /**
     * Convenience method returning a single geographic bounding box from the specified extent.
     * If no bounding box was found, then this method returns {@code null}. If more than one box
     * is found, then boxes are {@linkplain DefaultGeographicBoundingBox#add added} together.
     *
     * @param extent The extent to convert to a geographic bounding box.
     * @return A geographic bounding box extracted from the given extent.
     * @since 2.2
     */
    public static GeographicBoundingBox getGeographicBoundingBox(final Extent extent) {
        GeographicBoundingBox candidate = null;
        if (extent != null) {
            DefaultGeographicBoundingBox modifiable = null;
            for (final GeographicExtent element : extent.getGeographicElements()) {
                final GeographicBoundingBox bounds;
                if (element instanceof GeographicBoundingBox) {
                    bounds = (GeographicBoundingBox) element;
                } else if (element instanceof BoundingPolygon) {
                    // TODO: iterates through all polygons and invoke Polygon.getEnvelope();
                    continue;
                } else {
                    continue;
                }
                /*
                 * A single geographic bounding box has been extracted. Now add it to previous
                 * ones (if any). All exclusion boxes before the first inclusion box are ignored.
                 */
                if (candidate == null) {
                    /*
                     * Reminder: 'inclusion' is a mandatory attribute, so it should never be
                     * null for a valid metadata object.  If the metadata object is invalid,
                     * it is better to get an exception than having a code doing silently
                     * some probably inappropriate work.
                     */
                    final Boolean inclusion = bounds.getInclusion();
                    ensureNonNull("inclusion", inclusion);
                    if (inclusion.booleanValue()) {
                        candidate = bounds;
                    }
                } else {
                    if (modifiable == null) {
                        modifiable = new DefaultGeographicBoundingBox(candidate);
                        candidate = modifiable;
                    }
                    modifiable.add(bounds);
                }
            }
            if (modifiable != null) {
                modifiable.freeze();
            }
        }
        return candidate;
    }
}
