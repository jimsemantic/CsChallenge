# CsChallenge solution by Jim Miller
I have purposely commented the code little in order to have a full discussion about my solution at an interview
.  There are some assumptions, about the command line arguments and the formatting of the input data, listed at the top of the source files.  Thanks for the fun challenge.

Usage:

BuildDb:  no arguments.  Assumes input data is located at util/input_data

Query:  arguments as shown in challenge handout examples.  Spaces needed between functions and their arguments
, commas but no spaces between function arguments.  Value arguments in filter fields that contain internal spaces
 must be surrounded by quotes, e.g. for TITLEs.  If the same function appears more than once as an argument, the
  prior one will be clobbered.
  
 util/makeinputdata.py is a Python utility to generate input_data.  The "titles" variable can be edited to control
  the number of records to generate.
