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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.measure.Unit;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.IncompleteGridGeometryException;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.internal.referencing.GeodeticObjectBuilder;
import org.apache.sis.measure.Units;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.crs.DefaultEngineeringCRS;
import org.apache.sis.referencing.cs.AbstractCS;
import org.apache.sis.referencing.cs.DefaultCoordinateSystemAxis;
import org.apache.sis.referencing.datum.DefaultEngineeringDatum;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.client.CapabilitiesException;
import org.geotoolkit.coverage.grid.EstimatedGridGeometry;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.geotoolkit.temporal.object.ISODateParser;
import org.geotoolkit.temporal.object.TemporalUtilities;
import org.geotoolkit.temporal.util.PeriodUtilities;
import org.geotoolkit.wms.xml.AbstractDimension;
import org.geotoolkit.wms.xml.AbstractLayer;
import org.geotoolkit.wms.xml.AbstractWMSCapabilities;
import org.geotoolkit.wms.xml.Style;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.util.FactoryException;

/**
 * Convinient WMS methods.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public final class WMSUtilities {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.wms");

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
    public static boolean supportCRS(final WebMapClient server, final String layername, final CoordinateReferenceSystem crs)
            throws FactoryException, CapabilitiesException {
        ArgumentChecks.ensureNonNull("server", server);
        ArgumentChecks.ensureNonNull("layer name", layername);

        final AbstractLayer[] stack = server.getServiceCapabilities().getLayerStackFromName(layername);

        if(stack != null){
            final String srid = ReferencingUtilities.lookupIdentifier(crs, true);
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
     */
    public static CoordinateReferenceSystem findOriginalCRS(final WebMapClient server,
            final String layername) throws CapabilitiesException {
        ArgumentChecks.ensureNonNull("server", server);
        ArgumentChecks.ensureNonNull("layer name", layername);

        final AbstractLayer[] stack = server.getServiceCapabilities().getLayerStackFromName(layername);

        if(stack != null){
            //start by the most accurate layer
            for(int i=stack.length-1; i>=0; i--){
                for (final String srid : stack[i].getCRS()) {
                    //search and return the first crs that we succesfuly parsed.
                    try{
                        CoordinateReferenceSystem crs = CRS.forCode(srid);
                        if (crs != null) {
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
     */
    public static Long findClosestDate(final WebMapClient server, final String layername,
            final long date) throws CapabilitiesException {
        ArgumentChecks.ensureNonNull("server", server);
        ArgumentChecks.ensureNonNull("layer name", layername);

        final AbstractLayer layer = server.getServiceCapabilities().getLayerFromName(layername);

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
     * Find layer grid geometry.
     * This geometry only contains the envelope but may
     * also contain an approximated resolution.
     * Extra dimensions are included in the grid geometry.
     *
     * @param server web map server
     * @param layername wms layer name
     */
    public static GridGeometry getGridGeometry(final WebMapClient server,
            final String layername) throws CapabilitiesException {
        ArgumentChecks.ensureNonNull("server", server);
        ArgumentChecks.ensureNonNull("layer name", layername);

        final AbstractWMSCapabilities capa = server.getServiceCapabilities();

        final AbstractLayer layer = capa.getLayerFromName(layername);
        if (layer == null) {
            return null;
        }

        GridGeometry layerGrid = layer.getGridGeometry2D();

        final List<AbstractDimension> dimensions = (List<AbstractDimension>) layer.getDimension();
        if (!dimensions.isEmpty()) {
            final CoordinateReferenceSystem envCRS = layerGrid.getCoordinateReferenceSystem();
            final List<CoordinateReferenceSystem> dimensionsCRS = new ArrayList<CoordinateReferenceSystem>();
            dimensionsCRS.add(envCRS);

            final List<Double> lower = new ArrayList<>();
            final List<Double> upper = new ArrayList<>();
            GeneralEnvelope layerEnvelope = new GeneralEnvelope(layerGrid.getEnvelope());
            lower.add(layerEnvelope.getMinimum(0));
            lower.add(layerEnvelope.getMinimum(1));
            upper.add(layerEnvelope.getMaximum(0));
            upper.add(layerEnvelope.getMaximum(1));

            for (final AbstractDimension dim : dimensions) {

                CoordinateReferenceSystem dimCRS = null;
                final String dimName = dim.getName();
                final String dimUnitSymbol = dim.getUnitSymbol();
                final Unit<?> unit = dimUnitSymbol != null ? Units.valueOf(dimUnitSymbol) : Units.UNITY;

                //create CRS
                if ("time".equals(dimName)) {
                    dimCRS = CommonCRS.Temporal.JAVA.crs();
                } else if ("elevation".equals(dimName)) {
                    dimCRS = CommonCRS.Vertical.ELLIPSOIDAL.crs();
                } else {
                    final DefaultEngineeringDatum dimDatum = new DefaultEngineeringDatum(Collections.singletonMap("name", dimName));
                    final CoordinateSystemAxis csAxis = new DefaultCoordinateSystemAxis(
                            Collections.singletonMap("name", dimName), dimName.substring(0, 1), AxisDirection.OTHER, unit);
                    final AbstractCS dimCs = new AbstractCS(Collections.singletonMap("name", dimName), csAxis);
                    dimCRS = new DefaultEngineeringCRS(Collections.singletonMap("name", dimName), dimDatum, dimCs);
                }

                double minVal = Double.MIN_VALUE;
                double maxVal = Double.MAX_VALUE;

                //extract discret values
                final String dimValues = dim.getValue();
                if(dimValues != null){
                    if (!CommonCRS.Temporal.JAVA.crs().equals(dimCRS)) {
                        //serie of values
                        final String[] dimStrArray = dimValues.split(",");
                        final double[] dblValues = new double[dimStrArray.length];
                        for (int i = 0; i < dimStrArray.length; i++) {
                            dblValues[i] = Double.valueOf(dimStrArray[i]).doubleValue();
                        }
                        dimCRS = dimCRS;
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
                                    Logging.getLogger("org.geotoolkit.wms").log(Level.FINER, "Value : {0} is not a period", candidate);
                                }
                            }

                            //try to parse a single date
                            try {
                                final Date date = TemporalUtilities.parseDate(candidate);
                                dblValues.add((double)date.getTime());
                                continue;
                            } catch (ParseException ex) {
                                Logging.getLogger("org.geotoolkit.wms").log(Level.FINER, "Value : {0} is not a date", candidate);
                            }
                        }

                        final double[] values = new double[dblValues.size()];
                        for(int i=0;i<values.length;i++){
                            values[i] = dblValues.get(i);
                        }
                        dimCRS = dimCRS;
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

            // build new envelope with all dimension CRSs and lower/upper coordinates.
            if (!dimensionsCRS.isEmpty()) {

                final CoordinateReferenceSystem outCRS;
                try {
                    outCRS = new GeodeticObjectBuilder().addName(layer.getName())
                            .createCompoundCRS(dimensionsCRS.toArray(new CoordinateReferenceSystem[dimensionsCRS.size()]));
                } catch (FactoryException ex) {
                    throw new CapabilitiesException("", ex);
                }
                layerEnvelope = new GeneralEnvelope(outCRS);

                //build ordinate list like (xmin, ymin, zmin, xmax, ymax, zmax)
                final List<Double> ordinateList = new ArrayList<Double>(lower);
                ordinateList.addAll(upper);

                final double[] coordinates = new double[ordinateList.size()];
                for (int i = 0; i < ordinateList.size(); i++) {
                    coordinates[i] = ordinateList.get(i);
                }
                layerEnvelope.setEnvelope(coordinates);

                //add additional resolution informations
                try {
                    double[] resolution = layerGrid.getResolution(true);
                    double[] resnd = new double[layerEnvelope.getDimension()];
                    //todo : time and elevation values are not on a regular axis
                    //how can we map those to a correct resolution ?
                    Arrays.fill(resnd, Double.MAX_VALUE);
                    resnd[0] = resolution[0];
                    resnd[1] = resolution[1];

                    layerGrid = new EstimatedGridGeometry(layerEnvelope, resnd);

                } catch (IncompleteGridGeometryException ex) {
                    layerGrid = new GridGeometry(null, layerEnvelope);
                }
            }
        }

        return layerGrid;
    }

    /**
     * List available styles for given layer
     *
     * @param server web map server
     * @param layername wms layer name
     */
    public static List<? extends Style> findStyleCandidates(final WebMapClient server,
            final String layername) throws CapabilitiesException{
        ArgumentChecks.ensureNonNull("server", server);
        ArgumentChecks.ensureNonNull("layer name", layername);

        final AbstractLayer layer = server.getServiceCapabilities().getLayerFromName(layername);
         if(layer != null){
            return layer.getStyle();
        }
        return Collections.emptyList();
    }

}
