/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009 - 2012, Geomatys
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
package org.geotoolkit.processing.coverage.volume;

import com.vividsolutions.jts.geom.Geometry;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.processing.coverage.CoverageProcessingRegistry;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Define input and output objects necessary to {@link ComputeBulkProcess}.
 *
 * @author Remi Marechal (Geomatys).
 */
public class ComputeVolumeDescriptor extends AbstractProcessDescriptor {

    public static final String NAME = "computeVolume";

                                    /*INPUTS*/
    /**************************************************************************/
    public static final String INPUT_READER_NAME = "GridCoverageReader";
    /**
     * Input {@link GridCoverageReader} which will be studied.
     */
    public static final ParameterDescriptor<GridCoverageReader> IN_GRIDCOVERAGE_READER = new ParameterBuilder()
            .addName(INPUT_READER_NAME)
            .setRemarks("GridCoverageReader which contain Digital Elevation model to compute bulk.")
            .setRequired(true)
            .create(GridCoverageReader.class, null);
    
    public static final String INPUT_JTS_GEOMETRY_NAME = "Geometry";
    /**
     * Geometry which represent area where compute volume.
     */
    public static final ParameterDescriptor<Geometry> IN_JTSGEOMETRY = new ParameterBuilder()
            .addName(INPUT_JTS_GEOMETRY_NAME)
            .setRemarks("Geomatry which represent area where compute bulk.")
            .setRequired(true)
            .create(Geometry.class, null);
    
    public static final String INPUT_GEOMETRY_CRS_NAME = "Geometry CRS";
    /**
     * Geometry Coordinate Reference System.
     */
    public static final ParameterDescriptor<CoordinateReferenceSystem> IN_GEOMETRY_CRS = new ParameterBuilder()
            .addName(INPUT_GEOMETRY_CRS_NAME)
            .setRemarks("Define in which CRS geometry is defined.")
            .setRequired(false)
            .create(CoordinateReferenceSystem.class, null);
    
    public static final String INPUT_BAND_INDEX_NAME = "D E M band index.";
    /**
     * DEM band index where volume is computed.
     */
    public static final ParameterDescriptor<Integer> IN_INDEX_BAND = new ParameterBuilder()
            .addName(INPUT_BAND_INDEX_NAME)
            .setRemarks("D E M band index which will be use to compute volume.")
            .setRequired(false)
            .create(Integer.class, 0);
    
    public static final String INPUT_GEOMETRY_ALTITUDE_NAME = "Minimum Altitude ceiling value.";
    /**
     * Altitude ceiling value.<br/>
     * Bulk which will be compute between area formed by Geometry area and this altitude ceiling value.
     */
    public static final ParameterDescriptor<Double> IN_GEOMETRY_ALTITUDE = new ParameterBuilder()
            .addName(INPUT_GEOMETRY_ALTITUDE_NAME)
            .setRemarks("Minimal altitude value. Volume is computed between ground formed by geometry at this value and Maximum altitude value.")
            .setRequired(false)
            .create(Double.class, 0.00);
    
    public static final String INPUT_MAX_CEILING_NAME = "Maximum Altitude ceiling value.";
    /**
     * Altitude ceiling value.<br/>
     * volume which will be compute between area formed by Geometry area and this altitude ceiling value.
     */
    public static final ParameterDescriptor<Double> IN_MAX_ALTITUDE_CEILING = new ParameterBuilder()
            .addName(INPUT_MAX_CEILING_NAME)
            .setRemarks("Maximal altitude value. Volume is computed between ground formed by geometry at minimum ceiling value and its value.")
            .setRequired(true)
            .create(Double.class, 0.00);
    /**************************************************************************/
    
                                    /*Output*/
    /**************************************************************************/
    public static final String OUTPUT_VOLUME_NAME = "computed volume result.";
    /**
     * Altitude ceiling value.<br/>
     * Volume which will be compute between area formed by Geometry area and this altitude ceiling value.
     */
    public static final ParameterDescriptor<Double> OUT_VOLUME_RESULT = new ParameterBuilder()
            .addName(OUTPUT_VOLUME_NAME)
            .setRemarks("Result of volume computing.")
            .setRequired(true)
            .create(Double.class, 0.00);
    /**************************************************************************/
    
                                      /*GROUP*/
    /**************************************************************************/
    //Input group
    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName("InputParameters").createGroup(
                IN_GRIDCOVERAGE_READER, IN_JTSGEOMETRY, IN_GEOMETRY_CRS, IN_INDEX_BAND, IN_GEOMETRY_ALTITUDE, IN_MAX_ALTITUDE_CEILING);
    //Output group
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName("OutputParameters").createGroup(OUT_VOLUME_RESULT);
    /**************************************************************************/
    
    public static final ProcessDescriptor INSTANCE = new ComputeVolumeDescriptor();
    
    private ComputeVolumeDescriptor() {
        super(NAME, CoverageProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Create a pyramid / mosaic from the given"
                + "coverage. Created tiles are stored in the given Coverage store."),
                INPUT_DESC, OUTPUT_DESC);
    }
    
    /**
     * {@inheritDoc }.
     */
    @Override
    public Process createProcess(ParameterValueGroup input) {
        return new ComputeVolumeProcess(input);
    }
}
