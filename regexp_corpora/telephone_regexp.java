import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class telephone_regexp{
	public static void main(String[] args) throws Exception {
		File filepath = null;
		if (0 < args.length) {
		   filepath = new File(args[0]);
		} 
		else {
		   System.err.println("Invalid arguments count:" + args.length);
		   System.exit(-1);
		}

		BufferedReader br = new BufferedReader(new FileReader(filepath)); 
		FileWriter output = new FileWriter("telephone_output.txt");

		String input = "";
		// int total = 0;

		// Pattern pattern = Pattern.compile("((\\([2-9][0-9]{2}\\)\\s)|([2-9][0-9]{2}\\-))?[0-9]{3}-[0-9]{4}"); // all cases 
		String p1 = "\\([2-9][0-9]{2}\\)\\s?[0-9]{3}-[0-9]{4}"; // (xxx) xxx-xxxx and (xxx)xxx-xxxx
		String p2 = "[2-9][0-9]{2}[-/][0-9]{3}-[0-9]{4}"; // xxx-xxx-xxxx
		String p3 = "\\s[0-9]{3}-[0-9]{4}"; // xxx-xxxx
		Pattern pattern = Pattern.compile("(" + p1 + ")|(" + p2 + ")|(" + p3 + ")"); // all cases 

		while ((input = br.readLine()) != null) {
			Matcher matcher = pattern.matcher(input);
	    	
	    	// while(matcher.find()){
	    	// 	System.out.println(matcher.group());
	    	// }

		    while(matcher.find()) {
		    	try {
		    		output.write(matcher.group()+'\n');
		    		// total++;
		    	}
		    	catch (IOException e) {
		    		System.out.println("An error occurred.");
		    		e.printStackTrace();
		    	}
		    }
		}
		// output.write("total number: "+total);
		output.close();
	}
}