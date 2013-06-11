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
import java.util.Date;
import org.geotoolkit.data.s57.model.DataSetIdentification;
import org.geotoolkit.version.Version;
import org.geotoolkit.version.VersionHistory;

/**
 * An S-57 version is associated to a file.
 * main file or update files.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class S57Version extends Version{

    private final DataSetIdentification dsid;
    private final File file;
    
    public S57Version(VersionHistory history, String label, Date date, DataSetIdentification dsid, File file) {
        super(history, label, date);
        this.dsid = dsid;
        this.file = file;
    }

    public DataSetIdentification getDsid() {
        return dsid;
    }

    public File getFile() {
        return file;
    }
    
}
