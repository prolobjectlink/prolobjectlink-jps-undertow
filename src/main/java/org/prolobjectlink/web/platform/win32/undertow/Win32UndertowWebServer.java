/*-
 * #%L
 * prolobjectlink-jps-undertow
 * %%
 * Copyright (C) 2019 Prolobjectlink Project
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
package org.prolobjectlink.web.platform.win32.undertow;

import org.prolobjectlink.web.platform.AbstractUndertowServer;
import org.prolobjectlink.web.platform.UndertowWebServer;

public class Win32UndertowWebServer extends AbstractUndertowServer implements UndertowWebServer {

	public Win32UndertowWebServer(int serverPort) {
		super(serverPort);
	}

}
