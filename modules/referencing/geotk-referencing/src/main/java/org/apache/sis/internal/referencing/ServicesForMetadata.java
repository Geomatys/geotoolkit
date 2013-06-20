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
 */
package org.apache.sis.internal.referencing;

import java.util.Date;
import java.util.logging.LogRecord;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.opengis.util.FactoryException;
import org.opengis.referencing.crs.TemporalCRS;
import org.opengis.referencing.crs.VerticalCRS;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.CoordinateOperationFactory;
import org.opengis.referencing.operation.TransformException;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.geometry.Envelope;

import org.opengis.temporal.TemporalPrimitive;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.Factories;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.FactoryNotFoundException;
import org.geotoolkit.geometry.Envelopes;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultTemporalCRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.referencing.operation.TransformPathNotFoundException;
import org.apache.sis.metadata.iso.extent.DefaultExtent;
import org.apache.sis.metadata.iso.extent.DefaultVerticalExtent;
import org.apache.sis.metadata.iso.extent.DefaultTemporalExtent;
import org.apache.sis.metadata.iso.extent.DefaultSpatialTemporalExtent;
import org.apache.sis.metadata.iso.extent.DefaultGeographicBoundingBox;
import org.apache.sis.internal.metadata.ReferencingServices;
import org.geotoolkit.internal.TemporalUtilities;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.resources.Errors;

import static org.geotoolkit.internal.InternalUtilities.isPoleToPole;
import static org.apache.sis.metadata.iso.ISOMetadata.LOGGER;


/**
 * Provides convenience methods for some metadata constructors. This is mostly a helper class
 * for {@link DefaultGeographicBoundingBox}; users should not use this class directly.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Touraïvane (IRD)
 * @version 3.20
 *
 * @since 2.4
 * @module
 */
public final class ServicesForMetadata extends ReferencingServices implements ChangeListener {
    /**
     * The coordinate operation factory to be used for transforming the envelope. We will fetch
     * a lenient factory because {@link GeographicBoundingBox} are usually for approximative
     * bounds (e.g. the area of validity of some CRS). If a user wants accurate bounds, he
     * should probably use an {@link Envelope} with the appropriate CRS.
     */
    private CoordinateOperationFactory factory;

    /**
     * Creates a new instance. This constructor is invoked by reflection only, in the
     * {@link #getInstance()} method.
     */
    ServicesForMetadata() {
        Factories.addChangeListener(this);
    }

    /**
     * Discards the cached factory if the configuration changed. This method
     * is public as an implementation side effect. Do not invoke directly.
     *
     * @param e ignored.
     */
    @Override
    public void stateChanged(ChangeEvent e) {
        factory = null;
    }

