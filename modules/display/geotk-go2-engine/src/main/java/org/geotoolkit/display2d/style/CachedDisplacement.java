/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.display2d.style;

import java.util.ArrayList;
import java.util.Collection;

import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.style.StyleConstants;

import org.opengis.feature.Feature;
import org.opengis.filter.expression.Expression;
import org.opengis.style.Displacement;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class CachedDisplacement{

    protected final Displacement styleElement;
    private final Collection<String> attributs;

    private CachedDisplacement(final Displacement anchor, final Collection<String> attributs) {
        this.styleElement = anchor;
        this.attributs = attributs;
    }

    /**
     * @return the original object
     */
    public Displacement getSource(){
        return styleElement;
    }

    public boolean isStatic(){
        return attributs.isEmpty();
    }

    /**
     * Returns the list of attributs requiered by this style.
     * This can be used to help caching a light version of the feature.
     *
     * @return Collection<String> : all requiered feature attributs name
     */
    public Collection<String> getRequieredAttributsName(final Collection<String> buffer){
        if(buffer == null){
            return attributs;
        }else{
            buffer.addAll(attributs);
            return buffer;
        }
    }

    public abstract float[] getValues(Feature feature, float[] buffer);



    public static CachedDisplacement cache(final Displacement anchor){

        float cachedX = Float.NaN;
        float cachedY = Float.NaN;
        final Collection<String> attributs;

        if(anchor != null){
            attributs = new ArrayList<String>();
            final Expression expX = anchor.getDisplacementX();
            final Expression expY = anchor.getDisplacementY();

            if(GO2Utilities.isStatic(expX)){
                cachedX = GO2Utilities.evaluate(expX, null, Float.class, 0.5f);
            }else{
                GO2Utilities.getRequieredAttributsName(expX,attributs);
            }

            if(GO2Utilities.isStatic(expY)){
                cachedY = GO2Utilities.evaluate(expY, null, Float.class, 0.5f);
            }else{
                GO2Utilities.getRequieredAttributsName(expY,attributs);
            }

        }else{
           attributs = Cache.EMPTY_ATTRIBUTS;
            //we can cache X and Y with default values
           cachedX = StyleConstants.DEFAULT_DISPLACEMENT_Xf;
           cachedY = StyleConstants.DEFAULT_DISPLACEMENT_Yf;
        }

        if(attributs.isEmpty()){
            return new StaticDisplacement(anchor, cachedX, cachedY);
        }else{
            return new DynamicDisplacement(anchor, cachedX, cachedY,attributs);
        }

    }


    private static final class StaticDisplacement extends CachedDisplacement{

        private final float cachedX;
        private final float cachedY;

        public StaticDisplacement(final Displacement anchor, final float cachedX, final float cachedY) {
            super(anchor,Cache.EMPTY_ATTRIBUTS);
            this.cachedX = cachedX;
            this.cachedY = cachedY;
        }

        @Override
        public float[] getValues(final Feature feature, final float[] buffer){
            if(buffer == null){
                return new float[]{cachedX,cachedY};
            }else{
                buffer[0] = cachedX;
                buffer[1] = cachedY;
                return buffer;
            }
        }
    }

    private static final class DynamicDisplacement extends CachedDisplacement{

        private final float cachedX;
        private final float cachedY;

        public DynamicDisplacement(final Displacement anchor, final float cachedX, final float cachedY, final Collection<String> attributs) {
            super(anchor,attributs);
            this.cachedX = cachedX;
            this.cachedY = cachedY;
        }

        @Override
        public float[] getValues(final Feature feature, float[] buffer){

            if(buffer == null){
                buffer = new float[2];
            }

            if(Float.isNaN(cachedX)){
                //if X is null it means it is dynamic
                final Expression anchorX = styleElement.getDisplacementX();
                buffer[0] = GO2Utilities.evaluate(anchorX, null, Float.class, StyleConstants.DEFAULT_DISPLACEMENT_Xf);
            } else {
                buffer[0] = cachedX;
            }

            if(Float.isNaN(cachedY)){
                //if Y is null it means it is dynamic
                final Expression anchorY = styleElement.getDisplacementY();
                buffer[1] = GO2Utilities.evaluate(anchorY, null, Float.class, StyleConstants.DEFAULT_DISPLACEMENT_Yf);
            } else {
                buffer[1] = cachedY;
            }

            return buffer;
        }

    }

}
