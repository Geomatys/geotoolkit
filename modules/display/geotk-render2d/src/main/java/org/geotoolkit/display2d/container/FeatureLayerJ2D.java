/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2014, Geomatys
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
package org.geotoolkit.display2d.container;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.SampleModel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Stream;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.Query;
import org.apache.sis.storage.event.StoreEvent;
import org.apache.sis.storage.event.StoreListener;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display.SearchArea;
import org.geotoolkit.display.VisitFilter;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.geotoolkit.display2d.GO2Utilities;
import static org.geotoolkit.display2d.GO2Utilities.ALPHA_COMPOSITE_1F;
import static org.geotoolkit.display2d.GO2Utilities.FILTER_FACTORY;
import static org.geotoolkit.display2d.GO2Utilities.STYLE_FACTORY;
import static org.geotoolkit.display2d.GO2Utilities.getCached;
import static org.geotoolkit.display2d.GO2Utilities.propertiesNames;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.container.ContextContainer2D;
import org.geotoolkit.display2d.primitive.DefaultSearchAreaJ2D;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.display2d.primitive.ProjectedFeature;
import org.geotoolkit.display2d.primitive.ProjectedObject;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.display2d.style.CachedRule;
import org.geotoolkit.display2d.style.CachedSymbolizer;
import org.geotoolkit.display2d.style.renderer.RenderingRoutines;
import org.geotoolkit.display2d.style.renderer.RenderingRoutines.GraphicIterator;
import org.geotoolkit.display2d.style.renderer.SymbolizerRenderer;
import org.geotoolkit.display2d.style.renderer.SymbolizerRendererService;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.GraphicBuilder;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.referencing.operation.matrix.XAffineTransform;
import org.geotoolkit.storage.event.StorageListener;
import org.geotoolkit.storage.feature.FeatureStoreRuntimeException;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyle;
import org.opengis.display.primitive.Graphic;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyNotFoundException;
import org.opengis.filter.Filter;
import org.opengis.filter.Id;
import org.opengis.filter.identity.FeatureId;
import org.opengis.style.FeatureTypeStyle;
import org.opengis.style.Rule;
import org.opengis.style.Symbolizer;
import org.opengis.style.TextSymbolizer;
import org.opengis.util.GenericName;

/**
 * Single object to represent a complete feature map layer.
 * This is a Stateless graphic object.
 *
 * @author johann sorel (Geomatys)
 * @module
 */
public class FeatureLayerJ2D extends MapLayerJ2D<FeatureMapLayer> implements StoreListener<StoreEvent> {

    protected StorageListener.Weak weakSessionListener = new StorageListener.Weak(this);


    public FeatureLayerJ2D(final J2DCanvas canvas, final FeatureMapLayer layer){
        super(canvas, layer, false);

        final FeatureSet resource = layer.getResource();
        if (resource instanceof FeatureSet) {
            weakSessionListener.registerSource(resource);
        }
    }

