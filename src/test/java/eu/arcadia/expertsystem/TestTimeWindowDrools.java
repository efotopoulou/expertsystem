/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.arcadia.expertsystem;

import eu.arcadia.expertsystem.facts.MonitoredComponent;
import eu.arcadia.expertsystem.rules.generation.KieUtil;
import eu.arcadia.expertsystem.rules.generation.RuleUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import org.drools.compiler.lang.DrlDumper;
import org.drools.compiler.lang.api.CEDescrBuilder;
import org.drools.compiler.lang.api.DescrFactory;
import org.drools.compiler.lang.api.PackageDescrBuilder;
import org.drools.compiler.lang.api.RuleDescrBuilder;
import org.drools.compiler.lang.descr.AndDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.json.JSONObject;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message.Level;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.internal.utils.ClassLoaderUtil;
import org.kie.internal.utils.CompositeClassLoader;

/**
 *
 * @author Eleni Fotopoulou <efotopoulou@ubitech.eu>
 */
public class TestTimeWindowDrools {

    /*
     The current test creates a programmaticaly configured KieContainer with time window sessions
     */
    @Ignore
    @Test
    public void testtimewindowdrools() {

        try {
            KieUtil kieUtil = new KieUtil();

            KieServices kieServices = KieServices.Factory.get();
            KieModuleModel kieModuleModel = kieServices.newKieModuleModel();
            KieFileSystem kieFileSystem = kieServices.newKieFileSystem();

            KieBaseModel kieBaseModel1 = kieModuleModel.newKieBaseModel("ArcadiaGSGKnowledgeBase_gsg").setEventProcessingMode(EventProcessingOption.STREAM);

            kieBaseModel1.addPackage("rules.gsg");

            String factSessionName = "RulesEngineSession_gsg";
            kieBaseModel1.newKieSessionModel(factSessionName).setClockType(ClockTypeOption.get("realtime"));

            ReleaseId releaseId2 = kieServices.newReleaseId("eu.arcadia", "expert-system", "0.1");

            KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);

            kieFileSystem.generateAndWritePomXML(releaseId2);

            kieFileSystem.writeKModuleXML(kieModuleModel.toXML());
            System.out.println("kieModuleModel--ToXML\n" + kieModuleModel.toXML());

            CompositeClassLoader classLoader = ClassLoaderUtil.getClassLoader(new ClassLoader[]{Thread.currentThread().getContextClassLoader()}, getClass(), false);
            File file = new File(classLoader.getResource("rules/gsg/gsg.drl").getFile());

            Resource resource = kieServices.getResources().newFileSystemResource(file).setResourceType(ResourceType.DRL);
            kieFileSystem.write("src/main/resources/rules/gsg/gsg.drl", resource);

            kieBuilder.buildAll();

            if (kieBuilder.getResults()
                    .hasMessages(Level.ERROR)) {
                throw new RuntimeException("Build Errors:\n" + kieBuilder.getResults().toString());
            }

            KieContainer kieContainer = kieServices.newKieContainer(releaseId2);

            KieSession kieSession = kieContainer.newKieSession(factSessionName);

            kieUtil.fireKieSession(kieSession, factSessionName);

            KieSession kieSession1 = (KieSession) kieUtil.seeThreadMap().get(factSessionName);
            System.out.println("thread identifier" + kieSession1.getIdentifier());

            org.kie.api.runtime.rule.EntryPoint monitoringStream = kieSession.getEntryPoint("MonitoringStream");

            MonitoredComponent monitoredComponent = new MonitoredComponent("FFFFFE1", "memory_total", 90, "gsg");

            monitoringStream.insert(monitoredComponent);

            System.out.println("start thread sleep");
            Thread.sleep(15000L);
        } catch (InterruptedException ex) {
            Logger.getLogger(TestTimeWindowDrools.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

    }

    @Ignore
    @Test
    public void TimeWindowDRLsCreation() {

        System.out.println("---------------Simple time window DRL------------------");
        List<String> myParams = new LinkedList<String>();
        myParams.add("5s");

        //1. Get JSONs from mongo
        PackageDescrBuilder packageDescrBuilder = DescrFactory.newPackage();
        packageDescrBuilder
                .name("eu.arcadia.expertsystem.rules")
                .newImport().target("eu.arcadia.expertsystem.facts.*").end()
                .newDeclare().type().name("MonitoredComponent").newAnnotation("role").value("event").end()
                .newAnnotation("expires").value("30m").end().end()
                .newRule()
                .name("ScaleComponent_gsg")
                .lhs()
                .pattern().id("$tot", true).type(Double.class.getName()).constraint("$tot > 50").from()
                .accumulate().source().pattern("MonitoredComponent").id("$m", true).constraint("name==\"FFFFFE1\"")
                .from().entryPoint("MonitoringStream").behavior().type("window", "time").parameters(myParams).end().end().end()
                .function("sum", null, false, "$m.getValue()")
                .end()
                .end()
                .end()
                .rhs("System.out.println( \"Dinamic Added rule\");")
                .rhs("System.out.println(\"footesttt\"+$tot);")
                .end();

        String rule = new DrlDumper().dump(packageDescrBuilder.getDescr());

        System.out.println(rule);

    }

    @Ignore
    @Test
    public void TimeWindowDRLsCreation1() {

        System.out.println("---------------Medium Complexity time window DRL------------------");
        List<String> myParams = new LinkedList<String>();
        myParams.add("5s");

        //1. Get JSONs from mongo
        PackageDescrBuilder packageDescrBuilder = DescrFactory.newPackage();
        packageDescrBuilder
                .name("eu.arcadia.expertsystem.rules")
                .newImport().target("eu.arcadia.expertsystem.facts.*").end()
                .newDeclare().type().name("MonitoredComponent").newAnnotation("role").value("event").end()
                .newAnnotation("expires").value("30m").end().end()
                .newRule()
                .name("ScaleComponent_gsg")
                .lhs().and()
                .pattern().id("$tot", true).type(Double.class.getName()).constraint("$tot > 50").from()
                .accumulate().source().pattern("MonitoredComponent").id("$m", true).constraint("name==\"FFFFFE1\"")
                .from().entryPoint("MonitoringStream").behavior().type("window", "time").parameters(myParams).end().end().end()
                .function("sum", null, false, "$m.getValue()")
                .end()
                .end()
                .or()
                .pattern().id("$tot", true).type(Double.class.getName()).constraint("$tot > 50").from()
                .accumulate().source().pattern("MonitoredComponent").id("$m", true).constraint("name==\"FFFFFE2\"")
                .from().entryPoint("MonitoringStream").behavior().type("window", "time").parameters(myParams).end().end().end()
                .function("sum", null, false, "$m.getValue()")
                .end()
                .end()
                .pattern().id("$tot", true).type(Double.class.getName()).constraint("$tot > 50").from()
                .accumulate().source().pattern("MonitoredComponent").id("$m", true).constraint("name==\"FFFFFE2\"")
                .from().entryPoint("MonitoringStream").behavior().type("window", "time").parameters(myParams).end().end().end()
                .function("sum", null, false, "$m.getValue()")
                .end()
                .end()
                .end()
                .end()
                .end()
                .rhs("System.out.println( \"Dinamic Added rule\");")
                .rhs("System.out.println(\"footesttt\"+$tot);")
                .end();

        String rule = new DrlDumper().dump(packageDescrBuilder.getDescr());

        rule = rule.replace("|", "over");

        System.out.println(rule);

    }

    @Ignore
    @Test
    public void testTopLevelAccumulate() throws InstantiationException, IllegalAccessException {
        System.out.println("---------------BBBBBBBBBBBBBBBBBBBBBBBBB------------------");
        List<String> myParams = new LinkedList<String>();
        myParams.add("5s");

        //1. Get JSONs from mongo
        PackageDescrBuilder packageDescrBuilder = DescrFactory.newPackage();
        packageDescrBuilder
                .name("eu.arcadia.expertsystem.rules")
                .newImport().target("eu.arcadia.expertsystem.facts.*").end()
                .newDeclare().type().name("MonitoredComponent").newAnnotation("role").value("event").end()
                .newAnnotation("expires").value("30m").end().end()
                .newRule()
                .name("ScaleComponent_gsg")
                .lhs()
                //.pattern().id("$m", false).type("MonitoredComponent").constraint("(name==\"FFFFFE1\"  && metric==\"memory_total\" && value > 80 )|| (name==\"FFFFFE1\" && metric==\"MetricX\" && value == 3)").end()
                .pattern().id("$tot", true).type(Double.class.getName()).constraint("$tot > 50").from()
                .accumulate().source().pattern("MonitoredComponent").id("$m", true).constraint("name==\"FFFFFE1\"")
                .from().entryPoint("MonitoringStream").behavior().type("window", "time").parameters(myParams).end().end().end()
                //.function("sum", "$tot", true, "$m.getValue()")
                .function("sum", null, false, "$m.getValue()")
                //.constraint("$tot > 50")
                .end()
                .end()
                .end()
                .rhs("System.out.println( \"Dinamic Added rule\");")
                .rhs("System.out.println(\"footesttt\"+$tot);")
                .end();

        String rule = new DrlDumper().dump(packageDescrBuilder.getDescr());

        System.out.println(rule);

    }

    @Ignore
    @Test
    public void testNestedTopLevelAccumulate() throws InstantiationException, IllegalAccessException {
        System.out.println("---------------NESTED ACCUMULATE------------------");

        JSONObject jsonrulewhen1 = new JSONObject("{\n"
                + "  \"condition\": \"OR\",\n"
                + "  \"rules\": [\n"
                + "    {\n"
                + "      \"id\": \"rateSlider.FFFFFE1.memory_total\",\n"
                + "      \"field\": \"rateSlider.FFFFFE1.memory_total\",\n"
                + "      \"type\": \"integer\",\n"
                + "      \"input\": \"text\",\n"
                + "      \"operator\": \"equal\",\n"
                + "      \"value\": 47\n"
                + "    },\n"
                + "    {\n"
                + "      \"condition\": \"AND\",\n"
                + "      \"rules\": [\n"
                + "        {\n"
                + "          \"id\": \"rateSlider.FFFFFE1.processor_count\",\n"
                + "          \"field\": \"rateSlider.FFFFFE1.processor_count\",\n"
                + "          \"type\": \"integer\",\n"
                + "          \"input\": \"text\",\n"
                + "          \"operator\": \"equal\",\n"
                + "          \"value\": 15\n"
                + "        },\n"
                + "        {\n"
                + "          \"id\": \"rateSlider.FFFFFE2.memory_free\",\n"
                + "          \"field\": \"rateSlider.FFFFFE2.memory_free\",\n"
                + "          \"type\": \"integer\",\n"
                + "          \"input\": \"text\",\n"
                + "          \"operator\": \"less\",\n"
                + "          \"value\": 82\n"
                + "        }\n"
                + "      ]\n"
                + "    }\n"
                + "  ]\n"
                + "}");

        JSONObject jsonrulewhen2 = new JSONObject("{\n"
                + "	\"condition\": \"AND\",\n"
                + "	\"rules\": [{\n"
                + "		\"id\": \"FFFFFE1.MetricX\",\n"
                + "		\"field\": \"FFFFFE1.MetricX\",\n"
                + "		\"type\": \"integer\",\n"
                + "		\"input\": \"text\",\n"
                + "		\"operator\": \"equal\",\n"
                + "		\"value\": \"2\"\n"
                + "	}, {\n"
                + "		\"id\": \"rateSlider.FFFFFE2.net_packet_out\",\n"
                + "		\"field\": \"rateSlider.FFFFFE2.net_packet_out\",\n"
                + "		\"type\": \"integer\",\n"
                + "		\"input\": \"text\",\n"
                + "		\"operator\": \"greater_or_equal\",\n"
                + "		\"value\": 50\n"
                + "	}]\n"
                + "}");

        JSONObject jsonrulewhen = new JSONObject("\n"
                + "        {\n"
                + "  \"condition\": \"AND\",\n"
                + "  \"rules\": [\n"
                + "    {\n"
                + "      \"id\": \"rateSlider.245b03e3-13f4-4559-ad14-0c49aad62e66.memory_free\",\n"
                + "      \"field\": \"rateSlider.245b03e3-13f4-4559-ad14-0c49aad62e66.memory_free\",\n"
                + "      \"type\": \"integer\",\n"
                + "      \"input\": \"text\",\n"
                + "      \"operator\": \"greater\",\n"
                + "      \"value\": 28\n"
                + "    },\n"
                + "    {\n"
                + "      \"condition\": \"OR\",\n"
                + "      \"rules\": [\n"
                + "        {\n"
                + "          \"id\": \"rateSlider.d0930833-3e9a-4632-a664-fd68183d5afb.cpu_usage\",\n"
                + "          \"field\": \"rateSlider.d0930833-3e9a-4632-a664-fd68183d5afb.cpu_usage\",\n"
                + "          \"type\": \"integer\",\n"
                + "          \"input\": \"text\",\n"
                + "          \"operator\": \"equal\",\n"
                + "          \"value\": 64\n"
                + "        },\n"
                + "        {\n"
                + "          \"id\": \"rateSlider.d0930833-3e9a-4632-a664-fd68183d5afb.disk_usage\",\n"
                + "          \"field\": \"rateSlider.d0930833-3e9a-4632-a664-fd68183d5afb.disk_usage\",\n"
                + "          \"type\": \"integer\",\n"
                + "          \"input\": \"text\",\n"
                + "          \"operator\": \"greater\",\n"
                + "          \"value\": 29\n"
                + "        }\n"
                + "      ]\n"
                + "    }\n"
                + "  ]\n"
                + "}");

        PackageDescrBuilder packageDescrBuilder = DescrFactory.newPackage();
        RuleDescrBuilder droolrule = packageDescrBuilder
                .name("eu.arcadia.expertsystem.rules")
                .newImport().target("eu.arcadia.expertsystem.facts.*").end()
                .newDeclare().type().name("MonitoredComponent").newAnnotation("role").value("event").end()
                .newAnnotation("expires").value("30m").end().end()
                .newRule()
                .name("ScaleComponent_gsg");

        CEDescrBuilder<RuleDescrBuilder, AndDescr> when = droolrule.lhs();

        when = RuleUtil.convertToRule(when, jsonrulewhen, "AND","70s");

        droolrule.rhs("System.out.println( \"Dinamic Added rule\");")
                .rhs("System.out.println(\"footesttt\"+$tot);")
                .end();

        String rule = new DrlDumper().dump(packageDescrBuilder.getDescr());
        rule = rule.replace("|", "over");
        System.out.println(rule);

//        List<String> myParams = new LinkedList<String>();
//        myParams.add("5s");
//                .lhs()
//                .pattern().id("$tot", true).type(Double.class.getName()).constraint("$tot > 50").from()
//                .accumulate().source().pattern("MonitoredComponent").id("$m", true).constraint("name==\"FFFFFE1\"")
//                .from().entryPoint("MonitoringStream").behavior().type("window", "time").parameters(myParams).end().end().end()
//                .function("sum", null, false, "$m.getValue()")
//                .end()
//                .end()
//                .end()
    }

}
