POTA ADIF Log File Format Notes
===============================
From Zero District POTA Log Manager.
* Log Files must be in ADIF Format.
* One unique park activation per log file.  Log file may contain QSOs for multiple days of activation of that park.
* Mandatory fields are
  * QSO_DATE 
  * TIME_ON
  * BAND
  * MODE
  * CALL
* Optional fields
  * STATION_CALLSIGN 
  * OPERATOR
  * MY_SIG_INFO - The activated park reference number
  * SIG_INFO - For park-to-park contact, the other's park reference number
  
* Log files should be named "callsign@X-xxxx YYYYMMDD.adi" where callsign is the stations callsign, X-xxxx the reference number of the activated park and YYYYMMDD the UTC date of the activation.
* Log corrections are done by resubmitting the complete log file.