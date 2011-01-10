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
 * @moduel pending
 */
public final class ProcessFinder {

    private ProcessFinder(){}

    /**
     * @return Iterator of all available ProcessFactory.
     */
    public static Iterator<ProcessFactory> getProcessFactories(){
        return ServiceRegistry.lookupProviders(ProcessFactory.class);
    }

    /**
     * Return the factory for the given authority code.
     */
    public static ProcessFactory getProcessFactory(final String authorityCode){
        final Iterator<ProcessFactory> ite = getProcessFactories();
        while(ite.hasNext()){
            final ProcessFactory candidate = ite.next();
            for(final Identifier id : candidate.getIdentification().getCitation().getIdentifiers()){
                if(id.getCode().equalsIgnoreCase(authorityCode)){
                    return candidate;
                }
            }
        }
        return null;
    }

    /**
     * Search for a Process descriptor in the given authority and the given name.
     * 
     * @param authority
     * @param processName
     * @return ProcessDescriptor
     * @throws IllegalArgumentException if description could not be found
     */
    public static ProcessDescriptor getProcessDescriptor(String authority, 
            final String processName) throws IllegalArgumentException{
        if(authority != null && authority.trim().isEmpty()){
            authority = null;
        }

        if(authority != null){
            final ProcessFactory factory = getProcessFactory(authority);
            if(factory != null){
                return factory.getDescriptor(processName);
            }else{
                throw new IllegalArgumentException("No authority for code : " +authority);
            }
        }

        //try all factories
        final Iterator<ProcessFactory> factories = getProcessFactories();
        while(factories.hasNext()){
            final ProcessFactory factory = factories.next();
            try{
                return factory.getDescriptor(processName);
            }catch(IllegalArgumentException ex){}
        }

        throw new IllegalArgumentException("No process for code : " +processName);
    }

}