    @Override
    public void eventOccured(StoreEvent event) {
        if (item.isVisible() && getCanvas().isAutoRepaint()) {
            //TODO should call a repaint only on this graphic
            getCanvas().repaint();
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean paintLayer(final RenderingContext2D renderingContext) {
        final CanvasMonitor monitor = renderingContext.getMonitor();

        //search for a special graphic renderer
        final GraphicBuilder<GraphicJ2D> builder = (GraphicBuilder<GraphicJ2D>) item.getGraphicBuilder(GraphicJ2D.class);
        if (builder != null) {
            //let the parent class handle it
            return super.paintLayer(renderingContext);
        }

        if (monitor.stopRequested()) return false;

        if(Boolean.TRUE.equals(item.getUserProperties().get(MapLayer.USERKEY_STYLED_FEATURE))){
            //feature have self defined styles.
            return renderStyledFeature(renderingContext, 0.0);
        }

        //merge base style and selection style
        final MutableStyle baseStyle = item.getStyle();
        final Id selectionFilter = item.getSelectionFilter();
        final MutableStyle selectionStyle = item.getSelectionStyle();
        final FeatureType type;
        try {
            type = item.getResource().getType();
        } catch (DataStoreException ex) {
            renderingContext.getMonitor().exceptionOccured(ex, Level.WARNING);
            return false;
        }
        final MutableStyle style = RenderingRoutines.mergeStyles(baseStyle, selectionFilter, selectionStyle);

        boolean rendered = false;
        for (FeatureTypeStyle fts : style.featureTypeStyles()) {
            if (monitor.stopRequested()) return false;
            //first extract the valid rules at this scale
            final List<Rule> validRules = GO2Utilities.getValidRules(fts, renderingContext.getSEScale(), type);

            //we perform a first check on the style to see if there is at least
            //one valid rule at this scale, if not we just continue.
            if (validRules.isEmpty()) {
                continue;
            }

            //extract the used names
            Set<String> names = propertiesNames(validRules);
            if (names.contains("*")) {
                //we need all properties
                names = null;
            }

            //calculate max symbol size, to expand search envelope.
            double symbolsMargin = 0.0;
            for (Rule rule : validRules) {
                for (Symbolizer s : rule.symbolizers()) {
                    final CachedSymbolizer cs = GO2Utilities.getCached(s, null);
                    symbolsMargin = Math.max(symbolsMargin, cs.getMargin(null, renderingContext));
                }
            }
            if (Double.isNaN(symbolsMargin) || Double.isInfinite(symbolsMargin)) {
                //symbol margin can not be pre calculated, expect a max of 300pixels
                symbolsMargin = 300f;
            }
            if (symbolsMargin > 0) {
                final double scale = XAffineTransform.getScale(renderingContext.getDisplayToObjective());
                symbolsMargin = scale * symbolsMargin;
            }

            final FeatureSet candidates;
            final FeatureType expected;
            try {
                //optimize
                candidates = RenderingRoutines.optimizeFeatureSet(renderingContext, item, names, validRules, symbolsMargin);
                //get the expected result type
                expected = candidates.getType();
            } catch (Exception ex) {
                renderingContext.getMonitor().exceptionOccured(ex, Level.WARNING);
                continue;
            }

            //calculate optimized rules and included filter + expressions
            final CachedRule[] rules = toCachedRules(validRules, expected);

            //we do not check if the collection is empty or not since
            //it can be a very expensive operation

            //prepare the rendering parameters
            if (monitor.stopRequested()) return false;

            //check if we have group symbolizers, if it's the case we must render by symbol order.
            boolean symbolOrder = false;
            for (CachedRule rule : rules) {
                for (CachedSymbolizer symbolizer : rule.symbolizers()) {
                    if (symbolizer.getRenderer().isGroupSymbolizer()) {
                        symbolOrder = true;
                        break;
                    }
                }
            }

            if (symbolOrder) {
                try {
                    rendered |= renderBySymbolOrder(candidates, renderingContext, rules);
                } catch(PortrayalException ex) {
                    monitor.exceptionOccured(ex, Level.WARNING);
                }
            } else {
                try {
                    rendered |= renderByObjectOrder(candidates, renderingContext, rules);
                } catch(PortrayalException ex) {
                    monitor.exceptionOccured(ex, Level.WARNING);
                }
            }
        }

        return rendered;
    }

    /**
     * Render styled features.
     */
    private boolean renderStyledFeature(final RenderingContext2D context, double symbolsMargin){

        final CanvasMonitor monitor = context.getMonitor();
        final GraphicIterator statefullIterator;
        try {
            final FeatureSet candidates = RenderingRoutines.optimizeFeatureSet(context, item, symbolsMargin);
            statefullIterator = RenderingRoutines.getIterator(candidates, context);
        } catch (Exception ex) {
            context.getMonitor().exceptionOccured(ex, Level.WARNING);
            return false;
        }

        //prepare the rendering parameters
        if (monitor.stopRequested()) return false;

        boolean dataRendered = false;
        try {
            while (statefullIterator.hasNext()) {
                if(monitor.stopRequested()) return dataRendered;
                final ProjectedObject projectedCandidate = statefullIterator.next();
                final Feature feature = (Feature) projectedCandidate.getCandidate();

                final List<Symbolizer> symbolizers;
                try {
                    symbolizers = (List<Symbolizer>) feature.getPropertyValue(FeatureExt.ATTRIBUTE_SYMBOLIZERS.toString());
                } catch(PropertyNotFoundException ex) {
                    continue;
                }
                if (symbolizers == null) continue;
                for (Symbolizer symbolizer : symbolizers) {
                    final SymbolizerRendererService srs = GO2Utilities.findRenderer(symbolizer.getClass());
                    final CachedSymbolizer cs = srs.createCachedSymbolizer(symbolizer);
                    final SymbolizerRenderer sr = srs.createRenderer(cs, context);
                    try {
                        dataRendered |= sr.portray(projectedCandidate);
                    } catch (PortrayalException ex) {
                        monitor.exceptionOccured(ex, Level.WARNING);
                    }
                }
            }
        } finally {
            try {
                statefullIterator.close();
            } catch (IOException ex) {
                getLogger().log(Level.WARNING, null, ex);
            }
        }
        return dataRendered;
    }

    protected FeatureId id(Object candidate) {
        return FeatureExt.getId((Feature)candidate);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Graphic> getGraphicAt(final RenderingContext rdcontext, final SearchArea mask, final VisitFilter filter, List<Graphic> graphics) {

        if(!item.isSelectable()) return graphics;

        if(!(rdcontext instanceof RenderingContext2D)) return graphics;
        final RenderingContext2D c2d = (RenderingContext2D) rdcontext;

        //nothing visible so no possible selection
        if (!item.isVisible()) return graphics;

        final GenericName featureTypeName;
        try {
            featureTypeName = item.getResource().getType().getName();
        } catch (DataStoreException ex) {
            rdcontext.getMonitor().exceptionOccured(ex, Level.WARNING);
            return graphics;
        }
        final CachedRule[] rules = GO2Utilities.getValidCachedRules(item.getStyle(),
                c2d.getSEScale(), featureTypeName,null);

        //we perform a first check on the style to see if there is at least
        //one valid rule at this scale, if not we just return null.
        if (rules.length == 0) {
            return graphics;
        }

        if (graphics == null) graphics = new ArrayList<>();

        final double symbolsMargin = 50.0;
        if (mask instanceof SearchAreaJ2D) {
            return searchGraphicAt(item, rules, c2d, (SearchAreaJ2D)mask, filter, graphics, symbolsMargin);
        } else {
            return searchGraphicAt(item, rules, c2d, new DefaultSearchAreaJ2D(mask), filter, graphics, symbolsMargin);
        }
    }

    protected List<Graphic> searchGraphicAt(final FeatureMapLayer layer, final CachedRule[] rules,
            final RenderingContext2D renderingContext, final SearchAreaJ2D mask,
            final VisitFilter visitFilter, final List<Graphic> graphics, double symbolsMargin) {

        final Query query;
        try {
            final Set<String> attributs = GO2Utilities.propertiesCachedNames(rules);
            //add identifier property
            final FeatureType type = getUserObject().getResource().getType();
            try{
                type.getProperty(AttributeConvention.IDENTIFIER_PROPERTY.toString());
                attributs.add(AttributeConvention.IDENTIFIER_PROPERTY.toString());
            }catch(PropertyNotFoundException ex){}
            query = RenderingRoutines.prepareQuery(renderingContext, layer, attributs, null, symbolsMargin);
        } catch (PortrayalException | DataStoreException ex) {
            renderingContext.getMonitor().exceptionOccured(ex, Level.WARNING);
            return graphics;
        }

        final FeatureSet features;
        try{
            features = layer.getResource().subset(query);
        }catch(DataStoreException ex){
            renderingContext.getMonitor().exceptionOccured(ex, Level.WARNING);
            //can not continue this layer with this error
            return graphics;
        }

        //we do not check if the collection is empty or not since
        //it can be a very expensive operation


        // iterate and find the first graphic that hit the given point
        final Iterator<Feature> iterator;
        try (Stream<Feature> stream = features.features(false)) {
            iterator = stream.iterator();

            //prepare the renderers
            final RenderingRules preparedRenderers = new RenderingRules(rules, renderingContext);

            final ProjectedFeature projectedFeature = new ProjectedFeature(renderingContext);
            while (iterator.hasNext()) {
                Feature feature = iterator.next();
                projectedFeature.setCandidate(feature);

                boolean painted = false;
                for (int i=0;i<preparedRenderers.elseRuleIndex;i++) {
                    final CachedRule rule = preparedRenderers.rules[i];
                    final Filter ruleFilter = rule.getFilter();
                    //test if the rule is valid for this feature
                    if (ruleFilter == null || ruleFilter.evaluate(feature)) {
                        painted = true;
                        for(SymbolizerRenderer renderer : preparedRenderers.renderers[i]){
                            if(renderer.hit(projectedFeature, mask, visitFilter)){
                                if(feature != null) graphics.add( new ProjectedFeature(getCanvas(), layer, feature) );
                                break;
                            }
                        }
                    }
                }

                //the feature hasn't been painted, paint it with the 'else' rules
                if (!painted) {
                    for(int i=preparedRenderers.elseRuleIndex; i<preparedRenderers.rules.length; i++){
                        final CachedRule rule = preparedRenderers.rules[i];
                        final Filter ruleFilter = rule.getFilter();
                        //test if the rule is valid for this feature
                        if (ruleFilter == null || ruleFilter.evaluate(feature)) {
                            for(SymbolizerRenderer renderer : preparedRenderers.renderers[i]){
                                if(renderer.hit(projectedFeature, mask, visitFilter)){
                                    if(feature != null) graphics.add( new ProjectedFeature(getCanvas(), layer, feature) );
                                    break;
                                }
                            }
                        }
                    }
                }

            }
        } catch (FeatureStoreRuntimeException | DataStoreException ex) {
            renderingContext.getMonitor().exceptionOccured(ex, Level.WARNING);
            return graphics;
        }

        return graphics;
    }

    /**
     * @return the valid rules at this scale, selection rules will be mixed in.
     */
    public static List<Rule> getValidRules(final RenderingContext2D renderingContext,
            final FeatureMapLayer item, final FeatureType type){

        final List<Rule> normalRules = GO2Utilities.getValidRules(
                   item.getStyle(), renderingContext.getSEScale(), type);

        final Filter selectionFilter = item.getSelectionFilter();
        if(selectionFilter != null && !Filter.EXCLUDE.equals(selectionFilter)){
            //merge the style and filter with the selection
            final List<Rule> selectionRules;

            final List<Rule> mixedRules = new ArrayList<>();
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

    public static CachedRule[] toCachedRules(Collection<? extends Rule> rules, final FeatureType expected){
        final CachedRule[] cached = new CachedRule[rules.size()];

        int i=0;
        for(Rule r : rules){
            cached[i] = getCached(r, expected);
            i++;
        }

        return cached;
    }

    /**
     * Render by object order.
     */
    private final boolean renderByObjectOrder(final FeatureSet candidates,
            final RenderingContext2D context, final CachedRule[] rules) throws PortrayalException{
        final CanvasMonitor monitor = context.getMonitor();

        //prepare the renderers
        final RenderingRules renderers = new RenderingRules(rules, context);

        //performance routine, only one symbol to render
        if(renderers.rules.length == 1
           && (renderers.rules[0].getFilter() == null || renderers.rules[0].getFilter() == Filter.INCLUDE)
           && renderers.rules[0].symbolizers().length == 1){
            return renderers.renderers[0][0].portray(candidates);
        }

        try (GraphicIterator ite = RenderingRoutines.getIterator(candidates, context)) {
            boolean dataRendered = false;
            while (ite.hasNext()) {
                if(monitor.stopRequested()) return dataRendered;
                final ProjectedObject projectedCandidate = ite.next();

                boolean painted = false;
                for(int i=0; i<renderers.elseRuleIndex; i++){
                    final CachedRule rule = renderers.rules[i];
                    final Filter ruleFilter = rule.getFilter();
                    //test if the rule is valid for this feature
                    if (ruleFilter == null || ruleFilter.evaluate(projectedCandidate.getCandidate())) {
                        painted = true;
                        for (final SymbolizerRenderer renderer : renderers.renderers[i]) {
                            dataRendered |= renderer.portray(projectedCandidate);
                        }
                    }
                }

                //the feature hasn't been painted, paint it with the 'else' rules
                if (!painted) {
                    for(int i=renderers.elseRuleIndex; i<renderers.rules.length; i++){
                        final CachedRule rule = renderers.rules[i];
                        final Filter ruleFilter = rule.getFilter();
                        //test if the rule is valid for this feature
                        if (ruleFilter == null || ruleFilter.evaluate(projectedCandidate.getCandidate())) {
                            for (final SymbolizerRenderer renderer : renderers.renderers[i]) {
                                dataRendered |= renderer.portray(projectedCandidate);
                            }
                        }
                    }
                }
            }
            return dataRendered;

        } catch (DataStoreException | IOException ex) {
            throw new PortrayalException(ex.getMessage(), ex);
        }
    }

    /**
     * render by symbol order.
     */
    private final boolean renderBySymbolOrder(final FeatureSet candidates,
            final RenderingContext2D context, final CachedRule[] rules)
            throws PortrayalException {

        //performance routine, only one symbol to render
        if (rules.length == 1
           && (rules[0].getFilter() == null || rules[0].getFilter() == Filter.INCLUDE)
           && rules[0].symbolizers().length == 1) {
            final CachedSymbolizer s = rules[0].symbolizers()[0];
            final SymbolizerRenderer renderer = s.getRenderer().createRenderer(s, context);
            return renderer.portray(candidates);
        }
        return renderBySymbolIndexInRule(candidates,context,rules);
    }

    /**
     * Render by symbol index order in a single pass, this results in creating a buffered image
     * for each symbolizer depth, the maximum number of buffer is the maximum number of symbolizer a rule contain.
     */
    private boolean renderBySymbolIndexInRule(final FeatureSet candidates,
            final RenderingContext2D context, final CachedRule[] rules)
            throws PortrayalException {
        final CanvasMonitor monitor = context.getMonitor();

        final int elseRuleIndex = RenderingRules.sortByElseRule(rules);

        //store the ids of the features painted during the first round -----------------------------
        final BufferedImage originalBuffer = (BufferedImage) context.getCanvas().getSnapShot();
        final ColorModel cm = ColorModel.getRGBdefault();
        final SampleModel sm = cm.createCompatibleSampleModel(originalBuffer.getWidth(), originalBuffer.getHeight());
        final RenderingContext2D originalContext = context;

        final List<BufferedImage> images = new ArrayList<>();
        final List<RenderingContext2D> ctxs = new ArrayList<>();
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

            if (len > images.size()) {
                for (int k=images.size();k<len;k++){
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

        boolean dataRendered = false;

        final GraphicIterator statefullIterator;
        try {
            statefullIterator = RenderingRoutines.getIterator(candidates, context);
        } catch (DataStoreException ex) {
            throw new PortrayalException(ex.getMessage(), ex);
        }
        try {
            while (statefullIterator.hasNext()) {
                if(monitor.stopRequested()) return dataRendered;
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
                            dataRendered |= renderers[i][k].portray(projectedCandidate);
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
                                dataRendered |= renderers[i][k].portray(projectedCandidate);
                            }
                        }
                    }
                }

            }

            //paint group symbolizers
            for(int i=0; i<elseRuleIndex; i++){
                final CachedRule rule = rules[i];
                final CachedSymbolizer[] css = rule.symbolizers();
                for (int k=0; k<css.length; k++) {
                    if (renderers[i][k].getService().isGroupSymbolizer()) {
                        dataRendered |= renderers[i][k].portray(candidates);
                    }
                }
            }

        } finally {
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
        return dataRendered;
    }

    protected boolean contain(final Set<FeatureId> ids, final Object candidate){
        return ids.contains(id(candidate));
    }

}
