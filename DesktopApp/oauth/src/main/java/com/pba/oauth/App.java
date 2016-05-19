package com.pba.oauth;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets.Details;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.google.api.services.calendar.model.FreeBusyCalendar;
import com.google.api.services.calendar.model.FreeBusyRequest;
import com.google.api.services.calendar.model.FreeBusyRequestItem;
import com.google.api.services.calendar.model.FreeBusyResponse;

/**
 * Hello world!
 *
 */
public class App 
{
	
	
	//App name
	private static final String APPLICATION_NAME = "Jooxter-Google Calendar";
	
	//Logger
	private static final Logger LOG = Logger.getLogger(APPLICATION_NAME);
	
	//User connected on the Jooxter app (cf comparison table jooxter user <-> google user)
	private static final String USER_ID = "user1"; //we'll have to concat @jooxtersandbox.com to get the google id for the tests
	
	//Directory to store user credentials for this app
	private static final java.io.File DATA_STORE_DIR = new java.io.File(".credentials/"+USER_ID+"_jooxter_calendar.json");
	
	//global instance of the FileDataStoryFactory
	private static FileDataStoreFactory DATA_STORE_FACTORY;
	
	//global json factory instance
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	
	//global instance of http transport
	private static HttpTransport HTTP_TRANSPORT;
	
	//Global instance of the scopes required by the app
	//we need R/W rights on Calendar
	private static final List<String> SCOPES = Arrays.asList(CalendarScopes.CALENDAR);
	static{
		try{
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
		}catch(Throwable t){
			t.printStackTrace();
			System.exit(1);
		}
	}
	
	private static final String clientId="74411093080-qtpcoguctfiq2gjo55n2vpasg3q8v6di.apps.googleusercontent.com";
	private static final String clientSecret="V37dwNO540OA3B_huoi759-8";
	
	private static final String TIMEZONE = "Europe/Paris";
	
	
	//Method to create an authorized Credential object
	public static Credential auhorize() throws IOException{
		//Load client secrets (stored in the app resources)
//		InputStream in = App.class.getResourceAsStream("/client_secret.json");
//		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
		
		Details app=new Details();
		app.setClientId(clientId);
		app.setClientSecret(clientSecret);
		GoogleClientSecrets clientSecrets = new GoogleClientSecrets().setInstalled(app);
		
		
		//Build flow and trigger auth request
		GoogleAuthorizationCodeFlow flow = 
				new GoogleAuthorizationCodeFlow.Builder(
						HTTP_TRANSPORT, 
						JSON_FACTORY, 
						clientSecrets, 
						SCOPES)
				.setDataStoreFactory(DATA_STORE_FACTORY)
				.setAccessType("offline")
				.build();
		Credential credentials = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
		
		LOG.info("Credentials for user "+USER_ID+" saved to "+DATA_STORE_DIR.getAbsolutePath());
		
		return credentials;		
	}
	
	//Build & return an authorized Calendar client service 
	//Do not confuse this class with com.google.api.services.calendar.model.Calendar class.
	public static com.google.api.services.calendar.Calendar getCalendarService() throws IOException{
		Credential credential = auhorize();
		return new com.google.api.services.calendar.Calendar.Builder(
				HTTP_TRANSPORT, 
				JSON_FACTORY, 
				credential)
				.setApplicationName(APPLICATION_NAME)
				.build();
	}
	
	
	
