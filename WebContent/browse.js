/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {

    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let browseGenreBodyElement = jQuery("#browse-genre-body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < resultData.length; i++) {
        let rowHTML = "";

        rowHTML +=
            // Add a link to single-star.html with id passed with GET url parameter
            '<a href="browse.html?genre-id=' + resultData[i]['genre_id'] + '">'
            + resultData[i]["genre_name"] +     // display star_name for the link text
            '</a>';

        if((i + 1) % 6 == 0){
            rowHTML += "<br>";
        }else if(i != resultData.length - 1){
            rowHTML += " | ";
        }

        // Append the row created to the table body, which will refresh the page
        browseGenreBodyElement.append(rowHTML);
    }

    let browseTitleBodyElement = jQuery("#browse-title-body");
    // Concatenate the html tags with resultData jsonObject to create table rows
    let rowHTML = "";
    for (let i = 65; i <= 90; i++) {
        let tmp = String.fromCharCode(i);
        rowHTML +=
            // Add a link to single-star.html with id passed with GET url parameter
            '<a href="browse.html?first-later=' + tmp + '">'
            + tmp +     // display star_name for the link text
            '</a>';
        //  a b c d e f g h
        // i j k l m n o p q
        // r s t u v w x y z
        // 0 1 2 3 4 5 6 7 8 9
        if(tmp == 'H' || tmp == 'Q' || tmp == 'Z'){
            rowHTML += "<br>";
        }else{
            rowHTML += " | ";
        }
    }

    for(let i = 0; i <= 9; i++){
        rowHTML +=
            // Add a link to single-star.html with id passed with GET url parameter
            '<a href="browse.html?first-later=' + i + '">'
            + i +     // display star_name for the link text
            '</a>';

        if(i == 9){
            rowHTML += "<br>";
        }else{
            rowHTML += " | ";
        }
    }
    rowHTML += '<a href="browse.html?first-later=*">'
        + " * " +     // display star_name for the link text
        '</a>';
    // Append the row created to the table body, which will refresh the page
    browseTitleBodyElement.append(rowHTML);
}

function redirectToMovieListPage(){
    window.location.replace("movie-list.html");
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let genreId = getParameterByName('genre-id');

if(genreId != null){
    // Makes the HTTP GET request and registers on success callback function handleResult
    jQuery.ajax({
        dataType: "json",  // Setting return data type
        method: "GET",// Setting request method
        url: "api/browse-by-genre?id=" + genreId, // Setting request url, which is mapped by StarsServlet in Stars.java
        // success: (resultData) => handleBrowseByGenreResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
        success: redirectToMovieListPage
    });
}

let firstLater = getParameterByName('first-later');

if(firstLater != null){
    if(firstLater == "*"){
        jQuery.ajax({
            dataType: "json",  // Setting return data type
            method: "GET",// Setting request method
            url: "api/browse-other", // Setting request url, which is mapped by StarsServlet in Stars.java
            // success: (resultData) => handleBrowseByGenreResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
            success: redirectToMovieListPage
        });
    }else{
        // Makes the HTTP GET request and registers on success callback function handleResult
        jQuery.ajax({
            dataType: "json",  // Setting return data type
            method: "GET",// Setting request method
            url: "api/browse-by-title?first-later=" + firstLater, // Setting request url, which is mapped by StarsServlet in Stars.java
            // success: (resultData) => handleBrowseByGenreResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
            success: redirectToMovieListPage
        });
    }
}

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/browse", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});