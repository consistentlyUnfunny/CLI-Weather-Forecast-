import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import com.sun.net.httpserver.HttpHandler;

public class weatherApi {
    private static double lat, lon;
    private static String nameOfCity;
    public boolean getCoord() throws Exception { //throws exception because uri method throws it
        //get longitude and latitude
        city name = new city();
        String cityName = name.getCity();
        HttpRequest postReq = HttpRequest.newBuilder() //build necessary information
                .uri(new URI("http://api.openweathermap.org/geo/1.0/direct?" + "q=" + cityName + "&limit=3&appid=9174218ad064daac79f8a475b7742a0c"))
                .build();
        System.out.println(postReq.uri());

        HttpClient client = HttpClient.newBuilder().build(); //build a client to send response
        HttpResponse<String> postResponse = client.send(postReq, HttpResponse.BodyHandlers.ofString()); //send response and store result in postResponse
        System.out.println(postResponse.body());

        JsonArray jsonArray = JsonParser.parseString(postResponse.body()).getAsJsonArray(); //convert content of json into object array

        if (jsonArray.size() > 0){
            JsonObject cityObject = jsonArray.get(0).getAsJsonObject(); //get first object in the json array
            nameOfCity = cityObject.get("name").getAsString(); //get city name
            lat = cityObject.get("lat").getAsDouble(); //get latitude
            lon = cityObject.get("lon").getAsDouble(); //get longitude
            System.out.println("Latitude: " + lat);
            System.out.println("Longitude: " + lon);
            return true;

        } else { //json empty if city is not found
            System.out.println("City not found");
            return false;
        }

    }

    public void currentWeather() throws Exception {
        HttpRequest postReq2 = HttpRequest.newBuilder()
                .uri(new URI("https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=9174218ad064daac79f8a475b7742a0c&units=metric"))
                .build();
        System.out.println(postReq2);
        HttpClient client2 = HttpClient.newBuilder().build();
        HttpResponse<String> postResponse2 = client2.send(postReq2, HttpResponse.BodyHandlers.ofString());
        System.out.println(postResponse2.body());

        //get as object cause can't convert to array, only [] can be converted to array
        //getAsJsonObject detects and organise content of json into object ("coord", "weather", "main" etc.)
        JsonObject jsonObj = JsonParser.parseString(postResponse2.body()).getAsJsonObject();

        //weather object contains array
        JsonArray weatherArray = jsonObj.getAsJsonArray("weather");
        JsonObject weatherObj = weatherArray.get(0).getAsJsonObject();
        String weather = weatherObj.get("main").getAsString();
        String desc = weatherObj.get("description").getAsString();

        JsonObject mainObj = jsonObj.getAsJsonObject("main");
        float temp = mainObj.get("temp").getAsFloat();
        float feelTemp = mainObj.get("feels_like").getAsFloat();
        float minTemp = mainObj.get("temp_min").getAsFloat();
        float maxTemp = mainObj.get("temp_max").getAsFloat();
        float pressure = mainObj.get("pressure").getAsFloat();
        float humid = mainObj.get("humidity").getAsFloat();

        //name of city obtained from getCoord()
        System.out.println("Current Weather in " + nameOfCity);
        System.out.println("Weather: " + weather + ", " + desc);
        System.out.print("Actual Temp.: " + temp + "°C\nFeels Like: " + feelTemp + "°C\nTemp. Range: " + minTemp + " ~ " + maxTemp + "°C");
        System.out.println("\nPressure: " + pressure + " hPa\nHumidity: " + humid + "%");
    }

    public void getForecast() throws Exception{
        HttpRequest postReq3 = HttpRequest.newBuilder()
                .uri(new URI("https://api.openweathermap.org/data/2.5/forecast?lat=" + lat + "&lon=" + lon + "&appid=9174218ad064daac79f8a475b7742a0c&units=metric"))
                .build();
        System.out.println(postReq3);
        HttpClient client3 = HttpClient.newBuilder().build();
        HttpResponse<String> postResponse3 = client3.send(postReq3, HttpResponse.BodyHandlers.ofString());
        System.out.println(postResponse3.body());

        //convert json into object
        JsonObject jsonObject2 = JsonParser.parseString(postResponse3.body()).getAsJsonObject();
        //extract list of weather from object
        JsonArray weatherTimeArray = jsonObject2.getAsJsonArray("list");

        for (JsonElement elements : weatherTimeArray){
            //create a obj for every time frame
            JsonObject innerObj = elements.getAsJsonObject();
            //make main another object, it contains temp
            JsonObject mainInfo = innerObj.getAsJsonObject("main");
            //make weather into an array
            JsonArray weatherInfo = innerObj.getAsJsonArray("weather");
            float temp = mainInfo.get("temp").getAsFloat();
            String weather = weatherInfo.get(0).getAsJsonObject().get("description").getAsString(); //weatherInfo is in array, therefore use index to access
            String datetime = innerObj.get("dt_txt").getAsString();
            System.out.printf("%s\n%s\n%.2f°C\n\n", datetime, weather.toUpperCase(), temp);
        }
    }


    public static void main(String[] args) throws Exception {
        weatherApi weatherForecast = new weatherApi();
        if (weatherForecast.getCoord() == true){
            weatherForecast.currentWeather();
            weatherForecast.getForecast();
        }
    }
}

class city { //initialize city name
    private static String city;
    public city(){ //take input
        String inputTrim, inputFormat;
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter city: ");
        String input = scan.nextLine();
        inputTrim = input.trim();
        inputFormat = inputTrim.replace(" ", "+");
        setCity(inputFormat);
    }

    public void setCity(String name){ //setter method
        city = name;
    }

    public String getCity(){ //getter method
        return city;
    }
}