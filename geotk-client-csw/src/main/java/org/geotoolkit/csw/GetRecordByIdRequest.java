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
import org.geotoolkit.csw.xml.ElementSetType;


/**
 *
 * @author Cédric Briançon (Geomatys)
 * @module
 */
public interface GetRecordByIdRequest extends Request {
    ElementSetType getElementSetName();

    void setElementSetName(ElementSetType elementSetName);

    String[] getIds();

    void setIds(String... ids);

    String getOutputFormat();

    void setOutputFormat(String outputFormat);

    String getOutputSchema();

    void setOutputSchema(String outputSchema);
}
