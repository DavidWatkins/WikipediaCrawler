
/**
 * A movie info object to contain data pertaining to a particular movie
 * 
 * @author David Watkins
 * @Uni djw2146
 */
public class MovieInfo {
    private String movieName;
    private int numAwards;
    private int numNoms;
    private int year;
    private String prodComp;
    /**
     * Create a movie object with the given name
     * @param movieName
     */
    public MovieInfo(String movieName){
        this.movieName = movieName;
        numAwards = 0;
        numNoms = 0;
        setYear(0);
        setProdComp("");
    }
    /**
     * @return the name of the movie
     */
    public String getName(){
        return movieName;
    }
    /**
     * @return the award count won
     */
    public int getAwardCount(){
        return numAwards;
    }
    /**
     * increments the award count by one
     */
    public void incrementAwardCount(){
        numAwards++;
    }
    /**
     * @return the number of nominations for the movie
     */
    public int getNumNoms() {
        return numNoms;
    }
    /**
     * Increments number of nominations for the movie
     */
    public void incrementNumNoms() {
        numNoms++;
    }
    /**
     * @return the year of the movie
     */
    public int getYear() {
        return year;
    }
    /**
     * @param year new year of movie
     */
    public void setYear(int year) {
        this.year = year;
    }
    /**
     * @param prodComp new production company
     */
    public void setProdComp(String prodComp) {
        this.prodComp = prodComp;
    }
    
    /** 
     * Handles output of a movie object
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString(){
        String output = "";
        if(!movieName.equals(""))
            output += "Movie Title: " + movieName + "\n";
        if(!prodComp.equals(""))
            output += "Production Company: " + prodComp + "\n";
        if(numNoms > 0){
            output += "Number of Nominations: " + numNoms + "\n";
            output += "Number of Awards Won: " + numAwards + "\n";
        }
        if(year > 0)
            output += "Year: " + year + "\n";
        return output;
    }
}
