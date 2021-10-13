/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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

import org.geotoolkit.display2d.GO2Utilities;
import org.opengis.filter.Expression;
import org.opengis.style.PointPlacement;

/**
 * Cached Point placement.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class CachedPointPlacement extends CachedLabelPlacement<PointPlacement>{

    //caches
    private final CachedAnchorPoint cachedAnchor;
    private final CachedDisplacement cachedDisplacement;
    private float rotation = Float.NaN;

    private CachedPointPlacement(final PointPlacement placement){
        super(placement);
        this.cachedAnchor = CachedAnchorPoint.cache(placement.getAnchorPoint());
        this.cachedDisplacement = CachedDisplacement.cache(placement.getDisplacement());
    }

    /**
     * return an Array of 2 floats always in display unit.
     */
    public float[] getDisplacement(final Object candidate, final float[] buffer){
        return cachedDisplacement.getValues(candidate, buffer);
    }

    /**
     * return an Array of 2 floats.
     */
    public float[] getAnchor(final Object candidate, final float[] buffer){
        return cachedAnchor.getValues(candidate, buffer);
    }

    public float getRotation(final Object candidate){
        evaluate();

        if(Float.isNaN(rotation)){
            //value is feature dynamic
            final Expression exp = styleElement.getRotation();
            return GO2Utilities.evaluate(exp, candidate, Float.class, 0f);
        }else{
            return rotation;
        }
    }

    @Override
    protected void evaluate() {
        if(!isNotEvaluated) return;

        final Expression expRotation = styleElement.getRotation();

        //we can not know so always visible
        isStaticVisible = VisibilityState.VISIBLE;

        cachedAnchor.getRequieredAttributsName(requieredAttributs);
        cachedDisplacement.getRequieredAttributsName(requieredAttributs);

        if(GO2Utilities.isStatic(expRotation)){
            rotation = GO2Utilities.evaluate(expRotation, null, Float.class, 0f);
        }else{
            GO2Utilities.getRequieredAttributsName(expRotation,requieredAttributs);
            isStatic = false;
        }

        //no attributs needed replace with static empty list.
        if(requieredAttributs.isEmpty()){
            isStatic = true;
            requieredAttributs = EMPTY_ATTRIBUTS;
        }else{
            isStatic = false;
        }

        isNotEvaluated = false;
    }

    @Override
    public boolean isVisible(final Object candidate) {
        evaluate();
        //placement doesnt know if it's visible or not whit those informations, always true.
        return true;
    }

    public static CachedPointPlacement cache(final PointPlacement placement){
        return new CachedPointPlacement(placement);
    }

}
