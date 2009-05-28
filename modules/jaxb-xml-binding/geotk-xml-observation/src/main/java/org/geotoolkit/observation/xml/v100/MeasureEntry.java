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
package org.geotoolkit.observation.xml.v100;

// Constellation dependencies
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311modified.UnitOfMeasureEntry;

// GeoTools dependencies
import org.geotoolkit.util.Utilities;

// OpenGis dependencies
import org.opengis.observation.Measure;
/**
 * Resultat d'une observation de type {linkplain Measurement measurement}.
 *
 * @version $Id:
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Measure")
public class MeasureEntry { //implements Measure{
    
    /**
     * Le non de l'unité de mesure.
     */
    private String name;

    /**
     * L'unite de la mesure
     */
    private UnitOfMeasureEntry uom;
    
    /**
     * La valeur de la mesure
     */
    private float value;
    
    /**
     * constructeur vide utilisé par jaxB
     */
    protected MeasureEntry(){}
    
    /** 
     * crée un nouveau resultat de mesure.
     *
     * @param name  Le nom/identifiant du resultat.
     * @param uom   L'unité de mesure.
     * @param value La valeur mesurée.
     */
    public MeasureEntry(final String             name,
                        final UnitOfMeasureEntry uom,
                        final float              value)
    {
        this.name = name;
        this.uom   = uom;
        this.value = value;        
    }

    public String getName() {
        return name;
    }
    
    /**
     * {@inheritDoc}
     *
     * @todo Implementer le retour des unites.
     */
    public UnitOfMeasureEntry getUom() {
        return uom;
    }

    /**
     * {@inheritDoc}
     */
    public float getValue() {
        return value;
    }
    
     /**
     * Retourne un code représentant ce resultat de mesure.
     */
    @Override
    public final int hashCode() {
        return name.hashCode();
    }

    /**
     * Vérifie si cette entré est identique à l'objet spécifié.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (super.equals(object)) {
            final MeasureEntry that = (MeasureEntry) object;
            return Utilities.equals(this.name,  that.name) &&
                   Utilities.equals(this.uom,   that.uom) &&
                   Utilities.equals(this.value, that.value) ;
        }
        return false;
    }
    
    
    /**
     * Retourne une description de l'objet (debug).
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("name=").append(name).append(" value=").append(value).append('\n').append("uom =").append(uom.toString());
        return  s.toString();
    }
    
}
