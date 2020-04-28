## Project2

### The final version

### 1. Demo video URL: https://youtu.be/cKRI9VabjFE


### 2.  Deploy application with Tomcat:
The method that we deploy our application with tomcat is the same as the first method which professor told in lecture. 
Our application generate a .war file, after we login 'manager app' at tomcat main page, we add this .war file and deploy it.
The detail can also be seen in the demo video above.

### 3. Substring matching design:
In this project, there are lots of html, js and java files, I will explain them one by one:

1. login.html, login.js and LoginServlet files will enable user to login, only when the email and password that provided by user can match any of the record in our database, it is not hard code as the example code. LoginServle will handle that and return a string which contain "status": "success"/"fail", and then login.js can based on the status to decided what to display, what is more, LoginFilter can make sure that user can not go to index page before login.

2. Indext.html, index.js and HomeServlet display the landing page(index page), which has links to search, browse and check out(cart) page. This page is sample, HomeServlet find the 8 most highest rating movies and index.js handle the result and display them.

3. Single-movie.html, single-movie.js and SingleMovieServlet are part of project 1, this time we add some jump links: we can jump to movie-list page directly, and add it to cart, this page also have the header part(just like other page except login page) which can directly go to check out, search, home, browse pages.

4. Single-star.html, single-star.js and SingleStarServlet are also part of project 1, this time we add some jump links just as single-movie page described above.

5. adv-search.html, adv-search.js and AdvSearchServlet are tricky part for us, because we want to show all the search and browse result on the movie-list page, so in this part, NOT as usual, AdvSearchServlet do not output all the result infomation, instead, it stores all the search information into a root key:"movieParameter", which also contain a value:"status", in this case, its value is "adv-search", which can be used to distinguish between different browse and search in movie-list page. Thus, the movie-list page is in response to show all the results of browsing and searching, but not the AdvSearchServlet, it only store infomation into session.
##### Some details in AdvSearchServlet: we used blurry search, add "%" ahead and behind 'title', 'director' and 'starName'.

6. Browse.html, browse.js and BrowseServlet handle the browsing by genres and title part. BrowseServlet is to get all the genres and its id from database not just hard code all the genres. In this page, it would show all the genres and first letter of title. What is more, we also need BrowseByGenresServlet and BrowseByTitleServlet to store the browsing by genres and by title information into session, and just as adv-search above, the movie-list servelt is to handle the search result from database step. And the browse.js would redirect to movie-list.html.
##### Some details in BrowseByTitleServlet: When deal with "*" case which is non-alphanumeric characters, we use a new java file: browseOtherServlet to handle it using regular search of sql. 

7. Movie-list.html, movie-list.js and MovieListServlet handle the search and browse result. MovieListServlet.java did a lot of work including: getting the total number of results to handle the pagination; getting the adv-search result, browse by genres and browse by title result, by different "status" stored in session. Then the movie-list.js can get all the result data, it can display them. In this page, we also includes pagination, sortBy and  pageNumber limit change functionality, and they are   triggered by jquery and sent to corresponding server and are handled by PageServlet, SortServlet and LimitServlet  correspondingly. 

8. Cart.html, cart.js and cardServlet handle the check out page. In the cardServlet, we still use the idea to store thing in session and also output them, and cart.js would display this item in cart. Delete and update are handled by DeleteCartItem and UpdateCartItemQuantity java files, and the movie Id are also sent to server side to tell server which one need to delete or update.
 
9. Payment.html, payment.js and paymentInfoServlet are to handle place order, it will require user provide creditcard information. When user place the order, it would check whether the info is correct by paymentInfoServlet, it will return "status" success or fail, when it is success, we would redirect to paymentSuccessful.html and show some confirmations.

10. Test.css is used to beauity our web applications, include some padding, margin and background image.



### 4. Contribution of each member:
Member 1: Haoming Gao:
1. Finished adv-search functionality, browse functionality, pagination and sortBy dropbox, movieListServlet and movie-list.js, he also did the step when payment success, insert info into 'sales' table. And also some jump links, update in check out page, and login page. Added some css features.
2. Finished the Demo video.

Member 2: Zongchang Lyu:
1. Finished drop box of browse by genres, cart check out page, delete and update item functionality in cart page and payment page. He also did some part of movieListServelt, some part of adv-search functionality and some jump links. Added some css features.
2. Finished README file.

It can be seen that some of works are overlapped, because we do not seperate the work precisely, in many part, both of us did a different version, and the final version is just one of them. For more details, it can be check by commit records.



