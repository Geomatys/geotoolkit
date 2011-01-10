/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.data.osm.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import org.geotoolkit.client.AbstractRequest;

/**
 * Abstract implementation of {@link CloseChangeSetRequest}, which defines the
 * parameters for a close change set request.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractCloseChangeSet extends AbstractRequest implements CloseChangeSetRequest{

    protected int id = -1;

    public AbstractCloseChangeSet(final String serverURL, final String subPath){
        super(serverURL, subPath);
    }

    @Override
    public int getChangeSetID() {
        return id;
    }

    @Override
    public void setChangeSetID(final int id) {
        this.id = id;
    }

    @Override
    public InputStream getResponseStream() throws IOException {
        final URLConnection conec = getURL().openConnection();
        final HttpURLConnection ht = (HttpURLConnection) conec;
        ht.setRequestMethod("PUT");
        return openRichException(conec);
    }
    
}
