/*
    Simple AJAX library.
 */
function ajax(handler, method, url) {
    let httpRequest = new XMLHttpRequest();
    httpRequest.onreadystatechange = function () {
        // processing the response
        if (httpRequest.readyState === XMLHttpRequest.DONE) {
            // everything is good, the response was received.
            if (httpRequest.status === 200) {
                handler(JSON.parse(httpRequest.responseText));
            } else {
                alert("Chyba při komunikaci se serverem: " + httpRequest.status);
            }
        } else {
            // the response is not ready yet.
        }
    }

    httpRequest.open(method, url, true);
    httpRequest.send();
}
