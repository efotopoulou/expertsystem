package eu.arcadia.expertsystem;

import eu.arcadia.expertsystem.RulesEngineApp;
import eu.arcadia.expertsystem.RulesEngineService;
import eu.arcadia.expertsystem.rules.generation.RuleUtil;
import java.io.FileOutputStream;
import java.io.IOException;
import org.drools.compiler.lang.DrlDumper;
import org.drools.compiler.lang.api.CEDescrBuilder;
import org.drools.compiler.lang.api.DescrFactory;
import org.drools.compiler.lang.api.PackageDescrBuilder;
import org.drools.compiler.lang.api.RuleDescrBuilder;
import org.drools.compiler.lang.descr.AndDescr;
import org.jboss.logging.Logger;
import org.json.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author Eleni Fotopoulou <efotopoulou@ubitech.eu>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {RulesEngineApp.class})
public class R2RUnitTest {

    private static final Logger logger = Logger.getLogger(R2RUnitTest.class.getName());

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }
    
    @Ignore
    @Test
    @SuppressWarnings("Duplicates")
    public void testTranslationRuleToDRL() {

        JSONObject jsonrulewhen1 = new JSONObject("{\n"
                + "  \"condition\": \"AND\",\n"
                + "  \"rules\": [\n"
                + "    {\n"
                + "      \"id\": \"FFFFFE1.MetricX\",\n"
                + "      \"field\": \"FFFFFE1.MetricX\",\n"
                + "      \"type\": \"integer\",\n"
                + "      \"input\": \"text\",\n"
                + "      \"operator\": \"equal\",\n"
                + "      \"value\": \"2\"\n"
                + "    },\n"
                + "    {\n"
                + "      \"id\": \"rateSlider.FFFFFE2.net_packet_out\",\n"
                + "      \"field\": \"rateSlider.FFFFFE2.net_packet_out\",\n"
                + "      \"type\": \"integer\",\n"
                + "      \"input\": \"text\",\n"
                + "      \"operator\": \"greater_or_equal\",\n"
                + "      \"value\": 50\n"
                + "    },\n"
                + "    {\n"
                + "      \"id\": \"rateSlider.FFFFFE2.net_packet_in\",\n"
                + "      \"field\": \"rateSlider.FFFFFE2.net_packet_in\",\n"
                + "      \"type\": \"integer\",\n"
                + "      \"input\": \"text\",\n"
                + "      \"operator\": \"less\",\n"
                + "      \"value\": 67\n"
                + "    }\n"
                + "  ]\n"
                + "}");

        JSONObject jsonrulewhen2 = new JSONObject("{\n"
                + "  \"condition\": \"AND\",\n"
                + "  \"rules\": [\n"
                + "    {\n"
                + "      \"id\": \"FFFFFE1.MetricX\",\n"
                + "      \"field\": \"FFFFFE1.MetricX\",\n"
                + "      \"type\": \"integer\",\n"
                + "      \"input\": \"text\",\n"
                + "      \"operator\": \"equal\",\n"
                + "      \"value\": \"2\"\n"
                + "    },\n"
                + "    {\n"
                + "      \"id\": \"rateSlider.FFFFFE2.net_packet_out\",\n"
                + "      \"field\": \"rateSlider.FFFFFE2.net_packet_out\",\n"
                + "      \"type\": \"integer\",\n"
                + "      \"input\": \"text\",\n"
                + "      \"operator\": \"greater_or_equal\",\n"
                + "      \"value\": 50\n"
                + "    },\n"
                + "    {\n"
                + "      \"id\": \"rateSlider.FFFFFE2.net_packet_in\",\n"
                + "      \"field\": \"rateSlider.FFFFFE2.net_packet_in\",\n"
                + "      \"type\": \"integer\",\n"
                + "      \"input\": \"text\",\n"
                + "      \"operator\": \"less\",\n"
                + "      \"value\": 67\n"
                + "    }\n"
                + "  ]\n"
                + "}");

        JSONObject jsonrulewhen3 = new JSONObject("{\n"
                + "  \"condition\": \"OR\",\n"
                + "  \"rules\": [\n"
                + "    {\n"
                + "      \"id\": \"FFFFFE1.MetricX\",\n"
                + "      \"field\": \"FFFFFE1.MetricX\",\n"
                + "      \"type\": \"integer\",\n"
                + "      \"input\": \"text\",\n"
                + "      \"operator\": \"equal\",\n"
                + "      \"value\": \"4\"\n"
                + "    },\n"
                + "    {\n"
                + "      \"id\": \"rateSlider.FFFFFF.memory_free\",\n"
                + "      \"field\": \"rateSlider.FFFFFF.memory_free\",\n"
                + "      \"type\": \"integer\",\n"
                + "      \"input\": \"text\",\n"
                + "      \"operator\": \"equal\",\n"
                + "      \"value\": 35\n"
                + "    }\n"
                + "  ]\n"
                + "}");

        JSONObject jsonrulewhen = new JSONObject("{\n"
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

        //String solrQuery = RuleUtil.convertToRule("", jsonrulewhen, "AND");
        PackageDescrBuilder packageDescrBuilder = DescrFactory.newPackage();

        RuleDescrBuilder droolrule = packageDescrBuilder
                .name("eu.arcadia.expertsystem.rules")
                .newImport().target("eu.arcadia.expertsystem.facts.*").end()
                .newRule().name("r111111111111222222222");

        CEDescrBuilder<RuleDescrBuilder, AndDescr> when = droolrule.lhs();

        when = RuleUtil.convertToRule(when, jsonrulewhen, "AND","70s");

        droolrule.rhs("    System.out.println(\"foo\");\n").end();

        String rules = new DrlDumper().dump(packageDescrBuilder.getDescr());
        logger.info("drl " + rules);

        try {
            String data = rules;
            FileOutputStream out = new FileOutputStream("src/main/resources/eu/arcadia/expertsystem/rules/rule4.drl");
            out.write(data.getBytes());
            out.close();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(RulesEngineService.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

    }

    /**
     * Tests annotated with @Ignore are not executed.
     */
    @Ignore
    @Test
    public
            void ignoredTest() {
        Logger.getLogger(R2RUnitTest.class
                .getName()).warn("I shoud not be test!");
    }

}
