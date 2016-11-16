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

import org.apache.sis.metadata.KeyNamePolicy;
import org.apache.sis.metadata.MetadataStandard;
import org.apache.sis.metadata.ValueExistencePolicy;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.process.ProcessException;
import org.opengis.metadata.Metadata;
import org.opengis.parameter.ParameterValueGroup;

import java.util.Collection;
import java.util.Map;

import static org.geotoolkit.parameter.Parameters.getOrCreate;
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

        final Metadata first = (Metadata) getOrCreate(FIRST_IN, inputParameters).getValue();
        final Metadata second = (Metadata) getOrCreate(SECOND_IN, inputParameters).getValue();

        final DefaultMetadata merged = new DefaultMetadata(first);
        final MetadataStandard standard = merged.getStandard();
        merge(standard, second, merged);

        getOrCreate(RESULT_OUT, outputParameters).setValue(merged);

        fireProcessCompleted("Merge done.");
    }

    /**
     * Merger recursively metadata.
     * @param standard {@link MetadataStandard} object used to find Metadata tree object.
     * @param sourceMetadata a metadata object which need to be insert on the other metadata.
     * @param targetMetadata a metadata object which receive merged metadata
     */
    private void merge(final MetadataStandard standard, final Object sourceMetadata, final Object targetMetadata) {
        //transfomr metadatas to maps
        final Map<String, Object> source = standard.asValueMap(sourceMetadata, KeyNamePolicy.JAVABEANS_PROPERTY, ValueExistencePolicy.NON_EMPTY);
        final Map<String, Object> target = standard.asValueMap(targetMetadata, KeyNamePolicy.JAVABEANS_PROPERTY, ValueExistencePolicy.ALL);

        //Iterate on sources to found object which need to be merged
        for (final Map.Entry<String, Object> entry : source.entrySet()) {
            //
            final String propertyName = entry.getKey();
            final Object sourceValue = entry.getValue();
            final Object targetValue = target.get(propertyName);

            //directly put if value is null on targer (they don't need merge)
            if (targetValue == null) {
                target.put(propertyName, sourceValue);
            } else {
                //if it's metadata object (DefaultMetadata, Extent, ...)
                if (standard.isMetadata(targetValue.getClass())) {
                    merge(standard, sourceValue, targetValue);
                } else {
                    //targetValue is a Collection
                    if(targetValue instanceof Collection){
                        Collection targetList = ((Collection) targetValue);
                        Collection sourceList = ((Collection) sourceValue);
                        //recursively merge
                        if (targetList.size() > 0) {
                            for (Object mergeElement : targetList) {
                                for (Object sourceElement : sourceList) {
                                    if (mergeElement.getClass().equals(sourceElement.getClass()) && standard.isMetadata(mergeElement.getClass())) {
                                        merge(standard, sourceElement, mergeElement);
                                    }
                                }
                            }
                        } else {
                            //list is empty on target : we add all other collection without merge
                            ((Collection) targetValue).addAll((Collection) sourceValue);
                        }
                    }
                }
            }
        }
    }
}
