/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1999-2009, Open Source Geospatial Foundation (OSGeo)
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

import org.opengis.util.ProgressListener;
import org.opengis.util.InternationalString;

import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.util.SimpleInternationalString;


/**
 * Reports progress by sending email to the specified address at regular interval.
 *
 * @author Martin Desruisseaux (MPO, IRD)
 * @version 3.00
 *
 * @since 2.0
 * @module
 */
public class ProgressMailer implements ProgressListener {
    /**
     * Nom de l'opération en cours. Le pourcentage sera écris à la droite de ce nom.
     */
    private InternationalString description;

    /**
     * Langue à utiliser pour le formattage.
     */
    private final Locale locale;

    /**
     * Session à utiliser pour envoyer des courriels.
     */
    private final Session session;

    /**
     * Adresses des personnes à qui envoyer un rapport sur les progrès.
     */
    private final Address[] address;

    /**
     * Laps de temps entre deux courriers électroniques informant des progrès.
     * On attendra que ce laps de temps soit écoulés avant d'envoyer un nouveau courriel.
     */
    private long timeInterval = 3*60*60*1000L;

    /**
     * Date et heure à laquelle envoyer le prochain courriel.
     */
    private long nextTime;

    /**
     * The percentage executed up to date.
     */
    private float percent;

    /**
     * {@code true} if the action has been canceled.
     */
    private volatile boolean canceled;

    /**
     * Constructs an objects reporting progress to the specified email address.
     *
     * @param  host The server to use for sending emails.
     * @param  address Email adress where to send progress reports.
     * @throws AddressException if the specified address use an invalid syntax.
     */
    public ProgressMailer(final String host, final String address) throws AddressException {
        this(Session.getDefaultInstance(properties(host)), new InternetAddress[] {
                new InternetAddress(address)});
    }

    /**
     * Constructs an objects reporting progress to the specified email adresses.
     *
     * @param session Session to use for sending emails.
     * @param address
     */
    public ProgressMailer(final Session session, final Address[] address) {
        this.session = session;
        this.address = address;
        this.locale  = Locale.getDefault();
        nextTime = System.currentTimeMillis();
    }

    /**
     * Retourne un ensemble de propriétés nécessaires pour ouvrir une session.
     *
     * @param host Nom du serveur à utiliser pour envoyer des courriels.
     */
    private static final Properties properties(final String host) {
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
     * Set the time laps (in milliseconds) between two emails.
     * The default value is 3 hours.
     *
     * @param interval The new time laps in milliseconds.
     */
    public synchronized void setTimeInterval(final long interval) {
        this.timeInterval = interval;
    }

    /**
     * Returns the description of the current task being performed, or {@code null} if none.
     *
     * @since 2.3
     */
    @Override
    public InternationalString getTask() {
        return description;
    }

    /**
     * Description for the lengthly operation to be reported, or {@code null} if none.
     *
     * @deprecated Replaced by getTask().toString()
     */
    @Override
    @Deprecated
    public String getDescription() {
        return (description != null) ? description.toString() : null;
    }

    /**
     * Sets the description of the current task being performed. This method is usually invoked
     * before any progress begins. However, it is legal to invoke this method at any time during
     * the operation, in which case the description display is updated without any change to the
     * percentage accomplished.
     *
     * @since 2.3
     */
    @Override
    public void setTask(final InternationalString task) {
        description = task;
    }

    /**
     * Sets the description for the lenghtly operation to be reported. This method is usually
     * invoked before any progress begins. However, it is legal to invoke this method at any
     * time during the operation, in which case the description display is updated without
     * any change to the percentage accomplished.
     *
     * @deprecated Replaced by setTask
     */
    @Override
    @Deprecated
    public synchronized void setDescription(final String description) {
        this.description = new SimpleInternationalString(description);
    }

    /**
     * Envoie le message spécifié par courrier électronique.
     *
     * @param method Nom de la méthode qui appelle celle-ci. Cette information
     *        est utilisée pour produire un message d'erreur en cas d'échec.
     * @param subjectKey Clé du sujet: {@link ResourceKeys#PROGRESS},
     *        {@link ResourceKeys#WARNING} ou {@link ResourceKeys#EXCEPTION}.
     * @param messageText Message à envoyer par courriel.
     */
    private void send(final String method, final int subjectKey, final String messageText) {
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
     * Envoie par courrier électronique un rapport des progrès.
     *
     * @param method Nom de la méthode qui appelle celle-ci. Cette information
     *        est utilisée pour produire un message d'erreur en cas d'échec.
     * @param percent Pourcentage effectué (entre 0 et 100).
     */
    private void send(final String method, final float percent) {
        this.percent = percent;
        final Runtime       system = Runtime.getRuntime();
        final float    MEMORY_UNIT = (1024f*1024f);
        final float     freeMemory = system.freeMemory()  / MEMORY_UNIT;
        final float    totalMemory = system.totalMemory() / MEMORY_UNIT;
        final Vocabulary resources = Vocabulary.getResources(locale);
        final NumberFormat  format = NumberFormat.getPercentInstance(locale);
        final StringBuffer  buffer = new StringBuffer(description != null ?
                description : resources.getString(Vocabulary.Keys.PROGRESSION));
        buffer.append(": ");
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
        send("started", 0);
    }

    /**
     * Notifies progress. This method will send an email only if at least the amount
     * of time specified by {@link #setTimeInterval} is ellapsed since the last email.
     */
    @Override
    public synchronized void progress(float percent) {
        final long time = System.currentTimeMillis();
        if (time > nextTime) {
            nextTime = time + timeInterval;
            if (percent <  1f) percent =  1f;
            if (percent > 99f) percent = 99f;
            send("progress", percent);
        }
    }

    /**
     * Returns the current progress as a percent completed.
     *
     * @since 2.3
     */
    @Override
    public float getProgress() {
        return percent;
    }

    /**
     * Returns {@code true} if this job is cancelled.
     *
     * @since 2.3
     */
    @Override
    public boolean isCanceled() {
        return canceled;
    }

    /**
     * Indicates that task should be cancelled.
     *
     * @since 2.3
     */
    @Override
    public void setCanceled(final boolean cancel) {
        canceled = cancel;
    }

    /**
     * Sends a warning by email.
     *
     * @param source
     *          Name of the warning source, or {@code null} if none. This is typically the
     *          filename in process of being parsed or the URL of the data being processed
     * @param location
     *          Text to write on the left side of the warning message, or {@code null} if none.
     *          This is typically the line number where the error occured in the {@code source}
     *          file or the feature ID of the feature that produced the message
     * @param warning
     *          The warning message.
     */
    @Override
    public synchronized void warningOccurred(final String source, final String location, final String warning) {
        final StringBuilder buffer = new StringBuilder();
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

    /**
     * Sends an emails saying that the operation finished.
     */
    @Override
    public synchronized void complete() {
        send("complete", 100);
    }

    /**
     * Releases any resource used by this object.
     */
    @Override
    public void dispose() {
    }
}
