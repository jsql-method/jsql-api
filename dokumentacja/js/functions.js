function prep() {   
    /* Dropdowns */
    var dropdown = document.getElementsByClassName("expand");
    var i;

    for (i = 0; i < dropdown.length; i++) {
    dropdown[i].addEventListener("click", function() {
            this.classList.toggle("active");
            var dropdownContent = this.nextElementSibling;
            if (dropdownContent.style.display === "block") {
                dropdownContent.style.display = "none";
            } else {
                dropdownContent.style.display = "block";
            }
        });
    }

    /* Modals */
    var loginModal = document.getElementById('modal_login');
    var loginModal_btn = document.getElementById('modal_save');

    loginModal_btn.onclick = function() {
        //loginModal.style.display = "none";
        $.ajax({
            'url': 'controllers/save_session.php',
            'type': 'POST',
            'data': {
                login: $('#in_login').val(), 
                password: $('#in_password').val()
            },

            success: function(data) {
                //$('#myform').attr('action', data.newAction);
                alert('Data: '+data);
            }
        });
    }

    

    var frontBuild_modal = document.getElementById('modal_buildFront');
    var frontBuild_btn = document.getElementById('modal_buildFront_ok');


    var apiBuild_log = document.getElementById('logApi');


    $("#logApi").change( function() {
        adjust_textarea(this);     
    });

}

window.onload = prep;

function adjust_textarea(h) {
    h.style.height = "20px";
    h.style.height = (h.scrollHeight)+"px";
}

function callApi(run)
{

    if(run)
    {
        $.ajax({
                url: 'controllers/build-api.php',
                type: 'POST',
                dataType: 'json',

                success: function(data) {
                    //var dattt = JSON.stringify(data);
                    //alert(dattt);
                    
                    $('#logApi').val( $('#logApi').val() + "\n" + data.text).show();
                    $('#logApi').change();
                    
                    if(data.code != "100")
                    {
                        callApi(true);
                    }
                }
        });
    }
}

function LogAppendApi(text)
{
    $('#logApi').val( $('#logApi').val() + text + "\n").show();
    $('#logApi').change();
}

function buildApi()
{
    var apiBuild_modal = document.getElementById('modal_buildApi');
    var apiBuild_btn = document.getElementById('modal_buildApi_ok');
    var apiBuild_log = document.getElementById('logApi');

    apiBuild_btn.onclick = function() {
        apiBuild_modal.style.display = "none";  
    }

    var xhttp = new XMLHttpRequest();
    xhttp.onprogress  = function() {
    	LogAppendApi(this.responseText);
    }


    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            //LogAppendApi(this.responseText);
        }
    };

    
    
    xhttp.open("GET", 'http://46.41.138.32:9100/?type=api', true);
    xhttp.send(null);

    apiBuild_modal.style.display = "block";
}


function buildFront()
{
    
}



function logIn()
{
    var loginModal = document.getElementById('modal_login');
    var loginModal_btn = document.getElementById('modal_save');

    loginModal.style.display = "block";
}


    function getStatus() {

        var xmlHttp = new XMLHttpRequest();
        xmlHttp.open("GET", '/status.txt', false);
        xmlHttp.send(null);

        console.log(xmlHttp.responseText);

        var statusElement = document.getElementById('status');

        statusElement.classList.remove('ready');
        statusElement.classList.remove('building');

        if (xmlHttp.responseText.indexOf('OK') > -1) {
            statusElement.classList.add('ready');
            statusElement.innerHTML = 'Ready';
        } else if (xmlHttp.responseText.indexOf('BUILD') > -1) {
            statusElement.classList.add('building');
            statusElement.innerHTML = 'Building';
        }


    }

    function restart(context, type) {

        context.setAttribute('disabled', '');
        var time = 10000;

        if (type == 'front') {
            time = 30000;
        }

        if (type == 'api') {
            time = 120000;
        }

        var context2 = context;
        setTimeout(function () {
            context2.removeAttribute('disabled');
        }, time);

        var xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = function() {
            if (this.readyState == 4 && this.status == 200) {
                document.getElementById("demo").innerHTML =
                this.responseText;
            }
        };

        xhttp.open("GET", 'http://46.41.138.32:9100/?type=' + type, false);
        xhttp.send(null);
    }

