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
package org.geotoolkit.process.chain;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.process.chain.model.Chain;
import org.geotoolkit.process.chain.model.ChainElement;
import org.geotoolkit.process.chain.model.Constant;
import org.geotoolkit.process.chain.model.DataLink;
import org.geotoolkit.util.converter.ConverterRegistry;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.util.converter.ObjectConverter;
import org.geotoolkit.util.logging.Logging;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.NoSuchIdentifierException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @author Guilhem Legal (Geomatys)
 */
public class ChainProcess extends AbstractProcess{

    public static final ConverterRegistry CONVERTERS = ConverterRegistry.system();
    
    protected static final Logger LOGGER = Logging.getLogger(ChainProcess.class);

    private Process currentProcess;


    public ChainProcess(final ChainProcessDescriptor desc, final ParameterValueGroup input) {
        super(desc, input);
    }

    /**
     * Returns a {@linkplain ChainProcessDescriptor descriptor} of this chain.
     */
    @Override
    public ChainProcessDescriptor getDescriptor() {
        return (ChainProcessDescriptor)super.getDescriptor();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void execute() throws ProcessException {

        final Chain model = getDescriptor().getModel();

        // processing progress
        final float part = 100 / model.getChainElements().size();
        int i = 1;

        final Collection<ChainFlow.ExecutionNode> nodes = ChainFlow.createExecutionFlow(model);
        List<List<ChainFlow.ExecutionNode>> ranked = ChainFlow.sortByRankExec(nodes);

        //prepare all parameters for each process step
        final Map<Integer, ParameterValueGroup> configs = new HashMap<Integer, ParameterValueGroup>();

        for (ChainFlow.ExecutionNode node : nodes) {
            final Object obj = node.getObject();

            if(obj == ChainElement.END) {
                configs.put(Integer.MAX_VALUE, outputParameters);
            } else if(obj == ChainElement.BEGIN){
                // do nothing

            } else if(obj instanceof ChainElement){
                final ChainElement element = (ChainElement) obj;
                final ProcessDescriptor desc;
                try {
                    desc = getProcessDescriptor(element);
                } catch (NoSuchIdentifierException ex) {
                    throw new ProcessException("Sub process "+element.getAuthority()+"."+element.getCode()+" not found.", this, ex);
                }
                configs.put(element.getId(), desc.getInputDescriptor().createValue());
            }
        }

        //set all constant values in configurations
        for(Constant cst : model.getConstants()){
            //copy constant in children nodes
            final Object value = ConstantUtilities.stringToValue(cst.getValue(), cst.getType());
            for(DataLink link : model.getInputLinks(cst.getId())){
                setValue(value, configs.get(link.getTargetId()).parameter(link.getTargetCode()));
            }
        }

        // Will contain all the versions of processes used
        final StringBuilder processVersion = new StringBuilder();

        //run processes in order
        for (int j = 0; j < ranked.size(); j++) {
            final List<ChainFlow.ExecutionNode> rank = ranked.get(j);

            for(ChainFlow.ExecutionNode node : rank){
                final Object obj = node.getObject();
                if (obj == ChainElement.BEGIN) {
                    //copy input params in children nodes
                    for(DataLink link : model.getInputLinks(Integer.MIN_VALUE)){
                        final Object value = inputParameters.parameter(link.getSourceCode()).getValue();
                        setValue(value, configs.get(link.getTargetId()).parameter(link.getTargetCode()));
                    }
                } else if (obj == ChainElement.END) {
                    // do nothing

                } else if(obj instanceof ChainElement) {
                    // handle process cancel
                    if (isCanceled()) {
                       throw new ProcessException("Process Canceled by user", this, null);
                    }
                    // handle process pause
                    if (isPaused()) {
                        fireProcessPaused(descriptor.getIdentifier().getCode() + " paused", i * part);
                        while (isPaused()) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException ex) {
                                LOGGER.log(Level.WARNING, "Interruption while process is in pause", ex);
                            }
                        }
                        fireProcessResumed(descriptor.getIdentifier().getCode() + " resumed", i * part);
                    }

                    //execute process
                    final ChainElement element = (ChainElement) obj;
                    final ParameterValueGroup config = configs.get(element.getId());
                    final ProcessDescriptor pdesc;
                    try {
                        pdesc = getProcessDescriptor(element);
                    } catch (NoSuchIdentifierException ex) {
                        throw new ProcessException("Sub process not found", this, ex);
                    }
                    currentProcess = pdesc.createProcess(config);

                    final String processId = pdesc.getIdentifier().getCode();
                    // Fill process version with values coming from the current process.
                    if (processVersion.length() > 0) {
                        processVersion.append(", ");
                    }
                    processVersion.append(processId).append(" ")
                            .append(((AbstractProcessDescriptor)currentProcess.getDescriptor()).getVersion());

                    final ParameterValueGroup result = currentProcess.call();
                    fireProgressing(pdesc.getIdentifier().getCode() + " completed", i * part, false);
                    i++;

                    //set result in children
                    for(DataLink link : model.getInputLinks(element.getId())){
                        final Object value = result.parameter(link.getSourceCode()).getValue();
                        setValue(value, configs.get(link.getTargetId()).parameter(link.getTargetCode()));
                    }
                }
            }
        }

    }

    private ProcessDescriptor getProcessDescriptor(final ChainElement desc) throws NoSuchIdentifierException{
        return ProcessFinder.getProcessDescriptor(getDescriptor().getFactories().iterator(),
                desc.getAuthority(), desc.getCode());
    }

    private void setValue(Object value, ParameterValue param){
        final Class cs = param.getDescriptor().getValueClass();
        value = convert(value, cs);
        param.setValue(value);
    }

    public static <T> T convert(final Object candidate, final Class<T> target) {
        if (candidate == null) {
            return null;
        }
        if (target == null) {
            return (T) candidate;
        }
        return (T) convert(candidate, (Class) candidate.getClass(), target);
    }

    private static <S,T> T convert(final S candidate, final Class<S> source, final Class<T> target) {

        // handle case of source being an instance of target up front
        if (target.isAssignableFrom(source) ) {
            return (T) candidate;
        }

        final ObjectConverter<S,T> converter;
        try {
            converter = CONVERTERS.converter(source, target);
            return converter.convert(candidate);
        } catch (NonconvertibleObjectException ex) {
            LOGGER.log(Level.INFO, "convert", ex);
            return null;
        }
    }

    @Override
    public void cancelProcess() {
        super.cancelProcess();
        if (currentProcess instanceof AbstractProcess) {
            ((AbstractProcess)currentProcess).cancelProcess();
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[ChainProcess]");
        final Chain chain = getDescriptor().getModel();
        if (chain != null) {
            sb.append("Chain: \n");
            sb.append("elements:\n");
            for (ChainElement element : chain.getChainElements()) {
                sb.append(element).append('\n');
            }
        }
        return sb.toString();
    }
}
