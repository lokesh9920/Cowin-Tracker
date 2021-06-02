package com.vaccine.cowin;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.vaccine.cowin.models.AvailablityList;
import com.vaccine.cowin.models.Center;
import com.vaccine.cowin.models.Session;

@Service
public class EntryPoint {

	ScheduledExecutorService scheduledThreadPoolForQueries = Executors.newSingleThreadScheduledExecutor();

	@Value("${required.date}")
	String startDate;
	
	@Value("${future.date}")
	String futureDate;
	
	@PostConstruct
	public void startQueries() {
		System.out.println("Starting the PostConstruct");

		scheduledThreadPoolForQueries.schedule(new Runnable() {
			
			@Override
			public void run() {
				
				System.out.println("**** TASK STARTED ****");

				//infinite loop to run indefinitely
				while(true) {
					monitorRequiredDate();
					monitorFutureDate();
				}
				
			}
		}, 1, TimeUnit.MINUTES);
		
	}
	
	
	private void monitorRequiredDate() {
		RestTemplate restTemplate = getRestTemplate();		
		System.out.println("Got RestTemplate");
		
		try {
		ResponseEntity<AvailablityList> fetchedPosts = 
				restTemplate.getForEntity("https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/calendarByDistrict?district_id=603&date="+startDate,
						AvailablityList.class);
		
			ArrayList<Center> fetchedCentersList = fetchedPosts.getBody().getCenters();
			iterateOnCenters(fetchedCentersList, "required");
			
		}catch (Exception e) {
			System.out.println("**ERROR**: Exception encountered during restRequest");
			e.printStackTrace();
		}

	}
	
	private void monitorFutureDate() {
		RestTemplate restTemplate = getRestTemplate();		
		System.out.println("Got resttemplate");
		
		try {
		ResponseEntity<AvailablityList> fetchedPosts = 
				restTemplate.getForEntity("https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/calendarByDistrict?district_id=603&date="+futureDate,
						AvailablityList.class);
		
			ArrayList<Center> fetchedCentersList = fetchedPosts.getBody().getCenters();
			iterateOnCenters(fetchedCentersList, "future");
			
		}catch (Exception e) {
			System.out.println("**ERROR**: Exception encountered during restRequest");
			e.printStackTrace();
		}

	}
	
	
	private void iterateOnCenters(ArrayList<Center> centers, String filePrefix) {
		
		for(Center center : centers) {
			ArrayList<Session> sessions = center.getSessions();
			
			for(Session session : sessions) {
				if(session.getDate().equals("04-06-2021")) {
					
						print(getCurrentDateTime() + " ...The Center: " + center.toString() + "started vaccines at " + getCurrentDateTime() + "\n", filePrefix+"Starting_"+session.getDate());
				}
				
				if(session.getVaccine().equalsIgnoreCase("COVISHIELD") && session.getAvailable_capacity_dose1() > 0
						&& session.getMin_age_limit() == 18){
					
					//favorable for me
					print(getCurrentDateTime() + " ...The Center: " + center.toString() + "started vaccines at " + getCurrentDateTime() + "\n", filePrefix+"favorable_"+session.getDate());
					
				}
			}
		}
	}
	
	
	private void print(String printable, String fileName) {
		
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
	    
			writer.append(printable);
		    writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	    
	}
	
	private String getCurrentDateTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");  
	    Date date = new Date();  
	    return formatter.format(date); 
	}
	
	
	private RestTemplate getRestTemplate() {
			
			/**
			 * 
			 * Below lines are to skip certificate verfication for consuming https url
			 * 
			 */
			try {
				
			
			TrustStrategy acceptingTrustStrategy = (X509Certificates,s)->true;
		    SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
		    SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
	
			
			CloseableHttpClient httpClient = HttpClients.custom()
					.setSSLSocketFactory(csf)
					.build();
			HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
			requestFactory.setHttpClient(httpClient);
		
			// till here
			
			RestTemplate restTemplate = new RestTemplate(requestFactory);
			return restTemplate;
			}catch (Exception e) {
				System.out.println("**ERROR**: Encountered exception while getting restTemplate");
			}
			return null;
		}

}
