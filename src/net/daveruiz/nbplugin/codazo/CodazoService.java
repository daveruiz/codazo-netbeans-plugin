package net.daveruiz.nbplugin.codazo;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.prefs.Preferences;

/**
 *
 * @author daveruiz
 */
abstract class CodazoService {

	public	static String		codazoServiceURL = "http://api.codazo.com"; // Default
	public	static String		encoding = "UTF-8";
	public	static String		format = "json";

	/**
	 * Calls save method
	 */
	public static String		getShortUrl(String code, CodazoOptions options) throws Exception {
		String postdata = "";

		postdata += "code=" + URLEncoder.encode(code, encoding);
		postdata += "&" + options.buildPost(encoding);

		CodazoResponse response = api( "save", format, postdata );

		return response.url;
	}

	/**
	 * Global API method
	 */
	private static CodazoResponse	api( String method, String format, String data ) throws Exception {
		String service = Preferences.userNodeForPackage(CodazoSettingsPanel.class).get("codazoAPIURL", CodazoService.codazoServiceURL );
		String json="";
		HttpURLConnection conn;

		try {
			URL url = new URL( service.replaceAll( "/*$", "" ) + "/" + method + "." + format );
			conn = (HttpURLConnection) url.openConnection();
		} catch( Exception err ) {
			throw new Exception("Error connecting to "+service+" ("+err+")");
		}

		conn.setDoOutput( true );

		if ( data != null ) {
			conn.setRequestMethod( "PUT" ); // TODO: switch with method
			OutputStreamWriter wr;
			wr = new OutputStreamWriter( conn.getOutputStream() );
			wr.write( data );
			wr.flush();
			wr.close();
		} else {
			conn.setRequestMethod( "GET" );
		}

		BufferedReader rd;
		rd = new BufferedReader( new InputStreamReader( conn.getInputStream() ));

		String line;

		// read response
		while ((line = rd.readLine()) != null) {
			json += line;
		}

		rd.close();

		if ( !"".equals( json ) && json != null ) {
			if ( "json".equals(format) ) { return responseJSON( json ); }
		}

		return null;
	}

	/**
	 * JSON parser
	 */
	private static CodazoResponse	responseJSON( String json ) throws Exception {
		Gson oJson = new Gson();
		CodazoResponse oResponse = null;

		try {
			oResponse = oJson.fromJson( json, CodazoResponse.class );
		} catch( Exception err ) {
			// Shows parse error
			throw new Exception( "Error parsing response: "+err.toString() );
		}

		if ( oResponse != null ) {
			return oResponse;
		} else {
			throw new Exception( "Unexpected response." );
		}
	}

}
