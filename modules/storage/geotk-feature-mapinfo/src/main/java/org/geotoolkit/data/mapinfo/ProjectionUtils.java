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
package org.geotoolkit.data.mapinfo;

import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.mapinfo.mif.MIFUtils;
import org.geotoolkit.factory.FactoryFinder;
import org.apache.sis.metadata.iso.citation.Citations;
import org.apache.sis.metadata.iso.extent.DefaultExtent;
import org.apache.sis.metadata.iso.extent.DefaultGeographicBoundingBox;
import org.apache.sis.parameter.Parameters;
import org.geotoolkit.referencing.CRS;
import org.apache.sis.referencing.IdentifiedObjects;
import org.apache.sis.referencing.cs.DefaultCoordinateSystemAxis;
import org.geotoolkit.referencing.cs.PredefinedCS;
import org.geotoolkit.referencing.operation.provider.UniversalParameters;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.*;
import org.opengis.referencing.ReferenceSystem;
import org.opengis.referencing.crs.*;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CSFactory;
import org.opengis.referencing.cs.CartesianCS;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.datum.GeodeticDatum;
import org.opengis.referencing.operation.*;
import org.opengis.util.FactoryException;

import javax.measure.unit.Unit;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.sis.geometry.DirectPosition2D;
import org.apache.sis.geometry.Envelope2D;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.logging.Logging;

import static org.geotoolkit.data.mapinfo.ProjectionParameters.PARAMETER_LIST;
import static org.geotoolkit.data.mapinfo.ProjectionParameters.getProjectionParameters;

/**
 * The object in charge of CRS conversion.
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 12/03/13
 */
public class ProjectionUtils {

    private static final String MAP_INFO_NAMESPACE = org.apache.sis.metadata.iso.citation.Citations.getIdentifier(Citations.MAP_INFO);
    private static final char NAMESPACE_SEPARATOR = ':';

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.data.mapinfo");

    private static final CRSFactory CRS_FACTORY = FactoryFinder.getCRSFactory(null);
    private static final CSFactory CS_FACTORY = FactoryFinder.getCSFactory(null);
    private static final CoordinateOperationFactory PROJ_FACTORY = FactoryFinder.getCoordinateOperationFactory(null);

    private static final String BOUNDS_NAME = "Bounds";
    private static final String AFFINE_UNITS = "Affine Units";
    private static final String EARTH_PROJ_TYPE = "earth";
    private static final int GEO_PROJ_CODE = 1;

    public static final Pattern DOUBLE_PATTERN = Pattern.compile("(-|\\+)?\\d+(\\.\\d+((?i)e(-|\\+)?\\d+)?)?");

    /**
     * Get the coefficients of a projection to put it in a String.
     * @param source The source projection to parse.
     * @return A string containing projection's parameters. Never null, but can be empty.
     * @throws FactoryException If we can't to retrieve the projection parameters.
     */
    public static String getMIFProjCoefs(Conversion source, int projCode) throws FactoryException {
        final StringBuilder builder = new StringBuilder();

        String[] paramList = getProjectionParameters(projCode);

        final ParameterValueGroup coefs = source.getParameterValues();

        // First element case
        ParameterValue first = coefs.parameter(paramList[0]);
        if (first != null && first.getValue() != null) {
            builder.append(first.getValue());
        }

        for (int i = 1; i < paramList.length; i++) {
            ParameterValue param = coefs.parameter(paramList[i]);
            if (param != null && param.getValue() != null) {
                builder.append(", ").append(param.getValue());
            }
        }
        return builder.toString();
    }


    public static String getMIFBounds(CoordinateReferenceSystem source) {
        StringBuilder builder = new StringBuilder();
        Envelope bounds = CRS.getEnvelope(source);
        if(bounds != null) {
            double minX = bounds.getLowerCorner().getOrdinate(0);
            double minY = bounds.getLowerCorner().getOrdinate(1);
            double maxX = bounds.getUpperCorner().getOrdinate(0);
            double maxY = bounds.getUpperCorner().getOrdinate(1);
            builder.append(BOUNDS_NAME).append(' ')
                    .append('(').append(minX).append(',').append(minY).append(')')
                    .append('(').append(maxX).append(',').append(maxY).append(')');
        }
        return builder.toString();
    }

