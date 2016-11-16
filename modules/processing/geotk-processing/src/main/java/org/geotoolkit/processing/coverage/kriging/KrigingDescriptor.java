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
package org.geotoolkit.processing.coverage.kriging;

import java.awt.Dimension;
import org.apache.sis.parameter.ParameterBuilder;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.processing.coverage.CoverageProcessingRegistry;
import org.apache.sis.util.iso.SimpleInternationalString;

import org.opengis.geometry.DirectPosition;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Description of a coverage to polygon process.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public final class KrigingDescriptor extends AbstractProcessDescriptor{

    public static final String NAME = "kriging";

    /**
     * Mandatory - Source points
     */
    public static final ParameterDescriptor<DirectPosition[]> IN_POINTS = new ParameterBuilder()
            .addName("points")
            .setRemarks("points used to general the grid")
            .setRequired(true)
            .create(DirectPosition[].class,null);
    
    /**
     * Mandatory - stepping for line generation.
     * use 0 or negative to not generate them
     */
    public static final ParameterDescriptor<Double> IN_STEP = new ParameterBuilder()
            .addName("step")
            .setRemarks("step for isolines")
            .setRequired(true)
            .create(Double.class,0d);
    /**
     * Optional - maximum size of the image.
     */
    public static final ParameterDescriptor<Dimension> IN_DIMENSION = new ParameterBuilder()
            .addName("dimension")
            .setRemarks("Result grid size")
            .setRequired(false)
            .create(Dimension.class,null);

    /**
     * Mandatory - Coverage crs
     */
    public static final ParameterDescriptor<CoordinateReferenceSystem> IN_CRS = new ParameterBuilder()
            .addName("crs")
            .setRemarks("CRS used for the output coverage")
            .setRequired(true)
            .create(CoordinateReferenceSystem.class,null);


    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName(NAME+"InputParameters").createGroup(
                IN_POINTS, IN_CRS, IN_STEP, IN_DIMENSION);
    
    /**
     * Output coverage.
     */
    public static final ParameterDescriptor<GridCoverage2D> OUT_COVERAGE = new ParameterBuilder()
            .addName("coverage")
            .setRemarks("Coverage")
            .setRequired(false)
            .create(GridCoverage2D.class,null);
    
    /**
     * Output lines.
     */
    public static final ParameterDescriptor<FeatureCollection> OUT_LINES = new ParameterBuilder()
            .addName("lines")
            .setRemarks("Isolines as a featureCollection")
            .setRequired(false)
            .create(FeatureCollection.class,null);


    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName(NAME+"OutputParameters").createGroup(
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
