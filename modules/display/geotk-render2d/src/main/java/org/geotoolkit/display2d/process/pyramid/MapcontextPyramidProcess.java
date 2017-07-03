/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012 - 2013, Geomatys
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

package org.geotoolkit.display2d.process.pyramid;

import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.storage.coverage.CoverageUtilities;
import org.geotoolkit.storage.coverage.GridMosaic;
import org.geotoolkit.storage.coverage.Pyramid;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.service.CanvasDef;
import org.geotoolkit.display2d.service.PortrayalRenderedImage;
import org.geotoolkit.display2d.service.SceneDef;
import org.geotoolkit.display2d.service.ViewDef;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.process.ProcessException;
import org.apache.sis.referencing.CRS;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CancellationException;

import static org.geotoolkit.parameter.Parameters.getOrCreate;
import static org.geotoolkit.parameter.Parameters.value;
import static org.geotoolkit.display2d.process.pyramid.MapcontextPyramidDescriptor.*;
import org.geotoolkit.storage.coverage.PyramidalCoverageResource;

/**
 * Create a pyramid in the given PyramidalModel.
 * If a pyramid with the given CRS already exist it will be reused.
 * If a mosaic at the given scale exist it will be used.
 * Missing tiles in the mosaic will be generated.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public final class MapcontextPyramidProcess extends AbstractProcess {

    private volatile long progress = 0;
    private long total = 0;

    MapcontextPyramidProcess(final ParameterValueGroup input) {
        super(INSTANCE,input);
    }

    @Override
    protected void execute() throws ProcessException {
        ArgumentChecks.ensureNonNull("inputParameters", inputParameters);

        final MapContext context = value(IN_MAPCONTEXT, inputParameters);
        final Envelope envelope = value(IN_EXTENT, inputParameters);
        final Dimension tileSize = value(IN_TILE_SIZE, inputParameters);
        final double[] scales = value(IN_SCALES, inputParameters);
        Integer nbpainter = value(IN_NBPAINTER, inputParameters);
        final PyramidalCoverageResource container = value(IN_CONTAINER, inputParameters);
        final Boolean update = value(IN_UPDATE, inputParameters);

        if(nbpainter == null){
            nbpainter = Runtime.getRuntime().availableProcessors();
        }

        Hints hints = null;
        try{
            hints = value(IN_HINTS, inputParameters);
        } catch (ParameterNotFoundException ex) {
            // Get the hint parameter if exist, otherwise keep it to null.
        }

        final Envelope ctxEnv;
        try {
            ctxEnv = context.getBounds();
        } catch (IOException e) {
            throw new ProcessException(e.getMessage(), this, e);
        }

        if (update != null && update) {
            context.layers().add(0, MapBuilder.createCoverageLayer(container));
        }

        final CoordinateReferenceSystem ctxCRS = context.getCoordinateReferenceSystem();
        final CoordinateReferenceSystem destCRS = envelope.getCoordinateReferenceSystem();

        final int widthAxis = CoverageUtilities.getMinOrdinate(destCRS);
        final int heightAxis = widthAxis + 1;

        //calculate the number of tile to generate
        double destEnvWidth = envelope.getSpan(widthAxis);
        double destEnvHeight = envelope.getSpan(heightAxis);

        for(double scale : scales){
            final double gridWidth  = destEnvWidth / (scale*tileSize.width);
            final double gridHeight = destEnvHeight / (scale*tileSize.height);
            total += Math.ceil(gridWidth) * Math.ceil(gridHeight);
        }

        final CanvasDef canvasDef = new CanvasDef(new Dimension(1, 1), null);
        final ViewDef viewDef = new ViewDef(envelope);
        final SceneDef sceneDef = new SceneDef(context,hints);

        //find if we already have a pyramid in the given CRS
        Pyramid pyramid = null;
        try {
            final Envelope clipEnv = ReferencingUtilities.intersectEnvelopes(ctxEnv, envelope);
            final MathTransform destCRS_to_ctxCRS = CRS.findOperation(CRSUtilities.getCRS2D(destCRS), CRSUtilities.getCRS2D(ctxCRS), null).getMathTransform();

            for (Pyramid candidate : container.getPyramidSet().getPyramids()) {
                if (org.geotoolkit.referencing.CRS.equalsApproximatively(destCRS, candidate.getCoordinateReferenceSystem())) {
                    pyramid = candidate;
                    break;
                }
            }

            if (pyramid == null) {
                //we didn't find a pyramid, create one
                pyramid = container.createPyramid(destCRS);
            }

            final String pyramidId = pyramid.getId();

            //generate each mosaic
            for (final double scale : scales) {
                if (isCanceled()) {
                    throw new CancellationException();
                }

                //compute mosaic gridSize
                final double gridWidth  = destEnvWidth / (scale*tileSize.width);
                final double gridHeight = destEnvHeight / (scale*tileSize.height);
                final Dimension gridSize = new Dimension( (int)(Math.ceil(gridWidth)), (int)(Math.ceil(gridHeight)));

                //find mosaic
                final GridMosaic mosaic = getOrCreateMosaic(container, pyramidId, scale, envelope, tileSize, gridSize);

                if (isCanceled()) {
                    throw new CancellationException();
                }
                final PortrayalRenderedImage image = new PortrayalRenderedImage(
                        canvasDef, sceneDef, viewDef,
                        mosaic.getGridSize(), mosaic.getTileSize(), scale);
                image.addProgressListener(new PortrayalRenderedImage.ProgressListener() {
                    @Override
                    public void tileCreated(int x, int y) {
                        progress++;
                        progress();
                    }
                });

                // Transform context envelope into mosaic grid system to
                // find tiles impacted on container mosaic
                final double min0 = envelope.getMinimum(widthAxis);
                final double max1 = envelope.getMaximum(heightAxis);
                final MathTransform2D gridDest_to_crs = new AffineTransform2D(scale, 0, 0, -scale, min0, max1);
                final MathTransform ctxCRS_to_gridDest = MathTransforms.concatenate(gridDest_to_crs, destCRS_to_ctxCRS).inverse();

                final GeneralEnvelope ctxExtent = Envelopes.transform(ctxCRS_to_gridDest, clipEnv);
                final int startTileX = (int)ctxExtent.getMinimum(widthAxis) / tileSize.width;
                final int startTileY = (int)ctxExtent.getMinimum(heightAxis) / tileSize.height;
                final int endTileX   = ((int)(ctxExtent.getMaximum(widthAxis) + tileSize.width - 1) / tileSize.width) - startTileX;
                final int endTileY   = ((int)(ctxExtent.getMaximum(heightAxis) + tileSize.height - 1) / tileSize.height) - startTileY;
                final Rectangle area = new Rectangle(startTileX, startTileY, endTileX, endTileY);

                container.writeTiles(pyramid.getId(), mosaic.getId(), image, area, false, new MapcontextPyramidMonitor(this));
                if (isCanceled()) {
                    throw new CancellationException();
                }

                getOrCreate(OUT_CONTAINER, outputParameters).setValue(container);
            }

        } catch (DataStoreException | FactoryException | TransformException | PortrayalException ex) {
            throw new ProcessException(ex.getMessage(), this, ex);
        }
    }

    /**
     * Get or create a new mosaic in container for a specific scale and destination envelope.
     *
     * @param container
     * @param pyramidId
     * @param scale
     * @param destEnvelope
     * @param tileSize
     * @param gridSize
     * @return GridMosaic
     * @throws FactoryException
     * @throws DataStoreException
     */
    private GridMosaic getOrCreateMosaic(PyramidalCoverageResource container, String pyramidId, double scale,
                                         final Envelope destEnvelope, final Dimension tileSize, final Dimension gridSize)
            throws FactoryException, DataStoreException {

        final Pyramid pyramid = container.getPyramidSet().getPyramid(pyramidId);
        final CoordinateReferenceSystem destCRS = pyramid.getCoordinateReferenceSystem();

        final int widthAxis = CoverageUtilities.getMinOrdinate(destCRS);
        final int heightAxis = widthAxis + 1;

        final DirectPosition upperLeft = new GeneralDirectPosition(destCRS);
        upperLeft.setOrdinate(widthAxis, destEnvelope.getMinimum(widthAxis));
        upperLeft.setOrdinate(heightAxis, destEnvelope.getMaximum(heightAxis));
        int outDim = destEnvelope.getDimension();
        for (int d = 0; d < outDim; d++) {
            if (d != widthAxis && d != heightAxis) {
                //set upperLeft extra dimension ordinate from requested envelope
                upperLeft.setOrdinate(d, destEnvelope.getMinimum(d));
            }
        }

        //search with scale and upperLeft
        for(GridMosaic gm : pyramid.getMosaics()){
            if(gm.getScale() == scale && Arrays.equals(upperLeft.getCoordinate(), gm.getUpperLeftCorner().getCoordinate())){
                return gm;
            }
        }

        //create a new mosaic
        return container.createMosaic(pyramid.getId(),gridSize, tileSize, upperLeft, scale);
    }

    private void progress(){
        fireProgressing(progress+"/"+total, (float)((double)progress/(double)total)*100f, false);
    }

    /**
     * Allow to cancel the tiles creation process.
     */
    private class MapcontextPyramidMonitor extends ProgressMonitor {
        private final MapcontextPyramidProcess process;

        public MapcontextPyramidMonitor(final MapcontextPyramidProcess process) {
            super(new JLabel(), "", "", 0, 100);
            this.process = process;
        }

        @Override
        public boolean isCanceled() {
            return process.isCanceled();
        }
    }
}
