/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.ogcapi.storage;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.IdentifiedObjects;
import org.apache.sis.util.ObjectConverters;
import org.apache.sis.util.UnconvertibleObjectException;
import org.apache.sis.util.Utilities;
import org.geotoolkit.client.service.ServiceException;
import org.geotoolkit.ogcapi.client.processes.ProcessesApi;
import org.geotoolkit.ogcapi.dto.processes.Execute;
import org.geotoolkit.ogcapi.dto.processes.JobControlOption;
import org.geotoolkit.ogcapi.dto.processes.OutputSelection;
import org.geotoolkit.ogcapi.dto.processes.ProcessDescription;
import org.geotoolkit.ogcapi.dto.processes.ResponseType;
import org.geotoolkit.ogcapi.dto.processes.StatusCode;
import org.geotoolkit.ogcapi.dto.processes.StatusInfo;
import org.geotoolkit.ogcapi.dto.processes.TransmissionMode;
import org.geotoolkit.ogcapi.request.processes.ExecuteProcess;
import org.geotoolkit.ogcapi.request.processes.GetJob;
import org.geotoolkit.process.DismissProcessException;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcess;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 * Process executed on an OGC API - Processes service.
 *
 * @author Johann Sorel (Geomatys)
 */
final class OgcApiProcess extends AbstractProcess {

    /**
     * Delay between two job status requests, in milliseconds.
     */
    private static final long POLL_INTERVAL = 1000;

    private final OgcApiProcessingRegistry registry;
    private final OgcApiProcessDescriptor desc;

    OgcApiProcess(OgcApiProcessingRegistry registry, OgcApiProcessDescriptor desc, ParameterValueGroup input) {
        super(desc, input);
        this.registry = registry;
        this.desc = desc;
    }

    @Override
    protected void execute() throws ProcessException {
        final ProcessesApi api = registry.getApi();
        final ExecuteProcess request = new ExecuteProcess()
                .processId(desc.getIdentifier().getCode())
                .body(toExecute());

        final Map<String, Object> results;
        try {
            if (isAsync()) {
                results = waitForResults(api, api.executeAsync(request).send().body().get());
            } else {
                results = api.executeSync(request).send().body().get();
            }
        } catch (ServiceException ex) {
            throw new ProcessException("Remote process execution failed : " + ex.getMessage(), this, ex);
        }

        if (!isDimissed()) {
            fillOutputs(results);
        }
    }

    /**
     * Prefer asynchronous execution when supported, it allows progress monitoring and dismiss.
     */
    private boolean isAsync() {
        final List<JobControlOption> options = desc.getDescription().getJobControlOptions();
        return options != null && options.contains(JobControlOption.ASYNC_EXECUTE);
    }

    /**
     * Poll the job status until it ends.
     *
     * @return job results
     */
    private Map<String, Object> waitForResults(ProcessesApi api, StatusInfo status) throws ServiceException, ProcessException {
        if (status == null || status.getJobID() == null) {
            throw new ProcessException("Server did not return a job identifier", this);
        }
        jobId = status.getJobID();
        final GetJob job = new GetJob().jobId(jobId);

        String lastMessage = null;
        Integer lastProgress = null;
        while (true) {
            final StatusCode code = status.getStatus();
            String message = status.getMessage();
            if (message == null || (message = message.trim()).isEmpty()) {
                message = String.valueOf(code);
            }

            if (code == StatusCode.SUCCESSFUL) {
                fireProgressing("Remote process has been successfully executed", 100f, false);
                return api.getJobResults(job).send().body().get();
            } else if (code == StatusCode.FAILED) {
                throw new ProcessException("Remote process failed : " + message, this);
            } else if (code == StatusCode.DISMISSED) {
                throw new DismissProcessException("Remote process has been dismissed", this);
            }

            final Integer progress = status.getProgress();
            if (!Objects.equals(message, lastMessage) || !Objects.equals(progress, lastProgress)) {
                lastMessage = message;
                lastProgress = progress;
                fireProgressing(message, progress == null ? Float.NaN : progress, false);
            }

            stopIfDismissed();
            try {
                synchronized (this) {
                    wait(POLL_INTERVAL);
                }
            } catch (InterruptedException ex) {
                throw new DismissProcessException("Process interrupted while executing", this, ex);
            }
            stopIfDismissed();
            status = api.getJob(job).send().body().get();
        }
    }

