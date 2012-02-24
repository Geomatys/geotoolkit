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
package org.geotoolkit.wms;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.client.CapabilitiesException;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.IdentifiedObjects;
import org.geotoolkit.temporal.object.ISODateParser;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.wms.xml.AbstractDimension;
import org.geotoolkit.wms.xml.AbstractLayer;
import org.geotoolkit.wms.xml.AbstractWMSCapabilities;
import org.geotoolkit.wms.xml.Style;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 * Convinient WMS methods.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class WMSUtilities {
    
    private static final Logger LOGGER = Logging.getLogger(WMSUtilities.class);

    private WMSUtilities() {}
    
    /**
     * Verify if the server supports the given {@linkplain CoordinateReferenceSystem crs}.
     *
     * @param server web map server
     * @param layername wms layer name
     * @param crs The {@linkplain CoordinateReferenceSystem crs} to test.
     * @return {@code True} if the given {@linkplain CoordinateReferenceSystem crs} is present
     *         in the list of supported crs in the GetCapabilities response. {@code False} otherwise.
     * @throws FactoryException
     * @throws CapabilitiesException if failed to read capabilities or layer name does not exist in document 
     */
    public static boolean supportCRS(final WebMapServer server, final String layername, final CoordinateReferenceSystem crs)
            throws FactoryException, CapabilitiesException {
        ArgumentChecks.ensureNonNull("server", server);
        ArgumentChecks.ensureNonNull("layer name", layername);
        
        final AbstractLayer[] stack = server.getCapabilities().getLayerStackFromName(layername);

        if(stack != null){
            final String srid = IdentifiedObjects.lookupIdentifier(crs, true);
            //start by the most accurate layer
            for(int i=stack.length-1; i>=0; i--){
                for (String str : stack[i].getCRS()) {
                    if (srid.equalsIgnoreCase(str)) {
                        return true;
                    }
                }
            }
        }else{
            throw new CapabilitiesException(
                    "Layer : "+layername+" could not be found in the getCapabilities. "
                    + "This can be caused by an incorrect layer name "
                    + "(check case-sensitivity) or a non-compliant wms serveur.");
        }

        return false;
    }

    /**
     * Find the best original crs of the data in the capabilities.
     * 
     * @param server web map server
     * @param layername wms layer name
     * @return
     * @throws FactoryException
     * @throws CapabilitiesException  
     */
    public static CoordinateReferenceSystem findOriginalCRS(final WebMapServer server, 
            final String layername) throws CapabilitiesException {
        ArgumentChecks.ensureNonNull("server", server);
        ArgumentChecks.ensureNonNull("layer name", layername);
        
        final AbstractLayer[] stack = server.getCapabilities().getLayerStackFromName(layername);

        if(stack != null){
            //start by the most accurate layer
            for(int i=stack.length-1; i>=0; i--){
                for (final String srid : stack[i].getCRS()) {
                    //search and return the first crs that we succesfuly parsed.
                    try{
                        CoordinateReferenceSystem crs = CRS.decode(srid);
                        if(crs != null){
                            return crs;
                        }
                    }catch(FactoryException ex){
                        LOGGER.log(Level.FINE, "Could not parse crs code : {0}", srid);
                    }
                }
            }
        }else{
            throw new CapabilitiesException(
                    "Layer : "+layername+" could not be found in the getCapabilities. "
                    + "This can be caused by an incorrect layer name "
                    + "(check case-sensitivity) or a non-compliant wms serveur.");
        }

        return null;
    }

    /**
     * Search in the getCapabilities the closest date.
     * 
     * @param server web map server
     * @param layername wms layer name
     * @return  
     */
    public static Long findClosestDate(final WebMapServer server, final String layername,
            final long date) throws CapabilitiesException {
        ArgumentChecks.ensureNonNull("server", server);
        ArgumentChecks.ensureNonNull("layer name", layername);
        
        final AbstractLayer layer = server.getCapabilities().getLayerFromName(layername);

        if(layer != null){
            for(AbstractDimension dim : layer.getAbstractDimension()){
                if("time".equalsIgnoreCase(dim.getName())){
                    //we found the temporal dimension
                    final ISODateParser parser = new ISODateParser();
                    final String[] dates = dim.getValue().split(",");

                    final long d = date;
                    Long closest = null;
                    for(String str : dates){
                        str = str.replaceAll("\n", "");
                        str = str.trim();
                        long candidate = parser.parseToMillis(str);
                        if(closest == null){
                            closest = candidate;
                        }else if( Math.abs(d-candidate) < Math.abs(d-closest)){
                            closest = candidate;
                        }
                    }

                    return closest;
                }
            }
        }else{
            throw new CapabilitiesException(
                    "Layer : "+layername+" could not be found in the getCapabilities. "
                    + "This can be caused by an incorrect layer name "
                    + "(check case-sensitivity) or a non-compliant wms serveur.");
        }

        return null;
    }

    /**
     * Find layer envelope
     * 
     * @param server web map server
     * @param layername wms layer name
     * @return
     * @throws CapabilitiesException 
     */
    public static Envelope findEnvelope(final WebMapServer server, 
            final String layername) throws CapabilitiesException{
        ArgumentChecks.ensureNonNull("server", server);
        ArgumentChecks.ensureNonNull("layer name", layername);
        
        final AbstractWMSCapabilities capa = server.getCapabilities();
        
        final AbstractLayer layer = capa.getLayerFromName(layername);
        if(layer == null) return null;
        
        return layer.getEnvelope();
    }

    /**
     * List available styles for given layer
     * 
     * @param server web map server
     * @param layername wms layer name
     * @return
     * @throws CapabilitiesException 
     */
    public static List<? extends Style> findStyleCandidates(final WebMapServer server, 
            final String layername) throws CapabilitiesException{
        ArgumentChecks.ensureNonNull("server", server);
        ArgumentChecks.ensureNonNull("layer name", layername);
        
        final AbstractLayer layer = server.getCapabilities().getLayerFromName(layername);
         if(layer != null){
            return layer.getStyle();
        }
        return Collections.emptyList();
    }
    
}
