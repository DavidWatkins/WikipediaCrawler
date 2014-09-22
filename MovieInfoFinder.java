import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * THe movieInfoFinder class that goes through each page and finds the information for each question asked in HW 4
 * 
 * @author David Watkins
 * @UNI djw2146
 */
public class MovieInfoFinder {
    //Only hard code main portal page, and make timeout large enough to wait for unruly pages
    private final int TIME_OUT = 50000;
    private final URL WIKI_PORTAL_URL = new URL("http://en.wikipedia.org/wiki/Portal:Academy_Award");
    private Document wikiPortalPage;
    
    /**
     * Construct a new MovieInfoFinder and initialize the portal page
     * @throws IOException
     */
    public MovieInfoFinder() throws IOException{
        wikiPortalPage = Jsoup.parse(WIKI_PORTAL_URL, TIME_OUT);
    }
    
    //Helper Methods
    
    /**
     * Returns the column elements at a particular column index minus any rowspan elements
     * 
     * @param page The webpage to get the column elements
     * @param columnIndex The index of the columns
     * @return Column elements
     */
    private Elements getColumnsNoRowspan(Document page, int columnIndex){
        Elements column = page.select("table.wikitable:not([style=text-align: center]) tr td:eq(" + columnIndex + "):not([rowspan])");
        return column;
    }
    
    /**
     * Returns all columns from a particular webpage at the column index with elements that have rowspan
     * 
     * @param page The web page
     * @param columnIndex The column index to obtain columns
     * @return All column elements at the particular index
     */
    private Elements getColumnsRowspan(Document page, int columnIndex){
        Elements column = page.select("table.wikitable:not([style=text-align: center]) tr td:eq(" + columnIndex + ")[rowspan]");
        return column;
    }
    
    /**
     * Get the column indices for a particular page and title
     * 
     * @param page THe page to check for tables
     * @param titles The list of titles to check
     * @param subtractor The potential error in indexing
     * @return The list of column indices
     */
    private ArrayList<Integer> getColumnIndeces(Document page, String[] titles, int subtractor){
        ArrayList<Integer> columnIndeces = new ArrayList<Integer>();
        Elements titleBars = page.select("table.wikitable tr");
        //For each titleBar, determine if the title in question is within the titlebar
        if(titleBars.size() > 0){
            for(Element titleBar:titleBars){
                if(titleBar.select("th").size() > 0){
                    Elements titleList = titleBar.select("th");
                    for(int i = 0; i < titleList.size(); i++){
                        String titleName = titleList.get(i).ownText();
                        for(String title:titles){
                            if(titleName.toLowerCase().contains(title.toLowerCase()))
                                columnIndeces.add(new Integer(i-subtractor));
                        }       
                    }
                }
            }
        }
        
        return columnIndeces;
    }
    
    /**
     * Get the year the actress was born in
     * 
     * @param actressPageURL The url for the actress
     * @return the year the actress was born
     * @throws IOException
     */
    private int yearBornActress(URL actressPageURL) throws IOException{
        Document actressPage = Jsoup.parse(actressPageURL, TIME_OUT);
        int yearBorn = 0;
        
        //Navigate to biography, and return the first number in the bday attribute, which is the year they were born
        Elements yearBornInfo = actressPage.getElementsByAttributeValue("class", "infobox biography vcard").select("span");
        for(Element e:yearBornInfo){
            if(e.getElementsByAttributeValue("class", "bday").size() > 0){
                yearBorn = Integer.parseInt(e.getElementsByAttributeValue("class", "bday").get(0).ownText().split("-")[0]);
            }
        }
        return yearBorn;
    }
    
    /**
     * Get the directors properly and associate them with the given movie
     * 
     * @param directors The list of directors
     * @param bestDirectorPage The best director page
     */
    private void getNomDirectors(ArrayList<PersonInfo> directors, Document bestDirectorPage){
        //Make sure the tables are correct
        Elements directorsNom = getColumnsNoRowspan(bestDirectorPage, 2);
        Elements directorInfo = directorsNom.select("*:not(td):not(p):not(br)");
        directorInfo.removeAll(directorsNom.select("i").select("a"));
        
        //For the length of the elements, get each movie for each director
        int directorCount = 0;
        boolean used = false;
        ArrayList<String> dNames = new ArrayList<String>();
        while(directorCount < directorInfo.size()){
          //If the next index is a director
            if(directorInfo.get(directorCount).tagName().equals("a") && used){
                dNames = new ArrayList<String>();
                used = false;
            }
            
            //If the next index is a director or if it is a movie
            if(directorInfo.get(directorCount).tagName().equals("a"))
                dNames.add(directorInfo.get(directorCount).ownText());
            else if(directorInfo.get(directorCount).tagName().equals("i")){
                //Add movie to each director in dNames
                for(String name:dNames)
                    addPerson(directors, name, directorInfo.get(directorCount).text());
                used = true;
            }
            directorCount++;
        }
    }
    
