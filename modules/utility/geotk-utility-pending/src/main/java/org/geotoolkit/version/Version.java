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
import org.apache.sis.util.ArgumentChecks;

/**
 * A version, used by data management apis.
 * Composed of a date an a unique label within it's VersionHistory.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class Version {
    
    protected final VersionHistory history;
    protected final String label;
    protected final Date date;

    public Version(VersionHistory history, String label, Date date) {
        ArgumentChecks.ensureNonNull("history", history);
        ArgumentChecks.ensureNonNull("label", label);
        ArgumentChecks.ensureNonNull("date", date);
        this.history = history;
        this.label = label;
        this.date = date;
    }
    
    /**
     * Get the history containing this version.
     * @return VersionHistory
     */
    public VersionHistory getHistory() {
        return history;
    }

    /**
     * Version label. unique in the version history.
     * @return String, never null.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Version effective date, inclusive.
     * @return Date, in GMT+0
     */
    public Date getDate() {
        return (Date)date.clone();
    }

    @Override
    public String toString() {
        return label+":"+date;
    }
    
}
