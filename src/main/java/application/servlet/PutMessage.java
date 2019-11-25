package application.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.annotation.Resource;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class PutMessage
 */
@WebServlet("/JMSMessage")
public class PutMessage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Resource(lookup="jms/qcf")
	QueueConnectionFactory cf1;
	
	@Resource(lookup="jms/queue")
	Queue queue;
       

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String strAction = request.getParameter("action");
		PrintWriter output = response.getWriter();
		
	       output.println("<html>");
           output.println("<head>");
           output.println("<title>JMS tests</title>");
           
           output.println("</head>");
           output.println("<body>");
		
		try {

			if (strAction == null) {
				output.println("Please specify the Action");
				output.println("Example : http://<host>:<port>/JDBCApp/JMSMessage?action=send");
			} else if (strAction.equalsIgnoreCase("sendMessage")) {
				// Send Message only
				sendMessage(request, response);
				output.println("<br><a href=\"/JDBCApp/\">Return to main page</a><br>");

			} else if (strAction.equalsIgnoreCase("receiveMessages")) {
				// Receive All messages from queue
				receiveAllMessages(request, response);
				output.println("<br><a href=\"/JDBCApp/\">Return to main page</a><br>");

			} else {
				output.println("Incorrect Action Specified, the valid actions are <br>");
				output.println("action=sendMessage <br>");
				output.println("action=receiveMessages <br>");

			}

		} catch (Exception e) {
			output.println("Something unexpected happened, check the logs or restart the server <br>");
			e.printStackTrace();
		}
        output.println("<body>");

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	
	public void sendMessage(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		PrintWriter out = response.getWriter();
		out.println("SendMessage Started <br>");
		String msg = "Testowy komunikat ";
		if(request.getParameter("msg") != null) {
			msg = request.getParameter("msg");
		}
		
		// create a queue connection factory
		//QueueConnectionFactory cf1 = (QueueConnectionFactory) new InitialContext().lookup("java:comp/env/jms/myQCF");
		// create a queue by performing jndi lookup
		//Queue queue = (Queue) new InitialContext().lookup("java:comp/env/jms/myQ");

		// Creating Context
		JMSContext jmsContext = cf1.createContext();
		// Creating Producer using JMSContext
		JMSProducer producer = jmsContext.createProducer();

		// Creating Text Message using JMSContext
		TextMessage message = jmsContext.createTextMessage();

		message.setText(msg + " " + new Date() );
		// Sending message to Queue
		producer.send(queue, message);

		out.println("Message sent successfuly <br>");

		if (jmsContext != null)
			jmsContext.close();
		out.println("SendMessage Completed <br>");

	}// end of SendMessage

	
	public void receiveAllMessages(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		PrintWriter out = response.getWriter();
		out.println("ReceiveAllMessages Started <br>");
		// create queue connection factory
		//QueueConnectionFactory cf1 = (QueueConnectionFactory) new InitialContext().lookup("java:comp/env/jms/myQCF");

		// create a queue by looking up from the JNDI repository
		//Queue queue = (Queue) new InitialContext().lookup("java:comp/env/jms/myQ");

		// Creating Context
		JMSContext jmsContext = cf1.createContext();

		// Creating Consumer using JMSContext
		JMSConsumer consumer = jmsContext.createConsumer(queue);

		TextMessage msg = null;

		do {
			msg = (TextMessage) consumer.receive(2000);
			if (msg != null)
				out.println("Received  messages <br>" + msg.getText()+"<br>");
		} while (msg != null);

		if (jmsContext != null)
			jmsContext.close();

		out.println("ReceiveAllMessages Completed <br>");

	} // end of ReceiveAllMessages

}
