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
package org.geotoolkit.gml.xml.v311;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.internal.sql.Entry;
import org.opengis.observation.BaseUnit;

/**
 * Unité de mesure.
 *
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BaseUnit")
public class UnitOfMeasureEntry implements BaseUnit, Entry {
    /**
     * l'identifiant de l'unité ( exemple cm, és, ...)
     */
    @XmlAttribute
    private String id;

    /**
     * Le nom de l'unité.
     */
    private String name;

    /**
     * le type de l'unité de mesure (longueur, temporelle, ...).
     */
    private String quantityType;

    /**
     * Le system qui definit cette unité de mesure.
     */
    private String unitsSystem;

    /**
     * constructeur videé utilisé par JAXB
     */
    protected UnitOfMeasureEntry() {}

    /**
     * Créé une nouvelle unité de mesure.
     */
    public UnitOfMeasureEntry(final String id, final String name, final String quantityType, final String unitsSystem) {
        this.id           = id;
        this.name         = name;
        this.quantityType = quantityType;
        this.unitsSystem  = unitsSystem;
    }

    /**
     * Retourne l'identifiant.
     */
    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getIdentifier() {
        return id;
    }

    /**
     * Retourne l'identifiant.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * retourne le type de l'unité de mesure.
     */
    @Override
    public String getQuantityType() {
        return quantityType;
    }

    /**
     * retourne le nom du systeme qui definit cette unité.
     */
    @Override
    public String getUnitsSystem() {
        return unitsSystem;
    }

    /**
     * Vérifie si cette entrée est identique à l'objet spécifié.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof UnitOfMeasureEntry) {
            final UnitOfMeasureEntry that = (UnitOfMeasureEntry) object;
            return Objects.equals(this.name,  that.name) &&
                   Objects.equals(this.id,   that.id) &&
                   Objects.equals(this.quantityType, that.quantityType) &&
                   Objects.equals(this.unitsSystem, that.unitsSystem);
            }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 61 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 61 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 61 * hash + (this.quantityType != null ? this.quantityType.hashCode() : 0);
        hash = 61 * hash + (this.unitsSystem != null ? this.unitsSystem.hashCode() : 0);
        return hash;
    }

    /**
     * Retourne une representation de l'objet.
     */
     @Override
     public String toString() {
         StringBuilder s = new StringBuilder();
         s.append(" id= ").append(id).append(" name=").append(name).append(" quantity type=")
                 .append(quantityType).append(" unitSystem=").append(unitsSystem);
         return s.toString();
     }
}
