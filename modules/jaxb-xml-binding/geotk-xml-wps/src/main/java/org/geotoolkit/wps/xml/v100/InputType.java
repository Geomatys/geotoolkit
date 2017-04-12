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
package org.geotoolkit.wps.xml.v100;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.sis.referencing.IdentifiedObjects;
import org.geotoolkit.ows.xml.v110.BoundingBoxType;
import org.geotoolkit.ows.xml.v110.CodeType;
import org.geotoolkit.ows.xml.v110.LanguageStringType;
import org.geotoolkit.wps.converters.WPSConvertersUtils;
import org.geotoolkit.wps.xml.Input;
import org.opengis.geometry.Envelope;
import org.opengis.util.FactoryException;


/**
 * Value of one input to a process.
 *
 * <p>Java class for InputType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="InputType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}Identifier"/>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}Title" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}Abstract" minOccurs="0"/>
 *         &lt;group ref="{http://www.opengis.net/wps/1.0.0}InputDataFormChoice"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InputType", propOrder = {
    "identifier",
    "title",
    "_abstract",
    "reference",
    "data"
})
public class InputType implements Input{

    @XmlElement(name = "Identifier", namespace = "http://www.opengis.net/ows/1.1", required = true)
    protected CodeType identifier;
    @XmlElement(name = "Title", namespace = "http://www.opengis.net/ows/1.1")
    protected LanguageStringType title;
    @XmlElement(name = "Abstract", namespace = "http://www.opengis.net/ows/1.1")
    protected LanguageStringType _abstract;
    @XmlElement(name = "Reference")
    protected InputReferenceType reference;
    @XmlElement(name = "Data")
    protected DataType data;

    public InputType() {

    }

    public InputType(CodeType identifier, LanguageStringType title, LanguageStringType _abstract, InputReferenceType reference) {
        this.identifier = identifier;
        this.title = title;
        this._abstract = _abstract;
        this.reference = reference;
    }

    public InputType(CodeType identifier, LanguageStringType title, LanguageStringType _abstract, DataType data) {
        this.identifier = identifier;
        this.title = title;
        this._abstract = _abstract;
        this.data = data;
    }

    /**
     * Unambiguous identifier or name of a process, unique for this server, or unambiguous identifier or name of an output, unique for this process.
     *
     * @return
     *     possible object is
     *     {@link CodeType }
     *
     */
    @Override
    public CodeType getIdentifier() {
        return identifier;
    }

    /**
     * Unambiguous identifier or name of a process, unique for this server, or unambiguous identifier or name of an output, unique for this process.
     *
     * @param value
     *     allowed object is
     *     {@link CodeType }
     *
     */
    public void setIdentifier(final CodeType value) {
        this.identifier = value;
    }

    /**
     * Title of a process or output, normally available for display to a human.
     *
     * @return
     *     possible object is
     *     {@link LanguageStringType }
     *
     */
    @Override
    public LanguageStringType getTitle() {
        return title;
    }

    /**
     * Title of a process or output, normally available for display to a human.
     *
     * @param value
     *     allowed object is
     *     {@link LanguageStringType }
     *
     */
    public void setTitle(final LanguageStringType value) {
        this.title = value;
    }

    /**
     * Brief narrative description of a process or output, normally available for display to a human.
     *
     * @return
     *     possible object is
     *     {@link LanguageStringType }
     *
     */
    @Override
    public LanguageStringType getAbstract() {
        return _abstract;
    }

    /**
     * Brief narrative description of a process or output, normally available for display to a human.
     *
     * @param value
     *     allowed object is
     *     {@link LanguageStringType }
     *
     */
    public void setAbstract(final LanguageStringType value) {
        this._abstract = value;
    }

    /**
     * Gets the value of the reference property.
     *
     * @return
     *     possible object is
     *     {@link InputReferenceType }
     *
     */
    @Override
    public InputReferenceType getReference() {
        return reference;
    }

    /**
     * Sets the value of the reference property.
     *
     * @param value
     *     allowed object is
     *     {@link InputReferenceType }
     *
     */
    public void setReference(final InputReferenceType value) {
        this.reference = value;
    }

    /**
     * Gets the value of the data property.
     *
     * @return
     *     possible object is
     *     {@link DataType }
     *
     */
    public DataType getData() {
        return data;
    }

    /**
     * Sets the value of the data property.
     *
     * @param value
     *     allowed object is
     *     {@link DataType }
     *
     */
    public void setData(final DataType value) {
        this.data = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[").append(this.getClass().getSimpleName()).append("]\n");
        if (identifier != null) {
            sb.append("identifier:").append(identifier).append('\n');
        }
        if (title != null) {
            sb.append("title:").append(title).append('\n');
        }
        if (_abstract != null) {
            sb.append("_abstract:").append(_abstract).append('\n');
        }
        if (reference != null) {
            sb.append("reference:").append(reference).append('\n');
        }
        if (data != null) {
            sb.append("data:").append(data).append('\n');
        }
        return sb.toString();
    }

    /**
     * Verify that this entry is identical to the specified object.
     * @param object Object to compare
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof InputType) {
            final InputType that = (InputType) object;
            return Objects.equals(this._abstract, that._abstract) &&
                   Objects.equals(this.data, that.data) &&
                   Objects.equals(this.identifier, that.identifier) &&
                   Objects.equals(this.reference, that.reference) &&
                   Objects.equals(this.title, that.title);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.identifier);
        hash = 97 * hash + Objects.hashCode(this.title);
        hash = 97 * hash + Objects.hashCode(this._abstract);
        hash = 97 * hash + Objects.hashCode(this.reference);
        hash = 97 * hash + Objects.hashCode(this.data);
        return hash;
    }

    public static InputType createLiteral(final String identifier, final String data, String dataype, String uom) {

        final LiteralDataType literal = new LiteralDataType();
        literal.setValue(data);
        literal.setDataType(dataype);
        literal.setUom(uom);

        final DataType datatype = new DataType();
        datatype.setLiteralData(literal);

        final InputType inputType = new InputType();
        inputType.setIdentifier(new CodeType(identifier));
        inputType.setData(datatype);

        return inputType;
    }

    public static InputType createBoundingBox(String identifier, Envelope envelope) {

        String crs;
        try {
            crs = "EPSG:"+IdentifiedObjects.lookupEPSG(envelope.getCoordinateReferenceSystem());
        } catch (FactoryException ex) {
            crs = envelope.getCoordinateReferenceSystem().getName().getCode();
        }

        final DataType data = new DataType();
        final BoundingBoxType bbox = new BoundingBoxType(crs,
                envelope.getMinimum(0), envelope.getMinimum(1),
                envelope.getMaximum(0), envelope.getMaximum(1));

        data.setBoundingBoxData(bbox);

        final InputType inputType = new InputType();
        inputType.setIdentifier(new CodeType(identifier));
        inputType.setData(data);

        return inputType;
    }
    /**
     * Get an Input of type complex
     *
     * @param identifier
     * @param encoding
     * @param mime
     * @param schema
     * @param inputData
     * @param storageURL
     * @param storageDirectory
     * @return
     */
    public static InputType createComplex(String identifier, String encoding, String mime, String schema, Object inputData, String storageURL, String storageDirectory) {


        final Map<String,Object> parameters = new HashMap<>();
        parameters.put(WPSConvertersUtils.OUT_STORAGE_URL, storageURL);
        parameters.put(WPSConvertersUtils.OUT_STORAGE_DIR, storageDirectory);
        //Try to convert the complex input.
        final ComplexDataType complex = (ComplexDataType) WPSConvertersUtils.convertToComplex("1.0.0", inputData, mime, encoding, schema, parameters);

        final DataType datatype = new DataType();
        datatype.setComplexData(complex);

        final InputType inputType = new InputType();
        inputType.setIdentifier(new CodeType(identifier));
        inputType.setData(datatype);

        return inputType;
    }

    /**
     * Get an Input of type reference
     *
     * @param identifier
     * @param href
     * @param method
     * @param encoding
     * @param mime
     * @param schema
     * @return
     */
    public static InputType createReference(String identifier, String href, String method, String encoding, String mime, String schema) {

        final InputReferenceType ref = new InputReferenceType();
        ref.setHref(href);
        ref.setMethod(method);
        ref.setEncoding(encoding);
        ref.setMimeType(mime);
        ref.setSchema(schema);

        final InputType inputType = new InputType();
        inputType.setIdentifier(new CodeType(identifier));
        inputType.setReference(ref);

        return inputType;
    }

}
