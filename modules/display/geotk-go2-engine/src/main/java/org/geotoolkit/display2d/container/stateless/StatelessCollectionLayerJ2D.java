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
import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
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
import org.geotoolkit.filter.DefaultId;
import org.geotoolkit.filter.identity.DefaultFeatureId;

import org.opengis.feature.type.FeatureType;
import org.opengis.display.primitive.Graphic;
import org.opengis.filter.Filter;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.identity.FeatureId;
import org.opengis.style.FeatureTypeStyle;
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

        final CachedRule[] rules = prepareStyleRules(renderingContext, item, null);

        //we perform a first check on the style to see if there is at least
        //one valid rule at this scale, if not we just continue.
        if (rules.length == 0) {
            return;
        }

        paintVectorLayer(rules, renderingContext);
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
                mixedRules.add(GO2Utilities.getCached(mixedRule));
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
                mixedRules.add(GO2Utilities.getCached(mixedRule));
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
            final CachedRule[] rules) throws Exception {
        return item.getCollection();
    }

    protected void paintVectorLayer(final CachedRule[] rules, final RenderingContext2D context) {
        
        final CanvasMonitor monitor = context.getMonitor();

        //we do not check if the collection is empty or not since
        //it can be a very expensive operation

        //prepare the rendering parameters
        final StatefullContextParams params = getStatefullParameters(context);
        if(monitor.stopRequested()) return;

        final Collection<?> candidates;
        try {
            candidates = optimizeCollection(context, rules);
        } catch (Exception ex) {
            context.getMonitor().exceptionOccured(ex, Level.WARNING);
            //can not continue this layer with this error
            return;
        }

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
        final CanvasMonitor monitor = context.getMonitor();
        final RenderingIterator statefullIterator = getIterator(candidates, context, params);


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

        //Always use Parallale buffer, otherwise collection with no valid ids will fail
        //to render properly.
        //final Boolean parallal = (Boolean)context.getCanvas().getRenderingHint(GO2Hints.KEY_PARALLAL_BUFFER);
        //if(parallal != null && parallal){
            renderBySymbolParallal(candidates, context, rules, params);
        //}else{
        //    renderBySymbolStream(candidates, context, rules, params);
        //}
    }

    /**
     * Render by symbol order in multiple passes. consume less memory but require
     * more time.
     */
    private void renderBySymbolStream(final Collection<?> candidates,
            final RenderingContext2D context, final CachedRule[] rules, final StatefullContextParams params)
            throws PortrayalException {
        final CanvasMonitor monitor = context.getMonitor();

        //prepare the renderers
        final StatefullCachedRule renderers = new StatefullCachedRule(rules, context);

        //store the ids of the features painted during the first round ---------
        final Set<FeatureId> painted;
        if(renderers.elseRuleIndex >= renderers.rules.length){
            painted = new HashSet<FeatureId>();
        }else{
            //no need to store ids since we don't have any else rule
            painted = null;
        }

        //render the main rules ------------------------------------------------
        for (int i = 0; i < renderers.elseRuleIndex; i++) {
            if(monitor.stopRequested()) return;
            final CachedRule rule = renderers.rules[i];
            final Filter rulefilter = rule.getFilter();

            //starting from second path and after, we ask for features knowing there ids
            //it should be much more efficient.
            final Set<FeatureId> ruleFeatures = new HashSet<FeatureId>();
            Collection<?> col = candidates;

            //encapsulate interator
            for (int k=0,n=renderers.renderers[i].length; k<n;k++){

                final SymbolizerRenderer renderer = renderers.renderers[i][k];
                if(monitor.stopRequested()) return;

                if(k==1){
                    //we have collected all ids from the last round, let's optimize the query
                    col = getIdFilteredCollection(col, context, params, ruleFeatures);
                }

                RenderingIterator ite = getIterator(col, context, params);
                if(k==0){
                    //first pass, we must filter using the rule filter
                    //next passes have a ids filter, so no need to filter anymore
                    ite = getFilteredIterator(ite, rulefilter, (n>1)?ruleFeatures:null);
                }

                try {
                    renderer.portray(ite);
                } finally {
                    try {
                        ite.close();
                    } catch (IOException ex) {
                        getLogger().log(Level.WARNING, null, ex);
                    }
                }
            }

            if(painted!=null){ painted.addAll(ruleFeatures); }
        }

        //render the else rules ------------------------------------------------
        for (int i = renderers.elseRuleIndex; i < renderers.rules.length; i++) {
            if(monitor.stopRequested()) return;
            final CachedRule rule = renderers.rules[i];
            final Filter rulefilter = rule.getFilter();

            for (final SymbolizerRenderer renderer : renderers.renderers[i]) {
                if(monitor.stopRequested()) return;
                final RenderingIterator ite = getIterator(candidates, context, params);
                try {
                    while (ite.hasNext()) {
                        if(monitor.stopRequested()) return;
                        final ProjectedObject pf = ite.next();
                        final Object f = pf.getCandidate();
                        if (!contain(painted, f) && (rulefilter == null || rulefilter.evaluate(f))) {
                            renderer.portray(pf);
                        }
                    }
                } finally {
                    try {
                        ite.close();
                    } catch (IOException ex) {
                        getLogger().log(Level.WARNING, null, ex);
                    }
                }
            }
        }
    }

    /**
     * Render by symbol order in a single pass, this results in creating a buffered image
     * for each symbol, which may be expensive in memory but efficient in performance.
     */
    private void renderBySymbolParallal(final Collection<?> candidates,
            final RenderingContext2D context, final CachedRule[] rules, final StatefullContextParams params)
            throws PortrayalException {

        final CanvasMonitor monitor = context.getMonitor();

        final int elseRuleIndex = StatefullCachedRule.sortByElseRule(rules);


        //store the ids of the features painted during the first round -----------------------------
        final BufferedImage originalBuffer = (BufferedImage) context.getCanvas().getSnapShot();
        final ColorModel cm = ColorModel.getRGBdefault();
        final SampleModel sm = cm.createCompatibleSampleModel(originalBuffer.getWidth(), originalBuffer.getHeight());
        final RenderingContext2D originalContext = context;

        final Image[][] images = new Image[rules.length][0];
        final SymbolizerRenderer[][] renderers = new SymbolizerRenderer[rules.length][0];
        final boolean[][] used = new boolean[rules.length][0];

        for(int i=0;i<rules.length;i++){
            final CachedRule cr = rules[i];
            final CachedSymbolizer[] css = cr.symbolizers();
            images[i] = new Image[css.length];
            //renderers[i] = new SymbolizerRenderer[css.length];
            used[i] = new boolean[css.length];
            Arrays.fill(used[i], false);
        }

        final RenderingIterator statefullIterator = getIterator(candidates, context, params);

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
                        SymbolizerRenderer[] rss = renderers[i];

                        //if not created yet --------------
                        if(rss.length==0){
                            final CachedSymbolizer[] css = rule.symbolizers();
                            renderers[i] = new SymbolizerRenderer[css.length];
                            for(int k=0; k<css.length; k++){
                                final CachedSymbolizer cs = css[k];
                                if(cs.getSource() instanceof TextSymbolizer){
                                    images[i][k] = originalBuffer;
                                    renderers[i][k] = cs.getRenderer().createRenderer(cs, originalContext);
                                }else{
                                    if(i==0 && k==0){
                                        //first buffer is the current one
                                        images[i][k] = originalBuffer ;
                                        renderers[i][k] = cs.getRenderer().createRenderer(cs, originalContext);
                                    }else{
                                        final BufferedImage img = createBufferedImage(cm, sm);
                                        final RenderingContext2D ctx = context.create(img.createGraphics());
                                        images[i][k] = img ;
                                        renderers[i][k] = cs.getRenderer().createRenderer(cs, ctx);
                                    }
                                }
                            }
                            rss = renderers[i];
                        }

                        for (int k=0;k<rss.length;k++) {
                            final SymbolizerRenderer renderer = rss[k];
                            used[i][k] = true;
                            renderer.portray(projectedCandidate);
                        }
                    }
                }

                //the feature hasn't been painted, paint it with the 'else' rules
                if(!painted){
                    for(int i=elseRuleIndex; i<rules.length; i++){
                        final CachedRule rule = rules[i];
                        final Filter ruleFilter = rule.getFilter();
                        //test if the rule is valid for this feature
                        if (ruleFilter == null || ruleFilter.evaluate(projectedCandidate.getCandidate())) {
                            SymbolizerRenderer[] rss = renderers[i];

                            //if not created yet --------------
                            if(rss.length==0){
                                final CachedSymbolizer[] css = rule.symbolizers();
                                renderers[i] = new SymbolizerRenderer[css.length];
                                for(int k=0; k<css.length; k++){
                                    final CachedSymbolizer cs = css[k];
                                    if(cs.getSource() instanceof TextSymbolizer){
                                        images[i][k] = originalBuffer;
                                        renderers[i][k] = cs.getRenderer().createRenderer(cs, originalContext);
                                    }else{
                                        if(i==0 && k==0){
                                            //first buffer is the current one
                                            images[i][k] = originalBuffer ;
                                            renderers[i][k] = cs.getRenderer().createRenderer(cs, originalContext);
                                        }else{
                                            final BufferedImage img = createBufferedImage(cm, sm);
                                            final RenderingContext2D ctx = context.create(img.createGraphics());
                                            images[i][k] = img ;
                                            renderers[i][k] = cs.getRenderer().createRenderer(cs, ctx);
                                        }
                                    }
                                }
                                rss = renderers[i];
                            }



                            for (int k=0;k<rss.length;k++) {
                                final SymbolizerRenderer renderer = rss[k];
                                used[i][k] = true;
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

        //merge images --------------------------
        originalContext.switchToDisplayCRS();
        final Graphics2D g = originalContext.getGraphics();
        g.setComposite(ALPHA_COMPOSITE_1F);
        for(int i=0;i<images.length;i++){
            for(int k=0,n=images[i].length; k<n; k++){
                final Image img = images[i][k];
                if(img != originalBuffer){
                    if(used[i][k]){
                        g.drawImage(img, 0, 0, null);
                    }
                    recycleBufferedImage((BufferedImage)img);
                }
            }
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

    private void paintObject(final Object object, final Style style, 
            final RenderingContext2D context) throws PortrayalException{
        for(final FeatureTypeStyle fts : style.featureTypeStyles()){
            for(final Rule rule : fts.rules()){
                final Filter filter = rule.getFilter();
                if(filter == null || filter.evaluate(object)){
                    for(final Symbolizer symbolizer : rule.symbolizers()){
                        paintObject(object, symbolizer, context);
                    }
                }
            }
        }
    }

    private void paintObject(final Object object, final Symbolizer symbolizer, 
            final RenderingContext2D context) throws PortrayalException{
        final CachedSymbolizer cached = GO2Utilities.getCached(symbolizer);
        final SymbolizerRenderer renderer = cached.getRenderer().createRenderer(cached, context);
        final ProjectedObject projected = new DefaultProjectedObject(params, object);
        renderer.portray(projected);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Graphic> getGraphicAt(final RenderingContext rdcontext, 
            final SearchArea mask, final VisitFilter filter, final List<Graphic> graphics) {
        return graphics;
    }


    protected Collection<?> getIdFilteredCollection(final Collection<?> features,
            final RenderingContext2D renderingContext, final StatefullContextParams params,
            final Set<FeatureId> ids) throws PortrayalException{
        return new FilteredCollection(features,new DefaultId(ids));
    }

    protected RenderingIterator getIterator(final Collection<?> features,
            final RenderingContext2D renderingContext, final StatefullContextParams params){
        final Iterator<?> iterator = features.iterator();
        final DefaultProjectedObject projectedFeature = new DefaultProjectedObject(params);
        return new GraphicIterator(iterator, projectedFeature);
    }

    protected RenderingIterator getFilteredIterator(final RenderingIterator ite,
            final Filter filter, final Set<FeatureId> ids){
        return new FilterGraphicIterator(ite, filter, ids);
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

    protected class FilterGraphicIterator implements RenderingIterator{

        private RenderingIterator ite;
        private Filter filter;
        private ProjectedObject next = null;
        private final Set<FeatureId> ids;

        public FilterGraphicIterator(final RenderingIterator ite, final Filter filter, final Set<FeatureId> ids) {
            this.ite = ite;
            this.filter = (filter==null)?Filter.INCLUDE : filter ;
            this.ids = ids;
        }

        @Override
        public boolean hasNext() {
            findNext();
            return next != null;
        }

        @Override
        public ProjectedObject next() {
            //we know the hasNext has been called before
            final ProjectedObject t = next;
            next = null;
            return t;
        }

        private void findNext(){
            if(next != null){
                return;
            }

            while(ite.hasNext()){
                final ProjectedObject candidate = ite.next();
                final Object f = candidate.getCandidate();
                if(filter.evaluate(f)){
                    next = candidate;
                    if(ids!=null){ids.add(id(f));}
                    return;
                }
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void close() throws IOException {
            ite.close();
        }

    }

    private static class FilteredCollection extends AbstractCollection{

        private final Collection wrapped;
        private final Filter filter;

        public FilteredCollection(final Collection wrapped,final Filter filter) {
            this.wrapped = wrapped;
            this.filter = filter;
        }

        @Override
        public Iterator iterator() {
            return new FilteredIterator(wrapped.iterator(), filter);
        }

        @Override
        public int size() {
            throw new UnsupportedOperationException("Not supported.");
        }

    }

    private static class FilteredIterator implements Iterator,Closeable{

        private Iterator ite;
        private Filter filter;
        private Object next = null;

        public FilteredIterator(Iterator ite, Filter filter) {
            this.ite = ite;
            this.filter = filter;
        }

        @Override
        public boolean hasNext() {
            findNext();
            return next != null;
        }

        @Override
        public Object next() {
            //we know the hasNext has been called before
            final Object t = next;
            next = null;
            return t;
        }

        private void findNext(){
            if(next != null){
                return;
            }

            while(ite.hasNext()){
                final Object candidate = ite.next();
                if(filter.evaluate(candidate)){
                    next = candidate;
                    return;
                }
            }
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
