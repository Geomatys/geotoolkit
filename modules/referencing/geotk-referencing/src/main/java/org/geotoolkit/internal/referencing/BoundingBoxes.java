/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
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
 */
package org.geotoolkit.internal.referencing;

import java.util.Locale;
import java.text.FieldPosition;
import java.awt.geom.Rectangle2D;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.CoordinateOperationFactory;
import org.opengis.referencing.operation.TransformException;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.geometry.Envelope;

import org.geotoolkit.lang.Static;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.Factories;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.measure.Latitude;
import org.geotoolkit.measure.Longitude;
import org.geotoolkit.measure.AngleFormat;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.referencing.operation.TransformPathNotFoundException;
import org.geotoolkit.metadata.iso.extent.DefaultGeographicBoundingBox;
import org.geotoolkit.resources.Errors;


/**
 * Provides convenience methods for {@linkplain GeographicBoundingBox geographic bounding boxes}.
 * This is mostly a helper class for {@link DefaultGeographicBoundingBox}; users should not use
 * this class directly.
 *
 * @author Martin Desruisseaux (IRD)
 * @author Touraïvane (IRD)
 * @version 3.00
 *
 * @since 2.4
 * @module
 */
@Static
public final class BoundingBoxes implements ChangeListener {
    /**
     * The coordinate operation factory to be used for transforming the envelope. We will fetch
     * a lenient factory because {@link GeographicBoundingBox} are usually for approximative
     * bounds (e.g. the area of validity of some CRS). If a user wants accurate bounds, he
     * should probably use an {@link Envelope} with the appropriate CRS.
     */
    private static CoordinateOperationFactory factory;
    static {
        Factories.addChangeListener(new BoundingBoxes());
    }

    /**
     * Discarts the cached factory if the configuration changed. This method
     * is public as an implementation side effect. Do not invoke directly.
     *
     * @param e ignored.
     */
    @Override
    public void stateChanged(ChangeEvent e) {
        factory = null;
    }

    /**
     * Prevents the creation of instances of this class.
     */
    private BoundingBoxes() {
    }

    /**
     * Returns the coordinate operation factory. This method doesn't need to be synchronized;
     * it is not a big deal if the factory is queried twice from {@link FactoryFinder}.
     */
    private static CoordinateOperationFactory getFactory() {
        if (factory == null) {
            final Hints hints = new Hints(Hints.LENIENT_DATUM_SHIFT, Boolean.TRUE);
            factory = FactoryFinder.getCoordinateOperationFactory(hints);
        }
        return factory;
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
    public static void copy(Rectangle2D bounds, CoordinateReferenceSystem crs,
            final DefaultGeographicBoundingBox target) throws TransformException
    {
        if (crs != null) {
            crs = CRS.getHorizontalCRS(crs);
            if (crs == null) {
                throw new TransformPathNotFoundException(Errors.format(
                        Errors.Keys.ILLEGAL_COORDINATE_REFERENCE_SYSTEM));
            } else {
                final CoordinateReferenceSystem targetCRS = CRSUtilities.getStandardGeographicCRS2D(crs);
                if (!CRS.equalsIgnoreMetadata(crs, targetCRS)) {
                    final CoordinateOperation op;
                    try {
                        op = getFactory().createOperation(crs, targetCRS);
                    } catch (FactoryException e) {
                        throw new TransformPathNotFoundException(e);
                    }
                    bounds = CRS.transform(op, bounds, null);
                }
            }
        }
        target.setWestBoundLongitude(bounds.getMinX());
        target.setEastBoundLongitude(bounds.getMaxX());
        target.setSouthBoundLatitude(bounds.getMinY());
        target.setNorthBoundLatitude(bounds.getMaxY());
    }

    /**
     * Initializes a geographic bounding box from the specified envelope. If the envelope contains
     * a CRS, then the bounding box will be projected to a geographic CRS. Otherwise, the envelope
     * is assumed already in appropriate CRS.
     *
     * @param  envelope The source envelope.
     * @param  target The target bounding box.
     * @throws TransformException If the given envelope can't be transformed.
     */
    public static void copy(Envelope envelope, final DefaultGeographicBoundingBox target)
            throws TransformException
    {
        final CoordinateReferenceSystem crs = envelope.getCoordinateReferenceSystem();
        if (crs != null) {
            final GeographicCRS standardCRS = CRSUtilities.getStandardGeographicCRS2D(crs);
            if (!startsWith(crs, standardCRS) &&
                !startsWith(crs, DefaultGeographicCRS.WGS84) &&
                !startsWith(crs, DefaultGeographicCRS.WGS84_3D))
            {
                final CoordinateOperationFactory factory = getFactory();
                final CoordinateOperation operation;
                try {
                    operation = factory.createOperation(crs, standardCRS);
                } catch (FactoryException exception) {
                    throw new TransformPathNotFoundException(Errors.format(
                            Errors.Keys.CANT_TRANSFORM_ENVELOPE, exception));
                }
                envelope = CRS.transform(operation, envelope);
            }
        }
        target.setWestBoundLongitude(envelope.getMinimum(0));
        target.setEastBoundLongitude(envelope.getMaximum(0));
        target.setSouthBoundLatitude(envelope.getMinimum(1));
        target.setNorthBoundLatitude(envelope.getMaximum(1));
    }

    /**
     * Returns {@code true} if the specified {@code crs} starts with the specified {@code head}.
     */
    private static final boolean startsWith(final CoordinateReferenceSystem crs,
                                            final CoordinateReferenceSystem head)
    {
        final int dimension = head.getCoordinateSystem().getDimension();
        return crs.getCoordinateSystem().getDimension() >= dimension &&
               CRS.equalsIgnoreMetadata(CRSUtilities.getSubCRS(crs, 0, dimension), head);
    }

    /**
     * Returns a string representation of the specified extent using the specified angle
     * pattern and locale. See {@link AngleFormat} for a description of angle patterns.
     *
     * @param  box     The bounding box to format.
     * @param  pattern The angle pattern (e.g. {@code DD°MM'SS.s"}.
     * @param  locale  The locale, or {@code null} for the default one.
     * @return A string representation of the given bounding box.
     */
    public static String toString(final GeographicBoundingBox box,
                                  final String pattern, final Locale locale)
    {
        final AngleFormat format;
        format = (locale!=null) ? new AngleFormat(pattern, locale) : new AngleFormat(pattern);
        final FieldPosition pos = new FieldPosition(0);
        final StringBuffer buffer = new StringBuffer();
        format.format(new  Latitude(box.getNorthBoundLatitude()), buffer, pos).append(", ");
        format.format(new Longitude(box.getWestBoundLongitude()), buffer, pos).append(" - ");
        format.format(new  Latitude(box.getSouthBoundLatitude()), buffer, pos).append(", ");
        format.format(new Longitude(box.getEastBoundLongitude()), buffer, pos);
        return buffer.toString();
    }
}
