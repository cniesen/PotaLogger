/*
	Claus' POTA Logger
	Copyright (C) 2020  Claus Niesen
	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
	You should have received a copy of the GNU General Public License
	along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.niesens.potalogger;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class Qso {
    private LocalDate date;
    private LocalTime time;
    private String callsign;
    private String rstSent;
    private String rstReceived;
    private String parkToPark;
    private String activatedPark;
    private String frequency;
    private String mode;
    private String myCallsign;
    private String myCountry;
    private String myState;
    private String myCounty;
    private String myGrid;
    private String myItu;
    private String myCq;
    private String myIaruRegion;

    public Qso(LocalDate date, LocalTime time, String callsign, String rstSent, String rstReceived, String parkToPark, String activatedPark, String frequency, String mode, String myCallsign, String myCountry, String myState, String myCounty, String myGrid, String myItu, String myCq, String myIaruRegion) {
        this.date = date;
        this.time = time;
        this.callsign = callsign;
        this.rstSent = rstSent;
        this.rstReceived = rstReceived;
        this.parkToPark = parkToPark;
        this.activatedPark = activatedPark;
        this.frequency = frequency;
        this.mode = mode;
        this.myCallsign = myCallsign;
        this.myCountry = myCountry;
        this.myState = myState;
        this.myCounty = myCounty;
        this.myGrid = myGrid;
        this.myItu = myItu;
        this.myCq = myCq;
        this.myIaruRegion = myIaruRegion;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getCallsign() {
        return callsign;
    }

    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }

    public String getRstSent() {
        return rstSent;
    }

    public void setRstSent(String rstSent) {
        this.rstSent = rstSent;
    }

    public String getRstReceived() {
        return rstReceived;
    }

    public void setRstReceived(String rstReceived) {
        this.rstReceived = rstReceived;
    }

    public String getParkToPark() {
        return parkToPark;
    }

    public void setParkToPark(String parkToPark) {
        this.parkToPark = parkToPark;
    }

    public String getActivatedPark() {
        return activatedPark;
    }

    public void setActivatedPark(String activatedPark) {
        this.activatedPark = activatedPark;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getMyCallsign() {
        return myCallsign;
    }

    public void setMyCallsign(String myCallsign) {
        this.myCallsign = myCallsign;
    }

    public String getMyCountry() {
        return myCountry;
    }

    public void setMyCountry(String myCountry) {
        this.myCountry = myCountry;
    }

    public String getMyState() {
        return myState;
    }

    public void setMyState(String myState) {
        this.myState = myState;
    }

    public String getMyCounty() {
        return myCounty;
    }

    public void setMyCounty(String myCounty) {
        this.myCounty = myCounty;
    }

    public String getMyGrid() {
        return myGrid;
    }

    public void setMyGrid(String myGrid) {
        this.myGrid = myGrid;
    }

    public String getMyItu() {
        return myItu;
    }

    public void setMyItu(String myItu) {
        this.myItu = myItu;
    }

    public String getMyCq() {
        return myCq;
    }

    public void setMyCq(String myCq) {
        this.myCq = myCq;
    }

    public String getMyIaruRegion() {
        return myIaruRegion;
    }

    public void setMyIaruRegion(String myIaruRegion) {
        this.myIaruRegion = myIaruRegion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Qso qso = (Qso) o;
        return date.equals(qso.date) &&
                time.equals(qso.time) &&
                callsign.equals(qso.callsign) &&
                frequency.equals(qso.frequency) &&
                mode.equals(qso.mode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, time, callsign, frequency, mode);
    }
}
