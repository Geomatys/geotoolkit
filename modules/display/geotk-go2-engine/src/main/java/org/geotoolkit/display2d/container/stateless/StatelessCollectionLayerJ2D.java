/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.display2d.container.stateless;

import java.util.ArrayList;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.SampleModel;
import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.geotoolkit.style.MutableRule;
import org.geotoolkit.display2d.container.ContextContainer2D;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.geotoolkit.display.primitive.SearchArea;
import org.geotoolkit.display2d.GO2Hints;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.map.CollectionMapLayer;
import org.geotoolkit.map.GraphicBuilder;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.container.statefull.StatefullCachedRule;
import org.geotoolkit.display2d.container.statefull.StatefullContextParams;
import org.geotoolkit.display2d.primitive.DefaultProjectedObject;
import org.geotoolkit.display2d.primitive.ProjectedObject;
import org.geotoolkit.display2d.style.CachedRule;
import org.geotoolkit.display2d.style.CachedSymbolizer;
import org.geotoolkit.display2d.style.renderer.SymbolizerRenderer;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.filter.identity.DefaultFeatureId;

import org.opengis.feature.type.FeatureType;
import org.opengis.display.primitive.Graphic;
import org.opengis.feature.type.ComplexType;
import org.opengis.filter.Filter;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.identity.FeatureId;
import org.opengis.style.Rule;
import org.opengis.style.Style;
import org.opengis.style.Symbolizer;
import org.opengis.style.TextSymbolizer;

import static org.geotoolkit.display2d.GO2Utilities.*;


/**
 * Single object to represent a collection map layer
 * This is a Stateless graphic object.
 *
 * @author johann sorel (Geomatys)
 * @module pending
 */
public class StatelessCollectionLayerJ2D<T extends CollectionMapLayer> extends StatelessMapLayerJ2D<T>{

    private static final Literal ID_EXPRESSION = FactoryFinder.getFilterFactory(null).literal("@id");

    protected final StatefullContextParams params;
    
