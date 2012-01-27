/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.wmts;

import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import org.geotoolkit.wmts.xml.v100.TileMatrix;
import org.geotoolkit.wmts.xml.v100.TileMatrixSet;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CoordinateSystem;

/**
 * Utility methods for WMTS strange scale calculation
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class WMTSUtilities {
    
    /**
     * Pixel size used in WMTS specification
     */
    public static final double STANDARD_PIXEL_SIZE = 0.00028d; 
    /**
     * Earth perimeter at equator
     */
    public static final double EARTH_PERIMETER = 40075016d;
    
    /**
     * Used for WMTS predefined scale : GlobalCRS84Scale
     */
    public static final double SCALE = 500e6;
    public static final double PIXEL_SIZE = 1.25764139776733;
    
    private WMTSUtilities(){}
    
    /**
     * 
     * @param set
     * @param setCrs
     * @param matrix
     * @return 
     */
    public static double unitsByPixel(final TileMatrixSet set, final CoordinateReferenceSystem setCrs, final TileMatrix matrix){
        
        //predefined scales
        if("GlobalCRS84Scale".equalsIgnoreCase(set.getIdentifier().getValue())){
            return getGlobalCRS84PixelScale(matrix.getScaleDenominator());
        }
        
        // Specification default calculation method :
        // pixelSpan = scaleDenominator * 0.28 10-3 / metersPerUnit(crs)
        final double meterByUnit =  metersPerUnit(setCrs);
        final double candidateUnitByPixel = matrix.getScaleDenominator() * STANDARD_PIXEL_SIZE / meterByUnit;
        return candidateUnitByPixel;
    }
    
    /**
     * 
     * @param crs CoordinateReferenceSystem
     * @param pixelSpan size of a pixel in crs unit
     * @return WMTS scale 
     */
    public static double toScaleDenominator(final CoordinateReferenceSystem crs, final double pixelSpan){
        
        // Specification default calculation method : (Reversed)
        // scaleDenominator = (pixelSpan * metersPerUnit(crs)) / 0.28 10-3 
        final double meterByUnit =  metersPerUnit(crs);
        final double scaleDenom = (pixelSpan * meterByUnit) / STANDARD_PIXEL_SIZE;
        return scaleDenom;
    }
    
    /**
     * 
     * @param crs CoordinateReferenceSystem
     * @return size in meter of one unit along CRS first axis
     */
    public static double metersPerUnit(final CoordinateReferenceSystem crs){
        final CoordinateSystem cs = crs.getCoordinateSystem();
        final Unit axi0Unit = cs.getAxis(0).getUnit();
        
        //in case axis in not in a meter compatible unit
        if(!SI.METRE.isCompatible(axi0Unit)){
            if(axi0Unit == NonSI.DEGREE_ANGLE){
                //axis is in degree, likely a geographic crs
                return EARTH_PERIMETER / 360d ;
            }else{
                throw new IllegalArgumentException("Unsupported unit : "+ axi0Unit);
            }
        }
        
        //convert 1 unit of the crs unit in meter
        return axi0Unit.getConverterTo(SI.METRE).convert(1);
    }
            
    /**
     * 
     * @param scaleDenominator
     * @return 
     */
    public static double getGlobalCRS84PixelScale(double scaleDenominator) {
        return (PIXEL_SIZE * 1.118164528) * scaleDenominator / SCALE ;
    }
    
    
}
