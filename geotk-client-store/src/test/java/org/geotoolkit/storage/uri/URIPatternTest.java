/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2021, Geomatys
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
package org.geotoolkit.storage.uri;

import java.awt.Dimension;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.sis.geometry.DirectPosition2D;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.storage.multires.DefiningTileMatrix;
import org.geotoolkit.util.NamesExt;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class URIPatternTest {

    @Test
    public void testTileMatrixPattern() throws IOException {

        final DefiningTileMatrix mosaic = new DefiningTileMatrix(NamesExt.createRandomUUID(), new DirectPosition2D(CommonCRS.WGS84.normalizedGeographic()), 1, new Dimension(10, 10), new Dimension(64, 32));
        final Path base = Files.createTempDirectory("pattern").resolve("tm");
        Files.createDirectories(base);
        base.toFile().deleteOnExit();

        //most simple case
        String tilePath = format(URIPattern.resolve(base, mosaic, "{x}/{y}.png", 0, 0));
        Assert.assertTrue(tilePath.endsWith("/tm/0/0.png"));
        //check tolerance to starting slash
        tilePath = format(URIPattern.resolve(base, mosaic, "/{x}/{y}.png", 0, 0));
        Assert.assertTrue(tilePath.endsWith("/tm/0/0.png"));
        //check windows notation
        tilePath = format(URIPattern.resolve(base, mosaic, "\\{x}\\{y}.png", 0, 0));
        Assert.assertTrue(tilePath.endsWith("/tm/0/0.png"));
        //check intricate path
        tilePath = format(URIPattern.resolve(base, mosaic, "/test/{y}/sub/val{x}/img.tiff", 4, 9));
        Assert.assertTrue(tilePath.endsWith("/tm/test/9/sub/val4/img.tiff"));
        //check reverse
        tilePath = format(URIPattern.resolve(base, mosaic, "{reversex}/{reversey}.png", 0, 0));
        Assert.assertTrue(tilePath.endsWith("/tm/63/31.png"));
        tilePath = format(URIPattern.resolve(base, mosaic, "{reversey}/{x}.png", 7, 18));
        Assert.assertTrue(tilePath.endsWith("/tm/13/7.png"));
    }

    private static String format(Path path) {
        String str = path.toUri().toString();
        return str.replace('\\', '/');
    }

}
