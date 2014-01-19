/*
 * Copyright 2014 Theo Willows
 *
 * This file is part of JArgP.
 *
 * JArgP is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * JArgP is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JArgP.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.munkei;

import com.munkei.exception.ArgumentParsingException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation to use for command line options.
 * <p>
 * All elements are optional. Thus the simplest usage is:
 * <pre>
 * {@literal @}CommandLineOption<br/>
 * private Boolean verbose;
 * </pre> The above will match the option
 * <code>--verbose</code> when parsing
 * ({@link JArgP#parse(java.lang.String[])}), and if it is present, the field
 * <code>verbose</code> (of the "subject", see
 * {@link JArgP#JArgP(java.lang.Object)}) will be set to
 * <code>true</code>.
 * <p>
 * If you want the command line option to use the next argument as a value for a
 * field, make the field something other than a {@link Boolean}. Values are
 * converted in {@link Option#convert(java.lang.String, java.lang.Class)}, see
 * its documentation for details.
 * <p>
 * To configure the specifics of the command line option and how it is parsed,
 * see each element:
 * <ul>
 * <li>To specify which command line options match a field, see {@link #names()}
 * and {@link #pattern()}.</li>
 * <li>To specify how the documentation for the field is printed
 * ({@link JArgP#printUsage(java.io.PrintStream)}), see
 * {@link #placeholder()}, {@link #description()}, {@link #defaultValue()} and
 * {@link #defaultValue()}.</li>
 * </ul>
 *
 * @see JArgP
 *
 * @author Theo 'Biffen' Willows <theo@willows.se>
 *
 * @since 0.0.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CommandLineOption {

  /**
   * The names of the option. These names will be accepted as options when
   * parsing.
   * <p>
   * If no names are specified (and no {@link #pattern()} is specified), the
   * name of the field itself is used.
   * <p>
   * Single character names will be short options (
   * <code>-[...]</code>), others will be long options (
   * <code>--[...]</code>), with the exception of
   * <code>-</code>, which will be just that.
   *
   * @see #pattern()
   */
  String[] names() default {};

  /**
   * Specifies a method to set the value. Must take either two {@link String}
   * parameters, where the first one is the name and the second one is the
   * value:
   * <pre>&lt;setter&gt;({@link String} name, {@link String} value)</pre> or a
   * single {@link String} parameter that is the value:
   * <pre>&lt;setter&gt;({@link String} value)</pre>
   * <p>
   * For multiples ({@link java.util.Collection}s) it is assumed the setter
   * appends a single value.
   * <p>
   * One reason to use a setter would be to do some kind of validation of the
   * value. You <em>may</em> throw an exception in the setter if the value is
   * considered invalid. E.g. an {@link ArgumentParsingException}, but any type
   * can be used, just make sure to catch it wherever you call
   * {@link JArgP#parse(java.lang.String[])}.
   * <p>
   * If not used, members will be set directly.
   */
  String setter() default "";

  /**
   * For complex names match this pattern. Patterns will be matched
   * <em>after</em> names and aliases.
   * <p>
   * E.g:
   * <code>[0-9]+</code> for options like
   * <code>-1</code>,
   * <code>-2</code>, etc.
   *
   * @see #shortPattern()
   *
   * @see #names()
   */
  String pattern() default "";

  /**
   * For complex names match this pattern. <code>shortPattern</code>s are matched against short options (<code>--[...]</code>). Patterns will be matched
   * <em>after</em> names and aliases.
   * <p>
   * E.g:
   * <code>[0-9]+</code> for options like
   * <code>-1</code>,
   * <code>-2</code>, etc.
   *
   * @see #pattern()
   */
  String shortPattern() default "";

  /**
   * Creates a
   * <code>--no-[...]</code> alias that sets it to
   * <code>false</code>.
   * <p>
   * <em>Switches only.</em>
   */
  boolean opposite() default false;

  /**
   * Text to use as a placeholder when printing the usage.
   * <p>
   * E.g. for an option called
   * <code>file</code> one might set the placeholder to "FILE", to get a usage
   * like:
   * <pre>--file FILE</pre>
   * <p>
   * The default is "VALUE".
   * <p>
   * <em>Only used for options that take a value, i.e. not
   * {@link Boolean}s.</em>
   *
   * @see JArgP#printUsage(java.io.PrintStream)
   */
  String placeholder() default "VALUE";

  /**
   * A description of the option to use for the usage text.
   *
   * @see JArgP#printUsage(java.io.PrintStream)
   */
  String description() default "";

  /**
   * A default value. Will be documented in the usage text. Will be set by
   * {@link JArgP#parse(java.lang.String[])} to any member that has not been set
   * by the arguments.
   *
   * @see JArgP#printUsage(java.io.PrintStream)
   */
  String defaultValue() default "";

}
