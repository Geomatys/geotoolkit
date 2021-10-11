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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.ows.xml.v200.CodeType;


/**
 *
 * Schema for a WPS Execute operation request, to execute
 * one identified process with the given data and provide the requested
 * output data.
 *
 *
 * <p>Java class for Execute complex type.

 <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="Execute">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wps/2.0}RequestBase">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ows/2.0}Identifier"/>
 *         &lt;element name="Input" type="{http://www.opengis.net/wps/2.0}DataInput" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Output" type="{http://www.opengis.net/wps/2.0}OutputDefinition" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="mode" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="sync"/>
 *             &lt;enumeration value="async"/>
 *             &lt;enumeration value="auto"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="response" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="raw"/>
 *             &lt;enumeration value="document"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlType(name = "ExecuteRequestType", propOrder = {
    "identifier",
    "inputV2",
    "dataInputs",
    "outputV2",
    "responseForm"
})
@XmlRootElement(name = "Execute")
public class Execute extends RequestBase {

    /**
     * Desired response format, i.e. a response document or raw data.
     * Note: Raw data should be used only when a single output is queried.
     */
    public static enum Response {
        raw,
        document
    }

    /**
     * Desired execution mode.
     * <p>
     * String{sync | async | auto} Valid values are to be derived from the jobControlOptions property of each ProcessOffering. “auto” delegates the choice of execution mode to the server.
     * </p>
     */
    public static enum Mode {
        sync,
        async,
        auto
    }

    @XmlElement(name = "Identifier", namespace = "http://www.opengis.net/ows/2.0", required = true)
    protected CodeType identifier;
    protected List<DataInput> input;
    protected List<OutputDefinition> output;
    protected Mode mode;
    protected Response response;

    public Execute() {

    }

    public Execute(String service, String version, String language, CodeType identifier, List<DataInput> input, List<OutputDefinition> output, Response response) {
        super(service, version, language);
        this.identifier = identifier;
        this.input = input;
        this.output = output;
        this.response = response;
    }

    public Execute(String service, String version, String language, CodeType identifier, List<DataInput> input, List<OutputDefinition> output, Response response,
            boolean storeExecuteResp, boolean lineage, boolean status) {
        super(service, version, language);
        this.identifier = identifier;
        this.input = input;
        this.output = output;
        this.response = response;
        this.form = new ResponseForm();
        this.form.parent = this;
        ResponseDocument doc = new ResponseDocument(lineage, status);
        doc.parent = form;
        this.form.setResponseDocument(doc);
        if (storeExecuteResp) {
            this.mode = Mode.async;
        }
    }

    /**
     *
     * Identifier of the process to be executed. All valid process identifiers are
     * listed in the wps:Contents section of the Capabilities document.
     *
     *
     * @return
     *     possible object is
     *     {@link CodeType }
     *
     */
    public CodeType getIdentifier() {
        return identifier;
    }

    /**
     * Sets the value of the identifier property.
     *
     * @param value
     *     allowed object is
     *     {@link CodeType }
     *
     */
    public void setIdentifier(CodeType value) {
        this.identifier = value;
    }

    /**
     * {@inheritDoc}
     */
    public void setIdentifier(String identifiers) {
        setIdentifier(new CodeType(identifiers));
    }

    /**
     * Gets the value of the input property.
     *
     * @return
     */
    public List<DataInput> getInput() {
        if (input == null) {
            input = new ArrayList<>();
        }
        return this.input;
    }

    @XmlElement(name = "Input")
    private List<DataInput> getInputV2() {
        if (FilterByVersion.isV2()) {
            return getInput();
        }

        return null;
    }

    /**
     * Gets the value of the output property.
     *
     * @return
     */
    public List<OutputDefinition> getOutput() {
        if (output == null) {
            output = new ArrayList<>();
        }
        return this.output;
    }

    @XmlElement(name = "Output")
    private List<OutputDefinition> getOutputV2() {
        if (FilterByVersion.isV2()) {
            return getOutput();
        }

        return null;
    }

    @XmlAttribute(name = "mode")
    private Mode getModeMarshall() {
        if (FilterByVersion.isV1()) {
            return null;
        }
        return mode;
    }

    private void setModeMarshall(Mode value) {
        this.mode = value;
    }

    /**
     * Gets the value of the mode property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public Mode getMode() {
        return mode;
    }

    /**
     * Sets the value of the mode property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMode(Mode value) {
        this.mode = value;
    }

    @XmlAttribute(name = "response")
    private Response getResponseMarshall() {
        if (FilterByVersion.isV1()) {
            return null;
        }
        return response;
    }

    private void setResponseMarshall(Response value) {
        this.response = value;
    }

    /**
     * Gets the value of the response property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public Response getResponse() {
        return response;
    }

    /**
     * Sets the value of the response property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setResponse(Response value) {
        this.response = value;
    }

    ////////////////////////////////////////////////////////////////////////////
    //
    // Following section is boilerplate code for WPS v1 retro-compatibility.
    //
    ////////////////////////////////////////////////////////////////////////////

    private ResponseForm form;

    /**
     * @Deprecated @Deprecated WPS 1.0 compatibility
     * @return Language, only English...
     */
    @Deprecated
    @XmlAttribute(name="language")
    private String getLegacyLanguage() {
        return getLanguage();
    }

    /**
     *
     * @return Legacy (WPS 1) response description.
     * @deprecated Use directly WPS 2 methods, as {@link #getOutput() }.
     */
    @XmlElement(name = "ResponseForm")
    @XmlJavaTypeAdapter(FilterV1.ResponseForm.class)
    @Deprecated
    public ResponseForm getResponseForm() {
        if (form == null) {
            form = new ResponseForm();
            form.parent = this;
        }
        return form;
    }

    @Deprecated
    public boolean isStatus() {
        if (getResponseForm() != null && getResponseForm().getResponseDocument() != null
                && getResponseForm().getResponseDocument().isStatus() != null) {
            return getResponseForm().getResponseDocument().isStatus();
        }
        return false;
    }

    @Deprecated
    public boolean isLineage() {
        if (getResponseForm() != null && getResponseForm().getResponseDocument() != null
                && getResponseForm().getResponseDocument().isLineage() != null) {
            return getResponseForm().getResponseDocument().isLineage();
        }
        return false;
    }

    public boolean isRawOutput() {
        if (response != null) {
            return "raw".equalsIgnoreCase(response.name());
        } else if (getResponseForm() != null && getResponseForm().getRawDataOutput()!= null) {
            return true;
        }
        return false;
    }

    public boolean isDocumentOutput() {
        if (getResponseForm() != null) {
            if (getResponseForm().getRawDataOutput() != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Only JAXB can set a response form. For normal users, they should only use
     * the proxy given by {@link #getResponseForm() }.
     * @param form
     */
    private void setResponseForm(ResponseForm form) {
        this.form = form;
    }

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
    private static class DataInputs extends ParentAware<Execute> {

        DataInputs() {
            super(Execute.class);
        }

        /**
         * Gets the value of the input property.
         *
         * @return Objects of the following type(s) are allowed in the list
         * {@link InputDescription }
         *
         */
        @XmlElement(name = "Input", required = true)
        private List<DataInput> getInput() {
            return checkParent().getInput();
        }

        private void setInput(final List<DataInput> description) {
            checkParent().getInput().addAll(description);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("[DataInputs]\n");
            if (getInput() != null) {
                sb.append("Inputs:\n");
                for (DataInput out : getInput()) {
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
     * Defines the response type of the WPS, either raw data or XML document
     *
     * <p>
     * Java class for ResponseForm complex type.
     *
     * <p>
     * The following schema fragment specifies the expected content contained
     * within this class.
     *
     * <pre>
     * &lt;complexType name="ResponseForm">
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;choice>
     *         &lt;element name="ResponseDocument" type="{http://www.opengis.net/wps/1.0.0}ResponseDocument"/>
     *         &lt;element name="RawDataOutput" type="{http://www.opengis.net/wps/1.0.0}OutputDefinition"/>
     *       &lt;/choice>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     * @module
     */
    @XmlType(name = "ResponseFormType", propOrder = {
        "responseDocument",
        "rawDataOutput"
    })
    public static class ResponseForm extends ParentAware<Execute> {

        private ResponseDocument responseDocument;

        ResponseForm() {
            super(Execute.class);
        }

        ResponseForm(ResponseDocument responseDocument) {
            super(Execute.class);
            this.responseDocument = responseDocument;
        }

        /**
         * Gets the value of the responseDocument property.
         *
         * @return possible object is {@link ResponseDocument }
         *
         */
        @XmlElement(name = "ResponseDocument")
        public ResponseDocument getResponseDocument() {
            if (responseDocument == null) {
                responseDocument = new ResponseDocument();
                responseDocument.parent = this;
            }

            return responseDocument;
        }

        /**
         * Sets the value of the responseDocument property.
         * Only JAXB can set a response form. For normal users, they should only
         * use the proxy given by {@link #getResponseDocument() }.
         *
         */
        private void setResponseDocument(final ResponseDocument value) {
            this.responseDocument = value;
        }

        /**
         * @Deprecated WPS 1.0 compatibility. Check response mode instead.
         * @return True if response mode is "raw". Will be null if response is
         * null. False otherwise.
         */
        @Deprecated
        public Boolean isRawOutput() {
            final Response response = checkParent().getResponse();
            return response == null ? null : Response.raw.equals(response);
        }

        /**
         * Gets the value of the rawDataOutput property.
         *
         * @return possible object is {@link OutputDefinition }
         *
         */
        @XmlElement(name = "RawDataOutput")
        public OutputDefinition getRawDataOutput() {
            final Boolean rawOutput = isRawOutput();
            if (rawOutput != null && rawOutput) {
                List<OutputDefinition> output = checkParent().getOutput();
                if (!output.isEmpty()) {
                    // Note: according to the standard, we should have only one
                    // output when using raw mode.
                    return output.get(0);
                }
            }

            return null;
        }

        /**
         * Sets the value of the rawDataOutput property.
         *
         * @param value allowed object is {@link OutputDefinition }
         *
         */
        public void setRawDataOutput(final OutputDefinition value) {
            checkParent().setResponse(Response.raw);
            checkParent().getOutput().add(0, value);
        }
    }

    /**
     * <p>
     * Java class for ResponseDocument complex type.
     *
     * <p>
     * The following schema fragment specifies the expected content contained
     * within this class.
     *
     * <pre>
     * &lt;complexType name="ResponseDocument">
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="Output" type="{http://www.opengis.net/wps/1.0.0}DocumentOutputDefinition" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *       &lt;attribute name="storeExecuteResponse" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
     *       &lt;attribute name="lineage" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
     *       &lt;attribute name="status" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     * @module
     */
    @XmlType(name = "ResponseDocumentType", propOrder = {
        "output"
    })
    public static class ResponseDocument extends ParentAware<ResponseForm> {

        @XmlAttribute
        protected Boolean lineage;
        @XmlAttribute
        protected Boolean status;

        ResponseDocument() {
            super(ResponseForm.class);
        }

        ResponseDocument(Boolean lineage, Boolean status) {
            super(ResponseForm.class);
            this.lineage = lineage;
            this.status = status;
        }

        /**
         * @Deprecated WPS 1.0 compatibility
         * @return
         */
        @Deprecated
        public Boolean isLineage() {
            return lineage;
        }

        public void setLineage(Boolean outLineage) {
            lineage = outLineage;
        }

        /**
         * @Deprecated WPS 1.0 compatibility. Check response mode instead.
         * @return True if response mode is "document". False otherwise.
         */
        @Deprecated
        public boolean isDocumentOutput() {
            final Response response = checkParent().checkParent().getResponse();
            return response == null ? null : Response.document.equals(response);
        }

        /**
         * @Deprecated WPS 1.0 compatibility. Useless, do not use.
         * @return
         */
        @Deprecated
        public Boolean isStatus() {
            return status;
        }

        public void setStatus(final Boolean activateStatus) {
            status = activateStatus;
        }

        /**
         * @deprecated WPS 1.0 compatibility. If possible, check output
         * transmission modes instead.
         * @return
         */
        @XmlAttribute(name = "storeExecuteResponse")
        @Deprecated
        public Boolean isStoreExecuteResponse() {
            final Mode mode = checkParent().checkParent().getMode();
            if (mode == null) {
                return null;
            }

            return Mode.async.equals(mode);
        }

        @Deprecated
        public void setStoreExecuteResponse(Boolean val) {
            if (val == null)
                return;

            final Mode newMode = Boolean.FALSE.equals(val)? Mode.sync : Mode.async;
            checkParent().checkParent().setMode(newMode);
        }

        @XmlElement(name = "Output", required = true)
        private List<OutputDefinition> getOutput() {
            return checkParent().checkParent().getOutput();
        }
    }
}
