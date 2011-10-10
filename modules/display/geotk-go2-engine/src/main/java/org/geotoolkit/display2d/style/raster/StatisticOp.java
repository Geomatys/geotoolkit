/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.display2d.style.raster;

import java.awt.image.RenderedImage;
import java.util.HashMap;
import java.util.Map;
import javax.media.jai.iterator.RectIter;
import javax.media.jai.iterator.RectIterFactory;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReader;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class StatisticOp{

    public static final String MINIMUM = "min";
    public static final String MAXIMUM = "max";
    
    
    private StatisticOp(){
    }

    public static Map<String,Object> analyze(GridCoverageReader reader) throws CoverageStoreException {
        return analyze( ((GridCoverage2D)reader.read(0, null)).getRenderedImage() );
    }
    
    public static Map<String,Object> analyze(RenderedImage image) {
        final Map<String,Object> analyze = new HashMap<String, Object>();
        
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;
        
        final RectIter ite = RectIterFactory.create(image, null);        
        ite.startBands();

        if (!ite.finishedBands()) do {
            
            ite.startLines();
            if (!ite.finishedLines()) do {
                
                ite.startPixels();
                if (!ite.finishedPixels()) do {
                    
                    final double sample = ite.getSampleDouble() ;
                    min = Math.min(min, sample);
                    max = Math.max(max, sample);
                    ite.nextPixel();
                    
                } while (!ite.nextPixelDone());
                ite.nextLine();
                
            } while (!ite.nextLineDone());
            ite.nextBand();
            
        } while (!ite.nextBandDone());

        analyze.put(MINIMUM, min);
        analyze.put(MAXIMUM, max);
        return analyze;
    }

}
