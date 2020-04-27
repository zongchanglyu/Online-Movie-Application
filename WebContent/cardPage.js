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

    if (resultData[0] == null) {
        starTableBodyElement.append("Welcome! Your card is empty, you can go shopping now!");
    } else {
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


            

            rowHTML +=
                "<th>" +

                '<a href="cardPage.html?id=' + resultData[i]['movie_id'] + '&method=delete' + '">'
                + "Delete" +
                '</a>' +
                "</th>";

            count += resultData[i]["quantity"];


            rowHTML += "</tr>";

            // Append the row created to the table body, which will refresh the page
            starTableBodyElement.append(rowHTML);
        }


        let totalCost = jQuery("#totalCost");
        totalCost.append("Grand Total: " + count * 19.50);
    }
}

let movieId = getParameterByName('id');
let method = getParameterByName('method');

if(method==null) {
    if (movieId != null) {
        jQuery.ajax({
            dataType: "json", // Setting return data type
            method: "GET", // Setting request method
            url: "api/card?id=" + movieId,  // Setting request url, which is mapped by StarsServlet in MoviesServlet.java
            success: (resultData) => handleCardResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
        });
    } else {
        jQuery.ajax({
            dataType: "json", // Setting return data type
            method: "GET", // Setting request method
            url: "api/cartDisplay",
            success: (resultData) => handleCardResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
        });
    }
}
else{
    if(method=="delete"){
        jQuery.ajax({
            dataType: "json", // Setting return data type
            method: "POST", // Setting request method
            url: "api/cartDelete?id=" + movieId,
            success: (resultData) => handleCardResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
        });
    }
    if(method=="put"){

    }
}