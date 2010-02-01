/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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


package org.geotoolkit.feature.xml.jaxb;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

/**
 * A JAXB Validation Handler allowing not to lost the Exception throw by the CoordinateReferenceSystemAdapter
 * When a GML geometry srsName is unknow.
 * 
 * @author Guilhem Legal (Geomatys)
 */
public class JAXBEventHandler implements ValidationEventHandler {

    @Override
    public boolean handleEvent(ValidationEvent ve) {
        if (ve.getSeverity() == ve.FATAL_ERROR || ve.getSeverity() == ve.ERROR) {
            if (ve.getMessage() != null && ve.getMessage().startsWith("org.opengis.referencing.NoSuchAuthorityCodeException")) {
                return false;
            }
        }
        return true;
    }
}
