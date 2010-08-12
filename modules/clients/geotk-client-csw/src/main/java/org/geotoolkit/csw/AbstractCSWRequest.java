/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.csw;

import java.util.logging.Logger;
import org.geotoolkit.client.AbstractRequest;
import org.geotoolkit.ebrim.xml.EBRIMMarshallerPool;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.xml.MarshallerPool;

/**
 * Abstract base for all CSW request
 *
 * @author Guilhem Legal (Geomatys)
 * @module pending
 */
public abstract class AbstractCSWRequest extends AbstractRequest{

    /**
     * Default logger for all DescribeRecord requests.
     */
    protected static final Logger LOGGER = Logging.getLogger("org.geotoolkit.csw");

    protected static final MarshallerPool POOL;
    
    static {
        POOL = EBRIMMarshallerPool.getInstance();
    }

    public AbstractCSWRequest(final String serverURL) {
        super(serverURL);
    }
}
