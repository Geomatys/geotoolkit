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

package org.geotoolkit.process;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Abstract process factory.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractProcessFactory implements ProcessFactory {

    private final Map<String,ProcessDescriptor> descriptors = new HashMap<String,ProcessDescriptor>();

    protected AbstractProcessFactory(final ProcessDescriptor ... descs){
        for(final ProcessDescriptor desc : descs){
            final String name = desc.getName().getCode();
            descriptors.put(name, desc);
        }
    }

    @Override
    public final ProcessDescriptor getDescriptor(final String name) throws IllegalArgumentException{
        final ProcessDescriptor desc = descriptors.get(name);
        if(desc == null){
            throw new IllegalArgumentException("No process descriptor for name : "+ name);
        }else{
            return desc;
        }
    }

    @Override
    public final ProcessDescriptor[] getDescriptors() {
        final Collection<ProcessDescriptor> values = descriptors.values();
        return values.toArray(new ProcessDescriptor[values.size()]);
    }

    @Override
    public final String[] getNames() {
        final Set<String> keys = descriptors.keySet();
        return keys.toArray(new String[keys.size()]);
    }

    @Override
    public final Process create(final String name) throws IllegalArgumentException {
        return getDescriptor(name).createProcess();
    }

}
