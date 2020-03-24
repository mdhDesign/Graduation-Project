$(function($){
  var location = window.location.href;
  console.log(location);
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

    if (active != null && callerPhone != null && deviceImei != null && incidentTime != null && latitude != null && longitude != null && region != null) {
      notificationBody.append(toHtml(snapshot.key, active, incidentType, latitude, longitude, deviceImei, incidentTime, region));
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



    if (active != null && callerPhone != null && deviceImei != null && incidentTime != null && latitude != null && longitude != null && region != null) {
      var key = '.'+snapshot.key;
      $(key).html(cellInnertoHtml(snapshot.key, active, incidentType, latitude, longitude, deviceImei, region))
    }
  });

  notificationBody.on('click', '#notification', function() {
    addMarker({lat: parseFloat($(this).data('latitude')), lng: parseFloat($(this).data('longitude'))}, parseFloat($(this).data('type')));
    $(this).addClass('active').siblings().removeClass('active');
  });

  notificationBody.on('click', '#checkbox', function() {
    var notification = $(this).closest('#notification');
    var id = notification.data('id');
    notification.addClass('op-changing');
    incidents.child(id).update({active:1})
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

  function toHtml(incidentId, active, incidentType, latitude, longitude, deviceImei, incidentTime, region) {
    var notification = '<div class="notification '+ incidentId +'" data-id="'+ incidentId +'" data-type="'+ incidentType +'" data-latitude="'+ latitude +'" data-longitude="'+ longitude +'" id="notification"><div class="notification-cell"><img src="'+ getImage(incidentType) +'"/></div><div class="notification-cell"><h3>'+ getIncidentTypeString(incidentType) +'</3></div><div class="notification-cell"><h3>'+ latitude +'</3></div><div class="notification-cell"><h3>'+ longitude +'</3></div><div class="notification-cell"><h3>'+ region +'</3></div><div class="notification-cell"><h3>'+ milliToString(incidentTime) +'</3></div><div class="notification-cell"><h3>0771 99 99 99</3></div><div class="notification-cell"><h3>'+ deviceImei+'</3></div><div class="notification-cell"><h3 class="circle">'+ getProgressText(active) +'</3></div><div class="notification-cell"><span class="delete" id="delete"></span></div></div>'
    return notification;
  }

  function cellInnertoHtml(incidentId, active, incidentType, latitude, longitude, deviceImei, incidentTime, region) {
    var notification = '<div class="notification-cell"><img src="'+ getImage(incidentType) +'"/></div><div class="notification-cell"><h3>'+ getIncidentTypeString(incidentType) +'</3></div><div class="notification-cell"><h3>'+ latitude +'</3></div><div class="notification-cell"><h3>'+ longitude +'</3></div><div class="notification-cell"><h3>'+ region +'</3></div><div class="notification-cell"><h3>'+ milliToString(incidentTime) +'</3></div><div class="notification-cell"><h3>0771 99 99 99</3></div><div class="notification-cell"><h3>'+ deviceImei+'</3></div><div class="notification-cell"><h3 class="circle">'+ getProgressText(active) +'</3></div><div class="notification-cell"><span class="delete" id="delete"></span></div>'
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

  function addMarker(position, incidentType) {
    // console.log(jQuery.getJSON('https://maps.googleapis.com/maps/api/geocode/json?latlng=40.714224,-73.961452&sensor=false&key=AIzaSyDZ-BxAEsDGoVpELrC4hA4-vvy94z3umuk'));

    if (marker == null)
      marker = new google.maps.Marker({
        position: position,
        map: map
      });
    marker.setPosition(position);
    marker.setIcon(markerIcon(incidentType));
    map.setZoom(14);
    map.panTo(marker.position);
  }

  function getProgressText(active) {
    switch (active) {
      case 0:
        return 'Done'
        break;
      case 1:
        return 'Working on'
        break;
      case 2:
        return 'In Progress'
        break;
    }
  }

  function markerIcon(incidentType) {
    switch (incidentType) {
      case 0:
        return './assets/images/marker_fire.png'
        break;
      case 1:
        return './assets/images/marker_electricity.png'
        break;
      case 2:
        return './assets/images/marker_patient.png'
        break;
      case 3:
        return './assets/images/marker_accident.png'
        break;
    }
  }

  function milliToString(milliSeconds) {
    var d = new Date(milliSeconds);
    return d.getFullYear() + '/' + (d.getMonth() + 1) + '/' + d.getDate();
  }

  function regionFromLocation(location) {

    var geocoder = new google.maps.Geocoder();

      geocoder.geocode({
          'latLng': location
      }, function (results, status) {

          if (status == google.maps.GeocoderStatus.OK) {

              console.log(results[0].address_components[1].long_name);
          }
      });
  }
}(jQuery));
