/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.ogcwxs.request.observation;

import javax.xml.namespace.QName;
import org.geotoolkit.ogcwxs.request.RequestParameters;

/**
 * To retrieve a description of the model used for observation results of a given type.
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class DescribeResultModel extends RequestParameters {

    private QName resultName;

    /**
     * Name of the result type to describe. (required)
     *
     * @return the resultName
     */
    public QName getResultName() {
        return resultName;
    }

    public void setResultName(QName resultName) {
        this.resultName = resultName;
    }

    public DescribeResultModel resultName(QName resultName) {
        setResultName(resultName);
        return this;
    }

}
