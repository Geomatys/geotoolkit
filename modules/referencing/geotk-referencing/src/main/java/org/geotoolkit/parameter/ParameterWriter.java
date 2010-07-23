/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.parameter;

import java.util.*;
import java.io.Writer;
import java.io.Console;
import java.io.FilterWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.lang.reflect.Array;
import javax.measure.unit.Unit;
import javax.measure.unit.UnitFormat;

import org.opengis.parameter.*;
import org.opengis.util.GenericName;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.operation.OperationMethod;

import org.geotoolkit.io.X364;
import org.geotoolkit.io.TableWriter;
import org.geotoolkit.lang.Decorator;
import org.geotoolkit.measure.Angle;
import org.geotoolkit.measure.AngleFormat;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.util.Localized;

import static org.geotoolkit.util.collection.XCollections.hashMapCapacity;


/**
 * Formats {@linkplain ParameterDescriptorGroup parameter descriptors} or
 * {@linkplain ParameterValueGroup parameter values} in a tabular format.
 * This writer assumes a monospaced font and an encoding capable to provide
 * drawing box characters (e.g. unicode).
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.03
 *
 * @since 2.1
 * @module
 */
@Decorator(Writer.class)
public class ParameterWriter extends FilterWriter implements Localized {
    /**
     * Special authority name for requesting the display of EPSG codes.
     */
    private static final String SHOW_EPSG_CODES = "EPSG:#";

    /**
     * If the identifier of only some authorities should be written, the authorities.
     * Otherwise {@code null}.
     */
    private Set<String> scopes;

    /**
     * The locale.
     */
    private Locale locale = Locale.getDefault();

    /**
     * The formatter to use for numbers. Will be created only when first needed.
     */
    private transient NumberFormat numberFormat;

    /**
     * The formatter to use for dates. Will be created only when first needed.
     */
    private transient DateFormat dateFormat;

    /**
     * The formatter to use for angles. Will be created only when first needed.
     */
    private transient AngleFormat angleFormat;

    /**
     * {@code true} if we are allowed to invoke {@link DecimalFormat#setPositivePrefix}.
     */
    private boolean canSetPositivePrefix;

    /**
     * {@code true} if the positive prefix should be set, if we are allowed to.
     */
    private boolean wantPositivePrefix;

    /**
     * {@code true} if colors are allowed for an output on X3.64 compatible terminal.
     */
    private boolean colorEnabled;

    /**
     * Creates a new formatter writing parameters to the default output stream.
     */
    public ParameterWriter() {
        this(standardOutput());
    }

    /**
     * Work around for RFE #4093999 in Sun's bug database
     * ("Relax constraint on placement of this()/super() call in constructors").
     */
    private static Writer standardOutput() {
        final Console console = System.console();
        if (console != null) {
            return console.writer();
        } else {
            return new OutputStreamWriter(System.out);
        }
    }

    /**
     * Creates a new formatter writing parameters to the specified output writer.
     *
     * @param out Where to write the parameters.
     */
    public ParameterWriter(final Writer out) {
        super(out);
    }

    /**
     * Prints the elements of an operation to the default output stream.
     * This is a convenience method for:
     *
     * {@preformat java
     *     new ParameterWriter().format(operation)
     * }
     *
     * @param operation The operation for which to write the parameters.
     */
    public static void print(final OperationMethod operation) {
        final ParameterWriter writer = new ParameterWriter();
        try {
            writer.format(operation);
        } catch (IOException exception) {
            // Should never happen, since we are writing to System.out.
            throw new AssertionError(exception);
        }
    }

    /**
     * Prints the elements of a descriptor group to the output stream.
     * This is a convenience method for:
     *
     * {@preformat java
     *     new ParameterWriter().format(descriptor)
     * }
     *
     * @param descriptor The parameter descriptor to write.
     */
    public static void print(final ParameterDescriptorGroup descriptor) {
        final ParameterWriter writer = new ParameterWriter();
        try {
            writer.format(descriptor);
        } catch (IOException exception) {
            // Should never happen, since we are writing to System.out.
            throw new AssertionError(exception);
        }
    }

    /**
     * Prints the elements of a parameter group to the default output stream.
     * This is a convenience method for:
     *
     * {@preformat java
     *     new ParameterWriter().format(value)
     * }
     *
     * @param values The parameter values to write.
     */
    public static void print(final ParameterValueGroup values) {
        final ParameterWriter writer = new ParameterWriter();
        try {
            writer.format(values);
        } catch (IOException exception) {
            // Should never happen, since we are writing to System.out.
            throw new AssertionError(exception);
        }
    }

