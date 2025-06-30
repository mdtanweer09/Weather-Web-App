package in.weather;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;



/**
 * Servlet implementation class Weather
 */
@WebServlet("/Weather")
public class Weather extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Weather() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// API setup
		String apiKey = "521a20d953053cec2c8a1e3dccfc4a34";
		
		//Get city from the form.
		String city = request.getParameter("city");
		
		String encodedCity = URLEncoder.encode(city, "UTF-8");
		
		//URL for the OpenWeather API request.
		
		String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + encodedCity +"&appid="+ apiKey;
		
		//API integration
		URL url = new URL(apiUrl);
		
		HttpURLConnection connection = (HttpsURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		
		int responseCode = connection.getResponseCode();

		InputStream inputStream;

		if (responseCode == 200) {
		    inputStream = connection.getInputStream();
		} else {
		    inputStream = connection.getErrorStream();

		    if (responseCode == 404) {
		        request.setAttribute("errorMessage", "City not found. Please enter a valid city name.");
		    } else {
		        request.setAttribute("errorMessage", "Error fetching weather data. Please try again.");
		    }
		    // Forward back to JSP with the error message and exit the method
		    request.getRequestDispatcher("index.jsp").forward(request, response);
		    return;
		}
		
//		InputStream inputStream = connection.getInputStream();
		InputStreamReader reader = new InputStreamReader(inputStream);
		
		Scanner scanner = new Scanner(reader);
        StringBuilder responseContent = new StringBuilder();

        while (scanner.hasNext()) {
            responseContent.append(scanner.nextLine());
        }
        scanner.close();
//        System.out.println("API Raw JSON: " + responseContent.toString());

        //Parsing the json response to extract data.
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(responseContent.toString(), JsonObject.class);
        
        //Date & Time
        long dateTimestamp = jsonObject.get("dt").getAsLong();
        Instant instant = Instant.ofEpochSecond(dateTimestamp);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss")
                                        .withZone(ZoneId.systemDefault());

        String dateTime = formatter.format(instant);
        
        //Temperature
        double temKel = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
        int temCel = (int)(temKel - 273.15);
        
        // Humidity
        int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();
        
        //Wind Speed
        double windSpeed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();
        
        //Weather Condition
        String weatherCondition = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString();
//        System.out.println("Weather Condition: " + weatherCondition);
        
        
        //Set the data as request attributes.
        request.setAttribute("dateTime", dateTime);
        request.setAttribute("city", city);
        request.setAttribute("temperature", temCel);
        request.setAttribute("humidity", humidity);
        request.setAttribute("windSpeed", windSpeed);
        request.setAttribute("weatherCondition", weatherCondition);
        request.setAttribute("weatherData", responseContent.toString());
        
        connection.disconnect();
        
        //Forward request to jsp page
        request.getRequestDispatcher("index.jsp").forward(request, response);
            
	}

}
