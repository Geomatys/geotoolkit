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
import org.geotoolkit.csw.xml.Delete;
import org.geotoolkit.csw.xml.Insert;
import org.geotoolkit.csw.xml.Update;


/**
 *
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public interface TransactionRequest extends Request {
    Delete getDelete();

    void setDelete(Delete delete);

    Insert getInsert();

    void setInsert(Insert insert);

    Update getUpdate();

    void setUpdate(Update update);
}
