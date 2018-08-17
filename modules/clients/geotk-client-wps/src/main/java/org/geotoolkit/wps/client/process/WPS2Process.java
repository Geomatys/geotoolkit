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
package org.geotoolkit.wps.client.process;

import org.geotoolkit.wps.client.GetStatusRequest;
import org.geotoolkit.wps.client.GetResultRequest;
import org.geotoolkit.wps.client.DismissRequest;
import org.geotoolkit.wps.client.ExecuteRequest;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.ows.xml.ExceptionResponse;
import org.geotoolkit.ows.xml.v200.ExceptionReport;
import org.geotoolkit.process.DismissProcessException;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.utility.parameter.ExtendedParameterDescriptor;
import org.geotoolkit.wps.adaptor.ComplexAdaptor;
import org.geotoolkit.wps.adaptor.DataAdaptor;
import org.geotoolkit.wps.adaptor.LiteralAdaptor;
import org.geotoolkit.wps.xml.WPSMarshallerPool;
import org.geotoolkit.wps.xml.WPSUtilities;
import org.geotoolkit.wps.xml.v100.LegacyStatus;
import org.geotoolkit.wps.xml.v100.ProcessFailed;
import org.geotoolkit.wps.xml.v200.DataInput;
import org.geotoolkit.wps.xml.v200.DataOutput;
import org.geotoolkit.wps.xml.v200.Execute;
import org.geotoolkit.wps.xml.v200.OutputDefinition;
import org.geotoolkit.wps.xml.v200.ProcessOffering;
import org.geotoolkit.wps.xml.v200.Result;
import org.geotoolkit.wps.xml.v200.Status;
import org.geotoolkit.wps.xml.v200.StatusInfo;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;

