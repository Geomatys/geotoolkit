/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011-2012, Geomatys
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
package org.geotoolkit.process;

import java.util.Locale;

import org.opengis.util.InternationalString;

import org.apache.sis.util.Localized;
import org.apache.sis.util.iso.SimpleInternationalString;


/**
 * Monitors the progress of some lengthly operation, and allows cancellation.
 * This abstract class makes no assumption about the output device. Additionally,
 * this class provides support for non-fatal warning and exception reports.
 * <p>
 * All implementations should be multi-thread safe, even the ones that provide
 * feedback to a user interface thread.
 * <p>
 * Usage example:
 *
 * {@preformat java
 *     float scale = 100f / maximumCount;
 *     controller.started();
 *     for (int counter=0; counter<maximumCount; counter++) {
 *         if (controller.isCanceled()) {
 *             break;
 *         }
 *         controller.progress(scale * counter);
 *         try {
 *             // Do some work...
 *         } catch (NonFatalException e) {
 *             controller.exceptionOccurred(e);
 *         }
 *     }
 *     controller.complete();
 * }
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Jody Garnet (Refractions Research)
 * @author Guilhem Legal (Geomatys)
 * @version 3.20
 *
 * @since 3.19 (derived from 2.0)
 * @module
 */
public abstract class ProgressController implements Localized, ProcessListener {
    /**
     * The language to use for formatting messages.
     */
    private final Locale locale;

    /**
     * Name of the lengthly operation.
     */
    private volatile InternationalString task;

    /**
     * The current progress percentage, as a value between 0 and 100.
     */
    private volatile float progress;

    /**
     * {@code true} if the action has been canceled.
     */
    private volatile boolean canceled;

    /**
     * Constructs a progress controller which will format messages in the
     * {@linkplain Locale#getDefault() system default locale}.
     */
    protected ProgressController() {
        locale = Locale.getDefault();
    }

    /**
     * Returns the locale used for formatting messages. The default value is the
     * {@linkplain Locale#getDefault() system default} locale.
     *
     * @return The locale.
     */
    @Override
    public Locale getLocale() {
        return locale;
    }

    /**
     * Returns the description of the current task being performed, or {@code null} if none.
     * It is assumed that if the task is {@code null} applications may simply report that the
     * process is "<cite>in progress</cite>" or "<cite>working</cite>" as represented in the
     * {@linkplain #getLocale() current locale}.
     *
     * @return Description of the task being performed, or {@code null} if none.
     */
    public InternationalString getTask() {
        return task;
    }

    /**
     * Sets the description of the current task being performed. This method is usually invoked
     * before any progress begins. However, it is legal to invoke this method at any time during
     * the operation, in which case the task label is updated without any change to the percentage
     * accomplished.
     *
     * @param task Description of the task being performed as a {@link String} or
     *        {@link InternationalString}, or {@code null} if none.
     */
    public void setTask(CharSequence task) {
        if (task != null && !(task instanceof InternationalString)) {
            task = new SimpleInternationalString(task.toString());
        }
        this.task = (InternationalString) task;
    }

    /**
     * Notifies this controller that the operation begins.
     */
    public abstract void started();

    /**
     * Notifies this controller that the operation begins and sets the states of this controller
     * according the given event. The default implementation first invokes {@link #started()},
     * then invokes <code>{@linkplain #progressing(ProcessEvent) progressing}(event)</code>.
     *
     * @param event The progress event, or {@code null} if none.
     *
     * @since 3.19
     */
    @Override
    public void started(final ProcessEvent event) {
        started();
        progressing(event);
    }

    /**
     * Notifies this controller that the operation is suspended.
     *
     * @since 3.20
     */
    public abstract void paused();

    /**
     * Notifies this controller that the operation is suspended and sets the states of this
     * controller according the given event. The default implementation first invokes
     * <code>{@linkplain #progressing(ProcessEvent) progressing}(event)</code>,
     * then invokes {@link #paused()}.
     *
     * @param event The progress event, or {@code null} if none.
     *
     * @since 3.20
     */
    @Override
    public void paused(final ProcessEvent event) {
        progressing(event);
        paused();
    }

    /**
     * Notifies this controller that the operation is resumed.
     *
     * @since 3.20
     */
    public abstract void resumed();

    /**
     * Notifies this controller that the operation is resumed and sets the states of this controller
     * according the given event. The default implementation first invokes {@link #resumed()},
     * then invokes <code>{@linkplain #progressing(ProcessEvent) progressing}(event)</code>.
     *
     * @param event The progress event, or {@code null} if none.
     *
     * @since 3.20
     */
    @Override
    public void resumed(final ProcessEvent event) {
        resumed();
        progressing(event);
    }

