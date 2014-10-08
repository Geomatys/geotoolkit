/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.display2d.ext.graduation;

import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.display2d.style.CachedFont;
import org.geotoolkit.display2d.style.CachedStroke;
import org.geotoolkit.display2d.style.CachedSymbolizer;
import org.geotoolkit.display2d.style.renderer.SymbolizerRendererService;
import org.geotoolkit.style.StyleConstants;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class CachedGraduationSymbolizer extends CachedSymbolizer<GraduationSymbolizer>{

    private List<CachedGraduation> cache = null;
    
    public CachedGraduationSymbolizer(GraduationSymbolizer styleElement, SymbolizerRendererService<GraduationSymbolizer, ? extends CachedSymbolizer<GraduationSymbolizer>> renderer) {
        super(styleElement, renderer);
    }

    public List<CachedGraduation> getCachedGraduations() {
        evaluate();
        return cache;
    }
    
    @Override
    public float getMargin(Object candidate, float coeff) {
        return 0;
    }

    @Override
    protected void evaluate() {
        if(cache == null){
            cache = new ArrayList<>();
            for(GraduationSymbolizer.Graduation g : styleElement.getGraduations()){
                final CachedGraduation cg = new CachedGraduation(g);
                cg.evaluate();
                cache.add(cg);
            }
        }
    }

    @Override
    public boolean isVisible(Object candidate) {
        return true;
    }
    
    public static final class CachedGraduation {

        private final GraduationSymbolizer.Graduation graduation;
        private CachedStroke cachedStroke;
        private CachedFont cachedFont;
        
        public CachedGraduation(GraduationSymbolizer.Graduation graduation) {
            this.graduation = graduation;
        }       

        public GraduationSymbolizer.Graduation getGraduation() {
            return graduation;
        }

        public CachedStroke getCachedStroke() {
            return cachedStroke;
        }

        public CachedFont getCachedFont() {
            return cachedFont;
        }
        
        protected void evaluate() {
            if(graduation.getStroke()!=null){
                cachedStroke = CachedStroke.cache(graduation.getStroke());
            }else{
                cachedStroke = CachedStroke.cache(StyleConstants.DEFAULT_STROKE);
            }
            
            if(graduation.getFont()!=null){
                cachedFont = CachedFont.cache(graduation.getFont());
            }
        }
        
    }
    
}
