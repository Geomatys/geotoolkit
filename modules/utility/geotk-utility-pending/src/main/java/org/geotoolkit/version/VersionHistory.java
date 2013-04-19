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
 * Versioning api. allows to list and search available versions.
 * Inspired by JCR 2 part 15.
 * 
 * @author Johann Sorel (Geomatys)
 */
public interface VersionHistory {
    
    /**
     * Check if this history allows version modifications (trim and rollback).
     * Versions should be created automaticaly through storage writers.
     * @return true if history clipping is supported.
     */
    boolean isEditable();
    
    /**
     * Unmodifiable list of versions available.
     * @return List<Version>
     */
    List<Version> list() throws VersioningException;
    
    /**
     * Find version for given label.
     * @param label
     * @return Version or null
     */
    Version getVersion(String label) throws VersioningException;
    
    /**
     * Returns the version for for given date.
     * In case the date do not overlaps the closest version will be returned.
     * @param date : date in GMT+0
     * @return Version
     */
    Version getVersion(Date date) throws VersioningException;
    
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
