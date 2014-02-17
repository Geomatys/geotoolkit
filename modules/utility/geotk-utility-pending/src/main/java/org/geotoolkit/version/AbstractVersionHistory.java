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

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Abstract version history,
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractVersionHistory implements VersionHistory {

    private static final TimeZone GMT0 = TimeZone.getTimeZone("GMT+0");
    
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
        Collections.sort(lst, new VersionComparator());
        
        //ensure date in GMT0
        final Calendar vCal = new GregorianCalendar(GMT0);
        final Calendar rCal = new GregorianCalendar(GMT0);
        rCal.setTime(date);
        
        for(Version v : lst){
            vCal.setTime(v.getDate());
            //if requested date is after version date
            if(rCal.getTimeInMillis() >= vCal.getTimeInMillis()) {
                return v;
            }
        }
        //date is before. return first version
        //this is not exact, yet versioning might have started a very long time afeter initial data creation.
        return lst.isEmpty() ? null : lst.get(0);
    }
  
    /**
     * Comparator to sort Version list in reverted chronological order.
     */
    public class VersionComparator implements Comparator<Version> {

        @Override
        public int compare(Version o1, Version o2) {
            return o2.getDate().compareTo(o1.getDate());
        }
    }
}
