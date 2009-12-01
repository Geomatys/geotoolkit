/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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
package org.geotoolkit.process.coverage;

import com.vividsolutions.jts.geom.Geometry;

import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.util.NumberRange;

import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.identification.Identification;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;

/**
 * Description of a coverage to polygon process.
 *
 * @author Johann Sorel (Geomatys)
 */
public class CoverageToVectorDescriptor implements ProcessDescriptor{

    public static final String NAME = "CoverageToVector";

    /**
     * Mandatory - Coverage to process
     */
    public static final GeneralParameterDescriptor COVERAGE =
            new DefaultParameterDescriptor("coverage","Coverage to process.",GridCoverage2D.class,null,true);

    /**
     * Optional - Ranges to regroup
     */
    public static final GeneralParameterDescriptor RANGES =
            new DefaultParameterDescriptor("ranges","Ranges to regroup.",NumberRange[].class,null,false);

    /**
     * Optional - selected band, default 0
     */
    public static final GeneralParameterDescriptor BAND =
            new DefaultParameterDescriptor("band","Band to transform",Integer.class,0,false);

    public static final ParameterDescriptorGroup INPUT_DESC =
            new DefaultParameterDescriptorGroup(NAME+"InputParameters",
                new GeneralParameterDescriptor[]{COVERAGE,RANGES,BAND});

    /**
     * Mandatory - Result of vectorisation
     */
    public static final GeneralParameterDescriptor GEOMETRIES =
            new DefaultParameterDescriptor("geometries","Result of vectorisation.",Geometry[].class,null,true);


    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new DefaultParameterDescriptorGroup(NAME+"OutputParameters",
                new GeneralParameterDescriptor[]{GEOMETRIES});



    private final Identifier id;

    public CoverageToVectorDescriptor(final Identification factoryId){

        id = new Identifier() {

            @Override
            public String getCode() {
                return NAME;
            }

            @Override
            public Citation getAuthority() {
                return factoryId.getCitation();
            }
        };

    }

    @Override
    public Identifier getName() {
        return id;
    }

    @Override
    public ParameterDescriptorGroup getInputDescriptor() {
        return INPUT_DESC;
    }

    @Override
    public ParameterDescriptorGroup getOutputDescriptor() {
        return OUTPUT_DESC;
    }

    @Override
    public Process createProcess() {
        return new CoverageToVectorProcess(this);
    }

}
