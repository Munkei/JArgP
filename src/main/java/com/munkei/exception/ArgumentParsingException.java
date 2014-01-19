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

package com.munkei.exception;

import java.text.MessageFormat;

/**
 *
 * @author Theo 'Biffen' Willows <theo@willows.se>
 *
 * @since 0.0.1
 */
public class ArgumentParsingException
  extends Exception {

  private static final long serialVersionUID = 1L;

  public ArgumentParsingException(Throwable cause,
                                  String message,
                                  Object... arguments) {
    super(MessageFormat.format(message, arguments), cause);
  }

  public ArgumentParsingException(String message, Object... arguments) {
    this(null, message, arguments);
  }

}
