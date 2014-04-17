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
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.geotoolkit.swe.xml.v200.TextEncodingType.DEFAULT_ENCODING;

import org.apache.sis.util.logging.Logging;
import org.geotoolkit.observation.xml.OMXmlFactory;

import org.opengis.observation.Observation;

import ucar.ma2.Array;
import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

import static org.geotoolkit.sos.netcdf.NetCDFUtils.*;
import org.geotoolkit.sos.xml.SOSXmlFactory;
import org.geotoolkit.swe.xml.AbstractDataRecord;
import org.geotoolkit.swe.xml.DataArrayProperty;
import org.geotoolkit.swe.xml.v100.DataArrayType;
import ucar.ma2.ArrayChar;
import ucar.ma2.ArrayInt;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class NetCDFExtractor {
    
    private static final Logger LOGGER = Logging.getLogger(NetCDFExtractor.class);
    
    private static final DateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    
    public List<Observation> getObservationFromNetCDF(final File netCDFFile) {
        final NCFieldAnalyze analyze = analyzeResult(netCDFFile);
        return parseDataBlockTS(analyze.file, analyze);
    }
    
    public NCFieldAnalyze analyzeResult(final File netCDFFile) {
        final NCFieldAnalyze analyze = new NCFieldAnalyze();
        try {
            
            final NetcdfFile file = NetcdfFile.open(netCDFFile.getPath());
            analyze.file = file;

            final Attribute ftAtt = file.findGlobalAttribute("featureType");
            if (ftAtt != null) {
                final String value = ftAtt.getStringValue();
                if ("timeSeries".equals(value) || "trajectory".equals(value)) {
                    analyze.timeSeries = true;
                } else if ("profile".equals(value)) {
                    analyze.timeSeries = false;
                }
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
                    final String dimensionLabel = variable.getDimensionsString();
                    final Field currentField = new Field(name, dimension, dimensionLabel);
                    all.add(currentField);
                    analyze.vars.put(name, variable);
                    
                    // try to get units
                    final Attribute att = variable.findAttribute("units");
                    if (att != null) {
                        currentField.unit = att.getStringValue();
                    }
                    
                    if (name.equalsIgnoreCase("Time")) {
                        if (analyze.timeSeries) {
                            currentField.type = Type.DATE;
                            analyze.mainField = currentField;
                        }

                    } else if (name.equalsIgnoreCase("Latitude") || name.equalsIgnoreCase("Longitude")) {
                        currentField.type = Type.DOUBLE;
                        analyze.skippedFields.add(currentField);

                    } else if (name.equalsIgnoreCase("Pression") || name.equalsIgnoreCase("Depth") || name.equalsIgnoreCase("zLevel") || name.equalsIgnoreCase("z")) {
                        currentField.type = Type.DOUBLE;
                        if (!analyze.timeSeries) {
                            analyze.mainField = currentField;
                        } else if (dimension > 1) {
                            analyze.phenfields.add(currentField);
                        } else {
                            analyze.skippedFields.add(currentField);
                        }

                    } else if (name.equalsIgnoreCase("timeserie") || name.equalsIgnoreCase("trajectory") || name.equalsIgnoreCase("profile")) {
                        currentField.type = Type.STRING;
                        analyze.separatorField = currentField;
                        
                        
                    } else  {
                        currentField.type = getTypeFromDataType(variable.getDataType());
                        if ((currentField.type == Type.DOUBLE || currentField.type == Type.INT) && dimension != 0) {
                            analyze.phenfields.add(currentField);
                        } else {
                            analyze.skippedFields.add(currentField);
                        }
                    }
                }
            }
            
            // post analyze
            for (Field f : all) {
                final String mainDimension = analyze.mainField.label;
                
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


        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return analyze;
    }
    
    private List<Observation> parseDataBlockTS(final NetcdfFile file, final NCFieldAnalyze analyze) {
        final List<Observation> results = new ArrayList<>();
        if (analyze.mainField == null) {
            LOGGER.warning("No main field found");
            return results;
        }
        LOGGER.info("parsing netCDF TS");

        try {

            final List<String> separators = parseSeparatorValues(file, analyze);

            boolean single = false;
            if (separators.isEmpty()) {
                single = true;
                separators.add("");
            }
                
            for (String identifier : separators) {
                for (Field field : analyze.phenfields) {
                    final String serieName = identifier + field.label;
                    
                }
            }
            
            final Variable timeVar   = analyze.vars.get(analyze.mainField.label);
            final Array timeArray   = file.readArrays(Arrays.asList(timeVar)).get(0);
            final boolean constantT = analyze.mainField.dimension == 1;
            final boolean timeFirst = analyze.mainField.mainVariableFirst;
            
            
            final Map<String, Array> phenArrays = new HashMap<>();
            for (Field field : analyze.phenfields) {
                final Variable phenVar =  analyze.vars.get(field.label);
                final Array phenArray = file.readArrays(Arrays.asList(phenVar)).get(0);
                phenArrays.put(field.label, phenArray);
            }
            
            final AbstractDataRecord datarecord = OMUtils.getDataRecordTimeSeries("2.0.0", analyze.phenfields);
            final org.geotoolkit.swe.xml.Phenomenon phenomenon = OMUtils.getPhenomenon("2.0.0", analyze.phenfields);

            if (single) {
                final StringBuilder sb = new StringBuilder();
                final int count = timeVar.getDimension(0).getLength();
                for (int i = 0; i < count; i++) {

                    long millis = getTimeValue(timeArray, i);

                    if (millis == 0 || millis == ((Integer.MIN_VALUE * -1) + 1)) {
                        continue;
                    }
                    final Date d = new Date(millis * 1000);

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
                results.add(OMXmlFactory.buildObservation("2.0.0",                         // version
                                              "TODOID",    // id
                                              "TODONAME",        // name
                                              "TODODESC", // description
                                              null,                            // foi
                                              phenomenon,                      // phenomenon
                                              "TODOPROC",                       // procedure
                                              result,                          // result
                                              null));                       // time
                
                
            } else {
                for (int j = 0; j < separators.size(); j++) {
                    final String separator = separators.get(j);

                    final StringBuilder sb = new StringBuilder();
                    int count = timeVar.getDimension(0).getLength();
                    for (int i = 0; i < count; i++) {

                        long millis = getTimeValue(timeFirst, constantT, timeArray, i, j);

                        if (millis == 0 || millis == ((Integer.MIN_VALUE * -1) + 1)) {
                            continue;
                        }
                        final Date d = new Date(millis * 1000);

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
                    results.add(OMXmlFactory.buildObservation("2.0.0",                         // version
                                                  separator + "TODOID",    // id
                                                  separator + "TODONAME",        // name
                                                  "TODODESC", // description
                                                  null,                            // foi
                                                  phenomenon,                      // phenomenon
                                                  "TODOPROC",                       // procedure
                                                  result,                          // result
                                                  null));                       // time
                }
            }

        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "error while parsing netcdf timeserie", ex);
        }

        LOGGER.info("datablock parsed");
        return results;
    }
    
    /**
     * Parse a data block and return only the values matching the time filter.
     *
     * @param brutValues The data block.
     * @param abstractEncoding The encoding of the data block.
     *
     * @return a datablock containing the observations.
     */
    private List<Observation> parseDataBlockXY(final NetcdfFile file, final NCFieldAnalyze analyze, final String seriePrefix) {
        final List<Observation> results = new ArrayList<>();
        if (analyze.mainField == null) {
            LOGGER.warning("No main field found");
            return results;
        }
        LOGGER.info("parsing datablock XY");

        try {
            final List<String> separators = parseSeparatorValues(file, analyze);

            boolean single = false;
            if (separators.isEmpty()) {
                single = true;
                separators.add("");
            }

            for (String identifier : separators) {
                for (Field field : analyze.phenfields) {
                    final String serieName = identifier + field.label;
                    
                }
            }

            final Variable zVar      = analyze.vars.get(analyze.mainField.label);
            final Array zArray      = file.readArrays(Arrays.asList(zVar)).get(0);
            final boolean constantZ = analyze.mainField.dimension == 1;
            final boolean Zfirst    = analyze.mainField.mainVariableFirst;

            final Map<String, Array> phenArrays = new HashMap<>();
            for (Field field : analyze.phenfields) {
                final Variable phenVar = analyze.vars.get(field.label);
                final Array phenArray  = file.readArrays(Arrays.asList(phenVar)).get(0);
                phenArrays.put(field.label, phenArray);
            }

            if (single) {
                final StringBuilder sb = new StringBuilder();
                for (int zIndex = 0; zIndex < zVar.getDimension(0).getLength(); zIndex++) {

                    double zLevel = getDoubleValue(zArray, zIndex);
                    sb.append(zLevel).append(DEFAULT_ENCODING.getTokenSeparator());
                    if (zLevel == 0 || zLevel == FILL_VALUE) {
                        continue;
                    }

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
                
            } else {
                for (int profileIndex = 0; profileIndex < separators.size(); profileIndex++) {
                    final String separator = separators.get(profileIndex);
                    
                    final StringBuilder sb = new StringBuilder();
                    for (int zIndex = 0; zIndex < zVar.getDimension(0).getLength(); zIndex++) {

                        double zLevel = getZValue(Zfirst, constantZ, zArray, zIndex, profileIndex);
                        if (zLevel == 0 || zLevel == FILL_VALUE) {
                            continue;
                        }

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
                    final String dataValue = sb.toString();
                }
            }

        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "error while parsing netcdf timeserie", ex);
        }

        return results;
    }
    
    private List<String> parseSeparatorValues(final NetcdfFile file, final NCFieldAnalyze analyze) throws IOException {
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
        }
        return separators;
    }
}
