/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.arcadia.expertsystem.rules.generation;

/**
 *
 * @author Eleni Fotopoulou <efotopoulou@ubitech.eu>
 */
public enum Tags {

    DRL_EQUALS_TAG("=="),
    DRL_LESS_TAG("<"),
    DRL_LESS_OR_EQUAL_TAG("<="),
    DRL_GREATER_TAG(">"),
    DRL_GREATER_OR_EQUAL_TAG(">="),
    EB_STAR_TAG("<"),
    EB_END_TAG(">"),
    SOLR_EQUALS_TAG(":"),
    SOLR_EMPTY_TAG("*:*"),
    COMMA(","),
    DEFAULT_PAGE_SIZE("10"),
    DEFAULT_PAGE_START("0"),
    SPACE(" "),
    QUOTE("\""),
    NOT("NOT"),
    AND("AND"),
    OR("OR"),
    LBRACKET("["),
    RBRACKET("]"),
    LPARENTHESIS("("),
    RPARENTHESIS(")"),
    TO("TO"),
    ASTERISK("*"),
    EMPTY("");

    private final String tag;

    private Tags(String _tag) {
        tag = _tag;
    }

    /**
     * Get the actual value of the represented Tag
     *
     * @return String
     */
    public String value() {
        return tag;
    }
}
