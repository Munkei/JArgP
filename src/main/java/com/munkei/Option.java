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
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Theo 'Biffen' Willows <theo@willows.se>
 *
 * @since 0.0.1
 */
public class Option {

  private Object subject;

  private Field field;

  private List<String> names;

  Option(Object subject, Field field) {
    if (subject == null) {
      throw new NullPointerException("Subject may not be null");
    }
    if (field == null) {
      throw new NullPointerException("Field may not be null");
    }
    if (!field.isAnnotationPresent(CommandLineOption.class)) {
      throw new IllegalArgumentException(
        "Field ''{}'' has not got an @Option annotation.");
    }

    this.subject = subject;
    this.field = field;
  }

  void set(String name,
           Iterator<String> it)
    throws ArgumentParsingException {
    Object value = ((takesValue())
                    ? convert(it.next(), getEffectiveClass())
                    : true);
    String setterName = getCommandLineOption().setter();

    // Setter method
    if (getCommandLineOption().setter() != null
      && !getCommandLineOption().setter().isEmpty()) {
      Method setter;
      try {
        try {
          setter = subject.getClass().getMethod(setterName,
                                                String.class,
                                                getField().getType());
        } catch (NoSuchMethodException |
                 SecurityException ex) {
          // Just try with single-parameter method
          setter = subject.getClass().getMethod(setterName,
                                                getField().getType());
        }
        setter.invoke(subject, value);
      } catch (NoSuchMethodException |
               SecurityException ex) {
        throw new ArgumentParsingException(
          ex,
          "Could not get setter (''{0}(java.lang.String, {1})'') for field ''{2}''.",
          setterName,
          getField().getType().getName(),
          getField().getName());
      } catch (IllegalAccessException |
               InvocationTargetException ex) {
        throw new ArgumentParsingException(
          ex,
          "Failed to invoke setter (''{0}(java.lang.String, {1})'') for field ''{2}'' with value ''{3}''",
          setterName,
          getField().getType().getName(),
          getField().getName(),
          value);
      }
      return;
    }

    // Direct
    try {
      boolean accessible = getField().isAccessible();
      getField().setAccessible(true);
      if (isCollection()) {
        Collection collection;
        if (getField().get(subject) == null) {
          try {
            Constructor<?> constructor = getField().getType().getConstructor();
            collection = (Collection) constructor.newInstance();
            getField().set(subject, collection);
          } catch (NoSuchMethodException |
                   SecurityException |
                   InstantiationException |
                   IllegalAccessException |
                   IllegalArgumentException |
                   InvocationTargetException ex) {
            throw new ArgumentParsingException(
              ex,
              "Could not create a collection for field ''{0}''.",
              getField().getName());
          }
        } else {
          collection = (Collection) getField().get(subject);
        }
        collection.add(value);
      } else {
        getField().set(subject, value);
      }
      getField().setAccessible(accessible);
    } catch (IllegalArgumentException |
             IllegalAccessException ex) {
      throw new ArgumentParsingException(
        ex,
        "Could not directly set field ''{1}''.",
        getField().getName());
    }
  }

  /**
   * Helper method to convert command line options' values ({@link String}s) to
   * objects of other classes.
   * <p>
   * Values are converted to objects of different classes thusly:
   * <dl>
   * <dt>{@link String}</dt>
   * <dd>No conversion.</dd>
   * <dt>{@link Double}</dt>
   * <dt>{@link Float}</dt>
   * <dt>{@link Integer}</dt>
   * <dt>{@link Long}</dt>
   * <dt>{@link Short}</dt>
   * <dd>Each class' respective
   * <code>parse[...]({@link String} s)</code> method.</dd>
   * </dl>
   * If none of the above matches the desired class, an attempt is made to
   * create an object with a constructor taking a single {@link String}
   * parameter. This works well with, for instance, {@link java.io.File}.
   *
   * @param value The value to be converted.
   *
   * @param to The class to which to convert.
   *
   * @return An object of the specified class converted from the value.
   *
   * @throws ArgumentParsingException
   */
  static Object convert(String value, Class<?> to)
    throws ArgumentParsingException {
    if (value == null) {
      throw new NullPointerException("Value may not be null.");
    }
    if (to == null) {
      throw new NullPointerException("To may not be null.");
    }

    // String: no conversion needed
    if (to.isAssignableFrom(String.class)) {
      return value;
    }

    // Character: if value is single character, use it
    if (to.isAssignableFrom(Character.class)
      && value.length() == 1) {
      return new Character(value.charAt(0));
    }

    // Numericals are parsed
    if (to.isAssignableFrom(Double.class)) {
      return new Double(Double.parseDouble(value));
    }
    if (to.isAssignableFrom(Float.class)) {
      return new Float(Float.parseFloat(value));
    }
    if (to.isAssignableFrom(Integer.class)) {
      return new Integer(Integer.parseInt(value));
    }
    if (to.isAssignableFrom(Long.class)) {
      return new Long(Long.parseLong(value));
    }
    if (to.isAssignableFrom(Short.class)) {
      return new Short(Short.parseShort(value));
    }

    // Attempt to construct an object from a String
    try {
      Constructor<?> constructor = to.getConstructor(String.class);
      Object instance = constructor.newInstance(value);
      return instance;
    } catch (NoSuchMethodException | SecurityException | InstantiationException |
             IllegalAccessException | IllegalArgumentException |
             InvocationTargetException ex) {
      // We did our best
    }

    throw new ArgumentParsingException("Can't convert from String to ''{0}''.",
                                       to.getName());
  }

  boolean takesValue() {
    return !isBoolean();
  }

  boolean isBoolean() {
    return Boolean.class.isAssignableFrom(getField().getType());
  }

  boolean hasName(String name) {
    return getNames().contains(name);
  }

  CommandLineOption getCommandLineOption() {
    return getField().getAnnotation(CommandLineOption.class);
  }

  List<String> getNames() {
    // Lazy creation
    if (names == null) {
      names = new ArrayList<>();
      names.addAll(Arrays.asList(getCommandLineOption().names()));

      if (names.isEmpty()) {
        names.add(getField().getName());
      }

      // If defined, make "--no-..." version(s)
      if (isBoolean() && getCommandLineOption().opposite()) {
        for (String name : new ArrayList<>(getNames())) {
          names.add("no-" + name);
        }
      }
    }

    return Collections.unmodifiableList(names);
  }

  Field getField() {
    return field;
  }

  boolean isCollection() {
    return Collection.class.isAssignableFrom(getField().getType());
  }

  Class<?> getEffectiveClass() {
    return ((isCollection())
            ? (Class<?>) ((ParameterizedType) getField().getGenericType())
      .getActualTypeArguments()[0]
            : getField().getType());
  }

}
