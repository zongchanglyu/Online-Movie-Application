## Project2

### The final version

### 1. Demo video URL: 


### 2.  Deploy application with Tomcat:
The method that we deploy our application with tomcat is the same as the first method which professor told in lecture. 
Our application generate a .war file, after we login 'manager app' at tomcat main page, we add this .war file and deploy it.
The detail can also be seen in the demo video above.

### 3. All queries with Prepared Statements：
Almost every servlet java file need sql query, and most of these querys need parameter sent from browser, all of them we used prepared statements. These are files which have prepare statement:
xmlParse.java: https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-114/blob/beb7826e868aa82fb4012376db9966c9962056b1/src/main/java/xmlParse.java

AddNewStarServlet.java: https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-114/blob/beb7826e868aa82fb4012376db9966c9962056b1/src/AddNewStarServlet.java

CartServlet.java: https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-114/blob/beb7826e868aa82fb4012376db9966c9962056b1/src/CartServlet.java

EmpLoginServlet.java: https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-114/blob/beb7826e868aa82fb4012376db9966c9962056b1/src/EmpLoginServlet.java

HomeServlet.java: https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-114/blob/beb7826e868aa82fb4012376db9966c9962056b1/src/HomeServlet.java

LoginServlet.java : https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-114/blob/beb7826e868aa82fb4012376db9966c9962056b1/src/LoginServlet.java

MovieListServlet.java : https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-114/blob/beb7826e868aa82fb4012376db9966c9962056b1/src/MovieListServlet.java

SingleMovieServlet.java : https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-114/blob/beb7826e868aa82fb4012376db9966c9962056b1/src/SingleMovieServlet.java

SingleStarServlet.java: https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-114/blob/ad44c2aebcef4dfdf4feec354fbd783c3d551385/src/SingleStarServlet.java

paymentInfoServlet.java : https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-114/blob/ad44c2aebcef4dfdf4feec354fbd783c3d551385/src/paymentInfoServlet.java


### 4. Two parsing time optimization strategies compared with the naive approach: 
The first time optimization strategie is: Store all the old information(existing data) of movies and stars, so that every time when we want to insert some new data, we do NOT need to first search from database to check whether it already exist, we use some structure like HashMap to store these already existing data, and every time when we insert a new data, we still need to put into the corresponding HashMap structure.

The second time optimization strategie is: Using transaction. Instead of execute every sql query one by one, we using transaction to execute them as a batch, which can be much faster.

To compare: The first time we do not use any optimization, the total time cost is more than 5 minutes.
After we optimize some nested query and using the first optimization strategie, the time cost reduce to 9.163 seconds!!!
And then, after we using both of the two time optimization strategies, the final time cost is 5.697 Seconds.



### 5. Inconsistent data report from parsing: 
We generate 17821 inconsistent data, and used IO to output these data into a file: inconsistentData.txt
the url is: https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-114/blob/ad44c2aebcef4dfdf4feec354fbd783c3d551385/inconsistentData.txt



### 6. Contribution of each member:
Member 1: Haoming Gao:
1. Mainly focus on task1 to task6.
2. Finished the Demo video.

Member 2: Zongchang Lyu:
1. Mainly  focus on task7.
2. Finished README file.

It can be seen that some of works are overlapped, because we do not seperate the work precisely, in many part, both of us did a different version, and the final version is just one of them. For more details, it can be check by the commit records.



