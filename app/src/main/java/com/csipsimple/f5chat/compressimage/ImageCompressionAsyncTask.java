package com.csipsimple.f5chat.compressimage;//package com.f5chat.compressimage;
//
//import java.io.ByteArrayInputStream;
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.URL;
//
//import javax.xml.parsers.SAXParser;
//import javax.xml.parsers.SAXParserFactory;
//
//import org.takron.androidclient.chat.XMPPChatServiceAdapter;
//import org.takron.androidclient.util.PreferenceConstants;
//import org.takron.androidclient.util.Utils;
//import org.xml.sax.InputSource;
//import org.xml.sax.SAXException;
//import org.xml.sax.helpers.DefaultHandler;
//
//import android.content.Context;
//import android.database.Cursor;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Matrix;
//import android.graphics.Paint;
//import android.media.ExifInterface;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.os.Environment;
//import android.os.Message;
//import android.provider.MediaStore;
//import android.util.Log;
//
//import com.takron.R;
//
//public class ImageCompressionAsyncTask extends AsyncTask<String, Void, String>
//{
//	//private boolean fromGallery;
//	private ImageLoadingUtils utils;
//	String senderJID,receiverJID,fileType,actualFilepath;
//	long packetId;
//	String whichActivity;
//	Context context;
//	XMPPChatServiceAdapter mServiceAdapter;
//	String mthreadID;
//	private String TagValue = "";
//
//	public ImageCompressionAsyncTask(Context ctx,String senderJID, String receiverJID,String fileType,
//			long packetId,String whichActivity, XMPPChatServiceAdapter mServiceAdapter, String mthreadID) {
//		//this.fromGallery = fromGallery;
//		this.context = ctx;
//		this.senderJID = senderJID;
//		this.receiverJID = receiverJID;
//		this.fileType = fileType;
//		this.packetId = packetId;
//		this.whichActivity = whichActivity;
//		this.mServiceAdapter = mServiceAdapter;
//		utils = new ImageLoadingUtils(ctx);
//		this.mthreadID = mthreadID;
//
//	}
//
//	@Override
//	protected String doInBackground(String... params) {
//
//		String filePath = compressImage(params[0]);
//		actualFilepath = params[0];
//
//		return filePath;
//	}
//
//	public String compressImage(String imageUri)
//	{
//		String filePath;
//		filePath = getRealPathFromURI(imageUri);
//		Bitmap scaledBitmap = null;
//		BitmapFactory.Options options = new BitmapFactory.Options();
//		options.inJustDecodeBounds = true;
//		Bitmap bmp = BitmapFactory.decodeFile(filePath,options);
//
//		int actualHeight = options.outHeight;
//		int actualWidth = options.outWidth;
//		float maxHeight = 816.0f;
//		float maxWidth = 612.0f;
//		float imgRatio = actualWidth / actualHeight;
//		float maxRatio = maxWidth / maxHeight;
//
//		if (actualHeight > maxHeight || actualWidth > maxWidth) {
//			if (imgRatio < maxRatio) {
//				imgRatio = maxHeight / actualHeight;
//				actualWidth = (int) (imgRatio * actualWidth);
//				actualHeight = (int) maxHeight;
//			} else if (imgRatio > maxRatio) {
//				imgRatio = maxWidth / actualWidth;
//				actualHeight = (int) (imgRatio * actualHeight);
//				actualWidth = (int) maxWidth;
//			} else {
//				actualHeight = (int) maxHeight;
//				actualWidth = (int) maxWidth;
//
//			}
//		}
//
//		options.inSampleSize = utils.calculateInSampleSize(options, actualWidth, actualHeight);
//		options.inJustDecodeBounds = false;
//		options.inDither = false;
//		options.inPurgeable = true;
//		options.inInputShareable = true;
//		options.inTempStorage = new byte[16*1024];
//
//		try{
//			bmp = BitmapFactory.decodeFile(filePath,options);
//		}
//		catch(OutOfMemoryError exception){
//			exception.printStackTrace();
//
//		}
//		try{
//			scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
//		}
//		catch(OutOfMemoryError exception){
//			exception.printStackTrace();
//		}
//
//		float ratioX = actualWidth / (float) options.outWidth;
//		float ratioY = actualHeight / (float)options.outHeight;
//		float middleX = actualWidth / 3.0f;
//		float middleY = actualHeight / 3.0f;
//
//		Matrix scaleMatrix = new Matrix();
//		scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);
//
//		Canvas canvas = new Canvas(scaledBitmap);
//		canvas.setMatrix(scaleMatrix);
//		canvas.drawBitmap(bmp, middleX - bmp.getWidth()/3, middleY - bmp.getHeight() / 3, new Paint(Paint.FILTER_BITMAP_FLAG));
//
//
//		ExifInterface exif;
//		try {
//			exif = new ExifInterface(filePath);
//
//			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
//			Log.d("EXIF", "Exif: " + orientation);
//			Matrix matrix = new Matrix();
//			if (orientation == 6) {
//				matrix.postRotate(90);
//				Log.d("EXIF", "Exif: " + orientation);
//			} else if (orientation == 3) {
//				matrix.postRotate(180);
//				Log.d("EXIF", "Exif: " + orientation);
//			} else if (orientation == 8) {
//				matrix.postRotate(270);
//				Log.d("EXIF", "Exif: " + orientation);
//			}
//			scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		FileOutputStream out = null;
//		String filename = getFilename();
//		try {
//			out = new FileOutputStream(filename);
//			scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
//
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//		return filename;
//
//	}
//
//	private String getRealPathFromURI(String contentURI)
//	{
//		Uri contentUri = Uri.parse(contentURI);
//		Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
//		if (cursor == null) {
//			return contentUri.getPath();
//		} else {
//			cursor.moveToFirst();
//			int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
//			return cursor.getString(idx);
//		}
//	}
//
//	/*String unusablePath = contentUri.getPath();
//	int startIndex = unusablePath.indexOf("external/");
//	int endIndex = unusablePath.indexOf("/ACTUAL");
//	String embeddedPath = unusablePath.substring(startIndex, endIndex);
//	Uri.Builder builder = contentUri.buildUpon();
//	builder.path(embeddedPath);
//	builder.authority("media");
//	Uri newUri = builder.build();
//	 */
//
//
//	public String getFilename()
//	{
//		File file = new File(Environment.getExternalStorageDirectory().getPath(),context.getString(R.string.app_name)+"/Takron Images/Sent");
//		if (!file.exists()) {
//			file.mkdirs();
//		}
//		String fileName= getFileName(senderJID,receiverJID,fileType,String.valueOf(packetId));
//		String uriSting = (file.getAbsolutePath() + "/"+ fileName);
//		return uriSting;
//
//	}
//
//	public static String getFileName(String senderJID,String receiverJID,String fileType,String longTimeVal)
//	{
//		String name;
//		name = longTimeVal+".jpg";
//		return name;
//	}
//
//	@Override
//	protected void onPostExecute(String result) {
//		super.onPostExecute(result);
//		UploadThread uploadThread= new UploadThread(result,PreferenceConstants.IMAGE_TRANSFER,whichActivity,mServiceAdapter);
//		uploadThread.start();
//	}
//
//	class UploadThread extends Thread {
//		String path;
//		String response="Fail";
//		int position;
//		String str,type;
//		String whichActivity;
//		Message msg;
//		XMPPChatServiceAdapter mServiceAdapter;
//
//		public UploadThread(String path,String type,String whichActivity, XMPPChatServiceAdapter mServiceAdapter) {
//			this.path=path;
//			this.whichActivity = whichActivity;
//			this.type =type;
//			this.mServiceAdapter=mServiceAdapter;
//		}
//
//		@Override
//		public void run()
//		{
//			try
//			{
//				uploadFile(path) ;
//			}
//			catch(Exception e)
//			{
//			}
//		}
//		public String uploadFile(String sourceFileUri)
//		{
//			DataInputStream inStream;
//			String fileName = getFileName(senderJID,receiverJID,fileType,String.valueOf(packetId));
//			HttpURLConnection  conn = null;
//			DataOutputStream dos = null;
//			String lineEnd = "\r\n";
//			String twoHyphens = "--";
//			String boundary = "*****";
//
//			int bytesRead, bytesAvailable, bufferSize;
//			byte[] buffer;
//			int maxBufferSize = 1 * 1024 * 1024;
//			//int maxBufferSize = 1024;
//			File sourceFile = new File(sourceFileUri);
//			int sentBytes = 0;
//			long fileSize = sourceFile.length();
//
//			if (!sourceFile.isFile())
//			{
//				return "Fail";
//			}
//			else
//			{
//				try {
//
//					FileInputStream fileInputStream = new FileInputStream(sourceFile);
//					URL url = new URL(PreferenceConstants.FILETRANSFER_API);
//
//					// Open a HTTP  connection to  the URL
//					conn = (HttpURLConnection) url.openConnection();
//
//					conn.setDoInput(true); // Allow Inputs
//					conn.setDoOutput(true); // Allow Outputs
//					conn.setUseCaches(false); // Don't use a Cached Copy
//					conn.setRequestMethod("POST");
//					conn.setRequestProperty("Connection", "Keep-Alive");
//					conn.setRequestProperty("ENCTYPE", "multipart/form-data");
//					conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
//					conn.setRequestProperty("uploaded_file", fileName);
//					dos = new DataOutputStream(conn.getOutputStream());
//
//					dos.writeBytes(twoHyphens + boundary + lineEnd);
//					// dos.writeBytes("Content-Disposition: form-data; name="+Environment.getExternalStorageDirectory()+";filename=chatn.png" + lineEnd);
//					//publishProgress(10);
//
//					dos.writeBytes("Content-Disposition: form-data; name=file;filename="+ fileName + "" + lineEnd);
//					dos.writeBytes(lineEnd);
//					// create a buffer of  maximum size
//					bytesAvailable = fileInputStream.available();
//
//					bufferSize = Math.min(bytesAvailable, maxBufferSize);
//
//					buffer = new byte[bufferSize];
//
//					// read file and write it into form...
//					bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//					int downloadedSize=0;
//
//					while (bytesRead > 0)
//					{
//						dos.write(buffer, 0, bufferSize);
//
//						downloadedSize += bufferSize;
//						//	publishProgress((int)(downloadedSize * 100 / fileSize));
//						bytesAvailable = fileInputStream.available();
//						bufferSize = Math.min(bytesAvailable, maxBufferSize);
//						bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//					}
//					// send multipart form data necesssary after file data...
//					dos.writeBytes(lineEnd);
//					dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
//
//					fileInputStream.close();
//					dos.flush();
//					dos.close();
//
//					// Responses from the server (code and message)
//					//------------------ read the SERVER RESPONSE
//					try {
//						inStream = new DataInputStream ( conn.getInputStream() );
//
//						while (( response = inStream.readLine()) != null)
//						{
//							str=response;
//
//						}
//						inStream.close();
//						updateMsgInDB(str);
//					}
//					catch (Exception ioex)
//					{
//						updateMsgInDB("Upload Failed Please Try Again");
//						Log.e("Debug", "error: " + ioex.getMessage(), ioex);
//					}
//					//close the streams //
//
//
//				} catch (MalformedURLException ex)
//				{
//					updateMsgInDB("Upload Failed Please Try Again");
//
//					ex.printStackTrace();
//					Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
//
//
//				} catch (Exception e)
//				{
//					updateMsgInDB("Upload Failed Please Try Again");
//					e.printStackTrace();
//					Log.e("Upload file to server Exception", "Exception : "
//							+ e.getMessage(), e);
//				}
//				return response;
//			}
//		}
//
//		public void updateMsgInDB(String res)
//		{
//			if(res.contains("Upload Failed Please Try Again"))
//			{
//				Utils.updateDB(receiverJID+"@"+PreferenceConstants.SERVER,String.valueOf(packetId),context);
//
//			}
//
//			else if(res.contains("<status>1</status>"))
//				//		else if(res.contains("<result>success</result>"))
//			{
//				ParseXML(res);
//			}
//		}
//
//		private void ParseXML(String result)
//		{
//			// TODO Auto-generated method stub
//			try {
//				SAXParserFactory factory = SAXParserFactory.newInstance();
//				SAXParser saxParser = factory.newSAXParser();
//
//				DefaultHandler handler = new DefaultHandler(){
//					public void startElement(String uri, String localName,String qName,org.xml.sax.Attributes attributes) throws SAXException{
//						if (localName.equalsIgnoreCase("success")){
//
//						}
//					}
//					public void endElement(String uri, String localName,String qName) throws SAXException {
//						String url = TagValue;
//						if(localName.equalsIgnoreCase("message")){
//							if(whichActivity.equals("ChatWindow"))
//							{
//								mServiceAdapter.sendImage(receiverJID+"@"+PreferenceConstants.SERVER,
//										PreferenceConstants.SUBJECT_IMAGE,url,actualFilepath,packetId,mthreadID);
//							}
//							else if(whichActivity.equals("GroupChatWindow")){
//								mServiceAdapter.sendImage(receiverJID + PreferenceConstants.BROADCAST + PreferenceConstants.SERVER,
//										PreferenceConstants.SUBJECT_GROUPIMAGE,url,actualFilepath,packetId,mthreadID);
//							}
//							TagValue = "";
//						}
//					}
//					public void characters(char ch[], int start, int length) throws SAXException {
//						TagValue = new String(ch, start, length);
//					}//end of characters
//				};//end of DefaultHandler
//
//				ByteArrayInputStream is = new ByteArrayInputStream(result.getBytes());
//				saxParser.parse(new InputSource(is), handler);
//
//			} catch (Exception e) {
//
//				e.printStackTrace();
//			}
//		}
//
//	}
//
//
//}