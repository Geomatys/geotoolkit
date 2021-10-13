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
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlMixed;
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
 *       &lt;group ref="{http://www.w3.org/1998/Math/MathML}token.content" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;attGroup ref="{http://www.w3.org/1998/Math/MathML}mi.attributes"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "content"
})
public class Mi {

    @XmlElementRefs({
        @XmlElementRef(name = "malignmark", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class),
        @XmlElementRef(name = "mglyph", namespace = "http://www.w3.org/1998/Math/MathML", type = Mglyph.class)
    })
    @XmlMixed
    protected List<Object> content;
    @XmlAttribute(name = "mathcolor")
    protected String mathcolor;
    @XmlAttribute(name = "mathbackground")
    protected String mathbackground;
    @XmlAttribute(name = "mathvariant")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String mathvariant;
    @XmlAttribute(name = "mathsize")
    protected String mathsize;
    @XmlAttribute(name = "dir")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String dir;
    @XmlAttribute(name = "fontfamily")
    @XmlSchemaType(name = "anySimpleType")
    protected String fontfamily;
    @XmlAttribute(name = "fontweight")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String fontweight;
    @XmlAttribute(name = "fontstyle")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String fontstyle;
    @XmlAttribute(name = "fontsize")
    protected String fontsize;
    @XmlAttribute(name = "color")
    protected String color;
    @XmlAttribute(name = "background")
    protected String background;
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
     * Gets the value of the content property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the content property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContent().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link Malignmark }{@code >}
     * {@link String }
     * {@link Mglyph }
     *
     *
     */
    public List<Object> getContent() {
        if (content == null) {
            content = new ArrayList<Object>();
        }
        return this.content;
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
     * Obtient la valeur de la propriété mathvariant.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMathvariant() {
        return mathvariant;
    }

    /**
     * Définit la valeur de la propriété mathvariant.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMathvariant(String value) {
        this.mathvariant = value;
    }

    /**
     * Obtient la valeur de la propriété mathsize.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMathsize() {
        return mathsize;
    }

    /**
     * Définit la valeur de la propriété mathsize.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMathsize(String value) {
        this.mathsize = value;
    }

    /**
     * Obtient la valeur de la propriété dir.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDir() {
        return dir;
    }

    /**
     * Définit la valeur de la propriété dir.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDir(String value) {
        this.dir = value;
    }

    /**
     * Obtient la valeur de la propriété fontfamily.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFontfamily() {
        return fontfamily;
    }

    /**
     * Définit la valeur de la propriété fontfamily.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFontfamily(String value) {
        this.fontfamily = value;
    }

    /**
     * Obtient la valeur de la propriété fontweight.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFontweight() {
        return fontweight;
    }

    /**
     * Définit la valeur de la propriété fontweight.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFontweight(String value) {
        this.fontweight = value;
    }

    /**
     * Obtient la valeur de la propriété fontstyle.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFontstyle() {
        return fontstyle;
    }

    /**
     * Définit la valeur de la propriété fontstyle.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFontstyle(String value) {
        this.fontstyle = value;
    }

    /**
     * Obtient la valeur de la propriété fontsize.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFontsize() {
        return fontsize;
    }

    /**
     * Définit la valeur de la propriété fontsize.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFontsize(String value) {
        this.fontsize = value;
    }

    /**
     * Obtient la valeur de la propriété color.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getColor() {
        return color;
    }

    /**
     * Définit la valeur de la propriété color.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setColor(String value) {
        this.color = value;
    }

    /**
     * Obtient la valeur de la propriété background.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getBackground() {
        return background;
    }

    /**
     * Définit la valeur de la propriété background.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setBackground(String value) {
        this.background = value;
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