    public static void main( String[] args )throws IOException
    {
    	//Build a new authorized api client service
    	com.google.api.services.calendar.Calendar service = getCalendarService();
    	LOG.info("Authorized API Client service instantiated");
    	
    	DateTime now = new DateTime(System.currentTimeMillis());
    	
    	//List of known resources & users (based on the fact that the Jooxter knows them)
    	List<String> resources = new ArrayList<String>();
    	//resources.add("jooxtersandbox.com_2d3739343434393736333935@resource.calendar.google.com"); //Video proj
    	//resources.add("jooxtersandbox.com_34373234353336302d393139@resource.calendar.google.com"); //Meeting room
    	//resources.add("jooxtersandbox.com_2d3632303837353232343130@resource.calendar.google.com"); //atrium
//    	
//		List<String> users = new ArrayList<String>();
//		users.add("user1@jooxtersandbox.com");
//		users.add("user2@jooxtersandbox.com");
    	
		
		//CRUD Google Calendar
		
		//Test of Creation of an Event in Google Calendar
		
    	//Vars for testing purposes
    	DateTime start = new DateTime("2016-03-12T12:00:00.000+02:00");
		DateTime end = new DateTime("2016-05-12T13:20:00.000+02:00");
//		
//		String summary = "Titre d'Ã©vÃ¨nement";
//		String location = "Euratech";
//		String description = "jolie description";
		
    	//System.out.println(createEvent(service, start, end, resources, users, summary, location, description));
    	
    	//Test of requesting all events & a particular event in Google Calendar
    	
		//String eventId = "ue8vqal6it8u5kgi37ck6j56ao";
//    	start = new DateTime("2016-05-13T12:00:00.000+02:00");
    	end = new DateTime("2016-06-13T13:20:00.000+02:00");
		
    	List<Event> events = getEvents(service,start, end);
    	for(Event event : events){
    		DateTime begin = event.getStart().getDateTime();
    		DateTime ending = event.getEnd().getDateTime();
    		String id = event.getId();
    		
    		//System.out.println(id+" "+event.getSummary()+"@"+event.getLocation());
    		System.out.println(event.getSummary()+"@"+event.getLocation());
    	}
//    	System.out.println(getEvent(service, eventId));
//    	System.out.println(deleteEvent(service, eventId));
    	
    	//System.out.println(updateEvent(service, eventId, start, end, resources, users, "Au revoir", "CDS", "meilleure description"));
  	
    	
    }
    
    /* *************
     *    Global 
     *************** */
    
    /*
     * Return if the list of resources are available between start & end
     * @param service : the Google Calendar API service
     * @resources : list of google id of resources (ex : "jooxtersandbox.com_2d3632303837353232343130@resource.calendar.google.com") 
     *              if one of the resources is busy between timeMin & timeMax, the method will return false
     * @start : The start time to check
     * @end : the end time to check
     *
     * @return a boolean if all the resources are available or not
     */
    public static boolean areResourcesAvailable(com.google.api.services.calendar.Calendar service, List<String> resources, DateTime start, DateTime end) throws IOException{
    	//Retrieve the freebusy api service
    	com.google.api.services.calendar.Calendar.Freebusy fb = service.freebusy();
    	
    	//create a list of items that will be checked
    	List<FreeBusyRequestItem> items = new ArrayList<FreeBusyRequestItem>();
    	//Convert all google ids of resources into FreeBusyRequestItem for the processing
    	for(String resourceId:resources){
    		FreeBusyRequestItem item = new FreeBusyRequestItem().setId(resourceId);
    		items.add(item);
    	}
    	//Create & executing the request, adding the start and end time, the timezone & the items
    	FreeBusyRequest req = new FreeBusyRequest().setTimeMax(end).setTimeMin(start).setTimeZone(TIMEZONE).setItems(items);
    	FreeBusyResponse response = fb.query(req).execute();
    	
    	//Processing the response Map by retrieving the calendars
    	Map<String, FreeBusyCalendar> calendars = response.getCalendars();
    	Iterator<Map.Entry<String, FreeBusyCalendar>> entries = calendars.entrySet().iterator();
    	
    	//There will be an entry for each resource calendar
    	while(entries.hasNext()){
    		Map.Entry<String, FreeBusyCalendar> entry = entries.next();
    		
    		FreeBusyCalendar calendar = entry.getValue();
    		
    		System.out.println(calendar);
    		//If the busy field of a calendar is not empty, that means that the resource is needed by someone 
    		//between the timeMin & timeMax
    		if(!calendar.getBusy().isEmpty()){
    			return false;
    		}
    		
    	}
    	//If all the resources wasn't busy so far, it's available !
    	return true;
    }
    
