/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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
package org.geotoolkit.data.wfs;

import java.util.List;
import javax.xml.namespace.QName;

/**
 * WFS DescribeFeature mutable request interface.
 * The request shall be correctly configured before calling the getURL method.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface DescribeFeatureTypeRequest extends Request{

    /**
     * @return QName : requested type namse, can be empty
     * if not yet configured.
     */
    List<QName> getTypeNames();

    /**
     * @param typeNames : requested type names, can be null
     * if not yet configured.
     */
    void setTypeNames(List<QName> typeNames);

}
