/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2020, Geomatys
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
package org.geotoolkit.processing.coverage.isoline;

import java.util.Arrays;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.GridCoverageResource;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import org.geotoolkit.processing.ProcessBundle;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.InternationalString;

/**
 * Create a FeatureSet of isoline from a GridCoverageResource and an array of intervals.
 *
 * @author Quentin Boileau (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public class IsolineDescriptor extends AbstractProcessDescriptor {

    public static final String NAME = "coverage:isoline";
    public static final InternationalString abs = ProcessBundle.formatInternational(ProcessBundle.Keys.coverage_isoline_abstract);

    /*
     * Coverage
     */
    public static final InternationalString IN_COVERAGE_REF_PARAM_REMARKS = ProcessBundle.formatInternational(ProcessBundle.Keys.coverage_isoline_inCoverageRef);
    public static final ParameterDescriptor<GridCoverageResource> COVERAGE_REF = new ParameterBuilder()
            .addName("inCoverageRef")
            .setRemarks(IN_COVERAGE_REF_PARAM_REMARKS)
            .setRequired(true)
            .create(GridCoverageResource.class, null);

    /*
     * Output FeatureStore
     */
    public static final InternationalString IN_FEATURE_STORE_PARAM_REMARKS = ProcessBundle.formatInternational(ProcessBundle.Keys.coverage_isoline_inFeatureStore);
    public static final ParameterDescriptor<DataStore> FEATURE_STORE = new ParameterBuilder()
            .addName("inFeatureStore")
            .setRemarks(IN_FEATURE_STORE_PARAM_REMARKS)
            .setRequired(false)
            .create(DataStore.class, null);

    /*
     * Output FeatureType name
     */
    public static final InternationalString IN_FEATURE_NAME_PARAM_REMARKS = ProcessBundle.formatInternational(ProcessBundle.Keys.coverage_isoline_inFeatureTypeName);
    public static final ParameterDescriptor<String> FEATURE_NAME = new ParameterBuilder()
            .addName("inFeatureTypeName")
            .setRemarks(IN_FEATURE_NAME_PARAM_REMARKS)
            .setRequired(false)
            .create(String.class, null);

    /*
     * Intervals
     */
    public static final InternationalString IN_INTERVAL_PARAM_REMARKS = ProcessBundle.formatInternational(ProcessBundle.Keys.coverage_isoline_inIntervals);
    public static final ParameterDescriptor<double[]> INTERVALS = new ParameterBuilder()
            .addName("inIntervals")
            .setRemarks(IN_INTERVAL_PARAM_REMARKS)
            .setRequired(true)
            .create(double[].class, null);

    public static final ParameterDescriptor<String> METHOD = new ParameterBuilder()
            .addName("method")
            .setRequired(false)
            .createEnumerated(String.class, Arrays.stream(Method.values()).map(Method::name).toArray(String[]::new), Method.SIS_MARCHING_SQUARE.name());

     /**Input parameters */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName("InputParameters").createGroup(COVERAGE_REF, FEATURE_STORE, FEATURE_NAME, INTERVALS, METHOD);

    /*
     * FeatureCollection of isoline
     */
    public static final InternationalString OUT_FCOLL_PARAM_REMARKS = ProcessBundle.formatInternational(ProcessBundle.Keys.coverage_isoline_outFeatureCollection);
    public static final ParameterDescriptor<FeatureSet> FCOLL = new ParameterBuilder()
            .addName("outFeatureCollection")
            .setRemarks(OUT_FCOLL_PARAM_REMARKS)
            .setRequired(true)
            .create(FeatureSet.class, null);

    /**Output parameters */
    public static final ParameterDescriptorGroup OUTPUT_DESC = new ParameterBuilder().addName("OutputParameters").createGroup(FCOLL);


    public static final ProcessDescriptor INSTANCE = new IsolineDescriptor();

    private IsolineDescriptor() {
        super(NAME, GeotkProcessingRegistry.IDENTIFICATION, abs, INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess(final ParameterValueGroup input) {
       return new Isoline(INSTANCE, input);
    }

    public enum Method {
        SIS_MARCHING_SQUARE,
        GEOTK_MARCHING_SQUARE
    }
}
