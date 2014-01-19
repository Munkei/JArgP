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

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.munkei.exception.ArgumentParsingException;
import com.munkei.exception.NoSuchOptionException;

/**
 *
 * @author Theo 'Biffen' Willows <theo@willows.se>
 *
 * @since 0.0.1
 */
public class JArgP {

  /**
   * Make a string (ANSI) bold.
   *
   * @param string The string.
   *
   * @return The bold <code>string</code>.
   */
  protected static String bold(String string) {
    boolean ansi = true; // TODO
    if (ansi) {
      return "\033[1m" + string + "\033[22m";
    }

    return string;
  }

  /**
   * Make a string (ANSI) underlined.
   *
   * @param string The string.
   *
   * @return The underlined <code>string</code>.
   */
  protected static String underline(String string) {
    boolean ansi = true; // TODO
    if (ansi) {
      return "\033[4m" + string + "\033[24m";
    }

    return string;
  }

  private List<Option> options;

  private String usageHeader;

  /**
   * Creates a {@link JArgP} object to use for parsing, etc.
   *
   * @param subject The {@link Object} that has the annotated members.
   *
   * @throws NullPointerException
   */
  public JArgP(Object subject)
    throws NullPointerException {
    if (subject == null) {
      throw new NullPointerException("Subject may not be null");
    }

    options = new ArrayList<>();

    for (Field field : subject.getClass().getFields()) {
      if (field.isAnnotationPresent(CommandLineOption.class)) {
        options.add(new Option(subject, field));
      }
    }
  }

  /**
   * Parses command line arguments and sets the values to the subject.
   *
   * @param args The command line arguments.
   *
   * @return A list of the remaining parameters (i.e. arguments that are not
   * options of values of options).
   *
   * @throws NullPointerException If <code>args</code> is <code>null</code>.
   *
   * @throws NoSuchOptionException If an option is encountered that is not
   * configured.
   *
   * @throws ArgumentParsingException If something goes wrong with the parsing
   * or setting of parameters.
   */
  public List<String> parse(String[] args)
    throws NullPointerException,
           NoSuchOptionException,
           ArgumentParsingException {
    if (args == null) {
      throw new NullPointerException("Arguments may not be null");
    }

    List<String> remaining = new ArrayList<>();

    Iterator<String> it = Arrays.asList(args).iterator();
    while (it.hasNext()) {
      String arg = it.next();

      // Stop at "--"
      if (arg.equals("--")) {
        while (it.hasNext()) {
          remaining.add(it.next());
        }
        break;
      }

      // Long option
      if (arg.startsWith("--")) {
        set(arg.substring(2), it);
        continue;
      }

      // Short option(s)
      if (arg.startsWith("-")) {
        char[] chars = new char[arg.length() - 1];
        arg.getChars(1, arg.length(), chars, 0);
        for (char c : chars) {
          set(Character.toString(c), it);
        }

        continue;
      }

      // This wasn't an option
      remaining.add(arg);
    }

    // TODO set defaults
    return remaining;
  }

  /**
   * Sets the text to be shown at the beginning of the usage text.
   *
   * @param usageHeader The header text.
   *
   * @see #printUsage(java.io.PrintStream)
   */
  public void setUsageHeader(String usageHeader) {
    this.usageHeader = usageHeader;
  }

  /**
   * Prints the usage text, based on the <code>description</code> and
   * <code>default</code> annotation elements and the usage header.
   *
   * @param output The target for printing.
   *
   * @throws NullPointerException If <code>output</code> is <code>null</code>.
   *
   * @see #setUsageHeader(java.lang.String)
   *
   * @see #printUsage()
   */
  public void printUsage(PrintStream output)
    throws NullPointerException {
    if (output == null) {
      throw new NullPointerException("Output stream may not be null");
    }

    // A null usageHeader is allowed, it just means no header will be printed
    if (usageHeader != null) {
      output.println(usageHeader);
      output.println();
    }

    if (!options.isEmpty()) {
      output.println(bold("Options"));
      output.println();
    }

    for (Option option : options) {
      // Print all names, including pattern if defined
      List<String> names = new ArrayList<>(option.getNames());

      if (option.getCommandLineOption().pattern() != null
        && !option.getCommandLineOption().pattern().isEmpty()) {
        names.add("/" + option.getCommandLineOption().pattern() + "/");
      }

      for (String name : names) {
        output.print("  ");
        output.print(bold(((name.length() == 1) ? "-" : "--") + name));
        if (option.takesValue()) {
          output.print(" ");
          output.print(
            underline(
              (option.getCommandLineOption().placeholder() == null
              || option.getCommandLineOption().placeholder().isEmpty())
              ? "VALUE"
              : option.getCommandLineOption().placeholder()
            )
          );
        }
        output.println();
      }

      if (option.getCommandLineOption().description() != null
        && !option.getCommandLineOption().description().isEmpty()) {
        output.println();
        output.print("    ");
        output.println(option.getCommandLineOption().description());
      }

      if (option.getCommandLineOption().defaultValue() != null
        && !option.getCommandLineOption().defaultValue().isEmpty()) {
        output.println();
        output.print("    Default: ");
        output.println(option.getCommandLineOption().defaultValue());
      }

      output.println();         // Empty line between options
    }
  }

  /**
   * Like {@link #printUsage(Stream output)}, but prints to {@link System#out}.
   */
  public void printUsage() {
    printUsage(System.out);
  }

  private void set(String name,
                   Iterator<String> it)
    throws NoSuchOptionException,
           ArgumentParsingException {
    Option option = optionForName(name);
    option.set(name, it);
  }

  private Option optionForName(String name)
    throws NoSuchOptionException {
    for (Option option : options) {
      if (option.hasName(name)) {
        return option;
      }
    }

    throw new NoSuchOptionException(name);
  }

}
