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
package org.geotoolkit.data.s57;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import org.geotoolkit.data.s57.model.DataSetIdentification;
import org.geotoolkit.data.s57.model.S57FileReader;
import org.geotoolkit.data.s57.model.S57Object;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.version.AbstractVersionControl;

/**
 * S-57 history based on available files.
 * @author Johann Sorel (Geomatys)
 */
public class S57VersionControl extends AbstractVersionControl{

    private static final TimeZone GMT0 = TimeZone.getTimeZone("GMT+0");
    
    private final List<S57Version> versions = new ArrayList<S57Version>();
    private final List<File> files;

    @Override
    public boolean isVersioned() {
        return true;
    }
    
    /**
     * 
     * @param files ordered S-57 files .000, .001, .002 ...etc...
     */
    public S57VersionControl(final List<File> files) throws DataStoreException{
        this.files = files;
        
        final Calendar calendar = Calendar.getInstance(GMT0);
        try{
            for(File uf : files){
                final S57FileReader reader = new S57FileReader();
                try{
                    reader.setInput(uf);
                    while(reader.hasNext()){
                        S57Object obj = reader.next();
                        if(obj instanceof DataSetIdentification){
                            final DataSetIdentification dsid = (DataSetIdentification) obj;
                            final int year = Integer.valueOf(dsid.issueDate.substring(0, 4));
                            final int month = Integer.valueOf(dsid.issueDate.substring(4, 6));
                            final int day = Integer.valueOf(dsid.issueDate.substring(6, 8));
                            calendar.setTimeInMillis(0);
                            calendar.set(Calendar.YEAR, year);
                            calendar.set(Calendar.MONTH, month-1);
                            calendar.set(Calendar.DAY_OF_MONTH, day);
                            final S57Version v = new S57Version(this, dsid.issueDate, calendar.getTime(),dsid,uf);
                            versions.add(v);
                            break;
                        }
                    }
                }finally{
                    reader.dispose();
                }
            }
        }catch(IOException ex){
            throw new DataStoreException(ex);
        }
        
    }
        
    @Override
    public synchronized List list() {
        return versions;
    }
    
}
