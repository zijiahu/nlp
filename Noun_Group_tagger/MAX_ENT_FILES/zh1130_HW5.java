import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
/*
Suggested features:
1. Features of the word itself: 
	- POS
	- the word itself
	- stemmed version of the word
2. Similar features of previous and/or following words 
	- use the features of previous word
	- 2 words back 
	- following word
	- 2 words forward
3. Beginning/Ending Sentence
	- at the beginning of the sentence, omit features of 1 and 2 words back; 
	- at end of sentence, omit features of 1 and 2 words forward
4. capitalization, features of the sentence, your own special dictionary, etc.
*/

public class zh1130_HW5{

	public static void main(String[] args) throws Exception{

		File filepath = null;
		String output_file = "";
		boolean containsBIO = false;

		if (0 < args.length && args.length < 4) {
		   filepath = new File(args[0]);
		   output_file = args[1];
		   containsBIO = Boolean.parseBoolean(args[2]);
		} 
		else {
		   System.err.println("Invalid arguments count:" + args.length);
		   System.exit(-1);
		}

		BufferedReader br = new BufferedReader(new FileReader(filepath));
		FileWriter output = new FileWriter(output_file);
		

		String input = "";
		LinkedList<Line> lists = new LinkedList<>();
		boolean newLine = false;

		while((input = br.readLine()) != null){
				Line current;
				if(input.equals("")){
					current = new Line("","","");
					current.set_blank(true);
					newLine = true;
					if(!lists.isEmpty()) lists.getLast().set_end(true);
					lists.addLast(current);
					continue;
				}
				else{
					String[] line = input.split("\t");
					String word = line[0];
					String pos = line[1];
					String bio = containsBIO ? line[2] : "";

					current = new Line(word, pos, bio);
				}

				if(newLine){
					current.set_beginning(true);
					newLine = false;
				}

				Line previous = lists.getLast();
				if(previous.get_blank()){
					current.set_beginning(true);
				}

				previous.set_next_word(current.get_word());
				previous.set_next_pos(current.get_pos());
				current.set_pre_word(previous.get_word());
				current.set_pre_pos(previous.get_pos());

				if(lists.size() >=2){
					Line pre_previous = lists.get(lists.lastIndexOf(previous)-1);
					current.set_pre_pre_word(pre_previous.get_word());
					current.set_pre_pre_pos(pre_previous.get_pos());
					pre_previous.set_next_next_word(current.get_word());
					pre_previous.set_next_next_pos(current.get_pos());
				}

				lists.addLast(current);

				if(lists.size() >= 3){
					String output_line = getOutput(lists.removeFirst(), containsBIO);
					output.write(output_line);
				}
		}
		while(!lists.isEmpty()){
			String output_line = getOutput(lists.removeFirst(), containsBIO);
			output.write(output_line);
		}
		output.close();
	}


	public static String getOutput(Line line, boolean containsBIO){
		String output_line = "";

		if(line.get_blank()){
			output_line = "\n";
			return output_line;
		}

		if(line.get_end()){
			output_line = line.get_word() + "\t"
			+ "POS=" + line.get_pos() + "\t"
			+ "pre_WORD=" + line.get_pre_word() + "\t"
			+ "pre_POS=" + line.get_pre_pos() + "\t"
			+ "pre_pre_WORD=" + line.get_pre_pre_word() + "\t"
			+ "pre_pre_POS=" + line.get_pre_pre_pos();

			if(containsBIO) output_line+= "\tpre_bio=@@" + "\t" + line.get_bio();

			return output_line+ "\n";
		}
		
		if(line.get_beginning()){
			output_line = line.get_word() + "\t"
			+ "POS=" + line.get_pos() + "\t"
			+ "next_word=" + line.get_next_word() + "\t"
			+ "next_pos=" + line.get_next_pos() + "\t"
			+ "next_next_word=" + line.get_next_next_word() + "\t"
			+ "next_next_pos=" + line.get_next_next_pos();

			if(containsBIO) output_line+= "\tpre_bio=@@"+ "\t" + line.get_bio();
			return output_line+"\n";
		}

		output_line = line.get_word() + "\t"
		+ "POS=" + line.get_pos() + "\t"
		+ "pre_WORD=" + line.get_pre_word() + "\t"
		+ "pre_POS=" + line.get_pre_pos() + "\t"
		+ "pre_pre_WORD=" + line.get_pre_pre_word() + "\t"
		+ "pre_pre_POS=" + line.get_pre_pre_pos() + "\t"
		+ "next_word=" + line.get_next_word() + "\t"
		+ "next_pos=" + line.get_next_pos() + "\t"
		+ "next_next_word=" + line.get_next_next_word() + "\t"
		+ "next_next_pos=" + line.get_next_next_pos();
		
		if(containsBIO) output_line+= "\tpre_bio=@@"+ "\t" + line.get_bio();
		return output_line+"\n";
	}
}

