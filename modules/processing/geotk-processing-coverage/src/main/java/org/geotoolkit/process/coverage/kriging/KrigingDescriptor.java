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
package org.geotoolkit.process.coverage.kriging;

import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.process.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.coverage.CoverageProcessingRegistry;
import org.geotoolkit.util.SimpleInternationalString;

import org.opengis.geometry.DirectPosition;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Description of a coverage to polygon process.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class KrigingDescriptor extends AbstractProcessDescriptor{

    public static final String NAME = "kriging";

    /**
     * Mandatory - Source points
     */
    public static final ParameterDescriptor<DirectPosition[]> IN_POINTS =
            new DefaultParameterDescriptor<DirectPosition[]>("points",
            "points used to general the grid",DirectPosition[].class,null,true);
    
    /**
     * Mandatory - stepping for line generation.
     * use 0 or negative to not generate them
     */
    public static final ParameterDescriptor<Double> IN_STEP =
            new DefaultParameterDescriptor<Double>("step",
            "step for isolines",Double.class,0d,true);

    /**
     * Mandatory - Coverage crs
     */
    public static final ParameterDescriptor<CoordinateReferenceSystem> IN_CRS =
            new DefaultParameterDescriptor<CoordinateReferenceSystem>("crs",
            "CRS used for the output coverage",CoordinateReferenceSystem.class,null,true);


    public static final ParameterDescriptorGroup INPUT_DESC =
            new DefaultParameterDescriptorGroup(NAME+"InputParameters",
                IN_POINTS, IN_CRS, IN_STEP);
    
    /**
     * Output coverage.
     */
    public static final ParameterDescriptor<GridCoverage2D> OUT_COVERAGE =
            new DefaultParameterDescriptor<GridCoverage2D>("coverage",
            "Coverage",GridCoverage2D.class,null,false);
    
    /**
     * Output lines.
     */
    public static final ParameterDescriptor<FeatureCollection> OUT_LINES =
            new DefaultParameterDescriptor<FeatureCollection>("lines",
            "Isolines as a featureCollection",FeatureCollection.class,null,false);


    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new DefaultParameterDescriptorGroup(NAME+"OutputParameters",
                OUT_COVERAGE,OUT_LINES);
    
    public static final ProcessDescriptor INSTANCE = new KrigingDescriptor();


    private KrigingDescriptor(){
        super(NAME, CoverageProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Transform an array of points in a coverage"
                + "by using a kriging operation."),INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new KrigingProcess(input);
    }

}
