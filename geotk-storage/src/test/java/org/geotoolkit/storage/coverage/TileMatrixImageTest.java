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

import java.util.Arrays;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.measure.Units;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.storage.multires.DefiningTileMatrixSet;
import org.geotoolkit.storage.multires.TileMatrices;
import org.geotoolkit.test.Assert;
import org.junit.Test;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class TileMatrixImageTest {

    /**
     * Test the no data value from sample dimensions is used in empty tiles.
     */
    @Test
    public void testNoDataOnMissingTiles() throws DataStoreException {

        final DefiningTileMatrixSet tileMatrisSet = TileMatrices.createWorldWGS84Template(2);

        final SampleDimension sampleDimension = new SampleDimension.Builder()
                .addQualitative(null, 35.7)
                .addQuantitative("data", 10, 20, Units.CELSIUS)
                .setName(0)
                .build();

        final TileMatrixImage image = TileMatrixImage.create(tileMatrisSet.getTileMatrices().values().iterator().next(), null, Arrays.asList(sampleDimension));

        Assert.assertTrue(BufferedImages.isAll(image, new double[]{35.7}));


    }

}
