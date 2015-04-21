/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2014, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.metadata.geotiff;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.IIOImage;
import javax.imageio.metadata.IIOMetadataNode;
import org.apache.sis.measure.NumberRange;
import org.geotoolkit.coverage.Category;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.image.internal.ImageUtils;
import org.geotoolkit.image.internal.SampleType;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.image.io.plugin.IIOTiffMetadata;
import org.geotoolkit.image.io.plugin.TiffImageReader;
import org.geotoolkit.image.io.plugin.TiffImageWriter;
import org.geotoolkit.internal.image.io.DimensionAccessor;
import static org.geotoolkit.metadata.geotiff.GeoTiffConstants.TAG_GEOTIFF_IFD;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Test class to improve Reading / writing action with some geographicales or related metadatas fonctionalities.
 *
 * @author Remi Marechal (Geomatys).
 */
public class GeotiffTest {
    
    /**
     * Needed writer to test multiple no Data writing.
     */
    final TiffImageWriter writer = new TiffImageWriter(null);
    
    /**
     * Needed Reader to test multiple no Data reading.
     */
    final TiffImageReader reader = new TiffImageReader(null);
    
    /**
     * Close Reader / Writer.
     */
    @After
    public void close() {
        writer.dispose();
        reader.dispose();
    }
    
    /**
     * Improve Reader / Writer to write and read multiple noData values.
     * Test noData with 2 values out of sample interval boundary,
     * 2 values on sample boundary interval border, and a value which intersect interval.
     * We expect 5 saved nodata categories and to sample boundaries categories.
     * In our case : nodata : {0, 1, 128, 254, 255}, sample interval : [0 ; 255] (Byte space)
     * Expected 7 categories : [0 ; 0], [1 ; 1], ]1 ; 128[, [128 ; 128], ]128 ; 254[, [254 ; 254], [255 ; 255] in ascending order.
     * Moreover don't verify generated image pertinency, other Image reader / writer tests improve these comportements it is not the aim of it.
     * @throws java.io.IOException if problem during reading writing action.
     */
    @Test
    public void noDataTest() throws IOException {
        
        final RenderedImage testedImg    = ImageUtils.createScaledInterleavedImage(2, 2, SampleType.Byte, 3);
        final IIOMetadataNode root       = new IIOMetadataNode(TAG_GEOTIFF_IFD);
        final GeoTiffMetaDataStack stack = new GeoTiffMetaDataStack(root);
        stack.setMinSampleValue(1, 1, 1);
        stack.setMaxSampleValue(254, 254, 254);
        stack.setNoData("0.0");
        stack.setNoData("1.0");
        stack.setNoData("128.0");
        stack.setNoData("254.0");
        stack.setNoData("255.0");
        stack.flush();
        
        final File filTest = File.createTempFile("multipleNodata", "tiff");
        writer.setOutput(filTest);
        final IIOImage img = new IIOImage(testedImg, null, new IIOTiffMetadata(root));
        writer.write(img);
        writer.dispose();
        
        reader.setInput(filTest);
        SpatialMetadata sm = reader.getImageMetadata(0);
        reader.dispose();
        
        final DimensionAccessor dimAccess    = new DimensionAccessor(sm);
        final List<GridSampleDimension> sDim = dimAccess.getGridSampleDimensions();
        assertEquals("gridSampleDimension number", 3, sDim.size());
        
        final GridSampleDimension gsd   = sDim.get(0);
        final List<Category> categories = gsd.getCategories();
        
        assertEquals("categories number", 7, categories.size());
        
        //-- expected number range array
        //-- [0 ; 0], [1 ; 1], ]1 ; 128[, [128 ; 128], ]128 ; 254[, [254 ; 254], [255 ; 255]
        //-- in category specification all getted value are inclusive
        //-- expected array become : 
        final double[] expectedArray = new double[]{0,   0, //-- 7 categories
                                                    1,   1, 
                                                    2,   127, 
                                                    128, 128, 
                                                    129, 253, 
                                                    254, 254, 
                                                    255, 255}; 
        
        final double[] foundArray = new double[14];
        int fAId = 0;
        for (final Category cat : categories) {
            final NumberRange r = cat.getRange();
            foundArray[fAId++]  = r.getMinDouble(true);
            foundArray[fAId++]  = r.getMaxDouble(true);
        }
        assertArrayEquals(expectedArray, foundArray, 1E-12);
    }
}
