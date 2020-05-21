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
        let metadata_table_body = $("#metadata_table_body");
        for (let i = 0; i < resultData["metadata"].length; i++) {
            let rowHTML = "";
            rowHTML += "<tr>";
            rowHTML += "<th>" + resultData["metadata"][i]["table_name"] + "</th>";
            rowHTML += "<th>" + resultData["metadata"][i]["attributes"] + "</th>";
            rowHTML += "</tr>";
            metadata_table_body.append(rowHTML);
        }
    }
}

function handleAddStarResult(resultDataString){
    let resultDataJson = JSON.parse(resultDataString);
    let newStarId = resultDataJson["newStarId"];
    alert("Add new star successful,\n new star ID = " + newStarId);
    window.location.replace("_dashboard.html");
}

function handleAddMovieResult(resultDataString){
    let resultDataJson = JSON.parse(resultDataString);
    let movieMessage = resultDataJson["movieMessage"];
    let starMessage = resultDataJson["starMessage"];
    let genreMessage = resultDataJson["genreMessage"];
    alert(movieMessage + "\n" + starMessage + "\n" + genreMessage + "\n");
    window.location.replace("_dashboard.html");
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

let add_new_star_form = $("#add_new_star_form");

function submitAddStarForm(formSubmitEvent) {
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax(
        "api/add-new-star", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: add_new_star_form.serialize(),
            success: (resultDataString) => handleAddStarResult(resultDataString)
        }
    );
}

// Bind the submit action of the form to a handler function
add_new_star_form.submit(submitAddStarForm);



let add_new_movie_form = $("#add_new_movie_form");

function submitAddMovieForm(formSubmitEvent) {
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax(
        "api/add-new-movie", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: add_new_movie_form.serialize(),
            success: (resultDataString) => handleAddMovieResult(resultDataString)
        }
    );
}

// Bind the submit action of the form to a handler function
add_new_movie_form.submit(submitAddMovieForm);

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/dashboard", // Setting request url, which is mapped by StarsServlet in MoviesServlet.java
    success: (resultData) => handleDashboardResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});