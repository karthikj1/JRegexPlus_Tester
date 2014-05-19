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

   static String test_input_file = "perl_test_cases.txt";  
   static String out_file = "results.txt";
   static String search_text_file = "";
   
   static final int NUM_TRIALS = 100;
   static boolean DO_TIMING = false;
   static boolean DO_ORACLE = false;
   static boolean SEARCH_FROM_FILE = false;
   
   static int Java_matcher_flags = java.util.regex.Pattern.DOTALL | java.util.regex.Pattern.UNIX_LINES;
   
    public static void main(String[] args) {
                
        Matcher matchObj = null;
        java.util.regex.Matcher javaMatcher = null;
        java.util.regex.Pattern javaPattern;
        int test_counter = 0;
        
        String inp;
        String search_text = "";
        String regex, oracle_result_string;        
        int oracle_num_groups = 0;
        boolean oracle_result = false; 
        boolean match_success = false;
                
        Scanner scanner = null;
        
        parse_args(args);
        show_usage_syntax();
        System.out.println("\r\nInput file with test cases - " + test_input_file);        
        System.out.println("\r\nResults published to file " + out_file);
        
        try {            
            File output_file = new File(out_file);        
            scanner = new Scanner(new File(test_input_file)).useDelimiter(";|;\\n|\\n");
            PrintStream printStream = new PrintStream(new FileOutputStream(output_file));
            System.setOut(printStream);
            
            if(SEARCH_FROM_FILE){
                search_text = readFile(search_text_file);
            }
                
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());            
        }
        // skip over first line with column headings
        for (int r = 0; r < ((SEARCH_FROM_FILE) ? 2 : 3); r++) {
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
                
                inp = (SEARCH_FROM_FILE) ? search_text : scanner.next();
                
                if (scanner.hasNext())
                    scanner.next();
                
                test_counter++;
            
                System.out.print("\r\n\r\n" + test_counter + ". Testing: " + regex + " with string ");
                System.out.println((SEARCH_FROM_FILE) ? "in file " + search_text_file : inp);
                if (DO_TIMING)
                    {
                    show_compile_stats(NUM_TRIALS, regex, inp);
                    show_search_stats(NUM_TRIALS, regex, inp);
                    }
            
                matchObj = Pattern.compile(regex);
                match_success = matchObj.find(inp);

                javaPattern = java.util.regex.Pattern.compile(regex, Java_matcher_flags);
                javaMatcher = javaPattern.matcher(inp);
                if (DO_ORACLE)
                    oracle_result = javaMatcher.find();

                   //    System.out.println("Final:\r\n " + matchObj.toString());

                    oracle_result_string = (DO_ORACLE) ? "NO MATCH" : "DID NOT COMPARE TO ORACLE";
                    oracle_num_groups = 0;

                    if (match_success)
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
                            boolean group_match = true;
                            for (int i = 1; i <= matchObj.groupCount(r); i++)
                                {
                                System.out.print("group " + i);
                                System.out.print(": " + matchObj.group(r, i) + ", ");

                                if (DO_ORACLE && oracle_result)
                                    {
                                    System.out.print("Oracle group " + i);
                                    System.out.print(": " + javaMatcher.group(i) + "\r\n");
                                    if(!javaMatcher.group(i).equals(matchObj.group(r,i)))
                                        System.out.println("Group " + i + " DID NOT MATCH ");
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
                    System.out.println("");
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
            javaPattern = java.util.regex.Pattern.compile(regex, Java_matcher_flags);
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
        javaPattern = java.util.regex.Pattern.compile(regex, Java_matcher_flags);
        javaMatcher = javaPattern.matcher(inp);

        startTime = System.currentTimeMillis();
        for (int r = 0; r < NUM_TRIALS; r++){
            matchObj.reset();
            matchObj.find(inp);
        }
        
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
        System.out.println("Backreferences without backtracking\r\n");
        System.out.println("Usage: RegexTester [<test_input_filename>] [-timing] [-oracle] "
                + "[-text <search_text_filename>] [-output <output_file_name>]\r\n");
        System.out.println("[<test_input_filename>]  - Name of input file with test cases. Default is perl_test_cases.txt");
        System.out.println("[-timing] - Processes regex with java.util.regex engine and calculates running time comparison");
        System.out.println("[-output <output_file_name>] - Name of output file for results");
        System.out.println("[-text <text_file_name>] - Name of file containing text to search for all regexes");
        System.out.println("\r\n");
        
    }
    
    private static void parse_args(String[] args){
        int ctr = 0;
        String arg;
        
        while(ctr < args.length)
            {
            arg = args[ctr++];
            if(arg.equalsIgnoreCase("-timing")){
                DO_TIMING = DO_ORACLE = true;   
                continue;
            }
            
            if(arg.equalsIgnoreCase("-oracle")){
                DO_ORACLE = true;
                continue;
            }
            
            if(arg.equalsIgnoreCase("-output")){
                if(ctr < args.length){
                    out_file = args[ctr++];
                    continue;
                }
                else {
                    System.out.println("ERROR: -output must be followed by output file name");
                    show_usage_syntax();
                    System.exit(-1);
                }
            }
            
             if(arg.equalsIgnoreCase("-text")){
                if(ctr < args.length){
                    SEARCH_FROM_FILE = true;
                    search_text_file = args[ctr++];
                    continue;
                }
                else {
                    System.out.println("ERROR: -text must be followed by search text file name");
                    show_usage_syntax();
                    System.exit(-1);
                }
            }
            
            if(arg.charAt(0) != '-')
                test_input_file = arg;            
        }
        
    }
    
        private static String readFile(String filename) throws IOException{
            StringBuffer sb = new StringBuffer("");
            BufferedReader br = new BufferedReader(new FileReader(filename));
            int i;
            
            while((i = br.read()) != -1)
                sb.append((char) i);
            
            return sb.toString();
            
        }
}

