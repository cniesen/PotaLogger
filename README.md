Claus' POTA Logger
==================

Work in progress!  Not usable yet.

Logging POTA activation is a pain, especially with Log4OM.
This project allows easy data entry from paper logs, creation of ADIF files that are sent to POTA, 
and UDP upload of the ADIF data into Log4OM.

UDP packets are currently always sent to "localhost:8899".

Development
-----------

Run: `gradlew run`

Distribute: `gradlew shadowJar`