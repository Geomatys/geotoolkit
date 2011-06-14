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
package org.geotoolkit.wps;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.geotoolkit.client.AbstractRequest;
import org.geotoolkit.ows.xml.v110.BoundingBoxType;
import org.geotoolkit.ows.xml.v110.CodeType;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.wps.xml.WPSMarshallerPool;
import org.geotoolkit.wps.xml.v100.ComplexDataType;
import org.geotoolkit.wps.xml.v100.DataInputsType;
import org.geotoolkit.wps.xml.v100.DataType;
import org.geotoolkit.wps.xml.v100.DocumentOutputDefinitionType;
import org.geotoolkit.wps.xml.v100.Execute;
import org.geotoolkit.wps.xml.v100.InputReferenceType;
import org.geotoolkit.wps.xml.v100.InputType;
import org.geotoolkit.wps.xml.v100.LiteralDataType;
import org.geotoolkit.wps.xml.v100.OutputDefinitionType;
import org.geotoolkit.wps.xml.v100.ResponseDocumentType;
import org.geotoolkit.wps.xml.v100.ResponseFormType;

/**
 * Abstract get capabilities request.
 * 
 * @author Quentin Boileau
 * @module pending
 */
public abstract class AbstractExecute extends AbstractRequest implements ExecuteRequest{
    
