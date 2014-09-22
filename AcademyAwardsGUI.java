import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import java.io.IOException;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;


/**
 * Creates a GUI for the user to interface with the academyawardsinfofinder
 * 
 * @author David Watkins
 * @UNI djw2146
 */
public class AcademyAwardsGUI implements ActionListener {

    private JFrame frame;
    private JTextField q1Field;
    private JTextField q2Field;
    private JTextField q3Field;
    private JTextField q4Field;
    private JTextField q5Field;
    private JTextField q7AField;
    private JTextField q9Field;
    private JTextField q10Field;
    private JTextField q7BField;
    private MovieInfoFinder movies;
    private JTextArea outputArea;
    private ArrayList<JPanel> panels;
    

    /**
     * Create the application.
     */
    public AcademyAwardsGUI() {
        frame = new JFrame();
        frame.setName("Academy Award Info Finder");
        frame.setSize(new Dimension(700, 400));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new GridLayout(0, 2, 0, 0));
        
        try{
            movies = new MovieInfoFinder();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        
        JPanel questionButtonPanel = new JPanel();
        
        JScrollPane questionScroll = new JScrollPane();
        questionScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JPanel mainQuestionPanel = new JPanel(new GridLayout(20, 0));
        questionScroll.setViewportView(mainQuestionPanel);
        panels = new ArrayList<JPanel>();
        
        //The questions labels
        String[] questions = {"Question 1: List all movies nominated for the Best Picture award for which on of the Production companies was:",
                "Question 2: For the Best Original Screenplay award, list the writers for the movie that was nominaed/won titled:",
                "Question 3: List all actors nominated for a Best Leading Actor award whose role was playing a:",
                "Question 4: For the year ____, list all the actresses nominated for a best Leading Actress award along with the movie and their age that year.",
                "Question 5: List all directors (with the corresponding movies) that have been nominated for at least 4 Best Director awards",
                "Question 6: List the country (with the corresponding movies that has been nominated the mos tnumber of times for Best Regoeign Language Film award.",
                "Question 7: List all movies nominated for the _____ award that starred ____",
                "Question 8: List all movies that were nominated for Best Picture, Best Director, Best Leading Actor, and Best Leading Actress and indicate how many awards each movie won.",
                "Question 9: List the movies the best leading actress ____ was nominated for and how many she was nominated for",
                "Question 10: What movie won best picture for the year:"};
        
        //Add the questionPanel, then question description, then button to panel
        for(int i = 0; i < questions.length; i++){
            JPanel questionPanel = new JPanel(new FlowLayout());
            JTextArea questionDesc = new JTextArea(questions[i]);
            questionDesc.setEditable(false);
            questionDesc.setLineWrap(true);
            mainQuestionPanel.add(questionDesc, 2*i);
            mainQuestionPanel.add(questionPanel, 2*i+1);
            questionPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
            panels.add(questionPanel);
            
            JButton qButton = new JButton("Question " + (i+1));
            qButton.addActionListener(this);
            qButton.setHorizontalAlignment(SwingConstants.LEFT);
            questionPanel.add(qButton);
        }
        questionButtonPanel.setLayout(new GridLayout(0, 1, 0, 0));
        
        questionButtonPanel.add(questionScroll);
        frame.getContentPane().add(questionButtonPanel);
        
        JLabel lblNewLabel = new JLabel("Prod Comp:");
        panels.get(0).add(lblNewLabel);
        
        q1Field = new JTextField();
        panels.get(0).add(q1Field);
        q1Field.setColumns(10);
        
        JLabel lblMovieTitle = new JLabel("Movie Title:");
        panels.get(1).add(lblMovieTitle);
        
        q2Field = new JTextField();
        q2Field.setColumns(10);
        panels.get(1).add(q2Field);
        
        JLabel lblRole = new JLabel("Actor Role:");
        panels.get(2).add(lblRole);
        
        q3Field = new JTextField();
        q3Field.setColumns(10);
        panels.get(2).add(q3Field);
        
        JLabel lblMovieYear = new JLabel("Movie Year:");
        panels.get(3).add(lblMovieYear);
        
        q4Field = new JTextField();
        q4Field.setColumns(10);
        panels.get(3).add(q4Field);

        JLabel lblNumAwards = new JLabel("Awards:");
        panels.get(4).add(lblNumAwards);
        
        q5Field = new JTextField();
        q5Field.setColumns(10);
        panels.get(4).add(q5Field);

        JLabel lblCountryMostNomd = new JLabel("Country Most Nom'd for Foreign:");
        panels.get(5).add(lblCountryMostNomd);
        
        JLabel lblAward = new JLabel("Award:");
        panels.get(6).add(lblAward);
        
        q7AField = new JTextField();
        q7AField.setColumns(5);
        panels.get(6).add(q7AField);
        
        JLabel lblNewLabel_1 = new JLabel("Starring:");
        panels.get(6).add(lblNewLabel_1);
        
        q7BField = new JTextField();
        panels.get(6).add(q7BField);
        q7BField.setColumns(5);

        JLabel lblNomdForAll = new JLabel("Nom'd for all four best:");
        panels.get(7).add(lblNomdForAll);
        
        JLabel lblActress = new JLabel("Actress:");
        panels.get(8).add(lblActress);
        
        q9Field = new JTextField();
        q9Field.setColumns(10);
        panels.get(8).add(q9Field);
        
        JLabel lblYearOfAward = new JLabel("Year of Award:");
        panels.get(9).add(lblYearOfAward);
        
        q10Field = new JTextField();
        q10Field.setColumns(10);
        panels.get(9).add(q10Field);
        
        JPanel outputPanel = new JPanel();
        frame.getContentPane().add(outputPanel);
        outputPanel.setLayout(new GridLayout(1, 0, 0, 0));
        
        JScrollPane outputScroll = new JScrollPane();
        outputPanel.add(outputScroll);
        
        //Where the output of the window is displayed
        outputArea = new JTextArea();
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        outputArea.setEditable(false);
        outputScroll.setViewportView(outputArea);        
    }
    
