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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BuildInfo {
    public static String getVersion() {
        InputStream in = BuildInfo.class.getResourceAsStream("/application.properties");
        Properties properties = new Properties();
        if (in != null) {
            try {
                properties.load(in);
                return properties.getProperty("application.version");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
       return "unknown";
    }
}