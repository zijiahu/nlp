# Cranfield_collection
Create a system for an Ad Hoc Information Retrieval task using TF-IDF weights and cosine similarity scores. Vectors should be based on all the words in the query, after removing the members of a list of stop words.


## How to run this program:
Use the following command to run my system.

1. To compile the file, use 
	
	`javac zh1130_HW4.java`

2. To run my system, use 

	`java zh1130_HW4 cran.qry cran.all.1400`

My program takes two command line arguments. The first one is the name of the file that contains all the queries, and the second one is the name of the file that contains all the abstracts. 

3. Check the file named "output.txt" to see the result

## File details:

### `cran.qry`  
This file contains a set of 225 queries numbered 001 through 365, but referred to in cranqrel below as 1 through 225.

- Lines beginning with .I are ids for the queries (001 to 365) 
- Lines following .W are the queries


### `cran.all.1400` 
This file contains 1400 abstracts of aerodynamics journal articles (the document collection)

- Lines beginning with .I are ids for the abstracts
- Lines following .T are titles
- Lines following .A are authors
- Lines following .B are some sort of bibliographic notation 
- Lines following .W are the abstracts


### `cranqrel`
An answer key. Each line consists of three numbers separated by a space

- The first number is the query id (1 through 225) --- convert 001 to 365 by position: 001 --> 1, 002 --> 2, 004 --> 3, ... 365 --> 225
- The second number is the abstract id (1 through 1400)
- The third number is a number (-1,1,2,3 or 4)
	- These numbers represent how related the query is to the given abstract 
	- Unrelated query/abstract pairs are not listed at all (they would get a score of 5): There are 1836 lines in cranqrel. If all combinations were listed, there would be 225 * 1400 = 315,000 lines.
	- We will treat -1 as being the same as 1. We suspect it means something like "the best choice for the query", but the specs don't say.

### `stoplist.txt`  
A list of words that should be eliminated when running this program. They will not be included into the vectors.


### `output.txt`
The output file.

- The first column: query number
- The second column: abstract number
- The third column: cosine similarity score

For each query, the abstracts will be listed in order from highest scoring to lowest scoring, based on cosine-similarity.


### `cranfield_score.py`
This is an implementation of MAP which can be used to score the results. 

Run the script from the command line using the following command:

`python3 cranfield_score.py cranqrel output.txt`

*The scoring program has an optional feature that prints out the scores for each query, simply add an additional argument True:

`python3 cranfield_score.py cranqrel output.txt True`

assuming that the name of the output is called output.txt

This will give you an accuracy score for further debugging and tuning.


