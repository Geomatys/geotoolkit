/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

package org.geotoolkit.data;

import java.util.Iterator;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DataUtilities {

    private DataUtilities(){
        
    }

    public static FeatureReader reader(Iterator<? extends Feature> ite, FeatureType type){
        return new WrapIteratorFeatureReader(ite, type);
    }


    private static final class WrapIteratorFeatureReader implements FeatureReader{

        private final Iterator<? extends Feature> ite;
        private final FeatureType type;

        private WrapIteratorFeatureReader(Iterator<? extends Feature> ite, FeatureType type){
            if(ite == null){
                throw new NullPointerException("List can not be null");
            }
            this.ite = ite;
            this.type = type;
        }

        @Override
        public FeatureType getFeatureType() {
            return type;
        }

        @Override
        public Feature next() throws DataStoreRuntimeException {
            return ite.next();
        }

        @Override
        public boolean hasNext() throws DataStoreRuntimeException {
            return ite.hasNext();
        }

        @Override
        public void close() {
        }

        @Override
        public void remove() {
            ite.remove();
        }

    }

}
