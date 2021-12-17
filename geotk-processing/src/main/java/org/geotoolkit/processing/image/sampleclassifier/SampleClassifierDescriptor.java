package org.geotoolkit.processing.image.sampleclassifier;

import java.awt.image.RenderedImage;
import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.util.SimpleInternationalString;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * A process whose aim is to classify a single banded image according to a given
 * set of sample value categories. A category is a range (interval) of values
 * associated to one class value. This process accepts as many categories as
 * wanted, and multiple categories can refer to the same class value. For values
 * which don't fit in any category, a fill value (specified as input) will be used.
 *
 * @implNote The only check about category validity is to ensure that at least
 * one is given to the process. We do not check any of those points:
 * <ul>
 * <li>That given category range is not empty</li>
 * <li>That two categories overlap. In fact, if a category overlap another one,
 * it will be the category with the highest lower bound which will be selected
 * in the conflicting value range</li>
 * </ul>
 * Also, for consistency and performance purpose, output class values will be
 * limited to byte value.
 *
 * Example : You've got an image whose samples are floats in range 0..100. You
 * define three categories:
 * <ol>
 * <li>range=0..50, class value=0</li>
 * <li>range=50..70, class value=1</li>
 * <li>range=70..100, class value=0</li>
 * </ol>
 *
 * In this case, we'll make an image whose pixels which were between 50 and 70 in
 * the input image will have the value 1 in the output, and all other will have
 * the value 0.
 *
 * @author Alexis Manin (Geomatys)
 */
public class SampleClassifierDescriptor extends AbstractProcessDescriptor {

    /**
     * The image parameter to give as input, or get back as output.
     */
    public static final ParameterDescriptor<RenderedImage> IMAGE;

    /**
     * Index of the band to operate upon.
     * TODO : activate back to allow working on a single band from a multi-band image.
     */
    //public static final ParameterDescriptor<Integer> BAND;

    /**
     * A descriptor to specify the minimal value of a category range.
     */
    public static final ParameterDescriptor<Float> MIN;
    /**
     * A descriptor to specify the maximal value of a category range.
     */
    public static final ParameterDescriptor<Float> MAX;
    /**
     * A descriptor to specify the class value associated to one category.
     */
    public static final ParameterDescriptor<Byte> CLASS_VALUE;
    /**
     * The fallback value to use when no category fit for a sample.
     */
    public static final ParameterDescriptor<Byte> FILL_VALUE;

    /**
     * The list of categories to use for classification. Note that you can give
     * them unordered.
     */
    public static final ParameterDescriptorGroup CATEGORIES;

    static final ParameterDescriptorGroup INPUT;
    static final ParameterDescriptorGroup OUTPUT;
    static {
        final ParameterBuilder builder = new ParameterBuilder();
        IMAGE = builder.addName("image")
                .setRemarks("Single-band image")
                .setRequired(true)
                .create(RenderedImage.class, null);

        // TODO : activate back to allow working on a single band from a multi-band image.
//        BAND = builder.addName("band")
//                .setRemarks("Index of the band to operate upon.")
//                .setRequired(true)
//                .createBounded(0, Integer.MAX_VALUE, 0);

        MIN = builder.addName("inclusive_min")
                .setRemarks("Minimal boundary of a classification range. Inclusive.")
                .create(Float.class, null);

        MAX = builder.addName("exclusive_max")
                .setRemarks("Maximal boundary of a classification range. Exclusive.")
                .create(Float.class, null);

        CLASS_VALUE = builder.addName("class_value")
                .setRemarks("Value representing a class.")
                .create(Byte.class, null);

        FILL_VALUE = builder.addName("unclassified_value")
                .setRemarks("Value representing a class.")
                .create(Byte.class, null);

        CATEGORIES = builder.addName("classes")
                .setRemarks("Definition of the categories to use for sample value classification")
                .createGroup(1, Integer.MAX_VALUE, MIN, MAX, CLASS_VALUE);

        INPUT = builder.addName("input")
                .setRemarks("An image to classify, along with the definition of classes to apply.")
                .createGroup(IMAGE, CATEGORIES, FILL_VALUE);

        OUTPUT = builder.addName("output")
                .setRemarks("Classified image.")
                .createGroup(IMAGE);
    }

    public SampleClassifierDescriptor() {
        super(
                new DefaultIdentifier("image:sample-classifier"),
                new SimpleInternationalString("Apply a categorization palette to an image."),
                new SimpleInternationalString("Classification of an image sample values"),
                INPUT, OUTPUT
        );
    }

    @Override
    public Process createProcess(final ParameterValueGroup inputs) {
        return new SampleClassifier(this, inputs);
    }
}
