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
package org.geotoolkit.processing.referencing.createdb;

import org.apache.sis.parameter.ParameterBuilder;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Description of a create epsg databse process.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public final class CreateDBDescriptor extends AbstractProcessDescriptor{

    public static final String NAME = "referencing:createdb";

    public static final ParameterDescriptor<String> DBURL = new ParameterBuilder()
            .addName("dburl")
            .setRemarks("Database JDBC URL.")
            .setRequired(true)
            .create(String.class,"jdbc:postgresql://localhost:5432/epsg");

    public static final ParameterDescriptor<String> USER = new ParameterBuilder()
            .addName("user")
            .setRemarks("Database user.")
            .setRequired(true)
            .create(String.class,"user");

    public static final ParameterDescriptor<String> PASSWORD = new ParameterBuilder()
            .addName("password")
            .setRemarks("Database password.")
            .setRequired(true)
            .create(String.class,"password");


    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName(NAME+"InputParameters").createGroup(DBURL,USER,PASSWORD);


    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName(NAME+"OutputParameters").createGroup();

    public static final ProcessDescriptor INSTANCE = new CreateDBDescriptor();


    private CreateDBDescriptor(){
        super(NAME, GeotkProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Create an epsg database."),
                INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new CreateDBProcess(input);
    }

}
