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

import org.springframework.integration.ip.udp.UnicastSendingMessageHandler;
import org.springframework.messaging.support.MessageBuilder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;

public class Adif {
    private StringBuilder adif = new StringBuilder();

    private DateTimeFormatter dateFormatter = new DateTimeFormatterBuilder().appendPattern("yyyyMMdd").toFormatter();
    private DateTimeFormatter timeFormatter = new DateTimeFormatterBuilder().appendPattern("kkmmss").toFormatter();

    public Adif() {
        appendAdifField("ADIF_VERS", "3.1.0");
        appendAdifField("PROGRAMID", "Claus' POTA Logger");
        appendAdifField("PROGRAMVERSION", BuildInfo.getVersion());
        appendAdifEor();


    }

    public Adif addQso(Qso qso) {
        appendAdifField("QSO_DATE", qso.getDate());
        appendAdifField("TIME_ON", qso.getTime());
        appendAdifField("CALL", qso.getCallsign());
        appendAdifField("RST_RCVD", qso.getRstReceived());
        appendAdifField("RST_SENT", qso.getRstSent());
//        appendAdifField("BAND", qso.getBand());
        appendAdifField("FREQ", qso.getFrequency());
        appendAdifField("MODE", qso.getMode());
        if (!qso.getParkToPark().isBlank()) {
            appendAdifField("SIG_INFO", qso.getParkToPark());
        }
        appendAdifField("MY_SIG_INFO", qso.getActivatedPark());
        appendAdifField("STATION_CALLSIGN", qso.getMyCallsign());
        appendAdifField("MY_COUNTRY", qso.getMyCountry());
        appendAdifField("MY_STATE", qso.getMyState());
        appendAdifField("MY_CNTY", qso.getMyCounty());
        appendAdifField("MY_GRIDSQUARE", qso.getMyGrid());
//        appendAdifField("", qso.getMyIaruRegion());
        appendAdifField("MY_ITU_ZONE", qso.getMyItu());
        appendAdifField("MY_CQ_ZONE", qso.getMyCq());
        appendAdifField("APP_L4ONG_MY_AWARD_REFERENCES", "[{\"AC\":\"POTA\",\"R\":\"" + qso.getActivatedPark() + "\",\"SUB\":[],\"GRA\":[]}]");
        if (!qso.getParkToPark().isBlank()) {
            appendAdifField("APP_L4ONG_QSO_AWARD_REFERENCES", "[{\"AC\":\"POTA\",\"R\":\"" + qso.getParkToPark() + "\",\"SUB\":[],\"GRA\":[]}]");
        }
        appendAdifEor();
        return this;
    }

    public Adif addAllQsos(List<Qso> qsos) {
        qsos.forEach(this::addQso);
        return this;
    }

    private void appendAdifField(String fieldName, String fieldValue) {
        adif.append("<");
        adif.append(fieldName);
        adif.append(":");
        adif.append(fieldValue.trim().length());
        adif.append(">");
        adif.append(fieldValue);
        adif.append(" ");
    }

    private void appendAdifField(String fieldName, LocalDate fieldValue) {
        appendAdifField(fieldName, fieldValue.format(dateFormatter));
    }

    private void appendAdifField(String fieldName, LocalTime fieldValue) {
        appendAdifField(fieldName, fieldValue.format(timeFormatter));
    }

    private void appendAdifEor() {
        adif.append("<EOR>\n");
    }

    public StringBuilder getAdif() {
        return adif;
    }

    public String toString() {
        return adif.toString();
    }

    public void sendAdifViaUdp(String host, int port) {
        UnicastSendingMessageHandler handler = new UnicastSendingMessageHandler(host, port);
        handler.handleMessage(MessageBuilder.withPayload(adif.toString()).build());
    }
}
