/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.ncwms;

import java.util.logging.Logger;

import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.util.logging.Logging;

/**
 * Abstract implementation of {@link NcGetTimeseriesRequest}, which defines the parameters for
 * a GetTimeseries request.
 *
 * @author Fabien BERNARD (Geomatys)
 * @module pending
 */
public abstract class AbstractNcGetTimeseries extends AbstractNcGetFeatureInfo implements NcGetTimeseriesRequest {
    
    /**
     * Default logger for all GetTimeseries requests.
     */
    protected static final Logger LOGGER = Logging.getLogger(NcGetVerticalProfile.class);
    
    private String dateBegin = null;
    private String dateEnd = null;
    

    /**
     * {@inheritDoc}
     */
    protected AbstractNcGetTimeseries(final String serverURL, final String version, final ClientSecurity security) {
        super(serverURL, version, security);
    }
        
    /**
     * {@inheritDoc}
     */
    @Override
    public String getDateBegin() {
        return dateBegin;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDateBegin(final String dateBegin) {
        this.dateBegin = dateBegin;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getDateEnd() {
        return dateEnd;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDateEnd(final String dateEnd) {
        this.dateEnd = dateEnd;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void prepareParameters() {
        super.prepareParameters();

        // Tests if the mandatory parameters are available
        if (dateBegin == null || dateEnd == null)
            throw new IllegalArgumentException("Must provide a valid TIME period parameter");    
        
        requestParameters.put("TIME", dateBegin + "/" + dateEnd);
    }
}
