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

import java.util.List;
import org.geotoolkit.client.Request;
import org.geotoolkit.data.osm.model.Transaction;

/**
 * Request to upload a serie of changes.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface UploadRequest extends Request{

    String getVersion();

    void setVersion(String version);

    String getGenerator();

    void setGenerator(String generator);

    /**
     * @param id of the requested changeset to close
     */
    void setChangeSetID(int id);

    /**
     * @return id of the requested changeset
     */
    int getChangeSetID();

    /**
     * Transactions that will be send to the server.
     * @return List<Transaction>
     */
    List<Transaction> transactions();

}
