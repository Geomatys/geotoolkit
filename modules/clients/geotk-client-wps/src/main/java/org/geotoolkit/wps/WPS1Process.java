/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.CRS;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.ObjectConverter;
import org.apache.sis.util.ObjectConverters;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.ows.xml.ExceptionResponse;
import org.geotoolkit.ows.xml.ExceptionType;
import org.geotoolkit.ows.xml.v110.BoundingBoxType;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.utility.parameter.ExtendedParameterDescriptor;
import org.geotoolkit.wps.converters.WPSConvertersUtils;
import org.geotoolkit.wps.io.WPSIO;
import org.geotoolkit.wps.xml.WPSMarshallerPool;
import org.geotoolkit.wps.xml.v100.DataType;
import org.geotoolkit.wps.xml.v100.ExecuteResponse;
import org.geotoolkit.wps.xml.v100.InputType;
import org.geotoolkit.wps.xml.v100.LiteralDataType;
import org.geotoolkit.wps.xml.v100.OutputDataType;
import org.geotoolkit.wps.xml.v100.ProcessFailedType;
import org.geotoolkit.wps.xml.v100.ProcessStartedType;
import org.geotoolkit.wps.xml.v100.StatusType;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 * WPS v1.0.0 process.
 *
 * @author Johann Sorel (Geomatys)
 */
public class WPS1Process extends AbstractProcess {

    private final WPSProcessingRegistry registry;

    private boolean asReference = false;
    private boolean statusReport = false;

    //keep track of last progress state
    private Integer lastProgress;
    private String lastMessage;

    public WPS1Process(WPSProcessingRegistry registry, ProcessDescriptor desc, ParameterValueGroup params) {
        super(desc, params);
        this.registry = registry;
    }

    public void setAsReference(boolean asReference) {
        this.asReference = asReference;
    }

    public boolean isAsReference() {
        return asReference;
    }

    public void setStatusReport(boolean statusReport) {
        this.statusReport = statusReport;
    }

    public boolean isStatusReport() {
        return statusReport;
    }

    @Override
    protected void execute() throws ProcessException {
        final ExecuteRequest exec = createRequest();

        try {
            exec.getResponseStream();
        } catch (IOException ex) {
            Logger.getLogger(WPS1Process.class.getName()).log(Level.SEVERE, null, ex);
        }

        final ExecuteResponse response = sendExecuteRequest(exec, this);
        fillOutputs(outputParameters, getDescriptor(), response);
    }

    /**
     * Send the Execute request to the server URL an return the unmarshalled response.
     *
     * @param exec    the request
     * @param process process used for throw ProcessException
     * @return ExecuteResponse.
     * @throws ProcessException is can't reach the server or if there is an error during Marshalling/Unmarshalling request
     *                          or response.
     */
    private ExecuteResponse sendExecuteRequest(final ExecuteRequest req, final WPS1Process process) throws ProcessException {
        try {
            Object respObj = req.getResponse();
            respObj = checkResult(respObj);

            if (respObj instanceof ExecuteResponse) {
                final ExecuteResponse response = (ExecuteResponse) respObj;
                // Check if distant process has failed, in case we throw an exception.
                final ProcessFailedType processFailed = response.getStatus().getProcessFailed();
                if (processFailed != null) {
                    final StringBuilder errorText = new StringBuilder(process.getDescriptor().getIdentifier().getCode()+ " failed.");
                    final ExceptionResponse report = processFailed.getExceptionReport();
                    if (report != null) {
                        for (ExceptionType type : report.getException()) {
                            errorText.append('\n').append(type.getExceptionCode()).append(" : ");
                            for (String txt : type.getExceptionText()) {
                                errorText.append("\n\t").append(txt);
                            }
                        }
                    }
                    throw new ProcessException(errorText.toString(), process, null);

                } else {
                    return response;
                }

            } else if (respObj instanceof ExceptionResponse) {
                final ExceptionResponse report = (ExceptionResponse) respObj;
                final ExceptionType excep = report.getException().get(0);
                throw new ProcessException("Exception when executing the process.", process, new Exception(excep.toString()));
            }
            throw new ProcessException("Invalid response type.", process, null);

        } catch (ProcessException e) {
            throw e;
        } catch (JAXBException ex) {
            throw new ProcessException("Error when trying to parse the Execute response xml: ", process, ex);
        } catch (IOException ex) {
            throw new ProcessException("Error when trying to send request to the WPS server :", process, ex);
        } catch (Exception e) {
            throw new ProcessException(e.getMessage(), process, e);
        }
    }

