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

package org.geotoolkit.sos.xml.v200;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.observation.xml.v200.OMObservationType;
import org.geotoolkit.sos.xml.InsertResultTemplate;
import org.geotoolkit.sos.xml.ResultTemplate;
import org.geotoolkit.swe.xml.v200.AbstractDataComponentType;
import org.geotoolkit.swe.xml.v200.AbstractEncodingType;
import org.geotoolkit.swes.xml.v200.ExtensibleRequestType;


/**
 * <p>Java class for InsertResultTemplateType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InsertResultTemplateType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swes/2.0}ExtensibleRequestType">
 *       &lt;sequence>
 *         &lt;element name="proposedTemplate">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.opengis.net/sos/2.0}ResultTemplate"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InsertResultTemplateType", propOrder = {
    "proposedTemplate"
})
@XmlRootElement(name="InsertResultTemplate")
public class InsertResultTemplateType extends ExtensibleRequestType implements InsertResultTemplate {

    @XmlElement(required = true)
    private InsertResultTemplateType.ProposedTemplate proposedTemplate;

    public InsertResultTemplateType() {
        
    }
    
    public InsertResultTemplateType(final String version, final String offering, final OMObservationType template,  final AbstractDataComponentType resultStructure,
            final AbstractEncodingType encoding) {
        super(version, "SOS");
        this.proposedTemplate = new ProposedTemplate(offering, template, resultStructure, encoding);
    }
    
    /**
     * Gets the value of the proposedTemplate property.
     * 
     * @return
     *     possible object is
     *     {@link InsertResultTemplateType.ProposedTemplate }
     *     
     */
    public InsertResultTemplateType.ProposedTemplate getProposedTemplate() {
        return proposedTemplate;
    }

    /**
     * Sets the value of the proposedTemplate property.
     * 
     * @param value
     *     allowed object is
     *     {@link InsertResultTemplateType.ProposedTemplate }
     *     
     */
    public void setProposedTemplate(InsertResultTemplateType.ProposedTemplate value) {
        this.proposedTemplate = value;
    }

    @Override
    public ResultTemplate getTemplate() {
        if (proposedTemplate != null) {
            return proposedTemplate.resultTemplate;
        }
        return null;
    }


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
     *         &lt;element ref="{http://www.opengis.net/sos/2.0}ResultTemplate"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "resultTemplate"
    })
    public static class ProposedTemplate {

        @XmlElement(name = "ResultTemplate", required = true)
        private ResultTemplateType resultTemplate;

        public ProposedTemplate() {
            
        }
        
        public ProposedTemplate(final String offering, final OMObservationType template,  final AbstractDataComponentType resultStructure,
            final AbstractEncodingType encoding) {
            this.resultTemplate = new ResultTemplateType(offering, template, resultStructure, encoding);
        }
        
        /**
         * Gets the value of the resultTemplate property.
         * 
         * @return
         *     possible object is
         *     {@link ResultTemplateType }
         *     
         */
        public ResultTemplateType getResultTemplate() {
            return resultTemplate;
        }

        /**
         * Sets the value of the resultTemplate property.
         * 
         * @param value
         *     allowed object is
         *     {@link ResultTemplateType }
         *     
         */
        public void setResultTemplate(ResultTemplateType value) {
            this.resultTemplate = value;
        }

    }

}