    /**
     * Prints the elements of an operation to the output stream.
     *
     * @param  operation The operation method to format.
     * @throws IOException if an error occurred will writing to the stream.
     */
    public void format(final OperationMethod operation) throws IOException {
        synchronized (lock) {
            format(operation.getName().getCode(), operation.getParameters(), null);
        }
    }

    /**
     * Prints the elements of a descriptor group to the output stream.
     *
     * @param  descriptor The descriptor group to format.
     * @throws IOException if an error occurred will writing to the stream.
     */
    public void format(final ParameterDescriptorGroup descriptor) throws IOException {
        synchronized (lock) {
            format(descriptor.getName().getCode(), descriptor, null);
        }
    }

    /**
     * Prints the elements of a parameter group to the output stream.
     *
     * @param  values The parameter group to format.
     * @throws IOException if an error occurred will writing to the stream.
     */
    public void format(final ParameterValueGroup values) throws IOException {
        final ParameterDescriptorGroup descriptor = values.getDescriptor();
        synchronized (lock) {
            format(descriptor.getName().getCode(), descriptor, values);
        }
    }

    /**
     * Implementation of public {@code format} methods.
     *
     * @param  name The group name, usually {@code descriptor.getCode().getName()}.
     * @param  descriptor The parameter descriptor. Should be equal to
     *         {@code values.getDescriptor()} if {@code values} is non null.
     * @param  values The parameter values, or {@code null} if none.
     * @throws IOException if an error occurred will writing to the stream.
     */
    private void format(final String name, final ParameterDescriptorGroup group,
                        final ParameterValueGroup values) throws IOException
    {
        /*
         * Gets the constants that are going to be used in the whole method. We get them as final
         * local constants as a safety for protecting them from unintented changes. Then writes
         * the operation name and its aliases. Those names are formatted before the table.
         */
        final Writer  out           = this.out;
        final Locale  locale        = this.locale;
        final boolean colorEnabled  = this.colorEnabled;
        final String  lineSeparator = System.getProperty("line.separator", "\n");
        final Vocabulary resources  = Vocabulary.getResources(locale);
        new ParameterTableRow(group, locale, null).write(out, colorEnabled, false, lineSeparator);
        out.write(lineSeparator);
        /*
         * Formats the table header (i.e. the column names).
         */
        char horizontalLine = TableWriter.DOUBLE_HORIZONTAL_LINE;
        final TableWriter table = new TableWriter(out);
        table.setMultiLinesCells(true);
        table.nextLine(horizontalLine);
header: for (int i=0; ; i++) {
            boolean eol = false;
            final int key;
            switch (i) {
                case 0: key = Vocabulary.Keys.NAME;    break;
                case 1: key = Vocabulary.Keys.TYPE;    break;
                case 2: key = Vocabulary.Keys.MINIMUM; break;
                case 3: key = Vocabulary.Keys.MAXIMUM; break;
                case 4: key = (values == null) ? Vocabulary.Keys.DEFAULT : Vocabulary.Keys.VALUE; break;
                case 5: key = Vocabulary.Keys.UNITS; eol = true; break;
                default: break header;
            }
            if (colorEnabled) table.write(X364.BOLD.sequence());
            table.write(resources.getString(key));
            if (colorEnabled) table.write(X364.NORMAL.sequence());
            if (eol) table.nextLine();
            else table.nextColumn();
        }
        /*
         * Prepares the informations to be printed later as table rows. We scan all rows before
         * to print them in order to compute the width of authority names. During this process,
         * we split the objects to be printed later in two collections: simple parameters are
         * stored as (descriptor,value) pairs, while groups are stored in an other collection
         * for deferred printing after the simple parameters.
         */
        int authorityLength = 0;
        final Collection<?> elements = (values != null) ? values.values() : group.descriptors();
        final Map<GeneralParameterDescriptor,ParameterTableRow> descriptorValues =
                new LinkedHashMap<GeneralParameterDescriptor,ParameterTableRow>(
                hashMapCapacity(elements.size()));
        List<Object> deferredGroups = null; // To be created only if needed (it is usually not).
        for (final Object element : elements) {
            final GeneralParameterValue parameter;
            final GeneralParameterDescriptor descriptor;
            if (values != null) {
                parameter  = (GeneralParameterValue) element;
                descriptor = parameter.getDescriptor();
            } else {
                parameter  = null;
                descriptor = (GeneralParameterDescriptor) element;
            }
            if (descriptor instanceof ParameterDescriptorGroup) {
                if (deferredGroups == null) {
                    deferredGroups = new ArrayList<Object>();
                }
                deferredGroups.add(element);
                continue;
            }
            /*
             * In the vast majority of cases, there is only one value for each parameter. However
             * if we find more than one value, we will append all extra occurences in a "multiple
             * values" list to be formatted in the same row.
             */
            Object value = null;
            if (parameter instanceof ParameterValue<?>) {
                value = ((ParameterValue<?>) parameter).getValue();
            } else if (descriptor instanceof ParameterDescriptor<?>) {
                value = ((ParameterDescriptor<?>) descriptor).getDefaultValue();
            }
            ParameterTableRow row = descriptorValues.get(descriptor);
            if (row == null) {
                row = new ParameterTableRow(descriptor, locale, value);
                descriptorValues.put(descriptor, row);
            } else {
                row.addValue(value);
            }
            if (row.width > authorityLength) {
                authorityLength = row.width;
            }
        }
        /*
         * Now process to the formatting of (descriptor,value) pairs. Each descriptor alias
         * will be formatted on its own line in a table row. If there is more than one value,
         * then each value will be formatted on its own line as well. Note that the values may
         * be null if there is none.
         */
        UnitFormat unitFormat = null;
        final Object[] singleton = new Object[1];
        final Double POSITIVE_INFINITY = Double.POSITIVE_INFINITY; // Auto-boxing
        final Double NEGATIVE_INFINITY = Double.NEGATIVE_INFINITY; // Auto-boxing
        for (final Map.Entry<GeneralParameterDescriptor,ParameterTableRow> entry : descriptorValues.entrySet()) {
            table.nextLine(horizontalLine);
            horizontalLine = TableWriter.SINGLE_HORIZONTAL_LINE;
            final ParameterTableRow row = entry.getValue();
            row.width = authorityLength;
            row.write(table, false, colorEnabled, lineSeparator);
            table.nextColumn();
            final GeneralParameterDescriptor generalDescriptor = entry.getKey();
            if (generalDescriptor instanceof ParameterDescriptor<?>) {
                /*
                 * Writes value type.
                 */
                final ParameterDescriptor<?> descriptor = (ParameterDescriptor<?>) generalDescriptor;
                final Class<?> valueClass = descriptor.getValueClass();
                table.write(Classes.getShortName(valueClass));
                table.nextColumn();
                /*
                 * Writes minimum and maximum values.
                 */
                Object  minimum  = descriptor.getMinimumValue();
                Object  maximum  = descriptor.getMaximumValue();
                boolean negative = false;
                final boolean isNumber = Number.class.isAssignableFrom(valueClass);
                if (isNumber) {
                    table.setAlignment(TableWriter.ALIGN_RIGHT);
                    if (minimum == null) minimum = NEGATIVE_INFINITY;
                    if (maximum == null) maximum = POSITIVE_INFINITY;
                    negative = ((Number) minimum).doubleValue() < 0;
                }
                if (minimum != null) {
                    table.write(formatValue(minimum));
                }
                table.nextColumn();
                wantPositivePrefix = negative;
                if (maximum != null) {
                    table.write(formatValue(maximum));
                }
                wantPositivePrefix = false;
                table.nextColumn();
                /*
                 * Wraps the value in an array. Because it may be an array of primitive type,
                 * we can't cast to Object[]. Then, each array's element will be formatted on
                 * its own line.
                 */
                final Object array = row.values(singleton);
                final int length = Array.getLength(array);
                for (int i=0; i<length; i++) {
                    final Object value = Array.get(array, i);
                    if (value != null) {
                        if (i != 0) {
                            table.write(lineSeparator);
                        }
                        table.write(formatValue(value));
                    }
                }
                table.nextColumn();
                table.setAlignment(TableWriter.ALIGN_LEFT);
                final Unit<?> unit = descriptor.getUnit();
                if (unit != null) {
                    if (unitFormat == null) {
                        unitFormat = UnitFormat.getInstance(locale);
                    }
                    table.write(unitFormat.format(unit));
                }
            }
            table.nextLine();
        }
        table.nextLine(TableWriter.DOUBLE_HORIZONTAL_LINE);
        table.flush();
        /*
         * Now formats all groups deferred to the end of this table, with recursive calls to
         * this method (recursive calls use their own TableWriter instance, so they may result
         * in a different cell layout). Most of the time, there is no such additional group.
         */
        if (deferredGroups != null) {
            for (final Object element : deferredGroups) {
                final ParameterValueGroup value;
                final ParameterDescriptorGroup descriptor;
                if (element instanceof ParameterValueGroup) {
                    value = (ParameterValueGroup) element;
                    descriptor = value.getDescriptor();
                } else {
                    value = null;
                    descriptor = (ParameterDescriptorGroup) element;
                }
                out.write(lineSeparator);
                format(name + '/' + descriptor.getName().getCode(), descriptor, value);
            }
        }
    }