    /**
     * Request to stop the process, the remote job is dismissed if it has been created.
     */
    @Override
    public void dismissProcess() {
        if (isDimissed()) return;
        super.dismissProcess();
        synchronized (this) {
            notifyAll();
        }

        final String id = jobId;
        if (id != null) {
            try {
                registry.getApi().dismissJob(new GetJob().jobId(id)).send();
            } catch (ServiceException ex) {
                OgcApiProcessingRegistry.LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
    }

    /**
     * Create the execution request body from the input parameters.
     */
    Execute toExecute() throws ProcessException {
        final Execute execute = new Execute()
                .inputs(new LinkedHashMap<>())
                .outputs(new LinkedHashMap<>())
                .response(ResponseType.DOCUMENT);

        // group values by input, an input may have several occurrences
        final Map<String, List<Object>> values = new LinkedHashMap<>();
        for (GeneralParameterValue param : inputParameters.values()) {
            if (param instanceof ParameterValue<?> pv && pv.getValue() != null) {
                values.computeIfAbsent(pv.getDescriptor().getName().getCode(), (k) -> new ArrayList<>())
                      .add(toJson(pv.getValue()));
            }
        }
        for (Map.Entry<String, List<Object>> entry : values.entrySet()) {
            final GeneralParameterDescriptor pd = inputParameters.getDescriptor().descriptor(entry.getKey());
            final List<Object> occurrences = entry.getValue();
            execute.putInputsItem(entry.getKey(), pd.getMaximumOccurs() > 1 ? occurrences : occurrences.get(0));
        }

        final ProcessDescription description = desc.getDescription();
        final TransmissionMode mode = (description.getOutputTransmission() == null
                || description.getOutputTransmission().contains(TransmissionMode.VALUE)) ? TransmissionMode.VALUE : null;
        for (GeneralParameterDescriptor pd : descriptor.getOutputDescriptor().descriptors()) {
            execute.putOutputsItem(pd.getName().getCode(), new OutputSelection().transmissionMode(mode));
        }
        return execute;
    }

    /**
     * Fill output parameters from the results document, keyed by output identifier.
     *
     * @return output parameters
     */
    ParameterValueGroup fillOutputs(Map<String, Object> results) throws ProcessException {
        if (results == null) return outputParameters;
        for (GeneralParameterDescriptor gd : outputParameters.getDescriptor().descriptors()) {
            if (gd instanceof ParameterDescriptor<?> pd) {
                final String name = pd.getName().getCode();
                if (results.containsKey(name)) {
                    final Object value = convert(unwrap(results.get(name)), pd.getValueClass());
                    outputParameters.getOrCreate(pd).setValue(value);
                }
            }
        }
        return outputParameters;
    }

    /**
     * Convert a Java value to its JSON representation.
     */
    private Object toJson(Object value) throws ProcessException {
        if (value instanceof Envelope env) {
            final int dim = env.getDimension();
            final List<Double> bbox = new ArrayList<>(dim * 2);
            for (int i = 0; i < dim; i++) bbox.add(env.getMinimum(i));
            for (int i = 0; i < dim; i++) bbox.add(env.getMaximum(i));
            final Map<String, Object> json = new LinkedHashMap<>();
            json.put("bbox", bbox);
            final CoordinateReferenceSystem crs = env.getCoordinateReferenceSystem();
            if (crs != null && !Utilities.equalsIgnoreMetadata(crs, CommonCRS.defaultGeographic())) {
                json.put("crs", toCrsUri(crs));
            }
            return json;
        } else if (value instanceof URI uri) {
            return Map.of("href", uri.toString());
        }
        return value;
    }

    private String toCrsUri(CoordinateReferenceSystem crs) throws ProcessException {
        try {
            final Integer code = IdentifiedObjects.lookupEPSG(crs);
            if (code != null) {
                return "http://www.opengis.net/def/crs/EPSG/0/" + code;
            }
            final String urn = IdentifiedObjects.lookupURN(crs, null);
            if (urn != null) {
                return urn;
            }
        } catch (FactoryException ex) {
            throw new ProcessException("Failed to identify CRS " + crs.getName(), this, ex);
        }
        throw new ProcessException("Failed to identify CRS " + crs.getName(), this);
    }

    /**
     * Unwrap qualified values and references.
     */
    private static Object unwrap(Object value) {
        if (value instanceof Map<?,?> map) {
            if (map.containsKey("value")) {
                return map.get("value");
            } else if (map.get("href") instanceof String href) {
                return URI.create(href);
            }
        }
        return value;
    }

    private Object convert(Object value, Class<?> valueClass) throws ProcessException {
        if (value == null || valueClass.isInstance(value)) {
            return value;
        } else if (valueClass == Envelope.class) {
            return toEnvelope(value);
        }
        try {
            return ObjectConverters.convert(value, valueClass);
        } catch (UnconvertibleObjectException ex) {
            throw new ProcessException("Can not convert value " + value + " to " + valueClass.getSimpleName(), this, ex);
        }
    }

    private Envelope toEnvelope(Object value) throws ProcessException {
        if (value instanceof Map<?,?> map && map.get("bbox") instanceof List<?> bbox
                && !bbox.isEmpty() && bbox.size() % 2 == 0) {
            final int dim = bbox.size() / 2;
            final double[] lower = new double[dim];
            final double[] upper = new double[dim];
            for (int i = 0; i < dim; i++) {
                lower[i] = ((Number) bbox.get(i)).doubleValue();
                upper[i] = ((Number) bbox.get(i + dim)).doubleValue();
            }
            final GeneralEnvelope env = new GeneralEnvelope(lower, upper);
            try {
                if (map.get("crs") instanceof String crs) {
                    env.setCoordinateReferenceSystem(CRS.forCode(crs));
                } else if (dim == 2) {
                    env.setCoordinateReferenceSystem(CommonCRS.defaultGeographic());
                }
            } catch (FactoryException ex) {
                throw new ProcessException("Unknown bounding box CRS " + map.get("crs"), this, ex);
            }
            return env;
        }
        throw new ProcessException("Can not convert value " + value + " to a bounding box", this);
    }
}
