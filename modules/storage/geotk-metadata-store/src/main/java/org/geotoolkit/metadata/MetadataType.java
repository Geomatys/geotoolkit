/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.metadata;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public enum MetadataType {

    DUBLINCORE(false, true),
    ISO_19115(true, false),
    EBRIM(false, false),
    SENSORML(false, false),
    NATIVE(false, false),
    ISO_19110(false, false),
    CONTACT(false, false);

    public final boolean isDCtransformable;
    public final boolean isElementSetable;

    private MetadataType(final boolean isDCtransformable, final boolean isElementSetable) {
        this.isDCtransformable = isDCtransformable;
        this.isElementSetable  = isElementSetable;
    }


}
