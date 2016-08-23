/**
 * Copyright (C) 2010-2012 Regis Montoya (aka r3gis - www.r3gis.fr)
 * This file is part of inwi.
 *
 *  inwi is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  If you own a pjsip commercial license you can also redistribute it
 *  and/or modify it under the terms of the GNU Lesser General Public License
 *  as an android library.
 *
 *  inwi is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with inwi.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.csipsimple.wizards.impl;

import android.preference.EditTextPreference;

import com.csipsimple.R;
import com.csipsimple.api.SipMessage;
import com.csipsimple.api.SipProfile;
import com.csipsimple.api.SipUri;
import com.csipsimple.api.SipUri.ParsedSipContactInfos;
import com.csipsimple.utils.Log;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class Basic extends BaseImplementation {
	protected static final String THIS_FILE = "Basic W";

	private EditTextPreference accountDisplayName;
	private EditTextPreference accountUserName;
	private EditTextPreference accountServer;
	private EditTextPreference accountPassword;

	private void bindFields() {
		accountDisplayName = (EditTextPreference) findPreference("display_name");
		accountUserName = (EditTextPreference) findPreference("username");
		accountServer = (EditTextPreference) findPreference("server");
		accountPassword = (EditTextPreference) findPreference("password");
	}

	public void fillLayout(final SipProfile account) {
		bindFields();

		//accountDisplayName.setText(account.display_name);
		//accountDisplayName.setText(account.UserName);


		String serverFull = account.reg_uri;
		if (serverFull == null) {
			serverFull = "";
		}else {
			serverFull = serverFull.replaceFirst("sip:", "");
		}

		ParsedSipContactInfos parsedInfo = SipUri.parseSipContact(account.acc_id);
//		accountUserName.setText(parsedInfo.userName);
//		accountUserName.setText(account.UserName);
		accountServer.setText(serverFull);
		//if(serverFull.equals(""))
		//accountServer.setText("tuesdaygroup.net:5061");
		//accountServer.setText("tuesdaygroup.net:5061");
		//accountServer.setText(SipMessage.TuesdayGroupServerIP);//tuesdaygroup.net:5061
		accountServer.setText("85.214.17.140:5060");//tuesdaygroup.net:5061



		//String password = account.TEMP_DATA+"#"+account.TEMP_IMEI+"#U5IOvDNipjmU9P9";
//		System.out.println("<-- my password " + password);
//
//		String passwordMD5 = md5(password);
//		System.out.println("<-- my password in md5 " + passwordMD5);
		//accountPassword.setText(account.data);
		//accountPassword.setText(account.pass);
		accountDisplayName.setText("912929292929");
		accountUserName.setText("912929292929");
		accountPassword.setText("f512345");
	}

//	public static String convertPassMd5(String pass) {
//		String password = null;
//		MessageDigest mdEnc;
//		try {
//			mdEnc = MessageDigest.getInstance("MD5");
//			mdEnc.update(pass.getBytes(), 0, pass.length());
//			pass = new BigInteger(1, mdEnc.digest()).toString(16);
//			while (pass.length() < 32) {
//				pass = "0" + pass;
//			}
//			password = pass;
//		} catch (NoSuchAlgorithmException e1) {
//			e1.printStackTrace();
//		}
//		return password;
//	}

	private String md5(String in) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
			digest.reset();
			digest.update(in.getBytes());
			byte[] a = digest.digest();
			int len = a.length;
			StringBuilder sb = new StringBuilder(len << 1);
			for (int i = 0; i < len; i++) {
				sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));
				sb.append(Character.forDigit(a[i] & 0x0f, 16));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) { e.printStackTrace(); }
		return null;
	}

	public void updateDescriptions() {
		setStringFieldSummary("display_name");
		setStringFieldSummary("username");
		setStringFieldSummary("server");
		setPasswordFieldSummary("password");
	}

	private static HashMap<String, Integer>SUMMARIES = new  HashMap<String, Integer>(){/**
	 *
	 */
	private static final long serialVersionUID = -5743705263738203615L;

		{
			put("display_name", R.string.w_common_display_name_desc);
			put("username", R.string.w_basic_username_desc);
			put("server", R.string.w_common_server_desc);
			put("password", R.string.w_basic_password_desc);
		}};

	@Override
	public String getDefaultFieldSummary(String fieldName) {
		Integer res = SUMMARIES.get(fieldName);
		if(res != null) {
			return parent.getString( res );
		}
		return "";
	}

	public boolean canSave() {
		boolean isValid = true;

		isValid &= checkField(accountDisplayName, isEmpty(accountDisplayName));
		isValid &= checkField(accountPassword, isEmpty(accountPassword));
		isValid &= checkField(accountServer, isEmpty(accountServer));
		isValid &= checkField(accountUserName, isEmpty(accountUserName));

		return isValid;
	}

	public SipProfile buildAccount(SipProfile account) {
		Log.d(THIS_FILE, "begin of save ....");
		account.display_name = accountDisplayName.getText().trim();

		String[] serverParts = accountServer.getText().split(":");
		account.acc_id = "<sip:" + SipUri.encodeUser(accountUserName.getText().trim()) + "@"+serverParts[0].trim()+">";

		String regUri = "sip:" + accountServer.getText();
		account.reg_uri = regUri;
		account.proxies = new String[] { regUri } ;


		account.realm = "*";
		account.UserName = getText(accountUserName).trim();
		account.data = getText(accountPassword);
		account.scheme = SipProfile.CRED_SCHEME_DIGEST;
		account.datatype = SipProfile.CRED_DATA_PLAIN_PASSWD;
		//By default auto transport
		account.transport = SipProfile.TRANSPORT_UDP;

		//account.transport = SipProfile.TRANSPORT_TLS;
		account.use_srtp = 0;
		return account;
	}

	@Override
	public int getBasePreferenceResource() {
		return R.xml.w_basic_preferences;
	}

	@Override
	public boolean needRestart() {
		return false;
	}
}
