/*
 *  The scanner definition for COOL.
 */

import java_cup.runtime.Symbol;

%%

%{

    // Max size of string constants
    static int MAX_STR_CONST = 1025;

    // For assembling string constants
    StringBuffer string_buf = new StringBuffer();
    
    private int comment_count = 0;

    private static final String MAX_SIZE_ERROR_MSG = "String constant too long"; 
    private static final String EOF_COMMENT_ERROR_MSG = "EOF in comment";
    private static final String EOF_STRING_ERROR_MSG = "EOF in string constant";
    private static final String COMMENT_CLOSE_ERROR_MSG = "Unmatched *)";
    private static final String STRING_NULL_CHAR_ERROR_MSG = "String contains null character";
    private static final String UNTERMINATED_STRING_ERROR_MSG = "Unterminated string constant";
    private static Symbol get_error(String msg){
	return new Symbol(TokenConstants.ERROR, msg);
    }

    private int curr_lineno = 1;
    int get_curr_lineno() {
	return curr_lineno;
    }

    private AbstractSymbol filename;

    void set_filename(String fname) {
	filename = AbstractTable.stringtable.addString(fname);
    }

    AbstractSymbol curr_filename() {
	return filename;
    }
%}

%init{

%init}

%eofval{

    switch(yy_lexical_state) {
    	case YYINITIAL:
	break;
	case ML_COMMENT:
		yybegin(YYINITIAL);
		return get_error(EOF_COMMENT_ERROR_MSG);
	case NULL_CHAR:
	case STRING:
		yybegin(YYINITIAL);
		return get_error( EOF_STRING_ERROR_MSG);
    }
    return new Symbol(TokenConstants.EOF);
%eofval}

%class CoolLexer
%cup
%unicode

WITHOUT_NEW_LINE_WHITE_SPACE_CHAR=[\ \f\r\t\013]
TYPE_ID=[A-Z][A-Za-z0-9_]*
OBJECT_ID=[a-z][A-Za-z0-9_]*
INTEGERS=[0-9]+

%state ML_COMMENT, COMMENT, STRING, NULL_CHAR

%%

<YYINITIAL>[Cc][Ll][Aa][Ss][Ss]	{return new Symbol(TokenConstants.CLASS); }
<YYINITIAL>[Ii][Nn][Hh][Ee][Rr][Ii][Tt][Ss] {return new Symbol(TokenConstants.INHERITS); }
<YYINITIAL>[Ii][Ff]		{return new Symbol(TokenConstants.IF); }
<YYINITIAL>[Tt][Hh][Ee][Nn]	{return new Symbol(TokenConstants.THEN); }
<YYINITIAL>[Ff][Ii]		{return new Symbol(TokenConstants.FI); }
<YYINITIAL>[Ee][Ll][Ss][Ee]	{return new Symbol(TokenConstants.ELSE); }
<YYINITIAL>[Cc][Aa][Ss][Ee]	{return new Symbol(TokenConstants.CASE); }
<YYINITIAL>[Ee][Ss][Aa][Cc]	{return new Symbol(TokenConstants.ESAC); }
<YYINITIAL>[Ww][Hh][Ii][Ll][Ee]	{return new Symbol(TokenConstants.WHILE); }
<YYINITIAL>[Ll][Oo][Oo][Pp]	{return new Symbol(TokenConstants.LOOP); }
<YYINITIAL>[Pp][Oo][Oo][Ll]	{return new Symbol(TokenConstants.POOL); }
<YYINITIAL>[Ll][Ee][Tt]		{return new Symbol(TokenConstants.LET); }
<YYINITIAL>[Nn][Ee][Ww]		{return new Symbol(TokenConstants.NEW); }
<YYINITIAL>[Ii][Nn]		{return new Symbol(TokenConstants.IN); }
<YYINITIAL>[Oo][Ff]		{return new Symbol(TokenConstants.OF); }

<YYINITIAL>[Ii][Ss][Vv][Oo][Ii][Dd] {return new Symbol(TokenConstants.ISVOID); }
<YYINITIAL>[Nn][Oo][Tt]		{return new Symbol(TokenConstants.NOT); }

<YYINITIAL>t[Rr][Uu][Ee]	{return new Symbol(TokenConstants.BOOL_CONST, java.lang.Boolean.TRUE); }
<YYINITIAL>f[Aa][Ll][Ss][Ee]	{return new Symbol(TokenConstants.BOOL_CONST, java.lang.Boolean.FALSE); }

