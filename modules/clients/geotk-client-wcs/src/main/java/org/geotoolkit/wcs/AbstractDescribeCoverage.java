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
package org.geotoolkit.wcs;

import java.net.MalformedURLException;
import java.net.URL;
import org.geotoolkit.client.AbstractRequest;
import org.geotoolkit.util.StringUtilities;


/**
 * Abstract describe coverage request.
 *
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public abstract class AbstractDescribeCoverage extends AbstractRequest implements DescribeCoverageRequest {

    protected final String version;

    private String[] coverage;

    protected AbstractDescribeCoverage(String serverURL, String version){
        super(serverURL);
        this.version = version;
    }

    @Override
    public String[] getCoverage() {
        return coverage;
    }

    @Override
    public void setCoverage(String... coverage) {
        this.coverage = coverage;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public URL getURL() throws MalformedURLException {
        requestParameters.put("SERVICE",  "WCS");
        requestParameters.put("REQUEST",  "DescribeCoverage");
        requestParameters.put("VERSION",  version);
        requestParameters.put("COVERAGE", StringUtilities.toCommaSeparatedValues(coverage));
        return super.getURL();
    }
}
