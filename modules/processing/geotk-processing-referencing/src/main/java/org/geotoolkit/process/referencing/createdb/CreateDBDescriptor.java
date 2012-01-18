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
package org.geotoolkit.process.referencing.createdb;

import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.process.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.referencing.ReferencingProcessingRegistry;
import org.geotoolkit.util.SimpleInternationalString;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Description of a create epsg databse process.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class CreateDBDescriptor extends AbstractProcessDescriptor{

    public static final String NAME = "CreateDB";

    public static final ParameterDescriptor<String> DBURL =
            new DefaultParameterDescriptor<String>("dburl","Database JDBC URL.",String.class,null,true);
    
    public static final ParameterDescriptor<String> USER =
            new DefaultParameterDescriptor<String>("user","Database user.",String.class,null,true);
    
    public static final ParameterDescriptor<String> PASSWORD =
            new DefaultParameterDescriptor<String>("password","Database password.",String.class,null,true);
    
        
    public static final ParameterDescriptorGroup INPUT_DESC =
            new DefaultParameterDescriptorGroup(NAME+"InputParameters",
                DBURL,USER,PASSWORD);


    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new DefaultParameterDescriptorGroup(NAME+"OutputParameters");
    
    public static final ProcessDescriptor INSTANCE = new CreateDBDescriptor();


    private CreateDBDescriptor(){
        super(NAME, ReferencingProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Create an epsg database."),
                INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new CreateDBProcess(input);
    }

}