<YYINITIAL>"."			{return new Symbol(TokenConstants.DOT); }
<YYINITIAL>"@"			{return new Symbol(TokenConstants.AT); }
<YYINITIAL>"~"			{return new Symbol(TokenConstants.NEG); }
<YYINITIAL>"*"			{return new Symbol(TokenConstants.MULT); }
<YYINITIAL>"/"			{return new Symbol(TokenConstants.DIV); }
<YYINITIAL>"+"			{return new Symbol(TokenConstants.PLUS); }
<YYINITIAL>"-"			{return new Symbol(TokenConstants.MINUS); }
<YYINITIAL>"<="			{return new Symbol(TokenConstants.LE); }
<YYINITIAL>"<"			{return new Symbol(TokenConstants.LT); }
<YYINITIAL>"="			{return new Symbol(TokenConstants.EQ); }
<YYINITIAL>"<-"			{return new Symbol(TokenConstants.ASSIGN); }

<YYINITIAL>"("			{return new Symbol(TokenConstants.LPAREN); }
<YYINITIAL>")"			{return new Symbol(TokenConstants.RPAREN); }
<YYINITIAL>"{"			{return new Symbol(TokenConstants.LBRACE); }
<YYINITIAL>"}"			{return new Symbol(TokenConstants.RBRACE); }
<YYINITIAL>":"			{return new Symbol(TokenConstants.COLON); }
<YYINITIAL>";"			{return new Symbol(TokenConstants.SEMI); }
<YYINITIAL>","			{return new Symbol(TokenConstants.COMMA); }
<YYINITIAL>"=>"			{return new Symbol(TokenConstants.DARROW); }

<YYINITIAL>{WITHOUT_NEW_LINE_WHITE_SPACE_CHAR}+ { }

<YYINITIAL>\n			{ curr_lineno++; }

<YYINITIAL>"--" 		{ yybegin(COMMENT); }
<YYINITIAL>"(*" 		{ comment_count++; yybegin(ML_COMMENT); }
<YYINITIAL>"*)" 		{ return get_error(COMMENT_CLOSE_ERROR_MSG); }


<COMMENT>\n|\r 			{ curr_lineno++; yybegin(YYINITIAL); }
<COMMENT>. 			{ }

<ML_COMMENT>"(*" 		{ comment_count++; }
<ML_COMMENT>"*)" 		{ comment_count--; if (comment_count == 0) {yybegin(YYINITIAL);}}
<ML_COMMENT>\n|\r		{ curr_lineno++; }
<ML_COMMENT>.			{}

<YYINITIAL>{INTEGERS}		{return new Symbol(TokenConstants.INT_CONST, AbstractTable.inttable.addString(yytext()));}

<YYINITIAL>{TYPE_ID}		{return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }

<YYINITIAL>{OBJECT_ID} 		{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }

<YYINITIAL>"\""			{ string_buf.setLength(0); yybegin(STRING); }

<STRING>"\""			{ yybegin(YYINITIAL);
				  if(string_buf.length() >= MAX_STR_CONST){
					return get_error(MAX_SIZE_ERROR_MSG);
				  }
				  return new Symbol(TokenConstants.STR_CONST, AbstractTable.stringtable.addString(string_buf.toString())); 
				}
<STRING>\000|\\\000		{ yybegin(NULL_CHAR); return get_error(STRING_NULL_CHAR_ERROR_MSG); }
<STRING>\r			{ curr_lineno++; string_buf.append('\r');}
<STRING>\\b			{ string_buf.append('\b'); }
<STRING>\\t			{ string_buf.append('\t'); }
<STRING>\\n			{ string_buf.append('\n'); }
<STRING>\\f			{ string_buf.append('\f'); }
<STRING>\\.			{ string_buf.append(yytext().substring(1,yytext().length())); }
<STRING>\\\n			{ string_buf.append(yytext().substring(1,yytext().length())); curr_lineno++; }
<STRING>\n 			{ curr_lineno++; yybegin(YYINITIAL); return get_error(UNTERMINATED_STRING_ERROR_MSG); }
<STRING>.			{ string_buf.append(yytext()); }

<NULL_CHAR>"\"" 		{ yybegin(YYINITIAL); }
<NULL_CHAR>\n|\r 		{ curr_lineno++; yybegin(YYINITIAL); }
<NULL_CHAR>\\\n			{ curr_lineno++;}
<NULL_CHAR>.			{}

.                               {return get_error(yytext()); }
