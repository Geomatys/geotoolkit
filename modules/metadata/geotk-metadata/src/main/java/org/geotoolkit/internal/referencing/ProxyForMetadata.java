/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011-2012, Geomatys
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
 */
package org.geotoolkit.internal.referencing;

import java.awt.geom.Rectangle2D;

import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

import org.geotoolkit.metadata.iso.extent.DefaultGeographicBoundingBox;
import org.geotoolkit.metadata.iso.extent.DefaultVerticalExtent;
import org.geotoolkit.metadata.iso.extent.DefaultTemporalExtent;
import org.geotoolkit.metadata.iso.extent.DefaultExtent;
import org.geotoolkit.resources.Errors;


/**
 * Provides access to services defined in the {@code "geotk-referencing"} module.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.18
 * @module
 */
public abstract class ProxyForMetadata {
    /**
     * The proxy, fetched when first needed.
     */
    private static ProxyForMetadata instance;

    /**
     * For subclass only.
     */
    ProxyForMetadata() {
    }

    /**
     * Returns the singleton instance.
     *
     * @return The singleton instance.
     * @throws UnsupportedOperationException If the {@code "geotkj-referencing"} module has not
     *         been found on the classpath.
     */
    public static synchronized ProxyForMetadata getInstance() throws UnsupportedOperationException {
        if (instance == null) try {
            instance = (ProxyForMetadata) Class.forName("org.geotoolkit.internal.referencing.ProxyForMetadataImpl").newInstance();
        } catch (ClassNotFoundException exception) {
            throw new UnsupportedOperationException(Errors.format(
                    Errors.Keys.MISSING_MODULE_1, "geotk-referencing"), exception);
        } catch (ReflectiveOperationException exception) {
            // Should never happen if we didn't broke our helper class.
            throw new AssertionError(exception);
        }
        return instance;
    }

    /**
     * Initializes a geographic bounding box from the specified rectangle having the specified CRS.
     * If the CRS is not null, then the rectangle will be projected to a geographic CRS. Otherwise,
     * the rectangle is assumed already in appropriate CRS.
     *
     * @param  bounds The source rectangle.
     * @param  crs The rectangle CRS, or {@code null}.
     * @param  target The target bounding box.
     * @throws TransformException If the given rectangle can't be transformed to a geographic CRS.
     */
    public abstract void copy(Rectangle2D bounds, CoordinateReferenceSystem crs,
            DefaultGeographicBoundingBox target) throws TransformException;

    /**
     * Initializes a geographic bounding box from the specified envelope. If the envelope contains
     * a CRS, then the bounding box will be projected to a geographic CRS. Otherwise, the envelope
     * is assumed already in appropriate CRS.
     *
     * @param  envelope The source envelope.
     * @param  target The target bounding box.
     * @throws TransformException If the given envelope can't be transformed.
     */
    public abstract void copy(Envelope envelope, DefaultGeographicBoundingBox target)
            throws TransformException;

    /**
     * Initializes a vertical extent with the value inferred from the given envelope.
     * Only the vertical ordinates are extracted; all other ordinates are ignored.
     *
     * @param  envelope The source envelope.
     * @param  target The target vertical extent.
     * @throws TransformException If no vertical component can be extracted from the given envelope.
     */
    public abstract void copy(Envelope envelope, DefaultVerticalExtent target)
            throws TransformException;

    /**
     * Initializes a temporal extent with the value inferred from the given envelope.
     * Only the vertical ordinates are extracted; all other ordinates are ignored.
     *
     * @param  envelope The source envelope.
     * @param  target The target temporal extent.
     * @throws TransformException If no temporal component can be extracted from the given envelope.
     */
    public abstract void copy(Envelope envelope, DefaultTemporalExtent target)
            throws TransformException;

    /**
     * Initializes a horizontal, vertical and temporal extent with the values inferred from
     * the given envelope.
     *
     * @param  envelope The source envelope.
     * @param  target The target extent.
     * @throws TransformException If a coordinate transformation was required and failed.
     */
    public abstract void copy(Envelope envelope, DefaultExtent target) throws TransformException;
}
