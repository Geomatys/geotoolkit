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
     * @throws VersioningException 
     */
    void startVersioning() throws VersioningException;
    
    /**
     * 
     * @throws VersioningException 
     */
    void dropVersioning() throws VersioningException;
    
    /**
     * Check if this history allows version modifications (trim and rollback).
     * Versions should be created automaticaly through storage writers.
     * @return true if history clipping is supported.
     */
    boolean isEditable();
    
    /**
     * Remove oldest version history until given version exclusive.
     * @param version not null
     */
    void trim(Version version) throws VersioningException;
    
    /**
     * Rollback datas until given version exclusive.
     * @param version not null
     */
    void revert(Version version) throws VersioningException;
}
