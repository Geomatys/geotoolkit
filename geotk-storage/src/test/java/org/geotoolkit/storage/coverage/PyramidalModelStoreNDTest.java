/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013-2014, Geomatys
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.util.stream.Stream;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.util.GeodeticObjectBuilder;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.WritableAggregate;
import org.apache.sis.storage.tiling.WritableTileMatrix;
import org.apache.sis.storage.tiling.WritableTileMatrixSet;
import org.apache.sis.storage.tiling.WritableTiledResource;
import org.apache.sis.util.Utilities;
import org.geotoolkit.storage.multires.DefiningTileMatrix;
import org.geotoolkit.storage.multires.DefiningTileMatrixSet;
import org.geotoolkit.storage.multires.TileMatrices;
import org.geotoolkit.util.NamesExt;
import static org.junit.Assert.*;
import org.junit.Test;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.GenericName;

/**
 * Abstract pyramid store test.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public abstract class PyramidalModelStoreNDTest <T extends WritableTiledResource & org.apache.sis.storage.GridCoverageResource> {

    private static final double DELTA = 0.00000001;

    private static final double CORNER_LONG = -180;
    private static final double CORNER_LAT  = 90;
    private static final double[] CORNER_V   = {-15, 46.58};
    // store an integer for each tile color, used as R,G,B
    // vertical, scale, tile col, tile row
    private final int[][][][] colors = new int[2][2][0][0];

    private DataStore store;
    private CoordinateReferenceSystem crs;
    private T ref;

    protected abstract DataStore createStore() throws Exception ;

    private DataStore getCoverageStore() throws Exception {

        if(store != null){
            return store;
        }

        //create a small pyramid
        store = createStore();
        final WritableAggregate agg = (WritableAggregate) store;
        final CoordinateReferenceSystem horizontal = CommonCRS.WGS84.normalizedGeographic();
        final CoordinateReferenceSystem vertical = CommonCRS.Vertical.ELLIPSOIDAL.crs();
        crs = new GeodeticObjectBuilder().addName("3dcrs").createCompoundCRS(horizontal,vertical);

        final GenericName name = NamesExt.create("test");
        ref = (T) agg.add(new DefiningGridCoverageResource(name));

        //prepare expected colors
        int color = 0;
        for(int v=0;v<CORNER_V.length;v++){
            colors[v][0] = new int[2][2];
            colors[v][1] = new int[4][3];
            colors[v][0][0][0] = color++;
            colors[v][0][1][0] = color++;
            colors[v][0][0][1] = color++;
            colors[v][0][1][1] = color++;
            colors[v][1][0][0] = color++;
            colors[v][1][1][0] = color++;
            colors[v][1][2][0] = color++;
            colors[v][1][3][0] = color++;
            colors[v][1][0][1] = color++;
            colors[v][1][1][1] = color++;
            colors[v][1][2][1] = color++;
            colors[v][1][3][1] = color++;
            colors[v][1][0][2] = color++;
            colors[v][1][1][2] = color++;
            colors[v][1][2][2] = color++;
            colors[v][1][3][2] = color++;
        }

        final WritableTileMatrixSet pyramid = ref.createTileMatrixSet(new DefiningTileMatrixSet(crs));
        for(int v=0;v<CORNER_V.length;v++){
            final int[] tileSize = new int[]{10,10};
            final WritableTileMatrix mosaic_s0 = pyramid.createTileMatrix(new DefiningTileMatrix(null,
                    TileMatrices.toTilingScheme(createCorner(CORNER_LONG,CORNER_LAT,CORNER_V[v]), new Dimension(2, 2), 1.0, tileSize), tileSize));
            final WritableTileMatrix mosaic_s1 = pyramid.createTileMatrix(new DefiningTileMatrix(null,
                    TileMatrices.toTilingScheme(createCorner(CORNER_LONG,CORNER_LAT,CORNER_V[v]), new Dimension(4, 3), 0.5, tileSize), tileSize));

            //insert tiles
            mosaic_s0.writeTiles(Stream.of(
                    new DefaultImageTile(mosaic_s0, createImage(colors[v][0][0][0]), 0, 0),
                    new DefaultImageTile(mosaic_s0, createImage(colors[v][0][1][0]), 1, 0),
                    new DefaultImageTile(mosaic_s0, createImage(colors[v][0][0][1]), 0, 1),
                    new DefaultImageTile(mosaic_s0, createImage(colors[v][0][1][1]), 1, 1)
                ));

            mosaic_s1.writeTiles(Stream.of(
                    new DefaultImageTile(mosaic_s1, createImage(colors[v][1][0][0]), 0, 0),
                    new DefaultImageTile(mosaic_s1, createImage(colors[v][1][1][0]), 1, 0),
                    new DefaultImageTile(mosaic_s1, createImage(colors[v][1][2][0]), 2, 0),
                    new DefaultImageTile(mosaic_s1, createImage(colors[v][1][3][0]), 3, 0),
                    new DefaultImageTile(mosaic_s1, createImage(colors[v][1][0][1]), 0, 1),
                    new DefaultImageTile(mosaic_s1, createImage(colors[v][1][1][1]), 1, 1),
                    new DefaultImageTile(mosaic_s1, createImage(colors[v][1][2][1]), 2, 1),
                    new DefaultImageTile(mosaic_s1, createImage(colors[v][1][3][1]), 3, 1),
                    new DefaultImageTile(mosaic_s1, createImage(colors[v][1][0][2]), 0, 2),
                    new DefaultImageTile(mosaic_s1, createImage(colors[v][1][1][2]), 1, 2),
                    new DefaultImageTile(mosaic_s1, createImage(colors[v][1][2][2]), 2, 2),
                    new DefaultImageTile(mosaic_s1, createImage(colors[v][1][3][2]), 3, 2)
                ));
        }

        crs = pyramid.getCoordinateReferenceSystem();

        return store;
    }

    private Envelope createEnvelope(double... coords){
        final GeneralEnvelope env = new GeneralEnvelope(crs);
        for(int i=0;i<coords.length;i+=2){
            env.setRange(i/2, coords[i], coords[i+1]);
        }
        return env;
    }

    private GeneralDirectPosition createCorner(double... values) {
        final GeneralDirectPosition corner = new GeneralDirectPosition(crs);
        for (int i = 0; i < values.length; i++) {
            corner.setOrdinate(i, values[i]);
        }
        return corner;
    }

    private static BufferedImage createImage(int color) {
        color = color % 255;
        final BufferedImage buffer = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = buffer.createGraphics();
        g.setColor(new Color(color, color, color));
        g.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());
        return buffer;
    }

    /**
     * Read the full image.
     * @throws Exception
     */
    @Test
    public void checkMetaTest() throws Exception {
        //load the coverage store
        getCoverageStore();

        //check the image size
        final GridGeometry gridGeom = ref.getGridGeometry();
        final GridExtent gridEnv = gridGeom.getExtent();
        assertEquals( 3, gridEnv.getDimension());
        assertEquals( 0, gridEnv.getLow(0));
        assertEquals(39, gridEnv.getHigh(0));
        assertEquals( 0, gridEnv.getLow(1));
        assertEquals(29, gridEnv.getHigh(1));
        assertEquals( 0, gridEnv.getLow(2));
        assertEquals( 1, gridEnv.getHigh(2));

        final Envelope env = ref.getEnvelope().orElseThrow();
        assertEquals(-180, env.getMinimum(0), DELTA);
        assertEquals(-180 +(4*10)*0.5, env.getMaximum(0), DELTA);
        assertEquals(  90, env.getMaximum(1), DELTA);
        assertEquals(  90 -(2*10)*1, env.getMinimum(1), DELTA);
        assertEquals(CORNER_V[0], env.getMinimum(2), DELTA);
        assertEquals(CORNER_V[1], env.getMaximum(2), DELTA);

    }

    /**
     * Read with no parameter, we should obtain the most accurate data
     */
    @Test
    public void readDefaultTest() throws Exception {
        getCoverageStore();

        //we expect a 3D coverage, with all slices
        final GridCoverage coverage = ref.read(null);
        final Envelope env = coverage.getGridGeometry().getEnvelope();
        assertTrue(Utilities.equalsIgnoreMetadata(crs, env.getCoordinateReferenceSystem()));
        assertEquals(CORNER_LONG,  env.getMinimum(0), DELTA);//-- -180
        assertEquals(  75,         env.getMinimum(1), DELTA);
        assertEquals(CORNER_V[0],  env.getMinimum(2), DELTA);//-- 15
        assertEquals(-160,         env.getMaximum(0), DELTA);
        assertEquals(CORNER_LAT,   env.getMaximum(1), DELTA);//-- -90
        assertEquals( 46.58,       env.getMaximum(2), DELTA);

        //expecting image from mosaic with min resolution and vertical -15
        checkCoverage(coverage, 40, 30, colors[0][1], CORNER_LONG, -160, 75, CORNER_LAT, CORNER_V[0], -14);
        //expecting image from mosaic with min resolution and vertical 46.58
        checkCoverage(coverage, 40, 30, colors[1][1], CORNER_LONG, -160, 75, CORNER_LAT, CORNER_V[1], 47.58);
    }

    /**
     * Read special scales and dimensions.
     */
    @Test
    public void readSlicesTest() throws Exception {
        getCoverageStore();

        final GridGeometry gg = ref.getGridGeometry();

        //expecting image from mosaic with min resolution and vertical -15
        Envelope env = createEnvelope(CORNER_LONG, +180,          //-- dim 0 (long)
                                              -90, CORNER_LAT,    //-- dim 1 (lat)
                                      CORNER_V[0], CORNER_V[0]); //-- dim 2 (vertical)


        GridCoverage coverage = ref.read(gg.derive().subgrid(env, 0.5, 0.5, 1).build());
        checkCoverage(coverage, 40, 30, colors[0][1], CORNER_LONG, -160,
                                                               75, CORNER_LAT,
                                                      CORNER_V[0], -14); //-- -14 = corner_v[0] - 1 unity.

        //expecting image from mosaic with max resolution and vertical -15
        env = createEnvelope(CORNER_LONG, +180,
                                     -90, CORNER_LAT,
                             CORNER_V[0], CORNER_V[0]);
        coverage = ref.read(gg.derive().subgrid(env, 1, 1, 1).build());
        checkCoverage(coverage, 20, 20, colors[0][0], CORNER_LONG, -160,
                                                               70, CORNER_LAT,
                                                      CORNER_V[0], -14);

        //expecting image from mosaic with min resolution and vertical 46.58
        env = createEnvelope(CORNER_LONG, +180,
                                     -90, CORNER_LAT,
                             CORNER_V[1], CORNER_V[1]);
        coverage = ref.read(gg.derive().subgrid(env, 0.5, 0.5, 1).build());
        checkCoverage(coverage, 40, 30, colors[1][1], CORNER_LONG, -160,
                                                               75, CORNER_LAT,
                                                      CORNER_V[1], 47.58);

        //expecting image from mosaic with max resolution and vertical 46.58
        env = createEnvelope(CORNER_LONG, +180,
                                     -90, CORNER_LAT,
                             CORNER_V[1], CORNER_V[1]);
        coverage = ref.read(gg.derive().subgrid(env, 1, 1, 1).build());
        checkCoverage(coverage, 20, 20, colors[1][0], CORNER_LONG, -160,
                                                               70, CORNER_LAT,
                                                      CORNER_V[1], 47.58);
    }

    /**
     *
     * @param coverage coverage to test
     * @param width expected image width
     * @param height expected image height
     * @param colors colors to be found in the image
     * @param envelope expented envelope
     */
    private void checkCoverage(GridCoverage coverage, int width, int height, int[][] colors, double... envelope){
        assertTrue(Utilities.equalsApproximately(crs, coverage.getCoordinateReferenceSystem()));
        final GeneralEnvelope roi = new GeneralEnvelope(coverage.getCoordinateReferenceSystem());
        roi.setEnvelope(envelope);
        final GridGeometry domain = coverage.getGridGeometry().derive().subgrid(roi).build();

        final RenderedImage img = coverage.render(domain.getExtent());
        final Raster raster = img.getData();
        assertEquals(width,  img.getWidth());
        assertEquals(height, img.getHeight());

        //we should have a different color each 10pixel
        final int[] buffer = new int[4];
        for(int x=0;x<img.getWidth()/10;x++){
            for(int y=0;y<img.getHeight()/10;y++){
                raster.getPixel(x*10+5, y*10+5, buffer);
                assertEquals(colors[x][y],buffer[0]);
            }
        }
    }
}
