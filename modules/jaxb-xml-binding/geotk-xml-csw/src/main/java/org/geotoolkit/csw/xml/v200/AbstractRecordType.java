/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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
package org.geotoolkit.csw.xml.v200;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.csw.xml.AbstractRecord;


/**
 * <p>Java class for AbstractRecordType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractRecordType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractRecordType")
@XmlSeeAlso({
    BriefRecordType.class,
    SummaryRecordType.class,
    DCMIRecordType.class
})
public abstract class AbstractRecordType implements AbstractRecord {
    
    @XmlTransient
    protected static org.geotoolkit.ows.xml.v100.ObjectFactory owsFactory = new org.geotoolkit.ows.xml.v100.ObjectFactory();
    
    @XmlTransient
    protected static org.geotoolkit.dublincore.xml.v1.elements.ObjectFactory dublinFactory = new org.geotoolkit.dublincore.xml.v1.elements.ObjectFactory();
    
    @XmlTransient
    protected static org.geotoolkit.dublincore.xml.v1.terms.ObjectFactory dublinTermFactory = new org.geotoolkit.dublincore.xml.v1.terms.ObjectFactory();


}