    /**
     * Returns the coordinate operation factory. This method doesn't need to be synchronized;
     * it is not a big deal if the factory is queried twice from {@link FactoryFinder}.
     */
    private CoordinateOperationFactory getFactory() {
        if (factory == null) {
            final Hints hints = new Hints(Hints.LENIENT_DATUM_SHIFT, Boolean.TRUE);
            factory = FactoryFinder.getCoordinateOperationFactory(hints);
        }
        return factory;
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
    @Override
    public void setBounds(Envelope envelope, final DefaultGeographicBoundingBox target)
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
                envelope = Envelopes.transform(operation, envelope);
            }
        }
        double xmin = envelope.getMinimum(0);
        double xmax = envelope.getMaximum(0);
        double ymin = envelope.getMinimum(1);
        double ymax = envelope.getMaximum(1);
        if (ymin > ymax) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.ILLEGAL_ORDINATE_AT_1, 1));
        }
        boolean inclusion = true;
        if (xmin > xmax) {
            // Longitude is spanning the anti-meridian: we accept them only for lattitudes from
            // poles to poles, because we can declare the bounding box as exclusive only as a whole.
            if (!isPoleToPole(ymin, ymax)) {
                throw new IllegalArgumentException(Errors.format(Errors.Keys.ILLEGAL_ORDINATE_AT_1, 0));
            }
            double t=xmin; xmin=xmax; xmax=t;
            inclusion = false;
        }
        target.setBounds(xmin, xmax, ymin, ymax);
        target.setInclusion(inclusion);
    }

    /**
     * Returns {@code true} if the specified {@code crs} starts with the specified {@code head}.
     */
    private static boolean startsWith(final CoordinateReferenceSystem crs,
                                      final CoordinateReferenceSystem head)
    {
        final int dimension = head.getCoordinateSystem().getDimension();
        return crs.getCoordinateSystem().getDimension() >= dimension &&
               CRS.equalsIgnoreMetadata(CRS.getSubCRS(crs, 0, dimension), head);
    }

    /**
     * Initializes a vertical extent with the value inferred from the given envelope.
     * Only the vertical ordinates are extracted; all other ordinates are ignored.
     *
     * @param  envelope The source envelope.
     * @param  target The target vertical extent.
     * @throws TransformException If no vertical component can be extracted from the given envelope.
     */
    @Override
    public void setBounds(final Envelope envelope, final DefaultVerticalExtent target) throws TransformException {
        final CoordinateReferenceSystem crs = envelope.getCoordinateReferenceSystem();
        final VerticalCRS verticalCRS = CRS.getVerticalCRS(crs);
        if (verticalCRS == null && envelope.getDimension() != 1) {
            throw new TransformPathNotFoundException(Errors.format(
                    Errors.Keys.ILLEGAL_COORDINATE_REFERENCE_SYSTEM));
        }
        setBounds(envelope, target, crs, verticalCRS);
    }

    /**
     * Initializes a temporal extent with the value inferred from the given envelope.
     * Only the vertical ordinates are extracted; all other ordinates are ignored.
     *
     * @param  envelope The source envelope.
     * @param  target The target temporal extent.
     * @throws TransformException If no temporal component can be extracted from the given envelope.
     */
    @Override
    public void setBounds(final Envelope envelope, final DefaultTemporalExtent target) throws TransformException {
        final CoordinateReferenceSystem crs = envelope.getCoordinateReferenceSystem();
        final TemporalCRS temporalCRS = CRS.getTemporalCRS(crs);
        if (temporalCRS == null) {
            throw new TransformPathNotFoundException(Errors.format(
                    Errors.Keys.ILLEGAL_COORDINATE_REFERENCE_SYSTEM));
        }
        setBounds(envelope, target, crs, temporalCRS);
    }

    /**
     * Implementation of the public {@code copy} methods for the vertical extent.
     */
    private void setBounds(final Envelope envelope, final DefaultVerticalExtent target,
            final CoordinateReferenceSystem crs, final VerticalCRS verticalCRS)
            throws TransformException
    {
        final int dim = (verticalCRS == null) ? 0 : CRSUtilities.dimensionColinearWith(
                crs.getCoordinateSystem(), verticalCRS.getCoordinateSystem());
        if (dim >= 0) {
            target.setMinimumValue(envelope.getMinimum(dim));
            target.setMaximumValue(envelope.getMaximum(dim));
        }
        target.setVerticalCRS(verticalCRS);
    }

    /**
     * Implementation of the public {@code setBounds} methods for the temporal extent.
     */
    private void setBounds(final Envelope envelope, final DefaultTemporalExtent target,
            final CoordinateReferenceSystem crs, final TemporalCRS temporalCRS)
            throws TransformException
    {
        final int dim = CRSUtilities.dimensionColinearWith(
                crs.getCoordinateSystem(), temporalCRS.getCoordinateSystem());
        if (dim >= 0) {
            final DefaultTemporalCRS converter = DefaultTemporalCRS.castOrCopy(temporalCRS);
            Date tmin = converter.toDate(envelope.getMinimum(dim));
            Date tmax = converter.toDate(envelope.getMaximum(dim));
            if (tmax == null) {
                tmax = tmin;
            }
            if (tmax != null) try {
                final TemporalPrimitive value;
                if (tmin == null || tmin.equals(tmax)) {
                    value = TemporalUtilities.createInstant(tmax);
                } else {
                    value = TemporalUtilities.createPeriod(tmin, tmax);
                }
                target.setExtent(value);
            } catch (FactoryNotFoundException e) {
                final LogRecord record = TemporalUtilities.createLog(e);
                record.setSourceClassName(DefaultTemporalExtent.class.getCanonicalName());
                record.setSourceMethodName("getExtent");
                record.setLoggerName(LOGGER.getName());
                LOGGER.log(record);
            }
        }
    }

    @Override
    public void setBounds(Envelope envelope, DefaultSpatialTemporalExtent target) throws TransformException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Initializes a horizontal, vertical and temporal extent with the values inferred from
     * the given envelope.
     *
     * @param  envelope The source envelope.
     * @param  target The target extent.
     * @throws TransformException If a coordinate transformation was required and failed.
     */
    @Override
    public void addElements(final Envelope envelope, final DefaultExtent target) throws TransformException {
        final CoordinateReferenceSystem crs = envelope.getCoordinateReferenceSystem();
        if (CRS.getHorizontalCRS(crs) != null) {
            final DefaultGeographicBoundingBox extent = new DefaultGeographicBoundingBox();
            extent.setInclusion(Boolean.TRUE);
            setBounds(envelope, extent);
            target.getGeographicElements().add(extent);
        }
        final VerticalCRS verticalCRS = CRS.getVerticalCRS(crs);
        if (verticalCRS != null) {
            final DefaultVerticalExtent extent = new DefaultVerticalExtent();
            setBounds(envelope, extent, crs, verticalCRS);
            target.getVerticalElements().add(extent);
        }
        final TemporalCRS temporalCRS = CRS.getTemporalCRS(crs);
        if (temporalCRS != null) {
            final DefaultTemporalExtent extent = new DefaultTemporalExtent();
            setBounds(envelope, extent, crs, temporalCRS);
            target.getTemporalElements().add(extent);
        }
    }
}
