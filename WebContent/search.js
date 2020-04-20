let advanceSearch = $("#advance_search_form");



function handleSearchResult(resultData) {
    console.log("handleResult: populating star info from resultData");

    // populate the star info h3
    // find the empty h3 body by id "star_info"
    // let starInfoElement = jQuery("#star_info");
    //
    // // append two html <p> created to the h3 body, which will refresh the page
    // starInfoElement.append("<p>Star Name: " + resultData[0]["star_name"] + "</p>" +
    //     "<p>Date Of Birth: " + resultData[0]["star_dob"] + "</p>");

    console.log("handleResult: populating movie table from resultData");

    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < Math.min(10, resultData.length); i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        // rowHTML +=
        //     "<th>" +
        //     '<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '">'
        //     + resultData[i]["movie_title"] +
        //     '</a>' +
        //     "</th>";

        rowHTML += "<th>" + resultData[i]["movie_title"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
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