let advanceSearch = $("#advance_search_form");

function handleSearchResult(resultData) {
    // Populate the movie table
    // Find the table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");
    movieTableBodyElement.empty();
    console.log(resultData);
    console.log(movieTableBodyElement);
    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < Math.min(10, resultData.length); i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML +=
            "<th>" +
            '<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '">'
            + resultData[i]["movie_title"] +
            '</a>' +
            "</th>";

        // rowHTML += "<th>" + resultData[i]["movie_title"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
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

    $.ajax("api/search", {
        method: "GET",
        data: advanceSearch.serialize(),
        success: handleSearchResult
    });
}

advanceSearch.submit(handleSearchInfo);