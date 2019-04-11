package org.geotoolkit.coverage;

import java.util.List;
import java.util.ArrayList;
import java.awt.image.DataBuffer;

import org.opengis.util.CodeList;
import org.opengis.annotation.UML;

import static org.opengis.annotation.Obligation.*;
import static org.opengis.annotation.Specification.*;


/**
 * Specifies the various dimension types for coverage values.
 * For grid coverages, these correspond to band types.
 *
 * @deprecated This is a legacy type from OGC <a href="http://www.opengis.org/docs/01-004.pdf">Grid Coverages Implementation specification 1.0</a>.
 *
 * @author  Martin Desruisseaux (IRD)
 */
@Deprecated
public final class SampleDimensionType extends CodeList<SampleDimensionType> {
    /**
     * List of all enumerations of this type.
     * Must be declared before any enum declaration.
     */
    private static final List<SampleDimensionType> VALUES = new ArrayList<SampleDimensionType>(11);

    /**
     * Unsigned 1 bit integers.
     */
    @UML(identifier="CV_1BIT", obligation=CONDITIONAL, specification=OGC_01004)
    public static final SampleDimensionType UNSIGNED_1BIT = new SampleDimensionType("UNSIGNED_1BIT");

    /**
     * Unsigned 2 bits integers.
     */
    @UML(identifier="CV_2BIT", obligation=CONDITIONAL, specification=OGC_01004)
    public static final SampleDimensionType UNSIGNED_2BITS = new SampleDimensionType("UNSIGNED_2BITS");

    /**
     * Unsigned 4 bits integers.
     */
    @UML(identifier="CV_4BIT", obligation=CONDITIONAL, specification=OGC_01004)
    public static final SampleDimensionType UNSIGNED_4BITS = new SampleDimensionType("UNSIGNED_4BITS");

    /**
     * Unsigned 8 bits integers.
     *
     * @see #SIGNED_8BITS
     * @see DataBuffer#TYPE_BYTE
     */
    @UML(identifier="CV_8BIT_U", obligation=CONDITIONAL, specification=OGC_01004)
    public static final SampleDimensionType UNSIGNED_8BITS = new SampleDimensionType("UNSIGNED_8BITS");

    /**
     * Signed 8 bits integers.
     *
     * @see #UNSIGNED_8BITS
     */
    @UML(identifier="CV_8BIT_S", obligation=CONDITIONAL, specification=OGC_01004)
    public static final SampleDimensionType SIGNED_8BITS = new SampleDimensionType("SIGNED_8BITS");

    /**
     * Unsigned 16 bits integers.
     *
     * @see #SIGNED_16BITS
     * @see DataBuffer#TYPE_USHORT
     */
    @UML(identifier="CV_16BIT_U", obligation=CONDITIONAL, specification=OGC_01004)
    public static final SampleDimensionType UNSIGNED_16BITS = new SampleDimensionType("UNSIGNED_16BITS");

    /**
     * Signed 16 bits integers.
     *
     * @see #UNSIGNED_16BITS
     * @see DataBuffer#TYPE_SHORT
     */
    @UML(identifier="CV_16BIT_S", obligation=CONDITIONAL, specification=OGC_01004)
    public static final SampleDimensionType SIGNED_16BITS = new SampleDimensionType("SIGNED_16BITS");

    /**
     * Unsigned 32 bits integers.
     *
     * @see #SIGNED_32BITS
     */
    @UML(identifier="CV_32BIT_U", obligation=CONDITIONAL, specification=OGC_01004)
    public static final SampleDimensionType UNSIGNED_32BITS = new SampleDimensionType("UNSIGNED_32BITS");

    /**
     * Signed 32 bits integers.
     *
     * @see #UNSIGNED_32BITS
     * @see DataBuffer#TYPE_INT
     */
    @UML(identifier="CV_32BIT_S", obligation=CONDITIONAL, specification=OGC_01004)
    public static final SampleDimensionType SIGNED_32BITS = new SampleDimensionType("SIGNED_32BITS");

    /**
     * Simple precision floating point numbers.
     *
     * @see #REAL_64BITS
     * @see DataBuffer#TYPE_FLOAT
     */
    @UML(identifier="CV_32BIT_REAL", obligation=CONDITIONAL, specification=OGC_01004)
    public static final SampleDimensionType REAL_32BITS = new SampleDimensionType("REAL_32BITS");

    /**
     * Double precision floating point numbers.
     *
     * @see #REAL_32BITS
     * @see DataBuffer#TYPE_DOUBLE
     */
    @UML(identifier="CV_64BIT_REAL", obligation=CONDITIONAL, specification=OGC_01004)
    public static final SampleDimensionType REAL_64BITS = new SampleDimensionType("REAL_64BITS");

    /**
     * Constructs an element of the given name. The new element is
     * automatically added to the list returned by {@link #values()}.
     *
     * @param  name  the name of the new element.
     */
    private SampleDimensionType(final String name) {
        super(name, VALUES);
    }

    /**
     * Returns the list of codes of the same kind than this code list element.
     * Invoking this method is equivalent to invoking {@link #values()}, except that
     * this method can be invoked on an instance of the parent {@code CodeList} class.
     *
     * @return all code {@linkplain #values() values} for this code list.
     */
    @Override
    public SampleDimensionType[] family() {
        synchronized (VALUES) {
            return VALUES.toArray(new SampleDimensionType[VALUES.size()]);
        }
    }
}
