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
package org.geotoolkit.process.coverage.volume;

import com.vividsolutions.jts.geom.Geometry;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.process.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.coverage.CoverageProcessingRegistry;
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
     * Input {@link GridCoverageReader} which will be resample.
     */
    public static final ParameterDescriptor<GridCoverageReader> IN_GRIDCOVERAGE_READER =
            new DefaultParameterDescriptor<>(INPUT_READER_NAME,
            "GridCoverageReader which contain Digital Elevation model to compute bulk.", GridCoverageReader.class, null, true);
    
    public static final String INPUT_JTS_GEOMETRY_NAME = "Geometry";
    /**
     * Geometry which represent area where compute bulk.
     */
    public static final ParameterDescriptor<Geometry> IN_JTSGEOMETRY =
            new DefaultParameterDescriptor<>(INPUT_JTS_GEOMETRY_NAME,
            "Geomatry which represent area where compute bulk.", Geometry.class, null, true);
    
    public static final String INPUT_GEOMETRY_CRS_NAME = "Geometry CRS";
    /**
     * Geometry Coordinate Reference System.
     */
    public static final ParameterDescriptor<CoordinateReferenceSystem> IN_GEOMETRY_CRS =
            new DefaultParameterDescriptor<>(INPUT_GEOMETRY_CRS_NAME,
            "Define in which CRS geometry is defined.", CoordinateReferenceSystem.class, null, false);
    
    public static final String INPUT_BAND_INDEX_NAME = "D E M band index.";
    /**
     * DEM band index.
     */
    public static final ParameterDescriptor<Integer> IN_INDEX_BAND =
            new DefaultParameterDescriptor<>(INPUT_BAND_INDEX_NAME,
            "D E M band index which will be use to compute volume.", Integer.class, 0, false);
    
    public static final String INPUT_GEOMETRY_ALTITUDE_NAME = "Minimum Altitude ceiling value.";
    /**
     * Altitude ceiling value.<br/>
     * Bulk which will be compute between area formed by Geometry area and this altitude ceiling value.
     */
    public static final ParameterDescriptor<Double> GEOMETRY_ALTITUDE =
            new DefaultParameterDescriptor<>(INPUT_GEOMETRY_ALTITUDE_NAME,
            "Minimal altitude value. Volume is computed between ground formed by geometry at this value and Maximum altitude value.", Double.class, 0.00, false);
    
    public static final String INPUT_MAX_CEILING_NAME = "Maximum Altitude ceiling value.";
    /**
     * Altitude ceiling value.<br/>
     * Bulk which will be compute between area formed by Geometry area and this altitude ceiling value.
     */
    public static final ParameterDescriptor<Double> IN_MAX_ALTITUDE_CEILING =
            new DefaultParameterDescriptor<>(INPUT_MAX_CEILING_NAME,
            "Maximal altitude value. Volume is computed between ground formed by geometry at minimum ceiling value and its value.", Double.class, 0.00, true);
    /**************************************************************************/
    
                                    /*Output*/
    /**************************************************************************/
    public static final String OUTPUT_VOLUME_NAME = "computed volume result.";
    /**
     * Altitude ceiling value.<br/>
     * Bulk which will be compute between area formed by Geometry area and this altitude ceiling value.
     */
    public static final ParameterDescriptor<Double> OUT_VOLUME_RESULT =
            new DefaultParameterDescriptor<>(OUTPUT_VOLUME_NAME,
            "Result of volume computing.", Double.class, 0.00, true);
    /**************************************************************************/
    
                                      /*GROUP*/
    /**************************************************************************/
    //Input group
    public static final ParameterDescriptorGroup INPUT_DESC =
            new DefaultParameterDescriptorGroup("InputParameters",
                IN_GRIDCOVERAGE_READER, IN_JTSGEOMETRY, IN_GEOMETRY_CRS, IN_INDEX_BAND, GEOMETRY_ALTITUDE, IN_MAX_ALTITUDE_CEILING);
    //Output group
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new DefaultParameterDescriptorGroup("OutputParameters", OUT_VOLUME_RESULT);
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
