/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Geomatys
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

import java.net.URI;
import java.util.Collections;
import java.util.Map;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.storage.DataStoreException;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public abstract class AbstractFileFeatureStoreFactory extends AbstractFeatureStoreFactory implements FileFeatureStoreFactory{

    /**
     * url to the file.
     */
    public static final ParameterDescriptor<URI> PATH = new ParameterBuilder()
            .addName("path")
            .addName(Bundle.formatInternational(Bundle.Keys.paramPathAlias))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.paramPathRemarks))
            .setRequired(true)
            .create(URI.class, null);

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean canProcess(final ParameterValueGroup params) {
        boolean valid = super.canProcess(params);

        if(valid){
            final Object obj = params.parameter(PATH.getName().toString()).getValue();
            if(obj != null && obj instanceof URI){
                final String path = ((URI)obj).toString().toLowerCase();
                for(final String ext : getFileExtensions()){
                    if(path.endsWith(ext) && !path.endsWith("*"+ext)){
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
     * @param uri
     */
    @Override
    public boolean canProcess(final URI uri) {
        return canProcess(Collections.singletonMap(PATH.getName().toString(), uri));
    }

    /**
     * {@inheritDoc }
     * @param uri
     */
    @Override
    public FeatureStore createDataStore(final URI uri) throws DataStoreException {
        FeatureStore result;
        final  Map params = Collections.singletonMap(PATH.getName().toString(), uri);
        try {
            result = (FeatureStore) open(params);
        } catch (DataStoreException e) {
            result = (FeatureStore) create(params);
        }
        return result;
    }

}
