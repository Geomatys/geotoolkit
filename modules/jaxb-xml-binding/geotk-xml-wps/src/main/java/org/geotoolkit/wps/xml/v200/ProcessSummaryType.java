/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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

package org.geotoolkit.wps.xml.v200;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.LanguageString;
import org.geotoolkit.ows.xml.v200.CodeType;
import org.geotoolkit.ows.xml.v200.KeywordsType;
import org.geotoolkit.ows.xml.v200.LanguageStringType;
import org.geotoolkit.wps.xml.ProcessSummary;


/**
 * 
 * The process summary consists of descriptive elements at the process level,
 * the process profiles and the service-specific properties.
 * The process summary is not specific about process inputs and outputs.
 * 			
 * 
 * <p>Java class for ProcessSummaryType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProcessSummaryType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wps/2.0}DescriptionType">
 *       &lt;attGroup ref="{http://www.opengis.net/wps/2.0}processPropertiesAttributes"/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProcessSummaryType")
public class ProcessSummaryType extends DescriptionType implements ProcessSummary, org.geotoolkit.wps.xml.ProcessOffering {

    @XmlAttribute(name = "jobControlOptions", required = true)
    protected List<String> jobControlOptions;
    @XmlAttribute(name = "outputTransmission")
    protected List<DataTransmissionModeType> outputTransmission;
    @XmlAttribute(name = "processVersion")
    protected String processVersion;
    @XmlAttribute(name = "processModel")
    protected String processModel;

    public ProcessSummaryType() {
        
    }
    
    public ProcessSummaryType(CodeType identifier, final List<LanguageStringType> title,  final List<LanguageStringType> _abstract,
            final List<KeywordsType> keywords, String processVersion) {
        super(identifier, title, _abstract, keywords);
        this.processVersion = processVersion;
    }

    @Override
    public LanguageString getSingleAbstract() {
        final List<LanguageStringType> lst = getAbstract();
        return lst.isEmpty() ? null : lst.get(0);
    }

    /**
     * Gets the value of the jobControlOptions property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getJobControlOptions() {
        if (jobControlOptions == null) {
            jobControlOptions = new ArrayList<>();
        }
        return this.jobControlOptions;
    }

    /**
     * Gets the value of the outputTransmission property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link DataTransmissionModeType }
     * 
     * 
     */
    public List<DataTransmissionModeType> getOutputTransmission() {
        if (outputTransmission == null) {
            outputTransmission = new ArrayList<>();
        }
        return this.outputTransmission;
    }

    /**
     * Gets the value of the processVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProcessVersion() {
        return processVersion;
    }

    /**
     * Sets the value of the processVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProcessVersion(String value) {
        this.processVersion = value;
    }

    /**
     * Gets the value of the processModel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProcessModel() {
        if (processModel == null) {
            return "native";
        } else {
            return processModel;
        }
    }

    /**
     * Sets the value of the processModel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProcessModel(String value) {
        this.processModel = value;
    }

}
