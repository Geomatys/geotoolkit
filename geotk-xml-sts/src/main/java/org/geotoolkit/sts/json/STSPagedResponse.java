/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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
package org.geotoolkit.sts.json;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public interface STSPagedResponse extends STSResponse {

    List<? extends STSEntityResponse> getValue();

    BigDecimal getIotCount();

    void setIotCount(BigDecimal iotCount);

    String getIotNextLink();

    void setIotNextLink(String iotNextLink);
}
