/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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

package org.geotoolkit.processing.coverage.pgpyramid;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferFloat;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.media.jai.RasterFactory;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.storage.coverage.GridMosaic;
import org.geotoolkit.storage.coverage.GridMosaicCoverage2D;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.storage.coverage.Pyramid;
import org.geotoolkit.storage.coverage.PyramidalCoverageReference;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.memory.MPCoverageStore;
import org.geotoolkit.storage.coverage.DefaultCoverageReference;
import org.geotoolkit.feature.type.DefaultName;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.image.BufferedImages;
import org.junit.Test;
import org.opengis.util.GenericName;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class PyramidTest {

    /**
     * Just test there is no error on this simple case.
     * @throws Exception
     */
    @Test
    public void testPyramid() throws Exception{

        final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();
        final int tileSize = 256;
        final GeneralDirectPosition upperLeft = new GeneralDirectPosition(crs);
        upperLeft.setCoordinate(-180,90);

        final MPCoverageStore store = new MPCoverageStore();
        final GenericName name = DefaultName.create(null, "test");
        final PyramidalCoverageReference pcr = (PyramidalCoverageReference) store.create(name);
        final List<GridSampleDimension> dims = new ArrayList<GridSampleDimension>();
        final GridSampleDimension dim1 = new GridSampleDimension("sampleDesc1");
        final GridSampleDimension dim2 = new GridSampleDimension("sampleDesc2");
        dims.add(dim1);
        dims.add(dim2);
        pcr.setSampleDimensions(dims);

        final Pyramid pyramid = pcr.createPyramid(crs);
        final GridMosaic mosaic = pcr.createMosaic(pyramid.getId(), new Dimension(2, 1), new Dimension(tileSize, tileSize), upperLeft, 1);

        final RenderedImage tile1 = createImage(2, tileSize, tileSize);
        pcr.writeTile(pyramid.getId(), mosaic.getId(), 0, 0, tile1);
        final RenderedImage tile2 = createImage(2, tileSize, tileSize);
        pcr.writeTile(pyramid.getId(), mosaic.getId(), 1, 0, tile2);


        final GridCoverage2D coverage = GridMosaicCoverage2D.create(pcr, mosaic);
        final double[] scales = new double[]{360.0/256.0};
        final GeneralEnvelope env = new GeneralEnvelope(crs);
        env.setRange(0, -180, +180);
        env.setRange(1, -90, +90);

        final Map<Envelope, double[]> res = new HashMap<>();
        res.put(env, scales);

        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("coverage", "coveragepyramid");
        final ParameterValueGroup input = desc.getInputDescriptor().createValue();
        input.parameter("coverageref").setValue(new DefaultCoverageReference(coverage, name));
        input.parameter("in_coverage_store").setValue(store);
        input.parameter("tile_size").setValue(new Dimension(tileSize, tileSize));
        input.parameter("interpolation_type").setValue(InterpolationCase.NEIGHBOR);
        input.parameter("pyramid_name").setValue(name.tip().toString());
        input.parameter("resolution_per_envelope").setValue(res);
        final org.geotoolkit.process.Process p = desc.createProcess(input);
        p.call();




    }

    public static RenderedImage createImage(int nbband, int width, int height){

        final float[] data = new float[nbband*width*height];
        final int[] bankIndices = new int[nbband];
        final int[] bandOffsets = new int[nbband];
        for(int i=0;i<nbband;i++){
            bandOffsets[i] = i*width*height;
        }

        final DataBuffer dataBuffer = new DataBufferFloat(data, data.length);


        final int scanlinestride = width;
        final WritableRaster raster = RasterFactory.createBandedRaster(dataBuffer, width, height, scanlinestride, bankIndices, bandOffsets, new Point(0,0));
        final ColorModel cm = BufferedImages.createGrayScaleColorModel(DataBuffer.TYPE_FLOAT, nbband,0, -10, +10);

        final RenderedImage image = new BufferedImage(cm, raster, false, null);

        return image;
    }

}
