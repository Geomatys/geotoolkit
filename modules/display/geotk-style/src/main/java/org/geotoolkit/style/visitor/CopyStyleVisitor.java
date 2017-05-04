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

package org.geotoolkit.style.visitor;

import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;
import org.opengis.style.FeatureTypeStyle;
import org.opengis.style.Rule;
import org.opengis.style.Style;
import org.opengis.style.Symbolizer;

/**
 * Make a copy of a style object.
 * Only Style,FeatureTypeStyle and Rule are copies, symbolizers and expressions
 * are considered immutable.
 *
 * @author Johann Sorel (Geomatys)
 */
public class CopyStyleVisitor extends DefaultStyleVisitor{

    private static final MutableStyleFactory SF = (MutableStyleFactory) FactoryFinder.getStyleFactory(
                                            new Hints(Hints.STYLE_FACTORY, MutableStyleFactory.class));

    public static final CopyStyleVisitor INSTANCE = new CopyStyleVisitor();

    @Override
    public MutableStyle visit(Style style, Object data) {
        final MutableStyle copy = SF.style();
        copy.setDefault(style.isDefault());
        copy.setDefaultSpecification(style.getDefaultSpecification());
        copy.setDescription(style.getDescription());
        copy.setName(style.getName());

        for(FeatureTypeStyle fts : style.featureTypeStyles()){
            copy.featureTypeStyles().add( (MutableFeatureTypeStyle)fts.accept(this, data));
        }

        return copy;
    }

    @Override
    public MutableFeatureTypeStyle visit(FeatureTypeStyle fts, Object data) {
        final MutableFeatureTypeStyle copy = SF.featureTypeStyle();
        copy.semanticTypeIdentifiers().addAll(fts.semanticTypeIdentifiers());
        copy.setDescription(fts.getDescription());
        copy.setFeatureInstanceIDs(fts.getFeatureInstanceIDs());
        copy.setName(fts.getName());
        copy.setOnlineResource(fts.getOnlineResource());

        for(Rule r : fts.rules()){
            copy.rules().add((MutableRule)r.accept(this,data));
        }

        return copy;
    }

    @Override
    public MutableRule visit(Rule rule, Object data) {
        final MutableRule copy = SF.rule();
        copy.setDescription(rule.getDescription());
        copy.setElseFilter(rule.isElseFilter());
        copy.setFilter(rule.getFilter());
        copy.setLegendGraphic(rule.getLegend());
        copy.setMaxScaleDenominator(rule.getMaxScaleDenominator());
        copy.setMinScaleDenominator(rule.getMinScaleDenominator());
        copy.setName(rule.getName());
        copy.setOnlineResource(rule.getOnlineResource());

        for(Symbolizer symbol : rule.symbolizers()){
            copy.symbolizers().add(symbol);
        }
        return copy;
    }

}
