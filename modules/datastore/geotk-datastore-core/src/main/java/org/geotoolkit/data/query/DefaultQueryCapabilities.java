/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.data.query;

import org.geotoolkit.util.XArrays;

/**
 * Default query capabilities implementation.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultQueryCapabilities implements QueryCapabilities{

    private final boolean crossQuery;
    private final String[] supportedLanguages;

    public DefaultQueryCapabilities(final boolean crossQuery) {
        this(crossQuery, new String[]{Query.GEOTK_QOM});
    }

    public DefaultQueryCapabilities(final boolean crossQuery, final String[] languages) {
        this.crossQuery = crossQuery;

        if(languages == null){
            this.supportedLanguages = new String[]{Query.GEOTK_QOM};
        }else{
            if(!XArrays.contains(languages, Query.GEOTK_QOM)){
                throw new IllegalArgumentException("Supported languages must at least contain GEOTK_QOM.");
            }
            this.supportedLanguages = languages.clone();
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean handleCrossQuery(){
        return crossQuery;
    }

    @Override
    public String[] getSupportedQueryLanguages() {
        return supportedLanguages;
    }

}
