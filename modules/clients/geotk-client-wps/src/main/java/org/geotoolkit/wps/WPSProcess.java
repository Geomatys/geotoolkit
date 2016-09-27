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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import org.geotoolkit.ows.xml.v110.ExceptionReport;
import org.geotoolkit.ows.xml.v110.ExceptionType;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.wps.xml.WPSMarshallerPool;
import org.geotoolkit.wps.xml.v100.Execute;
import org.geotoolkit.wps.xml.v100.ExecuteResponse;
import org.geotoolkit.wps.xml.v100.ProcessFailedType;
import org.geotoolkit.wps.xml.v100.ProcessStartedType;
import org.geotoolkit.wps.xml.v100.StatusType;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class WPSProcess extends AbstractProcess {

    private final Map<String,String> inputTypes;
    private final WebProcessingClient client;

    //keep track of last progress state
    private Integer lastProgress;
    private String lastMessage;

    public WPSProcess(WebProcessingClient client, ProcessDescriptor desc, Map<String,String> inputTypes, ParameterValueGroup params) {
        super(desc, params);
        this.client = client;
        this.inputTypes = inputTypes;
    }

    @Override
    protected void execute() throws ProcessException {
        final Execute exec = client.createRequest(getInput(), getDescriptor(), inputTypes);
        final ExecuteResponse response = sendExecuteRequest(exec, this);
        client.fillOutputs(outputParameters, getDescriptor(), response);
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
    private ExecuteResponse sendExecuteRequest(final Execute exec, final WPSProcess process) throws ProcessException {
        try {
            Object respObj = client.sendSecuredRequestInPost(exec);
            respObj = checkResult(respObj);

            if (respObj instanceof ExecuteResponse) {
                final ExecuteResponse response = (ExecuteResponse) respObj;
                // Check if distant process has failed, in case we throw an exception.
                final ProcessFailedType processFailed = response.getStatus().getProcessFailed();
                if (processFailed != null) {
                    final StringBuilder errorText = new StringBuilder(process.getDescriptor().getIdentifier().getCode()+ " failed.");
                    final ExceptionReport report = processFailed.getExceptionReport();
                    if (report != null) {
                        final List<ExceptionType> exceptionTypes = report.getException();
                        for (ExceptionType type : exceptionTypes) {
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

            } else if (respObj instanceof ExceptionReport) {
                final ExceptionReport report = (ExceptionReport) respObj;
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

        if (respObj instanceof ExceptionReport) {
            return respObj;

        } else if (respObj instanceof ExecuteResponse) {
            StatusType status = ((ExecuteResponse) respObj).getStatus();
            if (status.getProcessFailed() != null || status.getProcessSucceeded() != null) {
                return respObj;
            }
            client.getLogger().log(Level.INFO, "Response object got, it's nor a succes nor a fail. Start querying statusLocation.");
            final Unmarshaller unmarshaller = WPSMarshallerPool.getInstance().acquireUnmarshaller();

            final ClientSecurity security = client.getClientSecurity();
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
                } else if (tmpResponse instanceof ExceptionReport) {
                    respObj = tmpResponse;
                    break;
                }
            }
        }

        return respObj;
    }


}
