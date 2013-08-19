package com.msi.tough.internal.autoscale;

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
public class AutoScaleInternalServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Appctx
			.getLogger(AutoScaleInternalServlet.class.getName());

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AutoScaleInternalServlet() {
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
			final String clazz = "com.msi.tough.internal.autoscale.actions."
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
