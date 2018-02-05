package eu.arcadia.expertsystem;

import eu.arcadia.repository.mongo.transferobjects.MonitoringMessageTO;
import eu.arcadia.repository.mongo.transferobjects.SGLifecycleMessageTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Eleni Fotopoulou <efotopoulou@ubitech.eu>
 */
@RestController
@RequestMapping("/expertsystem")
public class RulesEngineController {
    
    private static final Logger log = LoggerFactory.getLogger(RulesEngineController.class);
    
    @Autowired
    RulesEngineService rulesEngineService;
    
    @RequestMapping(value = "/newMonitoringMessage", method = RequestMethod.POST)
    public boolean newMonitoringMessage(@RequestBody MonitoringMessageTO tobject) {
        
        //tobject.setGsgid("5a0ae58d4f0c038e6475d049");
        tobject.setNodeid("3d020fef-cd87");
        
        rulesEngineService.createFact(tobject);
        
        return true;

//
//        return new ArcadiaRestResponse(BasicResponseCode.SUCCESS, Message.CREATED, Optional.empty());
    }
    

//    @RequestMapping(value = "/addKnowledgebase", method = RequestMethod.GET)
//    public boolean addKnowledgebase(@RequestParam(required = true) String ggid, @RequestParam(required = true) String policyid) {
//
//        log.info("Rest create Knowledgebase" + ggid + " with policyid " + policyid);
//        rulesEngineService.addNewKnowledgebase(ggid, policyid);
//
//        return true;
//
//    }
    @RequestMapping(value = "/removeKnowledgebase", method = RequestMethod.POST)
    public boolean removeKnowledgebase(@RequestBody SGLifecycleMessageTO tobject) {
        
        log.info("Rest remove addKnowledgebase" + tobject.getGsgid());
        rulesEngineService.removeKnowledgebase(tobject.getGsgid(), tobject.getPolicyid());
        
        return true;
        
    }

//    @RequestMapping(value = "/removeKnowledgebase", method = RequestMethod.GET)
//    public boolean removeKnowledgebase(@RequestParam(required = true) String ggid, @RequestParam(required = true) String policyid) {
//
//        log.info("Rest remove Knowledgebase" + ggid + " with policyid " + policyid);
//        rulesEngineService.removeKnowledgebase(ggid, policyid);
//
//        return true;
//
//    }
}