class Line{
	private String word;
	private String pos;
	private String bio;
	private String pre_pre_pos;
	private String pre_pos;
	private String pre_pre_word;
	private String pre_word;
	private String next_next_word;
	private String next_word;
	private String next_next_pos;
	private String next_pos;
	private boolean end = false;
	private boolean beginning = false;
	private boolean blank = false;

	private String pre_pre_bio;
	private String pre_bio;
	private String next_next_bio;
	private String next_bio;

	public Line(String word, String pos, String bio){
		this.set_word(word);
		this.set_pos(pos);
		this.set_bio(bio);
	}

	public Line(String word, String pos){
		this.set_word(word);
		this.set_pos(pos);
	}

	public String get_word(){
		return this.word;
	}
	public void set_word(String word){
		this.word = word;
	}

	public String get_pos(){
		return this.pos;
	}
	public void set_pos(String pos){
		this.pos = pos;
	}

	public String get_bio(){
		return this.bio;
	}
	public void set_bio(String bio){
		this.bio = bio;
	}

	public void set_pre_pre_pos(String pre_pre_pos){
		this.pre_pre_pos = pre_pre_pos;
	}
	public String get_pre_pre_pos(){
		return this.pre_pre_pos;
	}

	public void set_pre_pos(String pre_pos){
		this.pre_pos = pre_pos;
	}
	public String get_pre_pos(){
		return this.pre_pos;
	}

	public void set_pre_pre_word(String pre_pre_word){
		this.pre_pre_word = pre_pre_word;
	}
	public String get_pre_pre_word(){
		return this.pre_pre_word;
	}

	public void set_pre_word(String pre_word){
		this.pre_word = pre_word;
	}
	public String get_pre_word(){
		return this.pre_word;
	}

	public void set_next_next_word(String next_next_word){
		this.next_next_word = next_next_word;
	}
	public String get_next_next_word(){
		return this.next_next_word;
	}

	public void set_next_word(String next_word){
		this.next_word = next_word;
	}
	public String get_next_word(){
		return this.next_word;
	}

	public void set_next_next_pos(String next_next_pos){
		this.next_next_pos = next_next_pos;
	}
	public String get_next_next_pos(){
		return this.next_next_pos;
	}

	public void set_next_pos(String next_pos){
		this.next_pos = next_pos;
	}
	public String get_next_pos(){
		return this.next_pos;
	}

	public void set_end(boolean end){
		this.end = end;
	}
	public boolean get_end(){
		return this.end;
	}

	public void set_beginning(boolean beginning){
		this.beginning = beginning;
	}
	public boolean get_beginning(){
		return this.beginning;
	}

	public void set_blank(boolean blank){
		this.blank = blank;
	}
	public boolean get_blank(){
		return this.blank;
	}

	public void set_next_next_bio(String next_next_bio){
		this.next_next_bio = next_next_bio;
	}
	public String get_next_next_bio(){
		return this.next_next_bio;
	}

	public void set_next_bio(String next_bio){
		this.next_bio = next_bio;
	}
	public String get_next_bio(){
		return this.next_bio;
	}

	public void set_pre_pre_bio(String next_next_bio){
		this.next_next_bio = next_next_bio;
	}
	public String get_pre_pre_bio(){
		return this.next_next_bio;
	}

	public void set_pre_bio(String pre_bio){
		this.pre_bio = pre_bio;
	}
	public String get_pre_bio(){
		return this.pre_bio;
	}

}