/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2021, Geomatys
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
package org.geotoolkit.coverage;

import java.util.Comparator;
import java.util.Locale;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.coverage.Category;
import org.apache.sis.coverage.SampleDimension;


/**
 * A builder of {@link SampleDimension} with heuristic rules for determining the background value.
 *
 * <p>It is recommended to define the background value to enforce stable behavior upon associated data processing.
 * As a last resort, this sample dimension builder will try to identify an acceptable default background value by
 * searching for a qualitative category named <q>background</q>, <q>fill-value</q> or <q>no-data</q>.</p>
 *
 * <p>Ported from <a href="https://github.com/apache/sis/pull/24">SIS pull request #24</a></p>
 *
 * @author Alexis Manin (Geomatys)
 */
public class HeuristicSampleDimensionBuilder extends SampleDimension.Builder {
    /**
     * Creates a new builder.
     */
    public HeuristicSampleDimensionBuilder() {
    }

    /**
     * Try to define a background value if none has been defined by user.
     * Note that this is a best-effort. It analyzes available categories types and names. If it finds a very common
     * case in category naming/typing, it will promote the related category minimum value as background. Otherwise,
     * the background value is left unset.
     */
    @Override
    public SampleDimension build() {
        if (getBackground() == null) {
            final Logger logger = Logger.getLogger("org.apache.sis.coverage");
            try {
                Optional<BackgroundCandidate> first = categories().stream()
                        .map(this::score)
                        .filter(it -> it != null && it.score > 0)
                        .sorted(Comparator.comparing(BackgroundCandidate::getScore).reversed())
                        .findFirst();
                if (first.isPresent()) {
                    final BackgroundCandidate promoted = first.get();
                    logger.log(Level.FINE,
                            "No background value set by user. Defaulting to {}." +
                                    "Use `setBackground()` to short this automatic choice.",
                            promoted.category);
                    setBackground(promoted.category.getSampleRange().getMinValue());
                }
            } catch (RuntimeException e) {
                logger.log(Level.FINEST, "Defaulting background value failed", e);
            }
        }
        return super.build();
    }

    /**
     * Compute a score for a category. The score represent the chances for the category to be a good replacement for
     * an explicitly set background. The higher the score, the better. Note that any result with a 0 score should be
     * rejected.
     *
     * @param candidate A category to evaluate as a candidate for background value. Do not accept null values.
     * @return The input category with associated score. Can be null if input category is not a good candidate.
     */
    private BackgroundCandidate score(Category candidate) {
        if (candidate.isQuantitative() || candidate.getSampleRange().getMinValue() == null) {
            return null;
        }
        final String enName = candidate.getName()
                .toString(Locale.ENGLISH)
                .toLowerCase(Locale.ENGLISH);
        if (enName.equals("background")) return new BackgroundCandidate(1f, candidate);
        else if (enName.equals("fill-value") || enName.equals("fill_value") || enName.equals("fill")) return new BackgroundCandidate(0.9f, candidate);
        else if (enName.equals("no-data") || enName.equals("no_data") || enName.equals("no data")) return new BackgroundCandidate(0.8f, candidate);
        else if (enName.equals("missing-value") || enName.equals("missing_value") || enName.equals("missing value")) return new BackgroundCandidate(0.7f, candidate);
        else if (enName.equals("missing-data") || enName.equals("missing_data") || enName.equals("missing data")) return new BackgroundCandidate(0.6f, candidate);
        else return new BackgroundCandidate(0f, candidate);
    }

    /**
     * Model a category associated with a score that represent its probability
     * to be the best match for a background value.
     */
    private static final class BackgroundCandidate {
        final float score;
        final Category category;

        public BackgroundCandidate(float score, Category category) {
            this.score = score;
            this.category = category;
        }

        public float getScore() {
            return score;
        }
    }
}