/**
 * WPS 2 process.
 *
 * TODO : allow to define transmission mode individualy for each output.
 * TODO : simplify by creating a better level of abstraction. I.E : We should
 * introduce new objects for pure WPS execution tracing, with listeners for status
 * updates and a completable future for the final Result. Once done, all this
 * process adapter have to do is listen to status changes and wait for the final
 * result, to fill outputs. Such amelioration would lead to lighter classes,
 * code duplication reduction, better responsability separation and code re-use.
 * For now, we cannot have a pure WPS client without the process abstraction,
 * which is overkill (harder to maintain, more bugs and less performances due to
 * overhead code and java type conversions, etc.).
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
class WPS2Process extends AbstractProcess {

    private final WPSProcessingRegistry registry;

    private ClientSecurity security;
    private boolean asReference    = false;
    private boolean rawLiteralData = false;
    /**
     * Indicates if process output should be raw (not decorated in a WPS document).
     */
    private boolean rawOutput = false;
    private boolean debug          = false;

    /**
     * Specify if WPS execution should be done in synchronous or asynchronous mode.
     */
    private Execute.Mode executionMode;

    private final WPS2ProcessDescriptor desc;

    //keep track of last progress state
    private Integer lastProgress = 0;
    private String lastMessage;
    private volatile String jobId;

    /**
     * Create a new WPS process.
     *
     * @param registry WPS registry
     * @param desc process description
     * @param params input parameters
     */
    public WPS2Process(WPSProcessingRegistry registry, WPS2ProcessDescriptor desc, ParameterValueGroup params) {
        super(desc, params);
        this.security = registry.getClient().getClientSecurity();
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
     * Get client securing object.
     * The default security is the one from the WebProcessingClient.
     *
     * @return ClientSecurity, never null.
     */
    public ClientSecurity getClientSecurity() {
        return security;
    }

    /**
     * Set client securing object.
     *
     * @param security not null
     */
    public void setClientSecurity(ClientSecurity security) {
        ArgumentChecks.ensureNonNull("security", security);
        this.security = security;
    }

    public void setRawLiteralData(boolean rawLiteralData) {
        this.rawLiteralData = rawLiteralData;
    }

    @Deprecated
    public void setAsync(boolean async) {
        setExecutionMode(async? Execute.Mode.async : Execute.Mode.sync);
    }

    public void setExecutionMode(final Execute.Mode executionMode) {
        final ProcessOffering offering = desc.getOffering();
        boolean isCompatible = WPSUtilities.testCompatibility(offering, executionMode);
        if (isCompatible) {
            this.executionMode = executionMode;
        }

        throw new IllegalArgumentException(String.format(
                "%s mode is not compatible with available job control options: %s",
                executionMode, Arrays.toString(offering.getJobControlOptions().toArray())
        ));
    }

    public Execute.Mode getExecutionMode() {
        return executionMode;
    }

    public boolean isAsReference() {
        return asReference;
    }

    public void setAsReference(boolean asReference) {
        this.asReference = asReference;
    }

    public boolean isRawOutput() {
        return rawOutput;
    }

    public void setRawOutput(boolean rawOutput) {
        this.rawOutput = rawOutput;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
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
    private StatusInfo getStatus() throws ProcessException, JAXBException, IOException {
        if (jobId==null) throw new ProcessException("Process is not started.", this);

        if (isCanceled()) {
            throw new DismissProcessException("Process already dismissed", this);
        }

        final GetStatusRequest req = registry.getClient().createGetStatus(jobId);
        req.setDebug(debug);
        req.setClientSecurity(security);
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
        if (isCanceled() || jobId == null)
            return;
        super.cancelProcess();

        //send a stop request
        final DismissRequest request = registry.getClient().createDismiss(jobId);
        request.setDebug(debug);
        request.setClientSecurity(security);

        try {
            checkResult(request.getResponse());
        } catch (JAXBException ex) {
            registry.getClient().getLogger().log(Level.WARNING, ex.getMessage(), ex);
        } catch (IOException ex) {
            registry.getClient().getLogger().log(Level.WARNING, ex.getMessage(), ex);
        } catch (InterruptedException ex) {
            registry.getClient().getLogger().log(Level.WARNING, ex.getMessage(), ex);
        } catch (DismissProcessException ex) {
            //do nothing, normal behaviour
        } catch (ProcessException ex) {
            registry.getClient().getLogger().log(Level.WARNING, ex.getMessage(), ex);
        }
    }

    @Override
    protected void execute() throws ProcessException {
        final Result result;
        try {
            if (jobId != null) {
                try {
                    // It's an already running process we've got here. All we have to do
                    // is checking its status, to ensure/wait its completion.
                    result = checkResult(getStatus());
                } catch (JAXBException | IOException ex) {
                    throw new ProcessException("Cannot get process status", this, ex);
                }
            } else {
                final ExecuteRequest exec = createRequest();
                exec.setDebug(debug);
                exec.setClientSecurity(security);
                result = sendExecuteRequest(exec);
            }
        } catch (InterruptedException e) {
            throw new DismissProcessException("Process interrupted while executing", this, e);
        }

        if (!isCanceled()) {
            fillOutputs(result);
        }
    }

    /**
     * Send the Execute request to the server URL an return the unmarshalled response.
     *
     * @param exec    the request
     * @return ExecuteResponse.
     * @throws ProcessException is can't reach the server or if there is an error during Marshalling/Unmarshalling request
     *                          or response.
     */
    private Result sendExecuteRequest(final ExecuteRequest req) throws ProcessException, InterruptedException {
        try {
            return checkResult(req.getResponse());
        } catch (ProcessException e) {
            throw e;
        } catch (JAXBException ex) {
            throw new ProcessException("Error when trying to parse the Execute response xml: ", this, ex);
        } catch (IOException ex) {
            throw new ProcessException("Error when trying to send request to the WPS server :", this, ex);
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
            Status status = statusInfo.getStatus();
            jobId = statusInfo.getJobID();

            if (Status.SUCCEEDED.equals(status)) {
                fireProgressing("WPS remote process has been successfully executed", 100f, false);
                return null;
            } else if (Status.FAILED.equals(status)) {
                throw new ProcessException("Process failed", this);
            } else if (Status.DISMISS.equals(status)) {
                throw new DismissProcessException("WPS remote process has been canceled",this);
            } else if (Status.ACCEPTED.equals(status)) {
                // Initial status
                fireProgressing("Process accepted: "+jobId, 0, false);
            } else {
                // Running
                final Integer progress = statusInfo.getPercentCompleted();
                String message = statusInfo.getMessage(); // Not in the standard
                if (message == null || (message = message.trim()).isEmpty()) {
                    message = status.name();
                }
                fireProgressing(message, progress == null? Float.NaN : progress, false);
            }

            //loop until we have an answer
            Object tmpResponse;
            int timeLapse = 3000; // TODO : make timelapse configurable

            //we tolerate a few unmarshalling or IO errors, the servers behave differentely
            //and may not offer the result file right from the start
            int failCount = 0;
            while (true) {
                if (isCanceled()) {
                    throw new DismissProcessException("Process cancelled", this);
                }
                synchronized (this) {
                    wait(timeLapse);
                }

                try{
                    tmpResponse = getStatus();
                    failCount = 0;
                }catch(UnmarshalException | IOException ex){
                    if(failCount<5 && !isCanceled()){
                        failCount++;
                        continue;
                    } else if (isCanceled()) {
                        throw new DismissProcessException("WPS remote process has been canceled",this);
                    } else {
                        //server seems to have a issue or can't provide status
                        //informations in any case we don't known what is
                        //happenning so we consider the process failed
                        throw ex;
                    }
                }

                if (tmpResponse instanceof StatusInfo) {
                    final StatusInfo statInfo = (StatusInfo) tmpResponse;
                    status = statInfo.getStatus();

                    if (Status.SUCCEEDED.equals(status)) {
                        fireProgressing("WPS remote process has been successfully executed", 100f, false);
                        return null;
                    } else if (Status.FAILED.equals(status)) {
                        throw new ProcessException("Process failed", this);
                    } else if (Status.DISMISS.equals(status)) {
                        throw new DismissProcessException("WPS remote process has been canceled", this);
                    }

                    String message = statusInfo.getMessage(); // Not in the standard
                    if (message == null || (message = message.trim()).isEmpty()) {
                        message = status.name();
                    }

                    final Integer percentCompleted = statInfo.getPercentCompleted();
                    if (!Objects.equals(message, lastMessage) || !Objects.equals(percentCompleted, lastProgress)) {
                        lastMessage = message;
                        lastProgress = percentCompleted;
                        fireProgressing(lastMessage, lastProgress, false);
                    }

                } else if (tmpResponse instanceof ExceptionResponse) {
                    final ExceptionResponse report = (ExceptionResponse) tmpResponse;
                    throw new ProcessException("Exception when executing the process.", this, report.toException());
                }
            }
        } else if (response instanceof Result) {
            final Result result = checkLegacyResult((Result)response);
            if (result.getJobID() != null) {
                jobId = result.getJobID();
            }
            return result;
        } else {
            throw new ProcessException("Unexpected response "+response,this);
        }
    }

    private Result checkLegacyResult(Result r) throws ProcessException, JAXBException, IOException, InterruptedException {
        LegacyStatus legacyStatus = r.getStatus();
        if (legacyStatus == null) {
            return r;
        }

        // WPS 1 case
        ProcessFailed failure = legacyStatus.getProcessFailed();
        if (failure != null) {
            ExceptionReport error = failure.getExceptionReport();
            throw new ProcessException(
                    "Exception when executing the process.", this,
                    error != null ? error.toException() : new RuntimeException("No exception report attached")
            );
        }

        if (legacyStatus.getProcessSucceeded() != null) {
            return r;
        }

        final Integer currentProgress = legacyStatus.getPercentCompleted();
        final String currentMessage = legacyStatus.getMessage();
        if (!Objects.equals(lastProgress, currentProgress) || !Objects.equals(lastMessage, currentMessage)) {
            lastProgress = currentProgress;
            lastMessage = currentMessage;
            fireProgressing(lastMessage, lastProgress, false);
        }

        registry.getClient().getLogger().log(Level.INFO, "WPS 1 Response is neither a succes nor a fail. Start querying statusLocation.");
        legacyStatus.getPercentCompleted();
        final Unmarshaller unmarshaller = WPSMarshallerPool.getInstance().acquireUnmarshaller();
        String brutLocation = r.getStatusLocation();
        if (brutLocation == null || (brutLocation = brutLocation.trim()).isEmpty()) {
            throw new ProcessException("Received response is neither a success nor a fail, but no status location can be found.", this);
        }
        final URL statusLocation = security.secure(new URL(brutLocation));
        Object tmpResponse;
        int timeLapse = 3000; // TODO: make configurable

        //we tolerate a few unmarshalling or IO errors, the servers behave differentely
        //and may not offer the result file right from the start
        int failCount = 0;
        while (true) {
            synchronized (this) {
                wait(timeLapse);
            }

            try {
                tmpResponse = unmarshaller.unmarshal(security.decrypt(statusLocation.openStream()));
                if (tmpResponse instanceof JAXBElement) {
                    tmpResponse = ((JAXBElement) tmpResponse).getValue();
                }

                return checkResult(tmpResponse);
            } catch (UnmarshalException | IOException ex) {
                if (failCount < 5) {
                    failCount++;
                } else {
                    //server seems to have a issue or can't provide status
                    //informations in any case we don't known what is
                    //happenning so we consider the process failed
                    throw ex;
                }
            }
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
                final GetResultRequest request = registry.getClient().createGetResult(jobId);
                request.setDebug(debug);
                request.setClientSecurity(security);
                response = request.getResponse();
            }

            if (response instanceof Result) {
                final Result result = (Result) response;
                for (DataOutput out : result.getOutput()) {
                    fillOutputs(outputParameters, out);
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

    private static void fillOutputs(Parameters outParams, DataOutput out) {
        if (out != null) {
            final GeneralParameterDescriptor param = outParams.getDescriptor().descriptor(out.getId());

            if (param instanceof ParameterDescriptorGroup) {
                //expecting a complex output
                final Parameters group = Parameters.castOrWrap(outParams.addGroup(out.getId()));
                for (DataOutput output : out.getOutput()) {
                    fillOutputs(group, output);
                }
            } else {
                //simple output
                final ExtendedParameterDescriptor outDesc = (ExtendedParameterDescriptor) outParams.getDescriptor().descriptor(out.getId());
                final DataAdaptor adaptor = (DataAdaptor) outDesc.getUserObject().get(DataAdaptor.USE_ADAPTOR);
                final Object value = adaptor.fromWPS2Input(out);
                outParams.getOrCreate(outDesc).setValue(value);
            }

        } else {
            throw new UnsupportedOperationException("unsupported data type");
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

            final List<DataInput> wpsIN = new ArrayList<>();
            final List<OutputDefinition> wpsOUT = new ArrayList<>();

            final String processId = descriptor.getIdentifier().getCode();

            /*
             * INPUTS
             */

            for (final GeneralParameterValue inputValue : inputs.values()) {
                GeneralParameterDescriptor inputGeneDesc = inputValue.getDescriptor();
                if (inputGeneDesc instanceof ParameterDescriptor) {
                    final ParameterDescriptor inputDesc = (ParameterDescriptor) inputGeneDesc;
                    final DataAdaptor adaptor = (DataAdaptor) ((ExtendedParameterDescriptor)inputDesc).getUserObject().get(DataAdaptor.USE_ADAPTOR);

                    final Object value = ((ParameterValue)inputValue).getValue();
                    if (value==null) continue;

                    final DataInput dataInput;
                    if (adaptor instanceof LiteralAdaptor) {
                        dataInput = ((LiteralAdaptor)adaptor).toWPS2Input(value, rawLiteralData);
                    } else {
                        dataInput = adaptor.toWPS2Input(value);
                    }
                    dataInput.setId(inputDesc.getName().getCode());
                    wpsIN.add(dataInput);
                }
            }

            /*
             * OUTPUTS
             */
            for (final GeneralParameterDescriptor outputGeneDesc : outputParamDesc) {
                if (outputGeneDesc instanceof ParameterDescriptor) {
                    final ParameterDescriptor outputDesc = (ParameterDescriptor) outputGeneDesc;
                    final DataAdaptor adaptor = (DataAdaptor) ((ExtendedParameterDescriptor)outputDesc).getUserObject().get(DataAdaptor.USE_ADAPTOR);

                    final String outputIdentifier = outputDesc.getName().getCode();
                    String mime     = null;
                    String encoding = null;
                    String schema   = null;
                    if (adaptor instanceof ComplexAdaptor) {
                        final ComplexAdaptor cadaptor = (ComplexAdaptor) adaptor;
                        mime     = cadaptor.getMimeType();
                        encoding = cadaptor.getEncoding();
                        schema   = cadaptor.getSchema();
                    }

                    final OutputDefinition out = new OutputDefinition(outputIdentifier, asReference);
                    out.setEncoding(encoding);
                    out.setMimeType(mime);
                    out.setSchema(schema);
                    wpsOUT.add(out);
                } else if(outputGeneDesc instanceof ParameterDescriptorGroup) {
                    final ParameterDescriptorGroup outputDesc = (ParameterDescriptorGroup) outputGeneDesc;
                    final OutputDefinition out = new OutputDefinition(outputDesc.getName().getCode(), asReference);
                    wpsOUT.add(out);
                }
            }

            final ExecuteRequest request = registry.getClient().createExecute();
            request.setClientSecurity(security);
            final Execute execute = request.getContent();
            execute.setIdentifier(processId);
            final Execute.Mode mode = executionMode == null? Execute.Mode.auto : executionMode;
            execute.setMode(mode);
            execute.setResponse(rawOutput? Execute.Response.raw : Execute.Response.document);
            execute.getInput().addAll(wpsIN);
            execute.getOutput().addAll(wpsOUT);

            WPSProcessingRegistry.LOGGER.log(Level.INFO, "Execute request created for {0} in {1} mode.", new Object[]{processId, mode});

            return request;

        } catch (UnconvertibleObjectException ex) {
            throw new ProcessException("Error during conversion step.", null, ex);
        }
    }
}
