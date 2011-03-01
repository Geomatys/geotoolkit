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
package org.geotoolkit.sos.xml.v100;

import org.geotoolkit.internal.sql.table.Entry;
import org.geotoolkit.swe.xml.v101.PhenomenonType;
import org.geotoolkit.util.Utilities;

/**
 *
 * @author Guilhem legal
 * @module pending
 */
public class OfferingPhenomenonType implements Entry{
    
    /**
     * The offering identifier.
     */
    private String idOffering;
    
    /**
     * The phenomenon associed to this offering (reference).
     */
    private PhenomenonType component;
    
    /**
     * Build a new link between a procedure and an offering. 
     */
    public OfferingPhenomenonType(final String idOffering, final PhenomenonType component) {
        this.idOffering = idOffering;
        this.component  = component;
    }

    public String getName() {
        if (component != null) {
            return component.getId();
        }
        return null;
    }

    public String getIdentifier() {
        if (component != null) {
            return component.getId();
        }
        return null;
    }

    /**
     * Return the phenomenon id.
     */
    public String getIdOffering() {
        return idOffering;
    }

    /**
     * Retourne le phénomène associé.
     */
    public PhenomenonType getComponent() {
        return component;
    }

    /**
     * Verifie si cette entree est identique a l'objet specifie.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof OfferingPhenomenonType && super.equals(object)) {
            final OfferingPhenomenonType that = (OfferingPhenomenonType) object;
            return Utilities.equals(this.idOffering, that.idOffering) &&
                   Utilities.equals(this.component,  that.component);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (this.idOffering != null ? this.idOffering.hashCode() : 0);
        hash = 83 * hash + (this.component != null ? this.component.hashCode() : 0);
        return hash;
    }
}
