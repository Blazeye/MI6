
import java.time.LocalDateTime;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Educom
 */
public class Agent {

    protected boolean Active;
    protected boolean LicenseToKill;
    protected LocalDateTime LicenseEndTerm;
    
    /**
     * Stores various properties that are in the agents table in the database
     * @param active            whether the agent is active or out of commission
     * @param licenseToKill     whether the agent has been assigned a license to kill
     * @param licenseEndTerm    the date after which the license to kill is terminated
     */
    public Agent(boolean active, boolean licenseToKill, LocalDateTime licenseEndTerm){
        this.Active = active;
        this.LicenseToKill = licenseToKill;
        this.LicenseEndTerm = licenseEndTerm;
    }
        
}
