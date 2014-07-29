/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.georss.xml.v100;


import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 *
 */
@XmlRegistry
public class ObjectFactory {
    private final static QName _WhereType_QNAME = new QName("http://www.georss.org/georss", "where");

    public ObjectFactory() {
    }

    public WhereType createWhereType() {
        return new WhereType();
    }

    @XmlElementDecl(namespace = "http://www.georss.org/georss", name = "where")
    public JAXBElement<WhereType> createWhere(WhereType value) {
        return new JAXBElement<WhereType>(_WhereType_QNAME, WhereType.class, null, value);
    }
}
