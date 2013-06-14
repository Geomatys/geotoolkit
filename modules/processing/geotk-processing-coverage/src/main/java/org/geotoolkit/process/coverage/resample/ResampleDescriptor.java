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
package org.geotoolkit.process.coverage.resample;

import java.util.HashMap;
import java.util.Map;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.process.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.coverage.CoverageProcessingRegistry;
import org.apache.sis.util.iso.ResourceInternationalString;
import org.geotoolkit.util.SimpleInternationalString;
import org.opengis.coverage.Coverage;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ResampleDescriptor extends AbstractProcessDescriptor {

    public static final String NAME = "Resample";

    public static final ParameterDescriptor<Coverage> IN_COVERAGE;
    public static final ParameterDescriptor<CoordinateReferenceSystem> IN_CRS;
    public static final ParameterDescriptor<Envelope> IN_ENVELOPE;
    public static final ParameterDescriptorGroup INPUT_DESC;
    
    public static final ParameterDescriptor<Coverage> OUT_COVERAGE;
    public static final ParameterDescriptorGroup OUTPUT_DESC;


    static {
        final String BUNDLE_PATH = "org/geotoolkit/process/coverage/bundle";
        
        final Map<String, Object> propertiesRed = new HashMap<String, Object>();
        propertiesRed.put(IdentifiedObject.NAME_KEY, "coverage");
        propertiesRed.put(IdentifiedObject.ALIAS_KEY, new ResourceInternationalString(BUNDLE_PATH, "resample.inCoverage"));
        propertiesRed.put(IdentifiedObject.REMARKS_KEY, new ResourceInternationalString(BUNDLE_PATH, "resample.inCoverageDesc"));
        IN_COVERAGE = new DefaultParameterDescriptor<Coverage>(
                propertiesRed, Coverage.class, null, null, null, null, null, true);
        
        final Map<String, Object> propertiesCRS = new HashMap<String, Object>();
        propertiesCRS.put(IdentifiedObject.NAME_KEY, "crs");
        propertiesCRS.put(IdentifiedObject.ALIAS_KEY, new ResourceInternationalString(BUNDLE_PATH, "resample.inCRS"));
        propertiesCRS.put(IdentifiedObject.REMARKS_KEY, new ResourceInternationalString(BUNDLE_PATH, "resample.inCRSDesc"));
        IN_CRS = new DefaultParameterDescriptor<CoordinateReferenceSystem>(
                propertiesCRS, CoordinateReferenceSystem.class, null, null, null, null, null, false);
        
        final Map<String, Object> propertiesEnv = new HashMap<String, Object>();
        propertiesEnv.put(IdentifiedObject.NAME_KEY, "envelope");
        propertiesEnv.put(IdentifiedObject.ALIAS_KEY, new ResourceInternationalString(BUNDLE_PATH, "resample.inEnvelope"));
        propertiesEnv.put(IdentifiedObject.REMARKS_KEY, new ResourceInternationalString(BUNDLE_PATH, "resample.inEnvelopeDesc"));
        IN_ENVELOPE = new DefaultParameterDescriptor<Envelope>(
                propertiesEnv, Envelope.class, null, null, null, null, null, false);


        INPUT_DESC = new DefaultParameterDescriptorGroup(NAME + "InputParameters", IN_COVERAGE, IN_CRS, IN_ENVELOPE);

        final Map<String, Object> propertiesOut = new HashMap<String, Object>();
        propertiesOut.put(IdentifiedObject.NAME_KEY, "result");
        propertiesOut.put(IdentifiedObject.ALIAS_KEY, new ResourceInternationalString(BUNDLE_PATH, "resample.outCoverage"));
        propertiesOut.put(IdentifiedObject.REMARKS_KEY, new ResourceInternationalString(BUNDLE_PATH, "resample.outCoverageDesc"));
        OUT_COVERAGE = new DefaultParameterDescriptor<Coverage>(
                propertiesOut, Coverage.class, null, null, null, null, null, true);

        OUTPUT_DESC  = new DefaultParameterDescriptorGroup(NAME + "OutputParameters", OUT_COVERAGE);
    }

    public static final ProcessDescriptor INSTANCE = new ResampleDescriptor();

    private ResampleDescriptor() {
        super(NAME, CoverageProcessingRegistry.IDENTIFICATION, 
                new SimpleInternationalString("Resample a coverage."), 
                INPUT_DESC, 
                OUTPUT_DESC);
    }

    @Override
    public Process createProcess(ParameterValueGroup input) {
        return new ResampleProcess(input);
    }
        
}