    /**
     * Add a person to the list, if they are already in the list, add only their movie
     * 
     * @param people The list of person objects
     * @param name The name of the person
     * @param movie The name of the movie
     */
    private void addPerson(ArrayList<PersonInfo> people, String name, String movie){
        PersonInfo temp = new PersonInfo();
        temp.setName(name);
        PersonInfo p = personExists(people, temp);
        //If the person is null, then it doesn't exist and create a new person
        if(p == null){
            temp.addMovie(movie);
            people.add(temp);
        }
        else
            p.addMovie(movie);
    }
    
    /**
     * Returns if a person exists in a list based on name
     * 
     * @param list list of person objects
     * @param person the person to be checked against
     * @return null if the person is not in the list, else return the person
     */
    private PersonInfo personExists(ArrayList<PersonInfo> list, PersonInfo person){
        for(PersonInfo p:list){
            //If the name is something and it is equal, return PersonInfo
            if(!p.getName().equals("") && p.getName().equals(person.getName()))
                return p;
        }
        return null;
    }
    
    /**
     * Helper method for question6
     * Returns the page which the maximum number of nominations
     * 
     * @param listOfCountriesPage The list of countries page
     * @return The page of the country with the most nominations
     * @throws IOException 
     */
    private Document findMaxNoms(Document listOfCountriesPage, PersonInfo country) throws IOException{
        //Get country names and table nominations data
        Elements tablesNoms = listOfCountriesPage.select("[class~=sortable] tr td:eq(2)");
        Elements countryNames = listOfCountriesPage.select("[class~=sortable] tr td:eq(0)");
        int currentMaxIndex = 0;
        //For each in table noms, determine which is the maximum
        for(int index = 0; index < tablesNoms.size(); index++){
            if(Integer.parseInt(tablesNoms.get(index).ownText()) > Integer.parseInt(tablesNoms.get(currentMaxIndex).ownText()))
                currentMaxIndex = index;
        }
        //Set the name of the country and get the url for the new page
        country.setName(countryNames.get(currentMaxIndex).getElementsByAttribute("title").get(0).attr("title"));
        URL countryPageURL = new URL(countryNames.get(currentMaxIndex).select("a[title~=List of]").get(0).absUrl("abs:href"));
        return Jsoup.parse(countryPageURL, TIME_OUT);
    }
    
    /**
     * If a movie is included in a particular list, return true
     * 
     * @param movies The list of movies
     * @param newMovie The movie name
     * @return Whether or not the movie already exists in the list
     */
    private MovieInfo hasMovie(ArrayList<MovieInfo> movies, String newMovie){
        for(MovieInfo movie:movies){
            if(movie.getName().equalsIgnoreCase(newMovie))
                return movie;
        }
        return null;
    }
    
    /**
     * Check if a particular page includes the star, return true if it does
     * 
     * @param webPage The document page to be checked
     * @param actorName The name of the actor
     * @return Wether or not the actor is starred
     * @throws IOException
     */
    private boolean starringList(URL webPage, String actorName) throws IOException{
        //For each URL and Film, get list of starring and compare it to actorName
        try{
            Document filmPage = Jsoup.parse(webPage, TIME_OUT);
            Elements infobox = filmPage.select("[class~=infobox] tr");
            for(Element e:infobox){
                if(e.select("th").size() > 0 && e.select("th").get(0).ownText().equalsIgnoreCase("starring")){
                    Elements stars = e.select("td");
                    if(stars.text().toLowerCase().contains(actorName.toLowerCase()))
                        return true;
                }
            }
        }
        catch(HttpStatusException e){
            //e.printStackTrace();
            //Skip over an invalid movie link
        }
        
        return false;
    }
    
