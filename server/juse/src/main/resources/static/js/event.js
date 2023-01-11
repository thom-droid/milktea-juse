let sse = '';
const uuid = getCookie("uuid");

function connect () {
    const url = 'http://localhost:8080/notification-test/event-stream/' + uuid;
    try {

        const request = new Request(url);
        sse = new EventSource(url);

    } catch (error) {
        console.log(error);
    }
}

function getCookie(cookieName) {
  let cookie = {};
  document.cookie.split(';').forEach(function(el) {
    let [key,value] = el.split('=');
    cookie[key.trim()] = value;
  })
  console.log(cookie[cookieName]);
  return cookie[cookieName];
}


sse.onmessage = function (evt) {
    console.log(sse.readyState);
    var el = document.getElementById('notification');
    el.appendChild(document.createTextNode(evt.data));
    el.appendChild(document.createElement('br'));
};

sse.onerror = function (){
    console.log("timed out. Reestablishing the connection...");

    fetch(request).then((response) => {

        const status = response.status;
        console.log(`connection reestablished. ${status}`);

    }).catch((error) =>{
        console.log(`there is no server running. ${error}`);
        sse.close();
    });


}

