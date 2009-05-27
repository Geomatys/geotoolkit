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
package org.geotoolkit.wrs.xml.v090;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.csw.xml.v200.AbstractQueryType;
import org.geotoolkit.csw.xml.v200.ObjectFactory;
import org.geotoolkit.csw.xml.v200.QueryType;
import org.geotoolkit.ebrim.xml.v250.RegistryPackageType;


/**
 * Defines an expansion pack that bundles extensions for handling domain-specific resources; 
 * it may include ClassificationScheme, ClassificationNode, Association, XMLSchema, 
 * WRS query extensions (e.g. stored queries), plus constraints on the use of slots.
 * 
 * <p>Java class for ApplicationModuleType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ApplicationModuleType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}RegistryPackageType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/cat/csw}AbstractQuery" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ApplicationModuleType", propOrder = {
    "abstractQuery"
})
@XmlRootElement( name = "ApplicationModule")        
public class ApplicationModuleType extends RegistryPackageType {

    @XmlElementRef(name = "AbstractQuery", namespace = "http://www.opengis.net/cat/csw", type = JAXBElement.class)
    private List<AbstractQueryType> abstractQuery;

    @XmlTransient
    private static ObjectFactory cswFactory = new ObjectFactory();
    /**
     * Gets the value of the abstractQuery property.
     */
    public List<AbstractQueryType> getAbstractQuery() {
        if (abstractQuery == null) {
            abstractQuery = new ArrayList<AbstractQueryType>();
        }
        return this.abstractQuery;
    }
    
    /**
     * Sets the value of the abstractQuery property.
     */
    public void setAbstractQuery(List<AbstractQueryType> abstractQuery) {
        this.abstractQuery = abstractQuery;
    }
    
    /**
     * Sets the value of the abstractQuery property.
     */
    public void setAbstractQuery(AbstractQueryType abstractQuery) {
        if (this.abstractQuery == null) {
            this.abstractQuery = new ArrayList<AbstractQueryType>();
        }
        if (abstractQuery instanceof QueryType)
            this.abstractQuery.add(abstractQuery);
    }

}
