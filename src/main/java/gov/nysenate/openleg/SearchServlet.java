package gov.nysenate.openleg;

import gov.nysenate.openleg.search.*;
import gov.nysenate.openleg.util.BillCleaner;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.lucene.queryParser.ParseException;

public class SearchServlet extends HttpServlet implements OpenLegConstants
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(SearchServlet.class);

	/**
	 * Constructor of the object.
	 */
	public SearchServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy();
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doPost(request, response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String term = request.getParameter("term");
		String type = request.getParameter("type");
		
		String full = request.getParameter("full");
		String memo = request.getParameter("memo");
		String status = request.getParameter("status");
		String sponsor = request.getParameter("sponsor");
		String cosponsors = request.getParameter("cosponsors");
		String sameas = request.getParameter("sameas");
		String committee = request.getParameter("committee");
		String location = request.getParameter("location");

		String sortField = request.getParameter("sort");
		boolean sortOrder = false;
		if (request.getParameter("sortOrder")!=null)
			sortOrder = Boolean.parseBoolean(request.getParameter("sortOrder"));
		
		Date startDate = null; 
		Date endDate =  null;
		
		try {
			if (request.getParameter("startdate")!=null && (!request.getParameter("startdate").equals("mm/dd/yyyy")))
				startDate = OL_SEARCH_DATE_FORMAT.parse(request.getParameter("startdate"));
		} catch (java.text.ParseException e1) {
			logger.warn(e1);
		}
		
		try {
			if (request.getParameter("enddate")!=null && (!request.getParameter("enddate").equals("mm/dd/yyyy")))
			{
				endDate = OL_SEARCH_DATE_FORMAT.parse(request.getParameter("enddate"));
				endDate.setHours(11);
				endDate.setMinutes(59);
				endDate.setSeconds(59);
			}
		} catch (java.text.ParseException e1) {
			logger.warn(e1);
		}
		
		/*
		boolean noTerm = (term == null || term.length() == 0);
		boolean noType = (type == null || type.length() == 0);
		
		if (noTerm && noType)
		{
			response.sendRedirect("/legislation/");
		}*/
		
		String format = "html";
		
		if (request.getParameter("format")!=null)
			format = request.getParameter("format");
		

		int pageIdx = 1;//Integer.parseInt((String)request.getAttribute(OpenLegConstants.PAGE_IDX));
		int pageSize = 20;//Integer.parseInt((String)request.getAttribute(OpenLegConstants.PAGE_SIZE));
		
		if (request.getParameter("pageIdx") != null)
			pageIdx = Integer.parseInt(request.getParameter("pageIdx"));
		
		if (request.getParameter("pageSize") != null)
			pageSize = Integer.parseInt(request.getParameter("pageSize"));
		
		//now calculate start, end idx based on pageIdx and pageSize
		int start = (pageIdx - 1) * pageSize;
		
		
		request.setAttribute("type", type);
		
		if (sortField!=null)
		{
			request.setAttribute("sortField", sortField);
			request.setAttribute("sortOrder",sortOrder);
		}
		
		request.setAttribute(OpenLegConstants.PAGE_IDX,pageIdx+"");
		request.setAttribute(OpenLegConstants.PAGE_SIZE,pageSize+"");
	
		
		SearchResultSet srs;
		StringBuilder searchText = new StringBuilder();
		
		if (term != null)
			searchText.append(term);
		
		try {
			
		
			
			if (type != null && type.length() > 0)
			{
				if (searchText.length()>0)
					searchText.append(" AND ");
				
				searchText.append("otype:");
				searchText.append(type);
			}
			
			if (full != null && full.length() > 0)
			{
				if (searchText.length()>0)
					searchText.append(" AND ");
				
				searchText.append("(full:\"");
				searchText.append(full);
				searchText.append("\"");
				
				searchText.append(" OR ");
				searchText.append("osearch:\"");
				searchText.append(full);
				searchText.append("\")");
			}
			
			if (memo != null && memo.length() > 0)
			{
				if (searchText.length()>0)
					searchText.append(" AND ");
				
				searchText.append("memo:\"");
				searchText.append(memo);
				searchText.append("\"");
			}
			
			if (status != null && status.length() > 0)
			{
				if (searchText.length()>0)
					searchText.append(" AND ");
				
				searchText.append("status:\"");
				searchText.append(status);
				searchText.append("\"");
			}
			
			if (sponsor != null && sponsor.length() > 0)
			{
				if (searchText.length()>0)
					searchText.append(" AND ");
				
				searchText.append("sponsor:\"");
				searchText.append(sponsor);
				searchText.append("\"");
			}
			
			if (cosponsors != null && cosponsors.length() > 0)
			{
				if (searchText.length()>0)
					searchText.append(" AND ");
				
				searchText.append("cosponsors:\"");
				searchText.append(cosponsors);
				searchText.append("\"");
			}
			
			if (sameas != null && sameas.length() > 0)
			{
				if (searchText.length()>0)
					searchText.append(" AND ");
				
				searchText.append("sameas:");
				searchText.append(sameas);
			}
			

			if (committee != null && committee.length() > 0)
			{
				if (searchText.length()>0)
					searchText.append(" AND ");
				
				searchText.append("committee:\"");
				searchText.append(committee);
				searchText.append("\"");
			}
			
			if (location != null && location.length() > 0)
			{
				if (searchText.length()>0)
					searchText.append(" AND ");
				
				searchText.append("location:\"");
				searchText.append(location);
				searchText.append("\"");
			}
				
				
			if (startDate != null && endDate != null)
			{
				if (searchText.length()>0)
					searchText.append(" AND ");
				
				searchText.append("when:[");
				searchText.append(startDate.getTime());
				searchText.append(" TO ");
				searchText.append(endDate.getTime());
				searchText.append("]");
			}
			else if (startDate != null)
			{
				if (searchText.length()>0)
					searchText.append(" AND ");
				
				searchText.append("when:[");
				searchText.append(startDate.getTime());
				searchText.append(" TO ");
				
				startDate.setHours(23);
				startDate.setMinutes(59);
				startDate.setSeconds(59);
				
				searchText.append(startDate.getTime());
				searchText.append("]");
			}
			
			term = searchText.toString();
			request.setAttribute("term", term);

			term = BillCleaner.billFormat(term);
			
			srs = null;
			

			srs = SearchEngine.doSearch(term,start,pageSize,sortField,sortOrder);

			if (srs != null)
			{
				request.setAttribute("results", srs);
				String viewPath = "/views/search-" + format + DOT_JSP;
				getServletContext().getRequestDispatcher(viewPath).forward(request, response);
			}
			else
			{
				logger.error("Search Error: " + request.getRequestURI());

				response.sendError(500);
			}
			
		} catch (ParseException e) {
		
			logger.error("Search Error: " + request.getRequestURI(),e);
			
			response.sendError(500);
			
		}
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		logger.info("SearchServlet:init()");
	}

}
