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

package org.geotoolkit.sos;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import org.geotoolkit.client.AbstractRequest;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.xml.MarshallerPool;

/**
 * Abstract base for all SOS request
 *
 * @author Guilhem Legal (Geomatys)
 * @module pending
 */
public class AbstractSOSRequest extends AbstractRequest {

    /**
     * Logger specific for this implementation of {@link Request}.
     */
    protected static final Logger LOGGER = Logging.getLogger("org.geotoolkit.sos");

    protected static final MarshallerPool POOL;
    static {
        MarshallerPool temp = null;
        try {
            temp = new MarshallerPool("org.geotoolkit.sos.xml.v100:" +
                                      "org.geotoolkit.gml.xml.v311:" +
                                      "org.geotoolkit.swe.xml.v100:" +
                                      "org.geotoolkit.swe.xml.v101:" +
                                      "org.geotoolkit.observation.xml.v100:" +
                                      "org.geotoolkit.sampling.xml.v100:" +
                                      "org.geotoolkit.sml.xml.v100:" +
                                      "org.geotoolkit.sml.xml.v101:" +
                                      "org.geotoolkit.internal.jaxb.geometry");
        } catch (JAXBException ex) {
            LOGGER.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
        }
        POOL = temp;
    }

    public AbstractSOSRequest(final String serverURL) {
        super(serverURL);
    }
}
