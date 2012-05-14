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

package org.geotoolkit.mathml.xml;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;


/**
 * <p>Classe Java pour anonymous complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.w3.org/1998/Math/MathML}ImpliedMrow">
 *       &lt;attGroup ref="{http://www.w3.org/1998/Math/MathML}mtd.attributes"/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
public class TableCellExpression
    extends ImpliedMrow
{

    @XmlAttribute(name = "rowspan")
    protected BigInteger rowspan;
    @XmlAttribute(name = "columnspan")
    protected BigInteger columnspan;
    @XmlAttribute(name = "rowalign")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String rowalign;
    @XmlAttribute(name = "columnalign")
    protected Columnalignstyle columnalign;
    @XmlAttribute(name = "groupalign")
    protected List<GroupAlignment> groupalign;
    @XmlAttribute(name = "mathcolor")
    protected String mathcolor;
    @XmlAttribute(name = "mathbackground")
    protected String mathbackground;
    @XmlAttribute(name = "id")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    @XmlAttribute(name = "xref")
    @XmlSchemaType(name = "anySimpleType")
    protected String xref;
    @XmlAttribute(name = "class")
    @XmlSchemaType(name = "NMTOKENS")
    protected List<String> clazz;
    @XmlAttribute(name = "style")
    protected String style;
    @XmlAttribute(name = "href")
    @XmlSchemaType(name = "anyURI")
    protected String href;
    @XmlAttribute(name = "other")
    @XmlSchemaType(name = "anySimpleType")
    protected String other;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Obtient la valeur de la propriété rowspan.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getRowspan() {
        return rowspan;
    }

    /**
     * Définit la valeur de la propriété rowspan.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setRowspan(BigInteger value) {
        this.rowspan = value;
    }

    /**
     * Obtient la valeur de la propriété columnspan.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getColumnspan() {
        return columnspan;
    }

    /**
     * Définit la valeur de la propriété columnspan.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setColumnspan(BigInteger value) {
        this.columnspan = value;
    }

    /**
     * Obtient la valeur de la propriété rowalign.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRowalign() {
        return rowalign;
    }

    /**
     * Définit la valeur de la propriété rowalign.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRowalign(String value) {
        this.rowalign = value;
    }

    /**
     * Obtient la valeur de la propriété columnalign.
     * 
     * @return
     *     possible object is
     *     {@link Columnalignstyle }
     *     
     */
    public Columnalignstyle getColumnalign() {
        return columnalign;
    }

    /**
     * Définit la valeur de la propriété columnalign.
     * 
     * @param value
     *     allowed object is
     *     {@link Columnalignstyle }
     *     
     */
    public void setColumnalign(Columnalignstyle value) {
        this.columnalign = value;
    }

    /**
     * Gets the value of the groupalign property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the groupalign property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGroupalign().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GroupAlignment }
     * 
     * 
     */
    public List<GroupAlignment> getGroupalign() {
        if (groupalign == null) {
            groupalign = new ArrayList<GroupAlignment>();
        }
        return this.groupalign;
    }

    /**
     * Obtient la valeur de la propriété mathcolor.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMathcolor() {
        return mathcolor;
    }

    /**
     * Définit la valeur de la propriété mathcolor.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMathcolor(String value) {
        this.mathcolor = value;
    }

    /**
     * Obtient la valeur de la propriété mathbackground.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMathbackground() {
        return mathbackground;
    }

    /**
     * Définit la valeur de la propriété mathbackground.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMathbackground(String value) {
        this.mathbackground = value;
    }

    /**
     * Obtient la valeur de la propriété id.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Définit la valeur de la propriété id.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Obtient la valeur de la propriété xref.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXref() {
        return xref;
    }

    /**
     * Définit la valeur de la propriété xref.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXref(String value) {
        this.xref = value;
    }

    /**
     * Gets the value of the clazz property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the clazz property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getClazz().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getClazz() {
        if (clazz == null) {
            clazz = new ArrayList<String>();
        }
        return this.clazz;
    }

    /**
     * Obtient la valeur de la propriété style.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStyle() {
        return style;
    }

    /**
     * Définit la valeur de la propriété style.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStyle(String value) {
        this.style = value;
    }

    /**
     * Obtient la valeur de la propriété href.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHref() {
        return href;
    }

    /**
     * Définit la valeur de la propriété href.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHref(String value) {
        this.href = value;
    }

    /**
     * Obtient la valeur de la propriété other.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOther() {
        return other;
    }

    /**
     * Définit la valeur de la propriété other.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOther(String value) {
        this.other = value;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     * 
     * <p>
     * the map is keyed by the name of the attribute and 
     * the value is the string value of the attribute.
     * 
     * the map returned by this method is live, and you can add new attribute
     * by updating the map directly. Because of this design, there's no setter.
     * 
     * 
     * @return
     *     always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

}
