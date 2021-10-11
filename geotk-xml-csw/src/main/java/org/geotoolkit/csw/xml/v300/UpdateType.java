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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;
import org.geotoolkit.csw.xml.Update;


/**
 *
 *             Update statements may replace an entire record or only update part
 *             of a record:
 *             1) To replace an existing record, include a new instance of the
 *                record;
 *             2) To update selected properties of an existing record, include
 *                a set of RecordProperty elements. The scope of the update
 *                statement  is determined by the Constraint element.
 *                The 'handle' is a local identifier for the action.
 *
 *
 * <p>Classe Java pour UpdateType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="UpdateType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;any namespace='##other'/>
 *           &lt;sequence>
 *             &lt;element ref="{http://www.opengis.net/cat/csw/3.0}RecordProperty" maxOccurs="unbounded"/>
 *             &lt;element ref="{http://www.opengis.net/cat/csw/3.0}Constraint"/>
 *           &lt;/sequence>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="typeName" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;attribute name="handle" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UpdateType", propOrder = {
    "any",
    "recordProperty",
    "constraint"
})
public class UpdateType implements Update{

    @XmlAnyElement(lax = true)
    protected Object any;
    @XmlElement(name = "RecordProperty")
    protected List<RecordPropertyType> recordProperty;
    @XmlElement(name = "Constraint")
    protected QueryConstraintType constraint;
    @XmlAttribute(name = "typeName")
    protected QName typeName;
    @XmlAttribute(name = "handle")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String handle;

    public UpdateType() {

    }

    public UpdateType(final Object any, final QueryConstraintType query) {
        this.any = any;
        this.constraint = query;
    }

    public UpdateType(final List<RecordPropertyType> recordProperty, final QueryConstraintType query) {
        this.recordProperty = recordProperty;
        this.constraint = query;
    }

    /**
     * Obtient la valeur de la propriété any.
     *
     * @return
     *     possible object is
     *     {@link Object }
     *
     */
    @Override
    public Object getAny() {
        return any;
    }

    /**
     * Définit la valeur de la propriété any.
     *
     * @param value
     *     allowed object is
     *     {@link Object }
     *
     */
    public void setAny(Object value) {
        this.any = value;
    }

    /**
     * Gets the value of the recordProperty property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the recordProperty property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRecordProperty().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RecordPropertyType }
     *
     *
     */
    @Override
    public List<RecordPropertyType> getRecordProperty() {
        if (recordProperty == null) {
            recordProperty = new ArrayList<>();
        }
        return this.recordProperty;
    }

    @Override
    public Map<String, Object> getRecordPropertyMap() {
        final Map<String, Object> result = new HashMap<>();
        if (recordProperty != null) {
            for (RecordPropertyType rp : recordProperty) {
                result.put(rp.getName(), rp.getValue());
            }
        }
        return result;
    }

    /**
     * Obtient la valeur de la propriété constraint.
     *
     * @return
     *     possible object is
     *     {@link QueryConstraintType }
     *
     */
    @Override
    public QueryConstraintType getConstraint() {
        return constraint;
    }

    /**
     * Définit la valeur de la propriété constraint.
     *
     * @param value
     *     allowed object is
     *     {@link QueryConstraintType }
     *
     */
    public void setConstraint(QueryConstraintType value) {
        this.constraint = value;
    }

    /**
     * Obtient la valeur de la propriété typeName.
     *
     * @return
     *     possible object is
     *     {@link QName }
     *
     */
    public QName getTypeName() {
        return typeName;
    }

    /**
     * Définit la valeur de la propriété typeName.
     *
     * @param value
     *     allowed object is
     *     {@link QName }
     *
     */
    public void setTypeName(QName value) {
        this.typeName = value;
    }

    /**
     * Obtient la valeur de la propriété handle.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getHandle() {
        return handle;
    }

    /**
     * Définit la valeur de la propriété handle.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setHandle(String value) {
        this.handle = value;
    }

}
