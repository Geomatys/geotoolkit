/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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

package org.geotoolkit.data.session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @todo must be concurrent
 * @author Johann Sorel (Geomatys)
 */
public class DefaultSessionDiff implements SessionDiff{

    private final List<Alteration> alterations = new ArrayList<Alteration>();

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Alteration> alterations() {
        return Collections.unmodifiableList(alterations);
    }

    public void add(Alteration alt){
        alterations.add(alt);
    }

    public void reset(){
        alterations.clear();
    }

    public void remove(Alteration alt){
        alterations.remove(alt);
    }

}
