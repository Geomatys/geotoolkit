/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.openoffice;

import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.LogRecord;

import com.sun.star.sheet.XAddIn;
import com.sun.star.util.Date;
import com.sun.star.lang.Locale;
import com.sun.star.lang.XServiceInfo;
import com.sun.star.lang.XServiceName;
import com.sun.star.beans.XPropertySet;
import com.sun.star.uno.AnyConverter;
import com.sun.star.lib.uno.helper.WeakBase;

import org.apache.sis.util.collection.BackingStoreException;
import org.apache.sis.util.Classes;
import org.apache.sis.util.logging.Logging;


/**
 * Base class for methods to export as formulas in the
 * <A HREF="http://www.openoffice.org">OpenOffice</A> spread sheet.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 3.09 (derived from 2.2)
 * @module
 */
public abstract class Formulas extends WeakBase implements XAddIn, XServiceName, XServiceInfo {
    /**
     * {@code true} for throwing an exception in case of failure, or {@code false} for returning
     * {@code NaN} instead. This apply only to numerical computations. Formulas returning a text
     * value returns in the exception message in current implementation.
     */
    protected static final boolean THROW_EXCEPTION = true;

    /**
     * Factor for conversions of days to milliseconds.
     * Used for date conversions as in {@link #toDate}.
     */
    protected static final long DAY_TO_MILLIS = 24*60*60*1000L;

    /**
     * The name of the provided service.
     */
    protected static final String ADDIN_SERVICE = "com.sun.star.sheet.AddIn";

    /**
     * Informations about exported methods.
     */
    protected final Map<String,MethodInfo> methods = new HashMap<String,MethodInfo>();

    /**
     * Locale attribute required by {@code com.sun.star.lang.XLocalizable} interface.
     */
    private Locale locale;

    /**
     * The locale as an object from the standard Java SDK.
     * Will be fetched only when first needed.
     */
    private transient java.util.Locale javaLocale;

    /**
     * The calendar to uses for date conversions. Will be created only when first needed.
     */
    private transient Calendar calendar;

    /**
     * The logger, fetched when first needed.
     */
    private static Logger logger;

    /**
     * Default constructor. Subclass constructors need to add entries in the {@link #methods} map.
     */
    protected Formulas() {
    }

    /**
     * Sets the locale to be used by this object.
     *
     * @param locale The new locale.
     */
    @Override
    public void setLocale(final Locale locale) {
        this.locale = locale;
        javaLocale = null;
    }

    /**
     * Returns the locale, which is used by this object.
     *
     * @return The current locale.
     */
    @Override
    public final Locale getLocale() {
        return locale;
    }

    /**
     * Returns the locale as an object from the Java standard SDK.
     *
     * @return The current locale.
     */
    protected final java.util.Locale getJavaLocale() {
        if (javaLocale == null) {
            if (locale != null) {
                String language = locale.Language; if (language == null) language = "";
                String country  = locale.Country;  if (country  == null) country  = "";
                String variant  = locale.Variant;  if (variant  == null) variant  = "";
                javaLocale = new java.util.Locale(language, country, variant);
            } else {
                javaLocale = java.util.Locale.getDefault();
            }
        }
        return javaLocale;
    }

    /**
     * Provides the supported service names of the implementation, including also
     * indirect service names.
     *
     * @return Sequence of service names that are supported.
     */
    @Override
    public final String[] getSupportedServiceNames() {
        return new String[] {ADDIN_SERVICE, getServiceName()};
    }

    /**
     * Tests whether the specified service is supported, i.e. implemented by the implementation.
     *
     * @param  name Name of service to be tested.
     * @return {@code true} if the service is supported, {@code false} otherwise.
     */
    @Override
    public final boolean supportsService(final String name) {
        return name.equals(ADDIN_SERVICE) || name.equals(getServiceName());
    }

    /**
     * The service name that can be used to create such an object by a factory.
     * This is defined as a field in the subclass with exactly the following signature:
     *
     * {@preformat java
     *     private static final String __serviceName;
     * }
     *
     * @return The service name.
     */
    @Override
    public abstract String getServiceName();

    /**
     * Provides the implementation name of the service implementation.
     *
     * @return Unique name of the implementation.
     */
    @Override
    public final String getImplementationName() {
        return getClass().getName();
    }

    /**
     * Returns the programmatic name of the category the function belongs to.
     * The category name is used to group similar functions together. The programmatic
     * category name should always be in English, it is never shown to the user. It
     * is usually one of the names listed in {@code com.sun.star.sheet.XAddIn} interface.
     *
     * @param  function The exact name of a method within its interface.
     * @return The category name the specified function belongs to.
     */
    @Override
    public final String getProgrammaticCategoryName(final String function) {
        final MethodInfo info = methods.get(function);
        return (info != null) ? info.category : "Add-In";
    }

