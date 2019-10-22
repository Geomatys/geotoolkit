/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

package org.geotoolkit.processing.coverage.mathcalc;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Arrays;
import java.util.stream.Stream;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.internal.system.DefaultFactories;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.Utilities;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageWriteParam;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.geotoolkit.storage.multires.Mosaic;
import org.geotoolkit.storage.multires.MultiResolutionResource;
import org.geotoolkit.storage.multires.Pyramid;
import org.geotoolkit.storage.multires.Pyramids;
import org.geotoolkit.geometry.HyperCubeIterator;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.referencing.operation.matrix.GeneralMatrix;
import org.geotoolkit.storage.coverage.*;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * TODO : This should be part of the CoverageWriter interface.
 * The current CoverageWriter interface is limited to slice writing as renderedImage,
 * this implies the callers know exactly the internal structure of the coverage.
 *
 * This class is a preview of a possible future design of the coverage writer,
 * unlike the writer approach where the user provide an image of the datas, here
 * the writer calls an evaluator on each coordinate. This allows to manipulate
 * N dimension writing and coverages which are not grids.
 *
 *
 * @author Johann Sorel (Geomatys)
 */
public class FillCoverage {

    /**
     * Fill coverage values on given envelope.
     *
     * @param evaluator , used to generate the new sample values.
     * @param env , envelope where new values will be evaluated.
     * @throws org.geotoolkit.coverage.io.CoverageStoreException
     */
    public void fill(GridCoverageResource outRef, SampleEvaluator evaluator, Envelope env) throws DataStoreException {

        final GridGeometry gg;
        final GridCoverageWriter outWriter;
        outWriter = outRef.acquireWriter();
        gg = outRef.getGridGeometry();

        // prepare dynamic pick object
        final GridExtent ge = gg.getExtent();
        final int nbDim = ge.getDimension();
        final DirectPosition positionGrid = new GeneralDirectPosition(gg.getCoordinateReferenceSystem());
        final DirectPosition positionGeo = new GeneralDirectPosition(gg.getCoordinateReferenceSystem());

        //calculate the hyper-cube where we will need to recalculate values
        final MathTransform gridToCrs = gg.getGridToCRS(PixelInCell.CELL_CENTER);
        final long[] mins = new long[nbDim];
        final long[] maxs = new long[nbDim];
        for(int i=0;i<nbDim;i++){
            mins[i] = Math.toIntExact(ge.getLow(i));
            maxs[i] = Math.toIntExact(ge.getHigh(i)+1); //high value is inclusive in grid envelopes
        }
        //adjust the writing hyper-cube if an envelope is provided
        if(env!=null){
            if(!Utilities.equalsIgnoreMetadata(env.getCoordinateReferenceSystem(),gg.getCoordinateReferenceSystem())){
                throw new CoverageStoreException("Envelope is not in data CRS.");
            }
            try {
                final MathTransform crsToGrid = gridToCrs.inverse();
                // how do we round values ? cast, nearest, intersect, within ?
                GeneralEnvelope gridEnv = Envelopes.transform(crsToGrid, env);
                for(int i=0;i<nbDim;i++){
                    mins[i] = Math.max(mins[i], (int)gridEnv.getMinimum(i));
                    maxs[i] = Math.min(maxs[i], (int)gridEnv.getMaximum(i));
                }

            } catch (TransformException ex) {
                throw new CoverageStoreException(ex.getMessage(), ex);
            }
        }

        //create iterator
        final int[] maxSize = new int[nbDim];
        Arrays.fill(maxSize, 1);
        maxSize[0] = 256;
        maxSize[1] = 256;
        final HyperCubeIterator ite = new HyperCubeIterator(mins, maxs, maxSize);

        //loop on all slices pieces
        final MathTransformFactory mathFactory = DefaultFactories.forBuildin(MathTransformFactory.class);
        while(ite.hasNext()){
            final HyperCubeIterator.HyperCube cube = ite.next();
            final long[] hcubeLower = cube.getLower();
            final long[] hcubeUpper = cube.getUpper();
            for(int i=2;i<nbDim;i++){
                positionGrid.setOrdinate(i, hcubeLower[i]);
            }

            //create the slice coverage
            final BufferedImage zoneImage = BufferedImages.createImage(
                    Math.toIntExact(hcubeUpper[0]-hcubeLower[0]),
                    Math.toIntExact(hcubeUpper[1]-hcubeLower[1]),
                    1, DataBuffer.TYPE_DOUBLE);
            final WritableRaster raster = zoneImage.getRaster();


            //loop on all pixels
            final double[] sampleData = new double[1];
            try{
                for(long x=hcubeLower[0],xn=hcubeUpper[0];x<xn;x++){
                    for(long y=hcubeLower[1],yn=hcubeUpper[1];y<yn;y++){
                        positionGrid.setOrdinate(0, x);
                        positionGrid.setOrdinate(1, y);
                        gridToCrs.transform(positionGrid, positionGeo);
                        evaluator.evaluate(positionGeo, sampleData);
                        raster.setSample(Math.toIntExact(x-hcubeLower[0]), Math.toIntExact(y-hcubeLower[1]), 0, sampleData[0]);
                    }
                }
            }catch(TransformException ex){
                throw new CoverageStoreException(ex.getMessage(), ex);
            }

            //Calculate grid to crs of this zone
            final GeneralMatrix matrix = new GeneralMatrix(nbDim+1);
            matrix.setIdentity();
            for(int i=0;i<nbDim;i++){
                matrix.setElement(i, nbDim, hcubeLower[i]);
            }
            final MathTransform cornerToGrid;
            try {
                cornerToGrid = mathFactory.createAffineTransform(matrix);
            } catch (FactoryException ex) {
                throw new CoverageStoreException(ex.getMessage(), ex);
            }
            final MathTransform concat = MathTransforms.concatenate(cornerToGrid, gridToCrs);

            final GridCoverageBuilder gcb = new GridCoverageBuilder();
            gcb.setCoordinateReferenceSystem(gg.getCoordinateReferenceSystem());
            gcb.setRenderedImage(zoneImage);
            gcb.setGridToCRS(concat);
            final GridCoverage zoneCoverage = gcb.getGridCoverage2D();
            final GridCoverageWriteParam param = new GridCoverageWriteParam();
            outWriter.write(zoneCoverage, param);
        }

    }