    /**
     * Build a {@link CoordinateReferenceSystem} from a MIF CoordSys string.
     * @param mifCRS The String containing the MIF CRS object.
     * @return A {@link GeographicCRS} or {@link ProjectedCRS} representing given CoordSys.
     * @throws DataStoreException If the given CoordSys is non-Earth projection, or if there's a problem while parsing it.
     */
    public static CoordinateReferenceSystem buildCRSFromMIF(String mifCRS) throws DataStoreException, FactoryException {
        ArgumentChecks.ensureNonNull("CoordSys String", mifCRS);
        if(mifCRS.isEmpty()) {
            throw new DataStoreException("The given CoordSys to parse is empty.");
        }

        String boundsStr = null;
        String affineUnitsStr = null;

        Map<String, Object> crsIdentifiers = new HashMap<String, Object>();

        int projCode = -1;
        String[] paramList;

        GeodeticDatum datum = null;

        Unit unit;

        GeographicCRS baseCRS;

        CoordinateReferenceSystem result;

        // Which value we're on.
        int position  = 0;

        // Remove useless data from string.
        mifCRS = mifCRS.trim();
        final boolean removeStart =
            mifCRS.regionMatches(true, 0, MIFUtils.HeaderCategory.COORDSYS.name(), 0, MIFUtils.HeaderCategory.COORDSYS.name().length());
        if(removeStart) {
            mifCRS = mifCRS.substring(MIFUtils.HeaderCategory.COORDSYS.name().length()).trim();
        }

        Matcher earthType = Pattern.compile("^"+EARTH_PROJ_TYPE, Pattern.CASE_INSENSITIVE).matcher(mifCRS);
        if(!earthType.find()) {
            throw new DataStoreException("Only earth projections are supported.");
        }
        mifCRS = mifCRS.substring(EARTH_PROJ_TYPE.length()).trim();

        // Store bounds and affine transformation apart to re-use it later.
        Matcher boundsMatch = Pattern.compile(BOUNDS_NAME, Pattern.CASE_INSENSITIVE).matcher(mifCRS);
        if(boundsMatch.find()) {
            boundsStr = mifCRS.substring(boundsMatch.start());
            mifCRS = mifCRS.substring(0, boundsMatch.start()).trim();
        }

        Matcher affineMatch = Pattern.compile(AFFINE_UNITS, Pattern.CASE_INSENSITIVE).matcher(mifCRS);
        if(affineMatch.find()) {
            affineUnitsStr = mifCRS.substring(affineMatch.start());
            mifCRS = mifCRS.substring(0, affineMatch.start()).trim();
        }

        String[] crsParameters = mifCRS.split(",");
        if(crsParameters.length < 3) {
            throw new DataStoreException("Missing informations : A CoordSys must at least define projection type, datum and unit.");
        }

        Pattern codeMatch = Pattern.compile("\\d+");

        //find projection type
        Matcher projMatch = codeMatch.matcher(crsParameters[position++]);
        if(projMatch.find()) {
            projCode = Integer.decode(projMatch.group());
        }
        paramList = getProjectionParameters(projCode);

        // Datum
        int datumCode = -1;
        Matcher datumMatch = codeMatch.matcher(crsParameters[position++]);
        if(datumMatch.find()) {
            datumCode = Integer.decode(datumMatch.group());
            // If we've got a custom datum, check for its ellipsoid and Bursa Wolf parameters.
            if(datumCode == 999 || datumCode == 9999) {
                int dParamNumber = crsParameters.length-(position+paramList.length+1);

                double[] bursaWolfParameters = new double[dParamNumber];
                for(int i = 0 ; i < dParamNumber ; i++) {
                    Matcher match = DOUBLE_PATTERN.matcher(crsParameters[position++]);
                    if(match.find()) {
                        // For rotation parameters, we must inverse value.
                        bursaWolfParameters[i] = Double.parseDouble(match.group());
                    } else {
                        throw new DataStoreException("One of the custom datum parameters can't be read.");
                    }
                }
                datum = DatumIdentifier.buildCustomDatum(null, bursaWolfParameters);

            } else {
                datum = DatumIdentifier.getDatumFromMIFCode(datumCode);
            }
        }

        // Unit
        unit = UnitIdentifier.getUnitFromCode(crsParameters[position++]);

        if(datum == null) {
            throw new DataStoreException("One of the following mandatory parameter can't be read : datum code");
        } else if (unit == null) {
            throw new DataStoreException("One of the following mandatory parameter can't be read : unit code");
        }

        /*
         * If Bounds param is defined, we parse it to find the four numbers defining lower and upper corners. If there's
         * a problem during the parsing, we just keep going, since it's not an essential data for CRS definition.
         */
        Envelope bounds = null;
        try {
            if(boundsStr != null) {
                Matcher cornerMatch = DOUBLE_PATTERN.matcher(boundsStr);
                double[] coords = new double[4];
                int coordCounter = 0;
                while(cornerMatch.find() && coordCounter < 4) {
                    double corner = Double.parseDouble(cornerMatch.group());
                    coords[coordCounter++] = corner;
                }
                if(coordCounter >= 4) {
                    double[] minDP = new double[]{coords[0], coords[1]};
                    double[] maxDP = new double[]{coords[2], coords[3]};
                    bounds = new GeneralEnvelope(minDP, maxDP);

                    if(projCode == GEO_PROJ_CODE) {
                        final DefaultGeographicBoundingBox bbox = new DefaultGeographicBoundingBox();
                        bbox.setBounds(bounds);
                        final DefaultExtent ext = new DefaultExtent();
                        ext.setGeographicElements(Collections.singleton(bbox));
                        crsIdentifiers.put(ReferenceSystem.DOMAIN_OF_VALIDITY_KEY, ext);
                    }
                    // The case for Projected crs is managed below, after we've defined the needed conversion.
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "MIF CoordSys clause : Bounds can't be read.", e);
        }

        crsIdentifiers.put(ReferenceSystem.NAME_KEY, "MapInfoCRS");
        crsIdentifiers.put("authority", Citations.MAP_INFO);

        try {
            baseCRS = CRS_FACTORY.createGeographicCRS(crsIdentifiers, datum, PredefinedCS.GEODETIC_2D);
        } catch (FactoryException e) {
            throw new DataStoreException("A problem has been encountered while creating base geographic CRS.", e);
        }

        if(projCode == GEO_PROJ_CODE) {
            result = baseCRS;
        } else {
            /**
             * If projection code is not the geographical code, we must build a projected crs matching the projection
             * pointed by given code.
             */
            final OperationMethod method;
            try {
                method = PROJ_FACTORY.getOperationMethod(MAP_INFO_NAMESPACE+NAMESPACE_SEPARATOR+projCode);
            } catch (Exception e) {
                throw new DataStoreException("No projection can be found for code : "+projCode, e);
            }

            final ParameterDescriptorGroup projDesc = method.getParameters();
            final ParameterValueGroup projParams = projDesc.createValue();

            //Parse coefficients given in the CoordSys string. The order is REALLY important, so we browse possible
            // parameters list with an index.
            for(int i = 0 ; i < paramList.length ; i++) {
                final String paramName = paramList[i];

                final ParameterValue currentParam;
                try {
                    currentParam = projParams.parameter(paramName);
                } catch (ParameterNotFoundException e) {
                    continue;
                }

                if(currentParam != null) {
                    if(position >= crsParameters.length) {
                        // I we can't find the parameter in the string, we check if it's mandatory, to know if we keep
                        // going, or raise an error.
                        if(currentParam.getDescriptor().getMinimumOccurs() > 0) {
                            throw new DataStoreException("Needed projection parameter \'"+ paramName +"\' is missing.");
                        } else {
                            continue;
                        }
                    }

                    final String tmpStr = crsParameters[position++];
                    Matcher doubleMatch = DOUBLE_PATTERN.matcher(tmpStr);
                    if(doubleMatch.find()) {
                        currentParam.setValue(Double.parseDouble(doubleMatch.group()));
                    } else {
                        if(currentParam.getDescriptor().getMinimumOccurs() > 0) {
                            throw new DataStoreException("A problem appeared while parsing projection parameter \'"+ paramName +"\'.");
                        } else {
                            continue;
                        }
                    }
                }
            }

            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put("name", "MapInfoProjection");
            properties.put("authority", Citations.MAP_INFO);

            DefaultCoordinateSystemAxis east = new DefaultCoordinateSystemAxis(
                    Collections.singletonMap(DefaultCoordinateSystemAxis.NAME_KEY, "East"), "E", AxisDirection.EAST, unit);
            DefaultCoordinateSystemAxis north = new DefaultCoordinateSystemAxis(
                    Collections.singletonMap(DefaultCoordinateSystemAxis.NAME_KEY, "North"), "N", AxisDirection.NORTH, unit);
            CartesianCS cs = CS_FACTORY.createCartesianCS(properties, east, north);

            Conversion conversion = PROJ_FACTORY.createDefiningConversion(properties, method, projParams);
            // now that we've got conversion, we can check for bounds.
            if (bounds != null) {
                try {
                    MathTransform transform = CRS.findMathTransform(CRS_FACTORY.createProjectedCRS(crsIdentifiers, baseCRS, conversion, cs), baseCRS);
                    DirectPosition newLower = new DirectPosition2D(baseCRS);
                    DirectPosition newUpper = new DirectPosition2D(baseCRS);
                    transform.transform(bounds.getLowerCorner(), newLower);
                    transform.transform(bounds.getUpperCorner(), newUpper);
                    final DefaultExtent ext = new DefaultExtent();
                    ext.addElements(new Envelope2D(newLower, newUpper));
//                    ext.setGeographicElements(Collections.singleton(bbox));
                    crsIdentifiers.put(ReferenceSystem.DOMAIN_OF_VALIDITY_KEY, ext);
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "MIF CoordSys clause : Bounds can't be read.", e);
                }
            }

            result = CRS_FACTORY.createProjectedCRS(crsIdentifiers, baseCRS, conversion, cs);
        }

        return result;
    }

