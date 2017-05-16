/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009 - 2012, Geomatys
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

package org.geotoolkit.processing;

import java.util.Date;
import java.util.concurrent.CancellationException;
import javax.swing.event.EventListenerList;

import org.geotoolkit.process.*;
import org.opengis.metadata.quality.ConformanceResult;
import org.opengis.parameter.ParameterValueGroup;
import org.apache.sis.metadata.iso.lineage.DefaultProcessing;
import org.apache.sis.metadata.iso.lineage.DefaultProcessStep;
import org.geotoolkit.parameter.Parameters;

import static org.apache.sis.util.ArgumentChecks.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public abstract class AbstractProcess implements org.geotoolkit.process.Process {

    protected final EventListenerList listeners = new EventListenerList();

    protected final ProcessDescriptor descriptor;
    protected final ParameterValueGroup outputParameters;
    protected ParameterValueGroup inputParameters;

    volatile boolean isCanceled = false;

    volatile boolean isPaused = false;

    /**
     *
     * @param desc process description, not null
     * @param input process inpu parameters, not null
     */
    public AbstractProcess(final ProcessDescriptor desc, final ParameterValueGroup input) {
        ensureNonNull("descriptor", desc);
        ensureNonNull("input", input);
        this.descriptor = desc;
        this.outputParameters = descriptor.getOutputDescriptor().createValue();
        this.inputParameters = input;

        final ConformanceResult res = Parameters.isValid(inputParameters, inputParameters.getDescriptor());
        if (!res.pass()) {
            throw new IllegalArgumentException("Input parameters are invalid:" + res.getExplanation());
        }
    }

    /**
     * For subclasses only.<br>
     * Current use case is for WPS2, to reattach to an already running process.
     *
     * @param desc process description, not null
     */
    protected AbstractProcess(final ProcessDescriptor desc) {
        ensureNonNull("descriptor", desc);
        this.descriptor = desc;
        this.outputParameters = descriptor==null? null : descriptor.getOutputDescriptor().createValue();
        this.inputParameters = null;

    }

    @Override
    public ProcessDescriptor getDescriptor() {
        return descriptor;
    }

    @Override
    public ParameterValueGroup getInput() {
        return inputParameters;
    }

    /**
     * {@linkplain #execute() Executes} the process and returns the {@linkplain #outputParameters
     * output parameters}. This method takes care of invoking the
     * {@link ProcessListener#started(ProcessEvent) started} and
     * {@link ProcessListener#completed(ProcessEvent) completed} or
     * {@link ProcessListener#failed(ProcessEvent) failed} methods.
     *
     * @return The computation results stored in the {@linkplain #outputParameters output parameters}.
     * @throws ProcessException If the process failed.
     */
    @Override
    public ParameterValueGroup call() throws ProcessException {
        fireProcessStarted(null);
        boolean success = false;
        Exception exception = null;
        try {
            execute();
            success = true;
        } catch (ProcessException | RuntimeException e) {
            exception = e;
            throw e; // Will execute 'finally' before to exit.
        } catch (Throwable t) {
            exception = new ProcessException(t.getMessage(), this, t);
            throw (ProcessException)exception; // Will execute 'finally' before to exit.
        } finally {
            if (success) {
                fireProcessCompleted(null);
            } else {
                fireProcessFailed(null, exception);
            }
        }
        return outputParameters;
    }

    /**
     * Returns a description of the process, the geographic inputs and outputs and other metadata.
     * The default implementation performs the following mapping:
     *
     * <ul>
     *   <li>{@link ProcessDescriptor#getIdentifier()}  → {@link DefaultProcessing#getIdentifier()}.</li>
     *   <li>{@link ProcessDescriptor#getDisplayName()} → {@link DefaultProcessStep#getDescription()}.</li>
     *   <li>Current time → {@link DefaultProcessStep#getDate()}.</li>
     * </ul>
     *
     * Subclasses are encouraged to complete the metadata with their own information.
     *
     * @return A description of the process, the geographic inputs and outputs and other metadata.
     */
    @Override
    public DefaultProcessStep getMetadata() {
        final DefaultProcessStep step = new DefaultProcessStep(descriptor.getDisplayName());
        step.setDate(new Date()); // Set to current time.

        final DefaultProcessing processing = new DefaultProcessing();
        processing.setIdentifier(descriptor.getIdentifier());
        step.setProcessingInformation(processing);

        return step;
    }


    /**
     * {@linkplain #cancelProcess() CancelProcess} set the {@code isCanceled} flag to {@code true}.
     * The {@code isCanceled} flag is used by the process to know if someone ask for his cancelation.
     * Long process should when they can check the {@code isCanceled} flag through the {@link #isCanceled() isCanceled} method.
     * If a process see his {@code isCanceled} flag to {@code true} he should throw an {@link CancellationException exception}.
     */
    public void cancelProcess(){
        isCanceled = true;
    }

    /**
     * {@linkplain #pauseProcess() PauseProcess} set the {@code isPaused} flag to {@code true}.
     * The {@code isPaused} flag is used by the process to know if someone ask for his pause.
     * Long process should when they can check the {@code isPaused} flag through the {@link #isPaused() isPaused} method.
     * If a process see his {@code isPaused} flag to {@code true} he should throw an {@link PauseException exception}.
     */
    public void pauseProcess(){
        isPaused = true;
    }

    /**
     * {@linkplain #resumeProcess() ResumeProcess} set the {@code isPaused} flag to {@code false}.
     * The {@code isPaused} flag is used by the process to know if someone ask for his pause.
     * Long process should when they can check the {@code isPaused} flag through the {@link #isPaused() isPaused} method.
     * If a process see his {@code isPaused} flag to {@code true} he should throw an {@link PauseException exception}.
     */
    public void resumeProcess(){
        isPaused = false;
    }

    /**
     * Return the {@code isCanceled} flag value.
     *
     * @return {@code isCanceled} flag value.
     */
    public boolean isCanceled(){
        return isCanceled;
    }

    /**
     * Return the {@code isPaused} flag value.
     *
     * @return {@code isPaused} flag value.
     */
    public boolean isPaused(){
        return isPaused;
    }

    /**
     * Immediately performs the action of this process. This method is invoked by the {@link #call()}
     * method after any {@linkplain #addListener(ProcessListener) registered listeners} have been
     * notified of the process start. Listeners will also be notified when the process end, either
     * successfully or on failure.
     *
     * @throws ProcessException If the process failed.
     */
    protected abstract void execute() throws ProcessException;

    @Override
    public void addListener(final ProcessListener listener) {
        listeners.add(ProcessListener.class, listener);
    }

    @Override
    public void removeListener(final ProcessListener listener) {
        listeners.remove(ProcessListener.class, listener);
    }

    @Override
    public ProcessListener[] getListeners() {
        return listeners.getListeners(ProcessListener.class);
    }

    /**
     * Invoked when the process is about to start. This method invokes
     * {@link ProcessListener#started(ProcessEvent)} for all registered listeners.
     *
     * @param task A description of the task which is starting, or {@code null} if none.
     */
    protected void fireProcessStarted(final CharSequence task) {
        final ProcessEvent event = new ProcessEvent(this, task, 0f);
        for (ProcessListener listener : listeners.getListeners(ProcessListener.class)) {
            listener.started(event);
        }
    }

    /**
     * Invoked when the process is making progress. This method invokes
     * {@link ProcessListener#progressing(ProcessEvent)} for all registered listeners.
     *
     * @param task A description of the task which is progressing, or {@code null} if none.
     * @param progress The progress as a number between 0 and 100, or {@link Float#NaN} if undetermined.
     * @param hasIntermediateResults {@code true} if the {@link #outputParameters} contains
     *        intermediate results that can be sent to the listeners.
     */
    protected void fireProgressing(final CharSequence task, final float progress,
            final boolean hasIntermediateResults)
    {
        final ProcessEvent event = new ProcessEvent(this, task, progress,
                hasIntermediateResults ? outputParameters : null);
        for (ProcessListener listener : listeners.getListeners(ProcessListener.class)) {
            listener.progressing(event);
        }
    }

    /**
     * Invoked when a non-fatal exception occurred during process. This method invokes
     * {@link ProcessListener#progressing(ProcessEvent)} for all registered listeners.
     *
     * @param task     A description of the task which is progressing, or {@code null} if none.
     * @param progress The progress as a number between 0 and 100, or {@link Float#NaN} if undetermined.
     * @param warning  The non-fatal exception that occurred.
     */
    protected void fireWarningOccurred(final CharSequence task, final float progress, final Exception warning) {
        final ProcessEvent event = new ProcessEvent(this, task, progress, warning);
        for (ProcessListener listener : listeners.getListeners(ProcessListener.class)) {
            listener.progressing(event);
        }
    }

    /**
     * Invoked after the process successfully completed. This method invokes
     * {@link ProcessListener#completed(ProcessEvent)} for all registered listeners.
     *
     * @param task A description of the completed task, or {@code null} if none.
     */
    protected void fireProcessCompleted(final CharSequence task) {
        final ProcessEvent event = new ProcessEvent(this, task, 100f, outputParameters);
        for (ProcessListener listener : listeners.getListeners(ProcessListener.class)) {
            listener.completed(event);
        }
    }

    /**
     * Invoked after a fatal error occurred during the process execution. This method invokes
     * {@link ProcessListener#failed(ProcessEvent)} for all registered listeners.
     *
     * @param task A description of the task that failed, or {@code null} if none.
     * @param exception The exception which occurred, or {@code null} if unavailable.
     */
    protected void fireProcessFailed(final CharSequence task, final Exception exception) {
        final ProcessEvent event = new ProcessEvent(this, task, Float.NaN, exception);
        for (ProcessListener listener : listeners.getListeners(ProcessListener.class)) {
            listener.failed(event);
        }
    }

    /**
     * Invoked when the process is paused during the process execution. This method invokes
     * {@link ProcessListener#paused(ProcessEvent)} for all registered listeners.
     *
     * @param task A description of the task that being paused, or {@code null} if none.
     */
    protected void fireProcessPaused(final CharSequence task, final float progress) {
        final ProcessEvent event = new ProcessEvent(this, task, progress);
        for (ProcessListener listener : listeners.getListeners(ProcessListener.class)) {
            listener.paused(event);
        }
    }

    /**
     * Invoked when the process is resumed after being suspended. This method invokes
     * {@link ProcessListener#resumed(ProcessEvent)} for all registered listeners.
     *
     * @param task A description of the task that being resumed, or {@code null} if none.
     */
    protected void fireProcessResumed(final CharSequence task, final float progress) {
        final ProcessEvent event = new ProcessEvent(this, task, progress);
        for (ProcessListener listener : listeners.getListeners(ProcessListener.class)) {
            listener.resumed(event);
        }
    }

    /**
     * Forward a start event to all listeners.
     * @param event
     */
    @Deprecated
    protected void fireStartEvent(final ProcessEvent event) {
        for (ProcessListener listener : listeners.getListeners(ProcessListener.class)) {
            listener.started(event);
        }
    }

    /**
     * Forward a progress event to all listeners.
     * @param event
     */
    @Deprecated
    protected void fireProgressEvent(final ProcessEvent event) {
        for (ProcessListener listener : listeners.getListeners(ProcessListener.class)) {
            listener.progressing(event);
        }
    }

    /**
     * Forward a fail event to all listeners.
     * @param event
     */
    @Deprecated
    protected void fireFailEvent(final ProcessEvent event) {
        for (ProcessListener listener : listeners.getListeners(ProcessListener.class)) {
            listener.failed(event);
        }
    }

    /**
     * Forward an end event to all listeners.
     * @param event
     */
    @Deprecated
    protected void fireEndEvent(final ProcessEvent event) {
        for (ProcessListener listener : listeners.getListeners(ProcessListener.class)) {
            listener.completed(event);
        }
    }
}
