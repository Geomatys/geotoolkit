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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Paint;
import java.awt.RenderingHints;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.renderer.SymbolizerRendererService;
import org.opengis.filter.expression.Expression;
import org.opengis.style.Fill;
import org.opengis.style.Halo;
import org.opengis.style.LabelPlacement;
import org.opengis.style.LinePlacement;
import org.opengis.style.PointPlacement;
import org.opengis.style.TextSymbolizer;

/**
 * Cached text symbolizer.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class CachedTextSymbolizer extends CachedSymbolizer<TextSymbolizer>{

    //Cached values
    private final CachedFont cachedFont;
    private final CachedHalo cachedHalo;
    private final CachedFill cachedFill;
    private final CachedLabelPlacement cachedPlacement;

    private String label = null;

    public CachedTextSymbolizer(final TextSymbolizer symbolizer,
            final SymbolizerRendererService<TextSymbolizer,? extends CachedSymbolizer<TextSymbolizer>> renderer){
        super(symbolizer,renderer);

        final org.opengis.style.Font font = styleElement.getFont();
        cachedFont = CachedFont.cache(font);

        final Fill fill = styleElement.getFill();
        cachedFill = CachedFill.cache(fill);

        //halo can be null.
        final Halo halo = styleElement.getHalo();
        if(halo != null){
            cachedHalo = CachedHalo.cache(halo);
        }else{
            cachedHalo = null;
        }

        final LabelPlacement placement = styleElement.getLabelPlacement();
        if(placement instanceof PointPlacement){
            cachedPlacement = CachedPointPlacement.cache((PointPlacement) placement);
        }else if(placement instanceof LinePlacement){
            cachedPlacement = CachedLinePlacement.cache((LinePlacement) placement);
        }else{
            throw new IllegalArgumentException("A text symbolizer must have a placement set of type : PointPlacement or LinePlacement.");
        }

        styleElement.getFont();
//        symbol.getGeometryPropertyName();
        styleElement.getHalo();
        styleElement.getLabel();
        styleElement.getLabelPlacement();
        styleElement.getUnitOfMeasure();

    }

    public Paint getFontPaint(final Object candidate, final int x, final int y, final float coeff, final RenderingHints hints){
        Paint paint;

        if(cachedFill != null){
            paint = cachedFill.getJ2DPaint(candidate, x, y, coeff, hints);
        }else{
            paint = Color.BLACK;
        }

        return paint;
    }

    public Composite getFontComposite(final Object candidate){
        Composite composite;

        if(cachedFill != null){
            composite = cachedFill.getJ2DComposite(candidate);
        }else{
            composite = AlphaComposite.SrcOver;
        }

        return composite;
    }

    public Font getJ2dFont(final Object candidate, final float coeff){
        return cachedFont.getJ2dFont(candidate, coeff);
    }

    public String getLabel(final Object candidate) {
        return label != null ? label : GO2Utilities.evaluate(styleElement.getLabel(),candidate,String.class, null);
    }

    public CachedHalo getHalo(){
        return cachedHalo;
    }

    public CachedLabelPlacement getPlacement(){
        return cachedPlacement;
    }

    @Override
    protected void evaluate() {
        if(!isNotEvaluated) return;

        final Expression expLabel = styleElement.getLabel();

        //we can not know so always visible
        isStaticVisible = VisibilityState.VISIBLE;

        if (GO2Utilities.isStatic(expLabel)) {
            label = GO2Utilities.evaluate(expLabel, null, String.class, "No Label");
        } else {
            label = null;
            GO2Utilities.getRequieredAttributsName(expLabel,requieredAttributs);
            isStatic = false;
        }

        cachedFont.getRequieredAttributsName(requieredAttributs);
        cachedFill.getRequieredAttributsName(requieredAttributs);
        cachedHalo.getRequieredAttributsName(requieredAttributs);
        cachedPlacement.getRequieredAttributsName(requieredAttributs);

        //no attributs needed replace with static empty list.
        if (requieredAttributs.isEmpty()) {
            requieredAttributs = EMPTY_ATTRIBUTS;
        }


        isNotEvaluated = false;
    }

    @Override
    public float getMargin(Object candidate, RenderingContext2D ctx) {
        //we can not evaluate the size of a text symbolizer
        return Float.NaN;
    }

    @Override
    public boolean isVisible(final Object candidate) {
        return true;
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    @Override
    public VisibilityState isStaticVisible() {
        return VisibilityState.DYNAMIC;
    }

}
