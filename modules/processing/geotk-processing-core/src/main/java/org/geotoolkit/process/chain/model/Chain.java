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
package org.geotoolkit.process.chain.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.gui.swing.tree.Trees;
import org.geotoolkit.util.Utilities;
import org.opengis.parameter.ParameterDescriptor;

/**
 * Represents a process chain.
 *
 * @author Johann Sorel (Geomatys)
 */
@XmlRootElement(name="ProcessChain")
@XmlAccessorType(XmlAccessType.FIELD)
public class Chain implements Comparable<Chain> {

    public static final Integer IN_PARAMS = Integer.MIN_VALUE;
    public static final Integer OUT_PARAMS = Integer.MAX_VALUE;
    
    @XmlElement(name="name")
    protected String name;

    @XmlElement(name="input")
    protected List<Parameter> inputs;

    @XmlElement(name="output")
    protected List<Parameter> outputs;

    @XmlElement(name="constant")
    protected List<Constant> constants;

    @XmlElement(name="descriptor")
    protected List<ChainElement> chainElements;

    @XmlElement(name="links")
    protected List<LinkDto> links;

    @XmlElement(name="executionLinks")
    protected List<ExecutionLinkDto> executionLinks;


    private Chain() {
    }
    
    public Chain(String name) {
        ArgumentChecks.ensureNonEmpty("name", name);
        this.name = name;
    }

    /**
     * Constructor by copy.
     * Each sub element is all copied.
     *
     * @param chain
     */
    public Chain(final Chain chain) {
        if(chain == null) {return;}

        this.name            = chain.getName();

        //deep copy
        for(Constant cdt : chain.getConstants()){
            getConstants().add(new Constant(cdt));
        }
        for(ChainElement cdt : chain.getChainElements()){
            getChainElements().add(new ChainElement(cdt));
        }
        for(Parameter cdt : chain.getInputs()){
            getInputs().add(new Parameter(cdt));
        }
        for(LinkDto cdt : chain.getLinks()){
            getLinks().add(new LinkDto(cdt));
        }
        for(ExecutionLinkDto cdt : chain.getExecutionLinks()){
            getExecutionLinks().add(new ExecutionLinkDto(cdt));
        }
        for(Parameter cdt : chain.getOutputs()){
            getOutputs().add(new Parameter(cdt));
        }
    }

     /**
     * Return all link which come from a source id.
     * 
     * @param sourceId
     * @return a list of LinkDto
     */
    public List<LinkDto> getInputLinks(final int sourceId) {
        final List<LinkDto> result = new ArrayList<LinkDto>();
        if (links != null) {
            for (LinkDto link : links) {
                if (link.getSourceId() == sourceId) {
                    result.add(link);
                }
            }
        }
        return result;
    }

    /**
     * Return all link which point to target id.
     * 
     * @param targetId
     * @return a list of LinkDto
     */
    public List<LinkDto> getOutputLinks(final int targetId) {
        final List<LinkDto> result = new ArrayList<LinkDto>();
        if (links != null) {
            for (LinkDto link : links) {
                if (link.getTargetId() == targetId) {
                    result.add(link);
                }
            }
        }
        return result;
    }

    public List<LinkDto> getLinks() {
        if(links == null){
            links = new ArrayList<LinkDto>();
        }
        return links;
    }

    public void setLinks(final List<LinkDto> links) {
        this.links = links;
    }

    public List<ExecutionLinkDto> getExecutionLinks() {
        if (executionLinks == null) {
            executionLinks = new ArrayList<ExecutionLinkDto>();
        }
        return executionLinks;
    }

    public void setExecutionLinks(final List<ExecutionLinkDto> executionLinks) {
        this.executionLinks = executionLinks;
    }

    public List<ChainElement> getChainElements() {
        if(chainElements == null){
            chainElements = new ArrayList<ChainElement>();
        }
        return chainElements;
    }

    public void setChainElements(final List<ChainElement> descriptors) {
        this.chainElements = descriptors;
    }

    public List<Constant> getConstants() {
        if(constants == null){
            constants = new ArrayList<Constant>();
        }
        return constants;
    }

