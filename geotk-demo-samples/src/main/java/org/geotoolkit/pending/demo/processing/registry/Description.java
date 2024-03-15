package org.geotoolkit.pending.demo.processing.registry;

import java.util.Locale;
import java.util.ResourceBundle;
import org.apache.sis.util.ResourceInternationalString;

/**
 * An abstract or comment that can be expressed in different languages.
 */
final class Description extends ResourceInternationalString {
    Description(String key) {
        super(key);
    }

    @Override
    protected ResourceBundle getBundle(final Locale locale) {
        return ResourceBundle.getBundle("org.geotoolkit.pending.demo.processing.bundle", locale);
    }
}
