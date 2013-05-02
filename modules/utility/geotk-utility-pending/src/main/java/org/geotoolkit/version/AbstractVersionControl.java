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
 * Abstract Version control, override methods to support edition.
 * 
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractVersionControl extends AbstractVersionHistory implements VersionControl {

    /**
     * Overwrite to enable versioning edition.
     * @return false
     * @throws VersioningException 
     */
    @Override
    public boolean isEditable() {
        return false;
    }
    
    /**
     * Overwrite to enable automatic edition.
     * @return false
     * @throws VersioningException 
     */
    @Override
    public boolean isAutomatic(){
        return false;
    }

    /**
     * Overwrite to enable manual edition.
     * @return false
     * @throws VersioningException 
     */
    @Override
    public Version createVersion(Date date) throws VersioningException {
        throw new VersioningException("Versioning edition not supported.");
    }

    /**
     * Overwrite to enable manual edition.
     * @return false
     * @throws VersioningException 
     */
    @Override
    public void dropVersion(Version version) throws VersioningException {
        throw new VersioningException("Versioning edition not supported.");
    }
    
    /**
     * Overwrite to enable versioning edition.
     * @param version 
     * @throws VersioningException 
     */
    @Override
    public void trim(Version version) throws VersioningException {
        trim(version.getDate());
    }
    
    /**
     * Overwrite to enable versioning edition.
     * @param version 
     * @throws VersioningException 
     */
    @Override
    public void trim(Date date) throws VersioningException {
        throw new VersioningException("Versioning edition not supported.");
    }

    /**
     * Overwrite to enable versioning edition.
     * @param version 
     * @throws VersioningException 
     */
    @Override
    public void revert(Version version) throws VersioningException {
        revert(version.getDate());
    }
    
    /**
     * Overwrite to enable versioning edition.
     * @param version 
     * @throws VersioningException 
     */
    @Override
    public void revert(Date date) throws VersioningException {
        throw new VersioningException("Versioning edition not supported.");
    }
    
    /**
     * Overwrite to enable versioning edition.
     * @throws VersioningException 
     */
    @Override
    public void startVersioning() throws VersioningException {
        throw new VersioningException("Versioning edition not supported.");
    }

    /**
     * Overwrite to enable versioning edition.
     * @throws VersioningException 
     */
    @Override
    public void dropVersioning() throws VersioningException {
        throw new VersioningException("Versioning edition not supported.");
    }
    
}
