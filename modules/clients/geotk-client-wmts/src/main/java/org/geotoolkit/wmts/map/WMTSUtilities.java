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
package org.geotoolkit.wmts.map;

import java.util.List;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.wmts.WebMapTileServer;
import org.geotoolkit.wmts.scale.GlobalCRS84Scale;
import org.geotoolkit.wmts.xml.v100.Capabilities;
import org.geotoolkit.wmts.xml.v100.LayerType;
import org.geotoolkit.wmts.xml.v100.TileMatrix;
import org.geotoolkit.wmts.xml.v100.TileMatrixSet;
import org.geotoolkit.wmts.xml.v100.TileMatrixSetLink;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CoordinateSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class WMTSUtilities {

    /**
     * Pixel size used in WMTS specification
     */
    public static final double STANDARD_PIXEL_SIZE = 0.00028d; 
    
    private WMTSUtilities() {
    }
    
    /**
     * Search the layer in the server capabilities.
     * @param server : server to search in
     * @param name : name to search
     * @return LayerType or null if not found.
     */
    public static LayerType getLayer(final WebMapTileServer server, final String name){
        ArgumentChecks.ensureNonNull("server", server);
        ArgumentChecks.ensureNonNull("name", name);
        
        final Capabilities capa = server.getCapabilities();
        if(capa == null){
            return null;
        }
        
        final List<LayerType> layers = capa.getContents().getLayers();
        if(layers == null){
            return null;
        }
        
        for(final LayerType layer : layers){
            final String id = layer.getIdentifier().getValue();
            if(name.equalsIgnoreCase(id)){
                return layer;
            }
        }
        
        //layer not found
        return null;
    }
    
    /**
     * Find the most appropriate tileset available for the given layer and
     * wanted output crs.
     */
    public static TileMatrixSetLink getOptimalTileSet(final WebMapTileServer server, 
            final LayerType layer, final CoordinateReferenceSystem crs){
        
        //TODO
        return null;
    }
    
    public static double unitsByPixel(final TileMatrixSet set, final CoordinateReferenceSystem setCrs, final TileMatrix matrix){
        
        //predefined scales
        if("GlobalCRS84Scale".equalsIgnoreCase(set.getIdentifier().getValue())){
            return GlobalCRS84Scale.getPixelScale(matrix.getScaleDenominator());
        }
        
        // Specification default calculation method :
        // pixelSpan = scaleDenominator × 0.28 10-3 / metersPerUnit(crs);
        final double meterByUnit =  metersPerUnit(setCrs);
        final double candidateUnitByPixel = matrix.getScaleDenominator() * WMTSUtilities.STANDARD_PIXEL_SIZE / meterByUnit;
        return candidateUnitByPixel;
    }
    
    public static double metersPerUnit(final CoordinateReferenceSystem crs){
        final CoordinateSystem cs = crs.getCoordinateSystem();        
        final int dim = cs.getDimension();
        final Unit axi0Unit = cs.getAxis(0).getUnit();
        
        if(!SI.METRE.isCompatible(axi0Unit)){
            
            if(axi0Unit == NonSI.DEGREE_ANGLE){
                return 40075016d / 360d ;
            }else{
                throw new IllegalArgumentException("Unsupported unit : "+ axi0Unit);
            }
            
        }
        
        return axi0Unit.getConverterTo(SI.METRE).convert(1);
    }
    
    
    
}
