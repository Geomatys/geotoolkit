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
package org.geotoolkit.process.metadata.merge;

import java.util.Map;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.metadata.MetadataStandard;
import org.apache.sis.metadata.KeyNamePolicy;
import org.apache.sis.metadata.ValueExistencePolicy;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.ProcessException;
import org.opengis.metadata.Metadata;
import org.opengis.parameter.ParameterValueGroup;

import static org.geotoolkit.process.metadata.merge.MergeDescriptor.*;
import static org.geotoolkit.parameter.Parameters.*;


/**
 * Merge two metadata objects.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class Merge extends AbstractProcess {

    public Merge(final ParameterValueGroup input) {
        super(INSTANCE,input);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    protected void execute() throws ProcessException {

        fireProcessStarted("Start merge");

        final Metadata first = (Metadata) getOrCreate(FIRST_IN, inputParameters).getValue();
        final Metadata second = (Metadata) getOrCreate(SECOND_IN, inputParameters).getValue();

        final DefaultMetadata merged = new DefaultMetadata(first);
        final MetadataStandard standard = merged.getStandard();
        final Map<String, Object> source = standard.asValueMap(second, KeyNamePolicy.JAVABEANS_PROPERTY, ValueExistencePolicy.NON_EMPTY);
        final Map<String, Object> target = standard.asValueMap(merged, KeyNamePolicy.JAVABEANS_PROPERTY, ValueExistencePolicy.ALL);
        target.putAll(source);

        getOrCreate(RESULT_OUT, outputParameters).setValue(merged);

        fireProcessCompleted("Merge done.");
    }

}
