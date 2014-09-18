/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wps;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.lang.Setup;
import org.geotoolkit.util.FileUtilities;
import org.geotoolkit.wps.converters.inputs.complex.ComplexToCoverageConverterTest;
import org.geotoolkit.wps.converters.inputs.complex.ComplexToRenderedImageConvereterTest;
import org.geotoolkit.wps.converters.inputs.literal.StringToDoubleArrayConverterTest;
import org.geotoolkit.wps.converters.inputs.literal.StringToFloatArrayConverterTest;
import org.geotoolkit.wps.converters.inputs.literal.StringToIntegerArrayConverterTest;
import org.geotoolkit.wps.converters.inputs.reference.ReferenceToGridCoverage2DConverterTest;
import org.geotoolkit.wps.converters.inputs.reference.ReferenceToRenderedImageConverterTest;
import org.geotoolkit.wps.converters.outputs.complex.CoverageToComplexConverterTest;
import org.geotoolkit.wps.converters.outputs.complex.RenderedImageToComplexConverterTest;
import org.geotoolkit.wps.converters.outputs.literal.DoubleArrayToStringConverterTest;
import org.geotoolkit.wps.converters.outputs.literal.FloatArrayToStringConverterTest;
import org.geotoolkit.wps.converters.outputs.literal.IntegeArrayToStringConverterTest;
import org.geotoolkit.wps.io.WPSIOTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 * @author Quentin Boileau (Geoamtys).
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        WPSIOTest.class,
        ComplexToCoverageConverterTest.class,
        ComplexToRenderedImageConvereterTest.class,
        ReferenceToGridCoverage2DConverterTest.class,
        ReferenceToRenderedImageConverterTest.class,
        CoverageToComplexConverterTest.class,
        RenderedImageToComplexConverterTest.class,
        
        StringToDoubleArrayConverterTest.class,
        StringToFloatArrayConverterTest.class,
        StringToIntegerArrayConverterTest.class,
        DoubleArrayToStringConverterTest.class,
        FloatArrayToStringConverterTest.class,
        IntegeArrayToStringConverterTest.class
        
})

public class WPSConverterTestSuit {
    
    private static File tmpDataDir;
    private static boolean init;
    
    
    public static void initImageIO() {
        if (!init) {
            Hints.putSystemDefault(Hints.LENIENT_DATUM_SHIFT, Boolean.TRUE);
            ImageIO.scanForPlugins();
            Setup.initialize(null);
            init = true;
        }
    }
    
    public static void releaseImageIO() {
        if (init) {
            Setup.shutdown();
        }
    }
    
    @BeforeClass
    public static void init() {
        initImageIO();
        tmpDataDir = new File("tmpData");
        tmpDataDir.mkdir();
    }
    
    @AfterClass
    public static void release() throws IOException {
        FileUtilities.deleteDirectory(tmpDataDir.toPath());
        releaseImageIO();
    }
    
    public static String getTempDirPath() {
        return tmpDataDir.getAbsolutePath();
    }
    
    public static boolean isImageIOInitialized() {
        return init;
    }
}
