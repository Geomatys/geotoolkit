/*
 *    GeoTools - OpenSource mapping toolkit
 *    http://geotools.org
 *    (C) 2004-2008, Geotools Project Managment Committee (PMC)
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
package org.geotoolkit.display2d.style;

import org.opengis.feature.Feature;
import org.opengis.filter.expression.Expression;
import org.opengis.style.PointPlacement;

/**
 * Cached Point placement.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class CachedPointPlacement extends CachedLabelPlacement<PointPlacement>{

    //caches
    private float anchorX = Float.NaN;
    private float anchorY = Float.NaN;
    private float dispX = Float.NaN;
    private float dispY = Float.NaN;
    private float rotation = Float.NaN;
    
    public CachedPointPlacement(PointPlacement placement){
        super(placement);
    }
    
    public float getAnchorX(Feature feature){
        evaluate();
        
        if(Float.isNaN(anchorX)){
            //value is feature dynamic
            final Expression exp = styleElement.getAnchorPoint().getAnchorPointX();
            return GO2Utilities.evaluate(exp, feature, Float.class, 0.5f);
        }else{
            return anchorX;
        }
        
    }
    
    public float getAnchorY(Feature feature){
        evaluate();
        
        if(Float.isNaN(anchorY)){
            //value is feature dynamic
            final Expression exp = styleElement.getAnchorPoint().getAnchorPointY();
            return GO2Utilities.evaluate(exp, feature, Float.class, 0.5f);
        }else{
            return anchorY;
        }
    }
    
    public float getDisplacementX(Feature feature){
        evaluate();
        
        if(Float.isNaN(dispX)){
            //value is feature dynamic
            final Expression exp = styleElement.getDisplacement().getDisplacementX();
            return GO2Utilities.evaluate(exp, feature, Float.class, 0f);
        }else{
            return dispX;
        }
    }
    
    public float getDisplacementY(Feature feature){
        evaluate();
        
        if(Float.isNaN(dispY)){
            //value is feature dynamic
            final Expression exp = styleElement.getDisplacement().getDisplacementY();
            return GO2Utilities.evaluate(exp, feature, Float.class, 0f);
        }else{
            return dispY;
        }
    }
    
    public float getRotation(Feature feature){
        evaluate();
        
        if(Float.isNaN(rotation)){
            //value is feature dynamic
            final Expression exp = styleElement.getRotation();
            return GO2Utilities.evaluate(exp, feature, Float.class, 0f);
        }else{
            return rotation;
        }
    }
    
    @Override
    protected void evaluate() {
        if(!isNotEvaluated) return;
        
        final Expression expAnchorX = styleElement.getAnchorPoint().getAnchorPointX();
        final Expression expAnchorY = styleElement.getAnchorPoint().getAnchorPointY();
        final Expression expDispX = styleElement.getDisplacement().getDisplacementX();
        final Expression expDispY = styleElement.getDisplacement().getDisplacementY();
        final Expression expRotation = styleElement.getRotation();
        
        //we can not know so always visible
        isStaticVisible = VisibilityState.VISIBLE;
        
        if(GO2Utilities.isStatic(expAnchorX)){
            anchorX = GO2Utilities.evaluate(expAnchorX, null, Float.class, 0.5f);
        }else{
            GO2Utilities.getRequieredAttributsName(expAnchorX,requieredAttributs);
            isStatic = false;
        }
        
        if(GO2Utilities.isStatic(expAnchorY)){
            anchorY = GO2Utilities.evaluate(expAnchorY, null, Float.class, 0.5f);
        }else{
            GO2Utilities.getRequieredAttributsName(expAnchorY,requieredAttributs);
            isStatic = false;
        }
        
        if(GO2Utilities.isStatic(expDispX)){
            dispX = GO2Utilities.evaluate(expDispX, null, Float.class, 0f);
        }else{
            GO2Utilities.getRequieredAttributsName(expDispX,requieredAttributs);
            isStatic = false;
        }
        
        if(GO2Utilities.isStatic(expDispY)){
            dispY = GO2Utilities.evaluate(expDispY, null, Float.class, 0f);
        }else{
            GO2Utilities.getRequieredAttributsName(expDispY,requieredAttributs);
            isStatic = false;
        }
        
        if(GO2Utilities.isStatic(expRotation)){
            rotation = GO2Utilities.evaluate(expRotation, null, Float.class, 0f);
        }else{
            GO2Utilities.getRequieredAttributsName(expRotation,requieredAttributs);
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
