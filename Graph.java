import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Graph {

  private File cityFile, roadFile;
  private Map<City, Set<Road>> outputRoads;
  private Map<String, City> citiesByName;
  private Map<Integer, City> citiesById;

  public Graph(File cityFile, File roadFile){
    this.cityFile = cityFile;
    this.roadFile = roadFile;
    outputRoads = new HashMap<>();
    citiesByName = new HashMap<>();
    citiesById = new HashMap<>();
    remplirVilles();
    remplirRoutes();
  }

  //BFS
  public String calculerItineraireMinimisantNombreRoutes(String villeDepart, String villeArrivee){
    HashSet<City> visited = new HashSet<>();
    ArrayDeque<City> queue = new ArrayDeque<>();
    Map<City, Road> itinerary = new HashMap<>();

    City currentCity = citiesByName.get(villeDepart);
    City finalCity = citiesByName.get(villeArrivee);

    double totalDistance = 0;

    visited.add(currentCity);
    queue.add(currentCity);

    while(!currentCity.equals(finalCity) && !queue.isEmpty()){
      currentCity = queue.poll();
      Set<Road> routes = arcsSortants(currentCity);
      for(Road road : routes){
        if(!visited.contains(road.getArrivalCity())){
          visited.add(road.getArrivalCity());
          queue.add(road.getArrivalCity());
          itinerary.put(road.getArrivalCity(), road);
        }
      }
    }

    Road lastRoad = itinerary.get(currentCity);
    ArrayList<Road> roadsTaken = new ArrayList<>();

    while(!lastRoad.getDepartureCity().equals(villeDepart) && !queue.isEmpty()){
      roadsTaken.add(lastRoad);
      City departure = lastRoad.getDepartureCity();
      City arrival = lastRoad.getArrivalCity();
      totalDistance += Util.distance(departure.getLatitude(), departure.getLongitude(), arrival.getLatitude(), arrival.getLongitude());
      lastRoad = itinerary.get(departure);
    }
    String result = "Trajet de " + villeDepart + " à " + villeArrivee + " : " + roadsTaken.size() + " routes et " + totalDistance + " km";
    for(int i = roadsTaken.size() - 1; i >= 0; i--){
      City depart = roadsTaken.get(i).getDepartureCity();
      City arrivee = roadsTaken.get(i).getArrivalCity();
      result += "\n" + depart.getName() + " -> " + arrivee.getName() + " ("
          + Util.distance(depart.getLatitude(), depart.getLongitude(), arrivee.getLatitude(), arrivee.getLongitude()) + " km)";
    }
    return result;
  }

  //Dijkstra
  public String calculerItineraireMinimisantKm(String villeDepart, String villeArrivee){
    City cityOfDeparture = citiesByName.get(villeDepart);
    City cityOfDestination = citiesByName.get(villeArrivee);

    Map<City, Double> provisional = new HashMap<>();
    Map<City, Double> finall = new HashMap<>();
    Map<City, Road> paths = new HashMap<>();

    provisional.put(cityOfDeparture, 0.0);

    City current = cityOfDeparture;

    while(!finall.containsKey(cityOfDestination)) {
      double min = Double.MAX_VALUE;
      for (City c : provisional.keySet()) {
        if (provisional.get(c) < min) {
          current = c;
        }
      }
      finall.put(current, min);

      Set<Road> roads = outputRoads.get(current);
      for(Road road : roads) {
        double distance = min + Util.distance(road.getArrivalCity().getLatitude(), road.getArrivalCity().getLongitude(), road.getDepartureCity().getLatitude(), road.getDepartureCity().getLongitude());

      }

    }




  }

  protected void ajouterSommet(City c) {
    Set<Road> roads = new HashSet<>();
    outputRoads.put(c, roads);
  }

  protected void ajouterArc(Road r) {
    City city = r.getDepartureCity();
    for(City elem : outputRoads.keySet()){
      if(elem.equals(city)){
        outputRoads.get(elem).add(r);
      }
    }
  }

  public Set<Road> arcsSortants(City c) {
    return outputRoads.getOrDefault(c, new HashSet<>());
  }

  public boolean sontAdjacents(City c1, City c2){
    for (Road elem : outputRoads.get(c1)) {
      if (elem.getArrivalCity().equals(c2)) {
        return true;
      }
    }
    for (Road elem : outputRoads.get(c2)) {
      if (elem.getArrivalCity().equals(c1)) {
        return true;
      }
    }
    return false;
  }

  public void remplirVilles(){
    try{
      BufferedReader reader = new BufferedReader(new FileReader(cityFile));

      String ligne;

      while ((ligne = reader.readLine()) != null) {
        // Divisez la ligne en utilisant une virgule comme délimiteur
        String[] elements = ligne.split(",");

        City city = new City(Integer.parseInt(elements[0]), elements[1], Double.parseDouble(elements[2]), Double.parseDouble(elements[3]));
        ajouterSommet(city);
        citiesByName.put(city.getName(), city);
        citiesById.put(city.getId(), city);
      }
    }catch(IOException e){
      e.printStackTrace();
    }
  }

  public void remplirRoutes(){
    try{
      BufferedReader reader = new BufferedReader(new FileReader(roadFile));

      String ligne;

      while ((ligne = reader.readLine()) != null) {
        // Divisez la ligne en utilisant une virgule comme délimiteur
        String[] elements = ligne.split(",");

        Road road = new Road(citiesById.get(Integer.parseInt(elements[0])), citiesById.get(Integer.parseInt(elements[1])));
        ajouterArc(road);
      }
    }catch(IOException e){
      e.printStackTrace();
    }
  }
}