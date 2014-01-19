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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Theo 'Biffen' Willows <theo@willows.se>
 *
 * @since 0.0.1
 */
public class JArgPTest {

  public JArgPTest() {
  }

  @Test
  public void testUsage()
    throws Exception {
    TestSubject testSubject = new TestSubject();
    JArgP jArgP = new JArgP(testSubject);

    jArgP.setUsageHeader("Test\n\nUsage\n\n  test [options] [file [file]]");
    System.out.println("<<<USAGE");
    jArgP.printUsage();
    System.out.println(">>>USAGE");
  }

  @Test
  public void testParse()
    throws Exception {
    TestSubject testSubject = new TestSubject();
    JArgP jArgP = new JArgP(testSubject);

    String[] expectedRemaining = new String[]{
      "remain1",
      "remain2",
      "--remain3"
    };
    String[] expectedStrings = new String[]{
      "one",
      "two"
    };
    String stringarg = "stringarg";
    String[] args = new String[]{
      "--string",
      stringarg,
      expectedRemaining[0],
      "--flag",
      "--file",
      ".",
      "--arraylist",
      expectedStrings[0],
      "--arraylist",
      expectedStrings[1],
      "--set",
      expectedStrings[0],
      "--set",
      expectedStrings[1],
      "-xyz",
      "--",
      expectedRemaining[1],
      expectedRemaining[2]
    };

    List<String> remaining = jArgP.parse(args);

    System.out.println(
      "remaining = " + remaining);
    assertArrayEquals(expectedRemaining, remaining.toArray(new String[]{}));

    assertEquals(stringarg, testSubject.stringField);

    assertEquals(true, testSubject.booleanField);

    assertEquals(new File("."), testSubject.file);

    System.out.println("testSubject.arraylist = " + testSubject.arraylist);
    assertNotNull(testSubject.arraylist);
    assertArrayEquals(expectedStrings,
                      testSubject.arraylist.toArray(new String[]{}));

    System.out.println("testSubject.set = " + testSubject.set);
    assertNotNull(testSubject.set);
    assertEquals(new HashSet<>(Arrays.asList(expectedStrings)),
                 testSubject.set);

    assertEquals(true, testSubject.x);
    assertEquals(true, testSubject.y);
    assertEquals(true, testSubject.z);
  }

  public class TestSubject {

    @SuppressWarnings("PublicField")
    @CommandLineOption(
      names = { "string",
                "string-alt" },
      defaultValue = "default",
      description = "A single string value.",
      opposite = true,
      placeholder = "STRING")
    public String stringField;

    @SuppressWarnings("PublicField")
    @CommandLineOption(
      names = { "flag" },
      defaultValue = "false",
      description = "A flag.",
      opposite = true)
    public Boolean booleanField;

    @SuppressWarnings("PublicField")
    @CommandLineOption
    public File file;

    @SuppressWarnings("PublicField")
    @CommandLineOption(
      description = "A multiple string value.",
      opposite = true,
      placeholder = "STRING")
    public ArrayList<String> arraylist;

    @SuppressWarnings("PublicField")
    @CommandLineOption(
      description = "A multiple string value.",
      opposite = true,
      placeholder = "STRING")
    public Set<String> set = new HashSet<>();

    @SuppressWarnings("PublicField")
    @CommandLineOption(
      description = "A multiple string value.",
      opposite = true,
      placeholder = "LEVEL",
      pattern = "[0-9]")
    public Integer level;

    @SuppressWarnings("PublicField")
    @CommandLineOption
    public Boolean x;

    @SuppressWarnings("PublicField")
    @CommandLineOption
    public Boolean y;

    @SuppressWarnings("PublicField")
    @CommandLineOption
    public Boolean z;

  }

}
