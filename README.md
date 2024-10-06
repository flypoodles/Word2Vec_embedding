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
___
## Video :
> https://www.youtube.com/watch?v=URqDp_ij9r4&ab_channel=LeiChen

___
1. clone the repository into Intelliji IDE
2. go sbt shell from Intelliji
3. run reload and then assembly inside the sbt shell
4. sbt assembly will produce the jar file called "mapreduce.jar" which located at "target/scala-2.13/mapreduce.jar"
5. create an empty directory and copy the jar file to that directory, so you can have a clean state to run the jar file.
6. Download my data sample which is a book called [War and Peace](http://www.textfiles.com/etext/FICTION/warpeace.txt) and placed it into the same directory
> curl http://www.textfiles.com/etext/FICTION/warpeace.txt > data.txt
7. I will name this input file as "data.txt"
8. create a new directory inside the same directory called "shards" to store the shards of the input file
9. Run the split program by using the command below:
>java -jar mapreduce.jar split path_To_Data path_to_empty_shard_directory

In my case it will be 
> java -jar mapreduce.jar split data.txt shards/

10. create a bucket on amazon emr. I call my bucket : leichen-cs441-hw1

11. upload shards directory and jar file into this bucket
12. create a EMR cluster with default settings. Make sure this cluster has access to all buckets!
13. add a step to compute computes token embeddings.
* Run the step with the uploaded jar and shards. Below is the argument for this step
>token s3://leichen-cs441-hw1/shards/ s3://leichen-cs441-hw1/output

14. After the step is completed. Manually download all reducer files from the 
> s3://leichen-cs441-hw1/output
15. After download all the reducer files, I will merge them using my local hadoop file system.
16. First I created a directory on hadoop file system to store my reducer outputs
> hadoop fs -mkdir /output
17. Now I copy all the reducer output files to hadoop directory
> hadoop fs -put part-* /output

18. Then I merge the output files and saved it back to local

> hadoop fs -getmerge /output embedding.txt

* Now we finished computing token embedding and token occurance. 
* Each line in embedding.txt will have this format :  
> token, token occurance, token embedding

19. Now we need to compute cosine similarity. Before we do that we have to preprossed the embedding.txt

> java -jar mapreduce.jar processEmbed embedding.txt processed.txt

20. now upload the processed.txt to aws.

21. Add another step to the cluster to run cosine similarity map/reduce. Below are the arguments

> cosine s3://leichen-cs441-hw1/processed.txt s3://leichen-cs441-hw1/output2

22. Wait for the task to complete. After we done we download the reducer output.
23. The reducer output will in a csv format.  Each line contains
> word, similar word, cosine similarity
22. That's all folks!

