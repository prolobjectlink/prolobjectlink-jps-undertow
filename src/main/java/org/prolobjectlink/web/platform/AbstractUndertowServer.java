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

import java.sql.SQLException;
import java.util.List;

import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.servlet.ServletException;

import org.prolobjectlink.db.DatabaseDriver;
import org.prolobjectlink.db.DatabaseDriverFactory;
import org.prolobjectlink.db.jpa.spi.JPAPersistenceUnitInfo;
import org.prolobjectlink.db.util.JavaReflect;
import org.prolobjectlink.logging.LoggerConstants;
import org.prolobjectlink.logging.LoggerUtils;
import org.prolobjectlink.web.application.ControllerGenerator;
import org.prolobjectlink.web.application.ModelGenerator;
import org.prolobjectlink.web.application.ServletUrlMapping;
import org.prolobjectlink.web.application.UndertowControllerGenerator;
import org.prolobjectlink.web.application.UndertowModelGenerator;
import org.prolobjectlink.web.servlet.admin.DatabaseServlet;
import org.prolobjectlink.web.servlet.admin.DocumentsServlet;
import org.prolobjectlink.web.servlet.admin.ManagerServlet;
import org.prolobjectlink.web.servlet.admin.WelcomeServlet;

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
		servletBuilder.setDeploymentName("prolobjectlink-jps-undertow.war");
		servletBuilder.setContextPath("/");

		ServletInfo home = Servlets.servlet(WelcomeServlet.class.getName(), WelcomeServlet.class)
				.addMapping("/welcome");
		servletBuilder.addServlets(home);
		ServletInfo db = Servlets.servlet(DatabaseServlet.class.getName(), DatabaseServlet.class)
				.addMapping("/databases");
		servletBuilder.addServlets(db);
		ServletInfo man = Servlets.servlet(ManagerServlet.class.getName(), ManagerServlet.class).addMapping("/manager");
		servletBuilder.addServlets(man);
		ServletInfo doc = Servlets.servlet(DocumentsServlet.class.getName(), DocumentsServlet.class).addMapping("/doc");
		servletBuilder.addServlets(doc);

		// applications models
		try {
			ModelGenerator modelGenerator = new UndertowModelGenerator();
			List<PersistenceUnitInfo> units = modelGenerator.getPersistenceUnits();
			for (PersistenceUnitInfo unit : units) {
				DatabaseDriver databaseDriver = DatabaseDriverFactory.createDriver(unit);
				if (!databaseDriver.getDatabasePing()) {
					databaseDriver.createDatabase();
					JPAPersistenceUnitInfo jpaUnit = (JPAPersistenceUnitInfo) unit;
					String name = jpaUnit.getPersistenceProviderClassName();
					Class<?> cls = JavaReflect.classForName(name);
					Object object = JavaReflect.newInstance(cls);
					PersistenceProvider provider = (PersistenceProvider) object;
					provider.generateSchema(unit, unit.getProperties());
				}
			}
		} catch (SQLException e) {
			// do nothing
		}

		// applications controllers
		ControllerGenerator controllerGenerator = new UndertowControllerGenerator();
		List<ServletUrlMapping> mappings = controllerGenerator.getMappings();
		for (ServletUrlMapping servletUrlMapping : mappings) {
			ServletInfo info = Servlets.servlet(servletUrlMapping.getServlet().getClass().getName(),
					servletUrlMapping.getServlet().getClass()).addMapping(servletUrlMapping.getMappingUrl());
			servletBuilder.addServlets(info);
		}

		ServletContainer container = Servlets.defaultContainer();
		DeploymentManager manager = container.addDeployment(servletBuilder);
		manager.deploy();

		PathHandler path = null;
		try {
			path = Handlers.path(Handlers.redirect("/")).addPrefixPath("/", manager.start());
		} catch (ServletException e) {
			LoggerUtils.error(getClass(), LoggerConstants.SERVLET_ERROR, e);
		}
		server = Undertow.builder().addHttpListener(serverPort, "0.0.0.0").setHandler(path).build();
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