    /*
     * Return if the resource is available between start & end (use under the hood the method areResourcesAvailable)
     * @param service : the Google Calendar API service
     * @resources : a Google resource string id (ex : "jooxtersandbox.com_2d3632303837353232343130@resource.calendar.google.com")
     * @start : The start time to check
     * @end : the end time to check
     *
     * @return a boolean if the resource is available or not
     */
    public static boolean isResourceAvailable(com.google.api.services.calendar.Calendar service, String resource_id, DateTime start, DateTime end) throws IOException{
    	List<String> res = new ArrayList<String>();
    	res.add(resource_id);
    	return areResourcesAvailable(service, res , start, end);
    }

    
    /* *************
     *    CREATE 
     *************** */
    
    /*
     * Create an Event, the resources HAVE TO BE AVAILABLE, but NOT the guests. 
     * @param Service : the Calendar service
     * @startDateTime : the start time of the event
     * @endDateTime : the end time of the event
     * @resources : list of google id of resources (ex : "jooxtersandbox.com_2d3632303837353232343130@resource.calendar.google.com") 
     *              if one of the resources is busy between startDateTime & endDateTime, the method will not create the event
     *              and will return false (Check the areResourcesAvailable(service, resources, start, end) for more informations)
     * @guests : list of the google id of users (ex : "user1@jooxtersandbox.com") 
     * 			 The users may or may not be busy, the method will not check.
     * @summary : Title of the event in Google Calendar
     * @location : location of the event in Google Calendar
     * @description : description of the event in Google calendar
     * 
     * @return a boolean if the event was created or not. 
     * @Throws IOException if the event was not inserted (TODO -> CATCH & return false, but may hide a more important problem, so not now) 
     *  
     *              TODO : use the Builder pattern to clean those params
     */
    public static boolean createEvent(com.google.api.services.calendar.Calendar service, DateTime startDateTime, DateTime endDateTime, List<String> resources, List<String> guests, String summary, String location, String description) throws IOException{
   
    	if(!areResourcesAvailable(service, resources, startDateTime, endDateTime)){
    		return false;
    	}
    	
    	Event event = new Event()
    			.setSummary(summary)
    			.setLocation(location)
    			.setDescription(description);
    	
    	EventDateTime start = new EventDateTime()
    			.setDateTime(startDateTime)
    			.setTimeZone(TIMEZONE);
    	event.setStart(start);
    	
    	EventDateTime end = new EventDateTime()
    			.setDateTime(endDateTime)
    			.setTimeZone(TIMEZONE);
    	event.setEnd(end);
    	
    	List<EventAttendee> attendees = new ArrayList<EventAttendee>();
    	for(String resource:resources){
    		attendees.add(new EventAttendee().setEmail(resource));
    	}
    	for(String guest:guests){
    		attendees.add(new EventAttendee().setEmail(guest));
    	}
    	event.setAttendees(attendees);
    	
    	String calendarId = "primary";
    	event = service.events().insert(calendarId, event).execute();
    	
    	return true;
    			
    }
    
  
    /* *************
     *    REQUEST  
     *************** */
    
    /*
     * Retrieve the list of Google Event objects for the user happening between start & end time (in the limit of 100 events (default by Google API))
     * @param service : The google calendar api service
     * @start : The start time to check
     * @end : the end time to check
     * @return the list of Events (the list may be empty if there is no event)
     */
    public static List<Event> getEvents(com.google.api.services.calendar.Calendar service, DateTime start, DateTime end) throws IOException{
    	CalendarListEntry calendarListEntry = service.calendarList().get("primary").execute();
    	
    	Events events = service.events().list(calendarListEntry.getId())
    			.setTimeMin(start)
    			.setTimeMax(end)
    			.setSingleEvents(false)
    			.execute();
    	return events.getItems();
    }
    