    public StatelessCollectionLayerJ2D(final J2DCanvas canvas, final T layer){
        super(canvas, layer);
        params = new StatefullContextParams(canvas,layer);
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void paintLayer(final RenderingContext2D renderingContext) {

        //search for a special graphic renderer
        final GraphicBuilder<GraphicJ2D> builder = (GraphicBuilder<GraphicJ2D>) item.getGraphicBuilder(GraphicJ2D.class);
        if(builder != null){
            //let the parent class handle it
            super.paintLayer(renderingContext);
            return;
        }

        //first extract the valid rules at this scale
        final List<Rule> validRules = getValidRules(renderingContext,item,null);
        
        //we perform a first check on the style to see if there is at least
        //one valid rule at this scale, if not we just continue.
        if(validRules.isEmpty()){
            return;
        }
        
        final CachedRule[] rules = toCachedRules(validRules, null);
        final Set<String> attributs = GO2Utilities.propertiesCachedNames(rules);
        
        final Collection<?> candidates;
        try {
            candidates = optimizeCollection(renderingContext, attributs, validRules);
        } catch (Exception ex) {
            renderingContext.getMonitor().exceptionOccured(ex, Level.WARNING);
            //can not continue this layer with this error
            return;
        }
        
        paintVectorLayer(rules, candidates,renderingContext);
    }

    /**
     * @return the valid rules at this scale, selection rules will be mixed in.
     */
    protected static List<Rule> getValidRules(final RenderingContext2D renderingContext,
            final CollectionMapLayer item, final FeatureType type){

        final List<Rule> normalRules = GO2Utilities.getValidRules(
                   item.getStyle(), renderingContext.getSEScale(), type);

        final Filter selectionFilter = item.getSelectionFilter();
        if(selectionFilter != null && !Filter.EXCLUDE.equals(selectionFilter)){
            //merge the style and filter with the selection
            final List<Rule> selectionRules;

            final List<Rule> mixedRules = new ArrayList<Rule>();
            final MutableStyle selectionStyle = item.getSelectionStyle();
            if(selectionStyle == null){
                selectionRules = GO2Utilities.getValidRules(
                        ContextContainer2D.DEFAULT_SELECTION_STYLE, renderingContext.getSEScale(), type);
            }else{
                selectionRules = GO2Utilities.getValidRules(
                        selectionStyle, renderingContext.getSEScale(), type);
            }

            //update the rules filters
            for(final Rule rule : selectionRules){
                final List<? extends Symbolizer> symbols = rule.symbolizers();
                final MutableRule mixedRule = STYLE_FACTORY.rule(symbols.toArray(new Symbolizer[symbols.size()]));
                Filter f = rule.getFilter();
                if(f == null){
                    f = selectionFilter;
                }else{
                    f = FILTER_FACTORY.and(f,selectionFilter);
                }
                mixedRule.setFilter(f);
                mixedRules.add(mixedRule);
            }

            final Filter notSelectionFilter = FILTER_FACTORY.not(selectionFilter);

            for(final Rule rule : normalRules){
                final MutableRule mixedRule = STYLE_FACTORY.rule(rule.symbolizers().toArray(new Symbolizer[0]));
                Filter f = rule.getFilter();
                if(f == null){
                    f = notSelectionFilter;
                }else{
                    f = FILTER_FACTORY.and(f,notSelectionFilter);
                }
                mixedRule.setFilter(f);
                mixedRules.add(mixedRule);
            }
            
            return mixedRules;

        }
        
        return normalRules;
    }
    
    protected static CachedRule[] toCachedRules(Collection<? extends Rule> rules, final ComplexType expected){
        final CachedRule[] cached = new CachedRule[rules.size()];
        
        int i=0;
        for(Rule r : rules){
            cached[i] = getCached(r, expected);
            i++;
        }
        
        return cached;
    } 
    
    protected CachedRule[] prepareStyleRules(final RenderingContext2D renderingContext,
            final CollectionMapLayer layer, final FeatureType type){
        final CachedRule[] rules;

        final Style style = item.getStyle();

        final Filter selectionFilter = item.getSelectionFilter();
        if(selectionFilter != null && !Filter.EXCLUDE.equals(selectionFilter)){
            //merge the style and filter with the selection
            final List<Rule> selectionRules;
            final List<Rule> normalRules = GO2Utilities.getValidRules(
                   style, renderingContext.getSEScale(), type);

            final List<CachedRule> mixedRules = new ArrayList<CachedRule>();
            final MutableStyle selectionStyle = item.getSelectionStyle();
            if(selectionStyle == null){
                selectionRules = GO2Utilities.getValidRules(
                        ContextContainer2D.DEFAULT_SELECTION_STYLE, renderingContext.getSEScale(), type);
            }else{
                selectionRules = GO2Utilities.getValidRules(
                        selectionStyle, renderingContext.getSEScale(), type);
            }

            //update the rules filters
            for(final Rule rule : selectionRules){
                final List<? extends Symbolizer> symbols = rule.symbolizers();
                final MutableRule mixedRule = STYLE_FACTORY.rule(symbols.toArray(new Symbolizer[symbols.size()]));
                Filter f = rule.getFilter();
                if(f == null){
                    f = selectionFilter;
                }else{
                    f = FILTER_FACTORY.and(f,selectionFilter);
                }
                mixedRule.setFilter(f);
                mixedRules.add(GO2Utilities.getCached(mixedRule,type));
            }

            final Filter notSelectionFilter = FILTER_FACTORY.not(selectionFilter);

            for(final Rule rule : normalRules){
                final MutableRule mixedRule = STYLE_FACTORY.rule(rule.symbolizers().toArray(new Symbolizer[0]));
                Filter f = rule.getFilter();
                if(f == null){
                    f = notSelectionFilter;
                }else{
                    f = FILTER_FACTORY.and(f,notSelectionFilter);
                }
                mixedRule.setFilter(f);
                mixedRules.add(GO2Utilities.getCached(mixedRule,type));
            }

            rules = mixedRules.toArray(new CachedRule[mixedRules.size()]);

        }else{
            rules = GO2Utilities.getValidCachedRules(
                style, renderingContext.getSEScale(), type);
        }

        return rules;
    }

    protected StatefullContextParams getStatefullParameters(final RenderingContext2D context){
        params.update(context);
        return params;
    }

    protected Collection<?> optimizeCollection(final RenderingContext2D context,
            final Set<String> requieredAtts, final List<Rule> rules) throws Exception {
        return item.getCollection();
    }

    protected void paintVectorLayer(final CachedRule[] rules, final Collection<?> candidates, final RenderingContext2D context) {
        
        final CanvasMonitor monitor = context.getMonitor();

        //we do not check if the collection is empty or not since
        //it can be a very expensive operation

        //prepare the rendering parameters
        final StatefullContextParams params = getStatefullParameters(context);
        if(monitor.stopRequested()) return;

        final Boolean SymbolOrder = (Boolean) canvas.getRenderingHint(GO2Hints.KEY_SYMBOL_RENDERING_ORDER);
        if(SymbolOrder == null || SymbolOrder == false){
            try{
                renderByObjectOrder(candidates, context, rules, params);
            }catch(PortrayalException ex){
                monitor.exceptionOccured(ex, Level.WARNING);
            }
        }else{
            try{
                renderBySymbolOrder(candidates, context, rules, params);
            }catch(PortrayalException ex){
                monitor.exceptionOccured(ex, Level.WARNING);
            }
        }

    }

    /**
     * Render by object order.
     * @param candidates
     * @param renderers
     * @param context
     * @param params
     * @throws PortrayalException
     */
    protected final void renderByObjectOrder(final Collection<?> candidates,
            final RenderingContext2D context, final CachedRule[] rules,
            final StatefullContextParams params) throws PortrayalException{
        final RenderingIterator statefullIterator = getIterator(candidates, context, params);
        renderByObjectOrder(statefullIterator, context, rules);
    }
    
    protected final void renderByObjectOrder(final RenderingIterator statefullIterator,
            final RenderingContext2D context, final CachedRule[] rules) throws PortrayalException{
        final CanvasMonitor monitor = context.getMonitor();

        //prepare the renderers
        final StatefullCachedRule renderers = new StatefullCachedRule(rules, context);

        try{
            //performance routine, only one symbol to render
            if(renderers.rules.length == 1
               && (renderers.rules[0].getFilter() == null || renderers.rules[0].getFilter() == Filter.INCLUDE)
               && renderers.rules[0].symbolizers().length == 1){
                renderers.renderers[0][0].portray(statefullIterator);
                return;
            }

            while(statefullIterator.hasNext()){
                if(monitor.stopRequested()) return;
                final ProjectedObject projectedCandidate = statefullIterator.next();

                boolean painted = false;
                for(int i=0; i<renderers.elseRuleIndex; i++){
                    final CachedRule rule = renderers.rules[i];
                    final Filter ruleFilter = rule.getFilter();
                    //test if the rule is valid for this feature
                    if (ruleFilter == null || ruleFilter.evaluate(projectedCandidate.getCandidate())) {
                        painted = true;
                        for (final SymbolizerRenderer renderer : renderers.renderers[i]) {
                            renderer.portray(projectedCandidate);
                        }
                    }
                }

                //the feature hasn't been painted, paint it with the 'else' rules
                if(!painted){
                    for(int i=renderers.elseRuleIndex; i<renderers.rules.length; i++){
                        final CachedRule rule = renderers.rules[i];
                        final Filter ruleFilter = rule.getFilter();
                        //test if the rule is valid for this feature
                        if (ruleFilter == null || ruleFilter.evaluate(projectedCandidate.getCandidate())) {
                            for (final SymbolizerRenderer renderer : renderers.renderers[i]) {
                                renderer.portray(projectedCandidate);
                            }
                        }
                    }
                }
            }
        }finally{
            try {
                statefullIterator.close();
            } catch (IOException ex) {
                getLogger().log(Level.WARNING, null, ex);
            }
        }
    }
    
    

    /**
     * render by symbol order.
     */
    protected final void renderBySymbolOrder(final Collection<?> candidates,
            final RenderingContext2D context, final CachedRule[] rules, final StatefullContextParams params)
            throws PortrayalException {

        //performance routine, only one symbol to render
        if(rules.length == 1
           && (rules[0].getFilter() == null || rules[0].getFilter() == Filter.INCLUDE)
           && rules[0].symbolizers().length == 1){
            final RenderingIterator statefullIterator = getIterator(candidates, context, params);
            final CachedSymbolizer s = rules[0].symbolizers()[0];
            final SymbolizerRenderer renderer = s.getRenderer().createRenderer(s, context);
            renderer.portray(statefullIterator);
            try {
                statefullIterator.close();
            } catch (IOException ex) {
                getLogger().log(Level.WARNING, null, ex);
            }
            return;
        }
        
        renderBySymbolIndexInRule(candidates,context,rules,params);
    }

    /**
     * Render by symbol index order in a single pass, this results in creating a buffered image
     * for each symbolizer depth, the maximum number of buffer is the maximum number of symbolizer a rule contain.
     */
    protected void renderBySymbolIndexInRule(final Collection<?> candidates,
            final RenderingContext2D context, final CachedRule[] rules, final StatefullContextParams params)
            throws PortrayalException {
        final RenderingIterator statefullIterator = getIterator(candidates, context, params);
        renderBySymbolIndexInRule(statefullIterator, context, rules);
    }
    
    /**
     * Render by symbol index order in a single pass, this results in creating a buffered image
     * for each symbolizer depth, the maximum number of buffer is the maximum number of symbolizer a rule contain.
     */
    protected void renderBySymbolIndexInRule(final RenderingIterator statefullIterator,
            final RenderingContext2D context, final CachedRule[] rules)
            throws PortrayalException {

        final CanvasMonitor monitor = context.getMonitor();

        final int elseRuleIndex = StatefullCachedRule.sortByElseRule(rules);


        //store the ids of the features painted during the first round -----------------------------
        final BufferedImage originalBuffer = (BufferedImage) context.getCanvas().getSnapShot();
        final ColorModel cm = ColorModel.getRGBdefault();
        final SampleModel sm = cm.createCompatibleSampleModel(originalBuffer.getWidth(), originalBuffer.getHeight());
        final RenderingContext2D originalContext = context;

        final List<BufferedImage> images = new ArrayList<BufferedImage>();
        final List<RenderingContext2D> ctxs = new ArrayList<RenderingContext2D>();
        images.add(originalBuffer);
        ctxs.add(context);        
        final SymbolizerRenderer[][] renderers = new SymbolizerRenderer[rules.length][0];

        for(int i=0;i<rules.length;i++){
            final CachedRule cr = rules[i];
            final CachedSymbolizer[] css = cr.symbolizers();
            
            //do not count text symbolizers at the end
            int len = css.length;
            for(int k=css.length-1;k>=0;k--){
                if(css[k].getSource() instanceof TextSymbolizer){
                    len--;
                }else{
                    break;
                }
            }
            
            if(len > images.size()){
                for(int k=images.size();k<len;k++){
                    final BufferedImage layer = createBufferedImage(cm, sm);
                    images.add(k, layer);
                    ctxs.add(k, context.create( ((Graphics2D)layer.getGraphics()) ));
                }
            }
            
            renderers[i] = new SymbolizerRenderer[css.length];
            for(int k=0;k<css.length;k++){
                if(css[k].getSource() instanceof TextSymbolizer){
                    //use the original context
                    renderers[i][k] = css[k].getRenderer().createRenderer(css[k],context);
                }else{
                    renderers[i][k] = css[k].getRenderer().createRenderer(css[k],ctxs.get(k));
                }            
            }
        }
        
        try{
            while(statefullIterator.hasNext()){
                if(monitor.stopRequested()) return;
                final ProjectedObject projectedCandidate = statefullIterator.next();
                                
                boolean painted = false;
                for(int i=0; i<elseRuleIndex; i++){
                    final CachedRule rule = rules[i];                    
                    final Filter ruleFilter = rule.getFilter();
                    //test if the rule is valid for this feature
                    if (ruleFilter == null || ruleFilter.evaluate(projectedCandidate.getCandidate())) {
                        painted = true;
                        final CachedSymbolizer[] css = rule.symbolizers();
                        for(int k=0; k<css.length; k++){
                            renderers[i][k].portray(projectedCandidate);
                        }
                    }
                }
                
                //paint with else rules
                if(!painted){
                    for(int i=elseRuleIndex; i<rules.length; i++){
                        final CachedRule rule = rules[i];                    
                        final Filter ruleFilter = rule.getFilter();
                        //test if the rule is valid for this feature
                        if (ruleFilter == null || ruleFilter.evaluate(projectedCandidate.getCandidate())) {
                            final CachedSymbolizer[] css = rule.symbolizers();
                            for(int k=0; k<css.length; k++){
                                renderers[i][k].portray(projectedCandidate);
                            }
                        }
                    }
                }
                
            }
        }finally{
            try {
                statefullIterator.close();
            } catch (IOException ex) {
                getLogger().log(Level.WARNING, null, ex);
            }
        }
        
        //merge images --------------------------
        originalContext.switchToDisplayCRS();
        final Graphics2D g = originalContext.getGraphics();
        g.setComposite(ALPHA_COMPOSITE_1F);
        for(int i=1;i<images.size();i++){
            final Image img = images.get(i);
            g.drawImage(img, 0, 0, null);
            recycleBufferedImage((BufferedImage)img);
        }
    }
    
    protected boolean contain(final Set<FeatureId> ids, final Object candidate){
        return ids.contains(id(candidate));
    }

    protected FeatureId id(final Object candidate){
        final Object obj = ID_EXPRESSION.evaluate(candidate);
        if(obj != null){
            return new DefaultFeatureId(obj.toString());
        }else{
            return null;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Graphic> getGraphicAt(final RenderingContext rdcontext, 
            final SearchArea mask, final VisitFilter filter, final List<Graphic> graphics) {
        return graphics;
    }

    protected RenderingIterator getIterator(final Collection<?> features,
            final RenderingContext2D renderingContext, final StatefullContextParams params){
        final Iterator<?> iterator = features.iterator();
        final DefaultProjectedObject projectedFeature = new DefaultProjectedObject(params);
        return new GraphicIterator(iterator, projectedFeature);
    }

    protected static interface RenderingIterator extends Iterator<ProjectedObject>,Closeable{}

    protected class GraphicIterator implements RenderingIterator{

        private final Iterator<?> ite;
        private final DefaultProjectedObject projected;

        public GraphicIterator(final Iterator<?> ite, final DefaultProjectedObject projected) {
            this.ite = ite;
            this.projected = projected;
        }

        @Override
        public boolean hasNext() {
            return ite.hasNext();
        }

        @Override
        public ProjectedObject next() {
            projected.setCandidate(ite.next());
            return projected;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void close() throws IOException {
            if(ite instanceof Closeable){
                ((Closeable)ite).close();
            }
        }

    }

}
