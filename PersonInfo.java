import java.util.ArrayList;


/**
 * Contains information about a particular actor or country in regards to the academy awards
 * Having a different constructor for each variable was difficult to setters and getters were created
 * 
 * @author David Watkins
 * @UNI djw2146
 */
public class PersonInfo {

    private int age;
    private String name;
    private String role;
    private ArrayList<String> movies;
    private int numNoms;

    /**
     * Initialize a PersonInfo Object
     */
    public PersonInfo(){
        age = 0;
        name = "";
        movies = new ArrayList<String>();
        numNoms = 0;
        setRole("");
    }

    /**
     * @param age age of person
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * @return name of person
     */
    public String getName() {
        return name;
    }

    /**
     * @param name new name of person
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return list of movies associated with person
     */
    public ArrayList<String> getMovies() {
        return movies;
    }

    /**
     * @param movie movie to be added to list
     */
    public void addMovie(String movie) {
        movies.add(movie);
    }
    
    /**
     * Handles the display of the PersonInfo
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString(){
        String output = "";
        if(!name.equals(""))
            output += "Name: " + name + "\n";
        if(!role.equals(""))
            output+= "Role: " + role + "\n";
        if(age != 0)
            output += "Age: " + age + "\n";
        if(movies.size() > 0){
            output += "Related Films: \n";
            for(String movie : movies)
                output += "Film: " + movie + "\n";
        }
        if(numNoms > 0)
            output += "Number of nominations: " + numNoms + "\n";
        return output;
    }
   
    /**
     * @return number of nominations for person
     */
    public int getNumNoms() {
        return numNoms;
    }
    
    /**
     * Increment the number of nominations by one
     */
    public void incrementNumNoms() {
        numNoms++;
    }

    /**
     * @param role new role of person for a particular movie
     */
    public void setRole(String role) {
        this.role = role;
    }
}
