/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.wfs.xml;

import java.util.List;
import javax.xml.namespace.QName;
import org.geotoolkit.ows.xml.BoundingBox;


/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public interface FeatureType {

    QName getName();

    void setName(final QName value);

    String getDefaultCRS();

    void setDefaultCRS(final String CRS);

    void setOtherCRS(final List<String> otherCRS);

    void setAbstract(final String value);

    void addKeywords(final List<String> values);

    void addMetadataURL(final String value, final String type, final String format);

    List<? extends BoundingBox> getBoundingBox();
}