    /**
     * A Function to ensure response object is success or failure. Otherwise, we request continually statusLocation until
     * we reach wanted result.
     * @param respObj The execute response given by service.
     */
    private Object checkResult(Object respObj) throws IOException, JAXBException, InterruptedException {

        if (respObj instanceof ExceptionResponse) {
            return respObj;

        } else if (respObj instanceof ExecuteResponse) {
            StatusType status = ((ExecuteResponse) respObj).getStatus();
            if (status.getProcessFailed() != null || status.getProcessSucceeded() != null) {
                return respObj;
            }
            registry.getClient().getLogger().log(Level.INFO, "Response object got, it's nor a succes nor a fail. Start querying statusLocation.");
            final Unmarshaller unmarshaller = WPSMarshallerPool.getInstance().acquireUnmarshaller();

            final ClientSecurity security = registry.getClient().getClientSecurity();
            final URL statusLocation = security.secure(new URL(((ExecuteResponse) respObj).getStatusLocation()));
            Object tmpResponse;
            int timeLapse = 3000;

            //we tolerate a few unmarshalling or IO errors, the servers behave differentely
            //and may not offer the result file right from the start
            int failCount = 0;
            while (true) {
                //timeLapse = Math.min(timeLapse * 2, maxTimeLapse);
                synchronized (this) {
                    wait(timeLapse);
                }

                try{
                    tmpResponse = unmarshaller.unmarshal(security.decrypt(statusLocation.openStream()));
                    if (tmpResponse instanceof JAXBElement) {
                        tmpResponse = ((JAXBElement) tmpResponse).getValue();
                    }
                    failCount = 0;
                }catch(UnmarshalException | IOException ex){
                    if(failCount<5){
                        failCount++;
                        continue;
                    }else{
                        //server seems to have a issue or can't provide status
                        //informations in any case we don't known what is
                        //happenning so we consider the process failed
                        throw ex;
                    }
                }

                if (tmpResponse instanceof ExecuteResponse) {
                    status = ((ExecuteResponse) tmpResponse).getStatus();
                    final ProcessStartedType processStarted = status.getProcessStarted();
                    if(processStarted!=null){
                        if( !Objects.equals(processStarted.getPercentCompleted(),lastProgress)
                           || !Objects.equals(processStarted.getValue(), lastMessage)){
                            fireProgressing(processStarted.getValue(), processStarted.getPercentCompleted(), false);
                            lastProgress = processStarted.getPercentCompleted();
                            lastMessage = processStarted.getValue();
                        }
                    }
                    if (status.getProcessFailed() != null || status.getProcessSucceeded() != null) {
                        respObj = tmpResponse;
                        break;
                    }
                } else if (tmpResponse instanceof ExceptionResponse) {
                    respObj = tmpResponse;
                    break;
                }
            }
        }

        return respObj;
    }



