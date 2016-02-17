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

package org.geotoolkit.coverage;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.referencing.CRS;
import org.apache.sis.referencing.crs.DefaultCompoundCRS;
import org.geotoolkit.referencing.operation.matrix.GeneralMatrix;
import org.junit.Test;
import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.coverage.grid.GridGeometry;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.metadata.iso.spatial.PixelTranslation;

import static org.junit.Assert.*;
import org.opengis.referencing.datum.PixelInCell;

/**
 * Test GridCoverageStack class.
 *
 * @author Johann Sorel (Geomatys)
 */
public class GridCoverageStackTest extends org.geotoolkit.test.TestBase {

    private static final double DELTA = 0.00000001;

    private static Map<String,String> name(final String name) {
        return Collections.singletonMap(IdentifiedObject.NAME_KEY, name);
    }

    /**
     * Verify 3D grid coverage stack creation and correct grid geometry.
     */
    @Test
    public void test3D() throws FactoryException, IOException, TransformException{

        final CoordinateReferenceSystem horizontal = CommonCRS.WGS84.normalizedGeographic();
        final CoordinateReferenceSystem vertical = CommonCRS.Vertical.ELLIPSOIDAL.crs();
        final CoordinateReferenceSystem crs = new DefaultCompoundCRS(name("wgs84+ele"), horizontal, vertical);

        final GridCoverageStack stack = createCube3D(100, 100, crs);
        assertTrue(CRS.equalsIgnoreMetadata(crs, stack.getCoordinateReferenceSystem()));

        final GridGeometry gridGeom = stack.getGridGeometry();
        assertNotNull(gridGeom);

        //check grid envelope
        final GridEnvelope gridEnv = gridGeom.getExtent();
        assertNotNull(gridEnv);
        assertEquals(3,gridEnv.getDimension());
        assertEquals(0, gridEnv.getLow(0));
        assertEquals(0, gridEnv.getLow(1));
        assertEquals(0, gridEnv.getLow(2));

        assertEquals(99, gridEnv.getHigh(0));
        assertEquals(99, gridEnv.getHigh(1));
        assertEquals(2, gridEnv.getHigh(2));

        //check grid to crs
        final MathTransform gridToCRS = PixelTranslation.translate(gridGeom.getGridToCRS(), PixelInCell.CELL_CENTER, PixelInCell.CELL_CORNER);
        assertEquals(3, gridToCRS.getSourceDimensions());
        assertEquals(3, gridToCRS.getTargetDimensions());
        final double[] lower = new double[]{0,0,0};
        final double[] upper = new double[]{99,99,2};
        gridToCRS.transform(lower,0,lower,0,1);
        gridToCRS.transform(upper,0,upper,0,1);

        assertEquals(0.0, lower[0], DELTA);
        assertEquals(0.0, lower[1], DELTA);
        assertEquals(10, lower[2], DELTA);

        assertEquals(99, upper[0], DELTA);
        assertEquals(99, upper[1], DELTA);
        assertEquals(50, upper[2], DELTA);

    }

    /**
     * Verify 4D grid coverage stack creation and correct grid geometry.
     */
    @Test
    public void test4D() throws FactoryException, IOException, TransformException{

        final CoordinateReferenceSystem horizontal = CommonCRS.WGS84.normalizedGeographic();
        final CoordinateReferenceSystem vertical = CommonCRS.Vertical.ELLIPSOIDAL.crs();
        final CoordinateReferenceSystem temporal = CommonCRS.Temporal.JAVA.crs();
        final CoordinateReferenceSystem crs3d = new DefaultCompoundCRS(name("wgs84+ele"), horizontal,vertical);
        final CoordinateReferenceSystem crs4d = new DefaultCompoundCRS(name("wgs84+ele+time"), crs3d,temporal);

        final GridCoverageStack stack = createCube4D(100, 100, crs4d);
        assertTrue(CRS.equalsIgnoreMetadata(crs4d, stack.getCoordinateReferenceSystem()));

        final GridGeometry gridGeom = stack.getGridGeometry();
        assertNotNull(gridGeom);

        //check grid envelope
        final GridEnvelope gridEnv = gridGeom.getExtent();
        assertNotNull(gridEnv);
        assertEquals(4,gridEnv.getDimension());
        assertEquals(0, gridEnv.getLow(0));
        assertEquals(0, gridEnv.getLow(1));
        assertEquals(0, gridEnv.getLow(2));
        assertEquals(0, gridEnv.getLow(3));

        assertEquals(99, gridEnv.getHigh(0));
        assertEquals(99, gridEnv.getHigh(1));
        assertEquals(2, gridEnv.getHigh(2));
        assertEquals(3, gridEnv.getHigh(3));

        //check grid to crs
        //-- in convention gridToCrs in PixelInCell.Center
        final MathTransform gridToCRS = PixelTranslation.translate(gridGeom.getGridToCRS(), PixelInCell.CELL_CENTER, PixelInCell.CELL_CORNER);
        assertEquals(4, gridToCRS.getSourceDimensions());
        assertEquals(4, gridToCRS.getTargetDimensions());
        final double[] lower = new double[]{0,0,0,0};
        final double[] upper = new double[]{99,99,2,3};
        gridToCRS.transform(lower,0,lower,0,1);
        gridToCRS.transform(upper,0,upper,0,1);

        assertEquals(0.0, lower[0], DELTA);
        assertEquals(0.0, lower[1], DELTA);
        assertEquals(10, lower[2], DELTA);
        assertEquals(3, lower[3], DELTA);

        assertEquals( 99, upper[0], DELTA);
        assertEquals( 99, upper[1], DELTA);
        assertEquals( 50, upper[2], DELTA);
        assertEquals( 12, upper[3], DELTA);

    }