    public void setConstants(final List<Constant> constants) {
        this.constants = constants;
    }

    public List<Parameter> getInputs() {
        if(inputs == null){
            inputs = new ArrayList<Parameter>();
        }
        return inputs;
    }

    public void setInputs(final List<Parameter> inputs) {
        this.inputs = inputs;
    }

    public List<Parameter> getOutputs() {
        if(outputs == null){
            outputs = new ArrayList<Parameter>();
        }
        return outputs;
    }

    public void setOutputs(final List<Parameter> outputs) {
        this.outputs = outputs;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<ClassFull> getAllUsedClasses() {
        final List<ClassFull> result = new ArrayList<ClassFull>();
        if (inputs != null) {
            for (Parameter param : inputs) {
                if (!result.contains(param.getType())) {
                    result.add(param.getType());
                }
            }
        }
        if (outputs != null) {
            for (Parameter param : outputs) {
                if (!result.contains(param.getType())) {
                    result.add(param.getType());
                }
            }
        }
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Chain) {
            final Chain that = (Chain) obj;
            return Utilities.equals(this.getConstants(), that.getConstants())
                && Utilities.equals(this.getChainElements(), that.getChainElements())
                && Utilities.equals(this.getLinks(), that.getLinks())
                && Utilities.equals(this.getExecutionLinks(), that.getExecutionLinks())
                && Utilities.equals(this.name, that.name)
                && Utilities.equals(this.getOutputs(), that.getOutputs())
                && Utilities.equals(this.getInputs(), that.getInputs());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 97;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[ChainDto]");
        if (name != null) {
            sb.append("name:").append(name).append('\n');
        }
        sb.append(Trees.toString("Inputs", getInputs()));
        sb.append(Trees.toString("Outputs", getOutputs()));
        sb.append(Trees.toString("Constants", getConstants()));
        sb.append(Trees.toString("Chain elements", getChainElements()));
        sb.append(Trees.toString("Links", getLinks()));
        sb.append(Trees.toString("ExecutionLinks", getExecutionLinks()));

        return sb.toString();
    }

    @Override
    public int compareTo(Chain o) {
        if (o != null) {
            return this.getName().compareTo(o.getName());
        }
        return -1;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class LinkDto {
        @XmlElement(name="sourceId")
        private int sourceId;
        @XmlElement(name="sourceCode")
        private String sourceCode;
        @XmlElement(name="targetId")
        private int targetId;
        @XmlElement(name="targetCode")
        private String targetCode;

        private LinkDto() {}

        public LinkDto(LinkDto toCopy){
            this.sourceId = toCopy.sourceId;
            this.sourceCode = toCopy.sourceCode;
            this.targetId = toCopy.targetId;
            this.targetCode = toCopy.targetCode;
        }

        public LinkDto(int inId, String inCode, int outId, ParameterDescriptor paramDesc) {
            this(inId,inCode,outId,paramDesc.getName().getCode());
        }
        
        public LinkDto(int inId, ParameterDescriptor inCode, int outId, String paramDesc) {
            this(inId,inCode.getName().getCode(),outId,paramDesc);
        }
        
        public LinkDto(int inId, ParameterDescriptor inCode, int outId, ParameterDescriptor paramDesc) {
            this(inId,inCode.getName().getCode(),outId,paramDesc.getName().getCode());
        }
        
        public LinkDto(int inId, String inCode, int outId, String outCode) {
            this.sourceId = inId;
            this.sourceCode = inCode;
            this.targetId = outId;
            this.targetCode = outCode;
        }

        public int getSourceId() {
            return sourceId;
        }

        public void setSourceId(int sourceId) {
            this.sourceId = sourceId;
        }

        public String getSourceCode() {
            return sourceCode;
        }

        public void setSourceCode(String sourceCode) {
            this.sourceCode = sourceCode;
        }

        public int getTargetId() {
            return targetId;
        }

        public void setTargetId(int targetId) {
            this.targetId = targetId;
        }

        public String getTargetCode() {
            return targetCode;
        }

        public void setTargetCode(String targetCode) {
            this.targetCode = targetCode;
        }

        /**
         * @return source pointed by this link
         */
        public Object getSource(final Chain chain){

            //source is an input param
            if(sourceId == Integer.MIN_VALUE){
                for(Parameter param : chain.getInputs()){
                    if(param.getCode().equals(sourceCode)){
                        return param;
                    }
                }
            }

            for(ChainElement desc : chain.getChainElements()){
                if(desc.getId() == sourceId){
                    return desc;
                }
            }
            for(Constant cst : chain.getConstants()){
                if(cst.getId() == sourceId){
                    return cst;
                }
            }
            return null;
        }

        /**
         * @return target pointed by this link
         */
        public Object getTarget(final Chain seq){

            //targer is an output param
            if(targetId == Integer.MAX_VALUE){
                for(Parameter param : seq.getOutputs()){
                    if(param.getCode().equals(targetCode)){
                        return param;
                    }
                }
            }

            for(ChainElement desc : seq.getChainElements()){
                if(desc.getId() == targetId){
                    return desc;
                }
            }
            for(Constant cst : seq.getConstants()){
                if(cst.getId() == targetId){
                    return cst;
                }
            }
            return null;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof LinkDto) {
                final LinkDto that = (LinkDto) obj;
                return Utilities.equals(this.sourceCode, that.sourceCode)
                    && Utilities.equals(this.sourceId, that.sourceId)
                    && Utilities.equals(this.targetCode, that.targetCode)
                    && Utilities.equals(this.targetId, that.targetId);
            }
            return false;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("[LinkDto]");
            sb.append("sourceId:").append(sourceId).append('\n');
            sb.append("targetId:").append(targetId).append('\n');
            if (sourceCode != null) {
                sb.append("sourceCode:").append(sourceCode).append('\n');
            }
            if (targetCode != null) {
                sb.append("targetCode:").append(targetCode).append('\n');
            }
            return sb.toString();
        }

    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ExecutionLinkDto {
        @XmlElement(name="sourceId")
        private int sourceId;
        @XmlElement(name="targetId")
        private int targetId;
        
        @XmlTransient
        private String type = null;
        
        private ExecutionLinkDto() {}

        public ExecutionLinkDto(final ExecutionLinkDto toCopy) {
            if (toCopy != null) {
                this.sourceId = toCopy.sourceId;
                this.targetId = toCopy.targetId;
            }
        }

        public ExecutionLinkDto(int inId, int outId) {
            this.sourceId = inId;
            this.targetId = outId;
        }

        public int getSourceId() {
            return sourceId;
        }

        public void setSourceId(int sourceId) {
            this.sourceId = sourceId;
        }

        public int getTargetId() {
            return targetId;
        }

        public void setTargetId(int targetId) {
            this.targetId = targetId;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        /**
         * @return source pointed by this link
         */
        public Object getSource(final Chain chain){

            //source is an input param
            if(sourceId == Integer.MIN_VALUE){
                return ChainElement.BEGIN;
            }

            for (ChainElement desc : chain.getChainElements()) {
                if(desc.getId() == sourceId){
                    return desc;
                }
            }
            return null;
        }

        /**
         * @return target pointed by this link
         */
        public Object getTarget(final Chain seq){

            //targer is an output param
            if(targetId == Integer.MAX_VALUE){
                return ChainElement.END;
            }

            for (ChainElement desc : seq.getChainElements()) {
                if(desc.getId() == targetId){
                    return desc;
                }
            }
            return null;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof ExecutionLinkDto) {
                final ExecutionLinkDto that = (ExecutionLinkDto) obj;
                return Utilities.equals(this.sourceId, that.sourceId)
                    && Utilities.equals(this.targetId, that.targetId);
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 37 * hash + this.sourceId;
            hash = 37 * hash + this.targetId;
            return hash;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("[ExecutionLinkDto]");
            sb.append("sourceId:").append(sourceId).append('\n');
            sb.append("targetId:").append(targetId).append('\n');
            return sb.toString();
        }

    }

    public static class ClassAdaptor extends XmlAdapter<String, Class>{

        @Override
        public Class unmarshal(final String v) throws Exception {
            return Class.forName(v);
        }

        @Override
        public String marshal(final Class v) throws Exception {
            return v.getName();
        }

    }

}
