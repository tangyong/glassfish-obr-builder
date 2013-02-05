/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package org.glassfish.obrbuilder;

import java.net.URI;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.logging.Level;

import javax.servlet.ServletException;

import org.glassfish.jersey.servlet.ServletContainer;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTracker;

import static org.glassfish.obrbuilder.Logger.logger;

/**
 * @author TangYong(tangyong@cn.fujitsu.com)
 */
public class ObrBuilderActivator implements BundleActivator {

	private BundleContext bctx;
	ServiceRegistration registration = null;
	private ServiceTracker tracker = null;
	private HttpService httpService = null;

	public void start(BundleContext context) throws Exception {
		this.bctx = context;

		String gfRepositoryUris = context
				.getProperty(Constants.GF_MODULE_REPOSITORIES);

		createGFObrRepository(gfRepositoryUris);

		//Register ObrHandlerServiceFactory into OSGi Registry
		registration = context.registerService(
				ObrHandlerService.class.getName(),
				new ObrHandlerServiceFactory(), null);

		//Register Rest Servlets
		registerRestServlets();
	}

	private void registerRestServlets() {
		this.tracker = new ServiceTracker(this.bctx,
				HttpService.class.getName(), null) {
			@Override
			public Object addingService(ServiceReference serviceRef) {
				httpService = (HttpService) super.addingService(serviceRef);
				registerServlets();
				return httpService;
			}

			@Override
			public void removedService(ServiceReference ref, Object service) {
				if (httpService == service) {
					unregisterServlets();
					httpService = null;
				}
				super.removedService(ref, service);
			}
		};
		
        this.tracker.open();

        logger.info("HTTP SERVICE BUNDLE STARTED");
	}

	private void unregisterServlets() {
		if (this.httpService != null) {
			logger.info("JERSEY BUNDLE: UNREGISTERING SERVLETS");
			httpService.unregister("/jersey-http-service");
			logger.info("JERSEY BUNDLE: SERVLETS UNREGISTERED");
		}
	}

	private void registerServlets() {
		try {
			rawRegisterServlets();
		} catch (InterruptedException ie) {
			throw new RuntimeException(ie);
		} catch (ServletException se) {
			throw new RuntimeException(se);
		} catch (NamespaceException se) {
			throw new RuntimeException(se);
		}
	}

	private void rawRegisterServlets() throws ServletException,
			NamespaceException, InterruptedException {
		logger.info("JERSEY BUNDLE: REGISTERING SERVLETS");
		logger.info("JERSEY BUNDLE: HTTP SERVICE = " + httpService.toString());

		//Please seeing https://github.com/tangyong/glassfish-obr-builder/issues/31
		//TangYong/2013.2.5
		ClassLoader oldTCC = Thread.currentThread().getContextClassLoader();   					
		try{
			ClassLoader newTCC = new TCCLClassLoader(oldTCC, this.getClass().getClassLoader());
			Thread.currentThread().setContextClassLoader(newTCC);
			
			httpService.registerServlet("/jersey-http-service",
					new ServletContainer(), getJerseyServletParams(), null);
		}finally{
			Thread.currentThread().setContextClassLoader(oldTCC);
		}		

		logger.info("JERSEY BUNDLE: SERVLETS REGISTERED");
	}
	
	 private static class TCCLClassLoader extends ClassLoader {
	        private final ClassLoader oldCLS;

	        private final ClassLoader newCLS;

	        public TCCLClassLoader(ClassLoader oldCLS, ClassLoader newCLS) {
	            this.oldCLS = oldCLS;
	            this.newCLS = newCLS;
	        }

	        @Override
	        public Class<?> loadClass(String name) throws ClassNotFoundException {
	            Class<?> loadedClass = null;
	            try {
	                loadedClass = oldCLS.loadClass(name);
	            } catch (ClassNotFoundException cnfe) {
	                loadedClass = newCLS.loadClass(name);
	            }
	            return loadedClass;
	        }
	    }

	private Dictionary<String, String> getJerseyServletParams() {
		Dictionary<String, String> jerseyServletParams = new Hashtable<String, String>();
		jerseyServletParams.put("javax.ws.rs.Application",
				JerseyApplication.class.getName());
		return jerseyServletParams;
	}

	public void stop(BundleContext context) throws Exception {
		if (registration != null) {
			registration.unregister();
		}

		this.tracker.close();
	}

	private void createGFObrRepository(String repositoryUris) {
		if (repositoryUris != null) {
			for (String s : repositoryUris.split("\\s")) {
				URI repoURI = URI.create(s);
				ObrHandlerService obrHandler = new ObrHandlerServiceImpl(bctx);
				try {
					obrHandler.addRepository(repoURI);
				} catch (Exception e) {
					e.printStackTrace();
					logger.logp(
							Level.SEVERE,
							"ObrBuilderActivator",
							"createGFObrRepository",
							"Creating Glassfish OBR Repository failed, RepoURI: {0}",
							new Object[] { repoURI });
				}
			}
		}
	}
}
