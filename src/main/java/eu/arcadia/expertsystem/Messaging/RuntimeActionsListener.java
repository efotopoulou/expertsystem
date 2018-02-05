package eu.arcadia.expertsystem.Messaging;

import eu.arcadia.api.repository.IActivityManagement;
import eu.arcadia.api.repository.IIaaSManagementService;
import eu.arcadia.expertsystem.facts.RuleActionType;
import eu.arcadia.expertsystem.RulesEngineApp;
import eu.arcadia.repository.mongo.domain.Activity;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import eu.arcadia.repository.mongo.transferobjects.ExpertSystemMessageTO;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.web.client.RestTemplate;

@Component
public class RuntimeActionsListener {
    
    @Autowired
    IActivityManagement activityManagement;
    
    @Autowired
    IIaaSManagementService iIaaSManagementService;
    
    @Value("${iaasregistration.url}")
    private String IAAS_REGISTRATION_URL;
    
    @Value("${transcodingserver.url}")
    private String TRANSCODING_SERVER_URL;

    //@Autowired
    //OpenStackAdapter openStackAdapter;
    /**
     * Instead of just using the annotation here, we can configure a
     * DefaultMessageListenerContainer which has more features.
     */
    private static final Logger logger = Logger.getLogger(RuntimeActionsListener.class.getName());
    
    @JmsListener(destination = RulesEngineApp.RUNTIME_ACTIONS_TOPIC, containerFactory = "myJmsContainerFactory",
            selector = "context = 'runtime_action'" /*, concurrency="5-10"*/
    /*, subscription="durable"*/
    )
    public void expertSystemMessageReceived(ExpertSystemMessage message) {

        //logger.log(Level.INFO, "Receive to RUNTIME_ACTIONS_TOPIC action for ggid{0} and nodeid {1} with proposed action type {2} and value {3}", new Object[]{message.getGgid(), message.getNodeid(), message.getRuleActionType(), message.getValue()});
        logger.log(Level.INFO, "ExpertSystemMessageTO   is like this " + message.toString());
        
        String activityDescription = "";
        if (message.getRuleActionType().toString() == RuleActionType.ARCADIA_VIRTUAL_FUNCTION.toString()) {
            activityDescription = "The component with nodeid " + message.getNodeid() + " should do " + message.getAction() + " by " + message.getValue() + " . Msg from Grounded graph ";
            
        } else if (message.getRuleActionType().toString() == RuleActionType.IAAS_MANAGEMENT.toString()) {
            activityDescription = "The component with nodeid " + message.getNodeid() + " should do " + message.getAction() + " by " + message.getValue() + " . Msg from Grounded graph ";
            
        } else if (message.getRuleActionType().toString() == RuleActionType.ALERT_MESSAGE.toString()) {
            
            activityDescription = "Alert: \"" + message.getValue() + "\" . Msg from Grounded graph ";
            
        } else if (message.getRuleActionType().toString() == RuleActionType.COMPONENT_CONFIGURATION.toString()) {
            
            activityDescription = "The configuration parameter \"" + message.getAction() + "\" of the component with nodeid " + message.getNodeid() + " should be updated to value \" " + message.getValue() + "\" . Msg from Grounded graph ";
            
        } else if (message.getRuleActionType().toString() == RuleActionType.COMPONENT_LIFECYCLE_MANAGEMENT.toString()) {
            activityDescription = "The component with nodeid " + message.getNodeid() + " has to " + message.getValue() + " . Msg from Grounded graph ";
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = null;
            if (message.getAction().contains("start")) {
                response = restTemplate.getForEntity(TRANSCODING_SERVER_URL + "/infrastructure/start/" + message.getValue(), String.class);
            } else if (message.getAction().contains("start")) {
                response = restTemplate.getForEntity(TRANSCODING_SERVER_URL + "/infrastructure/stop/" + message.getValue(), String.class);
            }
            
            logger.info("invocation of transconding servise with response " + response);
            
        }
        
        Activity activity = (Activity) activityManagement.findActivity(message.getNodeid(), message.getGname(), activityDescription, "POLICY");
        
        if (activity == null) {
            
            activityManagement.addActivity(message.getUsername(), message.getNodeid(), message.getGname(), activityDescription, "POLICY");
            
            if (message.getRuleActionType().toString() == RuleActionType.IAAS_MANAGEMENT.toString()) {
                ExpertSystemMessageTO messageTO = new ExpertSystemMessageTO();
                messageTO.setAction(message.getAction());
                messageTO.setCid(message.getCid());
                messageTO.setConfParameter(message.getConfParameter());
                messageTO.setGgid(message.getGgid());
                messageTO.setGname(message.getGname());
                messageTO.setNodeid(message.getNodeid());
                messageTO.setRuleActionType(message.getRuleActionType().toString());
                messageTO.setUsername(message.getUsername());
                messageTO.setValue(message.getValue());
                
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<String> response = restTemplate.postForEntity(IAAS_REGISTRATION_URL + "/enforceIaasManagementPolicy", messageTO, String.class);
                //openStackAdapter.scaleUP(message);
                //String action = iIaaSManagementService.spaleUp(message);
                //action stores all actions details
                //System.out.println(action);
            }
        } else {
            
            java.util.Date datenow = new java.util.Date();
            
            System.out.println(datenow);
            
            Date activitydate = activity.getActivitydate();
            
            long diff = datenow.getTime() - activitydate.getTime();
            long diffMinutes = TimeUnit.MILLISECONDS.toMinutes(diff);
            //long diffMinutes = diff / (60 * 1000);

            if (diffMinutes > 15) {
                
                activityManagement.addActivity(message.getUsername(), message.getNodeid(), message.getGname(), activityDescription, "POLICY");
                
                if (message.getRuleActionType().toString() == RuleActionType.IAAS_MANAGEMENT.toString()) {
                    ExpertSystemMessageTO messageTO = new ExpertSystemMessageTO();
                    messageTO.setAction(message.getAction());
                    messageTO.setCid(message.getCid());
                    messageTO.setConfParameter(message.getConfParameter());
                    messageTO.setGgid(message.getGgid());
                    messageTO.setGname(message.getGname());
                    messageTO.setNodeid(message.getNodeid());
                    messageTO.setRuleActionType(message.getRuleActionType().toString());
                    messageTO.setUsername(message.getUsername());
                    messageTO.setValue(message.getValue());
                    
                    RestTemplate restTemplate = new RestTemplate();
                    ResponseEntity<String> response = restTemplate.postForEntity(IAAS_REGISTRATION_URL + "/enforceIaasManagementPolicy", messageTO, String.class);
                    //openStackAdapter.scaleUP(message);
                    //String action = iIaaSManagementService.spaleUp(message);
                    //action stores all actions details
                    //System.out.println(action);
                }
                
            }
        }
        
    }
    
}
