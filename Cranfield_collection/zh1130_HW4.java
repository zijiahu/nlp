import java.util.HashMap;
import java.util.Collections;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Comparator;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class zh1130_HW4{
	/*
	cran.qry -- contains a set of 225 queries numbered 001 through 365, but referred to in cranqrel below as 1 through 225 
	cran.all.1400 -- contains 1400 abstracts of aerodynamics journal articles (the document collection)
	cranqrel is an answer key.
	*/

	public static int total_docs = 0;
	public static HashMap<String, Double> doc_IDF = new HashMap<>();


	public static void main(String[] args) throws Exception{
		File filepath_query = null;
		File filepath_abstract = null;

		if (0 < args.length && args.length < 3) {
		   filepath_query = new File(args[0]);
		   filepath_abstract = new File(args[1]);
		} 
		else {
		   System.err.println("Invalid arguments count:" + args.length);
		   System.exit(-1);
		}

		// store all the words that need to be eliminated into stop_list
		String input = "";
		int total=0;
		BufferedReader br0 = new BufferedReader(new FileReader("stop_list.txt"));
		ArrayList<String> stop_list = new ArrayList<>();

		while((input = br0.readLine()) != null){
			stop_list.add(input);
			total++;
		}

		HashMap<Integer, HashMap<String, Integer>> queries = new HashMap<>();
		HashMap<Integer, HashMap<String, Integer>> abstracts = new HashMap<>();
		HashMap<String, Integer> query = new HashMap<>();
		HashMap<String, Integer> abst = new HashMap<>();


		BufferedReader br_query = new BufferedReader(new FileReader(filepath_query));
		input = "";
		int query_id = 0;
		String pre_tag = "";

		while((input = br_query.readLine()) != null){
			if(input.contains(".I ")){
				query_id++;
				queries.put(query_id, new HashMap<String, Integer>());
				pre_tag = "";
				continue;
			}
			if(input.equals(".W")){
				pre_tag = ".W";
				continue;
			}
			if(pre_tag.equals(".W")){
				Pattern pattern = Pattern.compile("([a-zA-Z])\\w+");
	      		Matcher matcher = pattern.matcher(input);
	      		query = queries.get(query_id);

	      		while(matcher.find()){
	      			String word = matcher.group().toLowerCase();
	      			if(stop_list.contains(word)) continue;
	      			query.put(word, query.getOrDefault(word, 0)+1);
	      		}
	      		queries.put(query_id, query);
			}
		}
		br_query.close();


		BufferedReader br_abstract = new BufferedReader(new FileReader(filepath_abstract));
		input = "";
		int abstract_id = 0;
		pre_tag = "";

		while((input = br_abstract.readLine()) != null){
			if(input.contains(".I ")){
				total_docs++;
				String[] s = input.split(" ");
				abstract_id = Integer.parseInt(s[1]);
				abstracts.put(abstract_id, new HashMap<String, Integer>());
				pre_tag = "";
				continue;
			}
			if(input.equals(".T")) continue;
			if(input.equals(".A")) continue;
			if(input.equals(".B")) continue;
			if(input.equals(".W")) {
				pre_tag = ".W";
				continue;
			}
			if(pre_tag.equals(".W")){
				// System.out.println(input);
				Pattern pattern = Pattern.compile("([a-zA-Z])\\w+");
				// Pattern pattern = Pattern.compile("([a-zA-Z])+");
	      		Matcher matcher = pattern.matcher(input);
	      		abst = abstracts.get(abstract_id);

	      		while(matcher.find()){
	      			String word = matcher.group().toLowerCase();
	      			if(stop_list.contains(word)) continue;
	      			abst.put(word, abst.getOrDefault(word, 0)+1);
	      		}
	      		abstracts.put(abstract_id, abst);
			}
		}
		br_abstract.close();

		// calculate cosine similarity
		FileWriter output = new FileWriter("output.txt");
		HashMap<Integer, HashMap<String, Double>> abstracts_idf = new HashMap<>();

		try{
			// calculate IDF for abstract
			for(int a_id: abstracts.keySet()){
				abstracts_idf.put(a_id, calculateIDF(abstracts.get(a_id),abstracts));
			}

			for(int q_id: queries.keySet()){
				// query = queries.get(q_id); // current query
				HashMap<String, Double> query_tfidf = new HashMap<>();
				HashMap<Integer, Double> sorted_hm = new HashMap<>();
				query_tfidf = calculateTFIDF(queries.get(q_id), abstracts);

				HashMap<String, Double> ab = new HashMap<>(); 
				for(int a_id: abstracts_idf.keySet()){
					ab = elimination(query_tfidf, abstracts_idf.get(a_id));

					double cosine_sim = cosineSimilarity(query_tfidf, ab);
					sorted_hm.put(a_id, cosine_sim);
				}

				sorted_hm = sortByValue(sorted_hm);
				for(int id: sorted_hm.keySet()){
					output.write(q_id + " " + id + " " + sorted_hm.get(id) +"\n");
					// System.out.println(q_id + " " + id + " " + sorted_hm.get(id));
				}
			}
			output.close();
		}
		catch (IOException e) {
	    		System.out.println("An error occurred.");
	    		e.printStackTrace();
	    }
	}



	public static HashMap<String, Double> calculateTFIDF(HashMap<String, Integer> hm, HashMap<Integer, HashMap<String, Integer>> abstracts){
		HashMap<String, Double> output = new HashMap<>();
		for(String word: hm.keySet()){
			int term_frequency = hm.get(word);
			double inverse_frequency = calculateIDF(word, abstracts);
			double tfide = term_frequency*inverse_frequency;
			output.put(word, tfide);
		}
		return output;
		
	}

	public static double calculateIDF(String word, HashMap<Integer, HashMap<String, Integer>> abstracts){
		int occurrence = 0;
		for(int id: abstracts.keySet()){
			HashMap<String, Integer> hm = abstracts.get(id);
			if(hm.containsKey(word)){
				occurrence++;
				continue;
			}
		}
		if(occurrence == 0) return 0.0;
		return Math.log((double)total_docs/occurrence);

	}

	// this function is to calculate the IDF for abstracts document
	public static HashMap<String, Double> calculateIDF(HashMap<String, Integer> hm, HashMap<Integer, HashMap<String, Integer>> abstracts){
		HashMap<String, Double> storage = new HashMap<>();

		for(String word: hm.keySet()){ // hm = the current abstract document
			if(doc_IDF.containsKey(word)){
				storage.put(word, doc_IDF.get(word));
				continue;
			}
			int occurrence = 0;
			for(int id: abstracts.keySet()){
				HashMap<String, Integer> hm2 = abstracts.get(id);
				if(hm2.containsKey(word)){
					occurrence++;
					continue;
				}
			}
			// occurrence is guaranteed to be >= 1
			double idf = Math.log((double)total_docs/occurrence);
			storage.put(word, idf);
			doc_IDF.put(word, idf);
		}
		return storage;
	}

	public static HashMap<String, Double> elimination(HashMap<String, Double> query, HashMap<String, Double> abst){
		HashMap<String, Double> output = new HashMap<>();
		for(String word: query.keySet()){
			if(abst.containsKey(word)){
				output.put(word, abst.get(word));
			}
			else{
				output.put(word, (double)0.0);
			}
		}
		return output;
	}


	public static double cosineSimilarity(HashMap<String, Double> query, HashMap<String, Double> abst){
		double q_sum = 0;
		double d_sum = 0;
		double numerator = 0;
		double denominator = 0;
		for(String word: query.keySet()){
			double q = query.get(word);
			double d = abst.get(word);
			numerator+= q*d;
			q_sum += Math.pow(q,2);
			d_sum += Math.pow(d,2);
		}
		double total = q_sum*d_sum;
		denominator = (double)Math.sqrt(total);
		if(denominator == 0 || numerator == 0) return (double) 0.0;

		return (double)numerator/denominator;
	}


    public static HashMap<Integer, Double> sortByValue(HashMap<Integer, Double> hm){ 
    	List<Map.Entry<Integer, Double>> list = new LinkedList<>(hm.entrySet()); 
  
        Collections.sort(list, new Comparator<Map.Entry<Integer, Double> >() { 
            public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2){ 
            	return (o2.getValue()).compareTo(o1.getValue());
            } 
        }); 
          
        HashMap<Integer, Double> temp = new LinkedHashMap<Integer, Double>(); 
        for (Map.Entry<Integer, Double> aa : list) { 
            temp.put(aa.getKey(), aa.getValue()); 
        }
        return temp; 
    }
}