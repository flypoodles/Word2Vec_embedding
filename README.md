# CS441_Fall2024
Class repository for CS441 on Cloud Computing taught at the University of Illinois, Chicago in Fall, 2024


---
### name
>Lei Chen 
### email
>lchen230@uic.edu

### netid
>lchen230

---
## Instructions

1. clone the repository into Intelliji IDE
2. go sbt shell from Intelliji
3. run reload and then assembly inside the sbt shell
4. sbt assembly will produce the jar file called "mapreduce.jar" which located at "target/scala-2.13/mapreduce.jar"
5. create an empty directory and copy the jar file to that directory, so you can have a clean state to run the jar file.
6. Download my data sample which is a book called [War and Peace](http://www.textfiles.com/etext/FICTION/warpeace.txt) and placed it into the same directory
7. I will name this input file as "data.txt"
8. create a new directory inside the same directory called "shards" to store the shards of the input file
9. Run the split program by using the command below:
>java -jar mapreduce.jar split path_To_Data path_to_empty_shard_directory

In my case it will be 
> java -jar mapreduce.jar split data.txt shards/
10. Now we are going to upload it to hadoop file system.
11. First lets create a directory on hadoop to store our shards
> hadoop fs -mkdir /shards
12. Now lets put our shards into this directory
> hadoop fs -put shards/* /shards
13. We can now run my map reduce program that computes token embeddings.  Run the command Below:
> hadoop jar mapreduce.jar token /shards /output
14. Now we get fetch the reducer outputs and copy it to our local directory
> hadoop fs -getmerge /output tokenEmbedding.txt
15. This is a csv file which formated like this :  
> token, token occurance, token embedding
16. Now we are going to compute cosine similarity
17. First we need to process TokenEmbedding.txt
> java -jar mapreduce.jar processEmbed tokenEmbedding.txt processed.txt
18. upload this file to hadoop
> hadoop fs -put processed.txt  /
19. run cosine similarity hadoop program and wait for completion:
> hadoop jar mapreduce.jar cosine /processed.txt /output2
20. now copy the result to our local system:
> hadoop fs -getmerge /output2 cosineSim.txt
21. Now we have produced cosine similarity csv file. Below is the format:
> word, most similar word, cosine similarity
22. That's all folks!

