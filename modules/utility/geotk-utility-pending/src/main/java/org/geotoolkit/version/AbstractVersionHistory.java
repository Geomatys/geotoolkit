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
package org.geotoolkit.version;

import java.util.Date;
import java.util.List;

/**
 * Abstract version history,
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractVersionHistory implements VersionHistory {

    @Override
    public Version getVersion(String label) throws VersioningException {
        for(Version v : list()){
            if(v.getLabel().equals(label)){
                return v;
            }
        }
        throw new VersioningException("No version with given label.");
    }

    @Override
    public Version getVersion(Date date) throws VersioningException {
        final List<Version> lst = list();
        for(Version v : lst){
            if(v.getDate().compareTo(date)>=0){
                return v;
            }
        }
        //date is before. return first version
        //this is not exact, yet versioning might have started a very long time afeter initial data creation.
        return lst.isEmpty() ? null : lst.get(0);
    }
  
}
