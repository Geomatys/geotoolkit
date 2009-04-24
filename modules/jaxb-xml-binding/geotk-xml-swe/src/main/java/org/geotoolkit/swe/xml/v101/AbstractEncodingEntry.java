/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2005, Institut de Recherche pour le Développement
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
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
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.swe.xml.AbstractEncoding;
import org.geotoolkit.util.Utilities;

/**
 * Cette classe n'as pas vraiment lieu d'etre.
 * Elle as été crée pour les besoin de JAXB qui ne supporte pas les interface.
 *
 * @version $Id:
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({TextBlockEntry.class})
@XmlType(name="AbstractEncoding")
public class AbstractEncodingEntry implements AbstractEncoding {
    
    /**
     * The encoding identifier.
     */
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    private String id;
    
    /**
     * constructor used by jaxB
     */
    AbstractEncodingEntry() {}

    /**
     *  An abstract encoding. used like super constructor
     */
    protected AbstractEncodingEntry(String id) {
        this.id = id;
    }
    
    public String getId() {
        return id;
    }

    public String getName() {
        return id;
    }
    
    /**
     * Returne the numeric code identifiyng this entry.
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }
    
    /**
     * Verify that this entry is identical to the specified object. 
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof AbstractEncodingEntry) {
            final AbstractEncodingEntry that = (AbstractEncodingEntry) object;
            return Utilities.equals(this.id, that.id);
        }
        return false;
    }
    
}
