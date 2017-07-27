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
package org.geotoolkit.processing.chain.model;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.apache.sis.util.ArgumentChecks;

import javax.xml.bind.JAXBContext;
import org.apache.sis.xml.MarshallerPool;
import org.geotoolkit.util.StringUtilities;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;

/**
 * Represents a process chain.
 *
 * @author Johann Sorel (Geomatys)
 */
@XmlRootElement(name="chain")
@XmlAccessorType(XmlAccessType.FIELD)
public class Chain implements Comparable<Chain>,Parameterized {

    @XmlTransient
    private static MarshallerPool POOL;

    @XmlElement(name="name")
    protected String name;

    @XmlElement(name="input")
    protected List<Parameter> inputs;

    @XmlElement(name="output")
    protected List<Parameter> outputs;

    @XmlElement(name="constant")
    protected List<Constant> constants;

    @XmlElement(name="element")
    protected List<Element> chainElements;

    @XmlElement(name="dataLink")
    protected List<DataLink> links;

    @XmlElement(name="flowLink")
    protected List<FlowLink> executionLinks;


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
        for(Element cdt : chain.getElements()){
            getElements().add(cdt.copy());
        }
        for(Parameter cdt : chain.getInputs()){
            getInputs().add(new Parameter(cdt));
        }
        for(DataLink cdt : chain.getDataLinks()){
            getDataLinks().add(new DataLink(cdt));
        }
        for(FlowLink cdt : chain.getFlowLinks()){
            getFlowLinks().add(new FlowLink(cdt));
        }
        for(Parameter cdt : chain.getOutputs()){
            getOutputs().add(new Parameter(cdt));
        }
    }

    public Parameter addInputParameter(final String code, final Class type,
            final String remarks, final int minOccurs, final int maxOccurs, final Object defaultValue){
        final Parameter param = new Parameter(code, type, remarks, minOccurs, maxOccurs, defaultValue);
        getInputs().add(param);
        return param;
    }

    public Parameter addOutputParameter(final String code, final Class type,
            final String remarks, final int minOccurs, final int maxOccurs, final Object defaultValue){
        final Parameter param = new Parameter(code, type, remarks, minOccurs, maxOccurs, defaultValue);
        getOutputs().add(param);
        return param;
    }

    public Constant addConstant(final int id, final Class type, final Object value){
        final Constant constant = new Constant(id, type, value,0,0);
        getConstants().add(constant);
        return constant;
    }

    public ElementProcess addProcessElement(final int id, final String authority, final String code){
        final ElementProcess ele = new ElementProcess(id, authority, code);
        getElements().add(ele);
        return ele;
    }

    public ElementCondition addConditionElement(final int id){
        final ElementCondition ele = new ElementCondition(id);
        getElements().add(ele);
        return ele;
    }

    public ElementManual addManualElement(final int id){
        final ElementManual ele = new ElementManual(id);
        getElements().add(ele);
        return ele;
    }

    public FlowLink addFlowLink(int inId, int outId){
        final FlowLink link = new FlowLink(inId, outId);
        getFlowLinks().add(link);
        return link;
    }

    public DataLink addDataLink(int inId, String inCode, int outId, String outCode){
        final DataLink link = new DataLink(inId, inCode, outId, outCode);
        getDataLinks().add(link);
        return link;
    }

    /**
     * Return all link which come from a source id.
     *
     * @param sourceId
     * @return a list of LinkDto
     */
    public List<DataLink> getInputLinks(final int sourceId) {
        final List<DataLink> result = new ArrayList<DataLink>();
        if (links != null) {
            for (DataLink link : links) {
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
    public List<DataLink> getOutputLinks(final int targetId) {
        final List<DataLink> result = new ArrayList<DataLink>();
        if (links != null) {
            for (DataLink link : links) {
                if (link.getTargetId() == targetId) {
                    result.add(link);
                }
            }
        }
        return result;
    }

    public List<DataLink> getDataLinks() {
        if(links == null){
            links = new ArrayList<DataLink>();
        }
        return links;
    }

    public void setDataLinks(final List<DataLink> links) {
        this.links = links;
    }

    public List<FlowLink> getFlowLinks() {
        if (executionLinks == null) {
            executionLinks = new ArrayList<FlowLink>();
        }
        return executionLinks;
    }

    public void setFlowLinks(final List<FlowLink> executionLinks) {
        this.executionLinks = executionLinks;
    }

    public List<Element> getElements() {
        if(chainElements == null){
            chainElements = new ArrayList<Element>();
        }
        return chainElements;
    }

    public void setElements(final List<Element> descriptors) {
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
            return Objects.equals(this.getConstants(), that.getConstants())
                && Objects.equals(this.getElements(), that.getElements())
                && Objects.equals(this.getDataLinks(), that.getDataLinks())
                && Objects.equals(this.getFlowLinks(), that.getFlowLinks())
                && Objects.equals(this.name, that.name)
                && Objects.equals(this.getOutputs(), that.getOutputs())
                && Objects.equals(this.getInputs(), that.getInputs());
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
        sb.append(StringUtilities.toStringTree("Inputs", getInputs())).append('\n');
        sb.append(StringUtilities.toStringTree("Outputs", getOutputs())).append('\n');
        sb.append(StringUtilities.toStringTree("Constants", getConstants())).append('\n');
        sb.append(StringUtilities.toStringTree("Chain elements", getElements())).append('\n');
        sb.append(StringUtilities.toStringTree("Links", getDataLinks())).append('\n');
        sb.append(StringUtilities.toStringTree("ExecutionLinks", getFlowLinks())).append('\n');

        return sb.toString();
    }

    @Override
    public int compareTo(Chain o) {
        if (o != null) {
            return this.getName().compareTo(o.getName());
        }
        return -1;
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

    /**
     * Write this ProcessSequence in given output.
     * @throws JAXBException
     */
    public void write(final Object output) throws JAXBException{
        final MarshallerPool pool = getPoolInstance();
        final Marshaller marshaller = pool.acquireMarshaller();
        try{
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            if(output instanceof ContentHandler){
                marshaller.marshal(this, (ContentHandler)output);
            }else if(output instanceof File){
                marshaller.marshal(this, (File)output);
            }else if(output instanceof Node){
                marshaller.marshal(this, (Node)output);
            }else if(output instanceof OutputStream){
                marshaller.marshal(this, (OutputStream)output);
            }else if(output instanceof Result){
                marshaller.marshal(this, (Result)output);
            }else if(output instanceof Writer){
                marshaller.marshal(this, (Writer)output);
            }else if(output instanceof XMLEventWriter){
                marshaller.marshal(this, (XMLEventWriter)output);
            }else if(output instanceof XMLStreamWriter){
                marshaller.marshal(this, (XMLStreamWriter)output);
            }else{
                throw new JAXBException("Unsupported output : "+output);
            }
        }finally{
            pool.recycle(marshaller);
        }
    }

    /**
     * Read the given input and return an ProcessSequence.
     *
     * @param input
     * @return
     * @throws JAXBException
     */
    public static Chain read(final Object input) throws JAXBException{
        final MarshallerPool pool = getPoolInstance();
        final Unmarshaller unmarshaller = pool.acquireUnmarshaller();
        final Chain set;
        try{
            if(input instanceof File){
                set = (Chain) unmarshaller.unmarshal((File)input);
            }else if(input instanceof InputSource){
                set = (Chain) unmarshaller.unmarshal((InputSource)input);
            }else if(input instanceof InputStream){
                set = (Chain) unmarshaller.unmarshal((InputStream)input);
            }else if(input instanceof Node){
                set = (Chain) unmarshaller.unmarshal((Node)input);
            }else if(input instanceof Reader){
                set = (Chain) unmarshaller.unmarshal((Reader)input);
            }else if(input instanceof Source){
                set = (Chain) unmarshaller.unmarshal((Source)input);
            }else if(input instanceof URL){
                set = (Chain) unmarshaller.unmarshal((URL)input);
            }else if(input instanceof XMLEventReader){
                set = (Chain) unmarshaller.unmarshal((XMLEventReader)input);
            }else if(input instanceof XMLStreamReader){
                set = (Chain) unmarshaller.unmarshal((XMLStreamReader)input);
            }else{
                throw new JAXBException("Unsupported input : "+input);
            }
        }finally{
            pool.recycle(unmarshaller);
        }

        return set;
    }

    public static synchronized MarshallerPool getPoolInstance() throws JAXBException{
        if(POOL == null){
            POOL = new MarshallerPool(JAXBContext.newInstance(Chain.class), null);
        }
        return POOL;
    }

}
