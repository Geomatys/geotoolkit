/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
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
package org.geotoolkit.ows.xml.v200;

import java.util.Objects;
import org.geotoolkit.ows.xml.AbstractOwsContextDescription;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class OwsContextDescriptionType implements AbstractOwsContextDescription {

    private OwsContextOfferingType offering = null;

    public OwsContextDescriptionType() {

    }

    public OwsContextDescriptionType(OwsContextOfferingType offering) {
        this.offering = offering;
    }

    public OwsContextDescriptionType(String code, String contentHref) {
       this.offering = new OwsContextOfferingType(code, contentHref);
    }

    /**
     * Get offering
     *
     * @return offering
     *
     */
    @Override
    public OwsContextOfferingType getOffering() {
        return offering;
    }

    public void setOffering(OwsContextOfferingType offering) {
        this.offering = offering;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OwsContextDescriptionType descriptionTypeOwsContext = (OwsContextDescriptionType) o;
        return Objects.equals(this.offering, descriptionTypeOwsContext.offering);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(offering);
    }

}
