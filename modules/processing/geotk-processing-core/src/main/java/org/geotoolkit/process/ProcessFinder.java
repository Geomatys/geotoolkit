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

import java.util.Iterator;
import javax.imageio.spi.ServiceRegistry;
import org.opengis.metadata.Identifier;

/**
 * Utility class to find Process factories and descriptors.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class ProcessFinder {

    private ProcessFinder(){}

    public static Iterator<ProcessFactory> getProcessFactories(){
        return ServiceRegistry.lookupProviders(ProcessFactory.class);
    }

    public static ProcessDescriptor getProcessDescriptor(String authority, String processName){
        final Iterator<ProcessFactory> factories = getProcessFactories();
        while(factories.hasNext()){
            final ProcessFactory factory = factories.next();
            for(final Identifier id : factory.getIdentification().getCitation().getIdentifiers()){
                if(id.getCode().equalsIgnoreCase(authority)){
                    try{
                        return factory.getDescriptor(processName);
                    }catch(IllegalArgumentException ex){}
                }
            }
        }
        return null;
    }

}
