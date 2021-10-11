/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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

package org.geotoolkit.dif.xml.v102;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 *
 *
 *             | DIF 9               | ECHO 10 | UMM               | DIF 10             | Notes          |
 *             | ------------------- | ------- | ------------------| ------------------ | -------------  |
 *             | Use_Constraints     |    -    | UseConstraints    | Use_Constraints    | No change      |
 *
 *
 *
 * <p>Classe Java pour UseConstraintsType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="UseConstraintsType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/>DisplayableTextTypeBaseType">
 *       &lt;attribute name="mime_type" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}DisplayableTextEnum" default="text/markdown" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UseConstraintsType", propOrder = {
    "value"
})
public class UseConstraintsType {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "mime_type")
    protected DisplayableTextEnum mimeType;

    public UseConstraintsType() {

    }

    public UseConstraintsType(String value) {
        this.value = value;
    }

    /**
     * Obtient la valeur de la propriété value.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getValue() {
        return value;
    }

    /**
     * Définit la valeur de la propriété value.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Obtient la valeur de la propriété mimeType.
     *
     * @return
     *     possible object is
     *     {@link DisplayableTextEnum }
     *
     */
    public DisplayableTextEnum getMimeType() {
        if (mimeType == null) {
            return DisplayableTextEnum.TEXT_MARKDOWN;
        } else {
            return mimeType;
        }
    }

    /**
     * Définit la valeur de la propriété mimeType.
     *
     * @param value
     *     allowed object is
     *     {@link DisplayableTextEnum }
     *
     */
    public void setMimeType(DisplayableTextEnum value) {
        this.mimeType = value;
    }

}