    protected final String version;
    protected String identifier;
    protected String outputForm;
    protected boolean storage;
    protected boolean lineage;
    protected boolean status;
    protected List<WPSOutput> outputs;
    protected List<AbstractWPSInput> inputs;
    
    
    /**
     * Constructor, initialize status, lineage and storage to false and output form to "document"
     * @param serverURL
     * @param version 
     */
    protected AbstractExecute(final String serverURL,final String version, final ClientSecurity security){
        super(serverURL,security,null);
        this.version = version;
        this.status = false;
        this.lineage = false;
        this.storage = false;
        this.outputForm = "document";
        this.outputs = new ArrayList<WPSOutput>();
        this.inputs = new ArrayList<AbstractWPSInput>();
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public String getIdentifier(){
        return identifier;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setIdentifier(String identifiers){
        this.identifier = identifiers;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public String getOutputForm(){
        return outputForm;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setOutputForm(String outForm){
        this.outputForm = outForm;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public boolean getOutputStorage(){
        return storage;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setOutputStorage(boolean outStrorage){
        this.storage = outStrorage;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public boolean getOutputLineage(){
        return lineage;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setOutputLineage(boolean outLineage){
        this.lineage = outLineage;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean getOutputStatus(){
        return status;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setOutputStatus(boolean outStatus){
        this.status = outStatus;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public List<WPSOutput> getOutputs(){
        return outputs;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setOutputs(List<WPSOutput> outForm){
        this.outputs = outForm;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public List<AbstractWPSInput> getInputs(){
        return inputs;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setInputs(List<AbstractWPSInput> inputs){
        this.inputs = inputs;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public InputStream getResponseStream() throws IOException {

        final Execute request = makeRequest();
        
        final URL url = new URL(serverURL);
        URLConnection conec = url.openConnection();
        conec = security.secure(conec);

        conec.setDoOutput(true);
        conec.setRequestProperty("Content-Type", "text/xml");

        OutputStream stream = conec.getOutputStream();
        stream = security.encrypt(stream);
        Marshaller marshaller = null;
        try {
            marshaller = WPSMarshallerPool.getInstance().acquireMarshaller();
            marshaller.marshal(request, stream);
            //marshaller.marshal(request, System.out);
        } catch (JAXBException ex) {
            throw new IOException(ex);
        } finally {
            if (marshaller != null) {
                WPSMarshallerPool.getInstance().release(marshaller);
            }
        }
        stream.close();
        return security.decrypt(conec.getInputStream());
    }
    
    public Execute makeRequest(){
        final Execute request = new Execute();
        request.setService("WPS");
        request.setVersion(version);
        request.setIdentifier(new CodeType(identifier));
        request.setResponseForm(getRespForm());
        request.setDataInputs(getDataInputs());
        
        return request;
    }
    
    /**
     * Get all outputs
     * @param in
     * @return 
     */
    private ResponseFormType getRespForm(){
        final ResponseFormType responseForm = new ResponseFormType();
        
        if(outputForm.equalsIgnoreCase("document")){
            responseForm.setResponseDocument(getRespDocument());
        }else if(outputForm.equalsIgnoreCase("raw")){
            responseForm.setRawDataOutput(getRespRaw());
        }else{
            throw new IllegalArgumentException("Respons form musb be \"raw\" or \"document\"");
        }
        
        return responseForm;
    }
    
    /**
     * Get an Output response for a document type
     * @param in
     * @return 
     */
    private ResponseDocumentType getRespDocument (){
        
        final ResponseDocumentType docu = new ResponseDocumentType();
        docu.setLineage(lineage);
        docu.setStatus(status);
        docu.setStoreExecuteResponse(storage);
        
        for(WPSOutput output : outputs){
            docu.getOutput().add(getOutputDef(output));                
        }
        return docu;
    }
    
    /**
     * Get an Output response for a raw type
     * @param in
     * @return 
     */
    private OutputDefinitionType getRespRaw (){
        
        final WPSOutput output = outputs.get(0);
        final OutputDefinitionType raw = new OutputDefinitionType();
        raw.setIdentifier(new CodeType(output.getIdentifier()));
        raw.setSchema(output.getSchema());
        raw.setMimeType(output.getMime());
        raw.setUom(output.getUom());
        
        return raw;
    }
    
    /**
     * Get an Output definition
     * @param in
     * @return 
     */
    private DocumentOutputDefinitionType getOutputDef(WPSOutput output){
        
        final DocumentOutputDefinitionType outDef = new DocumentOutputDefinitionType();
        outDef.setIdentifier(new CodeType(output.getIdentifier()));
        outDef.setAsReference(output.getAsReference());
        outDef.setEncoding(output.getEncoding());
        outDef.setSchema(output.getSchema());
        outDef.setMimeType(output.getMime());
        outDef.setUom(output.getUom());
        
        return outDef;
    }

    /**
     * Get all intputs
     * @param in
     * @return 
     */
    private DataInputsType getDataInputs() {
        
        final DataInputsType input = new DataInputsType();
                        
        for(AbstractWPSInput in :inputs){
            if(in instanceof WPSInputBoundingBox){
                input.getInput().add(getInputBbox((WPSInputBoundingBox)in));
            }else if(in instanceof WPSInputComplex){
                input.getInput().add(getInputComplex((WPSInputComplex)in));
            }else if(in instanceof WPSInputLiteral){
                input.getInput().add(getInputLiteral((WPSInputLiteral)in));
            }else if(in instanceof WPSInputReference){
                input.getInput().add(getInputReference((WPSInputReference)in));
            }
        }
        return input;       
    }

    /**
     * Get an Input of type bounding box
     * @param in
     * @return 
     */
    private InputType getInputBbox(WPSInputBoundingBox in) {
        final InputType inputType = new InputType();
        
        final DataType data = new DataType();
        final BoundingBoxType bbox = new BoundingBoxType(in.getCrs(), in.getLowerCorner().get(0), 
                in.getLowerCorner().get(1), in.getUpperCorner().get(0), in.getUpperCorner().get(1));
        
        data.setBoundingBoxData(bbox);
        inputType.setData(data);
        inputType.setIdentifier(new CodeType(in.getIdentifier()));
        
        return inputType;
    }

    /**
     * Get an Input of type complex
     * @param in
     * @return 
     */
    private InputType getInputComplex(WPSInputComplex in) {
        
        final InputType inputType = new InputType();
        
        final DataType datatype = new DataType();
        final ComplexDataType complex = new ComplexDataType();
        complex.setEncoding(in.getEncoding());
        complex.setMimeType(in.getMime());
        complex.setSchema(in.getSchema());
        final Object inputData = in.getData();
        if(inputData instanceof Collection){
            complex.getContent().addAll((Collection)inputData);
        }else{
            complex.getContent().add(inputData);
        }
        datatype.setComplexData(complex);
        inputType.setData(datatype);
        inputType.setIdentifier(new CodeType(in.getIdentifier()));
        
        return inputType;
    }

    /**
     * Get an Input of type literal
     * @param in
     * @return 
     */
    private InputType getInputLiteral(WPSInputLiteral in) {
        final InputType inputType = new InputType();
        
        final DataType datatype = new DataType();
        final LiteralDataType literal = new LiteralDataType();
        literal.setDataType(in.getDataType());
        literal.setUom(in.getUom());
        literal.setValue(in.getData());
        
        datatype.setLiteralData(literal);
        inputType.setData(datatype);
        inputType.setIdentifier(new CodeType(in.getIdentifier()));
        
        return inputType;
    }

    /**
     * Get an Input of type reference
     * @param in
     * @return 
     */
    private InputType getInputReference(WPSInputReference in) {
        final InputType inputType = new InputType();
        
        final InputReferenceType ref= new InputReferenceType();
        ref.setHref(in.getHref());
        ref.setEncoding(in.getEncoding());
        ref.setMethod(in.getMethod());
        ref.setMimeType(in.getMime());
        ref.setSchema(in.getSchema());
        
        inputType.setReference(ref);
        inputType.setIdentifier(new CodeType(in.getIdentifier()));
        
        return inputType;
    }
}