    /**
     * Set the frame to visible
     */
    public void setVisible(){
        frame.setVisible(true);
    }

    /** 
     * Handle buttons presses from the user for particular questions
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        JButton b = (JButton)e.getSource();
        String output = "";
        //For each type of question, append to output
        try{
            if(b.getText().equals("Question 1")){
                String prodCompany = q1Field.getText();
                ArrayList<MovieInfo> info = movies.question1(prodCompany);
                for(MovieInfo movie:info){
                    output += movie.toString();
                }
            }
            else if(b.getText().equals("Question 2")){
                String movieTitle = q2Field.getText();
                ArrayList<PersonInfo> writers = movies.question2(movieTitle);
                for(PersonInfo person:writers)
                    output+=person.toString();
            }
            else if(b.getText().equals("Question 3")){
                String role = q3Field.getText();                  
                ArrayList<PersonInfo> actors = movies.question3(role);
                for(PersonInfo person:actors)
                    output+=person.toString();
            }
            else if(b.getText().equals("Question 4")){
                int year = 0;
                try{
                    year = Integer.parseInt(q4Field.getText());                  
                }
                catch(NumberFormatException exc){
                    outputArea.setText("Year must be formatted properly");
                }
                ArrayList<PersonInfo> actresses = movies.question4(year);
                for(PersonInfo person:actresses)
                    output+=person.toString();
            }
            else if(b.getText().equals("Question 5")){
                int awardCount = 0;
                try{
                    awardCount = Integer.parseInt(q5Field.getText());                  
                }
                catch(NumberFormatException exc){
                    outputArea.setText("Number must be formatted properly");
                }
                ArrayList<PersonInfo> directors = movies.question5(awardCount);
                for(PersonInfo person:directors)
                    output+=person.toString();
            }
            else if(b.getText().equals("Question 6")){
                PersonInfo country = movies.question6();
                output+=country.toString();
            }
            else if(b.getText().equals("Question 7")){
                String awardType = q7AField.getText();
                String starringActor = q7BField.getText();
                ArrayList<MovieInfo> films = movies.question7(awardType, starringActor);
                for(MovieInfo movie:films)
                    output+=movie.toString();
            }
            else if(b.getText().equals("Question 8")){
                ArrayList<MovieInfo> films = movies.question8();
                for(MovieInfo movie:films)
                    output+=movie.toString() + "\n";
            }
            else if(b.getText().equals("Question 9")){
                String actress = q9Field.getText();
                PersonInfo act = movies.question9(actress);
                output += act.toString();
            }
            else if(b.getText().equals("Question 10")){
                int year = 0;
                MovieInfo film = null;
                try{//Need year to be formatted properly
                    year = Integer.parseInt(q10Field.getText());
                    film = movies.question10(year);
                }
                catch(NumberFormatException exc){
                    outputArea.setText("Year must be formatted properly");
                }
                output+=film.toString();
            }
        }
        catch(IOException exc){
            //If the web page was not accessed properly
            exc.printStackTrace();
        }
        
        //User input was invalid
        if(output.equals(""))
            output += "Your input did not return any results";
        
        outputArea.setText(output);
    }
}
