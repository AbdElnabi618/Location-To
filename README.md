# Location To </br>
# what ‘s app doing ?
The app have two points work on it,</br>
    • The first we have a user ‘A’  this user want to going from his location to another location, he can click on the location witch he target then click on the get direction button,
The app will draw a line from user’s location to the target location “goal point” and will calculate the distance between the two points and shows in ‘Kilo Mater(km)’ and ‘Mater(m)’ unit.
   </br> •  Second there is a user ‘B’, user ‘B’ can track user ‘A’ by get the current location of him and every-time the location changed it’s appear to user ‘B’ on the his map.</br>
``` The application had created with java programming language, we had used 3 activities in our app .```</br>
# application activities, and  What's it content ? </br></br>
   # i. Home activity :</br>
 this is activity has two button “Open Map” and “Tracking” the first witch used by user ‘A’ and the other used by user ‘B’.</br>
   # ii. Map activity :</br>
this activity witch draw the user’s way and send the user information to database.</br>
#  iii. Tracking activity:</br>
this is the activity witch the user ‘B’ used to tracking user ‘A’.</br></br>
# Activities content :</br></br>
# A. Map activity :</br>
* onCreate function witch initialization the variables, check for permission and 	called onMapReady  function.</br>
*  onMapReady  function witch enable the allow function to get user location 	and handled on map click event.</br>
* GPSIsEnable function witch check if the gps is enabled or not and ask to 	enabled it if not.</br>
* CreateGoogleApiClient  function witch create “google api client” if not 	created.</br>
* GetDirection function witch draw the direction between point ‘a’ to point ‘b’ 	with “google api direction“, and takes a three parameters start point, end point and  string server key.</br>
* DrawDirection : draw a line between to point the difference between this 	function and the above is the above draw many lines on the streets 	the user will go from but the another draw the line direct from point 	‘a’ to point ‘b’.</br>
* onConnected : start to get the user location when the google api client is 	connect to the server.</br>
* SendCurrentLocat : this is the function witch send the user location to 	firebase database.</br>
# B. Tracking activity :</br>
* onCreate function witch initialization the variables and called onMapReady function. </br>
* ZoomAndMarkLocation function witch set a mark in the position witch get 	from firebase and zoom to this location.</br>
* onMapReady witch  initialization the mark witch used to pointing to user	loaction and called ZoomAndMarkLocation.</br>
* onDataChange used to add any change happen to database to map by updating the current point and called ZoomAndMarkLocation.</br>
