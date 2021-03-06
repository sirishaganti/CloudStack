/**
 * *  Copyright (C) 2011 Citrix Systems, Inc.  All rights reserved
*
 *
 * This software is licensed under the GNU General Public License v3 or later.
 *
 * It is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.cloud.test.longrun;
import java.util.ArrayList;
import java.util.Map;
import java.io.InputStream;
import java.io.IOException;
import java.net.URLEncoder;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import com.cloud.test.stress.TestClientWithAPI;



public class User {
	public static final Logger s_logger= Logger.getLogger(User.class.getClass());
	
	private ArrayList<VirtualMachine> virtualMachines;
	private ArrayList<String> publicIp;
	private String server;
	private String developerServer;
	private String userName;
	private String userId;
	private String apiKey;
	private String secretKey;
	private String password;
	private String encryptedPassword;
	

	public User(String userName, String password, String server, String developerServer)
	{
		this.server=server;
		this.developerServer=developerServer;
		this.userName=userName;
		this.password=password;
		this.virtualMachines = new ArrayList<VirtualMachine>();
		this.publicIp = new ArrayList<String>();
	}
	
	public ArrayList<VirtualMachine> getVirtualMachines() {
		return virtualMachines;
	}


	public void setVirtualMachines(ArrayList<VirtualMachine> virtualMachines) {
		this.virtualMachines = virtualMachines;
	}


	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}
	

	public ArrayList<String> getPublicIp() {
		return publicIp;
	}


	public void setPublicIp(ArrayList<String> publicIp) {
		this.publicIp = publicIp;
	}



	public String getServer() {
		return server;
	}



	public void setServer(String server) {
		this.server = server;
	}



	public String getUserName() {
		return userName;
	}



	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
	
	

	public String getDeveloperServer() {
		return developerServer;
	}

	public void setDeveloperServer(String developerServer) {
		this.developerServer = developerServer;
	}

	public void launchUser() throws IOException {
		String encodedUsername = URLEncoder.encode(this.getUserName(), "UTF-8");
		this.encryptedPassword=TestClientWithAPI.createMD5Password(this.getPassword());
		String encodedPassword = URLEncoder.encode(this.encryptedPassword, "UTF-8");
		String url = this.server + "?command=createUser&username=" + encodedUsername
				+ "&password=" + encodedPassword
				+ "&firstname=Test&lastname=Test&email=alena@vmops.com&domainId=1";
		String userIdStr=null;
		HttpClient client = new HttpClient();
		HttpMethod method = new GetMethod(url);
		int responseCode = client.executeMethod(method);

		if (responseCode == 200) {
			InputStream is = method.getResponseBodyAsStream();
			Map<String, String> userIdValues = TestClientWithAPI.getSingleValueFromXML(is,
					new String[] { "id" });
			userIdStr = userIdValues.get("id");
			if ((userIdStr != null) && (Long.parseLong(userIdStr)!=-1)) {
				this.setUserId(userIdStr);
			}
		}
	}
	
	public void retrievePublicIp(long zoneId) throws IOException{
		
		String encodedApiKey = URLEncoder.encode(this.apiKey, "UTF-8");
		String encodedZoneId=URLEncoder.encode(""+zoneId,"UTF-8");
		String requestToSign = "apiKey=" + encodedApiKey
				+ "&command=associateIpAddress" + "&zoneId="+encodedZoneId;
		requestToSign = requestToSign.toLowerCase();
		String signature = TestClientWithAPI.signRequest(requestToSign, this.secretKey);
		String encodedSignature = URLEncoder.encode(signature, "UTF-8");

		String url = this.developerServer + "?command=associateIpAddress" + "&apiKey="
				+ encodedApiKey + "&zoneId="+encodedZoneId+"&signature="
				+ encodedSignature;

		HttpClient client = new HttpClient();
		HttpMethod method = new GetMethod(url);
		int responseCode=client.executeMethod(method);
		if (responseCode == 200) {
			InputStream is = method.getResponseBodyAsStream();
			Map<String, String> values = TestClientWithAPI.getSingleValueFromXML(is,
					new String[] { "ipaddress" });
			this.getPublicIp().add(values.get("ipaddress"));
			s_logger.info("Ip address is " + values.get("ipaddress"));
		} else if (responseCode == 500) {
			InputStream is = method.getResponseBodyAsStream();
			Map<String, String> errorInfo = TestClientWithAPI.getSingleValueFromXML(is,
					new String[] { "errorcode", "description" });
			s_logger.error("associate ip test failed with errorCode: "
					+ errorInfo.get("errorCode") + " and description: "
					+ errorInfo.get("description"));
		} else {
			s_logger.error("internal error processing request: "
					+ method.getStatusText());
		}
		
	}
	
		public void registerUser()throws HttpException, IOException{
	 
			String encodedUsername = URLEncoder.encode(this.userName, "UTF-8");
			String encodedPassword = URLEncoder.encode(this.password, "UTF-8");
			String url = server + "?command=register&username=" + encodedUsername
					+ "&domainid=1";
			s_logger.info("registering: " + this.userName+" with url "+url);
			HttpClient client = new HttpClient();
			HttpMethod method = new GetMethod(url);
			int responseCode = client.executeMethod(method);
			if (responseCode == 200) {
				InputStream is = method.getResponseBodyAsStream();
				Map<String, String> requestKeyValues = TestClientWithAPI.getSingleValueFromXML(is,
						new String[] { "apikey", "secretkey" });
				this.setApiKey(requestKeyValues.get("apikey"));
				this.setSecretKey(requestKeyValues.get("secretkey"));
			} else if (responseCode == 500) {
				InputStream is = method.getResponseBodyAsStream();
				Map<String, String> errorInfo = TestClientWithAPI.getSingleValueFromXML(is,
						new String[] { "errorcode", "description" });
				s_logger.error("registration failed with errorCode: "
						+ errorInfo.get("errorCode") + " and description: "
						+ errorInfo.get("description"));
			} else {
				s_logger.error("internal error processing request: "
						+ method.getStatusText());
			}
		}
		
}


