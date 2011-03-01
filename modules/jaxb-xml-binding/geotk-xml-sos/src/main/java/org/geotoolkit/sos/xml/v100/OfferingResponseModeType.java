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
import org.geotoolkit.util.Utilities;

/**
 *
 * @author Guilhem Legal
 * @module pending
 */
public class OfferingResponseModeType implements Entry {

    /**
     * L'identifiant de l'offering.
     */
    private String idOffering;
    
    /**
     * Le mode de reponse associe a cet offering.
     */
    private ResponseModeType mode;
    
    /**
     * Cree une nouveau lien entre une procedure et un offering. 
     */
    public OfferingResponseModeType(final String idOffering, final ResponseModeType mode) {
        this.idOffering = idOffering;
        this.mode  = mode;
    }

    public String getName() {
        if (mode != null) {
            return mode.value();
        }
        return null;
    }

    public String getIdentifier() {
        if (mode != null) {
            return mode.value();
        }
        return null;
    }

    /**
     * Retourne l'id de l'offering
     */
    public String getIdOffering() {
        return idOffering;
    }

    /**
     * Retourne le mode de reponse associe.
     */
    public ResponseModeType getMode() {
        return mode;
    }

    /**
     * Verifie si cette entree est identique a l'objet specifie.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof OfferingResponseModeType && super.equals(object)) {
            final OfferingResponseModeType that = (OfferingResponseModeType) object;
            return Utilities.equals(this.idOffering, that.idOffering) &&
                   Utilities.equals(this.mode,       that.mode);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (this.idOffering != null ? this.idOffering.hashCode() : 0);
        hash = 83 * hash + (this.mode != null ? this.mode.hashCode() : 0);
        return hash;
    }
}
