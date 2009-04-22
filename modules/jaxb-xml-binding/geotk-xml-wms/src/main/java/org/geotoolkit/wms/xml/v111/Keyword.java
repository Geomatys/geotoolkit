/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wms.xml.v111;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import org.geotoolkit.wms.xml.AbstractKeyword;


/**
 * <p>Java class for anonymous complex type.
 * 
 * 
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "value"
})
@XmlRootElement(name = "Keyword")
public class Keyword implements AbstractKeyword{

    @XmlValue
    private String value;
    @XmlAttribute
    private String vocabulary;

   
     /**
     * An empty constructor used by JAXB.
     */
     Keyword() {
     }

     /**
     * Build a new Keyword object.
     */
    public Keyword(final String value) {
        this.value      = value;
    }
    
    /**
     * Build a new Keyword object.
     */
    public Keyword(final String value, final String vocabulary) {
        this.value      = value;
        this.vocabulary = vocabulary; 
    }
    
    /**
     * Gets the value of the value property.
     */
    @Override
    public String getValue() {
        return value;
    }

   /**
    * Gets the value of the vocabulary property.
    */
    @Override
    public String getVocabulary() {
        return vocabulary;
    }
}
