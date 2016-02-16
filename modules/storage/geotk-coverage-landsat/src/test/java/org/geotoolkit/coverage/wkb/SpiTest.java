/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.coverage.wkb;

import java.util.Arrays;
import javax.imageio.ImageIO;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class SpiTest extends org.geotoolkit.test.TestBase {

    @Test
    public void readerSpiTest(){
        final String[] names = ImageIO.getReaderFormatNames();
        assertTrue(Arrays.asList(names).contains("PostGISWKBraster"));
    }

    @Test
    public void writerSpiTest(){
        final String[] names = ImageIO.getWriterFormatNames();
        assertTrue(Arrays.asList(names).contains("PostGISWKBraster"));
    }

}
