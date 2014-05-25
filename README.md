JRegexPlus_Tester
=================

Test files for JRegexPlus regular expression engine

Usage: RegexTester [\<test_input_filename\>] [-timing] [-oracle] [-text \<search_text_filename\>] [-output \<output_file_name\>][-all]

To get started quickly, compile RegexTester.java and use on the command line with the arguments

java -jar \<yourpath\>/JRegexPlus_Tester.jar "perl_test_cases.txt" -oracle

or

java -jar \<yourpath\>/JRegexPlus_Tester.jar "LoremRegex.txt"  -text "LoremIpsum.txt" -output "LoremOut.txt"

The first command publishes the output to results.txt by default unless -output specifies a different output filename.

The second runs the regex tests on a "large" text file specified by -text. Note the input file formats are slightly different
when specifying -text. See LoremRegex.txt and perl_test_cases.txt to compare. As a guide, the large input text and provided regexes currently take 15-20 seconds to run on an AMD A6-3420M with 6 GB memory. 

stress_tests.txt contains some examples of regexes that would make a traditional backtracking NFA crash. When testing expressions like these, do NOT use the -oracle or -timing flag in the command line.

