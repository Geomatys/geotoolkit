/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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
import java.awt.image.RenderedImage;
import java.io.IOException;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverageBuilder;
import org.apache.sis.coverage.grid.GridCoverageProcessor;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridOrientation;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.image.Interpolation;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.storage.memory.InMemoryGridCoverageResource;
import org.geotoolkit.storage.memory.InMemoryTiledGridCoverageResource;
import org.geotoolkit.storage.multires.TileMatrices;
import org.geotoolkit.storage.multires.TileMatrix;
import org.geotoolkit.storage.multires.TileMatrixSet;
import org.geotoolkit.storage.multires.WritableTileMatrixSet;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class CoverageTileGeneratorTest {

    /**
     * Test tile generation produces the expected results.
     */
    @Test
    public void fullAreaGenerateTest() throws DataStoreException, InterruptedException, IOException, TransformException {

        final BufferedImage image = new BufferedImage(180, 90, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = image.createGraphics();
        g.setColor(Color.RED);
        g.fillRect(0, 0, 180, 90);
        g.dispose();

        final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();
        final GeneralEnvelope dataEnvelope = new GeneralEnvelope(crs);
        dataEnvelope.setRange(0, 0, 180);
        dataEnvelope.setRange(1, 0, 90);
        final GridGeometry gridGeom = new GridGeometry(new GridExtent(180, 90), dataEnvelope, GridOrientation.HOMOTHETY);

        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setValues(image);
        gcb.setDomain(gridGeom);
        final GridCoverage coverage = gcb.build();

        final CoverageTileGenerator generator = new CoverageTileGenerator(new InMemoryGridCoverageResource(coverage));
        generator.setCoverageIsHomogeneous(false);


        final InMemoryTiledGridCoverageResource ipr = new InMemoryTiledGridCoverageResource(Names.createLocalName(null, null, "test"));
        final WritableTileMatrixSet tileMatrixSet = ipr.createTileMatrixSet(TileMatrices.createWorldWGS84Template(4));

        generator.generate(tileMatrixSet, null, null, null);

        compare(generator, tileMatrixSet, null);
    }

    /**
     * Test tile generation produces the expected results on a reduced area.
     */
    @Test
    public void partialAreaGenerateTest() throws DataStoreException, InterruptedException, IOException, TransformException {

        final BufferedImage image = new BufferedImage(360, 180, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = image.createGraphics();
        g.setColor(Color.RED);
        g.fillRect(0, 0, 360, 180);
        g.dispose();

        final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();
        final GeneralEnvelope dataEnvelope = new GeneralEnvelope(crs);
        dataEnvelope.setRange(0, -180, 180);
        dataEnvelope.setRange(1, -90, 90);
        final GridGeometry gridGeom = new GridGeometry(new GridExtent(360, 180), dataEnvelope, GridOrientation.HOMOTHETY);

        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setValues(image);
        gcb.setDomain(gridGeom);
        final GridCoverage coverage = gcb.build();

        final CoverageTileGenerator generator = new CoverageTileGenerator(new InMemoryGridCoverageResource(coverage));
        generator.setCoverageIsHomogeneous(false);

        final GeneralEnvelope generateEnvelope = new GeneralEnvelope(crs);
        generateEnvelope.setRange(0, 0, 45);
        generateEnvelope.setRange(1, 0, 30);

        final InMemoryTiledGridCoverageResource ipr = new InMemoryTiledGridCoverageResource(Names.createLocalName(null, null, "test"));
        final WritableTileMatrixSet tileMatrixSet = ipr.createTileMatrixSet(TileMatrices.createWorldWGS84Template(4));

        generator.generate(tileMatrixSet, generateEnvelope, null, null);

        compare(generator, tileMatrixSet, generateEnvelope);
    }

    /**
     * Test tile generation produces the expected results on a reduced area with optimisation
     * enable because coverage is Homogeneous.
     */
    @Test
    public void partialAreaHomogeneusGenerateTest() throws DataStoreException, InterruptedException, IOException, TransformException {

        final BufferedImage image = new BufferedImage(360, 180, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = image.createGraphics();
        g.setColor(Color.RED);
        g.fillRect(0, 0, 360, 180);
        g.dispose();

        final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();
        final GeneralEnvelope dataEnvelope = new GeneralEnvelope(crs);
        dataEnvelope.setRange(0, -180, 180);
        dataEnvelope.setRange(1, -90, 90);
        final GridGeometry gridGeom = new GridGeometry(new GridExtent(360, 180), dataEnvelope, GridOrientation.HOMOTHETY);

        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setValues(image);
        gcb.setDomain(gridGeom);
        final GridCoverage coverage = gcb.build();

        final CoverageTileGenerator generator = new CoverageTileGenerator(new InMemoryGridCoverageResource(coverage));
        generator.setCoverageIsHomogeneous(true);

        final GeneralEnvelope generateEnvelope = new GeneralEnvelope(crs);
        generateEnvelope.setRange(0, 0, 45);
        generateEnvelope.setRange(1, 0, 30);

        final InMemoryTiledGridCoverageResource ipr = new InMemoryTiledGridCoverageResource(Names.createLocalName(null, null, "test"));
        final WritableTileMatrixSet tileMatrixSet = ipr.createTileMatrixSet(TileMatrices.createWorldWGS84Template(1));

        generator.generate(tileMatrixSet, generateEnvelope, null, null);

        compare(generator, tileMatrixSet, generateEnvelope);
    }

    private void compare(CoverageTileGenerator generator, TileMatrixSet tileMatrixSet, GeneralEnvelope generateEnvelope) throws DataStoreException, IOException, TransformException {

        final GridCoverageResource origin = generator.getOrigin();
        final GeneralEnvelope dataEnvelope = new GeneralEnvelope(origin.getGridGeometry().getEnvelope());

        for (TileMatrix tileMatrix : tileMatrixSet.getTileMatrices().values()) {
            final Dimension gridSize = TileMatrices.getGridSize(tileMatrix);
            for (int y = 0; y < gridSize.height; y++) {
                for (int x = 0; x < gridSize.width; x++) {
                    final ImageTile tile = (ImageTile) tileMatrix.getTile(x, y).orElse(null);
                    final long[] tileCoord = new long[]{x, y};
                    final GridGeometry tileGridGeom = TileMatrices.getTileGridGeometry2D(tileMatrix, tileCoord);
                    final Envelope tileEnv = tileGridGeom.getEnvelope();
                    if (generateEnvelope == null || generateEnvelope.intersects(tileEnv)) {
                        final RenderedImage tileImage = tile.getImage();
                        if (dataEnvelope.contains(tileEnv)) {
                            //all red
                            Assert.assertTrue(BufferedImages.isAll(tileImage, new double[]{255.0, 0.0, 0.0, 255.0}));
                        } else if (dataEnvelope.intersects(tileEnv, false)) {
                            //partial image
                            final GridCoverageProcessor gcp = new GridCoverageProcessor();
                            gcp.setInterpolation(Interpolation.NEAREST);
                            final RenderedImage resampled = gcp.resample(origin.read(null), tileGridGeom).render(null);
                            Assert.assertTrue(BufferedImages.isPixelsIdenticals(resampled, tileImage));
                        } else {
                            //transparent
                            Assert.assertTrue(BufferedImages.isAll(tileImage, new double[]{0.0, 0.0, 0.0, 0.0}));
                        }
                    } else {
                        //must be missing
                        Assert.assertNull(tile);
                    }

                }
            }
        }
    }
}
