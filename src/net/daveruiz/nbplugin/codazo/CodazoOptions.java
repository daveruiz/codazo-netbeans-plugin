package net.daveruiz.nbplugin.codazo;

import java.net.URLEncoder;

/**
 *
 * @author daveruiz
 */
public class CodazoOptions {

	public Number	startWithLine = 1;
	public String	language = "auto";

	public String	buildPost( String encoding ) {
		String output = "";

		try {
			output +=  "lang=" + URLEncoder.encode( language );
			output += "&line=" + URLEncoder.encode( startWithLine.toString() );
		} catch( Exception err ) {
		}

		return output;
	}

}
