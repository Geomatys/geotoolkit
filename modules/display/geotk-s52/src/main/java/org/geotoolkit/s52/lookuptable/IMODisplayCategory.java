/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.s52.lookuptable;

import java.util.ArrayList;
import java.util.List;
import org.opengis.util.CodeList;

/**
 * S-52 ECDIS IMO display categories code list.
 *
 * @author Johann Sorel (Geomatys)
 */
public class IMODisplayCategory extends CodeList<IMODisplayCategory>{

        private static final List<IMODisplayCategory> VALUES = new ArrayList<>();

        public static final IMODisplayCategory STANDARD             = new IMODisplayCategory("STANDARD");
        public static final IMODisplayCategory OTHER                = new IMODisplayCategory("OTHER");
        public static final IMODisplayCategory DISPLAYBASE          = new IMODisplayCategory("DISPLAYBASE");
        public static final IMODisplayCategory MARINERS_STANDARD    = new IMODisplayCategory("MARINERS STANDARD");
        public static final IMODisplayCategory MARINERS_OTHER       = new IMODisplayCategory("MARINERS OTHER");
        public static final IMODisplayCategory MARINERS_DISPLAYBASE = new IMODisplayCategory("MARINERS DISPLAYBASE");
        public static final IMODisplayCategory NULL                 = new IMODisplayCategory("");

        private IMODisplayCategory(final String name) {
            super(name, VALUES);
        }

        public static IMODisplayCategory getOrCreate(final String code){
            return valueOf(IMODisplayCategory.class, new Filter() {

                @Override
                public boolean accept(CodeList<?> cl) {
                    return code.equals(cl.name());
                }

                @Override
                public String codename() {
                    return code;
                }
            });
        }

        public static IMODisplayCategory[] values() {
            synchronized (VALUES) {
                return VALUES.toArray(new IMODisplayCategory[VALUES.size()]);
            }
        }

        @Override
        public IMODisplayCategory[] family() {
            return values();
        }

}