    /**
     * Formats a summary of a collection of {@linkplain IdentifiedObject identified objects}.
     * The objects may be parameters, available map projections, CRS, <i>etc.</i>
     * The summary contains the identifier name and alias aligned in a table.
     * <p>
     * The table formatted by default may be quite large. It is recommended to invoke
     * {@link #setAuthorities} before this method in order to reduce the amount of columns
     * to display.
     *
     * @param  objects The collection of objects to format.
     * @throws IOException if an error occurred will writing to the stream.
     */
    public void summary(final Collection<? extends IdentifiedObject> objects) throws IOException {
        // synchronized(lock) performed later in this method.
        /*
         * Prepares all rows before we write them to the output stream, because not all
         * identified objects may have names with the same scopes in the same order. We
         * also need to iterate over all rows in order to know the number of columns.
         *
         * The two first columns are treated especially. The first one is the optional
         * EPSG code. The second one is the main identifier (usually the OGC name). We
         * put SHOW_EPSG_CODE and null as special values for their column names, to be
         * replaced later by "EPSG" and "Identifier" in user locale. We can not put the
         * localized strings in the map right now because they could conflict with the
         * scope of some alias to be processed below.
         */
        final Map<Object,Integer> header = new LinkedHashMap<Object,Integer>();
        final List<String[]>        rows = new ArrayList<String[]>();
        final List<String>     epsgNames = new ArrayList<String>();
        final Locale              locale = getLocale();
        final Set<String>         scopes = getAuthorities();
        final Vocabulary       resources = Vocabulary.getResources(locale);
        final int          showEpsgCodes = ((scopes == null) || scopes.contains(SHOW_EPSG_CODES)) ? 1 : 0;
        if (showEpsgCodes != 0) {
            header.put(SHOW_EPSG_CODES, 0);
        }
        header.put(null, showEpsgCodes); // See above comment for the meaning of "null" here.
        for (final IdentifiedObject element : objects) {
            /*
             * Prepares a row: puts the name in the "identifier" column, which is the
             * first or the second one depending if we display EPSG codes or not.
             */
            String epsgName = null;
            String[] row = new String[header.size()];
            row[showEpsgCodes] = element.getName().getCode();
            int numUnscoped = 0;
            final Collection<GenericName> aliases = element.getAlias();
            if (aliases != null) {
                /*
                 * Adds alias (without scope) to the row. Each alias will be put in the column
                 * appropriate for its scope. If a name has no scope, we will create one using
                 * sequential number ("numUnscoped" is the count of such names without scope).
                 */
                for (final GenericName alias : aliases) {
                    final GenericName scope = alias.scope().name();
                    final String name = alias.tip().toInternationalString().toString(locale);
                    final Object columnName;
                    if (scope != null) {
                        columnName = scope.toInternationalString().toString(locale);
                    } else {
                        columnName = ++numUnscoped;
                    }
                    if (columnName.equals("EPSG")) {
                        epsgName = name;
                    }
                    if (scopes!=null && !scopes.contains(scope.toString())) {
                        /*
                         * The user requested only for a few authorities and the current alias
                         * is not a member of this subset. Continue the search to other alias.
                         */
                        continue;
                    }
                    /*
                     * Now stores the alias name at the position we just determined above. If
                     * more than one value are assigned to the same column, keep the first one.
                     */
                    row = putIfAbsent(row, getColumnIndex(header, columnName), name);
                }
            }
            /*
             * After the aliases, search for the identifiers. The code in this block is similar
             * to the one we just did for aliases. By doing this operation after the aliases we
             * ensure that if both an identifier and a name is defined for the same column, the
             * name is given precedence.
             */
            final Collection<ReferenceIdentifier> identifiers = element.getIdentifiers();
            if (identifiers != null) {
                for (final ReferenceIdentifier identifier : identifiers) {
                    final String scope = identifier.getCodeSpace();
                    final String name = identifier.getCode();
                    final Object columnName = (scope != null) ? scope : ++numUnscoped;
                    int columnIndex;
                    if (showEpsgCodes != 0 && columnName.equals("EPSG")) {
                        columnIndex = 0;
                    } else {
                        if (scopes!=null && !scopes.contains(scope)) {
                            continue;
                        }
                        columnIndex = getColumnIndex(header, columnName);
                    }
                    row = putIfAbsent(row, columnIndex, name);
                }
            }
            rows.add(row);
            epsgNames.add(epsgName);
        }
        /*
         * Writes the table. The header will contains one column for each alias's scope
         * (or authority) declared in 'titles', in the same order. The column for Geotk
         * names will treated especially, because cit ontains ambiguous names.
         */
        synchronized (lock) {
            final TableWriter table = new TableWriter(out, TableWriter.SINGLE_VERTICAL_LINE);
            table.setMultiLinesCells(true);
            table.writeHorizontalSeparator();
            /*
             * Writes all column headers.
             */
            int column = 0;
            int geotoolkitColumn = -1;
            for (final Object element : header.keySet()) {
                String title;
                if (element == null) {
                    title = resources.getString(Vocabulary.Keys.IDENTIFIER);
                } else if (element == SHOW_EPSG_CODES) {
                    title = "EPSG";
                } else if (element instanceof String) {
                    title = (String) element;
                    if (title.equalsIgnoreCase("geotk") ||
                        title.equalsIgnoreCase("Geotoolkit.org") ||
                        title.equalsIgnoreCase("Geotoolkit")) // Legacy
                    {
                        geotoolkitColumn = column;
                        title = resources.getString(Vocabulary.Keys.DESCRIPTION);
                    }
                } else { // Should be a Number
                    title = resources.getString(Vocabulary.Keys.ALIAS) + ' ' + element;
                }
                if (colorEnabled) {
                    title = X364.BOLD.sequence() + title + X364.NORMAL.sequence();
                }
                table.write(title);
                table.nextColumn();
                column++;
            }
            table.writeHorizontalSeparator();
            /*
             * Writes all rows.
             */
            final int numRows    = rows.size();
            final int numColumns = header.size();
            for (int rowIndex=0; rowIndex<numRows; rowIndex++) {
                final String[] aliases = rows.get(rowIndex);
                for (column=0; column<numColumns; column++) {
                    if (column < aliases.length) {
                        String alias = aliases[column];
                        if (column == geotoolkitColumn) {
                            if (alias == null) {
                                alias = epsgNames.get(rowIndex);
                            } else if (colorEnabled) {
                                if (!alias.equals(aliases[showEpsgCodes])) {
                                    alias = X364.FAINT.sequence() + alias + X364.NORMAL.sequence();
                                }
                            }
                        }
                        if (alias != null) {
                            table.write(alias);
                        }
                    }
                    table.nextColumn();
                }
                table.nextLine();
            }
            table.writeHorizontalSeparator();
            table.flush();
        }
    }

