/*
 * TopStack (c) Copyright 2012-2013 Transcend Computing, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the License);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an AS IS BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.msi.tough.internal.openstack;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import com.msi.tough.core.Appctx;
import com.msi.tough.query.Action;

/**
 * Servlet implementation class RDSQueryServlet
 */
public class OpenstackServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Appctx.getLogger(OpenstackServlet.class
			.getName());

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public OpenstackServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {
		try {
			final String clazz = "com.msi.tough.internal.openstack.actions."
					+ request.getParameter("Action");
			final Class<?> c = Class.forName(clazz);
			final Action a = (Action) c.newInstance();
			logger.debug("calling action " + a);
			a.process(request, response);
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			Appctx.removeThreadMap();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {
		doGet(request, response);
	}
}