    /**
     * Fill {@link ParameterValueGroup parameters} of the process using the WPS
     * {@link ExecuteResponse response}.
     *
     * @param outputs
     * @param descriptor
     * @param response
     * @throws ProcessException if data conversion fails.
     */
    private void fillOutputs(final ParameterValueGroup outputs, final ProcessDescriptor descriptor, final ExecuteResponse response)
            throws ProcessException {
        ArgumentChecks.ensureNonNull("response", response);

        if (response.getProcessOutputs() != null) {

            final List<OutputDataType> wpsOutputs = response.getProcessOutputs().getOutput();

            registry.getClient().getLogger().log(Level.INFO, "Starting to parse output parameters. We found  {0} of them.", wpsOutputs.size());
            for (final OutputDataType output : wpsOutputs) {

                registry.getClient().getLogger().log(Level.INFO, "Parsing {0} output.", output.getIdentifier().getValue());
                final ParameterDescriptor outDesc = (ParameterDescriptor) descriptor.getOutputDescriptor().descriptor(output.getIdentifier().getValue());
                final Class clazz = outDesc.getValueClass();

                /*
                 * Reference
                 */
                if (output.getReference() != null) {

                    try {
                        outputs.parameter(output.getIdentifier().getValue()).setValue(WPSConvertersUtils.convertFromReference(output.getReference(), clazz));
                    } catch (UnconvertibleObjectException ex) {
                        throw new ProcessException(ex.getMessage(), null, ex);
                    }

                } else {
                    final DataType outputType = output.getData();

                    /*
                    * BBOX
                    */
                    if (outputType.getBoundingBoxData() != null) {
                        try {
                            final BoundingBoxType bbox = outputType.getBoundingBoxData();
                            final CoordinateReferenceSystem crs = CRS.forCode(bbox.getCrs());
                            final int dim = bbox.getDimensions();
                            final List<Double> lower = bbox.getLowerCorner();
                            final List<Double> upper = bbox.getUpperCorner();

                            final GeneralEnvelope envelope = new GeneralEnvelope(crs);
                            for (int i = 0; i < dim; i++) {
                                envelope.setRange(i, lower.get(i), upper.get(i));
                            }
                            outputs.parameter(output.getIdentifier().getValue()).setValue(envelope);

                        } catch (FactoryException ex) {
                            throw new ProcessException(ex.getMessage(), null, ex);
                        }

                   /*
                    * Complex
                    */
                    } else if (outputType.getComplexData() != null) {

                        try {
                            outputs.parameter(output.getIdentifier().getValue()).setValue(WPSConvertersUtils.convertFromComplex("1.0.0", outputType.getComplexData(), clazz));
                        } catch (UnconvertibleObjectException ex) {
                            throw new ProcessException(ex.getMessage(), null, ex);
                        }

                    /*
                    * Literal
                    */
                    } else if (outputType.getLiteralData() != null) {
                        try {
                            final LiteralDataType outputLiteral = outputType.getLiteralData();
                            final ObjectConverter converter = ObjectConverters.find(String.class, clazz);
                            outputs.parameter(output.getIdentifier().getValue()).setValue(converter.apply(outputLiteral.getValue()));
                        } catch (UnconvertibleObjectException ex) {
                            throw new ProcessException("Error during literal output conversion.", null, ex);
                        }
                    }
                }
            }
        }
    }

