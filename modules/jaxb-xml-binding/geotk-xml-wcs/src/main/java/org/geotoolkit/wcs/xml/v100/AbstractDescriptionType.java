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
package org.geotoolkit.wcs.xml.v100;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * Human-readable descriptive information for the object it is included within.
 * 
 * WCS version 1.0.0
 * 
 * <p>Java class for AbstractDescriptionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractDescriptionType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wcs}AbstractDescriptionBaseType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/wcs}description" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wcs}name"/>
 *         &lt;element name="label" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractDescriptionType", propOrder = {
    "rest"
})
@XmlSeeAlso({
    CoverageOfferingBriefType.class,
    ServiceType.class
})
public abstract class AbstractDescriptionType extends AbstractDescriptionBaseType {

    @XmlElementRefs({
        @XmlElementRef(name = "label",       namespace = "http://www.opengis.net/wcs", type = JAXBElement.class),
        @XmlElementRef(name = "name",        namespace = "http://www.opengis.net/wcs", type = JAXBElement.class),
        @XmlElementRef(name = "description", namespace = "http://www.opengis.net/wcs", type = JAXBElement.class)
    })
    private List<JAXBElement<String>> rest = new ArrayList<JAXBElement<String>>();

    /**
     * Empty constructor used by JAXB.
     */
    AbstractDescriptionType(){
    }
    
    /**
     * Build a new description.
     */
    public AbstractDescriptionType(List<MetadataLinkType> metadataLink, String name, String label, String description){
        super(metadataLink);
        ObjectFactory factory = new ObjectFactory();
        if (description!= null)
            rest.add(factory.createDescription(description));
        if (name != null)
            rest.add(factory.createName(name));
        if (label != null)
            rest.add(factory.createLabel(label));
    }

    public String getLabel() {
        for (JAXBElement<String> jb : rest) {
            if ("label".equals(jb.getName().getLocalPart())) {
                return jb.getValue();
            }
        }
        return null;
    }

    public void setLabel(String label) {
        //first we erase the old one.
        for (int i = 0; i < rest.size(); i++) {
            JAXBElement<String> jb = rest.get(i);
            if ("label".equals(jb.getName().getLocalPart())) {
                rest.remove(i);
                break;
            }
        }
        if (label != null) {
            ObjectFactory factory = new ObjectFactory();
            rest.add(factory.createLabel(label));
        }
    }

    public String getName() {
        for (JAXBElement<String> jb : rest) {
            if ("name".equals(jb.getName().getLocalPart())) {
                return jb.getValue();
            }
        }
        return null;
    }

    public void setName(String name) {
        //first we erase the old one.
        for (int i = 0; i < rest.size(); i++) {
            JAXBElement<String> jb = rest.get(i);
            if ("name".equals(jb.getName().getLocalPart())) {
                rest.remove(i);
                break;
            }
        }
        if (name != null) {
            ObjectFactory factory = new ObjectFactory();
            rest.add(factory.createName(name));
        }
    }

    public String getDescription() {
        for (JAXBElement<String> jb : rest) {
            if ("description".equals(jb.getName().getLocalPart())) {
                return jb.getValue();
            }
        }
        return null;
    }

    public void setDescription(String description) {
        //first we erase the old one.
        for (int i = 0; i < rest.size(); i++) {
            JAXBElement<String> jb = rest.get(i);
            if ("description".equals(jb.getName().getLocalPart())) {
                rest.remove(i);
                break;
            }
        }
        if (description != null) {
            ObjectFactory factory = new ObjectFactory();
            rest.add(factory.createDescription(description));
        }
    }
    
    /**
     * Gets the rest of the content model. 
     * 
     */
    public List<JAXBElement<String>> getRest() {
        return Collections.unmodifiableList(rest);
    }
    
    /**
     * Add a new element to the rest list.
     */
    public void addRest(JAXBElement<String> element) {
        this.rest.add(element);
    }

}
