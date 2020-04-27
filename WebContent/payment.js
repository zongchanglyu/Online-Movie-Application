
let price = getParameterByName('price');
let totalPrice = jQuery("#price");
totalPrice.append("The Total Price are: " + price);

let paymentForm = $("#payment_form");

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



function handleSearchResult(resultDataString) {
    console.log(resultDataString);
    // let resultDataJson = JSON.parse(resultDataString);
    let resultDataJson = resultDataString;
    console.log("handle search response");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);
    // if (resultDataJson["status"] === "success") {
    //     window.location.replace("movie-list.html");
    // }
    window.location.replace("movie-list.html");

}

function handleSearchInfo(searchEvent) {
    console.log("submit search form");
    const url = "api/adv-search";
    console.log("api/adv-search");
    console.log(url);
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    searchEvent.preventDefault();

    $.ajax("api/adv-search", {
        method: "GET",
        data: paymentForm.serialize(),
        success: handleSearchResult
    });
}

paymentForm.submit(handleSearchInfo);