    /**
     * Creates a a list of encounters with the movie names and also adds movies it encounters from the films
     * 
     * @param films Elements that contains film info
     * @param movies The arraylist of movies to be appended to
     * @param year The year
     * @return A list of encounters with each movie
     */
    private ArrayList<String> checkMovies(Elements films, ArrayList<MovieInfo> movies, int year){
        ArrayList<String> recentlyEncountered = new ArrayList<String>();
        for(int j = 0; j < films.size(); j++){
            MovieInfo p = hasMovie(movies, films.get(j).text());
            if(p != null){
                if(j==0)
                    p.incrementAwardCount();
                p.incrementNumNoms();
            }
            else{
                MovieInfo movie = new MovieInfo(films.get(j).text());
                if(j==0)
                    movie.incrementAwardCount();
                movie.incrementNumNoms();
                movie.setYear(year);
                movies.add(movie);
            }
            if(!recentlyEncountered.contains(films.get(j).text()))
                recentlyEncountered.add(films.get(j).text());
        }
        return recentlyEncountered;
    }
    
    /**
     * If the header contains one of the titles, return true
     * 
     * @param header header string value
     * @param titles array of titles that header could be
     * @return whether or not the header is equal
     */
    private boolean headerContains(String header, String[] titles){
        for(String title:titles)
            if(header.toLowerCase().contains(title.toLowerCase()))
                return true;
        return false;
    }
    
    /**
     * Gets the Document page from the given title value
     * 
     * @param page THe document to parse
     * @param phrase The title value
     * @return A document object
     * @throws IOException
     */
    private Document getPageFrom(Document page, String phrase) throws IOException{
        //Get the new url
        Elements websites = page.getElementsByAttributeValueContaining("title", phrase);
        URL newPageURL = new URL(websites.get(0).absUrl("abs:href"));
        
        //If the url works, return the page
        Document newPage = null;
        if(!newPageURL.getPath().equals(""))
            newPage = Jsoup.parse(newPageURL, TIME_OUT);
        else{
            System.out.println("Extreme error");
            System.exit(0);
        }
        return newPage;
    }
    
    //Questions

    /**
    * Finds the list of movies nominated for Best Picture where
    *  one of the production companies was productionCompany
    * 
    * @param productionCompany The production company the user was looking for
    * @return MovieData object containing all movies and subsequent info
     * @throws IOException 
    */
    public ArrayList<MovieInfo> question1(String productionCompany) throws IOException{
        //Navigate to the page, and if the production company is invalid, don't bother
       Document bestPicturePage = getPageFrom(wikiPortalPage, "Academy Award for Best Picture");
       ArrayList<MovieInfo> movieNames = new ArrayList<MovieInfo>();
       if(productionCompany.equals("")){
           return movieNames;
       }
       
       //Get the movie titles and production companies
       Elements movieTitles = getColumnsNoRowspan(bestPicturePage, 0);
       Elements prodCompies = getColumnsNoRowspan(bestPicturePage, 1);
       for(int i = 0; i < prodCompies.size(); i++){
           //If the production company contains productionCompany, add the movie
           if(prodCompies.get(i).text().toLowerCase().contains(productionCompany.toLowerCase())){
               MovieInfo movie = new MovieInfo(movieTitles.get(i).text());
               movie.setProdComp(prodCompies.get(i).text());
               movieNames.add(movie);
           }
       }
       return movieNames;
    }
    
    /**
     * Returns the list of writers for the best original screenplay with the title movieName
     * 
     * @param movieName The name of the movie
     * @return A MovieData object containing the list of writers
     * @throws IOException 
     */
    public ArrayList<PersonInfo> question2(String movieName) throws IOException{
        //Initialize page and list
        ArrayList<PersonInfo> writers = new ArrayList<PersonInfo>();
        Document bestOriginalScreenplayPage = getPageFrom(wikiPortalPage, "Academy Award for Writing Original Screenplay");
        
        //Get the filmnames and writernames
        Elements filmNames = getColumnsNoRowspan(bestOriginalScreenplayPage, 0);
        Elements writerNames = getColumnsNoRowspan(bestOriginalScreenplayPage, 1);
        
        //For each filmname, if it is equal, get the writers
        for(int i = 0; i < filmNames.size(); i++){
            if(filmNames.get(i).text().equalsIgnoreCase(movieName)){
                for(Element writer:writerNames.get(i).select("a")){
                    PersonInfo w = new PersonInfo();
                    w.setName(writer.ownText());
                    w.addMovie(filmNames.get(i).text());
                    writers.add(w);
                }
            }
        }
        
        return writers;
    }
    
