/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.swe.xml.v100;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.swe.xml.AbstractAllowedTokens;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="valueList" type="{http://www.opengis.net/swe/1.0}tokenList" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "valueList"
})
@XmlRootElement(name = "AllowedTokens")
public class AllowedTokens implements AbstractAllowedTokens {

    @XmlElementRef(name = "valueList", namespace = "http://www.opengis.net/swe/1.0", type = JAXBElement.class)
    private List<JAXBElement<List<String>>> valueList;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    private String id;

    public AllowedTokens() {
        
    }
    
    public AllowedTokens(AbstractAllowedTokens tk) {
        if (tk != null) {
            this.id = tk.getId();
            valueList = new ArrayList<JAXBElement<List<String>>>();
            ObjectFactory facto = new ObjectFactory();
            for (JAXBElement<List<String>> jbList : tk.getValueList()) {
                this.valueList.add(facto.createAllowedTokensValueList(jbList.getValue()));
            }
        }
    }
    
    /**
     * Gets the value of the valueList property.
     */
    public List<JAXBElement<List<String>>> getValueList() {
        if (valueList == null) {
            valueList = new ArrayList<JAXBElement<List<String>>>();
        }
        return this.valueList;
    }

    public void setValueList(String value) {
        if (value != null) {
            if (valueList == null) {
                valueList = new ArrayList<JAXBElement<List<String>>>();
            }
            ObjectFactory factory = new ObjectFactory();
            if (valueList.size() == 0) {
                valueList.add(factory.createAllowedTokensValueList(Arrays.asList(value)));
            } else {
                JAXBElement<List<String>> firstList = valueList.get(0);
                List<String> oldList = firstList.getValue();
                valueList.remove(0);
                List<String> newList = new ArrayList<String>();
                for (String s: oldList) {
                    newList.add(s);
                }
                newList.add(value);
                valueList.add(0, factory.createAllowedTokensValueList(newList));
            }
        }
    }

    public void setValueList(List<String> value) {
        if (value != null) {
            if (valueList == null) {
                valueList = new ArrayList<JAXBElement<List<String>>>();
            }
            ObjectFactory factory = new ObjectFactory();
            valueList.add(factory.createAllowedTokensValueList(value));
        }
    }

    /**
     * Gets the value of the id property.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     */
    public void setId(String value) {
        this.id = value;
    }

}
