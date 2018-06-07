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
import java.util.Objects;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.ows.xml.v200.CodeType;
import org.geotoolkit.ows.xml.v200.KeywordsType;
import org.geotoolkit.ows.xml.v200.LanguageStringType;
import org.geotoolkit.wps.xml.v100.WSDL;

import static org.geotoolkit.wps.xml.WPSMarshallerPool.WPS_2_0_NAMESPACE;

/**
 *
 * The process summary consists of descriptive elements at the process level,
 * the process profiles and the service-specific properties.
 * The process summary is not specific about process inputs and outputs.
 *
 *
 * <p>Java class for ProcessSummary complex type.

 <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ProcessSummary">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wps/2.0}Description">
 *       &lt;attGroup ref="{http://www.opengis.net/wps/2.0}processPropertiesAttributes"/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlType(name = "ProcessSummaryType")
public class ProcessSummary extends Description implements ProcessProperties {

    private List<JobControlOptions> jobControlOptions;
    private List<DataTransmissionMode> outputTransmission;
    @XmlAttribute(name = "processVersion")
    @XmlJavaTypeAdapter(FilterV2.String.class)
    private String processVersion;
    private String processModel;

    public ProcessSummary() {}

    public ProcessSummary(
            CodeType identifier,
            final LanguageStringType title,
            final List<LanguageStringType> _abstract,
            final List<KeywordsType> keywords,
            final String processVersion
    ) {
        super(identifier, title, _abstract, keywords);
        this.processVersion = processVersion;
    }

    /**
     * Gets the value of the jobControlOptions property.
     *
     */
    @Override
    public List<JobControlOptions> getJobControlOptions() {
        if (jobControlOptions == null) {
            jobControlOptions = new ArrayList<>();
        }
        return this.jobControlOptions;
    }

    @XmlAttribute(name = "jobControlOptions", required = true)
    private List<JobControlOptions> getJobOptionsToMarshal() {
        if (FilterByVersion.isV2()) {
            return getJobControlOptions();
        }

        return null;
    }

    /**
     * Gets the value of the outputTransmission property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the outputTransmission property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOutputTransmission().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DataTransmissionMode }
     *
     *
     */
    @XmlAttribute(name = "outputTransmission")
    @XmlJavaTypeAdapter(FilterV2.DataTransmissionMode.class)
    @Override
    public List<DataTransmissionMode> getOutputTransmission() {
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
    @Override
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
    @XmlAttribute(name = "processModel")
    public String getProcessModel() {
        if (FilterByVersion.isV2()) {
            if (processModel == null) {
                return "native";
            } else {
                return processModel;
            }
        }
        return null;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        if (jobControlOptions != null) {
            sb.append("jobControlOptions:\n");
            for (JobControlOptions jb : jobControlOptions) {
                sb.append(jb).append('\n');
            }
        }
        if (outputTransmission != null) {
            sb.append("outputTransmission:\n");
            for (DataTransmissionMode jb : outputTransmission) {
                sb.append(jb).append('\n');
            }
        }
        if (processModel != null) {
            sb.append("processModel:").append(processModel).append('\n');
        }
        if (processVersion != null) {
            sb.append("processVersion:").append(processVersion).append('\n');
        }
        if (profile != null) {
            sb.append("profile:\n");
            for (String jb : profile) {
                sb.append(jb).append('\n');
            }
        }
        if (wsdl != null) {
            sb.append("wsdl:").append(wsdl).append('\n');
        }
        return sb.toString();
    }

    /**
     * Verify that this entry is identical to the specified object.
     *
     * @param object Object to compare
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof ProcessSummary && super.equals(object)) {
            final ProcessSummary that = (ProcessSummary) object;
            return Objects.equals(this.jobControlOptions, that.jobControlOptions)
                    && Objects.equals(this.outputTransmission, that.outputTransmission)
                    && Objects.equals(this.processModel, that.processModel)
                    && Objects.equals(this.processVersion, that.processVersion)
                    && Objects.equals(this.profile, that.profile)
                    && Objects.equals(this.wsdl, that.wsdl);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.jobControlOptions);
        hash = 97 * hash + Objects.hashCode(this.outputTransmission);
        hash = 97 * hash + Objects.hashCode(this.processVersion);
        hash = 97 * hash + Objects.hashCode(this.processModel);
        hash = 97 * hash + Objects.hashCode(this.profile);
        hash = 97 * hash + Objects.hashCode(this.wsdl);
        return hash;
    }

    ////////////////////////////////////////////////////////////////////////////
    //
    // Following section is boilerplate code for WPS v1 retro-compatibility.
    //
    ////////////////////////////////////////////////////////////////////////////

    protected List<String> profile;

    private WSDL wsdl;

    @XmlElement(name="WSDL")
    @XmlJavaTypeAdapter(FilterV1.WSDL.class)
    @Deprecated
    public WSDL getWSDL() {
        return wsdl;
    }

    public void setWSDL(final WSDL wsdl) {
        this.wsdl = wsdl;
    }

    @Deprecated
    public List<String> getProfile() {
        return profile;
    }

    @Deprecated
    public void setProfile(List<String> profile) {
        this.profile = profile;
    }


    @XmlElement(name = "Profile")
    @XmlSchemaType(name = "anyURI")
    private List<String> getLegacyProfile() {
        return FilterByVersion.isV1()? profile : null;
    }

    private void setLegacyProfile(List<String> profile) {
        setProfile(profile);
    }

    /**
     * This is a hack, because in WPS 1, the this attribute must be prefixed,
     * but in WPS 2, it must not.
     * @return The version specified for this process, as depicted by {@link #getProcessVersion() }.
     */
    @XmlAttribute(name="processVersion", namespace=WPS_2_0_NAMESPACE)
    private String getLegacyProcessVersion() {
        return FilterByVersion.isV1() ? getProcessVersion() : null;
    }

    private void setLegacyProcessVersion(final String v) {
        setProcessVersion(v);
    }
}
