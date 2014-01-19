# Usage #

Generally, what one wants to do is have a class with a number of fields that
have the `@CommandLineOption` annotation.  Then at program startup, e.g. in the
`main(String[])` method:

1. Create an instance object of said class.
2. Create a `JArgP` object with the instance as the subject.
3. Call the `parse` method with the `String[]` that was sent to
   `main(String[])`.
4. If appropriate (e.g. `--help` or an unknown option was supplied), call the
   `printUsage()` method.

## Example ##

Below is a rather minimal example:  An application that takes `--help` as an
option to print a usage message, and `--output` *or* `-o` to define an output
file.  Remaining arguments are input files.

    public class App {

      // "--help", a boolean option (a "flag").  Even without options, the
      // annotation creates a command line option for the field.
      @CommandLineOption
      public Boolean help;

      // "--output" or "-o", a file option.  Since `File` has a single-string
      // constructor, it will be automatically created by JArgP when this option
      // is used.
      @CommandLineOption(
        names = { "output", "o" }
      )
      private File output;

      public static void Main(String[] args) {
        int          exitCode   = 0;
        boolean      printUsage = false;
        List<String> inputs;

        // Create the object that will receive the parsed options
        App instance = new App();

        // Create the parser object
        JArgP jargp = new JArgp(instance);

        // Parse the command line arguments.  The remaining arguments are put in
        // a list of input files.
        try {
          inputs = jargp.parse(args);
        } catch (ArgumentParsingException ex) {
          printUsage = true;
          exitCode = 1;
        } catch (UnknownOptionException ex) {
          printUsage = true;
          exitCode = 1;
          System.err.println("Unknown option: " + ex.getOption());
        }

        // If "--help" was among the arguments, print usage
        if (instance.help) {
          printUsage = true;
        }

        // Print usage and exit
        if (printUsage) {
          jargp.printUsage();
          System.exit(exitCode);
        }

        // The rest of the program's code goes here, probably letting `instance`
        // take over.  By the way, instance's `output` field is now a `File`, if
        // it was specified on the command line.
      }
    }
