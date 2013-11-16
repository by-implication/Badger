// query lat/long using this:
// http://maps.googleapis.com/maps/api/geocode/json?address=6C+2nd+Avenue,+Quezon+City,+Metro+Manila,+Philippines&sensor=true
// (address of teh best company evar, btw
// further reading: https://developers.google.com/maps/documentation/geocoding/
// you may also want to use Viewport or Region Biasing to filter results


// initialize the map.
var map = L.map('map').setView([14.612209, 121.0527097], 17);

// add an OpenStreetMap tile layer
L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png', {
    attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
}).addTo(map);

// add a marker in the given location, attach some popup content to it and open the popup
L.marker([14.612209, 121.0527097]).addTo(map)
    .bindPopup('omg wow such map<br> html <strong><em>works</em></strong>!<p><em>here is best company evar</em></p>')
    .openPopup();