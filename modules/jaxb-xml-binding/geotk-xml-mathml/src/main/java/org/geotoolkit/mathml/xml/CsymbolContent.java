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
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour csymbol.content complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="csymbol.content">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded" minOccurs="0">
 *         &lt;element ref="{http://www.w3.org/1998/Math/MathML}mglyph"/>
 *         &lt;element ref="{http://www.w3.org/1998/Math/MathML}PresentationExpression"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "csymbol.content", propOrder = {
    "content"
})
@XmlSeeAlso({
    Csymbol.class
})
public class CsymbolContent {

    @XmlElementRefs({
        @XmlElementRef(name = "PresentationExpression", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class),
        @XmlElementRef(name = "mglyph", namespace = "http://www.w3.org/1998/Math/MathML", type = Mglyph.class)
    })
    @XmlMixed
    protected List<Object> content;

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
     * {@link JAXBElement }{@code <}{@link Mstack }{@code >}
     * {@link JAXBElement }{@code <}{@link Msub }{@code >}
     * {@link JAXBElement }{@code <}{@link Mn }{@code >}
     * {@link JAXBElement }{@code <}{@link Mmultiscripts }{@code >}
     * {@link JAXBElement }{@code <}{@link Mi }{@code >}
     * {@link JAXBElement }{@code <}{@link Mstyle }{@code >}
     * {@link JAXBElement }{@code <}{@link Mroot }{@code >}
     * {@link JAXBElement }{@code <}{@link Object }{@code >}
     * {@link JAXBElement }{@code <}{@link Object }{@code >}
     * {@link JAXBElement }{@code <}{@link Mfrac }{@code >}
     * {@link JAXBElement }{@code <}{@link Mlongdiv }{@code >}
     * {@link JAXBElement }{@code <}{@link Mtable }{@code >}
     * {@link Mglyph }
     * {@link JAXBElement }{@code <}{@link Mfenced }{@code >}
     * {@link JAXBElement }{@code <}{@link Mrow }{@code >}
     * {@link JAXBElement }{@code <}{@link Object }{@code >}
     * {@link JAXBElement }{@code <}{@link Mtext }{@code >}
     * {@link JAXBElement }{@code <}{@link Mover }{@code >}
     * {@link JAXBElement }{@code <}{@link Munderover }{@code >}
     * {@link JAXBElement }{@code <}{@link Maligngroup }{@code >}
     * {@link JAXBElement }{@code <}{@link Malignmark }{@code >}
     * {@link JAXBElement }{@code <}{@link Maction }{@code >}
     * {@link JAXBElement }{@code <}{@link Msubsup }{@code >}
     * {@link JAXBElement }{@code <}{@link Merror }{@code >}
     * {@link JAXBElement }{@code <}{@link Munder }{@code >}
     * {@link JAXBElement }{@code <}{@link Mpadded }{@code >}
     * {@link String }
     * {@link JAXBElement }{@code <}{@link Mspace }{@code >}
     * {@link JAXBElement }{@code <}{@link Msup }{@code >}
     * {@link JAXBElement }{@code <}{@link Mphantom }{@code >}
     * {@link JAXBElement }{@code <}{@link Ms }{@code >}
     * {@link JAXBElement }{@code <}{@link Menclose }{@code >}
     * {@link JAXBElement }{@code <}{@link Msqrt }{@code >}
     * {@link JAXBElement }{@code <}{@link Mo }{@code >}
     *
     *
     */
    public List<Object> getContent() {
        if (content == null) {
            content = new ArrayList<Object>();
        }
        return this.content;
    }

}
