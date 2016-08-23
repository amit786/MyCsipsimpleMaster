package com.csipsimple.f5chat.http;


//public class HTTPGet extends AsyncTask<String, String, String>{
//
//	private Context context;
//	private int responseCode;
//	private String message;
//	private String apiresponse;
//	private String whichActivity;
//	private String groupJID;
//	private List<NameValuePair> nameValuePairs;
//	boolean resultStatus;
//	String responseString = "";
//	//SplashActivity splashActivity;
//
//
//
////	Home HomeObj;
//
//	public HTTPGet(LubricantPrice lubricantActivity, Context context)
//	{
//		this.lubricantActivity = lubricantActivity;
//		this.context = context;
//	}
//
//	@Override
//	protected String doInBackground(String... uri)
//	{
//		whichActivity = uri[1];
//		getHttpResponse(uri[0]);
//    //    responseString = callHttpGetRequest(uri[0]);
//		return responseString;
//	}
//
//	private void getHttpResponse(String url1)
//	{
//		StringBuilder content = new StringBuilder();
//
//		try
//		{
//			//Also change in HttpClient class
//			// for https
////			URL url = new URL(url1);
////			HttpsURLConnection httpConn = MySSLSocketFactory.getHttpUrlConnection(url);
////			httpConn.connect();
//			// for https
//
//
//			// for http
//			URL url_conn = new URL(url1);
//			URLConnection urlConnection = url_conn.openConnection();
//			HttpURLConnection httpConn = (HttpURLConnection)urlConnection;
//			// for http
//
//
//			int resCode = httpConn.getResponseCode();
//			String line;
//			if (resCode == HttpURLConnection.HTTP_OK) {
//
//				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
//				while ((line = bufferedReader.readLine()) != null)
//				{
//					content.append(line + "\n");
//				}
//				bufferedReader.close();
//
//				responseString  = content.toString();
//			}
//			else{
//				responseString  =  "Error Message";
//			}
//		}
//		catch(Exception e)
//		{
//			responseString  =  "Error Message";
//			e.printStackTrace();
//		}
//
//	}
//
//
//	@Override
//	protected void onPostExecute(String result){
//		super.onPostExecute(result);
//		/**
//		 * login api response
//		 */
//		if(whichActivity.equals(Const.GET_STATELIST_API)){
//			try {
//				JSONParser parser  =  new JSONParser(context,result);
//				parser.getStateListJsonResponse();
//                signupActivity.displayStatelistResult();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}
//}

