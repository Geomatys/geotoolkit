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
import org.opengis.style.AnchorPoint;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class CachedAnchorPoint{

    protected final AnchorPoint styleElement;
    private final Collection<String> attributs;

    private CachedAnchorPoint(AnchorPoint anchor, Collection<String> attributs) {
        this.styleElement = anchor;
        this.attributs = attributs;
    }

    /**
     * @return the original object
     */
    public AnchorPoint getSource(){
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
    public Collection<String> getRequieredAttributsName(Collection<String> buffer){
        if(buffer == null){
            return attributs;
        }else{
            buffer.addAll(attributs);
            return buffer;
        }
    }

    public abstract float[] getValues(Feature feature, float[] buffer);



    public static CachedAnchorPoint cache(AnchorPoint anchor){

        float cachedX = Float.NaN;
        float cachedY = Float.NaN;
        final Collection<String> attributs;

        if(anchor != null){
            attributs = new ArrayList<String>();
            final Expression expX = anchor.getAnchorPointX();
            final Expression expY = anchor.getAnchorPointY();

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
           cachedX = StyleConstants.DEFAULT_ANCHOR_POINT_Xf;
           cachedY = StyleConstants.DEFAULT_ANCHOR_POINT_Yf;
        }

        if(attributs.isEmpty()){
            return new StaticAnchorPoint(anchor, cachedX, cachedY);
        }else{
            return new DynamicAnchorPoint(anchor, cachedX, cachedY,attributs);
        }

    }


    private static final class StaticAnchorPoint extends CachedAnchorPoint{

        private final float cachedX;
        private final float cachedY;

        public StaticAnchorPoint(AnchorPoint anchor, float cachedX, float cachedY) {
            super(anchor,Cache.EMPTY_ATTRIBUTS);
            this.cachedX = cachedX;
            this.cachedY = cachedY;
        }

        @Override
        public float[] getValues(Feature feature, float[] buffer){
            if(buffer == null){
                return new float[]{cachedX,cachedY};
            }else{
                buffer[0] = cachedX;
                buffer[1] = cachedY;
                return buffer;
            }
        }
    }

    private static final class DynamicAnchorPoint extends CachedAnchorPoint{

        private final float cachedX;
        private final float cachedY;

        public DynamicAnchorPoint(AnchorPoint anchor, float cachedX, float cachedY, Collection<String> attributs) {
            super(anchor,attributs);
            this.cachedX = cachedX;
            this.cachedY = cachedY;
        }

        @Override
        public float[] getValues(Feature feature, float[] buffer){

            if(buffer == null){
                buffer = new float[2];
            }

            if(Float.isNaN(cachedX)){
                //if X is null it means it is dynamic
                final Expression anchorX = styleElement.getAnchorPointX();
                buffer[0] = GO2Utilities.evaluate(anchorX, null, Float.class, StyleConstants.DEFAULT_ANCHOR_POINT_Xf);
            } else {
                buffer[0] = cachedX;
            }

            if(Float.isNaN(cachedY)){
                //if Y is null it means it is dynamic
                final Expression anchorY = styleElement.getAnchorPointY();
                buffer[1] = GO2Utilities.evaluate(anchorY, null, Float.class, StyleConstants.DEFAULT_ANCHOR_POINT_Yf);
            } else {
                buffer[1] = cachedY;
            }

            return buffer;
        }

    }

}
