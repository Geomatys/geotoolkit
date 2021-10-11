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

//jaxB import
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

// Constellation dependencies
import javax.xml.bind.annotation.XmlType;
import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.geotoolkit.gml.xml.v311.DefinitionType;
import org.apache.sis.util.ComparisonMode;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.swe.xml.Phenomenon;
import org.opengis.metadata.Identifier;


/**
 * Implementation of an entry representing a {@linkplain Phenomenon phenomenon}.
 *
 * @version $Id: PhenomenonType.java 1286 2009-01-22 15:28:09Z glegal $
 * @author Antoine Hnawia
 * @author Guilhem Legal
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Phenomenon")
@XmlRootElement(name = "phenomenon")
@XmlSeeAlso({ CompoundPhenomenonType.class })
public class PhenomenonType extends DefinitionType implements Phenomenon {
    /**
     * Pour compatibilités entre les enregistrements binaires de différentes versions.
     */
    private static final long serialVersionUID = 5140595674231914861L;

    @XmlTransient
    private String definition;

    /**
     * Empty constructor used by JAXB.
     */
    protected PhenomenonType(){}

    /**
     * Construit un nouveau phénomène du nom spécifié.
     *
     * @param id L'identifiant de ce phenomene.
     * @param name Le nom du phénomène.
     */
    public PhenomenonType(final String id, final String name) {
        super(id, name, null);
    }

    /**
     *
     * Construit un nouveau phénomène du nom spécifié.
     *
     *
     * @param id L'identifiant de ce phenomene.
     * @param name Le nom du phénomène.
     * @param definition URN de definition de ce phénomène.
     * @param description La description de ce phénomène, ou {@code null}.
     */
    public PhenomenonType(final String id, final String name, final String definition, final String description ) {
        super(id, name, description);
        this.definition = definition;
    }

    public PhenomenonType(final PhenomenonType phen) {
        super(phen);
        this.definition = phen.definition;
    }

    @Override
    public Identifier getName() {
        if (definition != null) {
            DefaultIdentifier result = new DefaultIdentifier(definition);
            if (name != null) {
                result.setDescription(new SimpleInternationalString(name));
            }
            return result;
        }
        if (name != null) {
            return new DefaultIdentifier(name);
        }
        return null;
    }

    @Override
    public void setName(final Identifier name) {
        if (name != null) {
            if (name.getDescription() != null) {
                this.name       = name.getDescription().toString();
                this.definition = name.getCode();
            } else {
                this.name = name.getCode();
            }
        } else {
            this.name = null;
        }
    }

    /**
     * Retourne un code représentant ce phenomene.
     */
    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        } else return (object instanceof PhenomenonType && super.equals(object, mode));
    }

    /**
     * Retourne une chaine de charactere representant le phenomene.
     */
    @Override
    public String toString() {
        return super.toString();
    }
}
