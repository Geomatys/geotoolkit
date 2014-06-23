/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2014, Geomatys
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
package org.geotoolkit.process.coverage.copy;

import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.CoverageReader;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;

/**
 *
 * @author Remi Marechal (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @author Quentin Boileau (Geomatys)
 * @module pending
 */
public class StatisticOp{

    public static final String MINIMUM = "min";
    public static final String MAXIMUM = "max";
    
    
    private StatisticOp(){
    }

    /**
     * Analyse image to return min and max value per bands.
     * @param reader
     * @param imageIndex 
     * @return A Map with two Entry. 
     * Each Entry have a name ("min", "max") and values are an double[] for each bands.
     * @throws CoverageStoreException 
     */
    public static Map<String,Object> analyze(CoverageReader reader, int imageIndex) throws CoverageStoreException {
        return analyze( ((GridCoverage2D)reader.read(imageIndex, null)).getRenderedImage() );
    }
    
    /**
     * Analyse image to return min and max value per bands.
     * @param image
     * @return A Map with two Entry. 
     * Each Entry have a name ("min", "max") and values are an double[] for each bands.
     */
    public static Map<String,Object> analyze(RenderedImage image) {
        final Map<String,Object> analyze = new HashMap<String, Object>();

        final SampleModel sm = image.getSampleModel();
        final int nbBands = sm.getNumBands();
        double[] min = new double[nbBands];
        double[] max = new double[nbBands];
        Arrays.fill(min, Double.MAX_VALUE);
        Arrays.fill(max, Double.MIN_VALUE);
        
        int b = 0;        
        final PixelIterator pix = PixelIteratorFactory.createDefaultIterator(image);
        while (pix.next()) {
            final double d = pix.getSampleDouble();
            if (!Double.isNaN(d)) {
                min[b] = Math.min(min[b], d);
                max[b] = Math.max(max[b], d);
            }
            if (++b == nbBands) b = 0; 
        }
        analyze.put(MINIMUM, min);
        analyze.put(MAXIMUM, max);
        return analyze;
    }

}
