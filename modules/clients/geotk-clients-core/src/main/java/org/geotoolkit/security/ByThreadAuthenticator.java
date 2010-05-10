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

package org.geotoolkit.security;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * Authenticator storing login information different for each thread.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class ByThreadAuthenticator extends Authenticator{

    private static final ByThreadAuthenticator INSTANCE = new ByThreadAuthenticator();

    private static final ThreadLocal<PasswordAuthentication> LOCALS = new ThreadLocal<PasswordAuthentication>();

    private ByThreadAuthenticator(){}

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return super.getPasswordAuthentication();
    }

    /**
     * Register this authenticator as the default one.
     */
    public static void register(){
        Authenticator.setDefault(INSTANCE);
    }

    /**
     * Set the current thread authentication informations.
     * 
     * @param user : this thread current login
     * @param password : this thread current password
     */
    public static void setCurrentThreadAuthentication(String user, char[] password){
        LOCALS.set(new PasswordAuthentication(user, password));
    }

}
