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
     * Unmodifiable list of versions available.
     * List is ordered by date ascending
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

}
