/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2011, Geomatys
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

package org.geotoolkit.filter.binding;

/**
 * To test acessor order by priority.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class MockBinding2 extends AbstractBinding{


    public MockBinding2() {
        super(Object.class, 5);
    }

    @Override
    public boolean support(String xpath) {
        return false;
    }

    @Override
    public Object get(Object candidate, String xpath, Class target) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void set(Object candidate, String xpath, Object value) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
