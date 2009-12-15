/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotoolkit.data;

import java.util.Iterator;
import org.geotoolkit.data.session.ContentException;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

/**
 *
 * @author sorel
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
        public Feature next() throws ContentException {
            return ite.next();
        }

        @Override
        public boolean hasNext() throws ContentException {
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
