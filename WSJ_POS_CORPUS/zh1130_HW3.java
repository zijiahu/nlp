import java.util.HashMap;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class zh1130_HW3{

	// a hashmap that stores the occurrence of each word given its pos tag
	public static HashMap<String, HashMap<String, Integer>> occurrence = new HashMap<>();
	// a hashmap that stores the likelihood of each word
	public static HashMap<String, HashMap<String, Float>> likelihood = new HashMap<>();
	// a hashmap that stores the occurrence of each pos tag given its previous pos tag
	public static HashMap<String, HashMap<String, Integer>> prior = new HashMap<>();
	// a hashmap that stores the prior probability of each pos tag
	public static HashMap<String, HashMap<String, Float>> prior_prob = new HashMap<>();

	public static void main(String[] args) throws Exception{
		File filepath = null;
		File filepath2 = null;

		if (0 < args.length && args.length < 3) {
		   filepath = new File(args[0]);
		   filepath2 = new File(args[1]);
		} 
		else {
		   System.err.println("Invalid arguments count:" + args.length);
		   System.exit(-1);
		}

		BufferedReader br = new BufferedReader(new FileReader(filepath));
		
		String input = ""; // stores each line we read from the file
		String pre_pos = ""; // stores the previous pos tag of the current pos tag that we are dealing with
		// iterate over the file once, and store all the data into hashmap occurrence and hashmap prior
		while((input = br.readLine()) != null){
			/*
			Every time we encounter a blank line, we set the dependent pos tag of the previous pos tag to 'E': the end of a sentence.
			Also, we mark the previous pos tag as 'B': the beginning of a sentence
			*/
			if(input.equals("")) {
				if(prior.containsKey(pre_pos)){
					HashMap<String, Integer> hm = prior.get(pre_pos);
					hm.put("E", hm.getOrDefault("E", 0)+1);
				}
				else{
					prior.put(pre_pos, new HashMap<String, Integer>());
					HashMap<String, Integer> hm = prior.get(pre_pos);
					hm.put("E", 1);
				}
				pre_pos = "B";
				continue; // skip the blank line
			}

			// split the input line into two parts by '\t'
			String[] line = input.split("\t");
			String word = line[0];
			String pos = line[1];

			// counting occurence of a word given a pos tag
			if(occurrence.containsKey(pos)){
				HashMap<String, Integer> hm = occurrence.get(pos);
				hm.put(word, hm.getOrDefault(word ,0) + 1);
			}
			else{
				occurrence.put(pos, new HashMap<String, Integer>());
				HashMap<String, Integer> hm = occurrence.get(pos);
				hm.put(word, 1);
			}

			// counting the number of occurence of the current tag given the previous tag
			if(prior.containsKey(pre_pos)){
				HashMap<String, Integer> hm = prior.get(pre_pos);
				hm.put(pos, hm.getOrDefault(pos, 0)+1);
			}
			else{
				prior.put(pre_pos, new HashMap<String, Integer>());
				HashMap<String, Integer> hm = prior.get(pre_pos);
				hm.put(pos, 1);
			}
			pre_pos = pos; // update the pre_pos
		}

		likelihood = findLikelihood(occurrence); // find the likelihood table
		prior_prob = findPriorProbability(prior); // find the prior probability table



		ArrayList<String> pos_table = new ArrayList<>();

		pos_table.add("B");
		// store all the pos into pos_table
		for(String p: likelihood.keySet()){
			pos_table.add(p);
		}
		pos_table.add("E");
		int total_pos =  pos_table.size();


		// now, open the test file
		BufferedReader br2 = new BufferedReader(new FileReader(filepath2));
		FileWriter output = new FileWriter("submission.pos");
		
		input = ""; //  store the line we read from the file

		ArrayList<String> input_sentence = new ArrayList<>();


		while((input = br2.readLine()) != null){
			if(input_sentence.size() == 0){
				input_sentence.add("");
			}

			if(input.equals("")) {
				ArrayList<String> output_sentence = outputSentence(input_sentence, pos_table, total_pos);
				if(!output_sentence.isEmpty()){
					for(int i = output_sentence.size()-1; i >= 0; i--){
						output.write(output_sentence.get(i) +"\n");
					}
				}
				output.write("\n");
				input_sentence = new ArrayList<String>();
				input_sentence.add("");
				continue;
			}
			else{
				input_sentence.add(input);
			}
		}
		output.close();
	}

	public static ArrayList<String> outputSentence(ArrayList<String> input, ArrayList<String> pos_table, int pos_num){
		input.add("");
		int n = input.size(); // not including the end blank line
		double[][] viterbi = new double[pos_num][n];
		int[][] lookup = new int[pos_num][n];
		viterbi[0][0] = 1;
		for(int i = 1; i < pos_table.size(); i++){
			viterbi[i][0] = 0;
		}

		int word_index = 1;


		while(word_index < n){

			// store all the possible pos of the current word
			HashMap<String, Float> possible_pos = new HashMap<>();
			//    word's pos; the likelihood

			for(String p : likelihood.keySet()){
				HashMap<String, Float> hm = likelihood.get(p);
				for(String w: hm.keySet()){
					if(w.equals(input.get(word_index))) {
						possible_pos.put(p, hm.get(w));
						break;
					}
				}
			}

			// i = index of the previous pos
			for(int i = 0; i < pos_table.size(); i++){
				if(viterbi[i][word_index-1] == 0) continue;

				String pre_pos = pos_table.get(i);
				// j = the index of the current pos
				if(possible_pos.isEmpty()){
					for(int j = 1; j < pos_table.size()-1; j++){
						viterbi[j][word_index] = 1.0/1000;
					}
					double max = 0.0;
					int max_index = -1;
					for(int k = 0; k < pos_table.size(); k++){
						double current = viterbi[k][word_index-1];
						// System.out.println("CURRENT" + current);
						if(current > max){
							max = current;
							max_index = i;
						}
					}
					for(int h = 1; h<pos_table.size(); h++){
						lookup[h][word_index] = max_index;
					}
					
					break;
				}

				for(String s: possible_pos.keySet()){
					double curr_transition = findTransition(pre_pos, s);
					double curr_likelihood = findLikelihood(s, input.get(word_index));
					double temp = viterbi[i][word_index-1]*curr_transition*curr_likelihood;
					int index = pos_table.indexOf(s);
					if(temp > viterbi[index][word_index]){
						viterbi[index][word_index] = temp;
						lookup[index][word_index] = i;
					}
				}
			}
			word_index++;
		}

		double max = (double)-1.0;
		int max_index = -1;
		// viterbi is ready:
		for(int i = 0; i < pos_table.size(); i++){
			double current = viterbi[i][n-1];
			// System.out.println("CURRENT" + current);
			if(current > max){
				max = current;
				max_index = i;
			}
		}

		ArrayList<String> output_sentence = new ArrayList<>();
		// output_sentence.add(input.get(n-1) + '\t' + pos_table.get(max_index));
		int previous = lookup[max_index][n-1];
		// System.out.println("max index: " + max_index);

		for(int i = n-2; i > 0; i--){
			output_sentence.add(input.get(i) + '\t' + pos_table.get(previous));
			previous = lookup[previous][i];
		}

		return output_sentence;

	}

	public static float findLikelihood(String pos, String word){
		if(word.equals("")) return (float)1.0;
		HashMap<String, Float> hm = likelihood.get(pos);
		if(hm != null){
			for(String c : hm.keySet()){
				if(c.equals(word)){
					return hm.get(c);
				}
			}
		}
		return implement_oov();
	}

	public static float findTransition(String pre, String curr){
		// System.out.println("pre: "+ pre+  "; curr: "+ curr);
		HashMap<String, Float> hm = prior_prob.get(pre);
		for(String c : hm.keySet()){
			if(c.equals(curr)){
				// System.out.println(hm.get(c));
				return hm.get(c);
			}
		}
		// System.out.println("transition is 0");
		return (float)0.0;
	}

	public static float implement_oov(){
		return (float)1.0/1000;
	}
	public static HashMap<String, HashMap<String, Float>> findPriorProbability(HashMap<String, HashMap<String, Integer>> prior){
		HashMap<String, HashMap<String, Float>> prior_prob = new HashMap<>();
		for(String s: prior.keySet()){
			HashMap<String, Integer> hm = prior.get(s);
			int total = 0;
			for(String w: hm.keySet()){
				int occur = hm.get(w);
				total+=occur;
				// System.out.println("pre_pos: "+s+"; pos: "+w+"; Occurrence: "+occur);
			}
			// System.out.println("total number of pre_pos: "+s+" is "+total);

			HashMap<String, Float> fhm = new HashMap<>();

			for(String w: hm.keySet()){
				int occur = hm.get(w);
				float prob = (float)occur/total;

				fhm.put(w, prob);
			}
			prior_prob.put(s, fhm);
		}
		return prior_prob;
	}

	public static void printOccurrence(HashMap<String, HashMap<String, Integer>> hm){
		for(String s: hm.keySet()){
			int total = 0;
			HashMap<String, Integer> h = hm.get(s);
			for(String w: h.keySet()){
				Integer occur = h.get(w);
				total+=occur;
				// System.out.println("Pos: "+s+"; Word: "+w+"; Occurrence: "+occur);
				System.out.println("pre_pos: "+s+"; pos: "+w+"; Occurrence: "+occur);
			}
			System.out.println("*pre_pos: "+ s+" in total: " + total);
		}
	}

	public static void printLikelihood(HashMap<String, HashMap<String, Float>> hm){
		for(String s: hm.keySet()){
			HashMap<String, Float> h = hm.get(s);
			for(String w: h.keySet()){
				float occur = h.get(w);
				System.out.println("Pos: "+s+"; Word: "+w+"; probability: "+occur);
			}
		}
	}

	public static void printPriorProbability(HashMap<String, HashMap<String, Float>> hm){
		for(String s: hm.keySet()){
			HashMap<String, Float> h = hm.get(s);
			for(String w: h.keySet()){
				float occur = h.get(w);
				System.out.println("pre_pos: "+s+"; pos: "+w+"; probability: "+occur);
			}
		}
	}

	public static HashMap<String, HashMap<String, Float>> findLikelihood(HashMap<String, HashMap<String, Integer>> occurrence){
		HashMap<String, HashMap<String, Float>> likelihood = new HashMap<>();
		for(String s: occurrence.keySet()){
			HashMap<String, Integer> hm = occurrence.get(s);
			int total = 0;
			for(String w: hm.keySet()){
				int occur = hm.get(w);
				total+=occur;
			}

			HashMap<String, Float> fhm = new HashMap<>();

			for(String w: hm.keySet()){
				int occur = hm.get(w);
				float prob = (float)occur/total;

				fhm.put(w, prob);
			}
			likelihood.put(s, fhm);
		}
		return likelihood;
	}
}