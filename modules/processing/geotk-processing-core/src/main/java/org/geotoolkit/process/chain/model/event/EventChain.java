/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2013, Geomatys
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
package org.geotoolkit.process.chain.model.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.event.EventListenerList;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.parameter.ExtendedParameterDescriptor;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.process.ProcessingRegistry;
import org.geotoolkit.process.chain.ChainProcessDescriptor;
import org.geotoolkit.process.chain.model.Chain;
import org.geotoolkit.process.chain.model.ElementProcess;
import org.geotoolkit.process.chain.model.ClassFull;
import org.geotoolkit.process.chain.model.Constant;
import org.geotoolkit.process.chain.model.DataLink;
import org.geotoolkit.process.chain.model.Element;
import org.geotoolkit.process.chain.model.FlowLink;
import org.geotoolkit.process.chain.model.Parameter;
import org.geotoolkit.util.NumberRange;
import org.geotoolkit.util.collection.CollectionChangeEvent;
import org.geotoolkit.util.collection.NotifiedCheckedList;
import org.geotoolkit.util.converter.ObjectConverter;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.util.NoSuchIdentifierException;

/**
 * Extends a Chain, provides automatic link cleaning and events.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class EventChain extends Chain{

    /**
     * listeners
     */
    private final EventListenerList listeners = new EventListenerList();

    /**
     * Available process factories.
     */
    private final List<ProcessingRegistry> factories = new ArrayList<ProcessingRegistry>();

    /**
     * Type convertion tester.
     */
    private final ConverterMatcher matcher ;

    public EventChain(Collection<? extends ProcessingRegistry> factories, ConverterMatcher matcher){
        this(null,factories, matcher);
    }

    public EventChain(final Chain chain,Collection<? extends ProcessingRegistry> factories, ConverterMatcher matcher){
        super(chain);
        if(factories != null){
            this.factories.addAll((Collection)factories);
        }
        if(matcher == null){
            //use a default matcher, relying only on natural convertions.
            this.matcher = new ConverterMatcher(
                    new HashMap<ClassFull, List<ClassFull>>(),
                    new ArrayList<ObjectConverter>());
        }else{
            this.matcher = matcher;
        }
    }

    public ConverterMatcher getMatcher() {
        return matcher;
    }

    public List<ProcessingRegistry> getFactories() {
        return factories;
    }

    @Override
    public List<DataLink> getDataLinks() {
        if(links == null){
            links = new NotifiedCheckedList<DataLink>(DataLink.class) {
                @Override
                protected void notifyAdd(DataLink item, int index) {
                    //Null values not allowed
                    ArgumentChecks.ensureNonNull("item", item);
                    fireDataLinkChange(new CollectionChangeEvent(
                            this, item, CollectionChangeEvent.ITEM_ADDED, NumberRange.create(index, index), null));
                }
                @Override
                protected void notifyAdd(Collection<? extends DataLink> items, NumberRange<Integer> range) {
                    //Null values not allowed
                    ArgumentChecks.ensureNonNull("item", items);
                    for(Object lk : items){
                        ArgumentChecks.ensureNonNull("item", lk);
                    }
                    fireDataLinkChange(new CollectionChangeEvent(
                            this, items, CollectionChangeEvent.ITEM_ADDED, range, null));
                }
                @Override
                protected void notifyRemove(DataLink item, int index) {
                    fireDataLinkChange(new CollectionChangeEvent(
                            this, item, CollectionChangeEvent.ITEM_REMOVED, NumberRange.create(index, index), null));
                }
                @Override
                protected void notifyRemove(Collection<? extends DataLink> items, NumberRange<Integer> range) {
                    fireDataLinkChange(new CollectionChangeEvent(
                            this, items, CollectionChangeEvent.ITEM_REMOVED, range, null));
                }

                @Override
                protected void notifyChange(DataLink oldItem, DataLink newItem, int index) {
                }
            };
        }
        return links;
    }

    @Override
    public List<FlowLink> getFlowLinks() {
        if (executionLinks == null){
            executionLinks = new NotifiedCheckedList<FlowLink>(FlowLink.class) {
                @Override
                protected void notifyAdd(FlowLink item, int index) {
                    //Null values not allowed
                    ArgumentChecks.ensureNonNull("item", item);
                    fireFlowLinkChange(new CollectionChangeEvent(
                            this, item, CollectionChangeEvent.ITEM_ADDED, NumberRange.create(index, index), null));
                }
                @Override
                protected void notifyAdd(Collection<? extends FlowLink> items, NumberRange<Integer> range) {
                    //Null values not allowed
                    ArgumentChecks.ensureNonNull("item", items);
                    for(Object lk : items){
                        ArgumentChecks.ensureNonNull("item", lk);
                    }
                    fireFlowLinkChange(new CollectionChangeEvent(
                            this, items, CollectionChangeEvent.ITEM_ADDED, range, null));
                }
                @Override
                protected void notifyRemove(FlowLink item, int index) {
                    fireFlowLinkChange(new CollectionChangeEvent(
                            this, item, CollectionChangeEvent.ITEM_REMOVED, NumberRange.create(index, index), null));
                }
                @Override
                protected void notifyRemove(Collection<? extends FlowLink> items, NumberRange<Integer> range) {
                    fireFlowLinkChange(new CollectionChangeEvent(
                            this, items, CollectionChangeEvent.ITEM_REMOVED, range, null));
                }

                @Override
                protected void notifyChange(FlowLink oldItem, FlowLink newItem, int index) {
                }
            };
        }
        return executionLinks;
    }

    @Override
    public List<Element> getElements() {
        if(chainElements == null){
            chainElements = new NotifiedCheckedList<Element>(Element.class) {
                @Override
                protected void notifyAdd(Element item, int index) {
                    //Null values not allowed
                    ArgumentChecks.ensureNonNull("item", item);
                    fireDescriptorChange(new CollectionChangeEvent(
                            this, item, CollectionChangeEvent.ITEM_ADDED, NumberRange.create(index, index), null));
                }
                @Override
                protected void notifyAdd(Collection<? extends Element> items, NumberRange<Integer> range) {
                    //Null values not allowed
                    ArgumentChecks.ensureNonNull("item", items);
                    for(Object item : items){
                        ArgumentChecks.ensureNonNull("item", item);
                    }
                    fireDescriptorChange(new CollectionChangeEvent(
                            this, items, CollectionChangeEvent.ITEM_ADDED, range, null));
                }
                @Override
                protected void notifyRemove(Element item, int index) {
                    clearObsoleteLinks();
                    fireDescriptorChange(new CollectionChangeEvent(
                            this, item, CollectionChangeEvent.ITEM_REMOVED, NumberRange.create(index, index), null));
                }
                @Override
                protected void notifyRemove(Collection<? extends Element> items, NumberRange<Integer> range) {
                    clearObsoleteLinks();
                    fireDescriptorChange(new CollectionChangeEvent(
                            this, items, CollectionChangeEvent.ITEM_REMOVED, range, null));
                }

                @Override
                protected void notifyChange(Element oldItem, Element newItem, int index) {
                }
            };
        }
        return chainElements;
    }

    @Override
    public List<Constant> getConstants() {
        if(constants == null){
            constants = new NotifiedCheckedList<Constant>(Constant.class) {
                @Override
                protected void notifyAdd(Constant item, int index) {
                    //Null values not allowed
                    ArgumentChecks.ensureNonNull("item", item);
                    fireConstantChange(new CollectionChangeEvent(
                            this, item, CollectionChangeEvent.ITEM_ADDED, NumberRange.create(index, index), null));
                }
                @Override
                protected void notifyAdd(Collection<? extends Constant> items, NumberRange<Integer> range) {
                    //Null values not allowed
                    ArgumentChecks.ensureNonNull("item", items);
                    for(Object item : items){
                        ArgumentChecks.ensureNonNull("item", item);
                    }
                    fireConstantChange(new CollectionChangeEvent(
                            this, items, CollectionChangeEvent.ITEM_ADDED, range, null));
                }
                @Override
                protected void notifyRemove(Constant item, int index) {
                    clearObsoleteLinks();
                    fireConstantChange(new CollectionChangeEvent(
                            this, item, CollectionChangeEvent.ITEM_REMOVED, NumberRange.create(index, index), null));
                }
                @Override
                protected void notifyRemove(Collection<? extends Constant> items, NumberRange<Integer> range) {
                    clearObsoleteLinks();
                    fireConstantChange(new CollectionChangeEvent(
                            this, items, CollectionChangeEvent.ITEM_REMOVED, range, null));
                }

                @Override
                protected void notifyChange(Constant oldItem, Constant newItem, int index) {
                }
            };
        }
        return constants;
    }

    @Override
    public List<Parameter> getInputs() {
        if(inputs == null){
            inputs = new NotifiedCheckedList<Parameter>(Parameter.class) {
                @Override
                protected void notifyAdd(Parameter item, int index) {
                    //Null values not allowed
                    ArgumentChecks.ensureNonNull("item", item);
                    fireInputChange(new CollectionChangeEvent(
                            this, item, CollectionChangeEvent.ITEM_ADDED, NumberRange.create(index, index), null));
                }
                @Override
                protected void notifyAdd(Collection<? extends Parameter> items, NumberRange<Integer> range) {
                    //Null values not allowed
                    ArgumentChecks.ensureNonNull("item", items);
                    for(Object item : items){
                        ArgumentChecks.ensureNonNull("item", item);
                    }
                    fireInputChange(new CollectionChangeEvent(
                            this, items, CollectionChangeEvent.ITEM_ADDED, range, null));
                }
                @Override
                protected void notifyRemove(Parameter item, int index) {
                    clearObsoleteLinks();
                    fireInputChange(new CollectionChangeEvent(
                            this, item, CollectionChangeEvent.ITEM_REMOVED, NumberRange.create(index, index), null));
                }
                @Override
                protected void notifyRemove(Collection<? extends Parameter> items, NumberRange<Integer> range) {
                    clearObsoleteLinks();
                    fireInputChange(new CollectionChangeEvent(
                            this, items, CollectionChangeEvent.ITEM_REMOVED, range, null));
                }

                @Override
                protected void notifyChange(Parameter oldItem, Parameter newItem, int index) {
                }
            };
        }
        return inputs;
    }

    @Override
    public List<Parameter> getOutputs() {
        if(outputs == null){
            outputs = new NotifiedCheckedList<Parameter>(Parameter.class) {
                @Override
                protected void notifyAdd(Parameter item, int index) {
                    //Null values not allowed
                    ArgumentChecks.ensureNonNull("item", item);
                    fireOutputChange(new CollectionChangeEvent(
                            this, item, CollectionChangeEvent.ITEM_ADDED, NumberRange.create(index, index), null));
                }
                @Override
                protected void notifyAdd(Collection<? extends Parameter> items, NumberRange<Integer> range) {
                    //Null values not allowed
                    ArgumentChecks.ensureNonNull("item", items);
                    for(Object item : items){
                        ArgumentChecks.ensureNonNull("item", item);
                    }
                    fireOutputChange(new CollectionChangeEvent(
                            this, items, CollectionChangeEvent.ITEM_ADDED, range, null));
                }
                @Override
                protected void notifyRemove(Parameter item, int index) {
                    clearObsoleteLinks();
                    fireOutputChange(new CollectionChangeEvent(
                            this, item, CollectionChangeEvent.ITEM_REMOVED, NumberRange.create(index, index), null));
                }
                @Override
                protected void notifyRemove(Collection<? extends Parameter> items, NumberRange<Integer> range) {
                    clearObsoleteLinks();
                    fireOutputChange(new CollectionChangeEvent(
                            this, items, CollectionChangeEvent.ITEM_REMOVED, range, null));
                }

                @Override
                protected void notifyChange(Parameter oldItem, Parameter newItem, int index) {
                }
            };
        }
        return outputs;
    }

    /**
     * Not supported on this class, events would be lost.
     * @param descriptors
     */
    @Override
    public void setElements(final List<Element> descriptors) {
        throw new UnsupportedOperationException("Replacing chain collections is not permitted.");
    }

    /**
     * Not supported on this class, events would be lost.
     * @param constants
     */
    @Override
    public void setConstants(final List<Constant> constants) {
        throw new UnsupportedOperationException("Replacing chain collections is not permitted.");
    }

    /**
     * Not supported on this class, events would be lost.
     * @param inputs
     */
    @Override
    public void setInputs(final List<Parameter> inputs) {
        throw new UnsupportedOperationException("Replacing chain collections is not permitted.");
    }

    /**
     * Not supported on this class, events would be lost.
     * @param links
     */
    @Override
    public void setDataLinks(final List<DataLink> links) {
        throw new UnsupportedOperationException("Replacing chain collections is not permitted.");
    }

    /**
     * Not supported on this class, events would be lost.
     * @param links
     */
    @Override
    public void setFlowLinks(final List<FlowLink> executionLinks) {
        throw new UnsupportedOperationException("Replacing chain collections is not permitted.");
    }

    /**
     * Not supported on this class, events would be lost.
     * @param outputs
     */
    @Override
    public void setOutputs(final List<Parameter> outputs) {
        throw new UnsupportedOperationException("Replacing chain collections is not permitted.");
    }

    /**
     * @return integer, next id not used.
     */
    public synchronized int getNextId(){
        int maxId = 0;
        for(Element desc : getElements()){
            if(desc.getId() > maxId) {maxId = desc.getId();}
        }
        for(Constant cst : getConstants()){
            if(cst.getId() > maxId) {maxId = cst.getId();}
        }
        return maxId+1;
    }

    /**
     * Check if given execution link is valid.
     * @param link
     * @return true only if the link is valid
     */
    public boolean isValidFlowLink(final FlowLink link){
        if (link.getSourceId() == -1 || link.getTargetId() == -1 || link.getSourceId() == link.getTargetId() 
                || link.getTargetId() == Integer.MIN_VALUE || link.getSourceId() == Integer.MAX_VALUE) {
            return false;
        }
        return true;
    }

    /**
     * Check if the given link is valid.
     * @return true only if the link is valid
     */
    public boolean isValidLink(final DataLink link){
        if(link == null) {return false;}

        final Object source = link.getSource(this);
        final Object target = link.getTarget(this);

        //source and target must exist
        if(source == null || target == null){
            return false;
        }

        //input can not go on output of same process
        if(source == target){
            return false;
        }

        //we can not have more then one link pointing to the same target
        final List<DataLink> otherLinks = new ArrayList<DataLink>();
        for(DataLink lk : getDataLinks()){
            if(lk.getTargetId() == link.getTargetId() && lk.getTargetCode().equals(link.getTargetCode())){
                otherLinks.add(lk);
            }
        }
        
        if (!otherLinks.isEmpty()) {
            final Integer newSourceId = link.getSourceId();
            final List<FlowLink> execLinks = getFlowLinkFromId(link.getTargetId(), true);
            clearFlowLinkList(execLinks, otherLinks);
            
            if (execLinks.isEmpty()) {
                return false;
            } else {
                
                final Set<Integer> newSourceConnditional = new HashSet<Integer>();
                searchForConditionalParent(newSourceId, newSourceConnditional);
                
                if (newSourceConnditional.isEmpty()) {
                    return false;
                } else {
                    final Set<Integer> otherSourceConditonal = new HashSet<Integer>();
                    for (FlowLink exec : execLinks) {
                        if (exec.getSourceId() != newSourceId) {
                            searchForConditionalParent(exec.getSourceId(), otherSourceConditonal);
                        }
                    }
                    
                    boolean valid = false;
                    for (Integer id : newSourceConnditional) {
                        if (otherSourceConditonal.contains(id)) {
                            valid = true;
                            break;
                        }
                    }
                    if (!valid) {
                        return false;
                    }
                }
            }
        }
        

        //check types
        final Object sourceDataType = getDataType(source, false, link.getSourceCode());
        final Object targetDataType = getDataType(target, true, link.getTargetCode());

        return matcher.canBeConverted(sourceDataType, targetDataType);
    }

    private Object getElementFromId(final Integer identifier) {
        for (Element chainElement : getElements()) {
            if (chainElement.getId().equals(identifier)) {
                return chainElement;
            }
        }
        return null;
    }
    
    private void clearFlowLinkList(final List<FlowLink> inputExecLinks, final List<DataLink> inputLinks) {
        final List<FlowLink> clearedExecLinkList = new ArrayList<FlowLink>();
        for (DataLink lk : inputLinks) {
            for (FlowLink executionLink : inputExecLinks) {
                if (executionLink.getSourceId() == lk.getSourceId()) {
                    clearedExecLinkList.add(executionLink);
                    break;
                }
            }
        }
        inputLinks.removeAll(clearedExecLinkList);
    }
    
    private List<FlowLink> getFlowLinkFromId(final Integer identifier, boolean source) {
        final List<FlowLink> links = new ArrayList<FlowLink>();
        for (FlowLink execLink : getFlowLinks()) {
            if (source) {
                if (execLink.getTargetId() == identifier.intValue()) {
                    links.add(execLink);
                }
            } else {
                if (execLink.getSourceId() == identifier.intValue()) {
                    links.add(execLink);
                }
            }
        }
        return links;
    }
    
    private void searchForConditionalParent(final Integer startId, final Set<Integer> conditionalIdentifiers) {
        final List<FlowLink> execLinks = getFlowLinkFromId(startId, true);
        for (FlowLink exec : execLinks) {
            final Object sourceElement = getElementFromId(exec.getSourceId());
            if (sourceElement instanceof ElementProcess){
                final ElementProcess chainElement = (ElementProcess) sourceElement;
                if (!chainElement.getCode().equals("begin")) {
                    searchForConditionalParent(chainElement.getId(), conditionalIdentifiers);
                }
            }
        }
    }
        
    /**
     * Return the Java class of a parameter or constant.
     *
     * @param obj Could be a Parameter, Constant or ChainElement
     * @param in in case of ChainElement obj, search code in input or output parameters. True to search in inputs, false for outputs.
     * @param code of the parameter in case of ChainElement obj.
     * @return Class or ClassFull for Parameter.
     */
    private Object getDataType(final Object obj, final boolean in, final String code){
        if(obj instanceof Parameter){
            final Parameter dto = (Parameter) obj;
            return dto.getType();
        }else if(obj instanceof Constant){
            final Constant dto = (Constant) obj;
            return dto.getType();
        }else if(obj instanceof ElementProcess){
            final ElementProcess dto = (ElementProcess) obj;

            try {
                final ProcessDescriptor pd = ProcessFinder.getProcessDescriptor(
                    getFactories().iterator(), dto.getAuthority(), dto.getCode());

                final GeneralParameterDescriptor gd = (in) ?
                        pd.getInputDescriptor().descriptor(code) :
                        pd.getOutputDescriptor().descriptor(code);

                if(gd instanceof ExtendedParameterDescriptor){
                    Object type = ((ExtendedParameterDescriptor)gd).getUserObject().get(ChainProcessDescriptor.KEY_DISTANT_CLASS);

                    if(type == null){
                        //rely on base type
                        type = ((ExtendedParameterDescriptor)gd).getValueClass();
                    }

                    return type;
                }else if(gd instanceof ParameterDescriptor){
                    final Object type = ((ParameterDescriptor)gd).getValueClass();
                    return type;
                }

            } catch (NoSuchIdentifierException ex) {
                return null;
            }
        }

        return null;
    }

    /**
     * Search in links list the link with element identifier and parameter code in source or target.
     * @param elementId identifier of the widget element.
     * @param code of the parameter
     * @param source if true, search elementId in source, and in target if false.
     * @return a list of Link or empty list if not found.
     */
    public List<DataLink> findDataLink(final int elementId, final String code, final boolean source) {
        final List<DataLink> foundLinks = new ArrayList<DataLink>();
        if (links != null) {
            for (final DataLink link : links) {
                if (source && link.getSourceId() == elementId ) {
                    if (link.getSource(this) instanceof Constant) {
                         foundLinks.add(link);
                    } else if (link.getSourceCode().equals(code)) {
                         foundLinks.add(link);
                    }
                } else if (!source && link.getTargetId() == elementId) {
                    if (link.getTarget(this) instanceof Constant) {
                         foundLinks.add(link);
                    } else if (link.getTargetCode().equals(code)) {
                         foundLinks.add(link);
                    }
                }
            }
        }
        return foundLinks;
    }

    public void addListener(final ChainListener listener){
        listeners.add(ChainListener.class, listener);
    }

    public void removeListener(final ChainListener listener){
        listeners.remove(ChainListener.class, listener);
    }

    private void clearObsoleteLinks(){
        final List<DataLink> lst = new ArrayList<DataLink>(getDataLinks());
        for(final DataLink link : lst){
            //remove link if source or target doesn't exist anymore
            if(link.getSource(this) == null){
                getDataLinks().remove(link);
            }else if(link.getTarget(this) == null){
                getDataLinks().remove(link);
            }
        }
        final List<FlowLink> lstEx = new ArrayList<FlowLink>(getFlowLinks());
        for(final FlowLink link : lstEx){
            //remove link if source or target doesn't exist anymore
            if(link.getSource(this) == null){
                getFlowLinks().remove(link);
            }else if(link.getTarget(this) == null){
                getFlowLinks().remove(link);
            }
        }
    }

    private void fireConstantChange(final CollectionChangeEvent event){
        //can be null for a short time when using copy constructor of Chain
        if(listeners != null){
            for(ChainListener lst : listeners.getListeners(ChainListener.class)){
                lst.constantChange(event);
            }
        }
    }

    private void fireDescriptorChange(final CollectionChangeEvent event){
        //can be null for a short time when using copy constructor of Chain
        if(listeners != null){
            for(ChainListener lst : listeners.getListeners(ChainListener.class)){
                lst.descriptorChange(event);
            }
        }
    }

    private void fireDataLinkChange(final CollectionChangeEvent event){
        //can be null for a short time when using copy constructor of Chain
        if(listeners != null){
            for(ChainListener lst : listeners.getListeners(ChainListener.class)){
                lst.linkChange(event);
            }
        }
    }

    private void fireFlowLinkChange(final CollectionChangeEvent event){
        //can be null for a short time when using copy constructor of Chain
        if(listeners != null){
            for(ChainListener lst : listeners.getListeners(ChainListener.class)){
                lst.executionLinkChange(event);
            }
        }
    }

    private void fireInputChange(final CollectionChangeEvent event){
        //can be null for a short time when using copy constructor of Chain
        if(listeners != null){
            for(ChainListener lst : listeners.getListeners(ChainListener.class)){
                lst.inputChange(event);
            }
        }
    }

    private void fireOutputChange(final CollectionChangeEvent event){
        //can be null for a short time when using copy constructor of Chain
        if(listeners != null){
            for(ChainListener lst : listeners.getListeners(ChainListener.class)){
                lst.outputChange(event);
            }
        }
    }

}
