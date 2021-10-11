/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.tms;

import java.util.logging.Logger;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.client.AbstractRequest;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class DefaultGetTile extends AbstractRequest implements GetTileRequest {

    /**
     * Default logger for all GetTile requests.
     */
    protected static final Logger LOGGER = Logging.getLogger("org.geotoolkit.tms");

    private String pattern = "{z}/{x}/{y}.png";
    private int scale = 0;
    private long row = 0;
    private long col = 0;


    /**
     * Defines the server url for this request.
     *
     * @param serverURL The server url.
     */
    protected DefaultGetTile(final TileMapClient server){
        super(server);
    }

    @Override
    protected String getSubPath() {
        final StringBuilder sb = new StringBuilder();
        final String baseSub = super.getSubPath();
        if (baseSub != null) {
            sb.append(baseSub);
        }

        String part = pattern.replace("{z}", ""+scale).replace("{x}", ""+col).replace("{y}", ""+row);

        sb.append('/').append(part);
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
    public long getTileRow() {
        return row;
    }

    @Override
    public void setTileRow(final long row) {
        this.row = row;
    }

    @Override
    public long getTileCol() {
        return col;
    }

    @Override
    public void setTileCol(final long col) {
        this.col = col;
    }

    @Override
    public String getPattern() {
        return pattern;
    }

    @Override
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

}
