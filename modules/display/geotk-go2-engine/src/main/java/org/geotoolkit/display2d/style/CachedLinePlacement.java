/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
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
import org.opengis.feature.Feature;
import org.opengis.filter.expression.Expression;
import org.opengis.style.LinePlacement;

/**
 * Cached line placement.
 *
 * @author Johann Sorel (Geomatys)
 */
public class CachedLinePlacement extends CachedLabelPlacement<LinePlacement>{

    private float gap = Float.NaN;
    private float initial = Float.NaN;
    private float offset = Float.NaN;

    public CachedLinePlacement(LinePlacement placement){
        super(placement);
    }

    public float getGap(Feature feature){
        evaluate();
        
        if(Float.isNaN(gap)){
            //value is feature dynamic
            final Expression exp = styleElement.getGap();
            return GO2Utilities.evaluate(exp, feature, Float.class, 0f);
        }else{
            return gap;
        }
    }

    public float getInitialGap(Feature feature){
        evaluate();
        
        if(Float.isNaN(initial)){
            //value is feature dynamic
            final Expression exp = styleElement.getInitialGap();
            return GO2Utilities.evaluate(exp, feature, Float.class, 0f);
        }else{
            return initial;
        }
    }

    public float getOffset(Feature feature){
        evaluate();
        
        if(Float.isNaN(offset)){
            //value is feature dynamic
            final Expression exp = styleElement.getPerpendicularOffset();
            return GO2Utilities.evaluate(exp, feature, Float.class, 0f);
        }else{
            return offset;
        }
    }

    public boolean isRepeated(){
        return styleElement.isRepeated();
    }

    public boolean isAligned(){
        return styleElement.IsAligned();
    }

    public boolean isGeneralizeLine(){
        return styleElement.isGeneralizeLine();
    }

    @Override
    protected void evaluate() {
        if(!isNotEvaluated) return;

        final Expression expGap = styleElement.getGap();
        final Expression expInitial = styleElement.getInitialGap();
        final Expression expOffset = styleElement.getPerpendicularOffset();
        
        //we can not know so always visible
        isStaticVisible = VisibilityState.VISIBLE;
        
        if(GO2Utilities.isStatic(expGap)){
            gap = GO2Utilities.evaluate(expGap, null, Float.class, 0.5f);
        }else{
            GO2Utilities.getRequieredAttributsName(expGap,requieredAttributs);
            isStatic = false;
        }
        
        if(GO2Utilities.isStatic(expInitial)){
            initial = GO2Utilities.evaluate(expInitial, null, Float.class, 0.5f);
        }else{
            GO2Utilities.getRequieredAttributsName(expInitial,requieredAttributs);
            isStatic = false;
        }
        
        if(GO2Utilities.isStatic(expOffset)){
            offset = GO2Utilities.evaluate(expOffset, null, Float.class, 0.5f);
        }else{
            GO2Utilities.getRequieredAttributsName(expOffset,requieredAttributs);
            isStatic = false;
        }
        
        //no attributs needed replace with static empty list.
        if(requieredAttributs.isEmpty()){
            requieredAttributs = EMPTY_ATTRIBUTS;
        }
        
        isNotEvaluated = false;
    }

    @Override
    public boolean isVisible(Feature feature) {
        evaluate();
        //placement doesnt know if it's visible or not whit those informations, always true.
        return true;
    }

}
