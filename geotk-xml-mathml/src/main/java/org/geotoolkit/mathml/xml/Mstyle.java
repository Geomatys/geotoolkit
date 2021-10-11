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

import java.math.BigDecimal;
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
 *       &lt;attGroup ref="{http://www.w3.org/1998/Math/MathML}mstyle.attributes"/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
public class Mstyle
    extends ImpliedMrow
{

    @XmlAttribute(name = "veryverythinmathspace")
    protected String veryverythinmathspace;
    @XmlAttribute(name = "verythinmathspace")
    protected String verythinmathspace;
    @XmlAttribute(name = "thinmathspace")
    protected String thinmathspace;
    @XmlAttribute(name = "mediummathspace")
    protected String mediummathspace;
    @XmlAttribute(name = "thickmathspace")
    protected String thickmathspace;
    @XmlAttribute(name = "verythickmathspace")
    protected String verythickmathspace;
    @XmlAttribute(name = "veryverythickmathspace")
    protected String veryverythickmathspace;
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
    @XmlAttribute(name = "accent")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String accent;
    @XmlAttribute(name = "accentunder")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String accentunder;
    @XmlAttribute(name = "align")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String align;
    @XmlAttribute(name = "alignmentscope")
    protected List<String> alignmentscope;
    @XmlAttribute(name = "bevelled")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String bevelled;
    @XmlAttribute(name = "charalign")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String charalign;
    @XmlAttribute(name = "charspacing")
    protected String charspacing;
    @XmlAttribute(name = "close")
    @XmlSchemaType(name = "anySimpleType")
    protected String close;
    @XmlAttribute(name = "columnalign")
    protected List<Columnalignstyle> columnalign;
    @XmlAttribute(name = "columnlines")
    protected List<Linestyle> columnlines;
    @XmlAttribute(name = "columnspacing")
    protected List<String> columnspacing;
    @XmlAttribute(name = "columnspan")
    protected BigInteger columnspan;
    @XmlAttribute(name = "columnwidth")
    protected List<String> columnwidth;
    @XmlAttribute(name = "crossout")
    protected List<String> crossout;
    @XmlAttribute(name = "denomalign")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String denomalign;
    @XmlAttribute(name = "depth")
    protected String depth;
    @XmlAttribute(name = "dir")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String dir;
    @XmlAttribute(name = "edge")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String edge;
    @XmlAttribute(name = "equalcolumns")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String equalcolumns;
    @XmlAttribute(name = "equalrows")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String equalrows;
    @XmlAttribute(name = "fence")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String fence;
    @XmlAttribute(name = "form")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String form;
    @XmlAttribute(name = "frame")
    protected Linestyle frame;
    @XmlAttribute(name = "framespacing")
    protected List<String> framespacing;
    @XmlAttribute(name = "groupalign")
    protected String groupalign;
    @XmlAttribute(name = "height")
    protected String height;
    @XmlAttribute(name = "indentalign")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String indentalign;
    @XmlAttribute(name = "indentalignfirst")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String indentalignfirst;
    @XmlAttribute(name = "indentalignlast")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String indentalignlast;
    @XmlAttribute(name = "indentshift")
    protected String indentshift;
    @XmlAttribute(name = "indentshiftfirst")
    protected String indentshiftfirst;
    @XmlAttribute(name = "indentshiftlast")
    protected String indentshiftlast;
    @XmlAttribute(name = "indenttarget")
    @XmlSchemaType(name = "anySimpleType")
    protected String indenttarget;
    @XmlAttribute(name = "largeop")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String largeop;
    @XmlAttribute(name = "leftoverhang")
    protected String leftoverhang;
    @XmlAttribute(name = "length")
    protected BigInteger length;
    @XmlAttribute(name = "linebreak")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String linebreak;
    @XmlAttribute(name = "linebreakmultchar")
    @XmlSchemaType(name = "anySimpleType")
    protected String linebreakmultchar;
    @XmlAttribute(name = "linebreakstyle")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String linebreakstyle;
    @XmlAttribute(name = "lineleading")
    protected String lineleading;
    @XmlAttribute(name = "linethickness")
    protected String linethickness;
    @XmlAttribute(name = "location")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String location;
    @XmlAttribute(name = "longdivstyle")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String longdivstyle;
    @XmlAttribute(name = "lquote")
    @XmlSchemaType(name = "anySimpleType")
    protected String lquote;
    @XmlAttribute(name = "lspace")
    protected String lspace;
    @XmlAttribute(name = "mathsize")
    protected String mathsize;
    @XmlAttribute(name = "mathvariant")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String mathvariant;
    @XmlAttribute(name = "maxsize")
    protected String maxsize;
    @XmlAttribute(name = "minlabelspacing")
    protected String minlabelspacing;
    @XmlAttribute(name = "minsize")
    protected String minsize;
    @XmlAttribute(name = "movablelimits")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String movablelimits;
    @XmlAttribute(name = "mslinethickness")
    protected String mslinethickness;
    @XmlAttribute(name = "notation")
    @XmlSchemaType(name = "anySimpleType")
    protected String notation;
    @XmlAttribute(name = "numalign")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String numalign;
    @XmlAttribute(name = "open")
    @XmlSchemaType(name = "anySimpleType")
    protected String open;
    @XmlAttribute(name = "position")
    protected BigInteger position;
    @XmlAttribute(name = "rightoverhang")
    protected String rightoverhang;
    @XmlAttribute(name = "rowalign")
    protected List<Verticalalign> rowalign;
    @XmlAttribute(name = "rowlines")
    protected List<Linestyle> rowlines;
    @XmlAttribute(name = "rowspacing")
    protected List<String> rowspacing;
    @XmlAttribute(name = "rowspan")
    protected BigInteger rowspan;
    @XmlAttribute(name = "rquote")
    @XmlSchemaType(name = "anySimpleType")
    protected String rquote;
    @XmlAttribute(name = "rspace")
    protected String rspace;
    @XmlAttribute(name = "selection")
    protected BigInteger selection;
    @XmlAttribute(name = "separator")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String separator;
    @XmlAttribute(name = "separators")
    @XmlSchemaType(name = "anySimpleType")
    protected String separators;
    @XmlAttribute(name = "shift")
    protected BigInteger shift;
    @XmlAttribute(name = "side")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String side;
    @XmlAttribute(name = "stackalign")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String stackalign;
    @XmlAttribute(name = "stretchy")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String stretchy;
    @XmlAttribute(name = "subscriptshift")
    protected String subscriptshift;
    @XmlAttribute(name = "superscriptshift")
    protected String superscriptshift;
    @XmlAttribute(name = "symmetric")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String symmetric;
    @XmlAttribute(name = "valign")
    protected String valign;
    @XmlAttribute(name = "width")
    protected String width;
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
    @XmlAttribute(name = "scriptlevel")
    protected BigInteger scriptlevel;
    @XmlAttribute(name = "displaystyle")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String displaystyle;
    @XmlAttribute(name = "scriptsizemultiplier")
    protected BigDecimal scriptsizemultiplier;
    @XmlAttribute(name = "scriptminsize")
    protected String scriptminsize;
    @XmlAttribute(name = "infixlinebreakstyle")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String infixlinebreakstyle;
    @XmlAttribute(name = "decimalpoint")
    protected String decimalpoint;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Obtient la valeur de la propriété veryverythinmathspace.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getVeryverythinmathspace() {
        return veryverythinmathspace;
    }

    /**
     * Définit la valeur de la propriété veryverythinmathspace.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setVeryverythinmathspace(String value) {
        this.veryverythinmathspace = value;
    }

    /**
     * Obtient la valeur de la propriété verythinmathspace.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getVerythinmathspace() {
        return verythinmathspace;
    }

    /**
     * Définit la valeur de la propriété verythinmathspace.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setVerythinmathspace(String value) {
        this.verythinmathspace = value;
    }

    /**
     * Obtient la valeur de la propriété thinmathspace.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getThinmathspace() {
        return thinmathspace;
    }

    /**
     * Définit la valeur de la propriété thinmathspace.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setThinmathspace(String value) {
        this.thinmathspace = value;
    }

    /**
     * Obtient la valeur de la propriété mediummathspace.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMediummathspace() {
        return mediummathspace;
    }

    /**
     * Définit la valeur de la propriété mediummathspace.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMediummathspace(String value) {
        this.mediummathspace = value;
    }

    /**
     * Obtient la valeur de la propriété thickmathspace.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getThickmathspace() {
        return thickmathspace;
    }

    /**
     * Définit la valeur de la propriété thickmathspace.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setThickmathspace(String value) {
        this.thickmathspace = value;
    }

    /**
     * Obtient la valeur de la propriété verythickmathspace.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getVerythickmathspace() {
        return verythickmathspace;
    }

    /**
     * Définit la valeur de la propriété verythickmathspace.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setVerythickmathspace(String value) {
        this.verythickmathspace = value;
    }

    /**
     * Obtient la valeur de la propriété veryverythickmathspace.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getVeryverythickmathspace() {
        return veryverythickmathspace;
    }

    /**
     * Définit la valeur de la propriété veryverythickmathspace.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setVeryverythickmathspace(String value) {
        this.veryverythickmathspace = value;
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
     * Obtient la valeur de la propriété accent.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAccent() {
        return accent;
    }

    /**
     * Définit la valeur de la propriété accent.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAccent(String value) {
        this.accent = value;
    }

    /**
     * Obtient la valeur de la propriété accentunder.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAccentunder() {
        return accentunder;
    }

    /**
     * Définit la valeur de la propriété accentunder.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAccentunder(String value) {
        this.accentunder = value;
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
     * Obtient la valeur de la propriété bevelled.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getBevelled() {
        return bevelled;
    }

    /**
     * Définit la valeur de la propriété bevelled.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setBevelled(String value) {
        this.bevelled = value;
    }

    /**
     * Obtient la valeur de la propriété charalign.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCharalign() {
        return charalign;
    }

    /**
     * Définit la valeur de la propriété charalign.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCharalign(String value) {
        this.charalign = value;
    }

    /**
     * Obtient la valeur de la propriété charspacing.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCharspacing() {
        return charspacing;
    }

    /**
     * Définit la valeur de la propriété charspacing.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCharspacing(String value) {
        this.charspacing = value;
    }

    /**
     * Obtient la valeur de la propriété close.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getClose() {
        return close;
    }

    /**
     * Définit la valeur de la propriété close.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setClose(String value) {
        this.close = value;
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
     * Gets the value of the crossout property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the crossout property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCrossout().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getCrossout() {
        if (crossout == null) {
            crossout = new ArrayList<String>();
        }
        return this.crossout;
    }

    /**
     * Obtient la valeur de la propriété denomalign.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDenomalign() {
        return denomalign;
    }

    /**
     * Définit la valeur de la propriété denomalign.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDenomalign(String value) {
        this.denomalign = value;
    }

    /**
     * Obtient la valeur de la propriété depth.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDepth() {
        return depth;
    }

    /**
     * Définit la valeur de la propriété depth.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDepth(String value) {
        this.depth = value;
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
     * Obtient la valeur de la propriété edge.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getEdge() {
        return edge;
    }

    /**
     * Définit la valeur de la propriété edge.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setEdge(String value) {
        this.edge = value;
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
     * Obtient la valeur de la propriété fence.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFence() {
        return fence;
    }

    /**
     * Définit la valeur de la propriété fence.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFence(String value) {
        this.fence = value;
    }

    /**
     * Obtient la valeur de la propriété form.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getForm() {
        return form;
    }

    /**
     * Définit la valeur de la propriété form.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setForm(String value) {
        this.form = value;
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
     * Obtient la valeur de la propriété height.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getHeight() {
        return height;
    }

    /**
     * Définit la valeur de la propriété height.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setHeight(String value) {
        this.height = value;
    }

    /**
     * Obtient la valeur de la propriété indentalign.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getIndentalign() {
        return indentalign;
    }

    /**
     * Définit la valeur de la propriété indentalign.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setIndentalign(String value) {
        this.indentalign = value;
    }

    /**
     * Obtient la valeur de la propriété indentalignfirst.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getIndentalignfirst() {
        return indentalignfirst;
    }

    /**
     * Définit la valeur de la propriété indentalignfirst.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setIndentalignfirst(String value) {
        this.indentalignfirst = value;
    }

    /**
     * Obtient la valeur de la propriété indentalignlast.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getIndentalignlast() {
        return indentalignlast;
    }

    /**
     * Définit la valeur de la propriété indentalignlast.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setIndentalignlast(String value) {
        this.indentalignlast = value;
    }

    /**
     * Obtient la valeur de la propriété indentshift.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getIndentshift() {
        return indentshift;
    }

    /**
     * Définit la valeur de la propriété indentshift.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setIndentshift(String value) {
        this.indentshift = value;
    }

    /**
     * Obtient la valeur de la propriété indentshiftfirst.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getIndentshiftfirst() {
        return indentshiftfirst;
    }

    /**
     * Définit la valeur de la propriété indentshiftfirst.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setIndentshiftfirst(String value) {
        this.indentshiftfirst = value;
    }

    /**
     * Obtient la valeur de la propriété indentshiftlast.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getIndentshiftlast() {
        return indentshiftlast;
    }

    /**
     * Définit la valeur de la propriété indentshiftlast.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setIndentshiftlast(String value) {
        this.indentshiftlast = value;
    }

    /**
     * Obtient la valeur de la propriété indenttarget.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getIndenttarget() {
        return indenttarget;
    }

    /**
     * Définit la valeur de la propriété indenttarget.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setIndenttarget(String value) {
        this.indenttarget = value;
    }

    /**
     * Obtient la valeur de la propriété largeop.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLargeop() {
        return largeop;
    }

    /**
     * Définit la valeur de la propriété largeop.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLargeop(String value) {
        this.largeop = value;
    }

    /**
     * Obtient la valeur de la propriété leftoverhang.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLeftoverhang() {
        return leftoverhang;
    }

    /**
     * Définit la valeur de la propriété leftoverhang.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLeftoverhang(String value) {
        this.leftoverhang = value;
    }

    /**
     * Obtient la valeur de la propriété length.
     *
     * @return
     *     possible object is
     *     {@link BigInteger }
     *
     */
    public BigInteger getLength() {
        return length;
    }

    /**
     * Définit la valeur de la propriété length.
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *
     */
    public void setLength(BigInteger value) {
        this.length = value;
    }

    /**
     * Obtient la valeur de la propriété linebreak.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLinebreak() {
        return linebreak;
    }

    /**
     * Définit la valeur de la propriété linebreak.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLinebreak(String value) {
        this.linebreak = value;
    }

    /**
     * Obtient la valeur de la propriété linebreakmultchar.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLinebreakmultchar() {
        return linebreakmultchar;
    }

    /**
     * Définit la valeur de la propriété linebreakmultchar.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLinebreakmultchar(String value) {
        this.linebreakmultchar = value;
    }

    /**
     * Obtient la valeur de la propriété linebreakstyle.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLinebreakstyle() {
        return linebreakstyle;
    }

    /**
     * Définit la valeur de la propriété linebreakstyle.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLinebreakstyle(String value) {
        this.linebreakstyle = value;
    }

    /**
     * Obtient la valeur de la propriété lineleading.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLineleading() {
        return lineleading;
    }

    /**
     * Définit la valeur de la propriété lineleading.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLineleading(String value) {
        this.lineleading = value;
    }

    /**
     * Obtient la valeur de la propriété linethickness.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLinethickness() {
        return linethickness;
    }

    /**
     * Définit la valeur de la propriété linethickness.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLinethickness(String value) {
        this.linethickness = value;
    }

    /**
     * Obtient la valeur de la propriété location.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLocation() {
        return location;
    }

    /**
     * Définit la valeur de la propriété location.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLocation(String value) {
        this.location = value;
    }

    /**
     * Obtient la valeur de la propriété longdivstyle.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLongdivstyle() {
        return longdivstyle;
    }

    /**
     * Définit la valeur de la propriété longdivstyle.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLongdivstyle(String value) {
        this.longdivstyle = value;
    }

    /**
     * Obtient la valeur de la propriété lquote.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLquote() {
        return lquote;
    }

    /**
     * Définit la valeur de la propriété lquote.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLquote(String value) {
        this.lquote = value;
    }

    /**
     * Obtient la valeur de la propriété lspace.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLspace() {
        return lspace;
    }

    /**
     * Définit la valeur de la propriété lspace.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLspace(String value) {
        this.lspace = value;
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
     * Obtient la valeur de la propriété maxsize.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMaxsize() {
        return maxsize;
    }

    /**
     * Définit la valeur de la propriété maxsize.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMaxsize(String value) {
        this.maxsize = value;
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
     * Obtient la valeur de la propriété minsize.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMinsize() {
        return minsize;
    }

    /**
     * Définit la valeur de la propriété minsize.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMinsize(String value) {
        this.minsize = value;
    }

    /**
     * Obtient la valeur de la propriété movablelimits.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMovablelimits() {
        return movablelimits;
    }

    /**
     * Définit la valeur de la propriété movablelimits.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMovablelimits(String value) {
        this.movablelimits = value;
    }

    /**
     * Obtient la valeur de la propriété mslinethickness.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMslinethickness() {
        return mslinethickness;
    }

    /**
     * Définit la valeur de la propriété mslinethickness.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMslinethickness(String value) {
        this.mslinethickness = value;
    }

    /**
     * Obtient la valeur de la propriété notation.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getNotation() {
        return notation;
    }

    /**
     * Définit la valeur de la propriété notation.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setNotation(String value) {
        this.notation = value;
    }

    /**
     * Obtient la valeur de la propriété numalign.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getNumalign() {
        return numalign;
    }

    /**
     * Définit la valeur de la propriété numalign.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setNumalign(String value) {
        this.numalign = value;
    }

    /**
     * Obtient la valeur de la propriété open.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOpen() {
        return open;
    }

    /**
     * Définit la valeur de la propriété open.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOpen(String value) {
        this.open = value;
    }

    /**
     * Obtient la valeur de la propriété position.
     *
     * @return
     *     possible object is
     *     {@link BigInteger }
     *
     */
    public BigInteger getPosition() {
        return position;
    }

    /**
     * Définit la valeur de la propriété position.
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *
     */
    public void setPosition(BigInteger value) {
        this.position = value;
    }

    /**
     * Obtient la valeur de la propriété rightoverhang.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRightoverhang() {
        return rightoverhang;
    }

    /**
     * Définit la valeur de la propriété rightoverhang.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRightoverhang(String value) {
        this.rightoverhang = value;
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
     * Obtient la valeur de la propriété rquote.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRquote() {
        return rquote;
    }

    /**
     * Définit la valeur de la propriété rquote.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRquote(String value) {
        this.rquote = value;
    }

    /**
     * Obtient la valeur de la propriété rspace.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRspace() {
        return rspace;
    }

    /**
     * Définit la valeur de la propriété rspace.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRspace(String value) {
        this.rspace = value;
    }

    /**
     * Obtient la valeur de la propriété selection.
     *
     * @return
     *     possible object is
     *     {@link BigInteger }
     *
     */
    public BigInteger getSelection() {
        return selection;
    }

    /**
     * Définit la valeur de la propriété selection.
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *
     */
    public void setSelection(BigInteger value) {
        this.selection = value;
    }

    /**
     * Obtient la valeur de la propriété separator.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSeparator() {
        return separator;
    }

    /**
     * Définit la valeur de la propriété separator.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSeparator(String value) {
        this.separator = value;
    }

    /**
     * Obtient la valeur de la propriété separators.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSeparators() {
        return separators;
    }

    /**
     * Définit la valeur de la propriété separators.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSeparators(String value) {
        this.separators = value;
    }

    /**
     * Obtient la valeur de la propriété shift.
     *
     * @return
     *     possible object is
     *     {@link BigInteger }
     *
     */
    public BigInteger getShift() {
        return shift;
    }

    /**
     * Définit la valeur de la propriété shift.
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *
     */
    public void setShift(BigInteger value) {
        this.shift = value;
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
     * Obtient la valeur de la propriété stackalign.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getStackalign() {
        return stackalign;
    }

    /**
     * Définit la valeur de la propriété stackalign.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setStackalign(String value) {
        this.stackalign = value;
    }

    /**
     * Obtient la valeur de la propriété stretchy.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getStretchy() {
        return stretchy;
    }

    /**
     * Définit la valeur de la propriété stretchy.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setStretchy(String value) {
        this.stretchy = value;
    }

    /**
     * Obtient la valeur de la propriété subscriptshift.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSubscriptshift() {
        return subscriptshift;
    }

    /**
     * Définit la valeur de la propriété subscriptshift.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSubscriptshift(String value) {
        this.subscriptshift = value;
    }

    /**
     * Obtient la valeur de la propriété superscriptshift.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSuperscriptshift() {
        return superscriptshift;
    }

    /**
     * Définit la valeur de la propriété superscriptshift.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSuperscriptshift(String value) {
        this.superscriptshift = value;
    }

    /**
     * Obtient la valeur de la propriété symmetric.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSymmetric() {
        return symmetric;
    }

    /**
     * Définit la valeur de la propriété symmetric.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSymmetric(String value) {
        this.symmetric = value;
    }

    /**
     * Obtient la valeur de la propriété valign.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getValign() {
        return valign;
    }

    /**
     * Définit la valeur de la propriété valign.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setValign(String value) {
        this.valign = value;
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
     * Obtient la valeur de la propriété scriptlevel.
     *
     * @return
     *     possible object is
     *     {@link BigInteger }
     *
     */
    public BigInteger getScriptlevel() {
        return scriptlevel;
    }

    /**
     * Définit la valeur de la propriété scriptlevel.
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *
     */
    public void setScriptlevel(BigInteger value) {
        this.scriptlevel = value;
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
     * Obtient la valeur de la propriété scriptsizemultiplier.
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getScriptsizemultiplier() {
        return scriptsizemultiplier;
    }

    /**
     * Définit la valeur de la propriété scriptsizemultiplier.
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setScriptsizemultiplier(BigDecimal value) {
        this.scriptsizemultiplier = value;
    }

    /**
     * Obtient la valeur de la propriété scriptminsize.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getScriptminsize() {
        return scriptminsize;
    }

    /**
     * Définit la valeur de la propriété scriptminsize.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setScriptminsize(String value) {
        this.scriptminsize = value;
    }

    /**
     * Obtient la valeur de la propriété infixlinebreakstyle.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getInfixlinebreakstyle() {
        return infixlinebreakstyle;
    }

    /**
     * Définit la valeur de la propriété infixlinebreakstyle.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setInfixlinebreakstyle(String value) {
        this.infixlinebreakstyle = value;
    }

    /**
     * Obtient la valeur de la propriété decimalpoint.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDecimalpoint() {
        return decimalpoint;
    }

    /**
     * Définit la valeur de la propriété decimalpoint.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDecimalpoint(String value) {
        this.decimalpoint = value;
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
