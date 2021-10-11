/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wfs.xml.v200;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.wfs.xml.DropStoredQueryResponse;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
@XmlType(name = "DropStoredQueryResponseType")
@XmlRootElement(name = "DropStoredQueryResponse", namespace= "http://www.opengis.net/wfs/2.0")
public class DropStoredQueryResponseType extends ExecutionStatusType implements DropStoredQueryResponse {

    public DropStoredQueryResponseType() {

    }

    public DropStoredQueryResponseType(final String status) {
        super(status);
    }
}
