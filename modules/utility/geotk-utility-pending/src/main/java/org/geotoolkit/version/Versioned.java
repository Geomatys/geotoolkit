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

/**
 * Any object which handle versioning should implement this interface.
 * It allows to navigate on the different versions of the object.
 * 
 * @author Johann Sorel (Geomatys)
 */
public interface Versioned<T> {
    
    /**
     * Get the history of this object.
     * 
     * @return VersionHistory, never null, sorted from oldest to newest.
     * @throws VersioningException if failed to retrieve history.
     */
    VersionHistory getHistory() throws VersioningException;
    
    /**
     * Get object at specified version.
     * 
     * @param version get object for given version date
     * @return T object at given version
     * @throws VersioningException if failed to retrieve object.
     */
    T getForVersion(Date date) throws VersioningException;
    
    /**
     * Get object at specified version.
     * 
     * @param version get object for given version label
     * @return T object at given version
     * @throws VersioningException if failed to retrieve object.
     */
    T getForVersion(String versionLabel) throws VersioningException;
    
    /**
     * Get object at specified version.
     * 
     * @param version get object for given version
     * @return T object at given version
     * @throws VersioningException if failed to retrieve object.
     */
    T getForVersion(Version version) throws VersioningException;
    
}