    /**
     * Get the actors whose role was role, from the best actor page
     * 
     * @param role Role they played
     * @return The list of actors
     * @throws IOException
     */
    public ArrayList<PersonInfo> question3(String role) throws IOException{
        //Initialize page and list
        ArrayList<PersonInfo> actorList = new ArrayList<PersonInfo>();
        Document bestLeadingActorPage = getPageFrom(wikiPortalPage, "Academy Award for Best Actor");
        
        //The name of the column
        String[] a = {"actor"};
        String[] r = {"role"};
        String[] f = {"film"};
        
        //Get the indices for the column of the particular name
        ArrayList<Integer> actorIndeces = getColumnIndeces(bestLeadingActorPage, a, 1);
        ArrayList<Integer> roleIndeces = getColumnIndeces(bestLeadingActorPage, r, 1);
        ArrayList<Integer> filmIndeces = getColumnIndeces(bestLeadingActorPage, f, 1);
        
        //For each table, go through the indices and get the information
        Elements tables = bestLeadingActorPage.select("table.wikitable:not([style=text-align: center])");
        for(int i = 0; i < tables.size(); i++){
            Elements actors = tables.get(i).select("td:eq(" + actorIndeces.get(i).intValue() + "):not([rowspan])");
            Elements roles = tables.get(i).select("td:eq(" + roleIndeces.get(i).intValue() + "):not([rowspan])");
            Elements films = tables.get(i).select("td:eq(" + filmIndeces.get(i).intValue() + "):not([rowspan])");
            for(int j = 0; j < roles.size(); j++){
                //If it is equal, add the actor
                if(roles.get(j).text().toLowerCase().startsWith(role.toLowerCase())){
                    PersonInfo p = new PersonInfo();
                    p.setName(actors.get(j).text());
                    p.setRole(roles.get(j).text());
                    p.addMovie(films.get(j).text());
                    actorList.add(p);
                }
            }
        }
        return actorList;
    }
    
    /**
     * For the given year, return all of the leading actresses
     * 
     * @param year 
     * @return The list of leading actresses for a given year
     * @throws IOException
     */
    public ArrayList<PersonInfo> question4(int year) throws IOException{
        //Navigate to the page
        Document leadingActressPage = getPageFrom(wikiPortalPage, "Academy Award for Best Actress");
        ArrayList<PersonInfo> actresses = new ArrayList<PersonInfo>();
        
        //Get the year columns and the actress info columns
        Elements years = getColumnsRowspan(leadingActressPage, 0);
        Elements actressInfo = getColumnsNoRowspan(leadingActressPage, 0);
        int actressCount = 0;
        for(Element e:years){
            //The rowspan is the number of rows the years column skips
            int rowspan = Integer.parseInt(e.attr("rowspan"))-1;
            if(!e.text().toLowerCase().contains(Integer.toString(year)))
                actressCount += rowspan;
            else{
                //Get all the actresses
                for(int i = actressCount; i < rowspan+actressCount;i++){
                    PersonInfo actress = new PersonInfo();
                    Element act = actressInfo.get(i).getElementsByTag("a").get(0);
                    URL actressPageURL = new URL(act.absUrl("abs:href"));
                    //Navigate to the actress' page and get her year of birth
                    int yearBorn = yearBornActress(actressPageURL);
                    
                    //Set actress' age and name from the bio information
                    actress.setAge(year - yearBorn);
                    actress.setName(act.ownText());
                    actresses.add(actress);
                }
            }
        }
        
        return actresses;
    }
          
    /**
     * Get the list of directors that were nominated for at least numAwards
     * 
     * @param numAwards The number of nominations
     * @return The list of directors with at least numAwards
     * @throws IOException
     */
    public ArrayList<PersonInfo> question5(int numAwards) throws IOException{
        //Get the page for the diretor and get the columns of their movies
        Document bestDirectorPage = getPageFrom(wikiPortalPage, "Academy Award for Directing");
        ArrayList<PersonInfo> directors = new ArrayList<PersonInfo>();
        Elements directorsWon = getColumnsNoRowspan(bestDirectorPage, 1);
        
        //For each director add their name. 
        for(Element director:directorsWon){
            Elements info = director.select("a");
            String name = info.get(0).ownText();
            String movieName = info.get(1).ownText();
            addPerson(directors, name, movieName);
        }
        
        //Get the nominated directors
        getNomDirectors(directors, bestDirectorPage);
        
        //Add all the directors to the list
        ArrayList<PersonInfo> mostCommon = new ArrayList<PersonInfo>();
        for(PersonInfo p : directors){
            if(p.getMovies().size() >= numAwards)
                mostCommon.add(p);
        }
        
        return mostCommon;
    }
    
