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
package org.geotoolkit.processing.io.createtempfolder;

import java.net.URL;
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
 * Parameters description of CreateTempFolder process.
 * name of the process : "createTempFolder"
 * inputs :
 * <ul>
 *     <li>PREFIX_IN "prefix" folder prefix</li>
 * </ul>
 * outputs :
 * <ul>
 *     <li>FILE_OUT "folder" resulting temp folder</li>
 * </ul>
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public final class CreateTempFolderDescriptor extends AbstractProcessDescriptor {

    /**
     * Process name : createTempFolder
     */
    public static final String NAME = "io:createTempFolder";

    /**
     * Optional - file prefix
     */
    public static final ParameterDescriptor<String> PREFIX_IN = new ParameterBuilder()
            .addName("prefix")
            .setRemarks("The folder prefix")
            .setRequired(false)
            .create(String.class, "temp");

    /**
     * Input Parameters
     */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName("InputParameters").createGroup(PREFIX_IN);

    /**
     * Mandatory - temporary output folder
     */
    public static final ParameterDescriptor<URL> FILE_OUT = new ParameterBuilder()
            .addName("folder")
            .setRemarks("Created temp folder")
            .setRequired(true)
            .create(URL.class, null);

    /**
     * Output Parameters
     */
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName("OutputParameters").createGroup(FILE_OUT);

    public static final ProcessDescriptor INSTANCE = new CreateTempFolderDescriptor();

    private CreateTempFolderDescriptor() {
        super(NAME, GeotkProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Create a temporary folder."),
                INPUT_DESC, OUTPUT_DESC);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new CreateTempFolder(input);
    }
}
