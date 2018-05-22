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
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.ows.xml.v200.CodeType;
import org.geotoolkit.ows.xml.v200.KeywordsType;
import org.geotoolkit.ows.xml.v200.LanguageStringType;
import static org.geotoolkit.wps.xml.WPSMarshallerPool.WPS_2_0_NAMESPACE;


/**
 * Full description of a process.
 *
 *
 * In this use, the Description shall describe process properties.


 <p>Java class for ProcessDescription complex type.

 <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ProcessDescription">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wps/2.0}Description">
 *       &lt;sequence>
 *         &lt;element name="Input" type="{http://www.opengis.net/wps/2.0}InputDescription" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Output" type="{http://www.opengis.net/wps/2.0}OutputDescription" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute ref="{http://www.w3.org/XML/1998/namespace}lang"/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlType(name = "ProcessDescriptionType", propOrder = {
    "inputToMarshal",
    "outputToMarshal",
    "dataInputs",
    "processOutputs"
})
public class ProcessDescription extends Description {

    protected List<InputDescription> input;
    protected List<OutputDescription> output;
    @XmlAttribute(name = "lang", namespace = "http://www.w3.org/XML/1998/namespace")
    protected String lang;

    public ProcessDescription() {

    }

    public ProcessDescription(CodeType identifier, final LanguageStringType title,  final List<LanguageStringType> _abstract,
            final List<KeywordsType> keywords, List<InputDescription> input, List<OutputDescription> output) {
        super(identifier, title, _abstract, keywords);
        this.input = input;
        this.output = output;
    }
    /**
     * Gets the value of the input property.
     *
     */
    public List<InputDescription> getInputs() {
        if (input == null) {
            input = new ArrayList<>();
        }
        return this.input;
    }

    @XmlElement(name = "Input")
        private List<InputDescription> getInputToMarshal() {
        if (FilterByVersion.isV2()) {
            return getInputs();
        }
        return null;
    }
    /**
     * Gets the value of the output property.
     *
     */
    public List<OutputDescription> getOutputs() {
        if (output == null) {
            output = new ArrayList<>();
        }
        return this.output;
    }

    @XmlElement(name = "Output", required = true)
    private List<OutputDescription> getOutputToMarshal() {
        if (FilterByVersion.isV2()) {
            return getOutputs();
        }
        return null;
    }

    /**
     *
     * Identifier of a language used by the data(set) contents.
     * This language identifier shall be as specified in IETF RFC 4646. The
     * language tags shall be either complete 5 character codes (e.g. "en-CA"),
     * or abbreviated 2 character codes (e.g. "en"). In addition to the RFC
     * 4646 codes, the server shall support the single special value "*" which
     * is used to indicate "any language".
     *
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLang() {
        return lang;
    }

    /**
     * Sets the value of the lang property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLang(String value) {
        this.lang = value;
    }

        ////////////////////////////////////////////////////////////////////////////
    //
    // Following section is boilerplate code for WPS v1 retro-compatibility.
    //
    ////////////////////////////////////////////////////////////////////////////

    private Boolean statusSupported;
    private Boolean storeSupported;

    /**
     *
     * @deprecated WPS 1.0 retro-compatibility. In WPS 2.0, this information is
     * stored into the {@link ProcessOffering} containing this description.
     */
    @Deprecated
    @XmlAttribute(name="processVersion", namespace=WPS_2_0_NAMESPACE)
    @XmlJavaTypeAdapter(FilterV1.String.class)
    String processVersion;

    /**
     * JAXB boilerplate to switch WPS version on writing. Do not use !
     *
     * @return
     */
    @XmlElement(name = "DataInputs")
    private DataInputs getDataInputs() {
        if (FilterByVersion.isV1()) {
            final DataInputs in = new DataInputs();
            in.parent = this;
            return in;
        }

        return null;
    }

    /**
     * JAXB boilerplate to switch WPS version on reading. Do not use !
     *
     * @return
     */
    private void setDataInputs(final DataInputs in) {
        // Do nothing, we handle it directly into DataInputs class.
    }

    /**
     * JAXB boilerplate to switch WPS version on writing. Do not use !
     *
     * @return
     */
    @XmlElement(name = "ProcessOutputs")
    private ProcessOutputs getProcessOutputs() {
        if (FilterByVersion.isV1() && !getOutputs().isEmpty()) {
            final ProcessOutputs in = new ProcessOutputs();
            in.parent = this;
            return in;
        }

        return null;
    }

    /**
     * JAXB boilerplate to switch WPS version on reading. Do not use !
     *
     * @return
     */
    private void setProcessOutputs(final ProcessOutputs in) {
        // Do nothing, we handle it directly into ProcessOutputs class.
    }

    /**
     *
     * @return True if the status is supported by the process. WARNING: this is
     * only valid for WPS 1.
     *
     * @deprecated This is a retro-compatibility flag for WPS 1.0. It should be
     * used only by people communicating directly with a WPS 1.0 service.
     */
    @Deprecated
    @XmlAttribute(name="statusSupported")
    @XmlJavaTypeAdapter(FilterV1.Boolean.class)
    public Boolean isStatusSupported() {
        return statusSupported;
    }

    /**
     *
     * @param isStatusSupported True if the status is supported by the process.
     * WARNING: this is only valid for WPS 1.
     *
     * @deprecated This is a retro-compatibility flag for WPS 1.0. It should be
     * used only by people communicating directly with a WPS 1.0 service.
     */
    @Deprecated
    public void setStatusSupported(Boolean isStatusSupported) {
        statusSupported = isStatusSupported;
    }

    /**
     *
     * @return True if the service can store result for this process. WARNING:
     * this is only valid for WPS 1.
     *
     * @deprecated This is a retro-compatibility flag for WPS 1.0. It should be
     * used only by people communicating directly with a WPS 1.0 service.
     */
    @Deprecated
    @XmlAttribute(name="storeSupported")
    @XmlJavaTypeAdapter(FilterV1.Boolean.class)
    Boolean isStoreSupported() {
        return storeSupported;
    }

    void setStoreSupported(final Boolean activate) {
        storeSupported = activate;
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
     *         &lt;element name="Input" type="{http://www.opengis.net/wps/1.0.0}InputDescription" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlType(name = "", propOrder = {
        "input"
    })
    private static class DataInputs extends ParentAware<ProcessDescription> {

        DataInputs() {
            super(ProcessDescription.class);
        }

        /**
         * Gets the value of the input property.
         *
         * @return Objects of the following type(s) are allowed in the list
         * {@link InputDescription }
         *
         */
        @XmlElement(name = "Input", required = true)
        private List<InputDescription> getInput() {
            return checkParent().getInputs();
        }

        private void setInput(final List<InputDescription> description) {
            checkParent().getInputs().addAll(description);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("[DataInputs]\n");
            if (getInput() != null) {
                sb.append("Inputs:\n");
                for (InputDescription out : getInput()) {
                    sb.append(out).append('\n');
                }
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
            if (object instanceof DataInputs) {
                final DataInputs that = (DataInputs) object;
                return Objects.equals(this.getInput(), that.getInput());
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 89 * hash + Objects.hashCode(getInput());
            return hash;
        }
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
     *         &lt;element name="Output" type="{http://www.opengis.net/wps/1.0.0}OutputDescription" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlType(name = "", propOrder = {
        "output"
    })
    private static class ProcessOutputs extends ParentAware<ProcessDescription> {

        ProcessOutputs() {
            super(ProcessDescription.class);
        }

        /**
         * Gets the value of the output property.
         *
         * @return Objects of the following type(s) are allowed in the list
         * {@link OutputDescription }
         *
         *
         */
        @XmlElement(name = "Output", required = true)
        private List<OutputDescription> getOutput() {
            return checkParent().getOutputs();
        }

        private void setOutput(final List<OutputDescription> description) {
            checkParent().getOutputs().addAll(description);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("[ProcessOutputs]\n");
            if (getOutput() != null) {
                sb.append("Outputs:\n");
                for (OutputDescription out : getOutput()) {
                    sb.append(out).append('\n');
                }
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
            if (object instanceof ProcessOutputs) {
                final ProcessOutputs that = (ProcessOutputs) object;
                return Objects.equals(this.getOutput(), that.getOutput());
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 89 * hash + Objects.hashCode(this.getOutput());
            return hash;
        }
    }
}
