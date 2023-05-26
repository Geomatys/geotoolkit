/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2023, Geomatys
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
package org.geotoolkit.processing.vector;

import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureQuery;
import org.apache.sis.storage.FeatureSet;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcess;
import static org.geotoolkit.processing.vector.VectorDescriptor.FEATURESET_IN;
import static org.geotoolkit.processing.vector.VectorDescriptor.FEATURESET_OUT;
import org.opengis.filter.Filter;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * A process which parameters can be converted to a FeatureQuery
 *
 * @author Quentin Boileau (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public abstract class QueryProcess extends AbstractProcess {

    public QueryProcess(ProcessDescriptor desc, ParameterValueGroup input) {
        super(desc, input);
    }

    protected abstract FeatureQuery buildQuery();

    @Override
    protected void execute() throws ProcessException {
        final FeatureSet in   = inputParameters.getValue(VectorDescriptor.FEATURESET_IN);
        try {
            outputParameters.getOrCreate(VectorDescriptor.FEATURESET_OUT).setValue(in.subset(buildQuery()));
        } catch (DataStoreException ex) {
            throw new ProcessException(ex.getMessage(), this, ex);
        }
    }

    /**
     * Start FeatureSet at given offset
     */
    public static final class Offset extends QueryProcess {

        /**
         * Process name : startoffset
         */
        public static final String NAME = "vector:startoffset";
        /**
         * Mandatory - Start offset iteration on the FeatureCollection
         */
        public static final ParameterDescriptor<Integer> OFFSET_IN = new ParameterBuilder()
                .addName("offset_in")
                .setRemarks("Start offset iteration on the FeatureCollection")
                .setRequired(true)
                .create(Integer.class, null);

        /**
         * Input Parameters
         */
        public static final ParameterDescriptorGroup INPUT_DESC
                = new ParameterBuilder().addName("InputParameters").createGroup(FEATURESET_IN, OFFSET_IN);

        /**
         * Ouput Parameters
         */
        public static final ParameterDescriptorGroup OUTPUT_DESC
                = new ParameterBuilder().addName("OutputParameters").createGroup(FEATURESET_OUT);

        /**
         * Instance
         */
        public static final ProcessDescriptor DESCRIPTOR = new Descriptor();

        /**
         * Default constructor
         */
        public Offset(final ParameterValueGroup input) {
            super(DESCRIPTOR, input);
        }

        @Override
        protected FeatureQuery buildQuery() {
            final int offset = inputParameters.getValue(OFFSET_IN);
            final FeatureQuery query = new FeatureQuery();
            query.setOffset(offset);
            return query;
        }

        public static final class Descriptor extends VectorDescriptor {

            private Descriptor() {
                super(NAME, "Start FeatureSet iteration at given offset", INPUT_DESC, OUTPUT_DESC);
            }

            @Override
            public org.geotoolkit.process.Process createProcess(final ParameterValueGroup input) {
                return new Offset(input);
            }
        }
    }

    /**
     * Sort a FeatureSet.
     */
    public static class SortBy extends QueryProcess {

        /**
         * Process name : sortby
         */
        public static final String NAME = "vector:sortby";
        /**
         * Mandatory - Array of SortBy used give the order
         */
        public static final ParameterDescriptor<org.opengis.filter.SortProperty[]> SORTER_IN = new ParameterBuilder()
                .addName("sorter_in")
                .setRemarks("Array of SortBy used give the order")
                .setRequired(true)
                .create(org.opengis.filter.SortProperty[].class, null);

        /**
         * Input Parameters
         */
        public static final ParameterDescriptorGroup INPUT_DESC
                = new ParameterBuilder().addName("InputParameters").createGroup(FEATURESET_IN, SORTER_IN);

        /**
         * Ouput Parameters
         */
        public static final ParameterDescriptorGroup OUTPUT_DESC
                = new ParameterBuilder().addName("OutputParameters").createGroup(FEATURESET_OUT);

        public static final ProcessDescriptor DESCRIPTOR = new Descriptor();

        /**
         * Default constructor
         */
        public SortBy(final ParameterValueGroup input) {
            super(DESCRIPTOR, input);
        }

        @Override
        protected FeatureQuery buildQuery() {
            final org.opengis.filter.SortProperty[] sorter = inputParameters.getValue(SORTER_IN);
            final FeatureQuery query = new FeatureQuery();
            query.setSortBy(sorter);
            return query;
        }

        /**
         * Parameter description of SortBy process. name of the process : "sortby"
         * inputs :
         * <ul>
         * <li>FEATURE_IN "feature_in" FeatureSet </li>
         * <li>SORTER_IN "sorter_in" Array of SortBy used give the order</li>
         * </ul>
         * outputs :
         * <ul>
         * <li>FEATURE_OUT "feature_out" FeatureSet</li>
         * </ul>
         */
        public static final class Descriptor extends VectorDescriptor {

            private Descriptor() {
                super(NAME, "Sort a FeatureCollection", INPUT_DESC, OUTPUT_DESC);
            }

            @Override
            public org.geotoolkit.process.Process createProcess(final ParameterValueGroup input) {
                return new SortBy(input);
            }
        }
    }

    /**
     * Limit a FeatureSet returns to a maximum.
     */
    public static class Limit extends QueryProcess {

        /**
         * Process name : maxlimit
         */
        public static final String NAME = "vector:maxlimit";

        /**
         * Mandatory - Number maximum of Feature
         */
        public static final ParameterDescriptor<Integer> MAX_IN = new ParameterBuilder()
                .addName("max_in")
                .setRemarks("Number maximum of Feature")
                .setRequired(true)
                .create(Integer.class, null);

        /**
         * Input Parameters
         */
        public static final ParameterDescriptorGroup INPUT_DESC
                = new ParameterBuilder().addName("InputParameters").createGroup(FEATURESET_IN, MAX_IN);

        /**
         * Ouput Parameters
         */
        public static final ParameterDescriptorGroup OUTPUT_DESC
                = new ParameterBuilder().addName("OutputParameters").createGroup(FEATURESET_OUT);

        public static final ProcessDescriptor DESCRIPTOR = new Descriptor();

        public Limit(final ParameterValueGroup input) {
            super(DESCRIPTOR, input);
        }

        @Override
        protected FeatureQuery buildQuery() {
            final int max = inputParameters.getValue(MAX_IN);
            final FeatureQuery query = new FeatureQuery();
            query.setLimit(max);
            return query;
        }

        /**
         * Parameter description of MaxLimit process. name of the process :
         * "maxlimit" inputs :
         * <ul>
         * <li>FEATURE_IN "feature_in" FeatureSet </li>
         * <li>MAX_IN "max_in" Number maximum of Feature</li>
         * </ul>
         * outputs :
         * <ul>
         * <li>FEATURE_OUT "feature_out" FeatureSet</li>
         * </ul>
         */
        public static final class Descriptor extends VectorDescriptor {

            private Descriptor() {
                super(NAME, "Limit a FeatureCollection returns to a maximum", INPUT_DESC, OUTPUT_DESC);
            }

            /**
             * {@inheritDoc }
             */
            @Override
            public org.geotoolkit.process.Process createProcess(final ParameterValueGroup input) {
                return new Limit(input);
            }
        }
    }

    /**
     * Filter a FeatureSet.
     */
    public static class Selection extends QueryProcess {

        /**
         * Process name : filter
         */
        public static final String NAME = "vector:filter";

        /**
         * Mandatory - Filter
         */
        public static final ParameterDescriptor<Filter> FILTER_IN = new ParameterBuilder()
                .addName("filter_in")
                .setRemarks("Filter")
                .setRequired(true)
                .create(org.opengis.filter.Filter.class, null);

        /**
         * Input Parameters
         */
        public static final ParameterDescriptorGroup INPUT_DESC
                = new ParameterBuilder().addName("InputParameters").createGroup(FEATURESET_IN, FILTER_IN);

        /**
         * Ouput Parameters
         */
        public static final ParameterDescriptorGroup OUTPUT_DESC
                = new ParameterBuilder().addName("OutputParameters").createGroup(FEATURESET_OUT);

        public static final ProcessDescriptor DESCRIPTOR = new Descriptor();

        public Selection(final ParameterValueGroup input) {
            super(DESCRIPTOR, input);
        }

        @Override
        protected FeatureQuery buildQuery() {
            final org.opengis.filter.Filter filter = inputParameters.getValue(FILTER_IN);
            final FeatureQuery query = new FeatureQuery();
            query.setSelection(filter);
            return query;
        }

        /**
         * Parameter description of Filter process. name of the process :
         * "filter" inputs :
         * <ul>
         * <li>FEATURE_IN "feature_in" FeatureSet </li>
         * <li>FILTER_IN "filter_in" FeatureExtend</li>
         * </ul>
         * outputs :
         * <ul>
         * <li>FEATURE_OUT "feature_out" FeatureSet</li>
         * </ul>
         */
        public static final class Descriptor extends VectorDescriptor {

            private Descriptor() {
                super(NAME, "Apply a filter to a FeatureCollection", INPUT_DESC, OUTPUT_DESC);
            }

            @Override
            public org.geotoolkit.process.Process createProcess(final ParameterValueGroup input) {
                return new Selection(input);
            }
        }
    }
}
