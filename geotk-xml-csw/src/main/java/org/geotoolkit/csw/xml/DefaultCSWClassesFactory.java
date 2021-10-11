/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.csw.xml;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class DefaultCSWClassesFactory implements CSWClassesFactory {

    @Override
    public List<String> getExtraClasses() {
        return Arrays.asList("org.geotoolkit.naturesdi.NATSDI_DataIdentification", // TODO move to nsdi code
                             "org.geotoolkit.sml.xml.v100.ObjectFactory",
                             "org.geotoolkit.geotnetcab.GNC_Resource",// TODO move to gnc code
                             "org.geotoolkit.cnes.DefaultAstronomicalExtent");// TODO move to cnes code
    }

}
