package karthik.regexTester;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template output_file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.*;
import java.util.Scanner;
import karthik.regex.Matcher;
import karthik.regex.MatcherException;
import karthik.regex.Pattern;


/**
 *
 * @author karthik
 */
public class RegexTester {

    /**
     * @param args the command line argument regex
     */
    public static void main(String[] args) {

        String test_input_file = "perl_test_cases.txt";  
        String out_file = "results.txt";
        File output_file = new File(out_file);
        
        final int NUM_TRIALS = 100;
        boolean DO_TIMING = false;
        boolean DO_ORACLE = false;
        
        Matcher matchObj = null;
        java.util.regex.Matcher javaMatcher = null;
        java.util.regex.Pattern javaPattern;
        int test_counter = 1;
        
        String inp;
        String regex, oracle_result_string;        
        int oracle_num_groups = 0;
        boolean oracle_result = false;        
                
        Scanner scanner = null;
        
        for(String arg: args){
            if(arg.equals("-timing"))
                DO_TIMING = DO_ORACLE = true;            
            
            if(arg.equals("-oracle"))
                DO_ORACLE = true;
            
            if(arg.charAt(0) != '-')
                test_input_file = arg;            
        }
        
        show_usage_syntax();
        System.out.println("\r\nInput file with test cases - " + test_input_file);        
        System.out.println("\r\nOutput published to file " + out_file);
        
        try {            
            scanner = new Scanner(new File(test_input_file)).useDelimiter(";|;\\n|\\n");
            PrintStream printStream = new PrintStream(new FileOutputStream(output_file));
            System.setOut(printStream);
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());            
        }
        // skip over first line with column headings
        for (int r = 0; r < 3; r++) {
            scanner.next();
        }
        while (scanner.hasNext()) {
            try
                {
                // read in data from test suite
                regex = scanner.next();
                if (regex.trim() == "")
                    {
                    break;
                    }
                inp = scanner.next();
                if (scanner.hasNext())
                    scanner.next();
                System.out.println("\r\n\r\n" + test_counter + ". Testing: " + regex + " with string " + inp);

                test_counter++;
                if (DO_TIMING)
                    {
                    show_compile_stats(NUM_TRIALS, regex, inp);
                    show_search_stats(NUM_TRIALS, regex, inp);
                    }

                matchObj = Pattern.compile(regex);
                matchObj.match(inp);

                javaPattern = java.util.regex.Pattern.compile(regex);
                javaMatcher = javaPattern.matcher(inp);
                if (DO_ORACLE)
                    oracle_result = javaMatcher.find();

                if (matchObj != null)
                    {
                   //    System.out.println("Final:\r\n " + matchObj.toString());

                    oracle_result_string = (DO_ORACLE) ? "NO MATCH" : "DID NOT COMPARE TO ORACLE";
                    oracle_num_groups = 0;

                    if (matchObj.match(inp))
                        {
                        for (int r = 0; r < matchObj.matchCount(); r++)
                            {

                            System.out.print("MATCHED: " + matchObj.groupCount(r) + " groups ");

                            if (DO_ORACLE)
                                {
                                if (oracle_result)
                                    {
                                    oracle_result_string = javaMatcher.group();
                                    oracle_num_groups = javaMatcher.groupCount();
                                    if (oracle_result_string.equals(matchObj.group(r, 0)))
                                        System.out.println("MATCHED oracle");
                                    else
                                        System.out.println("DID NOT MATCH oracle");
                                    } else
                                    {
                                    System.out.println("DID NOT MATCH oracle");
                                    System.out.println("oracle groups = " + oracle_num_groups);
                                    }
                                }
                            System.out.println("Matched string: " + matchObj.group(r, 0)
                                    + " oracle string: " + oracle_result_string);
                            for (int i = 1; i <= matchObj.groupCount(r); i++)
                                {
                                System.out.print("group " + i);
                                System.out.print(": " + matchObj.group(r, i) + ", ");

                                if (DO_ORACLE && oracle_result)
                                    {
                                    System.out.print("Oracle group " + i);
                                    System.out.print(": " + javaMatcher.group(i) + ", ");
                                    }
                                }
                            }
                        } else
                        {
                        System.out.print(" :does not match ");
                        if (DO_ORACLE){
                            if (!oracle_result)
                                System.out.println("MATCHED oracle");
                            else
                                System.out.println("DID NOT MATCH oracle");
                        }
                        else
                                System.out.println("DID NOT COMPARE TO ORACLE");
                        }
                    }
                } // try
            catch (MatcherException me) {
                System.out.println("RegexTester:" + me.getMessage());
            }
            catch (IllegalStateException ise) {
                System.out.println("RegexTester Java matcher ERROR: " + ise.getMessage());
            }
            catch(NullPointerException | IndexOutOfBoundsException npe)
                {
                    System.out.println("\r\nError: " + npe.getMessage());
                    System.out.println("\r\n");
                    npe.printStackTrace(System.out);                    
                }
        } // while
    }
    
    private static void show_compile_stats(final int NUM_TRIALS, final String regex, final String inp)
            throws MatcherException
        {
        double startTime, endTime;
        Matcher matchObj = null;
        java.util.regex.Matcher javaMatcher = null;
        java.util.regex.Pattern javaPattern;

        startTime = System.currentTimeMillis();
        for (int r = 0; r < NUM_TRIALS; r++)
            matchObj = Pattern.compile(regex);
        endTime = System.currentTimeMillis();
        System.out.print("KJ Compiled in " + (endTime - startTime) / NUM_TRIALS);
        startTime = System.currentTimeMillis();
        for (int r = 0; r < NUM_TRIALS; r++)
            {
            javaPattern = java.util.regex.Pattern.compile(regex);
            javaMatcher = javaPattern.matcher(inp);
            }
        endTime = System.currentTimeMillis();
        System.out.println(", java compiled in " + (endTime - startTime) / NUM_TRIALS);
        }

    private static void show_search_stats(final int NUM_TRIALS, final String regex, final String inp) throws MatcherException
        {
        double startTime, endTime;
        Matcher matchObj;
        java.util.regex.Matcher javaMatcher;
        java.util.regex.Pattern javaPattern;

        matchObj = Pattern.compile(regex);
        javaPattern = java.util.regex.Pattern.compile(regex);
        javaMatcher = javaPattern.matcher(inp);

        startTime = System.currentTimeMillis();
        for (int r = 0; r < NUM_TRIALS; r++)
            matchObj.match(inp);
        endTime = System.currentTimeMillis();
        System.out.print("KJ matched in " + (endTime - startTime) / NUM_TRIALS);

        startTime = System.currentTimeMillis();
        for (int r = 0; r < NUM_TRIALS; r++)
            {
            javaMatcher.reset();
            javaMatcher.find();
            }
        endTime = System.currentTimeMillis();
        System.out.println(", java matched in " + (endTime - startTime) / NUM_TRIALS);
        }
    
    private static void show_usage_syntax(){
                
        System.out.println("\r\nJRegexPlus regex engine by Karthik Jayaraman\r\n");
        System.out.println("Usage: RegexTester [<test_input_filename>] [-timing] [-oracle]\r\n");
        System.out.println("[<test_input_filename>]  - Name of input file with test cases. Default is perl_test_cases.txt");
        System.out.println("[-timing] - Processes regex with java.util.regex engine and calculates running time comparison");
        System.out.println("[-oracle] - Processes regex with java.util.regex engine as a test oracle");
        System.out.println("\r\n");
        System.out.println("Results are published to results.txt by default");
    }
    
}
