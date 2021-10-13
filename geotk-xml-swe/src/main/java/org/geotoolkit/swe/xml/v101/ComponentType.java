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

import java.util.Objects;
import org.geotoolkit.internal.sql.Entry;

/**
 * Une liste de phénomèmene pour un phenoméne composé.
 *
 * @author Guilhem Legal
 */
public class ComponentType implements Entry{

    /**
     * L'identifiant du phénomène composé.
     */
    private String idCompositePhenomenon;

    /**
     * Le phenomene associé a ce phenomene composé.
     */
    private PhenomenonType component;

    /**
     * Crée une nouvelle liste de phénomene.
     */
    public ComponentType(final String idCompositePhenomenon, final PhenomenonType component) {
        this.idCompositePhenomenon = idCompositePhenomenon;
        this.component             = component;
    }

    public String getName() {
        if (component != null)
            return component.getId();
        return null;
    }

    public String getIdentifier() {
        if (component != null)
            return component.getId();
        return null;
    }

    /**
     * Retourne l'id du phénomène composé.
     */
    public String getIdCompositePhenomenon() {
        return idCompositePhenomenon;
    }

    /**
     * Retourne le phénomène associé.
     */
    public PhenomenonType getComponent() {
        return component;
    }

     /**
     * Vérifie si cette entré est identique à l'objet spécifié.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof ComponentType)  {
            final ComponentType that = (ComponentType) object;

            return  Objects.equals(this.component,   that.component)   &&
                    Objects.equals(this.idCompositePhenomenon,   that.idCompositePhenomenon);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + (this.idCompositePhenomenon != null ? this.idCompositePhenomenon.hashCode() : 0);
        hash = 37 * hash + (this.component != null ? this.component.hashCode() : 0);
        return hash;
    }

}