    /**
     * Returns the index of the column of the given name. If no such column
     * exists, then a new column is appended at the right of the table.
     */
    private static int getColumnIndex(final Map<Object,Integer> header, final Object columnName) {
        Integer position = header.get(columnName);
        if (position == null) {
            position = header.size();
            header.put(columnName, position);
        }
        return position;
    }

    /**
     * Stores a value at the given position in the given row, expanding the array if needed.
     * This operation is performed only if no value already exists at the given index.
     */
    private static String[] putIfAbsent(String[] row, final int columnIndex, final String name) {
        if (columnIndex >= row.length) {
            row = Arrays.copyOf(row, columnIndex+1);
        }
        if (row[columnIndex] == null) {
            row[columnIndex] = name;
        }
        return row;
    }

    /**
     * Returns the list of authorities to filter, or {@code null} if there is no
     * restriction. If non-null, only {@linkplain IdentifiedObject#getName name}
     * or {@linkplain IdentifiedObject#getAlias alias} of those authorities will
     * be displayed. The default value is {@code null}.
     *
     * @return The authorities to filter, or {@code null} if no restriction.
     *
     * @since 3.00
     */
    public Set<String> getAuthorities() {
        synchronized (lock) {
            return scopes;
        }
    }

    /**
     * Sets the list of authorities to filter, or {@code null} for accepting all of them.
     * The strings are authority names like {@code "OGC"}, {@code "EPSG"}, {@code "ESRI"}
     * or {@code "GeoTIFF"}. A few strings are treated especially:
     * <p>
     * <ul>
     *   <li>{@code "EPSG:#"} displays EPSG codes in the first column.</li>
     *   <li>{@code "Geotoolkit.org"} or {@code "Geotk"} displays Geotk name if available, or
     *       EPSG names otherwise, in a "Description" column. This is called "description"
     *       because the Geotk names are ambiguous and should not be used as identifiers.</li>
     * </ul>
     *
     * @param authorities The authorities to filter, or {@code null} for accepting all of them.
     *
     * @since 3.00
     */
    public void setAuthorities(String... authorities) {
        Set<String> copy = null;
        if (authorities != null) {
            copy = Collections.unmodifiableSet(new LinkedHashSet<String>(Arrays.asList(authorities)));
        }
        synchronized (lock) {
            scopes = copy;
        }
    }

