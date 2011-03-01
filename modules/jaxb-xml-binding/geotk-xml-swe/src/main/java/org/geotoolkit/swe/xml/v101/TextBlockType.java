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
package org.geotoolkit.swe.xml.v101;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.swe.xml.TextBlock;
import org.geotoolkit.util.Utilities;

/**
 * Textual Encoding of the data.
 *
 * @version $Id:
 * @author Guilhem Legal
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TextBlock", propOrder = {
    "tokenSeparator",
    "decimalSeparator",
    "blockSeparator"})
public class TextBlockType extends AbstractEncodingType implements TextBlock {

    public final static TextBlockType DEFAULT_ENCODING = new TextBlockType("encoding-1", ",", "@@", ".");

    public final static TextBlockType CSV_ENCODING     = new TextBlockType("encoding-CSV", ",", "\n", ".");

    /**
     * chaine de 3 caractere maximum pour separer les tokens.
     */
    @XmlAttribute(required = true)
    private String tokenSeparator;
    
    /**
     * chaine de 3 caractere maximum pour separer les blocks.
     */
    @XmlAttribute(required = true)
    private String blockSeparator;
    
    /**
     * un caractere pour separer les decimaux.
     */
    @XmlAttribute(required = true)
    private String decimalSeparator;
    
    /**
     * Constructeur utilisé par jaxB.
     */
    public TextBlockType() {}

    public TextBlockType(final TextBlock tb) {
        super(tb);
        if (tb != null) {
            this.blockSeparator   = tb.getBlockSeparator();
            this.decimalSeparator = tb.getDecimalSeparator();
            this.tokenSeparator   = tb.getTokenSeparator();
        }
    }
    
    /**
     * Crée un nouveau encodage de texte.
     */
    public TextBlockType(final String id, final String tokenSeparator, final String blockSeparator, final String decimalSeparator) {
        super(id);
        this.tokenSeparator   = tokenSeparator;
        this.blockSeparator   = blockSeparator;
        this.decimalSeparator = decimalSeparator;
    }

    /**
     * {@inheritDoc}
     */
    public String getTokenSeparator() {
        return tokenSeparator;
    }

    /**
     * {@inheritDoc}
     */
    public String getBlockSeparator() {
        return blockSeparator;
    }

    /**
     * {@inheritDoc}
     */
    public String getDecimalSeparator() {
        return decimalSeparator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof TextBlockType && super.equals(object)) {
            final TextBlockType that = (TextBlockType) object;
            return Utilities.equals(this.tokenSeparator,          that.tokenSeparator)   &&
                   Utilities.equals(this.blockSeparator,    that.blockSeparator)   && 
                   Utilities.equals(this.decimalSeparator,   that.decimalSeparator) ;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 11 * hash + (this.tokenSeparator != null ? this.tokenSeparator.hashCode() : 0);
        hash = 11 * hash + (this.blockSeparator != null ? this.blockSeparator.hashCode() : 0);
        hash = 11 * hash + (this.decimalSeparator != null? this.decimalSeparator.hashCode() : 0);
        return hash;
    }
    
    /**
     * Retourne une representation de l'objet (debug).
     */
    @Override
    public String toString() {
        return '[' + this.getClass().getSimpleName() + "]:" + super.toString()+ " " + this.blockSeparator 
                + '|' + this.decimalSeparator + '|' + this.tokenSeparator;
    }
    
    
}
