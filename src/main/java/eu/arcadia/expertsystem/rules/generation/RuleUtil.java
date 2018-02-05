/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.arcadia.expertsystem.rules.generation;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.drools.compiler.lang.api.CEDescrBuilder;
import org.drools.compiler.lang.api.RuleDescrBuilder;
import org.drools.compiler.lang.descr.AndDescr;
import org.drools.compiler.lang.descr.OrDescr;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Eleni Fotopoulou <efotopoulou@ubitech.eu>
 */
public class RuleUtil {

    List<String> myParams = new LinkedList<String>();

    /**
     * Use this method to convert the expression constructed form
     * ExpressionBuilder to valid Drools expression
     *
     * @param expressionObject
     * @param operand
     * @return
     */
    public static CEDescrBuilder<RuleDescrBuilder, AndDescr> convertToRule(CEDescrBuilder<RuleDescrBuilder, AndDescr> whendroolrule, Object expressionObject, String operand, String time_window) {

        //final String  TIME_WINDOW_DROOLS = "70s";
        String  TIME_WINDOW_DROOLS = time_window;
        
        List<String> myParams = new LinkedList<String>();
        myParams.add(TIME_WINDOW_DROOLS);

        //End of expression
        if ((null == expressionObject)) {
            if (operand == null) {
                return whendroolrule;
            }
            whendroolrule.end();
            return (whendroolrule);
        }
//        //Start of expression
//        if (droolrule.isEmpty()) {
//            droolrule += "( ";
//        }
        //An array of rules
        if (expressionObject instanceof JSONArray) {
            JSONArray tmpArray = ((JSONArray) expressionObject);
            //String tmpExpression = "";
            CEDescrBuilder<CEDescrBuilder<RuleDescrBuilder, AndDescr>, AndDescr> tempAND = null;
            CEDescrBuilder<CEDescrBuilder<RuleDescrBuilder, AndDescr>, OrDescr> tempOR = null;
            for (int i = 0; i < tmpArray.length(); i++) {

                if (i == 0) {

                    JSONObject jObj = tmpArray.getJSONObject(i);
                    // if (!tmpExpression.isEmpty()) {
                    //if (tempAND == null && tempOR == null) {
                    //tmpExpression += " " + operand + " ";
                    //System.out.println("operand" + operand);

                    if (operand.equalsIgnoreCase("AND")) {
                        tempAND = whendroolrule.and();
                    } else if (operand.equalsIgnoreCase("OR")) {
                        tempOR = whendroolrule.or();
                    }

                    //Multi level case
                    if (jObj.length() == 2) {
                        convertToRule(whendroolrule, jObj.getJSONArray("rules"), jObj.getString("condition"),time_window);
                    } //Single level case
                    else {
                        if (null != tempAND) {

                            tempAND.pattern().id("$tot" + i, true).type(Double.class.getName()).constraint("$tot" + i + " " + createdrlExpression(jObj).getString("operator")).from()
                                    .accumulate().source().pattern("MonitoredComponent").id("$m" + i, true).constraint("name== \"" + createdrlExpression(jObj).getString("pattern") + "\" && " + createdrlExpression(jObj).getString("constraint"))
                                    .from().entryPoint("MonitoringStream")
                                    .behavior().type("window", "time").parameters(myParams).end().end().end()
                                    .function("average", null, false, "$m" + i + ".getValue()")
                                    .end()
                                    .end();
                            //tempAND.pattern("MonitoredComponent").constraint("name== \"" + createdrlExpression(jObj).getString("pattern") + "\" && " + createdrlExpression(jObj).getString("constraint"));

                        } else {
                            tempOR.pattern().id("$tot" + i, true).type(Double.class.getName()).constraint("$tot" + i + " " + createdrlExpression(jObj).getString("operator")).from()
                                    .accumulate().source().pattern("MonitoredComponent").id("$m" + i, true).constraint("name== \"" + createdrlExpression(jObj).getString("pattern") + "\" && " + createdrlExpression(jObj).getString("constraint"))
                                    .from().entryPoint("MonitoringStream").behavior().type("window", "time").parameters(myParams).end().end().end()
                                    .function("average", null, false, "$m" + i + ".getValue()")
                                    .end()
                                    .end();
                            //tempOR.pattern("MonitoredComponent").constraint("name== \"" + createdrlExpression(jObj).getString("pattern") + "\" && " + createdrlExpression(jObj).getString("constraint"));
                        }
                    }

                } else {

                    JSONObject jObj = tmpArray.getJSONObject(i);
                    if (tempAND == null && tempOR == null) {
                        //tmpExpression += " " + operand + " ";

                        if (operand.equalsIgnoreCase("AND")) {
                            tempAND.and();
                        } else if (operand.equalsIgnoreCase("OR")) {
                            tempOR.or();
                        }
//                        switch (operand) {
//
//                            case ("AND"): {
//                                tempAND.and();
//                            }
//                            case ("OR"): {
//                                tempOR.or();
//                            }
//                        }
                    }
                    //Multi level case
                    if (jObj.length() == 2) {
                        //System.out.println("iterate -- call again");
                        convertToRule(whendroolrule, jObj.getJSONArray("rules"), jObj.getString("condition"),time_window);
                    } //Single level case
                    else {
                        if (null != tempAND) {
                            tempAND.pattern().id("$tot" + i, true).type(Double.class.getName()).constraint("$tot" + i + " " + createdrlExpression(jObj).getString("operator")).from()
                                    .accumulate().source().pattern("MonitoredComponent").id("$m" + i, true).constraint("name== \"" + createdrlExpression(jObj).getString("pattern") + "\" && " + createdrlExpression(jObj).getString("constraint"))
                                    .from().entryPoint("MonitoringStream").behavior().type("window", "time").parameters(myParams).end().end().end()
                                    .function("average", null, false, "$m" + i + ".getValue()")
                                    .end()
                                    .end();
                            //tempAND.pattern("MonitoredComponent").constraint("name== \"" + createdrlExpression(jObj).getString("pattern") + "\" && " + createdrlExpression(jObj).getString("constraint"));

                        } else {
                            tempOR.pattern().id("$tot" + i, true).type(Double.class.getName()).constraint("$tot" + i + " " + createdrlExpression(jObj).getString("operator")).from()
                                    .accumulate().source().pattern("MonitoredComponent").id("$m" + i, true).constraint("name== \"" + createdrlExpression(jObj).getString("pattern") + "\" && " + createdrlExpression(jObj).getString("constraint"))
                                    .from().entryPoint("MonitoringStream").behavior().type("window", "time").parameters(myParams).end().end().end()
                                    .function("average", null, false, "$m" + i + ".getValue()")
                                    .end()
                                    .end();
                            //tempOR.pattern("MonitoredComponent").constraint("name== \"" + createdrlExpression(jObj).getString("pattern") + "\" && " + createdrlExpression(jObj).getString("constraint"));
                        }
                    }

                }

            }
            //droolrule += tmpExpression;
            expressionObject = null;
        } else if (expressionObject instanceof JSONObject) {
            //System.out.println("einai jsonobject");
            JSONObject tmpObject = ((JSONObject) expressionObject);
            whendroolrule = convertToRule(whendroolrule, (tmpObject.getJSONArray("rules")), tmpObject.getString("condition"),time_window);
            expressionObject = null;
        }
        //System.out.println("kleinei kiklos");
        return convertToRule(whendroolrule, expressionObject, operand,time_window);
    }

