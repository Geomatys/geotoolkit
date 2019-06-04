/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.storage.coverage;

import java.awt.Image;
import java.util.List;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.internal.metadata.AxisDirections;
import org.apache.sis.internal.storage.StoreResource;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.NamedIdentifier;
import org.apache.sis.referencing.operation.transform.TransformSeparator;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.content.CoverageDescription;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.crs.VerticalCRS;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Resource to a coverage in the coverage store.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
@Deprecated
public interface GridCoverageResource extends org.apache.sis.storage.WritableGridCoverageResource, StoreResource {

    /**
     * Same as {@link org.apache.sis.storage.Resource} without exception.
     *
     * @todo restore the exception.
     */
    @Override
    NamedIdentifier getIdentifier();

    /**
     * Get the coverage description and statistics.
     *
     * @return CoverageDescripion, can be null
     */
    CoverageDescription getCoverageDescription();

    /**
     * @return true if coverage is writable
     */
    boolean isWritable() throws DataStoreException;

    /**
     * Return the legend of this coverage
     */
    Image getLegend() throws DataStoreException;

    /**
     * Get a reader for this coverage.
     * When you have finished using it, return it using the recycle method.
     */
    @Deprecated
    GridCoverageReader acquireReader() throws DataStoreException;

    /**
     * Get a writer for this coverage.
     * When you have finished using it, return it using the recycle method.
     */
    @Deprecated
    GridCoverageWriter acquireWriter() throws DataStoreException;

    /**
     * Return the used reader, they can be reused later.
     */
    void recycle(GridCoverageReader reader);

    /**
     * Return the used writer, they can be reused later.
     */
    void recycle(GridCoverageWriter writer);

    @Override
    default GridCoverage read(org.apache.sis.coverage.grid.GridGeometry domain, int... range) throws DataStoreException {
        final GridCoverageReader reader = acquireReader();
        try {
            final GridCoverageReadParam param = new GridCoverageReadParam();
            if (range != null && range.length > 0) {
                param.setSourceBands(range);
            }

            if (domain != null && domain.isDefined(org.apache.sis.coverage.grid.GridGeometry.ENVELOPE)) {
                /*
                 * Modify envelope: when we encounter a slice, use the median value instead of the slice width
                 * to avoid multiple coverage occurence of coverages at envelope border intersections.
                 */
                Envelope envelope = domain.getEnvelope();
                int startDim = 0;
                GeneralEnvelope modified = null;
                final GridExtent extent = domain.getExtent();
                for (final SingleCRS part : CRS.getSingleComponents(envelope.getCoordinateReferenceSystem())) {
                    final int crsDim = part.getCoordinateSystem().getDimension();
                    if (crsDim == 1 && extent.getSize(startDim) == 1) {
                        if (modified == null) {
                            envelope = modified = new GeneralEnvelope(envelope);
                        }
                        double m = modified.getMedian(startDim);
                        modified.setRange(startDim, m, m);
                    } else if (crsDim == 3) {
                        //might be 3d geographic/projected crs, see if we can split a vertical axis
                        VerticalCRS vcrs = CRS.getVerticalComponent(part, true);
                        if (vcrs != null) {
                            int idx = AxisDirections.indexOfColinear(part.getCoordinateSystem(), vcrs.getCoordinateSystem());
                            if (idx >= 0) {
                                if (modified == null) {
                                    envelope = modified = new GeneralEnvelope(envelope);
                                }
                                try {
                                    final int vidx = startDim + idx;
                                    final MathTransform gridToCRS = domain.getGridToCRS(PixelInCell.CELL_CENTER);
                                    final TransformSeparator ts = new TransformSeparator(gridToCRS);
                                    ts.addTargetDimensions(vidx);
                                    final MathTransform vtrs = ts.separate();
                                    final double[] vcoord = new double[]{extent.getLow(vidx)};
                                    vtrs.transform(vcoord, 0, vcoord, 0, 1);
                                    final double m = vcoord[0];
                                    modified.setRange(startDim+idx, m, m);
                                } catch (TransformException | FactoryException ex) {
                                    //we have try, no luck
                                }
                            }
                        }
                    }
                    startDim += crsDim;
                }

                param.setEnvelope(envelope);
                final double[] resolution = domain.getResolution(true);
                param.setResolution(resolution);
            }

            return reader.read(param);
        } finally {
            recycle(reader);
        }
    }

    @Override
    default void write(GridCoverage coverage, Option... options) throws DataStoreException {
        final GridCoverageWriter writer = acquireWriter();
        try {
            writer.write(coverage, null);
        } finally {
            recycle(writer);
        }
    }

    @Override
    default List<SampleDimension> getSampleDimensions() throws DataStoreException {
        final GridCoverageReader reader = acquireReader();
        try {
            return reader.getSampleDimensions();
        } finally {
            recycle(reader);
        }
    }

}
