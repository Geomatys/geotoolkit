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
package org.geotoolkit.ebrim.xml;

import java.util.List;
import org.geotoolkit.csw.xml.CSWClassesContext;

import org.geotoolkit.ebrim.xml.v300.IdentifiableType;
import org.geotoolkit.ebrim.xml.v300.RegistryObjectType;
import org.geotoolkit.wrs.xml.v100.ExtrinsicObjectType;


/**
 * Extension of the {@link CSWClassesContext} class, which adds the EBRIM profile
 * classes to the standard CSW classes already defined in {@link CSWClassesContext}.
 *
 * @author Guilhem Legal (Geomatys)
 * @author Cédric Briançon (Geomatys)
 */
public final class EBRIMClassesContext extends CSWClassesContext {

    private EBRIMClassesContext() {}

    public static Class[] getAllClasses() {
        final List<Class> classes = getAllClassesList();
        return classes.toArray(new Class[classes.size()]);
    }

    public static List<Class> getAllClassesList() {
        final List<Class> classes = CSWClassesContext.getAllClassesList();

        //Ebrim classes 3.0
        classes.add(IdentifiableType.class);
        classes.add(ExtrinsicObjectType.class);
        classes.add(org.geotoolkit.ebrim.xml.v300.ObjectFactory.class);
        classes.add(org.geotoolkit.wrs.xml.v100.ObjectFactory.class);

        //Ebrim classes 2.5
        classes.add(RegistryObjectType.class);
        classes.add(org.geotoolkit.ebrim.xml.v250.ObjectFactory.class);
        classes.add(org.geotoolkit.wrs.xml.v090.ObjectFactory.class);

        return classes;
    }
}
