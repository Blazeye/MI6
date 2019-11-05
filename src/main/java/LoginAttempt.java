
import java.time.LocalDateTime;

/**
 *
 * @author Educom
 */
public class LoginAttempt {
    protected LocalDateTime Date;
    protected boolean ConfirmedAgent;
    
    public LoginAttempt(LocalDateTime date, boolean confirmedAgent){
        this.Date = date;
        this.ConfirmedAgent = confirmedAgent;
    }
}
