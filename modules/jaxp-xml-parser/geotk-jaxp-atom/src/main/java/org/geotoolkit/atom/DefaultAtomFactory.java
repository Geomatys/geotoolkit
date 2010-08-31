/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
package org.geotoolkit.atom;

import java.util.List;
import org.geotoolkit.atom.model.AtomEmail;
import org.geotoolkit.atom.model.AtomLink;
import org.geotoolkit.atom.model.DefaultAtomLink;
import org.geotoolkit.atom.model.AtomPersonConstruct;
import org.geotoolkit.atom.model.DefaultAtomEmail;
import org.geotoolkit.atom.model.DefaultAtomPersonConstruct;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultAtomFactory implements AtomFactory {

    private static final AtomFactory ATOMF = new DefaultAtomFactory();

    private DefaultAtomFactory(){}
    
    public static AtomFactory getInstance(){
        return ATOMF;
    }

    /**
     * 
     * @{@inheritDoc }
     */
    @Override
    public AtomLink createAtomLink(String href, String rel, String type,
            String hreflang, String title, String length) {
        return new DefaultAtomLink(href, rel, type, hreflang, title, length);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AtomPersonConstruct createAtomPersonConstruct(List<Object> params) {
        return new DefaultAtomPersonConstruct(params);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AtomEmail createAtomEmail(String address) {
        return new DefaultAtomEmail(address);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AtomLink createAtomLink() {
        return new DefaultAtomLink();
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AtomPersonConstruct createAtomPersonConstruct() {
        return new DefaultAtomPersonConstruct();
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AtomEmail createAtomEmail() {
        return new DefaultAtomEmail();
    }
}
