/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */


/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleDashboardResult(resultData) {
    if(resultData["empFullname"] == null){
        $("#emp-content-container").attr('style', 'display: none');
        $("#emp-login-container").attr('style', 'display: block');
    }else{
        $("#emp-content-container").attr('style', 'display: block');
        $("#emp-login-container").attr('style', 'display: none');
        $("#login-employee").append("welcome, " + resultData["empFullname"]);
    }


}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/dashboard", // Setting request url, which is mapped by StarsServlet in MoviesServlet.java
    success: (resultData) => handleDashboardResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});