/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1999-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.gui.headless;

import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import java.io.PrintWriter;
import java.io.CharArrayWriter;
import java.text.NumberFormat;
import java.text.FieldPosition;

import javax.mail.Session;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.AddressException;
import net.jcip.annotations.ThreadSafe;

import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.process.ProgressController;

import static org.apache.sis.util.CharSequences.length;


/**
 * Reports progress by sending email to the specified address at regular interval.
 *
 * @author Martin Desruisseaux (MPO, IRD, Geomatys)
 * @author Guilhem Legal (Geomatys)
 * @version 3.20
 *
 * @since 2.0
 * @module
 */
@ThreadSafe
public class ProgressMailer extends ProgressController {
    /**
     * The session for sending emails.
     */
    private final Session session;

    /**
     * Where to send the emails.
     */
    private final Address[] address;

    /**
     * How long to wait before 2 emails.
     */
    private long timeInterval = 3*60*60*1000L;

    /**
     * When to send the next email.
     */
    private long nextTime;

    /**
     * Constructs an objects reporting progress to the specified email address.
     *
     * @param  host The server to use for sending emails.
     * @param  address Email address where to send progress reports.
     * @throws AddressException if the specified address use an invalid syntax.
     */
    public ProgressMailer(final String host, final String address) throws AddressException {
        this(Session.getDefaultInstance(properties(host)), new InternetAddress[] {
                new InternetAddress(address)});
    }

    /**
     * Constructs an objects reporting progress to the specified email addresses.
     *
     * @param session Session to use for sending emails.
     * @param address Email address where to send progress reports.
     */
    public ProgressMailer(final Session session, final Address... address) {
        this.session = session;
        this.address = address;
        nextTime = System.currentTimeMillis();
    }

    /**
     * Returns the set of properties required for opening a new session. This is a workaround for
     * RFE #4093999 ("Relax constraint on placement of this()/super() call in constructors").
     *
     * @param host The name of the server where to send the emails.
     */
    private static Properties properties(final String host) {
        final Properties props = new Properties();
        props.setProperty("mail.smtp.host", host);
        return props;
    }

    /**
     * Returns the time laps (in milliseconds) between two emails.
     *
     * @return The current time laps in milliseconds.
     */
    public synchronized long getTimeInterval() {
        return timeInterval;
    }

    /**
     * Sets the time laps (in milliseconds) between two emails.
     * The default value is 3 hours.
     *
     * @param interval The new time laps in milliseconds.
     */
    public synchronized void setTimeInterval(final long interval) {
        this.timeInterval = interval;
    }

    /**
     * Sends the given message by email.
     *
     * @param method Name of the caller method. Used only in case of failure.
     * @param subjectKey Subject resource key: {@link ResourceKeys#PROGRESS},
     *        {@link ResourceKeys#WARNING} or {@link ResourceKeys#EXCEPTION}.
     * @param messageText The message to send by email.
     */
    private void send(final String method, final int subjectKey, final String messageText) {
        final Locale locale = getLocale();
        try {
            final Message message = new MimeMessage(session);
            message.setFrom();
            message.setRecipients(Message.RecipientType.TO, address);
            message.setSubject(Vocabulary.getResources(locale).getString(subjectKey));
            message.setSentDate(new Date());
            message.setText(messageText);
            Transport.send(message);
        } catch (MessagingException exception) {
            Logging.unexpectedException(ProgressMailer.class, method, exception);
        }
    }

    /**
     * Send a progress report by email.
     *
     * @param method Name of the caller method. Used only in case of failure.
     * @param percent percentage.
     */
    private void send(final String method, final float percent) {
        super.setProgress(percent);
        final Locale        locale = getLocale();
        final Runtime       system = Runtime.getRuntime();
        final float    MEMORY_UNIT = (1024f*1024f);
        final float     freeMemory = system.freeMemory()  / MEMORY_UNIT;
        final float    totalMemory = system.totalMemory() / MEMORY_UNIT;
        final Vocabulary resources = Vocabulary.getResources(locale);
        final NumberFormat  format = NumberFormat.getPercentInstance(locale);
        CharSequence task = getTask();
        if (task == null) {
            task = resources.getString(Vocabulary.Keys.PROGRESSION);
        }
        final StringBuffer buffer = new StringBuffer(task).append(": ");
        format.format(percent/100, buffer, new FieldPosition(0)).append('\n');
        buffer.append(resources.getString(Vocabulary.Keys.MEMORY_HEAP_SIZE_$1, totalMemory)).append('\n')
              .append(resources.getString(Vocabulary.Keys.MEMORY_HEAP_USAGE_$1, 1f - freeMemory/totalMemory)).append('\n');
        send(method, Vocabulary.Keys.PROGRESSION, buffer.toString());
    }

    /**
     * Sends an email saying that the operation started.
     */
    @Override
    public synchronized void started() {
        send(Vocabulary.format(Vocabulary.Keys.STARTED), 0);
    }

    /**
     * Notifies progress. This method will send an email only if at least the amount
     * of time specified by {@link #setTimeInterval} is elapsed since the last email.
     */
    @Override
    public synchronized void setProgress(float percent) {
        final long time = System.currentTimeMillis();
        if (time > nextTime) {
            nextTime = time + timeInterval;
            if (percent <  1f) percent =  1f;
            if (percent > 99f) percent = 99f;
            send(Vocabulary.format(Vocabulary.Keys.PROGRESSION), percent);
        }
    }

    /**
     * Sends an email saying that the operation paused.
     *
     * @since 3.20
     */
    @Override
    public void paused() {
        send(Vocabulary.format(Vocabulary.Keys.PAUSED), getProgress());
    }

    /**
     * Sends an email saying that the operation resumed.
     *
     * @since 3.20
     */
    @Override
    public void resumed() {
        send(Vocabulary.format(Vocabulary.Keys.RESUMED), getProgress());
    }

    /**
     * Sends an email saying that the operation finished.
     */
    @Override
    public synchronized void completed() {
        send(Vocabulary.format(Vocabulary.Keys.COMPLETED), 100);
    }

    /**
     * Sends a warning by email.
     *
     * @param source
     *          Name of the warning source, or {@code null} if none. This is typically the
     *          filename in process of being parsed or the URL of the data being processed
     * @param location
     *          Text to write on the left side of the warning message, or {@code null} if none.
     *          This is typically the line number where the error occurred in the {@code source}
     *          file or the feature ID of the feature that produced the message
     * @param warning
     *          The warning message.
     */
    @Override
    public synchronized void warningOccurred(final String source, final String location, final String warning) {
        final StringBuilder buffer = new StringBuilder(length(source) + length(location) + length(warning) + 5);
        if (source != null) {
            buffer.append(source);
            if (location != null) {
                buffer.append(" (").append(location).append(')');
            }
            buffer.append(": ");
        } else if (location != null) {
            buffer.append(location).append(": ");
        }
        buffer.append(warning);
        send("warningOccurred", Vocabulary.Keys.WARNING, buffer.toString());
    }

    /**
     * Send an exception stack trace by email.
     */
    @Override
    public synchronized void exceptionOccurred(final Throwable exception) {
        final CharArrayWriter buffer = new CharArrayWriter();
        exception.printStackTrace(new PrintWriter(buffer));
        send("exceptionOccurred", Vocabulary.Keys.EXCEPTION, buffer.toString());
    }
}
