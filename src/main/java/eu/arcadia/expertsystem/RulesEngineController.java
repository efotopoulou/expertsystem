package eu.arcadia.expertsystem;

import eu.arcadia.expertsystem.transferobjects.MonitoringMessageTO;
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

        tobject.setNodeid("3d020fef-cd87");

        rulesEngineService.createFact(tobject);

        return true;

    }

    @RequestMapping(value = "/removeKnowledgebase", method = RequestMethod.POST)
    public boolean removeKnowledgebase(@RequestBody SGLifecycleMessageTO tobject) {

        log.info("Rest remove addKnowledgebase" + tobject.getGsgid());
        rulesEngineService.removeKnowledgebase(tobject.getGsgid(), tobject.getPolicyid());

        return true;

    }

}
