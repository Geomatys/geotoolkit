
package org.geotoolkit.coverage;

import java.util.Collections;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.memory.MPCoverageStore;
import org.geotoolkit.feature.type.DefaultName;
import org.geotoolkit.referencing.CRS;
import org.apache.sis.referencing.crs.DefaultCompoundCRS;
import org.geotoolkit.image.BufferedImages;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.coverage.grid.GridCoordinates;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.coverage.grid.GridGeometry;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.apache.sis.referencing.CommonCRS;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class PyramidReaderTest {

    private static final float DELTA = 0.000000001f;

    /**
     * 4D calc test
     * @throws Exception
     */
    @Test
    public void coverage4DTest() throws Exception{

        //create a small pyramid
        final CoverageStore store = new MPCoverageStore();
        final CoordinateReferenceSystem horizontal = CRS.decode("EPSG:4326",true);
        final CoordinateReferenceSystem vertical = CommonCRS.Vertical.ELLIPSOIDAL.crs();
        final CoordinateReferenceSystem temporal = CommonCRS.Temporal.JAVA.crs();
        final CoordinateReferenceSystem crs = new DefaultCompoundCRS(
                Collections.singletonMap(DefaultCompoundCRS.NAME_KEY, "4dcrs"), horizontal,vertical,temporal);
        final int width = 28;
        final int height = 13;

        final PyramidalCoverageReference ref1 = (PyramidalCoverageReference) store.create(new DefaultName("test1"));
        create4DPyramid(ref1, crs, width, height, new double[][]{
            {-5,-9,  12},
            {-5, 0,  -7},
            {-5,21,  51},
            {62,-9,  -3},
            {62, 0,   5},
            {62,21,  17},
        });


        final GeneralGridGeometry gridGeomReader = ref1.acquireReader().getGridGeometry(0);
        final GridEnvelope gridEnvReader = gridGeomReader.getExtent();
        final MathTransform gridToCrsReader = gridGeomReader.getGridToCRS();
        
        final GridCoverage result = ref1.acquireReader().read(0, null);
        Assert.assertEquals(crs,result.getCoordinateReferenceSystem());

        final GridGeometry gridGeom   = result.getGridGeometry();
        final GridEnvelope gridEnv    = gridGeom.getExtent();
        final MathTransform gridToCrs = gridGeom.getGridToCRS();

        //-- we must have the same grid grometry definition between the reader and the coverage
        Assert.assertEquals(gridEnvReader, gridEnv);

        final GridCoordinates lowerCorner = gridEnv.getLow();
        final GridCoordinates highCorner  = gridEnv.getHigh();

        //check grid envelope
        Assert.assertEquals(0,  lowerCorner.getCoordinateValue(0));
        Assert.assertEquals(0,  lowerCorner.getCoordinateValue(1));
        Assert.assertEquals(0,  lowerCorner.getCoordinateValue(2));
        Assert.assertEquals(0,  lowerCorner.getCoordinateValue(3));
        Assert.assertEquals(111,highCorner.getCoordinateValue(0)); //28 * 4 -1 
        Assert.assertEquals(38, highCorner.getCoordinateValue(1)); //13 * 3 -1
        Assert.assertEquals(1,  highCorner.getCoordinateValue(2)); // 2 slices
        Assert.assertEquals(2,  highCorner.getCoordinateValue(3)); // 3 slices

        //check transform
        final double[] buffer = new double[4];
        gridToCrs.transform(new double[]{0, 0, 0, 0} , 0, buffer, 0, 1);
        Assert.assertArrayEquals(new double[]{-49.5, 59.5, 28.5, -4.5}, buffer, DELTA);
        gridToCrs.transform(new double[]{0, 0, 0, 1} , 0, buffer, 0, 1);
        Assert.assertArrayEquals(new double[]{-49.5, 59.5, 28.5, 10.5}, buffer, DELTA);
        gridToCrs.transform(new double[]{0, 0, 0, 2} , 0, buffer, 0, 1);
        Assert.assertArrayEquals(new double[]{-49.5, 59.5, 28.5, 21}, buffer, DELTA);
        gridToCrs.transform(new double[]{0, 0, 1, 0} , 0, buffer, 0, 1);
        Assert.assertArrayEquals(new double[]{-49.5, 59.5, 62, -4.5}, buffer, DELTA);
        gridToCrs.transform(new double[]{0, 0, 1, 1} , 0, buffer, 0, 1);
        Assert.assertArrayEquals(new double[]{-49.5, 59.5, 62, 10.5}, buffer, DELTA);
        gridToCrs.transform(new double[]{0, 0, 1, 2} , 0, buffer, 0, 1);
        Assert.assertArrayEquals(new double[]{-49.5, 59.5, 62, 21}, buffer, DELTA);
        
        //we must obtain the same results with the gridToCrs from the reader
        gridToCrsReader.transform(new double[]{0, 0, 0, 0} , 0, buffer, 0, 1);
        Assert.assertArrayEquals(new double[]{-49.5, 59.5, 28.5, -4.5},buffer, DELTA);
        gridToCrsReader.transform(new double[]{0, 0, 0, 1} , 0, buffer, 0, 1);
        Assert.assertArrayEquals(new double[]{-49.5, 59.5, 28.5, 10.5}, buffer, DELTA);
        gridToCrsReader.transform(new double[]{0, 0, 0, 2} , 0, buffer, 0, 1);
        Assert.assertArrayEquals(new double[]{-49.5, 59.5, 28.5, 21}, buffer, DELTA);
        gridToCrsReader.transform(new double[]{0, 0, 1, 0} , 0, buffer, 0, 1);
        Assert.assertArrayEquals(new double[]{-49.5, 59.5, 62, -4.5},buffer, DELTA);
        gridToCrsReader.transform(new double[]{0, 0, 1, 1} , 0, buffer, 0, 1);
        Assert.assertArrayEquals(new double[]{-49.5, 59.5, 62, 10.5}, buffer, DELTA);
        gridToCrsReader.transform(new double[]{0, 0, 1, 2} , 0, buffer, 0, 1);
        Assert.assertArrayEquals(new double[]{-49.5, 59.5, 62, 21},   buffer, DELTA);



        //check each block range
        Assert.assertTrue(result instanceof GridCoverageStack);
        final GridCoverageStack stackT = (GridCoverageStack) result;
        final CoverageStack.Element[] elementsT = stackT.getElements();
        Assert.assertEquals(-9.0,elementsT[0].getZCenter());
        Assert.assertEquals( 0.0,elementsT[1].getZCenter());
        Assert.assertEquals(21.0,elementsT[2].getZCenter());
        Assert.assertEquals(NumberRange.create(-13.5, true, -4.5, false),elementsT[0].getZRange()); // [ (-9-((0+9)/2) ... (-9+((0+ 9)/2) [
        Assert.assertEquals(NumberRange.create( -4.5, true, 10.5, false),elementsT[1].getZRange()); // [ ( 0-((0+9)/2) ... ( 0+((0+21)/2) [
        Assert.assertEquals(NumberRange.create( 10.5, true, 31.5, false),elementsT[2].getZRange()); // [ (21-((0+9)/2) ... (21+((0+21)/2) [

        final GridCoverageStack stackT0 = (GridCoverageStack) stackT.coveragesAt(-9).get(0);
        final CoverageStack.Element[] elementsT0 = stackT0.getElements();
        Assert.assertEquals(-5.0,elementsT0[0].getZCenter());
        Assert.assertEquals(62.0,elementsT0[1].getZCenter());
        Assert.assertEquals(NumberRange.create(-38.5, true, 28.5, false),elementsT0[0].getZRange()); // [ -5-((-5+62)/2) ... -5-((-5+62)/2) [
        Assert.assertEquals(NumberRange.create( 28.5, true, 95.5, false),elementsT0[1].getZRange()); // [ 62-((-5+62)/2) ... 62-((-5+62)/2) [
        final GridCoverageStack stackT1 = (GridCoverageStack) stackT.coveragesAt( 0).get(0);
        final CoverageStack.Element[] elementsT1 = stackT0.getElements();
        Assert.assertEquals(-5.0,elementsT1[0].getZCenter());
        Assert.assertEquals(62.0,elementsT1[1].getZCenter());
        Assert.assertEquals(NumberRange.create(-38.5, true, 28.5, false),elementsT1[0].getZRange()); // [ -5-((-5+62)/2) ... -5-((-5+62)/2) [
        Assert.assertEquals(NumberRange.create( 28.5, true, 95.5, false),elementsT1[1].getZRange()); // [ 62-((-5+62)/2) ... 62-((-5+62)/2) [
        final GridCoverageStack stackT2 = (GridCoverageStack) stackT.coveragesAt(21).get(0);
        final CoverageStack.Element[] elementsT2 = stackT0.getElements();
        Assert.assertEquals(-5.0,elementsT2[0].getZCenter());
        Assert.assertEquals(62.0,elementsT2[1].getZCenter());
        Assert.assertEquals(NumberRange.create(-38.5, true, 28.5, false),elementsT2[0].getZRange()); // [ -5-((-5+62)/2) ... -5-((-5+62)/2) [
        Assert.assertEquals(NumberRange.create( 28.5, true, 95.5, false),elementsT2[1].getZRange()); // [ 62-((-5+62)/2) ... 62-((-5+62)/2) [

    }

    /**
     *
     * @param ref
     * @param crs
     * @param geovalues [0...n slices][Z coord, T coord, sample value]
     */
    private static void create4DPyramid(PyramidalCoverageReference ref, CoordinateReferenceSystem crs,
            int width, int height, double[][] geovalues) throws DataStoreException{

        final Pyramid pyramid = ref.createPyramid(crs);

        final Dimension gridSize = new Dimension(4, 3);
        final Dimension tilePixelSize = new Dimension(width, height);

        for(double[] slice : geovalues){
            final GeneralDirectPosition upperLeft = new GeneralDirectPosition(crs);
            upperLeft.setCoordinate(-50,60,slice[0],slice[1]);
            final GridMosaic mosaic = ref.createMosaic(pyramid.getId(), gridSize, tilePixelSize, upperLeft, 1);

            final float sample = (float)slice[2];
            for(int x=0;x<gridSize.width;x++){
                for(int y=0;y<gridSize.height;y++){
                    ref.writeTile(pyramid.getId(), mosaic.getId(), x, y, createRenderedImage(
                            tilePixelSize.width, tilePixelSize.height, sample, sample));
                }
            }
        }

    }

    private static BufferedImage createRenderedImage(int width, int height, float fillValue1, float fillValue2){
        final BufferedImage baseImage = BufferedImages.createImage(width, height, 1 , DataBuffer.TYPE_FLOAT);
        final WritableRaster baseRaster1 = baseImage.getRaster();
        for(int x=0;x<width;x++){
            for(int y=0;y<height;y++){
                baseRaster1.setSample(x, y, 0, (y<height/2) ? fillValue1 : fillValue2 );
            }
        }
        return baseImage;
    }

    private static void testImageContent(RenderedImage image, int width, int height, float value1, float value2){
        final Raster resultRaster = image.getData();
        for(int x=0;x<width;x++){
            for(int y=0;y<height;y++){
                float v = resultRaster.getSampleFloat(x, y, 0);
                Assert.assertEquals( (y<height/2) ? value1 : value2, v, DELTA);
            }
        }
    }

}
