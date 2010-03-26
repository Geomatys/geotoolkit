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
package org.geotoolkit.csw;

import org.geotoolkit.client.Request;
import org.geotoolkit.csw.xml.v202.DeleteType;
import org.geotoolkit.csw.xml.v202.InsertType;
import org.geotoolkit.csw.xml.v202.UpdateType;


/**
 *
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public interface TransactionRequest extends Request {
    DeleteType getDelete();

    void setDelete(DeleteType delete);

    InsertType getInsert();

    void setInsert(InsertType insert);

    UpdateType getUpdate();

    void setUpdate(UpdateType update);
}