    /**
     * Parse a Geotk CRS to build a MIF representation of it.
     * @param crs The CRS we want to get in MIF syntax.
     * @return a String which is the CRS in MIF syntax.
     * @throws org.apache.sis.storage.DataStoreException if the CRS does not get any equivalent in MIF.
     */
    public static String crsToMIFSyntax(CoordinateReferenceSystem crs) throws DataStoreException, FactoryException {
        ArgumentChecks.ensureNonNull("CRS to convert", crs);
        final StringBuilder builder = new StringBuilder();
        builder.append("CoordSys Earth\n\tProjection ");

        final String mifDatum  = DatumIdentifier.getMIFDatum((GeodeticDatum) ((SingleCRS) crs).getDatum());

        //Unit
        final CoordinateSystem cs = crs.getCoordinateSystem();
        if (cs.getDimension() > 2) {
            throw new DataStoreException("MIF format can only work with 2D coordinate systems.");
        }
        final String mifUnitCode = '\"'+cs.getAxis(0).getUnit().toString()+'\"';

        // Geographic CRS (special) case, mapinfo proj code is 1.
        if(crs instanceof GeographicCRS) {
            builder.append('1').append(", ").append(mifDatum).append(", ").append(mifUnitCode);

        } else if (crs instanceof ProjectedCRS) {
            final ProjectedCRS pCRS = (ProjectedCRS) crs;
            Conversion proj = pCRS.getConversionFromBase();
            String projCode = org.geotoolkit.referencing.IdentifiedObjects.lookupIdentifier(
                    Citations.MAP_INFO, proj.getMethod(), false);
            if(projCode == null) {
                // If we get a lambert conformal 1SP, we must convert it into 2P to use it.
                if(org.geotoolkit.referencing.IdentifiedObjects.lookupEpsgCode(proj.getMethod(), true).equals(9801)) {
                    projCode = "3";
                    OperationMethod method = PROJ_FACTORY.getOperationMethod(MAP_INFO_NAMESPACE+NAMESPACE_SEPARATOR+projCode);
                    Map<String, Object> properties = new HashMap<String, Object>();
                    properties.put("name", "Lambert Conformal Conic (2SP)");
                    properties.put("authority", Citations.MAP_INFO);
                    ParameterDescriptorGroup desc = method.getParameters();
                    ParameterValueGroup values = desc.createValue();
                    Parameters.copy(proj.getParameterValues(), values);

                    final String originLat = PARAMETER_LIST.get(1);
                    final String stParallel1 = PARAMETER_LIST.get(2);
                    final String stParallel2 = PARAMETER_LIST.get(3);
                    values.parameter(stParallel1).setValue(proj.getParameterValues().parameter(originLat).getValue());
                    values.parameter(stParallel2).setValue(proj.getParameterValues().parameter(originLat).getValue());

                    proj = PROJ_FACTORY.createDefiningConversion(properties, method, values);

                } else {
                    OperationMethod method = PROJ_FACTORY.getOperationMethod(proj.getMethod().getName().getCode());
                    projCode = org.geotoolkit.referencing.IdentifiedObjects.lookupIdentifier(Citations.MAP_INFO, method, false);
                }
                if (projCode == null) {
                    throw new DataStoreException("Projection of the given CRS does not get any equivalent in mapInfo.");
                }
            }
            builder.append(projCode).append(", ").append(mifDatum).append(", ").append(mifUnitCode);

            // Retrieve needed MIF projection parameters.
            final String coefs = ProjectionUtils.getMIFProjCoefs(proj, Integer.decode(projCode));
            if(!coefs.isEmpty()) {
                builder.append(", ").append(coefs);
            }
        } else {
            throw new DataStoreException("The given CRS can't be converted to MapInfo format.");
        }

        final String bounds = ProjectionUtils.getMIFBounds(crs);
        if(!bounds.isEmpty()) {
            builder.append(' ').append(bounds);
        }
        return builder.toString();
    }

}
