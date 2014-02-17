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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;
import java.util.List;
import java.util.SortedSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.measure.unit.Unit;
import org.geotoolkit.client.CapabilitiesException;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.IdentifiedObjects;
import org.geotoolkit.referencing.crs.AbstractSingleCRS;
import org.geotoolkit.referencing.crs.DefaultCompoundCRS;
import org.geotoolkit.referencing.crs.DefaultTemporalCRS;
import org.geotoolkit.referencing.crs.DefaultVerticalCRS;
import org.apache.sis.referencing.cs.AbstractCS;
import org.geotoolkit.referencing.cs.DefaultCoordinateSystemAxis;
import org.geotoolkit.referencing.cs.DiscreteReferencingFactory;
import org.apache.sis.referencing.datum.AbstractDatum;
import org.geotoolkit.temporal.object.ISODateParser;
import org.geotoolkit.temporal.object.TemporalUtilities;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.util.PeriodUtilities;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.wms.xml.AbstractDimension;
import org.geotoolkit.wms.xml.AbstractLayer;
import org.geotoolkit.wms.xml.AbstractWMSCapabilities;
import org.geotoolkit.wms.xml.Style;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.util.FactoryException;

/**
 * Convinient WMS methods.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class WMSUtilities {

    private static final Logger LOGGER = Logging.getLogger(WMSUtilities.class);

    private static final SimpleDateFormat PERIOD_DATE_FORMAT = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    static {
        PERIOD_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT+0"));
    }

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
            if(srid == null){
                //current crs has no knowned id, we can ask the server for this crs
                return false;
            }

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
                    + "(check case-sensitivity) or a non-compliant wms serveur."
                    + "WMS server:" + server.getURL());
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
            final String layername) throws CapabilitiesException {
        ArgumentChecks.ensureNonNull("server", server);
        ArgumentChecks.ensureNonNull("layer name", layername);

        final AbstractWMSCapabilities capa = server.getCapabilities();

        final AbstractLayer layer = capa.getLayerFromName(layername);
        if (layer == null) {
            return null;
        }

        GeneralEnvelope layerEnvelope = (GeneralEnvelope) layer.getEnvelope();

        final List<AbstractDimension> dimensions = (List<AbstractDimension>) layer.getDimension();
        if (!dimensions.isEmpty()) {
            final CoordinateReferenceSystem envCRS = layerEnvelope.getCoordinateReferenceSystem();
            final List<CoordinateReferenceSystem> dimensionsCRS = new ArrayList<CoordinateReferenceSystem>();
            dimensionsCRS.add(envCRS);

            final List<Double> lower = new ArrayList<Double>();
            final List<Double> upper = new ArrayList<Double>();
            lower.add(layerEnvelope.getLower(0));
            lower.add(layerEnvelope.getLower(1));
            upper.add(layerEnvelope.getUpper(0));
            upper.add(layerEnvelope.getUpper(1));

            for (final AbstractDimension dim : dimensions) {

                CoordinateReferenceSystem dimCRS = null;
                final String dimName = dim.getName();
                final String dimUnitSymbol = dim.getUnitSymbol();
                final Unit<?> unit = dimUnitSymbol != null ? Unit.valueOf(dimUnitSymbol) : Unit.ONE;

                //create CRS
                if ("time".equals(dimName)) {
                    dimCRS = DefaultTemporalCRS.JAVA;
                } else if ("elevation".equals(dimName)) {
                    dimCRS = DefaultVerticalCRS.ELLIPSOIDAL_HEIGHT;
                } else {
                    final AbstractDatum dimDatum = new AbstractDatum(Collections.singletonMap("name", dimName));
                    final CoordinateSystemAxis csAxis = new DefaultCoordinateSystemAxis(dimName, dimName.substring(0, 1), AxisDirection.OTHER, unit);
                    final AbstractCS dimCs = new AbstractCS(Collections.singletonMap("name", dimName), csAxis);
                    dimCRS = new AbstractSingleCRS(Collections.singletonMap("name", dimName), dimDatum, dimCs);
                }

                double minVal = Double.MIN_VALUE;
                double maxVal = Double.MAX_VALUE;

                //extract discret values
                final String dimValues = dim.getValue();
                if(dimValues != null){
                    if (!DefaultTemporalCRS.JAVA.equals(dimCRS)) {
                        //serie of values
                        final String[] dimStrArray = dimValues.split(",");
                        final double[] dblValues = new double[dimStrArray.length];
                        for (int i = 0; i < dimStrArray.length; i++) {
                            dblValues[i] = Double.valueOf(dimStrArray[i]).doubleValue();
                        }
                        dimCRS = DiscreteReferencingFactory.createDiscreteCRS(dimCRS, dblValues);
                        minVal = dblValues[0];
                        maxVal = dblValues[dblValues.length - 1];
                    }else{
                        //might be serie of dates or periods
                        final String[] dimStrArray = dimValues.split(",");
                        final List<Double> dblValues = new ArrayList<Double>();
                        for (int i = 0; i < dimStrArray.length; i++) {
                            final String candidate = dimStrArray[i];
                            //try to parse a period
                            synchronized(PERIOD_DATE_FORMAT){
                                final PeriodUtilities parser = new PeriodUtilities(PERIOD_DATE_FORMAT);
                                try {
                                    final SortedSet<Date> dates = parser.getDatesFromPeriodDescription(candidate);
                                    for(Date date : dates){
                                        dblValues.add((double)date.getTime());
                                    }
                                    continue;
                                } catch (ParseException ex) {
                                    Logger.getLogger(WMSUtilities.class.getName()).log(Level.FINER, "Value : {0} is not a period", candidate);
                                }
                            }

                            //try to parse a single date
                            try {
                                final Date date = TemporalUtilities.parseDate(candidate);
                                dblValues.add((double)date.getTime());
                                continue;
                            } catch (ParseException ex) {
                                Logger.getLogger(WMSUtilities.class.getName()).log(Level.FINER, "Value : {0} is not a date", candidate);
                            }
                        }

                        final double[] values = new double[dblValues.size()];
                        for(int i=0;i<values.length;i++){
                            values[i] = dblValues.get(i);
                        }
                        dimCRS = DiscreteReferencingFactory.createDiscreteCRS(dimCRS, values);
                        if(values.length>0){
                            minVal = values[0];
                            maxVal = values[values.length - 1];
                        }
                    }
                }


                //add CRS to list
                if (dimCRS != null) {
                    dimensionsCRS.add(dimCRS);
                    lower.add(minVal);
                    upper.add(maxVal);
                }
            }

            // build new envelope with all dimension CRSs and lower/upper ordinates.
            if (!dimensionsCRS.isEmpty()) {
                final CoordinateReferenceSystem outCRS = new DefaultCompoundCRS(layer.getName(), dimensionsCRS.toArray(new CoordinateReferenceSystem[dimensionsCRS.size()]));
                layerEnvelope = new GeneralEnvelope(outCRS);

                //build ordinate list like (xmin, ymin, zmin, xmax, ymax, zmax)
                final List<Double> ordinateList = new ArrayList<Double>(lower);
                ordinateList.addAll(upper);

                final double[] ordinates = new double[ordinateList.size()];
                for (int i = 0; i < ordinateList.size(); i++) {
                    ordinates[i] = ordinateList.get(i);
                }
                layerEnvelope.setEnvelope(ordinates);
            }
        }

        return layerEnvelope;
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