    /**
     * Returns the current progress as a percent completed.
     *
     * @return Percent completed between 0 and 100 inclusive.
     */
    public float getProgress() {
        return progress;
    }

    /**
     * Notifies this controller of progress in the lengthly operation. Progresses are reported
     * as a value between 0 and 100 inclusive. Values out of bounds will be clamped.
     *
     * @param percent The progress as a value between 0 and 100 inclusive.
     */
    public void setProgress(float percent) {
        if (percent < 0  ) percent = 0;
        if (percent > 100) percent = 100;
        progress = percent;
    }

    /**
     * Updates the {@linkplain #setTask(CharSequence) task} and {@linkplain #setProgress(float)
     * progress} state, and {@linkplain #exceptionOccurred(Throwable) reports the exception} if
     * any. More specifically:
     * <p>
     * <ul>
     *   <li>The new task is set to the {@link ProcessEvent#getTask()} value unless the later
     *       method returned {@code null}, in which case this {@code ProgressController} task
     *       is left unchanged.</li>
     *   <li>The new progress is set to the {@link ProcessEvent#getProgress()} value unless the
     *       later method returned {@link Float#NaN}, in which case this {@code ProgressController}
     *       progress state is left unchanged.</li>
     *   <li>If {@link ProcessEvent#getException()} returns a non-null value, then that value is
     *       given to {@link #exceptionOccurred(Throwable)}.</li>
     * </ul>
     *
     * @param event The progress event, or {@code null} if none.
     *
     * @since 3.19
     */
    @Override
    public void progressing(final ProcessEvent event) {
        if (event != null) {
            final InternationalString task = event.getTask();
            if (task != null) {
                setTask(task);
            }
            final float progress = event.getProgress();
            if (!Float.isNaN(progress)) {
                setProgress(progress);
            }
            final Exception ex = event.getException();
            if (ex != null) {
                exceptionOccurred(ex);
            }
        }
    }

    /**
     * Notifies this controller that the operation has finished. The progress indicator will
     * shows 100% or disappears, at implementor choice. If warning messages were pending,
     * they will be displayed now.
     */
    public abstract void completed();

    /**
     * Notifies this controller that the operation has finished and sets the states of this
     * controller according the given event. The default implementation first invokes
     * <code>{@linkplain #progressing(ProcessEvent) progressing}(event)</code>, then
     * invokes {@link #completed()}.
     *
     * @param event The progress event, or {@code null} if none.
     *
     * @since 3.19
     */
    @Override
    public void completed(final ProcessEvent event) {
        progressing(event);
        completed();
    }

    /**
     * Notifies this controller that the operation has failed and sets the states of this
     * controller according the given event. The default implementation just delegates to
     * <code>{@linkplain #progressing(ProcessEvent) progressing}(event)</code>. Note that
     * the above {@code progressing} method pass the {@linkplain ProcessEvent#getException()
     * exception declared by the event} (if any) to the {@link #exceptionOccurred(Throwable)}
     * method.
     *
     * @param event The progress event, or {@code null} if none.
     *
     * @since 3.19
     */
    @Override
    public void failed(final ProcessEvent event) {
        progressing(event);
    }

    /**
     * Indicates that this job should be canceled.
     */
    public void cancel() {
        canceled = true;
    }

    /**
     * Returns {@code true} if this job is canceled.
     *
     * @return {@code true} if this job is canceled.
     */
    public boolean isCanceled() {
        return canceled;
    }

    /**
     * Reports a warning. This warning may be {@linkplain java.util.logger.Logger logged}, printed
     * to the {@linkplain System#err standard error stream}, appears in a windows or be ignored,
     * at implementor choice.
     *
     * @param source
     *          Name of the warning source, or {@code null} if none. This is typically the
     *          name of the file being parsed, or the URL of the data being processed.
     * @param location
     *          Text to write on the left side of the warning message, or {@code null} if none.
     *          This is typically the line number where the error occurred in the {@code source}
     *          file or the feature ID of the feature that produced the message.
     * @param warning
     *          The warning message.
     */
    public abstract void warningOccurred(String source, String location, String warning);

    /**
     * Reports an exception. This method may prints the stack trace to the {@linkplain System#err
     * standard error stream} or display it in a dialog box, at implementor choice.
     *
     * @param exception The exception to report.
     */
    public abstract void exceptionOccurred(Throwable exception);
}
