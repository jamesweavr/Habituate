# Habituate

This program was written with the intent of providing notice if the temperature of the weather tomorrow would be greater or less than a 
set change in temperature.

Using Open Weather's API I was able to retrieve the current weather and the high temperature for the following day. The JSON response is 
parsed to retrieve the specific temperature values and compare them.

Recently an attempt was made to store the high temperature for tomorrow in a local file and simply read this file the next day. This would 
allow the program to retrieve the high temperature for the day, as opposed to the current temperature when the program is run. This 
presents a problem, however. The predicted temperature for the following day may not be the actual high temperature for the day. The error 
would be minuscule, but possibly noticeable.
