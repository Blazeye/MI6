/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import javax.swing.*;
import javax.swing.text.*;

/**
 *
 * @author Educom
 */
public class MainClass implements ActionListener
{
    Connection conn;
//    boolean[] blockedAcc = new boolean[965];
//    String secretCode = "For ThE Royal QUEEN";
    int agentNr;
    String agentCode;
    String agentPsswrd = "";
    JFrame frame = new JFrame("MI6 Input dialog");
    JTextField codeTBox, passTBox; 
    JLabel codeLabel, passLabel, error; 
    JButton enterBttn, cancelBttn;
     
    public MainClass(Connection Conn){
        this.conn = Conn;
    }
    
    public void Run()
    {
        
        JLabel greeting = new JLabel("Greetings agent, welcome to the MI6 login-screen", JLabel.CENTER);
        greeting.setBounds(0, 25, 350, 30);
        frame.add(greeting);

        codeLabel = new JLabel("Enter a service number from 0-965:", JLabel.LEFT);
        codeLabel.setBounds(50, 75, 350, 30);
        frame.add(codeLabel);
        codeTBox = createFilteredField();
        codeTBox.setBounds(50, 100, 230, 30);
        frame.add(codeTBox);

        passLabel = new JLabel("Enter the secret password:");
        passLabel.setBounds(50, 125, 350, 30);
        frame.add(passLabel);
        passTBox = new JTextField();
        passTBox.setBounds(50, 150, 230, 30);
        frame.add(passTBox);

        enterBttn = new JButton("Enter");
        enterBttn.setBounds(50, 200, 95, 30);
        enterBttn.addActionListener(this);
        frame.add(enterBttn);

        cancelBttn = new JButton("Cancel");
        cancelBttn.setBounds(185, 200, 95, 30);
        cancelBttn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        frame.add(cancelBttn);

        error = new JLabel();
        error.setForeground(Color.red);
        error.setBounds(0, 250, 350, 30);
        error.setHorizontalAlignment(JLabel.CENTER);
        frame.add(error);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(350, 350);
        frame.setLayout(null);
        frame.setVisible(true);
        
        
//        while(!agentPsswrd.equals(secretCode))
//        {
//        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try
        {
            error.setText("");
            agentCode = codeTBox.getText();
            agentPsswrd = passTBox.getText();

            agentNr = Integer.parseInt(agentCode.trim());
            
            checkCodes();
        }
        catch(NumberFormatException ex){
            System.out.println(ex);
        }
        //agentCode.matches("\\d+") || agentNr<0 || 965<agentNr
    }
    
