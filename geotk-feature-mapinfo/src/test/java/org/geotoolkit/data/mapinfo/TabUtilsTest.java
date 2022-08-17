/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2022, Geomatys
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
package org.geotoolkit.data.mapinfo;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.operation.transform.LinearTransform;
import org.apache.sis.storage.DataStoreException;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.Matrix;
import org.opengis.util.FactoryException;


/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class TabUtilsTest {

    /**
     * Test raster tab reading.
     */
    @Test
    public void testReadTab() throws URISyntaxException, IOException, DataStoreException, FactoryException {

        final Path path = Paths.get(TabUtilsTest.class.getResource("raster.tab").toURI());
        final GridGeometry grid = TabUtils.parseGridGeometry(path);

        //check CRS, should be close to EPSG:2154 but still to much difference for a comparaison test.
        final CoordinateReferenceSystem rgf93 = CRS.forCode("EPSG:2154");
        final CoordinateReferenceSystem crs = grid.getCoordinateReferenceSystem();
        Assert.assertEquals("MapInfo:MapInfoCRS", crs.getName().toString());

        MathTransform gridToCRS = grid.getGridToCRS(PixelInCell.CELL_CENTER);
        Assert.assertTrue(gridToCRS instanceof LinearTransform);
        LinearTransform lt = (LinearTransform) gridToCRS;
        Matrix matrix = lt.getMatrix();

        Assert.assertEquals(0.5, matrix.getElement(0, 0), 0.0);
        Assert.assertEquals(0.0, matrix.getElement(0, 1), 0.0);
        Assert.assertEquals(1160000.0, matrix.getElement(0, 2), 0.0);
        Assert.assertEquals(0.0, matrix.getElement(1, 0), 0.0);
        Assert.assertEquals(-0.5, matrix.getElement(1, 1), 0.0);
        Assert.assertEquals(6115000.0, matrix.getElement(1, 2), 0.0);
    }

}
