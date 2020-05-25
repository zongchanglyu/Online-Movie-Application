## Project4

### The final version

### 1. Demo video URL: https://youtu.be/qHCzYn4iMgE


### 2.  Deploy application with Tomcat:
The method that we deploy our application with tomcat is the same as the first method which professor told in lecture. 
Our application generate a .war file, after we login 'manager app' at tomcat main page, we add this .war file and deploy it.
The detail can also be seen in the demo video above.

### 3. A brief explanation of fuzzy search
In our project. we only design and implement fuzzy search for Android search. 
For this task, we we add a new servlet(MobileSearch.java) to handle it so that it could be faster. 
And the url is: 
https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-114/blob/0a986df74ae3d58b0cc62649373745019b029344/FabFlixWeb/src/MobileSearch.java
The edit distance we choose is 2, and we use 'or' to take the union of original search and fuzzy search.


### 4. Contribution of each member:
Member 1: Haoming Gao:
1. Mainly focus on Android and fuzzy search.
2. Finished the Demo video.

Member 2: Zongchang Lyu:
1. Mainly focus on Full-text Search and Autocomplete.
2. Finished README file.

Some of works are overlapped, because we do not seperate the work precisely, in many part, both of us did a different version, and the final version is just one of them. For more details, it can be check by the commit records.



