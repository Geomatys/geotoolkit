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

import java.util.Collection;
import java.util.Collections;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.jcip.annotations.ThreadSafe;

import org.opengis.geometry.Envelope;
import org.opengis.metadata.extent.Extent;
import org.opengis.metadata.extent.VerticalExtent;
import org.opengis.metadata.extent.TemporalExtent;
import org.opengis.metadata.extent.BoundingPolygon;
import org.opengis.metadata.extent.GeographicExtent;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.InternationalString;

import org.geotoolkit.metadata.iso.MetadataEntity;
import org.geotoolkit.metadata.InvalidMetadataException;
import org.geotoolkit.internal.referencing.ProxyForMetadata;
import org.geotoolkit.resources.Errors;


/**
 * Information about spatial, vertical, and temporal extent.
 * This interface has four optional attributes
 * ({@linkplain #getGeographicElements geographic elements},
 *  {@linkplain #getTemporalElements temporal elements}, and
 *  {@linkplain #getVerticalElements vertical elements}) and an element called
 *  {@linkplain #getDescription description}.
 *  At least one of the four shall be used.
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
@XmlType(name = "EX_Extent_Type", propOrder={
    "description",
    "geographicElements",
    "temporalElements",
    "verticalElements"
})
@XmlRootElement(name = "EX_Extent")
public class DefaultExtent extends MetadataEntity implements Extent {
    /**
     * Serial number for inter-operability with different versions.
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
        world.setGeographicElements(Collections.singleton(DefaultGeographicBoundingBox.WORLD));
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
     * @param source The metadata to copy, or {@code null} if none.
     *
     * @since 2.4
     */
    public DefaultExtent(final Extent source) {
        super(source);
    }

    /**
     * Constructs an extent from the specified envelope. This method inspects the
     * {@linkplain Envelope#getCoordinateReferenceSystem() envelope CRS} and creates
     * a {@link GeographicBoundingBox}, {@link VerticalExtent} or {@link TemporalExtent}
     * as needed.
     *
     * {@note This constructor is available only if the referencing module is on the classpath.}
     *
     * @param  envelope The envelope to use for initializing this extent.
     * @throws UnsupportedOperationException if the referencing module is not on the classpath.
     * @throws TransformException If a coordinate transformation was required and failed.
     *
     * @see #addElements(Envelope)
     * @see DefaultGeographicBoundingBox#DefaultGeographicBoundingBox(Envelope)
     * @see DefaultVerticalExtent#DefaultVerticalExtent(Envelope)
     * @see DefaultTemporalExtent#DefaultTemporalExtent(Envelope)
     *
     * @since 3.18
     */
    public DefaultExtent(final Envelope envelope) throws TransformException {
        ProxyForMetadata.getInstance().copy(envelope, this);
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
    public static DefaultExtent castOrCopy(final Extent object) {
        return (object == null) || (object instanceof DefaultExtent)
                ? (DefaultExtent) object : new DefaultExtent(object);
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
        return geographicElements = nonNullCollection(geographicElements, GeographicExtent.class);
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
        return temporalElements = nonNullCollection(temporalElements, TemporalExtent.class);
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
        return verticalElements = nonNullCollection(verticalElements, VerticalExtent.class);
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
     * Adds geographic, vertical or temporal extents inferred from the given envelope. The elements
     * to add are inferred from the {@linkplain Envelope#getCoordinateReferenceSystem() envelope CRS}.
     * This method does not check for duplicate values.
     *
     * {@note This method is available only if the referencing module is on the classpath.}
     *
     * @param  envelope The envelope to use for inferring the additional extents.
     * @throws UnsupportedOperationException if the referencing module is not on the classpath.
     * @throws TransformException If a coordinate transformation was required and failed.
     *
     * @since 3.18
     */
    public synchronized void addElements(final Envelope envelope) throws TransformException {
        ProxyForMetadata.getInstance().copy(envelope, this);
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
                    if (inclusion) {
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

    /**
     * Makes sure that an argument is non-null. This is used for checking if
     * a mandatory attribute is presents.
     *
     * @param  name   Argument name.
     * @param  object User argument.
     * @throws InvalidMetadataException if {@code object} is null.
     */
    static void ensureNonNull(final String name, final Object object) throws InvalidMetadataException {
        if (object == null) {
            throw new InvalidMetadataException(Errors.format(Errors.Keys.NULL_ATTRIBUTE_1, name));
        }
    }
}