    private static GridCoverageStack createCube4D(int width, int height, CoordinateReferenceSystem crs)
            throws IOException, TransformException, FactoryException{
        final GridCoverageStack slice0 = createSubStack3D(width, height, 3, crs);
        final GridCoverageStack slice1 = createSubStack3D(width, height, 6, crs);
        final GridCoverageStack slice2 = createSubStack3D(width, height, 9, crs);
        final GridCoverageStack slice3 = createSubStack3D(width, height, 12, crs);
        return new GridCoverageStack("4d", Arrays.asList(slice0,slice1,slice2,slice3), 3);
    }

    private static GridCoverageStack createSubStack3D(int width, int height, double t, CoordinateReferenceSystem crs)
            throws IOException, TransformException, FactoryException{

        final GridCoverage2D slice0 = createSlice4D(width, height, 10, t, crs);
        final GridCoverage2D slice1 = createSlice4D(width, height, 20, t, crs);
        final GridCoverage2D slice2 = createSlice4D(width, height, 50, t, crs);
        return new GridCoverageStack(null, Arrays.asList(slice0,slice1,slice2), 2);
    }

    private static GridCoverageStack createCube3D(int width, int height, CoordinateReferenceSystem crs)
            throws IOException, TransformException, FactoryException{

        final GridCoverage2D slice0 = createSlice3D(width, height, 10, crs);
        final GridCoverage2D slice1 = createSlice3D(width, height, 20, crs);
        final GridCoverage2D slice2 = createSlice3D(width, height, 50, crs);
        return new GridCoverageStack(null, Arrays.asList(slice0,slice1,slice2), 2);
    }

    private static GridCoverage2D createSlice3D(int width, int height, double z, CoordinateReferenceSystem crs) throws FactoryException{
        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        final GeneralMatrix matrix = new GeneralMatrix(4);
        matrix.setIdentity();
        matrix.setElement(2, 3, z);

        final MathTransformFactory mf = FactoryFinder.getMathTransformFactory(null);
        final MathTransform gridtoCrs = mf.createAffineTransform(matrix);

        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setName("slice");
        gcb.setPixelAnchor(PixelInCell.CELL_CORNER);
        gcb.setCoordinateReferenceSystem(crs);
        gcb.setGridToCRS(gridtoCrs);
        gcb.setRenderedImage(image);

        return gcb.getGridCoverage2D();
    }

    private static GridCoverage2D createSlice4D(int width, int height, double z, double t, CoordinateReferenceSystem crs) throws FactoryException{
        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        final GeneralMatrix matrix = new GeneralMatrix(5);
        matrix.setIdentity();
        matrix.setElement(2, 4, z);
        matrix.setElement(3, 4, t);

        final MathTransformFactory mf = FactoryFinder.getMathTransformFactory(null);
        final MathTransform gridtoCrs = mf.createAffineTransform(matrix);

        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setName("slice");
        gcb.setPixelAnchor(PixelInCell.CELL_CORNER);
        gcb.setCoordinateReferenceSystem(crs);
        gcb.setGridToCRS(gridtoCrs);
        gcb.setRenderedImage(image);

        return gcb.getGridCoverage2D();
    }

}
