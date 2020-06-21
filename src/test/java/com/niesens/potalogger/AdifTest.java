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

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class AdifTest {

    @Test
    void toAdifRecord() {
        String expected = "<QSO_DATE:8>20200214 <TIME_ON:6>200517 <CALL:4>KI0A <RST_RCVD:3>599 <RST_SENT:3>345 <EOR>\n";

        Qso qso = new Qso(1, LocalDate.parse("2020-02-14"), LocalTime.parse("20:05:17"), "KI0A","345","599", "");

        assertEquals(expected, new Adif().addQso(qso).getAdif().toString());
    }
}