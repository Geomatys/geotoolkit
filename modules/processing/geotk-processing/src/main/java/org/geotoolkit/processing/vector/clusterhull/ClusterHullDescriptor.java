package org.geotoolkit.processing.vector.clusterhull;

import org.apache.sis.measure.Units;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.processing.GeotkProcessingRegistry;

import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

import javax.measure.Unit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Parameters description of ClusterHull process.
 * name of the process : "clusterhull"
 * inputs :
 * <ul>
 *     <li>FEATURE_SET_IN "feature_set_in" FeatureCollection source</li>
 *     <li>TOLERANCE_VALUE "tolerance value"  Measure of tolerance used to compute cluster hull</li>
 *     <li>TOLERANCE_UNIT "tolerance unit"  unit of tolerance used</li>
 * </ul>
 * outputs :
 * <ul>
 *     <li>FEATURE_SET_OUT "feature_set_out" footprint Geometry</li>
 * </ul>
 * @author Maxime Gavens
 * @module
 */
public final class ClusterHullDescriptor extends AbstractProcessDescriptor {

    /**Process name : clusterhull */
    public static final String NAME = "vector:clusterhull";

    /**
     * Mandatory - Feature Set
     */
    public static final ParameterDescriptor<FeatureSet> FEATURE_SET_IN = new ParameterBuilder()
            .addName("feature_set_in")
            .setRemarks("Input Feature Set")
            .setRequired(true)
            .create(FeatureSet.class, null);
    /**
     * Mandatory - Measure of tolerance used to define cluster hull
     */
    public static final ParameterDescriptor<Double> TOLERANCE_VALUE = new ParameterBuilder()
            .addName("tolerance_value")
            .setRemarks("Measure of tolerance used")
            .setRequired(true)
            .create(Double.class, null);

    /**
     * Non Mandatory - Unit of tolerance (METER, KILOMETER, STATUTE MILE, NAUTICAL MILE, INCH) default value initialize at METER
     */
    static final Unit[] ALLOWED_UNITS = {Units.METRE, Units.KILOMETRE, Units.STATUTE_MILE, Units.NAUTICAL_MILE, Units.INCH};
    public static final ParameterDescriptor<Unit> TOLERANCE_UNIT = new ParameterBuilder()
            .addName("tolerance_unit")
            .setRemarks("Unit of tolerance used")
            .setRequired(false)
            .createEnumerated(Unit.class, ALLOWED_UNITS, Units.METRE);

    /**
     * Non Mandatory - the return value
     */
    public static final ParameterDescriptor<FeatureSet> FEATURE_SET_OUT = new ParameterBuilder()
            .addName("feature_set_out")
            .setRemarks("Cluster hull feature set")
            .setRequired(false)
            .create(FeatureSet.class, null);

    /** Input Parameters */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName("InputParameters").createGroup(FEATURE_SET_IN, TOLERANCE_VALUE, TOLERANCE_UNIT);
    /** Ouput Parameters */
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName("OutputParameters").createGroup(FEATURE_SET_OUT);
    /** Instance */
    public static final ProcessDescriptor INSTANCE = new ClusterHullDescriptor();

    /**
     * Default constructor
     */
    protected ClusterHullDescriptor() {

        super(NAME, GeotkProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Return the cluster hull based on FeatureCollection geometries"),
                INPUT_DESC, OUTPUT_DESC);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new ClusterHullProcess(input);
    }
}