    /**
     * Make a WPS Execute request from {@link ParameterValueGroup values}.
     *
     * @param inputs
     * @param descriptor
     * @param inputTypes
     * @return
     * @throws ProcessException
     */
    private ExecuteRequest createRequest() throws ProcessException {

        try {
            final ParameterValueGroup inputs = getInput();
            final List<GeneralParameterDescriptor> inputParamDesc = inputs.getDescriptor().descriptors();
            final List<GeneralParameterDescriptor> outputParamDesc = descriptor.getOutputDescriptor().descriptors();

            final List<InputType> wpsIN = new ArrayList<>();
            final List<WPSOutput> wpsOUT = new ArrayList<>();

            final String processId = descriptor.getIdentifier().getCode();

            /*
             * INPUTS
             */
            for (final GeneralParameterDescriptor inputGeneDesc : inputParamDesc) {
                if (inputGeneDesc instanceof ParameterDescriptor) {
                    final ParameterDescriptor inputDesc = (ParameterDescriptor) inputGeneDesc;
                    final String type = (String) ((ExtendedParameterDescriptor)inputDesc)
                            .getUserObject().get(WPSProcessingRegistry.USE_FORM_KEY);

                    final String inputIdentifier = inputDesc.getName().getCode();
                    final Class inputClazz = inputDesc.getValueClass();
                    final Object value = inputs.parameter(inputIdentifier).getValue();
                    final String unit = inputDesc.getUnit() != null ? inputDesc.getUnit().toString() : null;

                    if ("literal".equals(type)) {
                        wpsIN.add(InputType.createLiteral(
                                inputIdentifier,
                                String.valueOf(value),
                                WPSConvertersUtils.getDataTypeString(registry.getClient().getVersion().getCode(), inputClazz),
                                unit));

                    } else if ("bbox".equals(type)) {
                        final Envelope envelop = (Envelope) value;
                        wpsIN.add(InputType.createBoundingBox(inputIdentifier, envelop));

                    } else if ("complex".equals(type)) {
                        String mime     = null;
                        String encoding = null;
                        String schema   = null;
                        if (inputGeneDesc instanceof ExtendedParameterDescriptor) {
                            final Map<String, Object> userMap = ((ExtendedParameterDescriptor) inputGeneDesc).getUserObject();
                            if(userMap.containsKey(WPSProcessingRegistry.USE_FORMAT_KEY)) {
                                final WPSIO.FormatSupport support = (WPSIO.FormatSupport) userMap.get(WPSProcessingRegistry.USE_FORMAT_KEY);
                                mime     = support.getMimeType();
                                encoding = support.getEncoding();
                                schema   = support.getSchema();
                            }
                        }

                        InputType.createComplex(inputIdentifier, encoding, mime, schema, value, null, null);
                    }
                }
            }

            /*
             * OUPTUTS
             */
            for (final GeneralParameterDescriptor outputGeneDesc : outputParamDesc) {
                if (outputGeneDesc instanceof ParameterDescriptor) {
                    final ParameterDescriptor outputDesc = (ParameterDescriptor) outputGeneDesc;

                    final String outputIdentifier = outputDesc.getName().getCode();
                    final Class outputClazz = outputDesc.getValueClass();
                    String mime     = null;
                    String encoding = null;
                    String schema   = null;
                    if (outputDesc instanceof ExtendedParameterDescriptor) {
                        final Map<String, Object> userMap = ((ExtendedParameterDescriptor) outputDesc).getUserObject();
                        if(userMap.containsKey(WPSProcessingRegistry.USE_FORMAT_KEY)) {
                            final WPSIO.FormatSupport support = (WPSIO.FormatSupport) userMap.get(WPSProcessingRegistry.USE_FORMAT_KEY);
                            mime     = support.getMimeType();
                            encoding = support.getEncoding();
                            schema   = support.getSchema();
                        }
                    }

                    wpsOUT.add(new WPSOutput(outputIdentifier, encoding, schema, mime, null, asReference));
                }
            }

            final ExecuteRequest request = registry.getClient().createExecute();
            final org.geotoolkit.wps.xml.v100.Execute execute = (org.geotoolkit.wps.xml.v100.Execute) request.getContent();
            execute.setIdentifier(processId);
            request.setInputs(wpsIN);
            request.setOutputs(wpsOUT);
            request.setStorageDirectory(registry.getStorageDirectory());
            request.setOutputStorage(asReference);
            execute.setResponseForm(request.getRespForm());
            execute.setDataInputs(request.getDataInputs());
            // Status can be activated only if we ask outputs as references.
            request.setOutputStatus(asReference && statusReport);
            request.setStorageURL(registry.getStorageURL());
            WPSProcessingRegistry.LOGGER.log(Level.INFO, "Execute request created for {0} in {1} mode.", new Object[]{processId, (asReference)? "asynchronous": "synchronous"});

            return request;

        } catch (UnconvertibleObjectException ex) {
            throw new ProcessException("Error during conversion step.", null, ex);
        }
    }

}
