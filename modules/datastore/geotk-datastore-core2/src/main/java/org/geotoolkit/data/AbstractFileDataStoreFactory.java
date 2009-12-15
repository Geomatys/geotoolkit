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

package org.geotoolkit.data;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;

import org.geotoolkit.parameter.DefaultParameterDescriptor;

import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractFileDataStoreFactory extends AbstractDataStoreFactory implements FileDataStoreFactory{

    /**
     * url to the file.
     */
    public static final GeneralParameterDescriptor URLP =
            new DefaultParameterDescriptor("url","url to a file",URL.class,null,true);

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean canProcess(ParameterValueGroup params) {
        boolean valid = super.canProcess(params);

        if(valid){
            final Object obj = params.parameter(URLP.getName().toString()).getValue();
            if(obj != null && obj instanceof URL){
                final String path = ((URL)obj).toString().toLowerCase();
                for(final String ext : getFileExtensions()){
                    if(path.endsWith(ext)){
                        return true;
                    }
                }
                return false;
            }else{
                return false;
            }
        }else{
            return false;
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean canProcess(URL url) {
        return canProcess(Collections.singletonMap(URLP.getName().toString(), url));
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public DataStore createDataStore(URL url) throws IOException {
        return createDataStore(Collections.singletonMap(URLP.getName().toString(), url));
    }

}