    /**
     * Returns the user-visible name of the category the function belongs to.
     * This is used when category names are shown to the user.
     *
     * @param  function The exact name of a method within its interface.
     * @return The user-visible category name the specified function belongs to.
     */
    @Override
    public final String getDisplayCategoryName(final String function) {
        return getProgrammaticCategoryName(function);
    }

    /**
     * Returns the internal function name for an user-visible name. The user-visible
     * name of a function is the name shown to the user. It may be translated to the
     * {@linkplain #getLocale current language}, so it is never stored in files. It
     * should be a single word and is used when entering or displaying formulas.
     * <p>
     * Attention: The method name contains a spelling error. Due to compatibility
     * reasons the name cannot be changed.
     *
     * @param  display The user-visible name of a function.
     * @return The exact name of the method within its interface.
     */
    @Override
    public final String getProgrammaticFuntionName(final String display) {
        for (final Map.Entry<String,MethodInfo> entry : methods.entrySet()) {
            if (display.equals(entry.getValue().display)) {
                return entry.getKey();
            }
        }
        return "";
    }

    /**
     * Returns the user-visible function name for an internal name.
     * The user-visible name of a function is the name shown to the user.
     * It may be translated to the {@linkplain #getLocale current language},
     * so it is never stored in files. It should be a single word and is used
     * when entering or displaying formulas.
     *
     * @param  function The exact name of a method within its interface.
     * @return The user-visible name of the specified function.
     */
    @Override
    public final String getDisplayFunctionName(final String function) {
        final MethodInfo info = methods.get(function);
        return (info != null) ? info.display : "";
    }

    /**
     * Returns the description of a function. The description is shown to the user
     * when selecting functions. It may be translated to the {@linkplain #getLocale
     * current language}.
     *
     * @param  function The exact name of a method within its interface.
     * @return The description of the specified function.
     */
    @Override
    public final String getFunctionDescription(final String function) {
        final MethodInfo info = methods.get(function);
        return (info != null) ? info.description : "";
    }

    /**
     * Returns the user-visible name of the specified argument. The argument name is
     * shown to the user when prompting for arguments. It should be a single word and
     * may be translated to the {@linkplain #getLocale current language}.
     *
     * @param  function The exact name of a method within its interface.
     * @param  argument The index of the argument (0-based).
     * @return The user-visible name of the specified argument.
     */
    @Override
    public final String getDisplayArgumentName(final String function, int argument) {
        final MethodInfo info = methods.get(function);
        if (info != null) {
            argument <<= 1;
            final String[] arguments = info.arguments;
            if (argument >= 0 && argument < arguments.length) {
                return arguments[argument];
            }
        }
        return "";
    }

    /**
     * Returns the description of the specified argument. The argument description is
     * shown to the user when prompting for arguments. It may be translated to the
     * {@linkplain #getLocale current language}.
     *
     * @param  function The exact name of a method within its interface.
     * @param  argument The index of the argument (0-based).
     * @return The description of the specified argument.
     */
    @Override
    public final String getArgumentDescription(final String function, int argument) {
        final MethodInfo info = methods.get(function);
        if (info != null) {
            argument = (argument << 1) + 1;
            final String[] arguments = info.arguments;
            if (argument >= 0 && argument < arguments.length) {
                return arguments[argument];
            }
        }
        return "";
    }

    /**
     * Sets the timezone for time values to be provided to {@link #toDate}.
     * If this method is never invoked, then the default timezone is the locale one.
     *
     * @param timezone The new timezone.
     */
    protected final void setTimeZone(final String timezone) {
        final TimeZone tz = TimeZone.getTimeZone(timezone);
        if (calendar == null) {
            calendar = new GregorianCalendar(tz);
        } else {
            calendar.setTimeZone(tz);
        }
    }

    /**
     * Returns the spreadsheet epoch. The timezone is the one specified during the
     * last invocation of {@link #setTimeZone}. The epoch is used for date conversions
     * as in {@link #toDate}.
     *
     * @param  xOptions Provided by OpenOffice.
     * @return The spreadsheet epoch, always as a new Java Date object.
     */
    protected final java.util.Date getEpoch(final XPropertySet xOptions) {
        final Date date;
        try {
            date = (Date) AnyConverter.toObject(Date.class, xOptions.getPropertyValue("NullDate"));
        } catch (Exception e) {
            // Les exception lancées par la ligne ci-dessus sont nombreuses...
            reportException("getEpoch", e, THROW_EXCEPTION);
            return null;
        }
        if (calendar == null) {
            calendar = new GregorianCalendar();
        }
        calendar.clear();
        calendar.set(date.Year, date.Month-1, date.Day);
        return calendar.getTime();
    }

