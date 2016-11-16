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
package org.geotoolkit.ignrm;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.security.DefaultClientSecurity;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.logging.Logging;

/**
 * Client Security object relying on an IGN token.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class TokenClientSecurity extends DefaultClientSecurity{

    private final Token token;

    public TokenClientSecurity(final Token token) {
        ArgumentChecks.ensureNonNull("token", token);
        this.token = token;
    }

    @Override
    public URL secure(URL url) {
        String strUrl = url.toString();

        if(strUrl.endsWith("/")){
            strUrl = strUrl.substring(0,strUrl.length()-1);
        }

        try {
            strUrl = strUrl + "&" + token.getName() +"="+token.getValue();
            return new URL(strUrl);
        } catch (IOException | XMLStreamException ex) {
            Logging.getLogger("org.geotoolkit.ignrm").log(Level.SEVERE, null, ex);
        }

        return url;
    }

}
