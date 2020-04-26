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



function handleCardResult(resultData) {
    console.log("handleMoviesResult: populating movie table from resultData");

    // Populate the movie table
    // Find the empty table body by id "movie_table_body"
    let starTableBodyElement = jQuery("#movie_table_body");

    let count = 0;

    // Iterate through resultData, no more than 20 entries
    for (let i = 0; i < Math.min(20, resultData.length); i++) {

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
        rowHTML += "<th>" + resultData[i]["price"] + "</th>";

        rowHTML += "<th>" + resultData[i]["quantity"] + "</th>";

        count += resultData[i]["quantity"];





        // rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        //
        // rowHTML += "<th>";
        // for(let g = 0; g < Math.min(3, resultData[i]["genres_name"].length); g++){
        //     rowHTML += resultData[i]["genres_name"][g] + "<br>";
        // }
        // rowHTML = rowHTML.substring(0, rowHTML.length - 4);
        // rowHTML += "</th>";
        //
        // rowHTML += "<th>";
        // for(let s = 0; s < Math.min(3, resultData[i]["stars_name"].length); s++){
        //     // Add a link to single-movie.html with id passed with GET url parameter
        //     rowHTML += '<a href="single-star.html?id=' + resultData[i]["stars_name"][s]["star_id"] + '">'
        //     rowHTML += resultData[i]["stars_name"][s]["star_name"] + "<br>";
        // }
        // rowHTML = rowHTML.substring(0, rowHTML.length - 4);
        // rowHTML += "</th>";
        //
        // rowHTML += "<th>" + resultData[i]["rating"] + "</th>";
        //
        // rowHTML +=
        //     "<th>" +
        //
        //     '<a href="cardPage.html?id=' + resultData[i]['movie_id'] + '">'
        //     + "Add to Card"
        // '</a>' +
        // "</th>";

        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        starTableBodyElement.append(rowHTML);
    }

    let totalCost = jQuery("#totalCost");
    totalCost.append("Grand Total: "+ count*19.99);

}


let movieId = getParameterByName('id');

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/card?id=" + movieId,  // Setting request url, which is mapped by StarsServlet in MoviesServlet.java
    success: (resultData) => handleCardResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});