    /**
     * Gets the country with the most number of nominations
     * 
     * @return Country with the most nominations
     * @throws IOException
     */
    public PersonInfo question6() throws IOException{
        //Does not deserve own class, PersonInfo is enough
        PersonInfo country = new PersonInfo();
        
        //Navigate to correct list of countries page
        Document foreignLanguagePage = getPageFrom(wikiPortalPage, "Academy Award for Best Foreign Language Film");
        Document listOfCountriesPage = getPageFrom(foreignLanguagePage, "List of Countries by Number");
        
        //Determine the country with the maximum number of nominations
        Document listOfSubmissions = findMaxNoms(listOfCountriesPage, country);
        
        //Determine the movies that won/were nominated
        Elements titles = listOfSubmissions.select("table.wikitable td:eq(1) i a");
        Elements results = listOfSubmissions.select("table.wikitable td:eq(4)");
        for(int i = 0; i < results.size(); i++){
            String ownText = results.get(i).ownText().toLowerCase();
            if(ownText.contains("won academy award") || ownText.contains("nominee")){
                country.addMovie(titles.get(i).ownText());
                country.incrementNumNoms();
            }
        }
        
        return country;
    }
    
    /**
     * Gets the movies that starred the actor from the awardtype page
     * There are some pages that this method does no work for, but it does work for the majority of awards
     * 
     * @param awardType The type of award
     * @param actorName The name of the actor
     * @return A list of movies that starred the actor from the award page
     * @throws IOException
     */
    public ArrayList<MovieInfo> question7(String awardType, String actorName) throws IOException{
        ArrayList<MovieInfo> movies = new ArrayList<MovieInfo>();
        
        //Access page, get all film name index for each table
        Document awardPage = getPageFrom(wikiPortalPage, awardType);
        String[] filmT = {"film"}, winnerT = {"winner"}, nomineeT = {"nominees"}, nominatedT = {"nominated"};
        ArrayList<ArrayList<Integer>> indeces = new ArrayList<ArrayList<Integer>>();
        indeces.add(getColumnIndeces(awardPage, filmT, 1));
        indeces.add(getColumnIndeces(awardPage, winnerT, 0));
        indeces.add(getColumnIndeces(awardPage, nomineeT, 0));
        indeces.add(getColumnIndeces(awardPage, nominatedT, 0));
        //If the film column is at the beginning it will be negative
        if(indeces.size() > 0 && indeces.get(0).get(0) < 0){
            for(ArrayList<Integer> i:indeces){
                for(int j = 0; j < i.size(); j++){
                    i.set(j, new Integer(0));
                }
            }
        }
        //There should not be both a winner and a film category for a page
        if(indeces.get(1).size() > 0)
            indeces.remove(0);
        
        //Access page, get all titles and links
        ArrayList<URL> filmURLs = new ArrayList<URL>();
        ArrayList<String> filmNames = new ArrayList<String>();
        
        Elements tables = awardPage.select("table.wikitable:not([style=text-align: center])");
        for(int index = 0; index < tables.size(); index++){
            for(ArrayList<Integer> ind:indeces){
                if(ind.size() > 0){
                    //Get list of just movies
                    Elements movieList = tables.get(index).select("td:eq(" + ind.get(index) + ")");
                    if(movieList.select("i").size() > 0)
                        movieList = movieList.select("i");
                    else if(movieList.select("b").size() > 0)
                        movieList = movieList.select("b");
                    movieList = movieList.select("a"); 
                    
                    //Generate list of URLs from each movie
                    for(Element movie:movieList){
                        filmURLs.add(new URL(movie.absUrl("abs:href")));
                        filmNames.add(movie.text());
                    }
                }
            }
        }
        
        //Check to see if each movie's starring has the actor included
        for(int index = 0; index < filmURLs.size(); index++){
            if(starringList(filmURLs.get(index), actorName) && hasMovie(movies, filmNames.get(index)) == null)
                movies.add(new MovieInfo(filmNames.get(index)));
        }
        
        return movies;
    }
    
