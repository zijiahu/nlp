import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class dollar_program {
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
		FileWriter output = new FileWriter("dollar_output.txt");

	    // Pattern pattern1 = Pattern.compile("[a-zA-Z0-9]+\\s(hundred|thousand|million|billion)*\\s(dollars?|cents?)");
		// Pattern pattern2 = Pattern.compile("\\$[0-9\\.,]+(\\s)*(million|billion|thousand)*");
		// String p1 = "[a-zA-Z0-9]+\\s((hundred|thousand|million|billion)*\\s)?(dollars?|cents?)";
		String words_p1 = "[a-zA-Z]+\\s((hundred|thousand|million|billion)*\\s)?(dollars|cents)";
		String digits_p1 = "[0-9\\.,]+\\s((hundred|thousand|million|billion)*\\s)?(dollars|cents)";
		String p1 = '('+ words_p1 + ")|(" + digits_p1 + ')';

		String p2 = "\\$[0-9\\.,]+(\\s)*(million|billion|thousand)*";
		Pattern pattern = Pattern.compile('('+ p1 + ")|(" + p2 + ')');
		// int total = 0;


		String input = "";
		while ((input = br.readLine()) != null) {
			Matcher matcher = pattern.matcher(input);
	    	
	    	// while(matcher.find()){
	    	// 	System.out.println(matcher.group());
	    	// }

	    	while(matcher.find()){
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
		// output.write("total number: " + total);
		output.close();
	}
}
