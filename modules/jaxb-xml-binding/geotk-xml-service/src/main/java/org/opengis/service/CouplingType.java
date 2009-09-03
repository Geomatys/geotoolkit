


package org.opengis.service;

import java.util.ArrayList;
import java.util.List;
import org.opengis.annotation.UML;
import org.opengis.util.CodeList;
import static org.opengis.annotation.Obligation.*;
import static org.opengis.annotation.Specification.*;


//@UML(identifier="SV_CouplingType", specification=ISO_19119)
public class CouplingType  extends CodeList<CouplingType> {
    
    /**
     * List of all enumerations of this type.
     * Must be declared before any enum declaration.
     */
    private static final List<CouplingType> VALUES = new ArrayList<CouplingType>(3);

    /**
     * service instance is loosely coupled with a data instance,
     * i.e. no MD_DataIdentification class has to be described
     */
    //@UML(identifier="loose", obligation=CONDITIONAL, specification=ISO_19119)
    public static final CouplingType LOOSE = new CouplingType("LOOSE");

    /**
     *service instance is tightly coupled with a data instance,
     * i.e. MD_DataIdentification class MUST be described
     */
    //@UML(identifier="tight", obligation=CONDITIONAL, specification=ISO_19119)
    public static final CouplingType TIGHT = new CouplingType("TIGHT");

    /**
     * service instance is mixed coupled with a data instance,
     * i.e. MD_DataIdentification describes the associated data
     * instance and additionally the service instance might work
     * with other external data instances
     */
    //@UML(identifier="mixed", obligation=CONDITIONAL, specification=ISO_19119)
    public static final CouplingType MIXED = new CouplingType("MIXED");
    
    /**
     * Constructs an enum with the given name. The new enum is
     * automatically added to the list returned by {@link #values}.
     *
     * @param name The enum name. This name must not be in use by an other enum of this type.
     */
    private CouplingType(final String name) {
        super(name, VALUES);
    }

    /**
     * Returns the list of {@code CouplingType}s.
     */
    public static CouplingType[] values() {
        synchronized (VALUES) {
            return VALUES.toArray(new CouplingType[VALUES.size()]);
        }
    }

    /**
     * Returns the list of enumerations of the same kind than this enum.
     */
    public CouplingType[] family() {
        return values();
    }

    /**
     * Returns the CouplingType that matches the given string, or returns a
     * new one if none match it.
     */
    public static CouplingType valueOf(String code) {
        return valueOf(CouplingType.class, code);
    }
    
}
