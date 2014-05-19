/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

package org.geotoolkit.sos.netcdf;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.measure.Longitude;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.gml.xml.FeatureProperty;
import org.geotoolkit.gml.xml.LineString;
import org.geotoolkit.gml.xml.Point;
import org.geotoolkit.observation.xml.OMXmlFactory;
import org.geotoolkit.sos.netcdf.ExtractionResult.ProcedureTree;
import static org.geotoolkit.sos.netcdf.FeatureType.*;
import static org.geotoolkit.sos.netcdf.NetCDFUtils.*;
import org.geotoolkit.sos.xml.SOSXmlFactory;
import org.geotoolkit.swe.xml.AbstractDataRecord;
import org.geotoolkit.swe.xml.DataArrayProperty;
import org.geotoolkit.swe.xml.Phenomenon;
import static org.geotoolkit.swe.xml.v200.TextEncodingType.DEFAULT_ENCODING;
import org.opengis.geometry.DirectPosition;
import org.opengis.observation.sampling.SamplingFeature;
import ucar.ma2.Array;
import ucar.ma2.ArrayChar;
import ucar.ma2.ArrayInt;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class NetCDFExtractor {
    
    private static final Logger LOGGER = Logging.getLogger(NetCDFExtractor.class);
    
    private static final DateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    
    public static ExtractionResult getObservationFromNetCDF(final File netCDFFile, final String procedureID) throws NetCDFParsingException {
        final NCFieldAnalyze analyze = analyzeResult(netCDFFile, null);
        switch (analyze.featureType) {
            case TIMESERIES :
            return parseDataBlockTS(analyze, procedureID, null);
            case PROFILE :
            return parseDataBlockXY(analyze, procedureID, null);
            case TRAJECTORY :
            return parseDataBlockTraj(analyze, procedureID, null);
            case GRID :
            return parseDataBlockGrid(analyze, procedureID, null);
            default : return null;
        }
    }
    
    public static ExtractionResult getObservationFromNetCDF(final NCFieldAnalyze analyze, final String procedureID, final List<String> acceptedProcedureIDs) throws NetCDFParsingException {
        switch (analyze.featureType) {
            case TIMESERIES :
            return parseDataBlockTS(analyze, procedureID, acceptedProcedureIDs);
            case PROFILE :
            return parseDataBlockXY(analyze, procedureID, acceptedProcedureIDs);
            case TRAJECTORY :
            return parseDataBlockTraj(analyze, procedureID, acceptedProcedureIDs);
            case GRID :
            return parseDataBlockGrid(analyze, procedureID, acceptedProcedureIDs);
            default : return null;
        }
    }
    
    public static ExtractionResult getObservationFromNetCDF(final File netCDFFile, final String procedureID, final String selectedBand) throws NetCDFParsingException {
        final NCFieldAnalyze analyze = analyzeResult(netCDFFile, selectedBand);
        if (analyze.phenfields.isEmpty()) {
            LOGGER.info("There is no variable to collect in this file");
            return new ExtractionResult();
        }
        switch (analyze.featureType) {
            case TIMESERIES :
            return parseDataBlockTS(analyze, procedureID, null);
            case PROFILE :
            return parseDataBlockXY(analyze, procedureID, null);
            case TRAJECTORY :
            return parseDataBlockTraj(analyze, procedureID, null);
            case GRID :
            return parseDataBlockGrid(analyze, procedureID, null);
            default : return new ExtractionResult();
        }
    }
    
    public static NCFieldAnalyze analyzeResult(final File netCDFFile, final String selectedBand) {
        final NCFieldAnalyze analyze = new NCFieldAnalyze();
        try {
            
            final NetcdfFile file = NetcdfFile.open(netCDFFile.getPath());
            analyze.file = file;

            final Attribute ftAtt = file.findGlobalAttribute("featureType");
            if (ftAtt != null) {
                final String value = ftAtt.getStringValue();
                if ("timeSeries".equalsIgnoreCase(value)) {
                    analyze.featureType = TIMESERIES;
                } else if ("profile".equalsIgnoreCase(value)) {
                    analyze.featureType = PROFILE;
                } else if ("trajectory".equalsIgnoreCase(value)) {
                    analyze.featureType = TRAJECTORY;
                } else {
                    analyze.featureType = GRID;
                }
            } else {
                analyze.featureType = GRID;
            }

            final Attribute titAtt = file.findGlobalAttribute("title");
            if (titAtt != null) {
                final String value = titAtt.getStringValue();
                analyze.title = value;
            }

            final List<Field> all = new ArrayList<>();

            for (Variable variable : file.getVariables()) {
                final String name = variable.getFullName();
                if (name != null) {
                    final int dimension = variable.getDimensions().size();
                    final String dimensionLabel = getDimensionString(variable);
                    final Field currentField = new Field(name, dimension, dimensionLabel);
                    all.add(currentField);
                    analyze.vars.put(name, variable);
                    
                    // try to get units
                    final Attribute att = variable.findAttribute("units");
                    if (att != null) {
                        currentField.unit = att.getStringValue();
                    }
                    
                    if (name.equalsIgnoreCase("Time")) {
                        currentField.type = Type.DATE;
                        if (analyze.featureType == TIMESERIES || analyze.featureType == TRAJECTORY || analyze.featureType == GRID) {
                            analyze.mainField = currentField;
                        } else {
                            analyze.skippedFields.add(currentField);
                        }
                        analyze.timeField = currentField;

                    } else if (name.equalsIgnoreCase("Latitude") || name.equalsIgnoreCase("lat")) {
                        currentField.type = Type.DOUBLE;
                        analyze.skippedFields.add(currentField);
                        analyze.latField = currentField;
                    
                    } else if (name.equalsIgnoreCase("Longitude") || name.equalsIgnoreCase("long") || name.equalsIgnoreCase("lon")) {
                        currentField.type = Type.DOUBLE;
                        analyze.skippedFields.add(currentField);
                        analyze.lonField = currentField;

                    
                    } else if (name.equalsIgnoreCase("pression") || name.equalsIgnoreCase("pres") || name.equalsIgnoreCase("depth") || name.equalsIgnoreCase("zLevel") || name.equalsIgnoreCase("z")) {
                        currentField.type = Type.DOUBLE;
                        if (analyze.featureType == PROFILE) {
                            analyze.mainField = currentField;
                        } else if (dimension > 1 && (selectedBand == null || name.equals(selectedBand))) {
                            analyze.phenfields.add(currentField);
                        } else {
                            analyze.skippedFields.add(currentField);
                        }

                    } else if (name.equalsIgnoreCase("timeserie") || name.equalsIgnoreCase("trajectory") || name.equalsIgnoreCase("profile")) {
                        currentField.type = Type.STRING;
                        analyze.separatorField = currentField;
                        
                    } else  {
                        currentField.type = getTypeFromDataType(variable.getDataType());
                        if ((currentField.type == Type.DOUBLE || currentField.type == Type.INT) && dimension != 0 && (selectedBand == null || name.equals(selectedBand))) {
                            analyze.phenfields.add(currentField);
                        } else {
                            analyze.skippedFields.add(currentField);
                        }
                    }
                }
            }
            
            // another round to try to find a main field
            if (analyze.mainField == null) {
                for (Field field : all) {
                    // try to find a time field
                    if ((analyze.featureType == TIMESERIES || analyze.featureType == TRAJECTORY || analyze.featureType == GRID) && field.label.toLowerCase().contains("time")) {
                        analyze.mainField = field;
                        analyze.phenfields.remove(field);
                        break;
                    }
                }
            }
            
            // post analyze
            if (analyze.mainField != null) {
                for (Field f : all) {
                    final String mainDimension = analyze.mainField.dimensionLabel;
                    // dimension order
                    if (!f.dimensionLabel.startsWith(mainDimension)) {
                        f.mainVariableFirst = false;
                    }

                    // exclude phenomenon field not related to main
                    if (analyze.phenfields.contains(f)) {
                        if (!f.dimensionLabel.contains(mainDimension)) {
                            analyze.phenfields.remove(f);
                            analyze.skippedFields.add(f);
                        }
                    }
                }
                
                //look for invisible separator
                if (analyze.featureType != TRAJECTORY && analyze.separatorField == null) {
                    for (Field phenField : analyze.phenfields) {
                        if (phenField.dimension > 1) {
                            final String separatorDim = phenField.dimensionLabel.replace(analyze.mainField.label, "").trim();
                            final Dimension dim = file.findDimension(separatorDim);
                            if (dim != null) {
                                analyze.dimensionSeparator = separatorDim;
                            }
                        }
                    }
                } 
                
                if (analyze.featureType == TRAJECTORY && analyze.separatorField == null) {
                    for (Field phenField : analyze.phenfields) {
                        if (phenField.dimension > 1) {
                            final Dimension dim = file.findDimension("trajectory");
                            if (dim != null) {
                                final String separatorDim = "trajectory";
                                analyze.dimensionSeparator = separatorDim;
                            }
                        }
                    }
                } else if (analyze.featureType == TIMESERIES && analyze.separatorField == null && analyze.dimensionSeparator == null) {
                    for (Field phenField : analyze.phenfields) {
                        if (phenField.dimension > 1) {
                            final Dimension dim = file.findDimension("timeseries");
                            if (dim != null) {
                                final String separatorDim = "timeseries";
                                analyze.dimensionSeparator = separatorDim;
                            }
                        }
                    }
                }
                
                if (analyze.dimensionSeparator != null) {
                    for (Field f : all) {
                        // dimension order
                        if (f.dimensionLabel.startsWith(analyze.dimensionSeparator)) {
                            f.mainVariableFirst = false;
                        }
                    }
                }
            }


        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return analyze;
    }
    
    public static boolean isObservationFile(final String nfilePath) {
        try {
            final NetcdfFile file = NetcdfFile.open(nfilePath);
            final Attribute ftAtt = file.findGlobalAttribute("featureType");
            if (ftAtt != null) {
                final String value = ftAtt.getStringValue();
                if ("timeSeries".equalsIgnoreCase(value) ||
                    "profile".equalsIgnoreCase(value)   ||
                    "trajectory".equalsIgnoreCase(value)) {
                    return true;
                }
            }
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "IO exception while opening netCDF file", ex);
        }
        return false;
    }
    
    private static ExtractionResult parseDataBlockTS(final NCFieldAnalyze analyze, final String procedureID, final List<String> acceptedSensorID) throws NetCDFParsingException {
        final ExtractionResult results = new ExtractionResult();
        if (analyze.mainField == null) {
            LOGGER.warning("No main field found");
            return results;
        }
        LOGGER.info("parsing netCDF TS");

        try {

            final List<String> separators = parseSeparatorValues(analyze.file, analyze);
            final boolean single          = separators.isEmpty();
            
            Array latArray  = null;
            Array lonArray  = null;
            if (analyze.hasSpatial()) {
                Variable latVar = analyze.vars.get(analyze.latField.label);
                Variable lonVar = analyze.vars.get(analyze.lonField.label);
                latArray        = analyze.file.readArrays(Arrays.asList(latVar)).get(0);
                lonArray        = analyze.file.readArrays(Arrays.asList(lonVar)).get(0);
            }
            
            final Variable timeVar  = analyze.vars.get(analyze.mainField.label);
            final Array timeArray   = analyze.file.readArrays(Arrays.asList(timeVar)).get(0);
            final boolean constantT = analyze.mainField.dimension == 1;
            final boolean timeFirst = analyze.mainField.mainVariableFirst;
            
            
            final Map<String, Array> phenArrays = new HashMap<>();
            for (Field field : analyze.phenfields) {
                final Variable phenVar = analyze.vars.get(field.label);
                final Array phenArray  = analyze.file.readArrays(Arrays.asList(phenVar)).get(0);
                phenArrays.put(field.label, phenArray);
                results.fields.add(field.label);
            }
            
            final AbstractDataRecord datarecord = OMUtils.getDataRecordTimeSeries("2.0.0", analyze.phenfields);
            final Phenomenon phenomenon         = OMUtils.getPhenomenon("2.0.0", analyze.phenfields);
            results.phenomenons.add(phenomenon);

            if (single) {
                if (acceptedSensorID == null || acceptedSensorID.contains(procedureID)) {
                    final ProcedureTree compo = new ProcedureTree(procedureID, "Component");
                    results.procedures.add(compo);

                    final StringBuilder sb   = new StringBuilder();
                    final int count          = timeVar.getDimension(0).getLength();
                    final GeoSpatialBound gb = new GeoSpatialBound();
                    final String identifier  = UUID.randomUUID().toString();
                    //read geometry (assume point)
                    FeatureProperty foi = null;
                    if (analyze.hasSpatial()) {
                        final double latitude         = getDoubleValue(latArray);
                        final double longitude        = Longitude.normalize(getDoubleValue(lonArray));
                        final DirectPosition position = SOSXmlFactory.buildDirectPosition("2.0.0", null, 2, Arrays.asList(latitude, longitude));
                        final Point geom              = SOSXmlFactory.buildPoint("2.0.0", "SamplingPoint", position);
                        final SamplingFeature sp      = SOSXmlFactory.buildSamplingPoint("2.0.0", identifier, null, null, null, geom);
                        foi                           = SOSXmlFactory.buildFeatureProperty("2.0.0", sp);
                        gb.addXCoordinate(longitude);
                        gb.addYCoordinate(latitude);
                        gb.addGeometry(geom);
                    }

                    // iterating over time
                    for (int i = 0; i < count; i++) {

                        long millis = getTimeValue(timeArray, i);

                        if (millis == 0 || millis == ((Integer.MIN_VALUE * -1) + 1)) {
                            continue;
                        }
                        final Date d = new Date(millis * 1000);
                        gb.addDate(d);

                        synchronized(FORMATTER) {
                            sb.append(FORMATTER.format(d)).append(DEFAULT_ENCODING.getTokenSeparator());
                        }
                        for (Field field : analyze.phenfields) {
                            final Array phenArray = phenArrays.get(field.label);
                            final Double value    = getDoubleValue(phenArray, i);

                            //empty string for missing value
                            if (!Double.isNaN(value)) {
                                sb.append(value);
                            }
                            sb.append(DEFAULT_ENCODING.getTokenSeparator());
                        }
                        // remove the last token separator
                        sb.deleteCharAt(sb.length() - 1);
                        sb.append(DEFAULT_ENCODING.getBlockSeparator());
                    }
                    final DataArrayProperty result = SOSXmlFactory.buildDataArrayProperty("2.0.0", "array-1", count, "SimpleDataArray", datarecord, DEFAULT_ENCODING, sb.toString());
                    results.observations.add(OMXmlFactory.buildObservation("2.0.0",             // version
                                                  identifier,                      // id
                                                  identifier,                      // name
                                                  null,                            // description
                                                  foi,                             // foi
                                                  phenomenon,                      // phenomenon
                                                  procedureID,                     // procedure
                                                  result,                          // result
                                                  gb.getTimeObject("2.0.0")));     // time
                    results.spatialBound.merge(gb);
                    compo.spatialBound.merge(gb);
                }
                
            } else {
                final ProcedureTree system = new ProcedureTree(procedureID, "System");
                results.procedures.add(system);
                for (int j = 0; j < separators.size(); j++) {
                    
                    final String identifier    = separators.get(j);
                    final StringBuilder sb     = new StringBuilder();
                    final int count            = getGoodTimeDimension(timeVar, analyze.dimensionSeparator).getLength();
                    final GeoSpatialBound gb   = new GeoSpatialBound();
                    final String currentProcID = procedureID + '-' + identifier;
                    final ProcedureTree compo  = new ProcedureTree(currentProcID, "Component");
                    
                    if (acceptedSensorID == null || acceptedSensorID.contains(currentProcID)) {
                    
                        //read geometry (assume point)
                        FeatureProperty foi = null;
                        if (analyze.hasSpatial()) {
                            final double latitude         = getDoubleValue(latArray, j);
                            final double longitude        = Longitude.normalize(getDoubleValue(lonArray, j));
                            final DirectPosition position = SOSXmlFactory.buildDirectPosition("2.0.0", null, 2, Arrays.asList(latitude, longitude));
                            final Point geom              = SOSXmlFactory.buildPoint("2.0.0", "SamplingPoint", position);
                            final SamplingFeature sp      = SOSXmlFactory.buildSamplingPoint("2.0.0", identifier, null, null, null, geom);
                            foi                           = SOSXmlFactory.buildFeatureProperty("2.0.0", sp);
                            gb.addXCoordinate(longitude);
                            gb.addYCoordinate(latitude);
                            gb.addGeometry(geom);
                        }

                        for (int i = 0; i < count; i++) {

                            long millis = getTimeValue(timeFirst, constantT, timeArray, i, j);

                            if (millis == 0 || millis == ((Integer.MIN_VALUE * -1) + 1)) {
                                continue;
                            }
                            final Date d = new Date(millis * 1000);
                            gb.addDate(d);

                            synchronized(FORMATTER) {
                                sb.append(FORMATTER.format(d)).append(DEFAULT_ENCODING.getTokenSeparator());
                            }
                            for (Field field : analyze.phenfields) {
                                final Array phenArray   = phenArrays.get(field.label);
                                final boolean mainFirst = field.mainVariableFirst;
                                final Double value      = getDoubleValue(mainFirst, phenArray, i, j);

                                //empty string for missing value
                                if (!Double.isNaN(value)) {
                                    sb.append(value);
                                }
                                sb.append(DEFAULT_ENCODING.getTokenSeparator());
                            }
                            // remove the last token separator
                            sb.deleteCharAt(sb.length() - 1);
                            sb.append(DEFAULT_ENCODING.getBlockSeparator());
                        }
                        final DataArrayProperty result = SOSXmlFactory.buildDataArrayProperty("2.0.0", "array-1", count, "SimpleDataArray", datarecord, DEFAULT_ENCODING, sb.toString());

                        compo.spatialBound.merge(gb);
                        system.children.add(compo);

                        final String obsid = UUID.randomUUID().toString();
                        results.observations.add(OMXmlFactory.buildObservation("2.0.0",           // version
                                                      obsid,                         // id
                                                      obsid,                         // name
                                                      null,                          // description
                                                      foi,                           // foi
                                                      phenomenon,                    // phenomenon
                                                      currentProcID,            // procedure
                                                      result,                        // result
                                                      gb.getTimeObject("2.0.0")));   // time
                        results.spatialBound.merge(gb);
                    }
                }
            }

        } catch (IOException | IllegalArgumentException ex) {
            throw new NetCDFParsingException("error while parsing netcdf timeserie", ex);
        }

        LOGGER.info("datablock parsed");
        return results;
    }
    
    private static ExtractionResult parseDataBlockXY(final NCFieldAnalyze analyze, final String procedureID, final List<String> acceptedSensorID) throws NetCDFParsingException {
        final ExtractionResult results = new ExtractionResult();
        if (analyze.mainField == null) {
            LOGGER.warning("No main field found");
            return results;
        }
        LOGGER.info("parsing datablock XY");

        try {
            final List<String> separators = parseSeparatorValues(analyze.file, analyze);
            final boolean single          = separators.isEmpty();

            Array latArray  = null;
            Array lonArray  = null;
            if (analyze.hasSpatial()) {
                Variable latVar = analyze.vars.get(analyze.latField.label);
                Variable lonVar = analyze.vars.get(analyze.lonField.label);
                latArray        = analyze.file.readArrays(Arrays.asList(latVar)).get(0);
                lonArray        = analyze.file.readArrays(Arrays.asList(lonVar)).get(0);
            }
            
            Array timeArray  = null;
            if (analyze.hasTime()) {
                Variable timeVar = analyze.vars.get(analyze.timeField.label);
                timeArray        = analyze.file.readArrays(Arrays.asList(timeVar)).get(0);
            }
            
            final Variable zVar     = analyze.vars.get(analyze.mainField.label);
            final Array zArray      = analyze.file.readArrays(Arrays.asList(zVar)).get(0);
            final boolean constantZ = analyze.mainField.dimension == 1;
            final boolean Zfirst    = analyze.mainField.mainVariableFirst;

            final Map<String, Array> phenArrays = new HashMap<>();
            for (Field field : analyze.phenfields) {
                final Variable phenVar = analyze.vars.get(field.label);
                final Array phenArray  = analyze.file.readArrays(Arrays.asList(phenVar)).get(0);
                phenArrays.put(field.label, phenArray);
                results.fields.add(field.label);
            }
            
            final AbstractDataRecord datarecord = OMUtils.getDataRecordProfile("2.0.0", analyze.phenfields);
            final Phenomenon phenomenon         = OMUtils.getPhenomenon("2.0.0", analyze.phenfields);
            results.phenomenons.add(phenomenon);
            
            if (single) {
                final ProcedureTree compo = new ProcedureTree(procedureID, "Component");
                if (acceptedSensorID == null || acceptedSensorID.contains(procedureID)) {
                    results.procedures.add(compo);

                    final StringBuilder sb   = new StringBuilder();
                    final int count          = zVar.getDimension(0).getLength();
                    final GeoSpatialBound gb = new GeoSpatialBound();
                    final String identifier  = UUID.randomUUID().toString();

                    //read geometry (assume point)
                    FeatureProperty foi = null;
                    if (analyze.hasSpatial()) {
                        final double latitude         = getDoubleValue(latArray, 0);
                        final double longitude        = Longitude.normalize(getDoubleValue(lonArray, 0));
                        final DirectPosition position = SOSXmlFactory.buildDirectPosition("2.0.0", null, 2, Arrays.asList(latitude, longitude));
                        final Point geom              = SOSXmlFactory.buildPoint("2.0.0", "SamplingPoint", position);
                        final SamplingFeature sp      = SOSXmlFactory.buildSamplingPoint("2.0.0", identifier, null, null, null, geom);
                        foi                           = SOSXmlFactory.buildFeatureProperty("2.0.0", sp);
                        gb.addXCoordinate(longitude);
                        gb.addYCoordinate(latitude);
                        gb.addGeometry(geom);
                    }
                    if (analyze.hasTime()) {
                        long millis = getTimeValue(timeArray, 0);

                        if (millis != 0 && millis != ((Integer.MIN_VALUE * -1) + 1)) {
                            final Date d = new Date(millis * 1000);
                            gb.addDate(d);
                        }
                    }

                    for (int zIndex = 0; zIndex < zVar.getDimension(0).getLength(); zIndex++) {

                        double zLevel = getDoubleValue(zArray, zIndex);
                        sb.append(zLevel).append(DEFAULT_ENCODING.getTokenSeparator());
                        if (zLevel == 0 || zLevel == FILL_VALUE) {
                            continue;
                        }
                        sb.append(zLevel).append(DEFAULT_ENCODING.getTokenSeparator());

                        for (Field field : analyze.phenfields) {
                            final Array phenArray = phenArrays.get(field.label);
                            final double value    = getDoubleValue(phenArray, zIndex);

                            //empty string for missing value
                            if (!Double.isNaN(value)) {
                                sb.append(value);
                            }
                            sb.append(DEFAULT_ENCODING.getTokenSeparator());
                        }
                        // remove the last token separator
                        sb.deleteCharAt(sb.length() - 1);
                        sb.append(DEFAULT_ENCODING.getBlockSeparator());
                    }
                    final DataArrayProperty result = SOSXmlFactory.buildDataArrayProperty("2.0.0", "array-1", count, "SimpleDataArray", datarecord, DEFAULT_ENCODING, sb.toString());
                    results.observations.add(OMXmlFactory.buildObservation("2.0.0",             // version
                                                  identifier,                      // id
                                                  identifier,                      // name
                                                  null,                            // description
                                                  foi,                             // foi
                                                  phenomenon,                      // phenomenon
                                                  procedureID,                     // procedure
                                                  result,                          // result
                                                  gb.getTimeObject("2.0.0")));     // time
                    results.spatialBound.merge(gb);
                    compo.spatialBound.merge(gb);
                }
                
            } else {
                final ProcedureTree system = new ProcedureTree(procedureID, "System");
                results.procedures.add(system);
                
                for (int profileIndex = 0; profileIndex < separators.size(); profileIndex++) {
                    
                    final String identifier    = separators.get(profileIndex);
                    final int count            = zVar.getDimension(0).getLength();
                    final GeoSpatialBound gb   = new GeoSpatialBound();
                    final String currentProcID = procedureID + '-' + identifier;
                    final ProcedureTree compo  = new ProcedureTree(currentProcID, "Component");
                        
                    if (acceptedSensorID == null || acceptedSensorID.contains(currentProcID)) {
                        //read geometry (assume point)
                        FeatureProperty foi = null;
                        if (analyze.hasSpatial()) {
                            final double latitude         = getDoubleValue(latArray, 0);
                            final double longitude        = Longitude.normalize(getDoubleValue(lonArray, 0));
                            final DirectPosition position = SOSXmlFactory.buildDirectPosition("2.0.0", null, 2, Arrays.asList(latitude, longitude));
                            final Point geom              = SOSXmlFactory.buildPoint("2.0.0", "SamplingPoint", position);
                            final SamplingFeature sp      = SOSXmlFactory.buildSamplingPoint("2.0.0", identifier, null, null, null, geom);
                            foi                           = SOSXmlFactory.buildFeatureProperty("2.0.0", sp);
                            gb.addXCoordinate(longitude);
                            gb.addYCoordinate(latitude);
                            gb.addGeometry(geom);
                        }
                        if (analyze.hasTime()) {
                            long millis = getTimeValue(timeArray, 0);

                            if (millis != 0 && millis != ((Integer.MIN_VALUE * -1) + 1)) {
                                final Date d = new Date(millis * 1000);
                                gb.addDate(d);
                            }
                        }

                        final StringBuilder sb = new StringBuilder();
                        for (int zIndex = 0; zIndex < zVar.getDimension(0).getLength(); zIndex++) {

                            double zLevel = getZValue(Zfirst, constantZ, zArray, zIndex, profileIndex);
                            if (zLevel == 0 || zLevel == FILL_VALUE) {
                                continue;
                            }
                            sb.append(zLevel).append(DEFAULT_ENCODING.getTokenSeparator());

                            for (Field field : analyze.phenfields) {
                                final Array phenArray   = phenArrays.get(field.label);
                                final boolean mainFirst = field.mainVariableFirst;
                                final double value      = getDoubleValue(mainFirst, phenArray, zIndex, profileIndex);

                                //empty string for missing value
                                if (!Double.isNaN(value)) {
                                    sb.append(value);
                                }
                                sb.append(DEFAULT_ENCODING.getTokenSeparator());
                            }
                            // remove the last token separator
                            sb.deleteCharAt(sb.length() - 1);
                            sb.append(DEFAULT_ENCODING.getBlockSeparator());
                        }
                        final DataArrayProperty result = SOSXmlFactory.buildDataArrayProperty("2.0.0", "array-1", count, "SimpleDataArray", datarecord, DEFAULT_ENCODING, sb.toString());

                        compo.spatialBound.merge(gb);
                        system.children.add(compo);

                        final String obsid = UUID.randomUUID().toString();
                        results.observations.add(OMXmlFactory.buildObservation("2.0.0",           // version
                                                      obsid,                         // id
                                                      obsid,                         // name
                                                      null,                          // description
                                                      foi,                           // foi
                                                      phenomenon,                    // phenomenon
                                                      currentProcID,            // procedure
                                                      result,                        // result
                                                      gb.getTimeObject("2.0.0")));   // time
                        results.spatialBound.merge(gb);
                    }
                }
            }

        } catch (IOException | IllegalArgumentException ex) {
            throw new NetCDFParsingException("error while parsing netcdf profile", ex);
        }

        return results;
    }
    
    private static ExtractionResult parseDataBlockTraj(final NCFieldAnalyze analyze, final String procedureID, final List<String> acceptedSensorID) throws NetCDFParsingException {
        final ExtractionResult results = new ExtractionResult();
        if (analyze.mainField == null) {
            LOGGER.warning("No main field found");
            return results;
        }
        LOGGER.info("parsing netCDF Traj");

        try {

            final List<String> separators = parseSeparatorValues(analyze.file, analyze);
            final boolean single          = separators.isEmpty();
            
            Array latArray  = null;
            Array lonArray  = null;
            if (analyze.hasSpatial()) {
                Variable latVar = analyze.vars.get(analyze.latField.label);
                Variable lonVar = analyze.vars.get(analyze.lonField.label);
                latArray        = analyze.file.readArrays(Arrays.asList(latVar)).get(0);
                lonArray        = analyze.file.readArrays(Arrays.asList(lonVar)).get(0);
            }
            
            final Variable timeVar  = analyze.vars.get(analyze.mainField.label);
            final Array timeArray   = analyze.file.readArrays(Arrays.asList(timeVar)).get(0);
            final boolean constantT = analyze.mainField.dimension == 1;
            final boolean timeFirst = analyze.mainField.mainVariableFirst;
            
            
            final Map<String, Array> phenArrays = new HashMap<>();
            for (Field field : analyze.phenfields) {
                final Variable phenVar = analyze.vars.get(field.label);
                final Array phenArray  = analyze.file.readArrays(Arrays.asList(phenVar)).get(0);
                phenArrays.put(field.label, phenArray);
                results.fields.add(field.label);
            }
            
            final AbstractDataRecord datarecord = OMUtils.getDataRecordTrajectory("2.0.0", analyze.phenfields);
            final Phenomenon phenomenon         = OMUtils.getPhenomenon("2.0.0", analyze.phenfields);
            results.phenomenons.add(phenomenon);

            if (single) {
                final ProcedureTree compo = new ProcedureTree(procedureID, "Component");
                if (acceptedSensorID == null || acceptedSensorID.contains(procedureID)) {
                    results.procedures.add(compo);

                    final StringBuilder sb   = new StringBuilder();
                    final int count          = timeVar.getDimension(0).getLength();
                    final GeoSpatialBound gb = new GeoSpatialBound();
                    final String identifier  = UUID.randomUUID().toString();

                    final List<DirectPosition> positions = new ArrayList<>();
                    DirectPosition previousPosition = null;

                    // iterating over time
                    for (int i = 0; i < count; i++) {

                        long millis = getTimeValue(timeArray, i);

                        if (millis == 0 || millis == ((Integer.MIN_VALUE * -1) + 1)) {
                            continue;
                        }
                        final Date d = new Date(millis * 1000);
                        gb.addDate(d);
                        synchronized(FORMATTER) {
                            sb.append(FORMATTER.format(d)).append(DEFAULT_ENCODING.getTokenSeparator());
                        }

                        final double latitude         = getDoubleValue(latArray, i);
                        sb.append(latitude).append(DEFAULT_ENCODING.getTokenSeparator());
                        final double longitude        = Longitude.normalize(getDoubleValue(lonArray, i));
                        sb.append(longitude).append(DEFAULT_ENCODING.getTokenSeparator());
                        final DirectPosition position = SOSXmlFactory.buildDirectPosition("2.0.0", null, 2, Arrays.asList(latitude, longitude));
                        if (!position.equals(previousPosition)) {
                            positions.add(position);
                        }
                        previousPosition = position;
                        gb.addXCoordinate(longitude);
                        gb.addYCoordinate(latitude);

                        for (Field field : analyze.phenfields) {
                            final Array phenArray = phenArrays.get(field.label);
                            final Double value    = getDoubleValue(phenArray, i);

                            //empty string for missing value
                            if (!Double.isNaN(value)) {
                                sb.append(value);
                            }
                            sb.append(DEFAULT_ENCODING.getTokenSeparator());
                        }
                        // remove the last token separator
                        sb.deleteCharAt(sb.length() - 1);
                        sb.append(DEFAULT_ENCODING.getBlockSeparator());
                    }

                    final LineString geom         = SOSXmlFactory.buildLineString("2.0.0", null, "EPSG:4326", positions);
                    final SamplingFeature sp      = SOSXmlFactory.buildSamplingCurve("2.0.0", identifier, null, null, null, geom, null, null, null);
                    final FeatureProperty foi     = SOSXmlFactory.buildFeatureProperty("2.0.0", sp);
                    gb.addGeometry(geom);

                    final DataArrayProperty result = SOSXmlFactory.buildDataArrayProperty("2.0.0", "array-1", count, "SimpleDataArray", datarecord, DEFAULT_ENCODING, sb.toString());
                    results.observations.add(OMXmlFactory.buildObservation("2.0.0",             // version
                                                  identifier,                      // id
                                                  identifier,                      // name
                                                  null,                            // description
                                                  foi,                             // foi
                                                  phenomenon,                      // phenomenon
                                                  procedureID,                     // procedure
                                                  result,                          // result
                                                  gb.getTimeObject("2.0.0")));     // time
                    results.spatialBound.merge(gb);
                    compo.spatialBound.merge(gb);
                }
                
            } else {
                final ProcedureTree system = new ProcedureTree(procedureID, "System");
                results.procedures.add(system);
                
                for (int j = 0; j < separators.size(); j++) {
                    
                    final String identifier    = separators.get(j);
                    final StringBuilder sb     = new StringBuilder();
                    int count                  = timeVar.getDimension(0).getLength();
                    final GeoSpatialBound gb   = new GeoSpatialBound();
                    final String currentProcID = procedureID + '-' + identifier;
                    final ProcedureTree compo  = new ProcedureTree(currentProcID, "Component");
                        
                    if (acceptedSensorID == null || acceptedSensorID.contains(currentProcID)) {
                        final List<DirectPosition> positions = new ArrayList<>();
                        DirectPosition previousPosition = null;

                        for (int i = 0; i < count; i++) {

                            long millis = getTimeValue(timeFirst, constantT, timeArray, i, j);

                            if (millis == 0 || millis == ((Integer.MIN_VALUE * -1) + 1)) {
                                continue;
                            }
                            final Date d = new Date(millis * 1000);
                            gb.addDate(d);
                            synchronized(FORMATTER) {
                                sb.append(FORMATTER.format(d)).append(DEFAULT_ENCODING.getTokenSeparator());
                            }

                            final double latitude         = getDoubleValue(true, latArray, i, j);
                            sb.append(latitude).append(DEFAULT_ENCODING.getTokenSeparator());
                            final double longitude        = Longitude.normalize(getDoubleValue(true, lonArray, i, j));
                            sb.append(longitude).append(DEFAULT_ENCODING.getTokenSeparator());
                            final DirectPosition position = SOSXmlFactory.buildDirectPosition("2.0.0", null, 2, Arrays.asList(latitude, longitude));
                            if (!position.equals(previousPosition)) {
                                positions.add(position);
                            }
                            previousPosition = position;
                            gb.addXCoordinate(longitude);
                            gb.addYCoordinate(latitude);

                            for (Field field : analyze.phenfields) {
                                final Array phenArray   = phenArrays.get(field.label);
                                final boolean mainFirst = field.mainVariableFirst;
                                final Double value      = getDoubleValue(mainFirst, phenArray, i, j);

                                //empty string for missing value
                                if (!Double.isNaN(value)) {
                                    sb.append(value);
                                }
                                sb.append(DEFAULT_ENCODING.getTokenSeparator());
                            }
                            // remove the last token separator
                            sb.deleteCharAt(sb.length() - 1);
                            sb.append(DEFAULT_ENCODING.getBlockSeparator());
                        }

                        final LineString geom          = SOSXmlFactory.buildLineString("2.0.0", null, "EPSG:4326", positions);
                        final SamplingFeature sp       = SOSXmlFactory.buildSamplingCurve("2.0.0", identifier, null, null, null, geom, null, null, null);
                        final FeatureProperty foi      = SOSXmlFactory.buildFeatureProperty("2.0.0", sp);
                        gb.addGeometry(geom);

                        final DataArrayProperty result = SOSXmlFactory.buildDataArrayProperty("2.0.0", "array-1", count, "SimpleDataArray", datarecord, DEFAULT_ENCODING, sb.toString());

                        compo.spatialBound.merge(gb);
                        system.children.add(compo);

                        final String obsid = UUID.randomUUID().toString();
                        results.observations.add(OMXmlFactory.buildObservation("2.0.0",           // version
                                                      obsid,                    // id
                                                      obsid,                    // name
                                                      null,                          // description
                                                      foi,                           // foi
                                                      phenomenon,                    // phenomenon
                                                      currentProcID,            // procedure
                                                      result,                        // result
                                                      gb.getTimeObject("2.0.0")));   // time
                        results.spatialBound.merge(gb);
                    }
                }
            }

        } catch (IOException | IllegalArgumentException ex) {
            throw new NetCDFParsingException("error while parsing netcdf trajectory", ex);
        }

        LOGGER.info("datablock parsed");
        return results;
    }
    
    private static ExtractionResult parseDataBlockGrid(final NCFieldAnalyze analyze, final String procedureID, final List<String> acceptedSensorID) throws NetCDFParsingException {
        final ExtractionResult results = new ExtractionResult();
        final ProcedureTree compo = new ProcedureTree(procedureID, "Component");
        if (acceptedSensorID == null || acceptedSensorID.contains(procedureID)) {
            results.procedures.add(compo);

            if (analyze.mainField == null) {
                LOGGER.warning("No main field found");
                return results;
            }
            LOGGER.info("parsing netCDF GRID");

            try {

                final Variable latVar = analyze.vars.get(analyze.latField.label);
                final Variable lonVar = analyze.vars.get(analyze.lonField.label);
                final Array latArray  = analyze.file.readArrays(Arrays.asList(latVar)).get(0);;
                final Array lonArray  = analyze.file.readArrays(Arrays.asList(lonVar)).get(0);

                final Variable timeVar  = analyze.vars.get(analyze.mainField.label);
                final Array timeArray   = analyze.file.readArrays(Arrays.asList(timeVar)).get(0);


                final Map<String, Array> phenArrays = new HashMap<>();
                for (Field field : analyze.phenfields) {
                    final Variable phenVar = analyze.vars.get(field.label);
                    final Array phenArray  = analyze.file.readArrays(Arrays.asList(phenVar)).get(0);
                    phenArrays.put(field.label, phenArray);
                    results.fields.add(field.label);
                }

                final AbstractDataRecord datarecord = OMUtils.getDataRecordTimeSeries("2.0.0", analyze.phenfields);
                final Phenomenon phenomenon         = OMUtils.getPhenomenon("2.0.0", analyze.phenfields);
                results.phenomenons.add(phenomenon);

                final int latSize = latVar.getDimension(0).getLength();
                for (int latIndex = 0; latIndex < latSize; latIndex++) {

                    final int lonSize = lonVar.getDimension(0).getLength();
                    for (int lonIndex = 0; lonIndex < lonSize; lonIndex++) {

                        final String identifier  = UUID.randomUUID().toString();
                        final StringBuilder sb   = new StringBuilder();
                        final int count          = timeVar.getDimension(0).getLength();
                        final GeoSpatialBound gb = new GeoSpatialBound();


                        final double latitude         = getDoubleValue(latArray, latIndex);
                        final double longitude        = Longitude.normalize(getDoubleValue(lonArray, lonIndex));
                        final DirectPosition position = SOSXmlFactory.buildDirectPosition("2.0.0", null, 2, Arrays.asList(latitude, longitude));
                        final Point geom              = SOSXmlFactory.buildPoint("2.0.0", "SamplingPoint", position);
                        final SamplingFeature sp      = SOSXmlFactory.buildSamplingPoint("2.0.0", identifier, null, null, null, geom);
                        final FeatureProperty foi     = SOSXmlFactory.buildFeatureProperty("2.0.0", sp);
                        gb.addXCoordinate(longitude);
                        gb.addYCoordinate(latitude);

                        for (int i = 0; i < count; i++) {

                            long millis = getTimeValue(timeArray, i);

                            if (millis == 0 || millis == ((Integer.MIN_VALUE * -1) + 1)) {
                                continue;
                            }
                            final Date d = new Date(millis * 1000);
                            gb.addDate(d);
                            synchronized(FORMATTER) {
                                sb.append(FORMATTER.format(d)).append(DEFAULT_ENCODING.getTokenSeparator());
                            }

                            for (Field field : analyze.phenfields) {
                                final Array phenArray   = phenArrays.get(field.label);
                                final Double value      = getDoubleValue(phenArray, i, latIndex, lonIndex);

                                //empty string for missing value
                                if (!Double.isNaN(value)) {
                                    sb.append(value);
                                }
                                sb.append(DEFAULT_ENCODING.getTokenSeparator());
                            }
                            // remove the last token separator
                            sb.deleteCharAt(sb.length() - 1);
                            sb.append(DEFAULT_ENCODING.getBlockSeparator());
                        }

                        final DataArrayProperty result = SOSXmlFactory.buildDataArrayProperty("2.0.0", "array-1", count, "SimpleDataArray", datarecord, DEFAULT_ENCODING, sb.toString());
                        results.observations.add(OMXmlFactory.buildObservation("2.0.0",           // version
                                                      identifier,                    // id
                                                      identifier,                    // name
                                                      null,                          // description
                                                      foi,                           // foi
                                                      phenomenon,                    // phenomenon
                                                      procedureID,                   // procedure
                                                      result,                        // result
                                                      gb.getTimeObject("2.0.0")));   // time
                        results.spatialBound.merge(gb);
                    }
                }

                results.spatialBound.addGeometry(results.spatialBound.getPolyGonBounds("2.0.0"));
                compo.spatialBound.addGeometry(results.spatialBound.getPolyGonBounds("2.0.0"));

            } catch (IOException | IllegalArgumentException ex) {
                throw new NetCDFParsingException("error while parsing netcdf grid", ex);
            }
        }

        LOGGER.info("datablock parsed");
        return results;
    }
    
    private static List<String> parseSeparatorValues(final NetcdfFile file, final NCFieldAnalyze analyze) throws IOException {
        final List<String> separators = new ArrayList<>();
        if (analyze.separatorField != null) {
            final Variable separatorVar = analyze.vars.get(analyze.separatorField.label);
            final Array array = file.readArrays(Arrays.asList(separatorVar)).get(0);
            if (array instanceof ArrayChar.D2) {
                final ArrayChar.D2 separatorArray = (ArrayChar.D2) file.readArrays(Arrays.asList(separatorVar)).get(0);
                final int separatorsSize = separatorVar.getDimension(0).getLength();
                for (int j = 0; j < separatorsSize; j++) {
                    final int size = separatorVar.getDimension(1).getLength();
                    final char[] id = new char[size];
                    for (int i = 0; i < size; i++) {
                        id[i] = separatorArray.get(j, i);
                    }
                    final String identifier = new String(id).trim() + '-';
                    separators.add(identifier);
                }
            } else if (array instanceof ArrayInt.D1) {
                final ArrayInt.D1 separatorArray = (ArrayInt.D1) file.readArrays(Arrays.asList(separatorVar)).get(0);
                final int separatorsSize = separatorVar.getDimension(0).getLength();
                for (int j = 0; j < separatorsSize; j++) {
                    final int id = separatorArray.get(j);
                    final String identifier = Integer.toString(id).trim() + '-';
                    separators.add(identifier);
                }
            }
        } else if (analyze.dimensionSeparator != null) {
            final Dimension dim = analyze.file.findDimension(analyze.dimensionSeparator);
            if (dim != null) {
                for (int i = 0; i < dim.getLength(); i++) {
                    separators.add(Integer.toString(i));
                }
            }
        }
        return separators;
    }
}
