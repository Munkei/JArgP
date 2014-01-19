# JArgP — Java Command Line Argument Parser #

Command line options are configured through the
[@CommandLineOption annotation](apidocs/com/munkei/CommandLineOption.html).  Its
[documentation](apidocs/com/munkei/CommandLineOption.html) describes how to use
it.

The actual parsing is done by a [JArgP](apidocs/com/munkei/JArgP.html) object.

The JArgP object can also
[print a usage message](apidocs/com/munkei/JArgP.html#printUsage).

See [usage](usage.html) for a code example.

## Features ##

* Long and short options.  E.g. `--help` and `-h`.
* Grouped short options.  E.g. `-abc` means the same as `-a -b -c`.
* Stop looking for options when reaching `--`, to allow parameters that start
  with `-`.
* Interpret a single, free-standing `-` as an option (if configured).  It is
  common to use this option to mean "read from STDIN".
* Match option by pattern (regular expression).  E.g. `\d+` to allow options
  `--1`, `--2`, etc.
* Generation, and printing, of a usage message, with descriptions of all the
  options.

## Bugs ##

There may be some bugs, and they should be listed on JArgP's GitHub page.  If
you find a bug that is not listed there, feel free to report it.

## Possible Future Features ##

* Producing completions, for use by, for instance, `bash-completion`.
