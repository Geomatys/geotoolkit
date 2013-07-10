/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.gui.swing.referencing;

import java.util.Set;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import javax.swing.AbstractListModel;

import org.opengis.util.FactoryException;
import org.opengis.referencing.AuthorityFactory;
import org.opengis.referencing.IdentifiedObject;

import org.apache.sis.util.logging.Logging;
import org.geotoolkit.internal.swing.FastComboBox;


/**
 * A list of {@link AuthorityCode}s. This implementation will try to fetch the codes only
 * when first needed. Keep in mind that the collection provided to the constructor may be
 * database backed (not a usual implementation from {@code java.util} package), so it is
 * worth to do lazy loading here.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.12
 *
 * @since 2.3
 * @module
 */
@SuppressWarnings("serial") // Actually not serializable because AuthorityCode is not.
final class AuthorityCodeList extends AbstractListModel<AuthorityCode> implements FastComboBox.Model<AuthorityCode> {
    /**
     * The authority codes as {@link AuthorityCode} objects.
     */
    private AuthorityCode[] codes;

    /**
     * The selected item, or {@code null} if none.
     */
    private AuthorityCode selected;

    /**
     * The code of the selected element. This is used when the selected element may
     * not yet be built by the background thread. Otherwise this is {@code null}.
     */
    private transient String selectedCode;

    /**
     * The number of elements in this list.
     */
    private int size;

    /**
     * Information needed for refreshing the list of authority codes
     * while we load them in a background thread.
     */
    private static final class Step {
        final AuthorityCode[] codes;
        final int size;

        Step(final AuthorityCode[] codes, final int size) {
            this.codes = codes;
            this.size  = size;
        }
    }

    /**
     * Creates a list for the given codes.
     *
     * @param The locale for formatting the code descriptions.
     * @param factory The factory to use for fetching the codes.
     * @param type Base classes of CRS objects to extract.
     */
    @SafeVarargs
    public AuthorityCodeList(final Locale locale, final AuthorityFactory factory,
            final Class<? extends IdentifiedObject>... types)
    {
        new SwingWorker<Step,Step>() {
            /**
             * Gets the authority code in a background thread. Note that the iterator
             * returned by factory.getAuthorityCode(type) may be backed by a JCBC ResultSet.
             */
            @Override
            protected Step doInBackground() throws FactoryException {
                int count = 0;
                AuthorityCode[] codes = new AuthorityCode[256];
                for (final Class<? extends IdentifiedObject> type : types) {
                    final Set<String> ic = factory.getAuthorityCodes(type);
                    // Don't invoke 'ic.size()' because it may be costly.
                    for (final String code : ic) {
                        if (count == codes.length) {
                            codes = Arrays.copyOf(codes, count*2);
                        }
                        codes[count] = new AuthorityCode(factory, code, count, locale);
                        if ((++count & 0xFF) == 0) { // Report progress.
                            publish(new Step(codes, count));
                        }
                    }
                }
                return new Step(codes, count);
            }

            /**
             * Invoked in the Swing thread when new codes have been added in the list
             * by the background thread.
             */
            private void process(final Step step) {
                final int lower = AuthorityCodeList.this.size;
                final int upper = step.size - 1;
                AuthorityCodeList.this.codes = step.codes;
                AuthorityCodeList.this.size  = step.size;
                fireIntervalAdded(AuthorityCodeList.this, lower, upper);
            }

            /**
             * Invoked in the Swing thread when new codes have been added in the list
             * by the background thread. Only the last element from the chunk is used,
             * on the assumption that it is the most recent.
             */
            @Override
            protected void process(final List<Step> chunk) {
                process(chunk.get(chunk.size() - 1));
            }

            /**
             * Invoked in the Swing thread when the background thread finished its work.
             * If case of failure, the list will be incomplete but the combox box will
             * otherwise works as expected.
             */
            @Override
            protected void done() {
                try {
                    process(get());
                } catch (InterruptedException e) {
                    // Probably a cancelation, so stop the process.
                    Logging.recoverableException(AuthorityCodeList.class, "<init>", e);
                } catch (ExecutionException e) {
                    Logging.unexpectedException(AuthorityCodeList.class, "<init>", e.getCause());
                }
                setSelectedCode(selectedCode);
            }
        }.execute();
    }

    /**
     * Returns the length of the list.
     */
    @Override
    public int getSize() {
        return size;
    }

    /**
     * Returns the value at the specified index.
     */
    @Override
    public AuthorityCode getElementAt(final int index) {
        return (index >= 0 && index < size) ? codes[index] : null;
    }

    /**
     * Returns the selected item.
     */
    @Override
    public AuthorityCode getSelectedItem() {
        return selected;
    }

    /**
     * Sets the selected item.
     */
    @Override
    public void setSelectedItem(final Object code) {
        if (!Objects.equals(code, selected)) {
            selected = (AuthorityCode) code;
            selectedCode = null;
            final int index = (selected != null) ? selected.index : -1;
            fireContentsChanged(this, index, index);
        }
    }

    /**
     * Sets the selected item to the {@code AuthorityCode} element inferred from
     * the given code. This method does nothing if the given code is null.
     */
    void setSelectedCode(final String code) {
        if (code != null) {
            final AuthorityCode[] codes = this.codes;
            final int size = this.size;
            for (int i=0; i<size; i++) {
                final AuthorityCode candidate = codes[i];
                if (code.equals(candidate.code)) {
                    setSelectedItem(candidate);
                    return;
                }
            }
        }
        // If the SwingWorker thread is still running, we will search
        // for the code when the worker will have finished its job.
        selected = null;
        selectedCode = code;
    }

    /**
     * Returns the index of the currently selected element, or -1 if none.
     */
    @Override
    public int getSelectedIndex() {
        return (selected != null) ? selected.index : -1;
    }
}