    private static JSONObject createdrlExpression(JSONObject jsonObject) {

        String jsonfield = jsonObject.getString("field").replace("rateSlider.", "");

        String[] fields = jsonfield.split("\\.");

        String component = fields[0];
        //System.out.println("component name" + component);

        String metric = fields[1];
        //System.out.println("metric " + metric);

        String operator = null;

        String drlExpression = Tags.EB_STAR_TAG.value() + jsonfield + Tags.EB_END_TAG.value();

        String solrOperator = Tags.EMPTY.value();
        boolean NOT_EXPRESSION = false;
        //Valid operators 
        switch (jsonObject.getString("operator")) {

            case ("equal"): {
                solrOperator = Tags.EMPTY.value();
                break;
            }

            case ("not_equal"): {
                NOT_EXPRESSION = true;
                break;
            }
            case ("between"): {
                solrOperator = ":[]";
                break;
            }

            case ("not_begins_with"):
            case ("not_contains"):
            case ("not_ends_with"):
                solrOperator = Tags.ASTERISK.value();
                NOT_EXPRESSION = true;
                break;

            case ("begins_with"):
            case ("contains"):
            case ("ends_with"):
                solrOperator = Tags.ASTERISK.value();
                break;
        }

        //Handle NOT operator
        if (NOT_EXPRESSION) {
            drlExpression = Tags.SOLR_EMPTY_TAG.value() + Tags.SPACE.value() + Tags.NOT.value() + Tags.SPACE.value() + drlExpression;
            System.out.println("not expression " + Tags.SOLR_EMPTY_TAG.value() + Tags.SPACE.value() + Tags.NOT.value() + Tags.SPACE.value() + drlExpression);
        }

        //Valid Types date,integer,double,string
        if (jsonObject.getString("type").equals("date")) {
            throw new UnsupportedOperationException("Type Date is not yet implemented....");

        } //Supported Operators:  equal, not_equal, between, less, less_or_equal, greater, greater_or_equal 
        else if (jsonObject.getString("type").equals("integer") || jsonObject.getString("type").equals("double")) {
            switch (jsonObject.getString("operator")) {
                //Handle Operators: between
                case "between":
                    JSONArray values = jsonObject.getJSONArray("value");
                    drlExpression += Tags.SOLR_EQUALS_TAG.value() + Tags.LBRACKET.value() + (jsonObject.getString("type").equals("double") ? values.getDouble(0) : String.valueOf(values.getInt(0))) + Tags.SPACE.value() + Tags.TO.value() + Tags.SPACE.value() + (jsonObject.getString("type").equals("double") ? values.getDouble(0) : String.valueOf(values.getInt(0))) + Tags.RBRACKET.value() + Tags.SPACE.value();
                    break;
                //Handle Operators: less, less_or_equal
                case "less":
                    operator = Tags.DRL_LESS_TAG.value() + (jsonObject.getString("type").equals("double") ? jsonObject.getDouble("value") : String.valueOf(jsonObject.getInt("value")));
                //System.out.println("less: " + operator);

                case "less_or_equal":
                    drlExpression += Tags.SOLR_EQUALS_TAG.value() + Tags.LBRACKET.value() + Tags.ASTERISK.value() + Tags.SPACE.value() + Tags.TO.value() + Tags.SPACE.value()
                            + (jsonObject.getString("type").equals("double") ? jsonObject.getDouble("value") : String.valueOf(jsonObject.getInt("value")))
                            + Tags.RBRACKET.value() + Tags.SPACE.value()
                            + (jsonObject.getString("operator").equals("less") ? Tags.AND.value() + Tags.SPACE.value() + Tags.NOT.value() + Tags.SPACE.value() + (jsonObject.getString("type").equals("double") ? jsonObject.getDouble("value") : String.valueOf(jsonObject.getInt("value"))) : Tags.EMPTY.value()) + Tags.SPACE.value();

                    operator = Tags.DRL_LESS_OR_EQUAL_TAG.value() + (jsonObject.getString("type").equals("double") ? jsonObject.getDouble("value") : String.valueOf(jsonObject.getInt("value")));
                    //System.out.println("less_or_equal: " + operator);
                    break;
                //Handle Operators: equal, not_equalaa
                case "greater":
                    operator = Tags.DRL_GREATER_TAG.value() + (jsonObject.getString("type").equals("double") ? jsonObject.getDouble("value") : String.valueOf(jsonObject.getInt("value")));
                //System.out.println("greater: " + operator);
                case "greater_or_equal":
                    drlExpression += Tags.SOLR_EQUALS_TAG.value() + Tags.LBRACKET.value() + (jsonObject.getString("type").equals("double") ? jsonObject.getDouble("value") : String.valueOf(jsonObject.getInt("value"))) + Tags.SPACE.value() + Tags.TO.value() + Tags.SPACE.value() + Tags.ASTERISK.value() + Tags.RBRACKET.value() + Tags.SPACE.value() + (jsonObject.getString("operator").equals("greater") ? Tags.AND.value() + Tags.SPACE.value() + Tags.NOT.value() + Tags.SPACE.value() + (jsonObject.getString("type").equals("double") ? jsonObject.getDouble("value") : String.valueOf(jsonObject.getInt("value"))) : Tags.EMPTY.value()) + Tags.SPACE.value();
                    operator = Tags.DRL_GREATER_OR_EQUAL_TAG.value() + (jsonObject.getString("type").equals("double") ? jsonObject.getDouble("value") : String.valueOf(jsonObject.getInt("value")));
                    //System.out.println("greater_or_equal: " + operator);
                    break;
                case "equal":
                    operator = Tags.DRL_EQUALS_TAG.value() + (jsonObject.getString("type").equals("double") ? jsonObject.getDouble("value") : String.valueOf(jsonObject.getInt("value")));
                case "not_equal":
                    drlExpression += Tags.SOLR_EQUALS_TAG.value() + solrOperator + (jsonObject.getString("type").equals("double") ? jsonObject.getDouble("value") : String.valueOf(jsonObject.getInt("value"))) + Tags.SPACE.value();
                    break;
                default:
                    Logger.getLogger(RuleUtil.class.getName()).log(Level.SEVERE, "Operator: {0} is not supported for type: {1}", new Object[]{jsonObject.getString("operator"), jsonObject.getString("type")});
                    break;
            }
        } //Supported Operators:  equal, not_equal, begins_with, not_begins_with, contains, not_contains, ends_with, not_ends_with
        else if (jsonObject.getString("type").equals("string")) {
            //Handle Phrase Expressions
            String value = jsonObject.getString("value").replaceAll(" ", "\\\\ ");
            //Hanlde Operators: begins_with / not_begins_with
            switch (jsonObject.getString("operator")) {
                //Hanlde Operators: ends_with / not_ends_with
                case "begins_with":
                case "not_begins_with":
                    drlExpression += Tags.SOLR_EQUALS_TAG.value() + value + solrOperator + Tags.SPACE.value();
                    break;
                //Hanlde Operators: contains / not_contains
                case "ends_with":
                case "not_ends_with":
                    drlExpression += Tags.SOLR_EQUALS_TAG.value() + solrOperator + value + Tags.SPACE.value();
                    break;
                //Handle Operators: equal, not_equal
                case "contains":
                case "not_contains":
                    drlExpression += Tags.SOLR_EQUALS_TAG.value() + solrOperator + value + solrOperator + Tags.SPACE.value();
                    break;
                case "equal":
                    operator = Tags.DRL_EQUALS_TAG.value() + Tags.QUOTE.value()  + jsonObject.getString("value") + Tags.QUOTE.value() ;
                case "not_equal":
                    drlExpression += Tags.SOLR_EQUALS_TAG.value() + solrOperator + Tags.QUOTE.value() + value + Tags.QUOTE.value() + Tags.SPACE.value();
                    break;
                default:
                    Logger.getLogger(RuleUtil.class.getName()).log(Level.SEVERE, "Operator: {0} is not supported for type: {1}", new Object[]{jsonObject.getString("operator"), jsonObject.getString("type")});
                    break;
            }
        }

//        System.out.println("solrOperator ---> " + solrOperator);
        //System.out.println("drlExpression ---> " + drlExpression);
        JSONObject expression = new JSONObject();
        expression.put("pattern", component);
        //expression.put("constraint", "metric== \"" + metric + "\" && value" + operator);
        expression.put("constraint", "metric== \"" + metric + "\"");
        expression.put("operator", operator);
        return expression;
    }

    //TODO: Add all Solr Operands
    /**
     * Checks if a given keyword matches a drl operand
     *
     * @param keyword String representing a search term given by the user
     * @return Return true if the keyword is a Drooles operand, otherwise return
     * false
     */
    public static boolean isdrlOperand(String keyword) {
        if (keyword.equalsIgnoreCase(Tags.AND.toString()) || keyword.equalsIgnoreCase(Tags.OR.toString()) || keyword.equalsIgnoreCase(Tags.NOT.toString())) {
            return true;
        }
        return false;
    }
}
