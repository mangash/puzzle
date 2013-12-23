puzzle
======

Problem Description:

Write a program that analyses a set of documents for the “key-terms” in those documents.
Your program will be run with a single command line argument, a path to a folder containing a list of plain text files.
The list of files can be small, say 10 for example, or very large, say 1,000,000 files. Each text file will contain just the text of the document without any other identifying information like title, author, or date.

Your program should output the key terms for each document in the folder provided as well as the 5 to 9 terms that best
represent the corpus of documents in the input folder.  Your program should take into account memory footprint and performance considerations.  It is preferred but not required to have your program leverage multi-processing techniques like Java’s ForkJoin or Python’s multiprocessing library.  We recommend using text from Wikipedia as a sample input into your program for testing and validation.  When submitting the solution, if you used test input data, please make sure to submit it as well.

Solution Description:

This program is based on the tf-idf algorithm, with some additions. For instance, an english stop-words list is used to
filter out common unuseful terms prior to mapping them.
(More info on the algorithm can be found here: http://en.wikipedia.org/wiki/Tf-idf)

Also, the implementation of the ExtractKeyTerms Class utilizes Java's ForkJoin for multiprocessing purposes.
