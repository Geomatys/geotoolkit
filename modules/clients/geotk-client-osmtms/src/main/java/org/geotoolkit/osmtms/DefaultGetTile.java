/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.osmtms;

import java.util.logging.Logger;

import org.geotoolkit.client.AbstractRequest;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.util.logging.Logging;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultGetTile extends AbstractRequest implements GetTileRequest {
    
    /**
     * Default logger for all GetMap requests.
     */
    protected static final Logger LOGGER = Logging.getLogger(DefaultGetTile.class);

    /**
     * The version to use for this webservice request.
     */
    private int scale = 0;
    private int row = 0;
    private int col = 0;
    private String extension = ".png";


    /**
     * Defines the server url for this request.
     *
     * @param serverURL The server url.
     */
    protected DefaultGetTile(final String serverURL, final ClientSecurity security){
        super(serverURL,security,null);
    }

    @Override
    protected String getSubPath() {
        final StringBuilder sb = new StringBuilder();
        final String baseSub = super.getSubPath();
        if(baseSub != null){
            sb.append(baseSub);
        }
        sb.append('/').append(scale).append('/').append(col).append('/').append(row).append(extension);
        return sb.toString();
    }

    
    @Override
    public int getScaleLevel() {
        return scale;
    }

    @Override
    public void setScaleLevel(final int level) {
        this.scale = level;
    }

    @Override
    public int getTileRow() {
        return row;
    }

    @Override
    public void setTileRow(final int row) {
        this.row = row;
    }

    @Override
    public int getTileCol() {
        return col;
    }

    @Override
    public void setTileCol(final int col) {
        this.col = col;
    }

    @Override
    public String getExtension() {
        return extension;
    }

    @Override
    public void setExtension(final String ext) {
        this.extension = ext;
    }

}
