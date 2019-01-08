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
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.image.WritablePixelIterator;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.referencing.operation.transform.LinearTransform;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.coverage.Coverage;
import org.geotoolkit.coverage.CoverageStack;
import org.apache.sis.coverage.SampleDimension;
import org.geotoolkit.coverage.SampleDimensionUtils;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.io.CoverageReader;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.DisjointCoverageDomainException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.data.multires.AbstractTileGenerator;
import org.geotoolkit.data.multires.Mosaic;
import org.geotoolkit.data.multires.Pyramid;
import org.geotoolkit.data.multires.Pyramids;
import org.geotoolkit.data.multires.Tile;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.ProcessFinder;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.Matrix;
import org.opengis.util.NoSuchIdentifierException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class CoverageTileGenerator extends AbstractTileGenerator {

    private final CoverageResource resource;
    private final double[] empty;

    private InterpolationCase interpolation = InterpolationCase.NEIGHBOR;
    private double[] fillValues;

    public CoverageTileGenerator(CoverageResource resource) throws CoverageStoreException {
        ArgumentChecks.ensureNonNull("resource", resource);

        this.resource = resource;

        final CoverageReader reader = resource.acquireReader();
        try {
            List<SampleDimension> sampleDimensions = reader.getSampleDimensions();

            if (sampleDimensions == null) {
                final GridCoverageReadParam param = new GridCoverageReadParam();
                param.setDeferred(true);
                Coverage coverage = reader.read(param);
                sampleDimensions = (List) coverage.getSampleDimensions();
            }

            empty = new double[sampleDimensions.size()];
            for (int i=0;i<empty.length;i++) {
                empty[i] = getEmptyValue(sampleDimensions.get(i));
            }

            this.resource.recycle(reader);
        } catch (CoverageStoreException ex) {
            //dispose the reader, it may be in an invalid state
            try {
                reader.dispose();
            } catch (CoverageStoreException e) {
                ex.addSuppressed(e);
            }
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
        dim = dim.forConvertedValues(true);
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

        //extract resolution
        final Matrix matrix = gridToCrsNd.getMatrix();
        final double[] resolution = new double[high.length];
        for (int i=0;i<resolution.length;i++) {
            resolution[i] = Math.abs(matrix.getElement(i, i));
        }

        final CoverageReader reader = resource.acquireReader();
        Coverage coverage;
        try {
            final GridCoverageReadParam param = new GridCoverageReadParam();
            param.setEnvelope(gridGeomNd.getEnvelope());
            param.setResolution(resolution);
            coverage = reader.read(param);
            resource.recycle(reader);
        } catch (DisjointCoverageDomainException ex) {
            resource.recycle(reader);
            //create an empty tile
            final BufferedImage img = BufferedImages.createImage(tileSize.width, tileSize.height, empty.length, DataBuffer.TYPE_DOUBLE);
            final WritablePixelIterator ite = WritablePixelIterator.create(img);
            while (ite.next()) {
                ite.setPixel(fillValues == null ? empty : fillValues);
            }
            ite.close();
            return new DefaultImageTile(img, tileCoord);
        } catch (CoverageStoreException ex) {
            //dispose the reader, it may be in an invalid state
            try {
                reader.dispose();
            } catch (CoverageStoreException e) {
                ex.addSuppressed(e);
            }
            throw ex;
        }

        //at this point we should have a coverage 2D
        //if not, this means the source coverage has more dimensions then the pyramid
        if (coverage instanceof GridCoverage2D) {

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
                coverage = (Coverage) results.parameter("result").getValue();
            } catch (ProcessException | NoSuchIdentifierException ex) {
                throw new DataStoreException(ex.getMessage(), ex);
            }

        } else if (coverage instanceof CoverageStack) {
            throw new DataStoreException("Pyramid tile resulted in a Coverage stack from the source coverage, "
                    + "this happens when source coverage has more dimensions then the pyramid. Given source coverage can not be used with this pyramid");
        } else {
            throw new DataStoreException("Unexpected coverage type : "+coverage.getClass().getName());
        }

        GridCoverage2D coverage2d = (GridCoverage2D) coverage;
        coverage2d = coverage2d.view(ViewType.GEOPHYSICS);
        final RenderedImage image = coverage2d.getRenderedImage();
        return new DefaultImageTile(image, tileCoord);
    }

}
