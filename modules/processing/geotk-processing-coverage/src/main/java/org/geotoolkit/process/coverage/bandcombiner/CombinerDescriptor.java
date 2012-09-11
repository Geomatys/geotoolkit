/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
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
package org.geotoolkit.process.coverage.bandcombiner;

import java.util.HashMap;
import java.util.Map;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.process.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.coverage.CoverageProcessingRegistry;
import org.geotoolkit.util.ResourceInternationalString;
import org.geotoolkit.util.SimpleInternationalString;
import org.opengis.coverage.Coverage;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.IdentifiedObject;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class CombinerDescriptor extends AbstractProcessDescriptor {

    public static final String NAME = "Combiner";

    /**
     * Mandatory - Bands to merge.
     */
    public static final ParameterDescriptor<Coverage> IN_RED;

    public static final ParameterDescriptor<Coverage> IN_GREEN ;

    public static final ParameterDescriptor<Coverage> IN_BLUE ;

    public static final ParameterDescriptorGroup INPUT_DESC;
    /**
     * Mandatory - Resulting image.
     */
    public static final ParameterDescriptor<Coverage> OUT_BAND =
            new DefaultParameterDescriptor<Coverage>("result","Coverage created", Coverage.class,null,true);

    public static final ParameterDescriptorGroup OUTPUT_DESC;

    public static final String BUNDLE_PATH = "org/geotoolkit/process/coverage/bundle";

    /**
     * Following attributes are the keys for international description fields.
     * (See @Process_package/bundle.properties).
     */
    protected static final String RED_DESC_KEY    = "redDescription";
    protected static final String RED_TITLE_KEY   = "redTitle";
    protected static final String GREEN_DESC_KEY  = "greenDescription";
    protected static final String GREEN_TITLE_KEY = "greenTitle";
    protected static final String BLUE_DESC_KEY   = "blueDescription";
    protected static final String BLUE_TITLE_KEY  = "blueTitle";
    protected static final String OUT_DESC_KEY    = "rgbDescription";
    protected static final String OUT_TITLE_KEY   = "rgbTitle";

    static {
        Map<String, Object> propertiesRed = new HashMap<String, Object>();
        propertiesRed.put(IdentifiedObject.NAME_KEY,        "red");
        propertiesRed.put(IdentifiedObject.ALIAS_KEY,       new ResourceInternationalString(BUNDLE_PATH, RED_TITLE_KEY));
        propertiesRed.put(IdentifiedObject.REMARKS_KEY,     new ResourceInternationalString(BUNDLE_PATH, RED_DESC_KEY));
        IN_RED = new DefaultParameterDescriptor<Coverage>(propertiesRed, Coverage.class, null, null, null, null, null, true);

        Map<String, Object> propertiesGreen = new HashMap<String, Object>();
        propertiesGreen.put(IdentifiedObject.NAME_KEY,        "green");
        propertiesGreen.put(IdentifiedObject.ALIAS_KEY,       new ResourceInternationalString(BUNDLE_PATH, GREEN_TITLE_KEY));
        propertiesGreen.put(IdentifiedObject.REMARKS_KEY,     new ResourceInternationalString(BUNDLE_PATH, GREEN_DESC_KEY));
        IN_GREEN = new DefaultParameterDescriptor<Coverage>(propertiesGreen, Coverage.class, null, null, null, null, null, true);

        Map<String, Object> propertiesBlue = new HashMap<String, Object>();
        propertiesBlue.put(IdentifiedObject.NAME_KEY,        "blue");
        propertiesBlue.put(IdentifiedObject.ALIAS_KEY,       new ResourceInternationalString(BUNDLE_PATH, BLUE_TITLE_KEY));
        propertiesBlue.put(IdentifiedObject.REMARKS_KEY,     new ResourceInternationalString(BUNDLE_PATH, BLUE_DESC_KEY));
        IN_BLUE = new DefaultParameterDescriptor<Coverage>(propertiesBlue, Coverage.class, null, null, null, null, null, true);

        INPUT_DESC = new DefaultParameterDescriptorGroup(NAME + "InputParameters", IN_RED, IN_GREEN, IN_BLUE);

        Map<String, Object> propertiesOut = new HashMap<String, Object>();
        propertiesOut.put(IdentifiedObject.NAME_KEY,        "result");
        propertiesOut.put(IdentifiedObject.ALIAS_KEY,       new ResourceInternationalString(BUNDLE_PATH, OUT_TITLE_KEY));
        propertiesOut.put(IdentifiedObject.REMARKS_KEY,     new ResourceInternationalString(BUNDLE_PATH, OUT_DESC_KEY));

        OUTPUT_DESC  = new DefaultParameterDescriptorGroup(NAME + "OutputParameters", OUT_BAND);
    }

    public static final ProcessDescriptor INSTANCE = new CombinerDescriptor();

    private CombinerDescriptor() {
        super(NAME, CoverageProcessingRegistry.IDENTIFICATION, new SimpleInternationalString("Get multiple raster bands to merge them into one entity"), INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess(ParameterValueGroup input) {
        return new CombinerProcess(input);
    }

}
