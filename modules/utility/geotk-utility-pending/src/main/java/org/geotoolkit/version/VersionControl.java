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
 * Extends the version history, adding management methods.
 * 
 * @author Johann Sorel (Geomatys)
 */
public interface VersionControl extends VersionHistory {
    
    /**
     * Test if the object versioning mechanic is present.
     * If false modifications of the object won't be historized.
     * Use methods install and uninstall to respectivly start versioning and 
     * drop the all history.
     * 
     * @return true if the versioning is enable.
     */
    boolean isVersioned() throws VersioningException;
    
    /**
     * Install versioning mechanism if not installed.
     * 
     * @throws VersioningException 
     */
    void startVersioning() throws VersioningException;
    
    /**
     * Uninstall versioning mechanism, drops history.
     * 
     * @throws VersioningException 
     */
    void dropVersioning() throws VersioningException;
    
    /**
     * If versioning is not automatic then it is the user work to properly
     * call create/drop version to add or remove versions.
     * 
     * If versioning is automatic then create/drop version methods will raise a
     * VersioningException
     * 
     * @return true if versioning is automatic.
     */
    boolean isAutomatic();
    
    /**
     * Check if this history allows version modifications (trim and rollback).
     * Versions should be created automaticaly through storage writers.
     * @return true if history clipping is supported.
     */
    boolean isEditable();
    
    /**
     * Create a new version for given date.
     * @param date new version date
     * @return Version
     * @throws VersioningException 
     */
    Version createVersion(Date date) throws VersioningException;
    
    /**
     * Drop given version.
     * @param version version to delete
     * @throws VersioningException 
     */
    void dropVersion(Version version) throws VersioningException;
    
    /**
     * Remove oldest history until given date exclusive.
     * @param version not null
     */
    void trim(Date date) throws VersioningException;
    
    /**
     * Remove oldest version history until given version exclusive.
     * @param version not null
     */
    void trim(Version version) throws VersioningException;
    
    /**
     * Rollback datas until given date exclusive.
     * @param version not null
     */
    void revert(Date date) throws VersioningException;
    
    /**
     * Rollback datas until given version exclusive.
     * @param version not null
     */
    void revert(Version version) throws VersioningException;
}
