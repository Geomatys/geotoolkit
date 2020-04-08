/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.List;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.image.ImageProcessor;
import org.apache.sis.image.WritablePixelIterator;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.referencing.operation.transform.LinearTransform;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.NoSuchDataException;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.coverage.SampleDimensionUtils;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.storage.multires.AbstractTileGenerator;
import org.geotoolkit.storage.multires.Mosaic;
import org.geotoolkit.storage.multires.Pyramid;
import org.geotoolkit.storage.multires.Pyramids;
import org.geotoolkit.storage.multires.Tile;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.util.NoSuchIdentifierException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class CoverageTileGenerator extends AbstractTileGenerator {

    private final GridCoverageResource resource;
    private final double[] empty;

    private InterpolationCase interpolation = InterpolationCase.NEIGHBOR;
    private double[] fillValues;

    public CoverageTileGenerator(GridCoverageResource resource) throws DataStoreException {
        ArgumentChecks.ensureNonNull("resource", resource);

        this.resource = resource;

        try {
            List<SampleDimension> sampleDimensions = resource.getSampleDimensions();
            if (sampleDimensions == null || sampleDimensions.isEmpty()) {
                throw new DataStoreException("Base resource sample dimensions are undefined");
            }
            empty = new double[sampleDimensions.size()];
            for (int i=0;i<empty.length;i++) {
                empty[i] = getEmptyValue(sampleDimensions.get(i));
            }

        } catch (DataStoreException ex) {
            throw ex;
        }
    }

    public void setInterpolation(InterpolationCase interpolation) {
        ArgumentChecks.ensureNonNull("interpolation", interpolation);
        this.interpolation = interpolation;
    }

    public InterpolationCase getInterpolation() {
        return interpolation;
    }

    public void setFillValues(double[] fillValues) {
        this.fillValues = fillValues;
    }

    public double[] getFillValues() {
        return fillValues;
    }

    private static double getEmptyValue(SampleDimension dim){
        //dim = dim.forConvertedValues(true);
        double fillValue = Double.NaN;
        final double[] nodata = SampleDimensionUtils.getNoDataValues(dim);
        if (nodata!=null && nodata.length>0) {
            fillValue = nodata[0];
        }
        return fillValue;
    }

    @Override
    protected boolean isEmpty(Tile tileData) throws DataStoreException {
        ImageTile it = (ImageTile) tileData;
        try {
            final RenderedImage image = it.getImage();
            return BufferedImages.isAll(image, empty);
        } catch (IOException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }
    }

    @Override
    public Tile generateTile(Pyramid pyramid, Mosaic mosaic, Point tileCoord) throws DataStoreException {
        final Dimension tileSize = mosaic.getTileSize();
        final CoordinateReferenceSystem crs = pyramid.getCoordinateReferenceSystem();
        final LinearTransform gridToCrsNd = Pyramids.getTileGridToCRS(mosaic, tileCoord, PixelInCell.CELL_CENTER);
        final long[] high = new long[crs.getCoordinateSystem().getDimension()];
        high[0] = tileSize.width-1; //inclusive
        high[1] = tileSize.height-1; //inclusive
        final GridExtent extent = new GridExtent(null, null, high, true);
        final GridGeometry gridGeomNd = new GridGeometry(extent, PixelInCell.CELL_CENTER, gridToCrsNd, crs);

        GridCoverage coverage;
        try {
            coverage = resource.read(gridGeomNd);
        } catch (NoSuchDataException ex) {
            //create an empty tile
            final BufferedImage img = BufferedImages.createImage(tileSize.width, tileSize.height, empty.length, DataBuffer.TYPE_DOUBLE);
            final WritablePixelIterator ite = WritablePixelIterator.create(img);
            while (ite.next()) {
                ite.setPixel(fillValues == null ? empty : fillValues);
            }
            ite.close();
            return new DefaultImageTile(img, tileCoord);
        } catch (DataStoreException ex) {
            throw ex;
        }

        //at this point we should have a coverage 2D
        //if not, this means the source coverage has more dimensions then the pyramid
        //resample coverage to exact tile grid geometry
        try {
            final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("geotoolkit", "coverage:resample");
            final Parameters params = Parameters.castOrWrap(desc.getInputDescriptor().createValue());
            params.parameter("Source").setValue(coverage);
            params.parameter("GridGeometry").setValue(gridGeomNd);
            params.parameter("Background").setValue(fillValues == null ? empty : fillValues);
            params.parameter("InterpolationType").setValue(interpolation);

            final Process process = desc.createProcess(params);
            final ParameterValueGroup results = process.call();
            coverage = (GridCoverage) results.parameter("result").getValue();
        } catch (ProcessException | NoSuchIdentifierException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }

        RenderedImage image = coverage.render(null);
		ImageProcessor ip = new ImageProcessor();
		image = ip.prefetch(image);
        return new DefaultImageTile(image, tileCoord);
    }

}
