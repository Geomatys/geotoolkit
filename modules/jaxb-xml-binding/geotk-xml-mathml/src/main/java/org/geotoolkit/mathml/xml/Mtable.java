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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
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
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.w3.org/1998/Math/MathML}TableRowExpression" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.w3.org/1998/Math/MathML}mtable.attributes"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "tableRowExpression"
})
public class Mtable {

    @XmlElementRef(name = "TableRowExpression", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class)
    protected List<JAXBElement<?>> tableRowExpression;
    @XmlAttribute(name = "align")
    protected String align;
    @XmlAttribute(name = "rowalign")
    protected List<Verticalalign> rowalign;
    @XmlAttribute(name = "columnalign")
    protected List<Columnalignstyle> columnalign;
    @XmlAttribute(name = "groupalign")
    protected String groupalign;
    @XmlAttribute(name = "alignmentscope")
    protected List<String> alignmentscope;
    @XmlAttribute(name = "columnwidth")
    protected List<String> columnwidth;
    @XmlAttribute(name = "width")
    protected String width;
    @XmlAttribute(name = "rowspacing")
    protected List<String> rowspacing;
    @XmlAttribute(name = "columnspacing")
    protected List<String> columnspacing;
    @XmlAttribute(name = "rowlines")
    protected List<Linestyle> rowlines;
    @XmlAttribute(name = "columnlines")
    protected List<Linestyle> columnlines;
    @XmlAttribute(name = "frame")
    protected Linestyle frame;
    @XmlAttribute(name = "framespacing")
    protected List<String> framespacing;
    @XmlAttribute(name = "equalrows")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String equalrows;
    @XmlAttribute(name = "equalcolumns")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String equalcolumns;
    @XmlAttribute(name = "displaystyle")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String displaystyle;
    @XmlAttribute(name = "side")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String side;
    @XmlAttribute(name = "minlabelspacing")
    protected String minlabelspacing;
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
     * Gets the value of the tableRowExpression property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tableRowExpression property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTableRowExpression().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link Mlabeledtr }{@code >}
     * {@link JAXBElement }{@code <}{@link Mtr }{@code >}
     * {@link JAXBElement }{@code <}{@link Object }{@code >}
     * 
     * 
     */
    public List<JAXBElement<?>> getTableRowExpression() {
        if (tableRowExpression == null) {
            tableRowExpression = new ArrayList<JAXBElement<?>>();
        }
        return this.tableRowExpression;
    }

    /**
     * Obtient la valeur de la propriété align.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAlign() {
        return align;
    }

    /**
     * Définit la valeur de la propriété align.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAlign(String value) {
        this.align = value;
    }

    /**
     * Gets the value of the rowalign property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rowalign property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRowalign().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Verticalalign }
     * 
     * 
     */
    public List<Verticalalign> getRowalign() {
        if (rowalign == null) {
            rowalign = new ArrayList<Verticalalign>();
        }
        return this.rowalign;
    }

    /**
     * Gets the value of the columnalign property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the columnalign property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getColumnalign().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Columnalignstyle }
     * 
     * 
     */
    public List<Columnalignstyle> getColumnalign() {
        if (columnalign == null) {
            columnalign = new ArrayList<Columnalignstyle>();
        }
        return this.columnalign;
    }

    /**
     * Obtient la valeur de la propriété groupalign.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGroupalign() {
        return groupalign;
    }

    /**
     * Définit la valeur de la propriété groupalign.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGroupalign(String value) {
        this.groupalign = value;
    }

    /**
     * Gets the value of the alignmentscope property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the alignmentscope property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAlignmentscope().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getAlignmentscope() {
        if (alignmentscope == null) {
            alignmentscope = new ArrayList<String>();
        }
        return this.alignmentscope;
    }

    /**
     * Gets the value of the columnwidth property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the columnwidth property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getColumnwidth().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getColumnwidth() {
        if (columnwidth == null) {
            columnwidth = new ArrayList<String>();
        }
        return this.columnwidth;
    }

    /**
     * Obtient la valeur de la propriété width.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWidth() {
        return width;
    }

    /**
     * Définit la valeur de la propriété width.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWidth(String value) {
        this.width = value;
    }

    /**
     * Gets the value of the rowspacing property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rowspacing property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRowspacing().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getRowspacing() {
        if (rowspacing == null) {
            rowspacing = new ArrayList<String>();
        }
        return this.rowspacing;
    }

    /**
     * Gets the value of the columnspacing property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the columnspacing property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getColumnspacing().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getColumnspacing() {
        if (columnspacing == null) {
            columnspacing = new ArrayList<String>();
        }
        return this.columnspacing;
    }

    /**
     * Gets the value of the rowlines property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rowlines property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRowlines().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Linestyle }
     * 
     * 
     */
    public List<Linestyle> getRowlines() {
        if (rowlines == null) {
            rowlines = new ArrayList<Linestyle>();
        }
        return this.rowlines;
    }

    /**
     * Gets the value of the columnlines property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the columnlines property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getColumnlines().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Linestyle }
     * 
     * 
     */
    public List<Linestyle> getColumnlines() {
        if (columnlines == null) {
            columnlines = new ArrayList<Linestyle>();
        }
        return this.columnlines;
    }

    /**
     * Obtient la valeur de la propriété frame.
     * 
     * @return
     *     possible object is
     *     {@link Linestyle }
     *     
     */
    public Linestyle getFrame() {
        return frame;
    }

    /**
     * Définit la valeur de la propriété frame.
     * 
     * @param value
     *     allowed object is
     *     {@link Linestyle }
     *     
     */
    public void setFrame(Linestyle value) {
        this.frame = value;
    }

    /**
     * Gets the value of the framespacing property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the framespacing property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFramespacing().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getFramespacing() {
        if (framespacing == null) {
            framespacing = new ArrayList<String>();
        }
        return this.framespacing;
    }

    /**
     * Obtient la valeur de la propriété equalrows.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEqualrows() {
        return equalrows;
    }

    /**
     * Définit la valeur de la propriété equalrows.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqualrows(String value) {
        this.equalrows = value;
    }

    /**
     * Obtient la valeur de la propriété equalcolumns.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEqualcolumns() {
        return equalcolumns;
    }

    /**
     * Définit la valeur de la propriété equalcolumns.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEqualcolumns(String value) {
        this.equalcolumns = value;
    }

    /**
     * Obtient la valeur de la propriété displaystyle.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDisplaystyle() {
        return displaystyle;
    }

    /**
     * Définit la valeur de la propriété displaystyle.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDisplaystyle(String value) {
        this.displaystyle = value;
    }

    /**
     * Obtient la valeur de la propriété side.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSide() {
        return side;
    }

    /**
     * Définit la valeur de la propriété side.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSide(String value) {
        this.side = value;
    }

    /**
     * Obtient la valeur de la propriété minlabelspacing.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMinlabelspacing() {
        return minlabelspacing;
    }

    /**
     * Définit la valeur de la propriété minlabelspacing.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMinlabelspacing(String value) {
        this.minlabelspacing = value;
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
