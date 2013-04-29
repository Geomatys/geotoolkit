/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.db.postgres;

import java.util.Collections;
import java.util.List;
import org.geotoolkit.version.AbstractVersionControl;
import org.geotoolkit.version.Version;
import org.geotoolkit.version.VersioningException;
import org.opengis.feature.type.FeatureType;

/**
 * Manage versioning for a given feature type.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class PostgresVersionControl extends AbstractVersionControl{

    private final PostgresFeatureStore featureStore;
    private final FeatureType featureType;
    private Boolean isVersioned = null;
    
    
    public PostgresVersionControl(PostgresFeatureStore featureStore, FeatureType featureType) {
        this.featureStore = featureStore;
        this.featureType = featureType;
    }
    
    @Override
    public List<Version> list() throws VersioningException {
        //TODO
        return Collections.EMPTY_LIST;
    }

    @Override
    public boolean isVersioned() throws VersioningException {
        boolean hasHS = featureStore.hasHSFunctions();
        if(!hasHS) return false;
        
        if(isVersioned!=null) return isVersioned;
        
        //search for the versioning table
        //TODO
        isVersioned = false;
        return hasHS;
        
    }
    
}
