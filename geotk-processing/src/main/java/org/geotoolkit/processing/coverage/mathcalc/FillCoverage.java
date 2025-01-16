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
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Arrays;
import java.util.stream.Stream;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverageBuilder;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.operation.matrix.Matrices;
import org.apache.sis.referencing.operation.matrix.MatrixSIS;
import org.apache.sis.referencing.operation.transform.DefaultMathTransformFactory;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.WritableGridCoverageResource;
import org.apache.sis.storage.tiling.WritableTileMatrix;
import org.apache.sis.storage.tiling.WritableTileMatrixSet;
import org.apache.sis.storage.tiling.WritableTiledResource;
import org.apache.sis.util.Utilities;
import org.geotoolkit.geometry.HyperCubeIterator;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.storage.coverage.*;
import org.geotoolkit.storage.multires.TileMatrices;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.apache.sis.coverage.grid.PixelInCell;
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
     * @throws DataStoreException
     */
    public void fill(WritableGridCoverageResource outRef, SampleEvaluator evaluator, Envelope env) throws DataStoreException {

        final GridGeometry gg;
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
        if (env != null) {
            if (!Utilities.equalsIgnoreMetadata(env.getCoordinateReferenceSystem(),gg.getCoordinateReferenceSystem())) {
                throw new DataStoreException("Envelope is not in data CRS.");
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
                throw new DataStoreException(ex.getMessage(), ex);
            }
        }

        //create iterator
        final int[] maxSize = new int[nbDim];
        Arrays.fill(maxSize, 1);
        maxSize[0] = 256;
        maxSize[1] = 256;
        final HyperCubeIterator ite = new HyperCubeIterator(mins, maxs, maxSize);

        //loop on all slices pieces
        final MathTransformFactory mathFactory = DefaultMathTransformFactory.provider();
        while (ite.hasNext()) {
            final HyperCubeIterator.HyperCube cube = ite.next();
            final long[] hcubeLower = cube.getLower();
            final long[] hcubeUpper = cube.getUpper();
            for(int i=2;i<nbDim;i++){
                positionGrid.setCoordinate(i, hcubeLower[i]);
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
                        positionGrid.setCoordinate(0, x);
                        positionGrid.setCoordinate(1, y);
                        gridToCrs.transform(positionGrid, positionGeo);
                        evaluator.evaluate(positionGeo, sampleData);
                        raster.setSample(Math.toIntExact(x-hcubeLower[0]), Math.toIntExact(y-hcubeLower[1]), 0, sampleData[0]);
                    }
                }
            } catch (TransformException ex) {
                throw new DataStoreException(ex.getMessage(), ex);
            }

            //Calculate grid to crs of this zone
            final MatrixSIS matrix = Matrices.createDiagonal(nbDim+1, nbDim+1);
            for (int i=0;i<nbDim;i++) {
                matrix.setElement(i, nbDim, hcubeLower[i]);
            }
            final MathTransform cornerToGrid;
            try {
                cornerToGrid = mathFactory.createAffineTransform(matrix);
            } catch (FactoryException ex) {
                throw new DataStoreException(ex.getMessage(), ex);
            }
            final MathTransform concat = MathTransforms.concatenate(cornerToGrid, gridToCrs);

            final GridCoverageBuilder gcb = new GridCoverageBuilder();
            gcb.setDomain(new GridGeometry(null, PixelInCell.CELL_CENTER, concat, gg.getCoordinateReferenceSystem()));
            gcb.setValues(zoneImage);
            final GridCoverage zoneCoverage = gcb.build();
            outRef.write(zoneCoverage, WritableGridCoverageResource.CommonOption.UPDATE);
        }

    }

    /**
     * Fill given coverage reference, providing it with processed images.
     *
     * @param evaluator
     * @param outRef
     */
    public static void fill(WritableTiledResource outRef, SampleEvaluator evaluator)
            throws DataStoreException, TransformException, FactoryException {

//        final ColorModel cm = outRef.getColorModel();
//        final SampleModel sm = outRef.getSampleModel();
        final ColorModel cm = null;
        final SampleModel sm = null;

        for(WritableTileMatrixSet matrixset : outRef.getTileMatrixSets()){
            for(WritableTileMatrix matrix : matrixset.getTileMatrices().values()){
                final int[] tileSize = TileMatrices.getTileSize(matrix);
                final double[] upperLeftGeo = TileMatrices.getUpperLeftCorner(matrix).getCoordinates();

                final Dimension gridSize = TileMatrices.getGridSize(matrix);
                for(int y=0;y<gridSize.height;y++){
                    for(int x=0;x<gridSize.width;x++){
                        final MathTransform gridToCRS = TileMatrices.getTileGridToCRS(matrix, new long[]{x, y}, PixelInCell.CELL_CENTER);
                        final MathTransform crsToGrid = gridToCRS.inverse();
                        final double[] baseCoord = new double[upperLeftGeo.length];
                        crsToGrid.transform(upperLeftGeo, 0, baseCoord, 0, 1);
                        final MathCalcImageEvaluator eval = new MathCalcImageEvaluator(baseCoord, gridToCRS, evaluator.copy());
                        final ProcessedRenderedImage image = new ProcessedRenderedImage(sm, cm, eval, (int) tileSize[0], (int) tileSize[1]);
                        matrix.writeTiles(Stream.of(new DefaultImageTile(matrix, image, new long[]{x, y})));
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
