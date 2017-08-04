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
package org.geotoolkit.processing.metadata.merge;

import org.apache.sis.metadata.iso.DefaultMetadata;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.process.ProcessException;
import org.opengis.metadata.Metadata;
import org.opengis.parameter.ParameterValueGroup;

import org.apache.sis.internal.metadata.Merger;
import org.apache.sis.metadata.ModifiableMetadata;

import static org.geotoolkit.processing.metadata.merge.MergeDescriptor.FIRST_IN;
import static org.geotoolkit.processing.metadata.merge.MergeDescriptor.INSTANCE;
import static org.geotoolkit.processing.metadata.merge.MergeDescriptor.RESULT_OUT;
import static org.geotoolkit.processing.metadata.merge.MergeDescriptor.SECOND_IN;


/**
 * Merge two metadata objects.
 *
 * @author Johann Sorel (Geomatys)
 * @author Benjamin Garcia (Geomatys)
 * @module
 */
public class Merge extends AbstractProcess {

    public Merge(final ParameterValueGroup input) {
        super(INSTANCE, input);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void execute() throws ProcessException {

        fireProcessStarted("Start merge");

        final Metadata first = inputParameters.getValue(FIRST_IN);
        final Metadata second = inputParameters.getValue(SECOND_IN);

        final DefaultMetadata merged = new DefaultMetadata(first);
        final Merger merger = new Merger(null) {
            @Override
            protected void unmerged(ModifiableMetadata target, String propertyName, Object sourceValue, Object targetValue) {
                // Ignore (TODO: we should probably emit some kind of warnings).
            }
        };
        merger.avoidConflicts = true;
        merger.merge(second, merged);

        outputParameters.getOrCreate(RESULT_OUT).setValue(merged);

        fireProcessCompleted("Merge done.");
    }
}
