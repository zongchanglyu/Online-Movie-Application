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
            '<a href="browse.html?id=' + resultData[i]['genre_id'] + '">'
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
}

function redirectToMovieListPage(){
    // console.log(resultDataString);
    // // let resultDataJson = JSON.parse(resultDataString);
    // let resultDataJson = resultDataString;
    // console.log("handle search response");
    // console.log(resultDataJson);
    // console.log(resultDataJson["status"]);
    // if (resultDataJson["status"] === "success") {
    //     window.location.replace("movie-list.html");
    // }
    window.location.replace("movie-list.html");
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let genreId = getParameterByName('id');
console.log(genreId);

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

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/browse", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});