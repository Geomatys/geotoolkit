/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2010, Geomatys
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

package org.geotoolkit.display2d.container.statefull;

import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.CachedRule;
import org.geotoolkit.display2d.style.CachedSymbolizer;
import org.geotoolkit.display2d.style.renderer.SymbolizerRenderer;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class StatefullCachedRule {

    public final CachedRule[] rules;
    public final SymbolizerRenderer renderers[][];
    public final int elseRuleIndex;

    public StatefullCachedRule(final CachedRule[] rules, final RenderingContext2D context) {

        //sort the rules
        elseRuleIndex = sortByElseRule(rules);

        this.rules = rules;
        renderers = new SymbolizerRenderer[rules.length][0];

        for(int i=0; i<rules.length; i++){
            final CachedSymbolizer[] symbols = rules[i].symbolizers();
            renderers[i] = new SymbolizerRenderer[symbols.length];
            for(int k=0; k<symbols.length; k++){
                renderers[i][k] = symbols[k].getRenderer().createRenderer(symbols[k], context);
            }
        }
    }

    /**
     * sort the rules, isolate the else rules, they must be handle differently
     */
    public static int sortByElseRule(final CachedRule[] sortedRules){
        int elseRuleIndex = sortedRules.length;

        for(int i=0; i<elseRuleIndex; i++){
            CachedRule r =sortedRules[i];
            if(r.getSource().isElseFilter()){
                elseRuleIndex--;

                for(int j=i+1;j<sortedRules.length;j++){
                    sortedRules[j-1] = sortedRules[j];
                }

                //move the rule at the end
                sortedRules[sortedRules.length-1] = r;
            }
        }

        return elseRuleIndex;
    }

}
