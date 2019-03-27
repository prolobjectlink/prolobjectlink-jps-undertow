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
package org.prolobjectlink.web.platform;

import javax.servlet.ServletException;

import org.prolobjectlink.web.servlet.HomeServlet;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.Version;
import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletContainer;
import io.undertow.servlet.api.ServletInfo;

/**
 * 
 * @author Jose Zalacain
 * @since 1.0
 */
public abstract class AbstractUndertowServer extends AbstractWebServer implements UndertowWebServer {

	private final Undertow server;

	public AbstractUndertowServer(int serverPort) {
		super(serverPort);
		DeploymentInfo servletBuilder = Servlets.deployment();
		servletBuilder.setClassLoader(getClass().getClassLoader());
		servletBuilder.setDeploymentName("test.war");
		servletBuilder.setContextPath("/");

		ServletInfo all = Servlets.servlet(HomeServlet.class.getName(), HomeServlet.class).addMapping("/*");
		ServletInfo home = Servlets.servlet(HomeServlet.class.getName(), HomeServlet.class).addMapping("/");
		servletBuilder.addServlets(all, home);

		ServletContainer container = Servlets.defaultContainer();
		DeploymentManager manager = container.addDeployment(servletBuilder);
		manager.deploy();

		PathHandler path = null;
		try {
			path = Handlers.path(Handlers.redirect("/")).addPrefixPath("/", manager.start());
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		server = Undertow.builder().addHttpListener(serverPort, "localhost").setHandler(path).build();
		server.start();
	}

	public final String getVersion() {
		String info = Version.getFullVersionString();
		return info.substring(info.indexOf('-'));
	}

	public final String getName() {
		String info = Version.getFullVersionString();
		return info.substring(0, info.indexOf('-') + 2);
	}

	public final void start() {
//		server.start();
	}

	public final void stop() {
		server.stop();
	}

}
