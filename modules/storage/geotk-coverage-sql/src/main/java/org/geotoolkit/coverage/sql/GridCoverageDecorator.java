/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2012, Geomatys
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
package org.geotoolkit.coverage.sql;

import java.io.IOException;
import java.io.Serializable;
import java.awt.geom.Rectangle2D;
import static java.lang.Double.NaN;
import java.util.concurrent.CancellationException;
import org.geotoolkit.coverage.io.GridCoverageReader;

import org.opengis.coverage.Coverage;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.util.DateRange;
import org.apache.sis.measure.NumberRange;
import org.geotoolkit.image.io.IIOListeners;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.io.CoverageStoreException;


/**
 * Redirect every calls to a wrapped {@link GridCoverageReference} instance.
 * Subclasses need to override a few methods in order to make this proxy useful.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.16
 *
 * @since 3.11 (derived from Seagis)
 * @module
 */
class GridCoverageDecorator implements GridCoverageReference, Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 1679051552440633120L;

    /**
     * The backing reference on which to delegate the work.
     */
    protected final GridCoverageReference reference;

    /**
     * Creates a new proxy which will delegates the method calls to the given reference.
     *
     * @param reference The backing reference on which to delegate the work.
     */
    protected GridCoverageDecorator(final GridCoverageReference reference) {
        this.reference = reference;
    }

    /**
     * Forwards the call to the wrapped {@linkplain #reference}.
     */
    @Override
    public String getName() {
        return reference.getName();
    }

    /**
     * Forwards the call to the wrapped {@linkplain #reference}.
     */
    @Override
    public <T> T getFile(Class<T> type) throws IOException {
        return reference.getFile(type);
    }

    /**
     * Forwards the call to the wrapped {@linkplain #reference}.
     */
    @Override
    public String getImageFormat() {
        return reference.getImageFormat();
    }

    /**
     * Forwards the call to the wrapped {@linkplain #reference}.
     */
    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem(boolean includeTime) {
        return reference.getCoordinateReferenceSystem(includeTime);
    }

    /**
     * Forwards the call to the wrapped {@linkplain #reference}.
     */
    @Override
    public GeographicBoundingBox getGeographicBoundingBox() {
        return reference.getGeographicBoundingBox();
    }

    /**
     * Forwards the call to the wrapped {@linkplain #reference}.
     */
    @Override
    public Envelope getEnvelope() {
        return reference.getEnvelope();
    }

    /**
     * Forwards the call to the wrapped {@linkplain #reference}.
     */
    @Override
    public Rectangle2D getXYRange() {
        return reference.getXYRange();
    }

    @Override
    public Number getZCenter() throws IOException {
        final NumberRange<?> range = getZRange();
        if (range != null) {
            final Number lower = range.getMinValue();
            final Number upper = range.getMaxValue();
            if (lower != null) {
                if (upper != null) {
                    return 0.5 * (lower.doubleValue() + upper.doubleValue());
                } else {
                    return lower.doubleValue();
                }
            } else if (upper != null) {
                return upper.doubleValue();
            }else{
                return NaN;
            }
        }else{
            return NaN;
        }
    }
    
    /**
     * Forwards the call to the wrapped {@linkplain #reference}.
     */
    @Override
    public NumberRange<?> getZRange() {
        return reference.getZRange();
    }

    /**
     * Forwards the call to the wrapped {@linkplain #reference}.
     */
    @Override
    public DateRange getTimeRange() {
        return reference.getTimeRange();
    }

    /**
     * Forwards the call to the wrapped {@linkplain #reference}.
     */
    @Override
    public GridGeometry2D getGridGeometry() {
        return reference.getGridGeometry();
    }

    /**
     * Forwards the call to the wrapped {@linkplain #reference}.
     */
    @Override
    public GridSampleDimension[] getSampleDimensions() {
        return reference.getSampleDimensions();
    }

    /**
     * Forwards the call to the wrapped {@linkplain #reference}.
     */
    @Override
    public GridCoverageReader getCoverageReader(GridCoverageReader recycle) throws CoverageStoreException {
        return reference.getCoverageReader(recycle);
    }

    /**
     * Forwards the call to the wrapped {@linkplain #reference}.
     */
    @Override
    public Coverage getCoverage(IIOListeners listeners) throws IOException, CancellationException {
        return reference.getCoverage(listeners);
    }

    /**
     * Forwards the call to the wrapped {@linkplain #reference}.
     */
    @Override
    public GridCoverage2D read(CoverageEnvelope envelope, IIOListeners listeners) throws CoverageStoreException, CancellationException {
        return reference.read(envelope, listeners);
    }

    /**
     * Forwards the call to the wrapped {@linkplain #reference}.
     */
    @Override
    public void abort() {
        reference.abort();
    }

    /**
     * Forwards the call to the wrapped {@linkplain #reference}.
     */
    @Override
    public String toString() {
        return reference.toString();
    }
}