    /*
     * Retrieve the list of Google Event objects for the user happening between start & end time (in the limit of 100 events (default by Google API))
     * @param service : The google calendar api service
     * @param resourceId : the id of the resource to retrieve the events for
     * @param start : The start time to check
     * @param end : the end time to check
     * @return the list of Events (the list may be empty if there is no event)
     */
    public static List<Event> getResourceEvents(com.google.api.services.calendar.Calendar service, String resourceId, DateTime start, DateTime end) throws IOException{
    	CalendarListEntry calendarListEntry = service.calendarList().get(resourceId).execute();
    	
    	Events events = service.events().list(calendarListEntry.getId())
    			.setTimeMin(start)
    			.setTimeMax(end)
    			.setSingleEvents(false)
    			.execute();
    	return events.getItems();
    }
    
    /*
     * Retrieve Google Event objects for the user 
     * @param service : The google calendar api service
     * @param eventId: The google eventID of the event
     * @return an event
     */
    public static Event getEvent(com.google.api.services.calendar.Calendar service, String eventId) throws IOException{
    	return getResourceEvent(service, eventId, "primary");
    }
    
    public static Event getResourceEvent(com.google.api.services.calendar.Calendar service, String eventId, String resourceId) throws IOException{
    	try{
    		return service.events().get(resourceId, eventId).execute();
    	}catch(GoogleJsonResponseException e){
    		return null;
    	}
    }
    
    
    /* *************
     *    UPDATE 
     *************** */
    
    public static boolean updateEvent(com.google.api.services.calendar.Calendar service, String eventId, DateTime startDateTime, DateTime endDateTime, List<String> resources, List<String> guests, String summary, String location, String description) throws IOException{
    	
    	Event event = getEvent(service, eventId);

    	if(event==null){
    		return false;
    	}
    	
    	if(!areResourcesAvailable(service, resources, startDateTime, endDateTime)){
    		boolean areAllResourcesBusyInCurrentEvent = true;
    		
    		for(String resourceId : resources){
    			
    			List<Event> events = getResourceEvents(service, resourceId, event.getStart().getDateTime(), event.getEnd().getDateTime());
    			
    			if(events.size()>1){
    				areAllResourcesBusyInCurrentEvent=false;
    			}
    			for(Event ev:events){
					if(!ev.getId().equals(eventId)){
						areAllResourcesBusyInCurrentEvent = false;
					}
				}
    		}
    		
    		if(!areAllResourcesBusyInCurrentEvent){
    			return false;
    		}
    	}
    	
    	
    	
    	event.setSummary(summary)
			.setLocation(location)
			.setDescription(description);
    	
    	EventDateTime start = new EventDateTime()
    			.setDateTime(startDateTime)
    			.setTimeZone(TIMEZONE);
    	event.setStart(start);
    	
    	EventDateTime end = new EventDateTime()
    			.setDateTime(endDateTime)
    			.setTimeZone(TIMEZONE);
    	event.setEnd(end);
    	
    	List<EventAttendee> attendees = new ArrayList<EventAttendee>();
    	for(String resource:resources){
    		attendees.add(new EventAttendee().setEmail(resource));
    	}
    	for(String guest:guests){
    		attendees.add(new EventAttendee().setEmail(guest));
    	}
    	event.setAttendees(attendees);
    	String calendarId = "primary";
    	
    	event = service.events().update(calendarId, eventId, event).execute();
    	
    	return true;
    	
    	
    }
    
    
    
    
    /* *************
     *    DELETE 
     *************** */
    
    public static boolean deleteEvent(com.google.api.services.calendar.Calendar service, String eventId) throws IOException{
    	
    	Event event = getEvent(service, eventId);
    	
    	if(event==null || event.getStatus().equals("cancelled")){
    		return false;
    	}
    	
    	service.events().delete("primary", eventId).execute();
    	return true;
    }
}
