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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.referencing.CRS;
import org.apache.sis.util.ObjectConverters;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.geometry.isoonjts.GeometryUtils;
import org.geotoolkit.ows.xml.BoundingBox;
import org.geotoolkit.ows.xml.ExceptionResponse;
import org.geotoolkit.ows.xml.v200.BoundingBoxType;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.utility.parameter.ExtendedParameterDescriptor;
import org.geotoolkit.utility.parameter.ParametersExt;
import org.geotoolkit.wps.converters.WPSConvertersUtils;
import org.geotoolkit.wps.io.WPSIO;
import org.geotoolkit.wps.xml.ExecuteResponse;
import org.geotoolkit.wps.xml.v200.ComplexDataType;
import org.geotoolkit.wps.xml.v200.Data;
import org.geotoolkit.wps.xml.v200.DataInputType;
import org.geotoolkit.wps.xml.v200.DataOutputType;
import org.geotoolkit.wps.xml.v200.Dismiss;
import org.geotoolkit.wps.xml.v200.LiteralValue;
import org.geotoolkit.wps.xml.v200.OutputDefinitionType;
import org.geotoolkit.wps.xml.v200.Result;
import org.geotoolkit.wps.xml.v200.StatusInfo;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 * WPS 2 process.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class WPS2Process extends AbstractProcess {

    private final WPSProcessingRegistry registry;

    private boolean asReference = false;
    private boolean statusReport = false;
    private final WPS2ProcessDescriptor desc;

    //keep track of last progress state
    private Integer lastProgress;
    private String lastMessage;
    private String jobId;

    /**
     * Create a new WPS process.
     *
     * @param registry WPS registry
     * @param desc process description
     * @param params input parameters
     */
    public WPS2Process(WPSProcessingRegistry registry, WPS2ProcessDescriptor desc, ParameterValueGroup params) {
        super(desc, params);
        this.desc = desc;
        this.registry = registry;
    }

    /**
     * Open a process which is already running on a WPS server.
     *
     * @param registry WPS registry
     * @param desc process description
     * @param jobId process running task identifier
     */
    public WPS2Process(WPSProcessingRegistry registry, WPS2ProcessDescriptor desc, String jobId) {
        super(desc);
        this.desc = desc;
        this.registry = registry;
        this.jobId = jobId;
    }

    /**
     * Returns the process execution identifier, called jobId.<br>
     * This value is available only after the process execution has started.
     *
     * @return job identifier, null before execution
     */
    public String getJobId() {
        return jobId;
    }

    /**
     * Get current task status.<br>
     *
     * This method should not be called if process is not running.
     *
     * @return StatusInfo
     * @throws org.geotoolkit.process.ProcessException
     * @throws javax.xml.bind.UnmarshalException
     * @throws java.io.IOException
     */
    public StatusInfo getStatus() throws ProcessException, JAXBException, IOException {
        if (jobId==null) throw new ProcessException("Process is not started.", this);

        final GetStatusRequest req = registry.getClient().createGetStatus();
        req.getContent().setJobID(jobId);
        final Object response = req.getResponse();

        if (response instanceof ExceptionResponse) {
            final ExceptionResponse report = (ExceptionResponse) response;
            throw new ProcessException("Exception when executing the process.", this, report.toException());

        } else if (response instanceof StatusInfo) {
            return (StatusInfo) response;

        } else {
            throw new ProcessException("Unexpected response "+response.getClass().getName(), this);
        }
    }

    /**
     * Request to stop the process.<br>
     * This request has no effect if the process has not start or is already finished or canceled.
     */
    @Override
    public void cancelProcess() {
        if(isCanceled() || jobId==null) return;
        super.cancelProcess();

        //send a stop request
        final DismissRequest request = registry.getClient().createDismiss();
        final Dismiss dismiss = request.getContent();
        dismiss.setJobID(jobId);

        try {
            checkResult(request.getResponse());
        } catch (JAXBException ex) {
            registry.getClient().getLogger().log(Level.WARNING, ex.getMessage(), ex);
        } catch (IOException ex) {
            registry.getClient().getLogger().log(Level.WARNING, ex.getMessage(), ex);
        } catch (InterruptedException ex) {
            registry.getClient().getLogger().log(Level.WARNING, ex.getMessage(), ex);
        } catch (ProcessException ex) {
            registry.getClient().getLogger().log(Level.WARNING, ex.getMessage(), ex);
        }
    }

    @Override
    protected void execute() throws ProcessException {
        final ExecuteRequest exec = createRequest();
        final Result result = sendExecuteRequest(exec);
        fillOutputs(result);
    }

    /**
     * Send the Execute request to the server URL an return the unmarshalled response.
     *
     * @param exec    the request
     * @return ExecuteResponse.
     * @throws ProcessException is can't reach the server or if there is an error during Marshalling/Unmarshalling request
     *                          or response.
     */
    private Result sendExecuteRequest(final ExecuteRequest req) throws ProcessException {
        try {
            return checkResult(req.getResponse());
        } catch (ProcessException e) {
            throw e;
        } catch (JAXBException ex) {
            throw new ProcessException("Error when trying to parse the Execute response xml: ", this, ex);
        } catch (IOException ex) {
            throw new ProcessException("Error when trying to send request to the WPS server :", this, ex);
        } catch (Exception e) {
            throw new ProcessException(e.getMessage(), this, e);
        }
    }

    /**
     * A Function to ensure response object is success or failure. Otherwise, we request continually status until
     * we reach a result.
     *
     * @param response The execute response given by service.
     */
    private Result checkResult(Object response) throws IOException, JAXBException, InterruptedException, ProcessException {

        if (response instanceof ExceptionResponse) {
            final ExceptionResponse report = (ExceptionResponse) response;
            throw new ProcessException("Exception when executing the process.", this, report.toException());

        } else if (response instanceof StatusInfo) {
            final StatusInfo statusInfo = (StatusInfo) response;
            final String status = statusInfo.getStatus();
            jobId = statusInfo.getJobID();

            if (StatusInfo.STATUS_SUCCEEDED.equalsIgnoreCase(status)) {
                fireProgressing("", 100f, false);
                return null;
            } else if (StatusInfo.STATUS_FAILED.equalsIgnoreCase(status)) {
                throw new ProcessException("Process failed", this);
            }

            //loop until we have an answer
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
                    tmpResponse = getStatus();
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

                if (tmpResponse instanceof StatusInfo) {
                    final StatusInfo statInfo = (StatusInfo) tmpResponse;
                    final String stat = statInfo.getStatus();

                    if (StatusInfo.STATUS_SUCCEEDED.equalsIgnoreCase(stat)) {
                        fireProgressing("", 100f, false);
                        return null;
                    } else if (StatusInfo.STATUS_FAILED.equalsIgnoreCase(stat)) {
                        throw new ProcessException("Process failed", this);
                    }

                    lastMessage = stat;
                    if (statInfo.getPercentCompleted()!=null) {
                        lastProgress = statInfo.getPercentCompleted();
                    }
                    fireProgressing(lastMessage, lastProgress, false);

                } else if (tmpResponse instanceof ExceptionResponse) {
                    final ExceptionResponse report = (ExceptionResponse) tmpResponse;
                    throw new ProcessException("Exception when executing the process.", this, report.toException());
                }
            }
        } else if (response instanceof Result) {
            return (Result) response;
        } else {
            throw new ProcessException("Unexpected response "+response,this);
        }
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
    private void fillOutputs(Object response) throws ProcessException {

        try {
            if (response==null) {
                //request the result from the server
                final GetResultRequest request = registry.getClient().createGetResult();
                request.getContent().setJobID(jobId);
                response = request.getResponse();
            }

            if (response instanceof Result) {
                final Result result = (Result) response;
                for (DataOutputType out : result.getOutput()) {
                    final GeneralParameterDescriptor desc = outputParameters.getDescriptor().descriptor(out.getId());
                    final Data data = out.getData();
                    if (data!=null) {
                        final LiteralValue literalData = data.getLiteralData();
                        final BoundingBox bbox = data.getBoundingBoxData();
                        final ComplexDataType complex = data.getComplexData();

                        if (literalData!=null) {
                            final String value = literalData.getValue();
                            final Object cvalue = ObjectConverters.convert(value, ((ParameterDescriptor)desc).getValueClass());
                            ParametersExt.getOrCreateValue(outputParameters, out.getId()).setValue(cvalue);

                        } else if (bbox!=null) {
                            final List<Double> lower = bbox.getLowerCorner();
                            final List<Double> upper = bbox.getUpperCorner();
                            final String crsName = bbox.getCrs();
                            final int dimension = bbox.getDimensions();

                            //Check if it's a 2D boundingbox
                            if (dimension != 2 || lower.size() != 2 || upper.size() != 2) {
                                throw new ProcessException("Invalid data input : Only 2 dimension boundingbox supported.", this);
                            }

                            final CoordinateReferenceSystem crs;
                            try {
                                crs = CRS.forCode(crsName);
                            } catch (FactoryException ex) {
                                throw new ProcessException("Invalid data input : CRS not supported.", this, ex);
                            }

                            final Envelope cenv = GeometryUtils.createCRSEnvelope(crs, lower.get(0), lower.get(1), upper.get(0), upper.get(1));
                            ParametersExt.getOrCreateValue(outputParameters, out.getId()).setValue(cenv);
                        } else {
                            throw new UnsupportedOperationException("unsupported data type");
                        }

                    } else {
                        throw new UnsupportedOperationException("unsupported data type");
                    }
                }

            } else if (response instanceof ExceptionResponse) {
                final ExceptionResponse report = (ExceptionResponse) response;
                throw new ProcessException("Exception when getting process result.", this, report.toException());
            }

        } catch (JAXBException ex) {
            Logger.getLogger(WPS2Process.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WPS2Process.class.getName()).log(Level.SEVERE, null, ex);
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

            final List<DataInputType> wpsIN = new ArrayList<>();
            final List<OutputDefinitionType> wpsOUT = new ArrayList<>();

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
                    final Object value = Parameters.castOrWrap(inputParameters).getValue(inputDesc);
                    if (value==null) continue;
                    final String unit = inputDesc.getUnit() != null ? inputDesc.getUnit().toString() : null;

                    if ("literal".equals(type)) {
                        final LiteralValue litValue = new LiteralValue();
                        litValue.setDataType(WPSConvertersUtils.getDataTypeString(registry.getClient().getVersion().getCode(), inputClazz));
                        litValue.setValue(String.valueOf(value));
                        litValue.setUom(unit);

                        final Data data = new Data();
                        data.getContent().add(litValue);

                        final DataInputType dit = new DataInputType();
                        dit.setId(inputIdentifier);
                        dit.setData(data);

                        wpsIN.add(dit);

                    } else if ("bbox".equals(type)) {
                        final Envelope envelop = (Envelope) value;

                        final BoundingBoxType litValue = new BoundingBoxType(envelop);
                        final Data data = new Data();
                        data.getContent().add(litValue);

                        final DataInputType dit = new DataInputType();
                        dit.setId(inputIdentifier);
                        dit.setData(data);

                        wpsIN.add(dit);

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

                        final ComplexDataType cdt = new ComplexDataType();
                        throw new UnsupportedOperationException("Complex type not supported.");
                        //wpsIN.add(new WPSInputComplex(inputIdentifier, value, inputClazz, encoding, schema, mime));
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

                    final OutputDefinitionType out = new OutputDefinitionType(outputIdentifier, asReference);
//                    out.setEncoding(encoding);
//                    out.setMimeType(mime);
//                    out.setSchema(schema);
                    wpsOUT.add(out);
                }
            }

            final ExecuteRequest request = registry.getClient().createExecute();
            final org.geotoolkit.wps.xml.v200.ExecuteRequestType execute = (org.geotoolkit.wps.xml.v200.ExecuteRequestType) request.getContent();
            execute.setIdentifier(processId);
            //execute.setMode("async");
            execute.setMode("auto");
            execute.setResponse("document");
            execute.getInput().addAll(wpsIN);
            execute.getOutput().addAll(wpsOUT);
            request.setStorageDirectory(registry.getStorageDirectory());
            request.setOutputStorage(asReference);
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
