/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.tjs.xml.v100;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Describes a descriptor column or data column in the dataset. Used for
 * {@code tjs:Columnset/Attributes/Column}.
 *
 * @author Quentin Bialota (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AttributeColumnType", propOrder = {"title", "abstractText", "documentation", "values", "getDataRequest"})
public class AttributeColumnType extends ColumnDescriptionType {

    @XmlElement(name = "Title", required = true)
    private String title;

    @XmlElement(name = "Abstract", required = true)
    private String abstractText;

    @XmlElement(name = "Documentation")
    private String documentation;

    @XmlElement(name = "Values", required = true)
    private ValuesType values;

    @XmlElement(name = "GetDataRequest", required = true)
    private RequestReferenceType getDataRequest;

    @XmlAttribute(required = true)
    private ColumnPurposeType purpose;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAbstractText() {
        return abstractText;
    }

    public void setAbstractText(String abstractText) {
        this.abstractText = abstractText;
    }

    public String getDocumentation() {
        return documentation;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

    public ValuesType getValues() {
        return values;
    }

    public void setValues(ValuesType values) {
        this.values = values;
    }

    public RequestReferenceType getGetDataRequest() {
        return getDataRequest;
    }

    public void setGetDataRequest(RequestReferenceType getDataRequest) {
        this.getDataRequest = getDataRequest;
    }

    public ColumnPurposeType getPurpose() {
        return purpose;
    }

    public void setPurpose(ColumnPurposeType purpose) {
        this.purpose = purpose;
    }
}
