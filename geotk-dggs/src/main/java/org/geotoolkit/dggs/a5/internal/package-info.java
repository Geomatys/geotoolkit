/**
 * A5 DGGS implementation.
 *
 * Original code from https://github.com/felixpalmer/a5
 * Last update : tag v1.0.0, commit b912f5ead92b8aa2ae36a1d8d43537924a37404e (TypeScript sources)
 *
 * The java code is kept very close to the typescript both in terms of types, names, logic and doc.
 * This ensure updating the code does not require too much efforts.
 * Each sub-package matches an upstream {@code modules/} directory and each class an upstream file
 * ({@code traversal/grid-disk.ts} becomes {@code traversal.GridDisk}).
 *
 * Main type mappings :
 * <ul>
 *   <li>{@code bigint} cell identifiers and slots : {@code long} holding the unsigned 64 bits value,
 *       compared with {@link java.lang.Long#compareUnsigned(long, long)} and shifted with {@code >>>}.</li>
 *   <li>vectors, quaternions, matrices and coordinate tuples : {@code double[]}.</li>
 *   <li>{@code BigUint64Array} and cell lists : {@code long[]}.</li>
 * </ul>
 *
 * Tests reuse the upstream JSON fixtures, plus a deep-resolution cross-check fixture
 * generated with the a5-py port (see test resources {@code crosscheck/gen.py}).
 */
package org.geotoolkit.dggs.a5.internal;
