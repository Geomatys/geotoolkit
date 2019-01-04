/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
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
package org.geotoolkit.internal.coverage;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.geotoolkit.coverage.Coverage;
import org.geotoolkit.coverage.GridCoverageStack;
import org.geotoolkit.coverage.grid.GridCoverage;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.apache.sis.coverage.grid.GridGeometry;
import org.geotoolkit.coverage.grid.GridGeometryIterator;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.opengis.coverage.CannotEvaluateException;
import org.opengis.coverage.SampleDimension;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Rasterize a Coverage to a GridCoverage.
 *
 * @author Johann Sorel (Geomatys)
 */
public class CoverageRasterizer {

    private Coverage source;
    private GridCoverage target;
    private CoordinateReferenceSystem targetCrs;

    public void setSource(Coverage source) {
        this.source = source;
    }

    public Coverage getSource() {
        return source;
    }

    public void setTarget(GridCoverage target) {
        this.target = target;
        this.targetCrs = target.getCoordinateReferenceSystem();
    }

    public GridCoverage getTarget() {
        return target;
    }

    public GridCoverage execute() throws IOException, TransformException, FactoryException {

        final GridGeometry gridGeometry = target.getGridGeometry();

        final int axis = CRSUtilities.firstHorizontalAxis(targetCrs);
        if (axis != 0) throw new IllegalArgumentException("Unsupported CRS, Horizontal component must start at index 0");
        final int dimension = targetCrs.getCoordinateSystem().getDimension();

        if (dimension == 2) {
            //single slice
            return buildSlice((GridGeometry) gridGeometry);
        } else {
            //cube
            return build(gridGeometry, 2);
        }
    }

    private GridCoverage build(GridGeometry global, int zDim) throws IOException, TransformException, FactoryException {
        final int dimension = global.getExtent().getDimension();
        final GridGeometryIterator ite = new GridGeometryIterator(global, targetCrs, zDim);

        if (zDim == dimension-1) {
            //last dimension
            final List<GridCoverage> slices = new ArrayList<>();
            while (ite.hasNext()) {
                final GridGeometry sliceGridGeom = ite.next();
                final GridCoverage slice = buildSlice(sliceGridGeom);
                slices.add(slice);
            }
            return new GridCoverageStack("stack", slices, zDim);
        } else {
            final List<GridCoverage> cubes = new ArrayList<>();
            while (ite.hasNext()) {
                final GridGeometry sliceGridGeom = ite.next();
                final GridCoverage cube = build(sliceGridGeom, zDim+1);
                cubes.add(cube);
            }
            return new GridCoverageStack("stack", cubes, zDim);
        }
    }

    private GridCoverage buildSlice(GridGeometry sliceGridGeom) throws TransformException, FactoryException {

        final List<? extends SampleDimension> samples = source.getSampleDimensions();

        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setName("slice");
        gcb.setGridGeometry(sliceGridGeom);
        gcb.setSampleDimensions(samples);
        final GridCoverage2D coverage = (GridCoverage2D) gcb.build();

        final BufferedImage image = (BufferedImage) coverage.getRenderedImage();
        final WritableRaster raster = image.getRaster();
        final double[] pixel = new double[image.getSampleModel().getNumDataElements()];
        final double[] fillValues = new double[pixel.length];
        Arrays.fill(fillValues, Double.NaN);

        final MathTransform gridToCRS = sliceGridGeom.getGridToCRS(PixelInCell.CELL_CENTER);
        final MathTransform crsToSource = CRS.findOperation(coverage.getCoordinateReferenceSystem(), source.getCoordinateReferenceSystem(), null).getMathTransform();
        final MathTransform gridToSource = MathTransforms.concatenate(gridToCRS, crsToSource);
        final GeneralDirectPosition pos = new GeneralDirectPosition(source.getCoordinateReferenceSystem());

        //fill samples
        final int width = image.getWidth();
        final int height = image.getHeight();
        final double[] imgPos = new double[pos.getDimension()];
        final double[] crsPos = new double[pos.getDimension()];
        for (int y=0;y<height;y++) {
            for (int x=0;x<width;x++) {
                imgPos[0] = x;
                imgPos[1] = y;
                gridToSource.transform(imgPos, 0, crsPos, 0, 1);
                pos.setCoordinate(crsPos);
                try {
                    source.evaluate(pos, pixel);
                    raster.setPixel(x, y, pixel);
                } catch (CannotEvaluateException ex) {
                    raster.setPixel(x, y, fillValues);
                }
            }
        }

        return coverage;
    }

}
