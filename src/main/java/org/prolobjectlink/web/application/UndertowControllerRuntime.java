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
package org.prolobjectlink.web.application;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UndertowControllerRuntime {

	public static void run(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		String protocol = req.getScheme();
		String contextHost = req.getHeader("host");
		Integer contextPort = req.getLocalPort();
		String servletPath = req.getServletPath();
		servletPath = servletPath.substring(1);
		String[] components = servletPath.split("/");
		String application = components[0];
		String procedure = components[1];

		Object[] arguments = new Object[0];
		if (req.getMethod().equalsIgnoreCase("GET")) {
			String pathInfo = req.getPathInfo();
			if (pathInfo != null && !pathInfo.isEmpty()) {
				pathInfo = pathInfo.substring(1);
				arguments = pathInfo.split("/");
			}
		} else if (req.getMethod().equalsIgnoreCase("POST")) {
			Map<String, String[]> param = req.getParameterMap();
			int i = 0;
			arguments = new Object[param.size()];
			for (String[] array : param.values()) {
				arguments[i++] = array[0];
			}
		}

		ServletOutputStream out = resp.getOutputStream();
		ControllerRuntime.run(protocol, contextHost, contextPort, application, procedure, arguments, out);
		resp.setStatus(HttpServletResponse.SC_OK);

	}

	private UndertowControllerRuntime() {
	}

}