    /**
     * Return the movies that were nominated for all four major awards
     * 
     * @return The list of movies nominated for all major awards
     * @throws IOException
     */
    public ArrayList<MovieInfo> question8() throws IOException{
        //An arraylist containing the four different arraylists for movie types
        ArrayList<MovieInfo> movies = new ArrayList<MovieInfo>();
        
        //Get the URLs for these pages
        ArrayList<URL> yearURLs = new ArrayList<URL>();
        ArrayList<Integer> years = new ArrayList<Integer>();
        Elements academyYears = wikiPortalPage.getElementsByAttributeValueContaining("title", "Academy Awards");
        for(Element year:academyYears){
            String text = year.text();
            //If the format is "YYYY" or "YYYY/YYYY"
            if(text.length() == 4 || text.length() == 9){
                years.add(new Integer(text.split("/")[0]));
                yearURLs.add(new URL(year.absUrl("abs:href")));
            }
        }
        
        //Use a hashmap to associate the encounter of a particular actor to the year
        String[] titles = {"best picture", "outstanding", "best director", "best actress", "best actor"};//The potential header titles
        HashMap<Integer, ArrayList<ArrayList<String>>> allEncounters = new HashMap<Integer, ArrayList<ArrayList<String>>>();
        for(int y = 0; y < yearURLs.size(); y++){
            //Get year page and get all headers
            Document yearPage = Jsoup.parse(yearURLs.get(y), TIME_OUT);
            Elements tableHeaders = yearPage.select("table.wikitable tr th");
            //Get a list of encounters
            ArrayList<ArrayList<String>> encounters = new ArrayList<ArrayList<String>>();
            Elements tableData = yearPage.select("table.wikitable tr td");
            for(int i = 0; i < tableHeaders.size(); i++){
                //If the header is a match, check the movies in that table
                String header = tableHeaders.get(i).text().toLowerCase();
                if(headerContains(header, titles)){
                    Elements films = tableData.get(i).select("i a");
                    encounters.add(checkMovies(films, movies, years.get(y).intValue()));
                }
            }
            allEncounters.put(years.get(y), encounters);
        }
        
        //Check each movie if they were encountered separately four times. 
        ArrayList<MovieInfo> output = new ArrayList<MovieInfo>();
        for(int i = 0; i < movies.size(); i++){
            int movieCount = 0;
            for(ArrayList<String> a:allEncounters.get(movies.get(i).getYear())){
                if(a.contains(movies.get(i).getName())){
                    movieCount++;
                }
            }
            //Would only be encountered four times
            if(movieCount == 4)
                output.add(movies.get(i));
        }
        
        return output;
    }
    
    /**
     * Return the actress' movies and the number of times they were nominated for best actress
     * 
     * @return The actress with her movies and number of nominations
     * @throws IOException 
     */
    public PersonInfo question9(String actress) throws IOException{
        //Get to the page and get columns
        PersonInfo onlyActress = new PersonInfo();
        Document bestActressPage = getPageFrom(wikiPortalPage, "Academy Award for Best Actress");
        Elements actresses = getColumnsNoRowspan(bestActressPage, 0);
        Elements movies = getColumnsNoRowspan(bestActressPage, 1);

        //For all the actresses in the page, find the actress in particular and find the number of nominations
        for(int i = 0; i < actresses.size(); i++)
            if(actresses.get(i).text().toLowerCase().contains(actress.toLowerCase())){
                onlyActress.setName(actresses.get(i).text());
                onlyActress.addMovie(movies.get(i).text());
                onlyActress.incrementNumNoms();
            }
        
        return onlyActress;
    }
    
    /**
     * Returns the best picture for a particular year
     * 
     * @return
     * @throws IOException 
     */
    public MovieInfo question10(int year) throws IOException{
        //Get the URL for the year
        URL awardURL = null;
        Elements academyYears = wikiPortalPage.getElementsByAttributeValueContaining("title", "Academy Awards");
        for(Element link:academyYears)
            if(link.text().startsWith(Integer.toString(year)))
                awardURL = new URL(link.absUrl("abs:href"));
        
        //Go to the award page 
        if(awardURL == null)
            return new MovieInfo("");
        Document awardPage = Jsoup.parse(awardURL, TIME_OUT);
        Elements infobox = awardPage.select("[class~=infobox] tr");
        String movieTitle = "";
        //Search the infobox for the best picture title and get the movie name
        for(Element e:infobox)
            if(e.select("th a").size() > 0 && e.select("th a").get(0).attr("title").toLowerCase().contains("best picture"))
                movieTitle = e.select("td").text();
        
        //Return a new movie object with the title and year
        MovieInfo movie = new MovieInfo(movieTitle);
        movie.setYear(year);
        return movie;
    }
}
