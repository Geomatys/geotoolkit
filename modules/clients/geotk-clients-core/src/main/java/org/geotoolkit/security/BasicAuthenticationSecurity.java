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
import net.iharder.Base64;
import org.geotoolkit.util.ArgumentChecks;

/**
 * Basic authentication security.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class BasicAuthenticationSecurity extends DefaultClientSecurity{

    private final String user;
    private final String password;
    
    public BasicAuthenticationSecurity(final String user, final String password){
        ArgumentChecks.ensureNonNull("user", user);
        ArgumentChecks.ensureNonNull("password", password);
        this.user = user;
        this.password = password;
    }
    
    @Override
    public URLConnection secure(URLConnection cnx) {
        cnx = super.secure(cnx);
        final String userPassword = user + ":" + password;
        final String encoding = Base64.encodeBytes(userPassword.getBytes());
        cnx.setRequestProperty ("Authorization", "Basic " + encoding);
        return cnx;
    }
    
}
