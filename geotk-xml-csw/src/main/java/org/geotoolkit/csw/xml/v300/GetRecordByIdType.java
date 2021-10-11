/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2019, Geomatys
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

package org.geotoolkit.csw.xml.v300;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.csw.xml.GetRecordById;


/**
 *
 *             Convenience operation to retrieve default record representations
 *             by identifier.
 *             Id - object identifier (a URI) that provides a reference to a
 *                  catalogue item (or a result set if the catalogue supports
 *                  persistent result sets).
 *             ElementSetName - one of "brief, "summary", or "full"
 *
 *
 * <p>Classe Java pour GetRecordByIdType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="GetRecordByIdType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/cat/csw/3.0}RequestBaseType">
 *       &lt;sequence>
 *         &lt;element name="Id" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element ref="{http://www.opengis.net/cat/csw/3.0}ElementSetName" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="outputFormat" type="{http://www.w3.org/2001/XMLSchema}string" default="application/xml" />
 *       &lt;attribute name="outputSchema" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetRecordByIdType", propOrder = {
    "id",
    "elementSetName"
})
public class GetRecordByIdType extends RequestBaseType implements GetRecordById {

    @XmlElement(name = "Id", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String id;
    @XmlElement(name = "ElementSetName")
    protected ElementSetNameType elementSetName;
    @XmlAttribute(name = "outputFormat")
    protected String outputFormat;
    @XmlAttribute(name = "outputSchema")
    @XmlSchemaType(name = "anyURI")
    protected String outputSchema;

    /**
     * An empty constructor used by JAXB
     */
     GetRecordByIdType(){

     }

     /**
     * An empty constructor used by JAXB
     */
     public GetRecordByIdType(final String service, final String version, final ElementSetNameType elementSetName,
             final String outputFormat, final String outputSchema, final String id){
         super(service, version);
         this.elementSetName = elementSetName;
         this.outputFormat   = outputFormat;
         this.outputSchema   = outputSchema;
         this.id             = id;
     }

    /**
     * Obtient la valeur de la propriété id.
     */
    @Override
    public List<String> getId() {
        if (id != null) {
            return Arrays.asList(id);
        }
        return new ArrayList<>();
    }

    /**
     * Définit la valeur de la propriété id.
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Obtient la valeur de la propriété elementSetName.
     */
    @Override
    public ElementSetNameType getElementSetName() {
        return elementSetName;
    }

    /**
     * Définit la valeur de la propriété elementSetName.
     */
    public void setElementSetName(ElementSetNameType value) {
        this.elementSetName = value;
    }

    /**
     * Obtient la valeur de la propriété outputFormat.
     */
    @Override
    public String getOutputFormat() {
        if (outputFormat == null) {
            return "application/xml";
        } else {
            return outputFormat;
        }
    }

    /**
     * Définit la valeur de la propriété outputFormat.
     */
    @Override
    public void setOutputFormat(String value) {
        this.outputFormat = value;
    }

    /**
     * Obtient la valeur de la propriété outputSchema.
     */
    @Override
    public String getOutputSchema() {
        return outputSchema;
    }

    /**
     * Définit la valeur de la propriété outputSchema.
     */
    public void setOutputSchema(String value) {
        this.outputSchema = value;
    }
}
