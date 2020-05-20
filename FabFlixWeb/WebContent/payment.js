
let price = getParameterByName('price');
let totalPrice = jQuery("#price");
totalPrice.append("The Total Price is: " + price);

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



function handlePaymentResult(resultDataString) {
    console.log(resultDataString);
    // let resultDataJson = JSON.parse(resultDataString);
    let resultDataJson = resultDataString;
    console.log("handle search response");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);
    // if (resultDataJson["status"] === "success") {
    //     window.location.replace("movie-list.html");
    // }
    if(resultDataString["status"]=== "fail") {
        alert("payment fail!");
        window.location.replace("payment.html?price=" + price);
    }else{
        window.location.replace("paymentSuccessful.html");
    }
}

function handlePaymentInfo(searchEvent) {
    console.log("submit payment form");
    const url = "api/payment-info";
    console.log("api/payment-info");
    console.log(url);
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    searchEvent.preventDefault();

    $.ajax("api/payment-info", {
        method: "POST",
        data: paymentForm.serialize(),
        success: handlePaymentResult
    });
}

paymentForm.submit(handlePaymentInfo);