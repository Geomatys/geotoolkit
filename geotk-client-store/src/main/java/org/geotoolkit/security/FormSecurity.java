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
package org.geotoolkit.security;

import java.net.URLConnection;
import org.apache.sis.util.ArgumentChecks;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class FormSecurity extends DefaultClientSecurity {

    private String sessionID;

    public FormSecurity() {

    }

    public FormSecurity(String sessionID) {
        ArgumentChecks.ensureNonNull("sessionID", sessionID);
        this.sessionID = sessionID;
    }

    @Override
    public URLConnection secure(URLConnection cnx) {
        cnx = super.secure(cnx);
        cnx.setRequestProperty("Cookie", "JSESSIONID=" + sessionID);
        return cnx;
    }

    /**
     * @return the sessionID
     */
    public String getSessionID() {
        return sessionID;
    }

    /**
     * @param sessionID the sessionID to set
     */
    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

}