    /**
     * Fill given coverage reference, providing it with processed images.
     *
     * @param evaluator
     * @param outRef
     */
    public static void fill(MultiResolutionResource outRef, SampleEvaluator evaluator)
            throws DataStoreException, TransformException, FactoryException {

//        final ColorModel cm = outRef.getColorModel();
//        final SampleModel sm = outRef.getSampleModel();
        final ColorModel cm = null;
        final SampleModel sm = null;

        for(Pyramid pyramid : Pyramids.getPyramids(outRef)){
            for(Mosaic mosaic : pyramid.getMosaics()){
                final Dimension tileSize = mosaic.getTileSize();
                final double[] upperLeftGeo = mosaic.getUpperLeftCorner().getCoordinate();

                final Dimension gridSize = mosaic.getGridSize();
                for(int y=0;y<gridSize.height;y++){
                    for(int x=0;x<gridSize.width;x++){
                        final MathTransform gridToCRS = Pyramids.getTileGridToCRS(mosaic, new Point(x, y), PixelInCell.CELL_CENTER);
                        final MathTransform crsToGrid = gridToCRS.inverse();
                        final double[] baseCoord = new double[upperLeftGeo.length];
                        crsToGrid.transform(upperLeftGeo, 0, baseCoord, 0, 1);
                        final MathCalcImageEvaluator eval = new MathCalcImageEvaluator(baseCoord, gridToCRS, evaluator.copy());
                        final ProcessedRenderedImage image = new ProcessedRenderedImage(sm, cm, eval, tileSize.width, tileSize.height);
                        mosaic.writeTiles(Stream.of(new DefaultImageTile(image, new Point(x, y))), null);
                    }
                }
            }
        }
    }


    /**
     *
     */
    public static interface SampleEvaluator {

        /**
         * Evaluate the new sample values at given geographic coordinate.
         *
         * @param position , coordinate where to evaluate the sample.
         * @param sampleBuffer , new samples must be set in this buffer
         */
        void evaluate(DirectPosition position, double[] sampleBuffer);

        SampleEvaluator copy() throws FactoryException;

    }

}
