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

package org.geotoolkit.gui.swing.go2.control.edition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.imageio.spi.ServiceRegistry;
import org.geotoolkit.lang.Static;
import org.geotoolkit.util.collection.UnmodifiableArrayList;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
@Static
public final class EditionTools {

    private static List<EditionTool> TOOLS;

    static {
        final Iterator<EditionTool> ite = ServiceRegistry.lookupProviders(EditionTool.class);
        final List<EditionTool> cache = new ArrayList<EditionTool>();
        while(ite.hasNext()){
            cache.add(ite.next());
        }
        TOOLS = UnmodifiableArrayList.wrap(cache.toArray(new EditionTool[cache.size()]));
    }

    private EditionTools() {}

    public static List<EditionTool> getTools(){
        return TOOLS;
    }

}