    /**
     * Returns {@code true} if this writer is allowed to send color instructions for
     * {@linkplain X364 X3.64} compatible terminal. The default value is {@code false}.
     *
     * @return {@code true} if this writer is allowed to send X3.64 sequences.
     *
     * @since 3.00
     */
    public boolean isColorEnabled() {
        synchronized (lock) {
            return colorEnabled;
        }
    }

    /**
     * Sets whatever this writer is allowed to send color instructions for {@linkplain X364 X3.64}
     * compatible terminal. This is used for example in order to emphase the identifier in a list
     * of alias. The default value is {@code false}.
     *
     * @param enabled {@code true} to allow this writer to send X3.64 sequences.
     *
     * @since 3.00
     */
    public void setColorEnabled(final boolean enabled) {
        synchronized (lock) {
            colorEnabled = enabled;
        }
    }

    /**
     * Returns the current locale. Newly constructed {@code ParameterWriter}
     * use the {@linkplain Locale#getDefault system default}.
     *
     * @return The current locale.
     */
    @Override
    public Locale getLocale() {
        synchronized (lock) {
            return locale;
        }
    }

    /**
     * Sets the locale to use for table formatting.
     *
     * @param locale The new locale to use.
     */
    public void setLocale(final Locale locale) {
        synchronized (lock) {
            this.locale  = locale;
            numberFormat = null;
            dateFormat   = null;
            angleFormat  = null;
        }
    }

