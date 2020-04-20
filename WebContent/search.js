let advanceSearch = $("#advance_search_form");



function handleMoviesResult(resultData) {
    console.log("handleMoviesResult: populating movie table from resultData");

    // Populate the movie table
    // Find the empty table body by id "movie_table_body"
    let starTableBodyElement = jQuery("#movie_table_body");

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
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["director"] + "</th>";

        rowHTML += "<th>";
        for(let g = 0; g < Math.min(3, resultData[i]["genres_name"].length); g++){
            rowHTML += resultData[i]["genres_name"][g] + "<br>";
        }
        rowHTML = rowHTML.substring(0, rowHTML.length - 4);
        rowHTML += "</th>";

        rowHTML += "<th>";
        for(let s = 0; s < Math.min(3, resultData[i]["stars_name"].length); s++){
            // Add a link to single-movie.html with id passed with GET url parameter
            rowHTML += '<a href="single-star.html?id=' + resultData[i]["stars_name"][s]["star_id"] + '">'
            rowHTML += resultData[i]["stars_name"][s]["star_name"] + "<br>";
        }
        rowHTML = rowHTML.substring(0, rowHTML.length - 4);
        rowHTML += "</th>";

        rowHTML += "<th>" + resultData[i]["rating"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        starTableBodyElement.append(rowHTML);
    }
}

function handleSearchInfo(searchEvent) {
    console.log("submit search form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    searchEvent.preventDefault();

    $.ajax("api/index", {
        method: "GET",
        data: advanceSearch.serialize(),
        success: handleMoviesResult
    });
}

advanceSearch.submit(handleSearchInfo);