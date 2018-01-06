package org.geotoolkit.processing.coverage.categorize;

import org.geotoolkit.processing.image.sampleclassifier.*;
import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.storage.coverage.CoverageResource;
import org.opengis.geometry.Envelope;
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
public class CategorizeDescriptor extends AbstractProcessDescriptor {

    /**
     * Handle on input data to categorize.
     */
    public static final ParameterDescriptor<CoverageResource> IN_COVERAGE;

    /**
     * Handle on output data source to write into.
     */
    public static final ParameterDescriptor<CoverageResource> OUT_COVERAGE;

    /**
     * An optional envelope region of interest to focus on for classification.
     */
    public static final ParameterDescriptor<Envelope> ENVELOPE;

    public static final ParameterDescriptor<Byte> FILL_VALUE = SampleClassifierDescriptor.FILL_VALUE;

    public static final ParameterDescriptorGroup CATEGORIES = SampleClassifierDescriptor.CATEGORIES;

    static final ParameterDescriptorGroup INPUT;
    static final ParameterDescriptorGroup OUTPUT;
    static {
        final ParameterBuilder builder = new ParameterBuilder();
        IN_COVERAGE = builder.addName("source")
                .setRemarks("Resource to classify.")
                .setRequired(true)
                .create(CoverageResource.class, null);

        OUT_COVERAGE = builder.addName("destination")
                .setRemarks("Resource to send classified data into.")
                .create(CoverageResource.class, null);

        ENVELOPE = builder.addName("envelope")
                .setRemarks("An envelope representing a subset of the source grid coverage to work with.")
                .setRequired(false)
                .create(Envelope.class, null);

        INPUT = builder.addName("input")
                .setRemarks("An image to classify, along with the definition of classes to apply.")
                .setRequired(true)
                .createGroup(IN_COVERAGE, OUT_COVERAGE, CATEGORIES, FILL_VALUE, ENVELOPE);

        OUTPUT = builder.addName("output")
                .setRemarks("Classified image.")
                .createGroup();
    }

    public CategorizeDescriptor() {
        super(
                new DefaultIdentifier("coverage:sample-classifier"),
                new SimpleInternationalString("Apply a categorization palette to an image."),
                new SimpleInternationalString("Classification of an image sample values"),
                INPUT, OUTPUT
        );
    }

    @Override
    public Process createProcess(final ParameterValueGroup inputs) {
        return new Categorize(this, inputs);
    }
}