    /**
     * Formats the specified value as a string. This method is automatically invoked by
     * {@code format(...)} methods. The default implementation format {@link Number},
     * {@link Date} and {@link Angle} object according the {@linkplain #getLocale current locale}.
     * This method can been overridden if more objects need to be formatted in a special way.
     *
     * @param  value the value to format.
     * @return The value formatted as a string.
     */
    protected String formatValue(final Object value) {
        if (value instanceof Number) {
            if (numberFormat == null) {
                numberFormat = NumberFormat.getNumberInstance(locale);
                canSetPositivePrefix = false;
                if (numberFormat instanceof DecimalFormat) {
                    final DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
                    String prefix = decimalFormat.getPositivePrefix();
                    if (prefix != null && prefix.length() == 0) {
                        prefix = decimalFormat.getNegativePrefix();
                        if ("-".equals(prefix)) {
                            canSetPositivePrefix = true;
                        }
                    }
                }
            }
            if (canSetPositivePrefix) {
                ((DecimalFormat) numberFormat).setPositivePrefix(wantPositivePrefix ? "+" : "");
            }
            return numberFormat.format(value);
        }
        if (value instanceof Date) {
            if (dateFormat == null) {
                dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
            }
            return dateFormat.format(value);
        }
        if (value instanceof Angle) {
            if (angleFormat == null) {
                angleFormat = AngleFormat.getInstance(locale);
            }
            return angleFormat.format(value);
        }
        return String.valueOf(value);
    }
}
