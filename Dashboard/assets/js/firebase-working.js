$(function($){
  var hash = window.location.hash;
  if (hash != "#recent" || hash != "#working" || hash != "#incidents") {
    window.location.href = "#recent"
  }
  // Initialize Firebase
  var config = {
    apiKey: "",
    authDomain: "",
    databaseURL: "",
    projectId: "",
    storageBucket: "",
    messagingSenderId: ""
  };
  firebase.initializeApp(config);

  var notificationBody = $('#notification-body');
  var marker;

  var incidents = firebase.database().ref('incidents');
  incidents.on('child_added', function(snapshot) {
    var active =  snapshot.child('active').val();
    var incidentType =  snapshot.child('incidentType').val();
    var callerPhone =  snapshot.child('callerPhone').val();
    var deviceImei =  snapshot.child('deviceImei').val();
    var incidentTime =  snapshot.child('incidentTime').val();
    var latitude =  snapshot.child('latitude').val();
    var longitude =  snapshot.child('longitude').val();
    var region =  snapshot.child('region').val();

    if (active != null && callerPhone != null && deviceImei != null && incidentTime != null && latitude != null && longitude != null && region != null && active == 1) {
      notificationBody.append(toHtml(snapshot.key, incidentType, latitude, longitude, deviceImei));
    }
  });

  incidents.on('child_changed', function(snapshot) {
    var active =  snapshot.child('active').val();
    var incidentType =  snapshot.child('incidentType').val();
    var callerPhone =  snapshot.child('callerPhone').val();
    var deviceImei =  snapshot.child('deviceImei').val();
    var incidentTime =  snapshot.child('incidentTime').val();
    var latitude =  snapshot.child('latitude').val();
    var longitude =  snapshot.child('longitude').val();
    var region =  snapshot.child('region').val();

    if (active != null && callerPhone != null && deviceImei != null && incidentTime != null && latitude != null && longitude != null && region != null && active == 1) {
      notificationBody.append(toHtml(snapshot.key, incidentType, latitude, longitude, deviceImei));
    }
  });

  notificationBody.on('click', '#notification', function() {
    addMarker({lat: parseFloat($(this).data('latitude')), lng: parseFloat($(this).data('longitude'))});
    $(this).addClass('active').siblings().removeClass('active');
  });

  notificationBody.on('click', '#checkbox', function() {
    var notification = $(this).closest('#notification');
    var id = notification.data('id');
    notification.addClass('op-changing');
    incidents.child(id).update({active:0})
    setTimeout(function() {
      notification.remove();
    }, 1000);
  });

  $('#notification-body').on('click', '#delete', function() {
    var notification = $(this).closest('#notification');
    var id = notification.data('id');
    notification.addClass('op-changing');
    incidents.child(id).update({active:4})
    setTimeout(function() {
      notification.remove();
    }, 1000);
  });

  function toHtml(incidentId, incidentType, latitude, longitude, deviceImei) {
    var notification = '<div class="notification" data-id="'+ incidentId +'" data-latitude="'+ latitude +'" data-longitude="'+ longitude +'" id="notification"><div class="notification-cell"><img src="'+ getImage(incidentType) +'"/></div><div class="notification-cell"><h3>'+ getIncidentTypeString(incidentType) +'</3></div><div class="notification-cell"><h3>'+ latitude +'</3></div><div class="notification-cell"><h3>'+ longitude +'</3></div><div class="notification-cell"><h3>Djelfa</3></div><div class="notification-cell"><h3>02/03/2018</3></div><div class="notification-cell"><h3>0771 99 99 99</3></div><div class="notification-cell"><h3>'+ deviceImei+'</3></div><div class="notification-cell"><h3 class="circle">In progress</3></div><div class="notification-cell"><label class="switch"><input type="checkbox" id="checkbox"><span class="slider round"></span></label></div><div class="notification-cell"><span class="delete" id="delete"></span></div></div>'
    return notification;
  }

  function getImage(incidentType) {
    switch (incidentType) {
      case 0:
        return 'assets/images/fire.png'
        break;
      case 1:
        return 'assets/images/electricity.png'
        break;
      case 2:
        return 'assets/images/patient.png'
        break;
      case 3:
        return 'assets/images/accident.png'
        break;
    }
  }

  function getIncidentTypeString(incidentType) {
    switch (incidentType) {
      case 0:
        return 'Fire'
        break;
      case 1:
        return 'Electricity'
        break;
      case 2:
        return 'Patient'
        break;
      case 3:
        return 'Accident'
        break;
    }
  }

  function addMarker(position) {
    if (marker == null)
      marker = new google.maps.Marker({
        position: position,
        map: map
      });
    marker.setPosition(position);
    map.setZoom(14);
    map.panTo(marker.position);
  }

}(jQuery));
