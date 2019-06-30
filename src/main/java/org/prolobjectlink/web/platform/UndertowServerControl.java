/*-
 * #%L
 * prolobjectlink-jps-undertow
 * %%
 * Copyright (C) 2012 - 2019 Prolobjectlink Project
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.prolobjectlink.web.platform;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.prolobjectlink.db.DatabaseServer;
import org.prolobjectlink.db.platform.linux.LinuxDatabaseServer;
import org.prolobjectlink.db.platform.macosx.MacosxDatabaseServer;
import org.prolobjectlink.db.platform.win32.Win32DatabaseServer;
import org.prolobjectlink.web.platform.linux.undertow.LinuxUndertowWebServer;
import org.prolobjectlink.web.platform.macosx.undertow.MacosxUndertowWebServer;
import org.prolobjectlink.web.platform.win32.undertow.Win32UndertowWebServer;

public class UndertowServerControl extends AbstractWebControl implements WebServerControl {

	public UndertowServerControl(WebServer webServer, DatabaseServer databaseServer) {
		super(webServer, databaseServer);
	}

	public static void main(String[] args) {

		int port = 8080;

		UndertowWebServer server = null;
		DatabaseServer database = null;

		if (WebPlatformUtil.runOnWindows()) {
			database = new Win32DatabaseServer();
			server = new Win32UndertowWebServer(port);
		} else if (WebPlatformUtil.runOnOsX()) {
			database = new MacosxDatabaseServer();
			server = new MacosxUndertowWebServer(port);
		} else if (WebPlatformUtil.runOnLinux()) {
			database = new LinuxDatabaseServer();
			server = new LinuxUndertowWebServer(port);
		} else {
			Logger.getLogger(UndertowServerControl.class.getName()).log(Level.SEVERE, null, "Not supported platform");
			System.exit(1);
		}

		new UndertowServerControl(server, database).run(args);
	}

}
