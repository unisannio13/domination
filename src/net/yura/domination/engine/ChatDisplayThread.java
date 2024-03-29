// Yura Mamyrin, Group D

package net.yura.domination.engine;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * <p> Display Thread </p>
 * @author Yura Mamyrin
 */


 // This thread reads in input stream from a socket and 
 // appends the output to a TextArea object

public class ChatDisplayThread extends Thread {    
   Risk risk; 
   BufferedReader inChat = null;

    ChatDisplayThread (Risk r, BufferedReader in) 
    { 
       risk = r;
       inChat = in; 
    }

    public void run() {
	//System.out.println("Start DisplayThread ");
	String str;
	boolean badexit=true;

	try {
               while ((str = inChat.readLine()) != null) 
               {
                       if (str.length() > 0)
                       {
                           risk.GameParser(str);
                       }
               }   
	}
	catch (IOException e) {

	    //System.out.println("inChat received an IOException: "+
	    //e.getMessage());
	    //RiskUtil.printStackTrace(e);

	    if ("Stream closed".equals( e.getMessage() ) ) { badexit=false; }

       }

	//System.out.println("Display Thread Finishing");

	if (badexit) { risk.kickedOff(); }

    } 
} 
