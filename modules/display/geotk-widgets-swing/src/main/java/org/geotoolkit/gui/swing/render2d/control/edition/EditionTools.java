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

package org.geotoolkit.gui.swing.render2d.control.edition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import org.geotoolkit.lang.Static;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public final class EditionTools extends Static {

    private static final ServiceLoader<EditionTool> REGISTRY;

    static {
        REGISTRY = ServiceLoader.load(EditionTool.class);
    }

    private EditionTools() {}

    public static List<EditionTool> getTools(){
        final Iterator<EditionTool> ite = REGISTRY.iterator();
        final List<EditionTool> cache = new ArrayList<>();
        while(ite.hasNext()){
            cache.add(ite.next());
        }
        Collections.sort(cache, new Comparator<EditionTool>(){
            @Override
            public int compare(final EditionTool o1, final EditionTool o2) {
                return o2.getPriority() - o1.getPriority();
            }
        });

        return Collections.unmodifiableList(cache);
    }

    public static ServiceLoader getRegistry() {
        return REGISTRY;
    }

}
