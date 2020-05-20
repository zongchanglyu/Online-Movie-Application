/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */


/*
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleMoviesResult(resultData) {
    console.log("handleMoviesResult: populating movie table from resultData");
    console.log(resultData[0]);
    $("#sortOrder").find("option[value='" + resultData[0]['orderBy'] + "']").attr("selected",true);

    $("#resultLimit").find("option[value='" + resultData[0]['numberOfList'] + "']").attr("selected",true);

    let currentPage = resultData[0]['page'];
    let numOfData = resultData[0]['numOfData'];
    let numberOfList = resultData[0]['numberOfList'];

    let numOfPage = Math.floor(numOfData / numberOfList);

    if(currentPage == 0){
        $("#prev").attr('disabled',true);
    }
    if(currentPage == numOfPage){
        $("#next").attr('disabled',true);
    }
    let pageBodyElement = $("#page");
    pageBodyElement.append(parseInt(currentPage) + 1);
    pageBodyElement.append(" / ");
    pageBodyElement.append(parseInt(numOfPage) + 1);

    // Populate the movie table
    // Find the empty table body by id "movie_table_body"
    let starTableBodyElement = jQuery("#movie_table_body");

    // Iterate through resultData, no more than 20 entries
    for (let i = 0; i < resultData.length; i++) {

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML +=
            "<th>" +
            // Add a link to single-movie.html with id passed with GET url parameter
            '<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '">'
            + resultData[i]["movie_title"] +     // display movie_title for the link text
            '</a>' +
            "</th>";
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";

        rowHTML += "<th>";
        if(resultData[i]["genres_name"].length == 0){
            rowHTML += "N/A";
        }else{
            for(let g = 0; g < Math.min(3, resultData[i]["genres_name"].length); g++){
                rowHTML +=
                    '<a href="browse.html?genre-id=' + resultData[i]['genres_name'][g]['genre_id'] + '">'
                    + resultData[i]["genres_name"][g]["genre_name"] + "<br>";
            }
            rowHTML = rowHTML.substring(0, rowHTML.length - 4);
        }
        rowHTML += "</th>";

        rowHTML += "<th>";
        if(resultData[i]["stars_name"].length == 0){
            rowHTML += "N/A";
        }else {
            for (let s = 0; s < Math.min(3, resultData[i]["stars_name"].length); s++) {
                // Add a link to single-movie.html with id passed with GET url parameter
                rowHTML += '<a href="single-star.html?id=' + resultData[i]["stars_name"][s]["star_id"] + '">'
                rowHTML += resultData[i]["stars_name"][s]["star_name"] + "<br>";
            }
            rowHTML = rowHTML.substring(0, rowHTML.length - 4);
        }
        rowHTML += "</th>";

        rowHTML += "<th>" + resultData[i]["rating"] + "</th>";

        rowHTML +=
            "<th>" +

            '<a href="cart.html?method=add&id=' + resultData[i]['movie_id'] + '">'
            + "Add to Cart"
            '</a>' +
            "</th>";

        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        starTableBodyElement.append(rowHTML);
    }
}

function redirectToMovieListPage(){
    window.location.replace("movie-list.html");
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

$(" #sortOrder").change(function(){
    let options=$("#sortOrder");
    let value = options.val();
    // let text = options.text();
    // alert("value = " + value);
    // alert("text = " + text);
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/sort-change?orderBy=" + value, // Setting request url, which is mapped by StarsServlet in MoviesServlet.java
        success: redirectToMovieListPage // Setting callback function to handle data returned successfully by the StarsServlet
    });
});

$(" #resultLimit").change(function(){
    let options=$("#resultLimit");
    let value = options.val();
    // let text = options.text();
    // alert("value = " + value);
    // alert("text = " + text);
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/limit-change?numberOfList=" + value, // Setting request url, which is mapped by StarsServlet in MoviesServlet.java
        success: redirectToMovieListPage // Setting callback function to handle data returned successfully by the StarsServlet
    });
});

$('#prev').click(function(){
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/page?page=prev", // Setting request url, which is mapped by StarsServlet in MoviesServlet.java
        success: redirectToMovieListPage // Setting callback function to handle data returned successfully by the StarsServlet
    });
});

$('#next').click(function(){
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/page?page=next", // Setting request url, which is mapped by StarsServlet in MoviesServlet.java
        success: redirectToMovieListPage // Setting callback function to handle data returned successfully by the StarsServlet
    });
});

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/movie-list", // Setting request url, which is mapped by StarsServlet in MoviesServlet.java
    success: (resultData) => handleMoviesResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});