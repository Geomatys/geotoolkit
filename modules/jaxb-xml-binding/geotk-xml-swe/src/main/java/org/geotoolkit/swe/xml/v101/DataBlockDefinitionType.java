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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.internal.sql.table.Entry;
import org.geotoolkit.swe.xml.AbstractDataComponent;
import org.geotoolkit.swe.xml.DataBlockDefinition;
import org.geotoolkit.util.Utilities;

/**
 * Resultat d'une observation de type DataBlockDefinition.
 *
 * @version $Id:
 * @author Guilhem Legal
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataBlockDefinition", propOrder = {
    "components",
    "encoding"})
    public class DataBlockDefinitionType implements DataBlockDefinition, Entry {
    
    /**
     * L'identifiant du resultat.
     */
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    private String id;
    
    /**
     * Liste de composant Data record.
     */
    private Collection<? extends AbstractDataComponentType> components;
    
    /**
     * Decrit l'encodage des données.
     */
    private AbstractEncodingPropertyType encoding;
    
    /**
     * constructeur utilisé par jaxB
     */
    DataBlockDefinitionType() {}

    public DataBlockDefinitionType(final DataBlockDefinition db) {
        if (db != null) {
            this.id = db.getId();
            if (db.getEncoding() != null) {
                this.encoding = new AbstractEncodingPropertyType(db.getEncoding());
            }
            if (db.getComponents() != null) {
                List<AbstractDataComponentType> tmp = new ArrayList<AbstractDataComponentType>();
                for (AbstractDataComponent c : db.getComponents()) {
                    // TODO disociate types
                    tmp.add(new AbstractDataComponentType(c));
                }
                this.components = tmp;
            }
        }
    }
    
    /**
     * créé un nouveau resultat d'observation.
     * Liste de valeur decrite dans swe:DatablockDefinition de type simple,
     * pour valeur scalaire ou textuelle.
     *
     * @param id l'identifiant du resultat.
     * @param components liste de composant data record.
     * @param encoding encodage des données.
     */
    public DataBlockDefinitionType(final String id, final Collection<? extends AbstractDataComponentType> components,
            final AbstractEncodingType encoding) {
        this.id         = id;
        this.components = components;
        this.encoding   = new AbstractEncodingPropertyType(encoding);
    }
    
    /**
     * {@inheritDoc}
     */
    public String getId() {
        return id;
    }

    public String getIdentifier() {
        return id;
    }

    public String getName() {
        return id;
    }
    
    /**
     * {@inheritDoc}
     */
    public Collection<? extends AbstractDataComponentType> getComponents() {
        return components;
    }
    
    /**
     * {@inheritDoc}
     */
    public AbstractEncodingPropertyType getEncoding() {
        return encoding;
    }
    
    /**
     * Retourne un code représentant ce dataBlock.
     */
    @Override
    public final int hashCode() {
        return id.hashCode();
    }
    
    /**
     * Vérifie si cette entré est identique à l'objet spécifié.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        
        final DataBlockDefinitionType that = (DataBlockDefinitionType) object;
        if (this.components.size() != that.components.size())
            return false;
        
        Iterator<? extends AbstractDataComponentType> i  = this.components.iterator();
        Iterator<? extends AbstractDataComponentType> i2 = that.components.iterator();
        while (i.hasNext() && i2.hasNext()) {
            if (!Utilities.equals(i.next(), i2.next())) return false;
        }
        return Utilities.equals(this.id,         that.id) &&
               Utilities.equals(this.encoding,   that.encoding) ;
    }
    
    /**
     * Retourne une representation de l'objet (debug).
     */
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        final String lineSeparator = System.getProperty("line.separator", "\n");
        buffer.append('[').append(this.getClass().getSimpleName()).append("]:").append(id).append(lineSeparator);
        buffer.append("encoding: ").append(this.encoding.toString()).append(lineSeparator);
        appendTo(buffer, "", lineSeparator);
        return buffer.toString();
    }
    
    /**
     * Ajoute la description des composants du dataBlock definition.
     */
    private void appendTo(final StringBuilder buffer, String margin, final String lineSeparator) {
        buffer.append("components: ").append(lineSeparator);
        margin += "  ";
        for (final AbstractDataComponentType a : components) {
            buffer.append(margin).append(a.toString()).append(lineSeparator);
        }
    }
    
}
