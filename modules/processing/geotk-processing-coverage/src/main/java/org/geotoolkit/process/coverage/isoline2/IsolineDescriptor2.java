/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2013, Geomatys
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
package org.geotoolkit.process.coverage.isoline2;

import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.process.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.coverage.CoverageProcessingRegistry;
import org.geotoolkit.util.ResourceInternationalString;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.InternationalString;

/**
 * Create a FeatureCollection of isoline from a GridCoverage2D and an array of intervals.
 *
 * @author Quentin Boileau (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public class IsolineDescriptor2 extends AbstractProcessDescriptor {

    private static final String BUNDLE_PATH = "org/geotoolkit/process/coverage/bundle";


    public static final String NAME = "isoline2";
    public static final InternationalString abs = new ResourceInternationalString(BUNDLE_PATH, "isoline.abstract");

    /*
     * Coverage
     */
    public static final InternationalString IN_COVERAGE_REF_PARAM_REMARKS = new ResourceInternationalString(BUNDLE_PATH, "isoline.inCoverageRef");
    public static final ParameterDescriptor<CoverageReference> COVERAGE_REF =
            new DefaultParameterDescriptor("inCoverageRef", IN_COVERAGE_REF_PARAM_REMARKS, CoverageReference.class, null, true);

    /*
     * Output FeatureStore
     */
    public static final InternationalString IN_FEATURE_STORE_PARAM_REMARKS = new ResourceInternationalString(BUNDLE_PATH, "isoline.inFeatureStore");
    public static final ParameterDescriptor<FeatureStore> FEATURE_STORE =
            new DefaultParameterDescriptor("inFeatureStore", IN_FEATURE_STORE_PARAM_REMARKS, FeatureStore.class, null, false);

    /*
     * Output FeatureType name
     */
    public static final InternationalString IN_FEATURE_NAME_PARAM_REMARKS = new ResourceInternationalString(BUNDLE_PATH, "isoline.inFeatureTypeName");
    public static final ParameterDescriptor<String> FEATURE_NAME =
            new DefaultParameterDescriptor("inFeatureTypeName", IN_FEATURE_NAME_PARAM_REMARKS, String.class, null, false);

    /*
     * Intervals
     */
    public static final InternationalString IN_INTERVAL_PARAM_REMARKS = new ResourceInternationalString(BUNDLE_PATH, "isoline.inIntervals");
    public static final ParameterDescriptor<double[]> INTERVALS =
            new DefaultParameterDescriptor("inIntervals", IN_INTERVAL_PARAM_REMARKS, double[].class, null, true);

     /**Input parameters */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new DefaultParameterDescriptorGroup("InputParameters",
            new GeneralParameterDescriptor[]{COVERAGE_REF, FEATURE_STORE, FEATURE_NAME, INTERVALS});

    /*
     * FeatureCollection of isoline
     */
    public static final InternationalString OUT_FCOLL_PARAM_REMARKS = new ResourceInternationalString(BUNDLE_PATH, "isoline.outFeatureCollection");
    public static final ParameterDescriptor<FeatureCollection> FCOLL =
            new DefaultParameterDescriptor("outFeatureCollection", OUT_FCOLL_PARAM_REMARKS, FeatureCollection.class, null, true);

    /**Output parameters */
    public static final ParameterDescriptorGroup OUTPUT_DESC = new DefaultParameterDescriptorGroup("OutputParameters",
            new GeneralParameterDescriptor[]{FCOLL});


    public static final ProcessDescriptor INSTANCE = new IsolineDescriptor2();

    private IsolineDescriptor2() {
        super(NAME, CoverageProcessingRegistry.IDENTIFICATION, abs, INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess(final ParameterValueGroup input) {
       return new Isoline2(INSTANCE, input);
    }

}