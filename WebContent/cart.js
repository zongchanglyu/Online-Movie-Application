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
                '<a href="cart.html?id=' + resultData[i]['movie_id'] + '&method=plus' + '">' + ' +' + '</a>' +
                '<a href="cart.html?id=' + resultData[i]['movie_id'] + '&method=minus' + '">' + ' - ' + '</a>' +
                "</th>";

            rowHTML +=
                "<th>" +

                '<a href="cart.html?id=' + resultData[i]['movie_id'] + '&method=delete' + '">'
                + "Delete" +
                '</a>' +
                "</th>";

            item = $("input[id='item']").attr("value");

            count *= 1;
            count += resultData[i]["quantity"] * 1;

            rowHTML += "</tr>";

            // Append the row created to the table body, which will refresh the page
            starTableBodyElement.append(rowHTML);
        }


        let totalCost = jQuery("#totalCost");
        totalCost.append("Grand Total: " + count * 19.50);
        let toPayment = jQuery("#toPayment");
        let temp = "";
        temp += '<a class="btn btn-warning" href="payment.html?price=' + count * 19.50 + '">' + 'Go to Payment' + '</a>';
        toPayment.append(temp);
    }
}

function redirectToCartPage() {
    window.location.replace("cart.html");
}

function addRedirectToCartPage() {
    alert("add to cart successful!");
    window.location.replace("cart.html");
}


function add() {
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/card?id=" + movieId,  // Setting request url, which is mapped by StarsServlet in MoviesServlet.java
        // I think it can just redirect to this page(cardPage.html) so that can remove the appended url
        // And also the requirement include "A message should display to indicate success/failure."
        // we can do that using alert("add to cart success") in the new redirect function
        // success: (resultData) => handleCardResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
        success: addRedirectToCartPage
    });
}

function plus() {
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/updateCartItemQuantity?id=" + movieId + "&operate=plus",
        success: redirectToCartPage
    });
}

function minus() {
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/updateCartItemQuantity?id=" + movieId + "&operate=minus",
        success: redirectToCartPage
    });
}

function deleteItem() {
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "POST", // Setting request method
        url: "api/cartDelete?id=" + movieId,
        success: (resultData) => handleCardResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
    });
}

function display() {
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/cartDisplay",
        success: (resultData) => handleCardResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
    });
}

let movieId = getParameterByName('id');
let method = getParameterByName('method');  // including: plus, minus, delete, add

switch(method){
    case "add" : add(); break;
    case "plus" : plus(); break;
    case "minus" : minus(); break;
    case "delete" : deleteItem(); break;
    case null : display();
}
