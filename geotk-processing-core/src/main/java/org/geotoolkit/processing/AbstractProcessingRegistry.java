/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

package org.geotoolkit.processing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessingRegistry;
import org.opengis.util.NoSuchIdentifierException;

/**
 * Abstract process factory.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public abstract class AbstractProcessingRegistry implements ProcessingRegistry {

    private final Map<String,ProcessDescriptor> descriptors = new HashMap<String,ProcessDescriptor>();

    protected AbstractProcessingRegistry(final ProcessDescriptor ... descs){
        for(final ProcessDescriptor desc : descs){
            final String name = desc.getIdentifier().getCode();
            descriptors.put(name, desc);
        }
    }

    @Override
    public final ProcessDescriptor getDescriptor(final String name) throws NoSuchIdentifierException{
        final ProcessDescriptor desc = descriptors.get(name);
        if(desc == null){
            throw new NoSuchIdentifierException("No process descriptor for name : "+name, name);
        }else{
            return desc;
        }
    }

    @Override
    public final List<ProcessDescriptor> getDescriptors() {
        final Collection<ProcessDescriptor> values = descriptors.values();
        return new ArrayList<ProcessDescriptor>(values);
    }

    @Override
    public final List<String> getNames() {
        final Set<String> keys = descriptors.keySet();
        return new ArrayList<String>(keys);
    }

}
