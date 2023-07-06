# java_regex_checker
Small CLI Tool to debug java regex easier

## Idea
the rough idea is to put something like this in a file:

    ^[\w]+_(.*)$
    
    // ^[a-z0-9]+_.*$sad//
    // asdsad
    // Documentation: https://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html
    //
    
    123a1z23312_asdasd
    aadad111_asdasd
    d112121Ad_ffafa
    aaa
    fgafafaaasss
    ßßöx
    adada
    aa
    dadad
    bba

if you run the program with the parameter "test_file" this will be the output:

    pattern: ^[\w]+_(.*)$

    matchedExamples:
    123a1z23312_asdasd --> groups: asdasd
    aadad111_asdasd --> groups: asdasd
    d112121Ad_ffafa --> groups: ffafa
    
    notMatchedExamples:
    aaa
    fgafafaaasss
    ???x
    adada
    aa
    dadad
    bba

The Text is split by the line

    //
All lines before this will be ignored, if they start with "// " or empty.
The remaining line will be considered the regex-pattern to test.
All lines after the separation line will be used to test the regex-pattern.
In the output you can see which match and which don't match. 

## Parameters
    Usage: java-regex-checker [-hlvV] [-m=<millis>] <path>
    TODO
    <path>                  path to the file to process
    -h, --help              Show this help message and exit.
    -l, --loop
    -m, --millis=<millis>
    -v, --verbose           verbosity
    -V, --version           Print version information and exit.

use <code>--loop</code>  to repeat the execution in a loop, while waiting 500ms.

You can specify the delay in <code>--millis</code>.
