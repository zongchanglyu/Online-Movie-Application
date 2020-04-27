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

    console.log("handleResult: populating star info from resultData");

    // populate the star info h3
    // find the empty h3 body by id "star_info"
    let starInfoElement = jQuery("#movie_info");

    // append two html <p> created to the h3 body, which will refresh the page
    let firstRowHTML = "";
    firstRowHTML += "<p>Movie Title: " + resultData["movie_title"] + "</p>" +
        "<p>Release Year: " + resultData["movie_year"] + "</p>" +
        "<p>Director: " + resultData["movie_director"] + "</p>" +
        "<p>Rating: " + resultData["movie_rating"] + "</p>";

    firstRowHTML += "<p>Genre: ";

    for(let g = 0; g < resultData["genres"].length; g++){
        firstRowHTML +=
                // Add a link to single-star.html with id passed with GET url parameter
                '<a href="browse.html?genre-id=' + resultData["genres"][g]["genre_id"] + '">'
                + resultData["genres"][g]["genre_name"] +     // display star_name for the link text
                '</a>';

        firstRowHTML += ", ";
    }
    firstRowHTML = firstRowHTML.substring(0, firstRowHTML.length - 2);
    firstRowHTML += "</p>";

    firstRowHTML +=
        '<a class="btn btn-info" href="cart.html?method=add&id=' + resultData['movie_id'] + '">'
        + "Add to Cart"
        '</a>';

    starInfoElement.append(firstRowHTML);

    console.log("handleResult: populating movie table from resultData");

    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < resultData["stars"].length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";

        rowHTML +=
            "<th>" +
            // Add a link to single-star.html with id passed with GET url parameter
            '<a href="single-star.html?id=' + resultData["stars"][i]['star_id'] + '">'
            + resultData["stars"][i]["star_name"] +     // display star_name for the link text
            '</a>' +
            "</th>";

        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let movieId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});