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

import java.lang.reflect.Field;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Theo 'Biffen' Willows <theo@willows.se>
 *
 * @since 0.0.1
 */
public class CommandLineOptionTest {

  @Test
  public void test() {
    for (Field field : TestSubject.class.getFields()) {
      if (field.isAnnotationPresent(CommandLineOption.class)) {
        CommandLineOption option = field.getAnnotation(CommandLineOption.class);
        assertEquals(2, option.names().length);
        assertArrayEquals(new String[]{ "name1",
                                        "name2" },
                          option.names());
        assertEquals("default", option.defaultValue());
        assertEquals("description", option.description());
        assertEquals(true, option.opposite());
        assertEquals("pattern", option.pattern());
        assertEquals("setField", option.setter());
      }
    }
  }

  public class TestSubject {

    @SuppressWarnings("PublicField")
    @CommandLineOption(
      names = { "name1",
                "name2" },
      defaultValue = "default",
      description = "description",
      opposite = true,
      pattern = "pattern",
      setter = "setField")
    public String field;

  }

}