    public JTextField createFilteredField(){
        // make a new JTextField
        JTextField field = new JTextField();
        // gets the model associated with the textfield editor  
        AbstractDocument document = (AbstractDocument) field.getDocument();
        final int maxCharacters = 3;
        // DocumentFilter defines how methods on the document, such as 
        // replace and insertString, filter the input of the user 
        document.setDocumentFilter(new DocumentFilter(){
            // you are overriding the replace method which is part of DocumentFilter
            // replace(int offset, int length, String string, AttributeSet attrs)
            // Deletes the region of text from offset to offset + length, and replaces it with text.
            // the DocumentFilter may callback into the FilterBypass multiple times, or for different regions, 
            // but it should not callback into the FilterBypass after returning from the remove or insertString method. 
            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length,
                    String str, AttributeSet a) throws BadLocationException {
                // gets the text from the textfield
                String text = fb.getDocument().getText(0,
                        fb.getDocument().getLength());
                // str is userinput which is added to the text in the textbox
                text += str;
                // checks if the length of the changed document is within maxCharacters
                // and if the text contains only digits
                if((fb.getDocument().getLength() + str.length() - length) <= maxCharacters
                        && text.matches("^[0-9]+$")){
                    // then, super invokes the overridden method (original replace)
                    // starting at offs and ending at offs + length, the document's text is replaced with str 
                    super.replace(fb, offset, length, str, a);
                } else {
                    // else you get a beep
                    Toolkit.getDefaultToolkit().beep();
                }
            }
            
            // same changes done when replacing text is done 
            // for inserting text into the JTextBox 
            @Override
            public void insertString(FilterBypass fb, int offset, String str,
                    AttributeSet a) throws BadLocationException {
                
                String text = fb.getDocument().getText(0,
                        fb.getDocument().getLength());
                text += str;
                if((fb.getDocument().getLength() + str.length()) <= maxCharacters
                        && text.matches("^[0-9]+$")) {
                    super.insertString(fb, offset, str, a);
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        });
        return field;
    }
    
    public void checkCodes(){
        boolean checkServiceCode = DatabaseManager.checkServiceNumber((short) agentNr, conn);
        LocalDateTime sqlDate = LocalDateTime.now();
        int checkTimedOutMin;
        int agentId;
        int timeOutMinLeft = 0;
        LocalDateTime endTermLicense;
        Agent agentData;
        LocalDateTime localLastAttempt = null;

        if(checkServiceCode){
            agentData = DatabaseManager.authenticateAgent((short) agentNr, agentPsswrd, conn);
            boolean authenticatedAgent = agentData.Active;
            
            agentId = DatabaseManager.getAgentId((short) agentNr, conn);
            checkTimedOutMin = DatabaseManager.checkTimedOut(agentId, conn);
            Timestamp checkLastLoginAttempt = DatabaseManager.getLastLoginAttempt(agentId, conn);
            if(checkLastLoginAttempt != null)
            {
                localLastAttempt = checkLastLoginAttempt.toLocalDateTime();
                Duration duration = Duration.between(localLastAttempt, sqlDate);
                timeOutMinLeft = (checkTimedOutMin - (int) Math.ceil(duration.getSeconds()/60));
            }

                
            if(authenticatedAgent){
                if(timeOutMinLeft <= 0){
                    DatabaseManager.addLoginAttempt(sqlDate, true, 0, agentId, conn);
                    List<LoginAttempt> loginList = DatabaseManager.getLoginAttempts(checkLastLoginAttempt, conn);

                    boolean hasLicense = agentData.LicenseToKill;

                    endTermLicense = agentData.LicenseEndTerm;
                    showLoggedInScreen(loginList, hasLicense, endTermLicense);
                }
                else{
                    DatabaseManager.addLoginAttempt(sqlDate, false, timeOutMinLeft, agentId, conn);
                    LocalDateTime timeOutEnd = localLastAttempt.plusMinutes((long)timeOutMinLeft);

                    error.setText("TIMEOUT ENDS AT: " + timeOutEnd.toString());
                }
            }
            else{
                if(timeOutMinLeft <= 0){ 
                    DatabaseManager.addLoginAttempt(sqlDate, false, 1, agentId, conn);
                    error.setText("ACCESS DENIED");
                }
                else{
                    DatabaseManager.addLoginAttempt(sqlDate, false, (2 * timeOutMinLeft), agentId, conn);
                    error.setText("ACCESS DENIED");
                }
            }
        }
        else{
            error.setText("INCORRECT SERVICE NUMBER");
        }
    }
    
    public void showLoggedInScreen(List<LoginAttempt> loginList, boolean hasLicense, LocalDateTime endTermLicense){
        Object[][] rows = new Object[loginList.size()][2];

        
        for (int i = 0; i < loginList.size(); i++)
        {
            LoginAttempt loginAttempt = loginList.get(i);
            rows[i][0] = loginAttempt.Date.toString().replace("T", " at ");
            rows[i][1] = (loginAttempt.ConfirmedAgent) ? "success" : "failure";
        }
        String[] cols = { "Date:", "Login attempt:" };
        
        JFrame f = new JFrame();
        f.setTitle("Login attempts");
        
        JTable table = new JTable(rows, cols);
        table.setBounds(30, 40, 200, 300);

        JScrollPane scrollpane = new JScrollPane(table);

        f.add(scrollpane);
        f.setSize(500, 200);
        f.setVisible(true);

        String licenseText = (hasLicense) ? "enabled. Ends: " + endTermLicense.toString() : "denied.";
        error.setText("License to kill: " + licenseText);
    }
    
}
