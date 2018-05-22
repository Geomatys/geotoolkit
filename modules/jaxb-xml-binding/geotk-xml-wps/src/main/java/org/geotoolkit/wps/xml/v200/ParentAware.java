package org.geotoolkit.wps.xml.v200;

import javax.xml.bind.Unmarshaller;
import org.apache.sis.util.ArgumentChecks;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
abstract class ParentAware<T> {

    final Class<T> parentType;

    T parent;

    ParentAware(final Class<T> parentType) {
        ArgumentChecks.ensureNonNull("Parent type is not defined", parentType);
        this.parentType = parentType;
    }

    protected T checkParent() {
        if (parent == null) {
            throw new IllegalStateException("Cannot fill "+getClass().getSimpleName()+", because the " + parentType.getSimpleName() + " parent has not been set beforehand (should be done via private JAXB strategy)");
        }
        return parent;
    }

    private void beforeUnmarshal(Unmarshaller u, Object parent) {
        if (parentType.isInstance(parent)) {
            this.parent = (T) parent;
        } else {
            throw new IllegalStateException("Invalid parent affectation. We need a " + parentType.getCanonicalName() + ", but got a " + (parent == null ? "null value" : parent.getClass()));
        }
    }
}
