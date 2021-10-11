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

import java.util.Objects;
import org.geotoolkit.gml.xml.v311.ReferenceType;
import org.geotoolkit.internal.sql.Entry;

/**
 *
 * @author legal
 */
public class OfferingProcedureType implements Entry{

    /**
     * The offering identifier.
     */
    private String idOffering;

    /**
     * the process associated whith this offering.
     */
    private ReferenceType component;

    /**
     * Build a new link between a procedure and an offering.
     */
    public OfferingProcedureType(final String idOffering, final ReferenceType component) {
        this.idOffering = idOffering;
        this.component  = component;
    }

    public OfferingProcedureType(final String idOffering, final String componentRef) {
        this.idOffering = idOffering;
        this.component  = new ReferenceType(null, componentRef);
    }

     public String getName() {
        if (component != null) {
            return component.getHref();
        }
        return null;
    }

    @Override
     public String getIdentifier() {
        if (component != null) {
            return component.getHref();
        }
        return null;
    }

    /**
     * Return the offering id.
     */
    public String getIdOffering() {
        return idOffering;
    }

    /**
     * Return the process associated with this offering.
     */
    public ReferenceType getComponent() {
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
        if (object instanceof OfferingProcedureType && super.equals(object)) {
            final OfferingProcedureType that = (OfferingProcedureType) object;
            return Objects.equals(this.idOffering, that.idOffering) &&
                   Objects.equals(this.component,  that.component);
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
