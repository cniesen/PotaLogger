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

import javafx.concurrent.Task;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpSendTask extends Task<Boolean> {
    private final String data;
    private final String host;
    private final int port;

    public UdpSendTask(String dataString, String host, int port) {
        this.data = dataString;
        this.host = host;
        this.port = port;
    }

    @Override
    protected Boolean call() throws IOException {
        DatagramSocket socket = new DatagramSocket();
        byte[] buf = data.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(buf, buf.length, InetAddress.getByName(host), port);
        socket.send(sendPacket);

        DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);
        socket.setSoTimeout(15000); //15 seconds
        socket.receive(receivePacket);

        return true;
    }

}
