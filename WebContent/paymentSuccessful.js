function handleCardResult(resultData) {
    console.log("handleMoviesResult: populating movie table from resultData");

    // Populate the movie table
    // Find the empty table body by id "movie_table_body"
    let starTableBodyElement = jQuery("#movie_table_body");

    let count = 0;

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
        rowHTML += "<th>" + resultData[i]["price"] + "</th>";


        rowHTML += "<th>" +
            resultData[i]["quantity"] +
            "</th>";

        // item = $("input[id='item']").attr("value");

        count *= 1;
        count += resultData[i]["quantity"] * 1;

        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        starTableBodyElement.append(rowHTML);
    }


    let totalCost = jQuery("#totalCost");
    totalCost.append("Grand Total: " + count * 19.50);
}

function addRedirectToCartPage() {
    alert("add to cart successful!");
    window.location.replace("cart.html");
}

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/paymentSuccessful",
    success: (resultData) => handleCardResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});