    /**
     * Converts a date from a spreadsheet value to a Java {@link java.util.Date} object.
     * The timezone is the one specified during the last invocation of {@link #setTimeZone}.
     *
     * @param  xOptions Provided by OpenOffice.
     * @param  time The spreadsheet numerical value for a date, by default in the local timezone.
     * @return The date as a Java object.
     */
    protected final java.util.Date toDate(final XPropertySet xOptions, final double time) {
        final java.util.Date date = getEpoch(xOptions);
        if (date != null) {
            date.setTime(date.getTime() + Math.round(time * DAY_TO_MILLIS));
        }
        return date;
    }

    /**
     * Converts a date from a Java {@link java.util.Date} object to a spreadsheet value.
     * The timezone is the one specified during the last invocation of {@link #setTimeZone}.
     *
     * @param  xOptions Provided by OpenOffice.
     * @param  time The date as a Java object.
     * @return The spreadsheet numerical value for a date, by default in the local timezone.
     */
    protected final double toDouble(final XPropertySet xOptions, final java.util.Date time) {
        final java.util.Date epoch = getEpoch(xOptions);
        if (epoch != null) {
            return (time.getTime() - epoch.getTime()) / (double)DAY_TO_MILLIS;
        } else {
            return Double.NaN;
        }
    }

    /**
     * The string to returns when a formula don't have any value to return.
     *
     * @return The string with a message for missing values.
     * @todo localize.
     */
    protected static String emptyString() {
        return "(none)";
    }

    /**
     * Returns the minimal length of the specified arrays. In the special case where one array
     * has a length of 1, we assume that this single element will be repeated for all elements
     * in the other array.
     */
    static int getLength(final Object[] array1, final Object[] array2) {
        if (array1 == null || array2 == null) {
            return 0;
        }
        if (array1.length == 1) return array2.length;
        if (array2.length == 1) return array1.length;
        return Math.min(array1.length, array2.length);
    }

    /**
     * Returns the localized message from the specified exception. If no message is available,
     * returns a default string. This method never returns a null value.
     *
     * @param  exception The exception for which to get the localized message.
     * @return A message.
     */
    protected static String getLocalizedMessage(final Throwable exception) {
        final String message = exception.getLocalizedMessage();
        if (message != null) {
            return message;
        }
        return Classes.getShortClassName(exception);
    }

    /**
     * Returns a table filled with {@link Double#NaN NaN} values. This method is invoked when
     * an operation failed for a whole table.
     *
     * @param  rows The number of rows.
     * @param  cols The number of columns.
     * @return A table of the given size filled with NaN values.
     */
    protected static double[][] getFailure(final int rows, final int cols) {
        final double[][] dummy = new double[rows][];
        for (int i=0; i<rows; i++) {
            final double[] row = new double[cols];
            Arrays.fill(row, Double.NaN);
            dummy[i] = row;
        }
        return dummy;
    }

    /**
     * Reports an exception. This is used if an exception occurred in a method which can't returns
     * a {@link String} object. This method log the stack trace at the FINE level. We don't use
     * the WARNING level since this is not a program disfunction; the failure is probably caused
     * by wrong user-specified parameters.
     *
     * @param method    The method from which an exception occurred.
     * @param exception The exception.
     * @param rethrow   {@code true} for rethrowing the exception after the report.
     */
    protected final void reportException(final String method, final Throwable exception, final boolean rethrow) {
        final Logger logger = getLogger();
        final LogRecord record = new LogRecord(Level.FINE, getLocalizedMessage(exception));
        record.setLoggerName      (logger.getName());
        record.setSourceClassName (getClass().getName());
        record.setSourceMethodName(method);
        record.setThrown          (exception);
        logger.log(record);
        if (rethrow) {
            if (exception instanceof RuntimeException) {
                throw (RuntimeException) exception;
            }
            if (exception instanceof Error) {
                throw (Error) exception;
            }
            throw new BackingStoreException(exception);
        }
    }

    /**
     * Returns the logger to use for logging warnings.
     *
     * @return The logger to use.
     */
    protected static synchronized Logger getLogger() {
        if (logger == null) {
            logger = Logging.getLogger("org.geotoolkit.openoffice");
        }
        return logger;
    }
}
