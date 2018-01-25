
import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.util.*;
import java.lang.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


//TODO
//Account for tomorrow being a new month
//Account for leap year
public class habituate {

	public static LocalDateTime currDate = LocalDateTime.now();
	public static String today = null;
	public static String tomorrow = null;

	public static void main(String[] args) throws IOException {
		long id = findCityId("Baltimore", "US");
		setDate();

		// If city was found
		if (id != -1) {
			BufferedReader br = new BufferedReader(new FileReader("Resources/tempData.txt"));
			String fileTemp = br.readLine();
			double currTemp = 0;
			double forecastTemp = 0;
						
			if (fileTemp == null) {
				br.close();
				String currData = getData(String.valueOf(id), "weather");			
				writeData(currData, "weather");
				
				String forecastData = getData(String.valueOf(id), "forecast");			
				writeData(forecastData, "forecast");
				
				currTemp = readData("weather");
				forecastTemp = readData("forecast");
				
				PrintWriter writer = new PrintWriter("Resources/tempData.txt", "UTF-8");
				writer.println(forecastTemp);
				writer.close();
				
				System.out.println("Current: " + currTemp);
				System.out.println("Tomorrow: " + forecastTemp);
			}
			
			else {
				br.close();
				currTemp = Double.parseDouble(fileTemp);
				
				String forecastData = getData(String.valueOf(id), "forecast");			
				writeData(forecastData, "forecast");
				
				forecastTemp = readData("forecast");
				
				PrintWriter writer = new PrintWriter("Resources/tempData.txt", "UTF-8");
				writer.println(forecastTemp);
				writer.close();
				
				System.out.println("Current: " + fileTemp);
				System.out.println("Tomorrow: " + forecastTemp);
			}
			// If change is > 5 Kelvin notify 
			if (Math.abs(forecastTemp - currTemp) > 5) {
				System.out.println("Notify");
			}
			else {
				System.out.println("Dont Notify");
			}
		}
		
		// City or Country not found
		else {
			System.out.println("Not found");
		}
	}

	// HTTP GET api data and return it
	// Type = forecast or weather
	public static String getData(String id, String type) {
		try {
			URL url = new URL("http://api.openweathermap.org/data/2.5/" + type + "?id=" + id +"&appid=283d66db46da67ee68c7e2fdf01c7a42");

			HttpURLConnection connex = (HttpURLConnection) url.openConnection();
			connex.setRequestMethod("GET");

			connex.setRequestProperty("Content-Type", "application/json");

			BufferedReader in = new BufferedReader(new InputStreamReader(connex.getInputStream()));
			String inputLine;
			StringBuffer content = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				content.append(inputLine);
			}

			in.close();
			connex.disconnect();

			return content.toString();
		}
		catch (Exception e) {
			System.out.println("Error");
			System.out.println(e);
		}
		return null;
	}
	
	public static double readData(String type) {
		JSONParser parser = new JSONParser();
		
	    try {            	
	    	Object object = parser.parse(new FileReader("Resources/" + type + "Data.json"));
	    	JSONObject jsonObject = (JSONObject)object;
            if (type.equals("weather")) {
            	JSONObject mainObject = (JSONObject) jsonObject.get("main");
            	double temp = (double) mainObject.get("temp");
            	return temp;
            }
            else {
            	double tempMax = 0;
            	
            	JSONArray lists = (JSONArray) jsonObject.get("list");
            	
            	Iterator it = lists.iterator();
                
                while (it.hasNext() ) {
                	JSONObject slide = (JSONObject) it.next();
                	JSONObject mainTemp = (JSONObject) slide.get("main");

            		String date = (String) slide.get("dt_txt");
            		String[] parts = date.split(" ");
            		
                	if (today.equals(parts[0])) {
                		continue;
                	}
                	else if (!today.equals(parts[0]) && !tomorrow.equals(parts[0])) {
                		break;
                	}
                	
                	double temp = (double) mainTemp.get("temp");
                	
                	if (temp > tempMax) {
                		tempMax = temp;
                	}
                }
                
                return tempMax;
            }
        }
	    
        catch(FileNotFoundException fe)
        {
            fe.printStackTrace();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
	    
		return -1;
	}

	public static void writeData(String temp, String type) {
		try {
			PrintWriter writer = new PrintWriter("Resources/" + type + "Data.json", "UTF-8");
			writer.println(temp);
			writer.close();
		}
		catch (IOException FileNotFoundException) {
			System.out.println("Error trying to write to file");
		}
	}
	
	//Parse the city json file to find city id
	public static long findCityId (String myCity, String myCountry) {
		JSONParser parser = new JSONParser();
		
	    try {
            Object object = parser.parse(new FileReader("Resources/city.list.json"));
            JSONArray jsonArray = (JSONArray)object;
            Iterator i = jsonArray.iterator();
            
            while (i.hasNext()) {
            	JSONObject slide = (JSONObject) i.next();
            	
            	String name = (String)slide.get("name");
            	String country = (String)slide.get("country");
            	
            	long id = (long)slide.get("id");
            	if (name.equals(myCity) && country.equals(myCountry)) {
            		return id;
            	}
            }
        }
	    
        catch(FileNotFoundException fe)
        {
            fe.printStackTrace();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
		return (long) -1;
	}
	
	public static void setDate() {
		int month = currDate.getMonthValue();
		int day = currDate.getDayOfMonth();
		int tomDay = day + 1;
		
		String year = String.valueOf(currDate.getYear());
		String sMonth = String.valueOf(month);
		String sDay = String.valueOf(day);
		String sTomDay = String.valueOf(tomDay);
		
		if (month < 10) {
			sMonth = "0" + String.valueOf(month);
		}
		if (day < 10) {
			sDay = "0" + String.valueOf(day);
		}
		if (tomDay < 10) {
			sTomDay = "0" + String.valueOf(tomDay);
		}
		
		today = year +"-"+ sMonth +"-"+ sDay;
		tomorrow = year +"-"+ sMonth +"-"+ sTomDay;
		System.out.println(today);
		System.out.println(tomorrow);

	}

	
}
