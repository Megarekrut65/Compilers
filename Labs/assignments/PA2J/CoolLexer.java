/*
 *  The scanner definition for COOL.
 */
import java_cup.runtime.Symbol;


class CoolLexer implements java_cup.runtime.Scanner {
	private final int YY_BUFFER_SIZE = 512;
	private final int YY_F = -1;
	private final int YY_NO_STATE = -1;
	private final int YY_NOT_ACCEPT = 0;
	private final int YY_START = 1;
	private final int YY_END = 2;
	private final int YY_NO_ANCHOR = 4;
	private final int YY_BOL = 65536;
	private final int YY_EOF = 65537;

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
	private java.io.BufferedReader yy_reader;
	private int yy_buffer_index;
	private int yy_buffer_read;
	private int yy_buffer_start;
	private int yy_buffer_end;
	private char yy_buffer[];
	private boolean yy_at_bol;
	private int yy_lexical_state;

	CoolLexer (java.io.Reader reader) {
		this ();
		if (null == reader) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(reader);
	}

	CoolLexer (java.io.InputStream instream) {
		this ();
		if (null == instream) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(new java.io.InputStreamReader(instream));
	}

	private CoolLexer () {
		yy_buffer = new char[YY_BUFFER_SIZE];
		yy_buffer_read = 0;
		yy_buffer_index = 0;
		yy_buffer_start = 0;
		yy_buffer_end = 0;
		yy_at_bol = true;
		yy_lexical_state = YYINITIAL;

	}

	private boolean yy_eof_done = false;
	private final int STRING = 3;
	private final int ML_COMMENT = 1;
	private final int YYINITIAL = 0;
	private final int COMMENT = 2;
	private final int NULL_CHAR = 4;
	private final int yy_state_dtrans[] = {
		0,
		71,
		94,
		98,
		101
	};
	private void yybegin (int state) {
		yy_lexical_state = state;
	}
	private int yy_advance ()
		throws java.io.IOException {
		int next_read;
		int i;
		int j;

		if (yy_buffer_index < yy_buffer_read) {
			return yy_buffer[yy_buffer_index++];
		}

		if (0 != yy_buffer_start) {
			i = yy_buffer_start;
			j = 0;
			while (i < yy_buffer_read) {
				yy_buffer[j] = yy_buffer[i];
				++i;
				++j;
			}
			yy_buffer_end = yy_buffer_end - yy_buffer_start;
			yy_buffer_start = 0;
			yy_buffer_read = j;
			yy_buffer_index = j;
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}

		while (yy_buffer_index >= yy_buffer_read) {
			if (yy_buffer_index >= yy_buffer.length) {
				yy_buffer = yy_double(yy_buffer);
			}
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}
		return yy_buffer[yy_buffer_index++];
	}
	private void yy_move_end () {
		if (yy_buffer_end > yy_buffer_start &&
		    '\n' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
		if (yy_buffer_end > yy_buffer_start &&
		    '\r' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
	}
	private boolean yy_last_was_cr=false;
	private void yy_mark_start () {
		yy_buffer_start = yy_buffer_index;
	}
	private void yy_mark_end () {
		yy_buffer_end = yy_buffer_index;
	}
	private void yy_to_mark () {
		yy_buffer_index = yy_buffer_end;
		yy_at_bol = (yy_buffer_end > yy_buffer_start) &&
		            ('\r' == yy_buffer[yy_buffer_end-1] ||
		             '\n' == yy_buffer[yy_buffer_end-1] ||
		             2028/*LS*/ == yy_buffer[yy_buffer_end-1] ||
		             2029/*PS*/ == yy_buffer[yy_buffer_end-1]);
	}
	private java.lang.String yytext () {
		return (new java.lang.String(yy_buffer,
			yy_buffer_start,
			yy_buffer_end - yy_buffer_start));
	}
	private int yylength () {
		return yy_buffer_end - yy_buffer_start;
	}
	private char[] yy_double (char buf[]) {
		int i;
		char newbuf[];
		newbuf = new char[2*buf.length];
		for (i = 0; i < buf.length; ++i) {
			newbuf[i] = buf[i];
		}
		return newbuf;
	}
	private final int YY_E_INTERNAL = 0;
	private final int YY_E_MATCH = 1;
	private java.lang.String yy_error_string[] = {
		"Error: Internal error.\n",
		"Error: Unmatched input.\n"
	};
	private void yy_error (int code,boolean fatal) {
		java.lang.System.out.print(yy_error_string[code]);
		java.lang.System.out.flush();
		if (fatal) {
			throw new Error("Fatal Error.\n");
		}
	}
	private int[][] unpackFromString(int size1, int size2, String st) {
		int colonIndex = -1;
		String lengthString;
		int sequenceLength = 0;
		int sequenceInteger = 0;

		int commaIndex;
		String workString;

		int res[][] = new int[size1][size2];
		for (int i= 0; i < size1; i++) {
			for (int j= 0; j < size2; j++) {
				if (sequenceLength != 0) {
					res[i][j] = sequenceInteger;
					sequenceLength--;
					continue;
				}
				commaIndex = st.indexOf(',');
				workString = (commaIndex==-1) ? st :
					st.substring(0, commaIndex);
				st = st.substring(commaIndex+1);
				colonIndex = workString.indexOf(':');
				if (colonIndex == -1) {
					res[i][j]=Integer.parseInt(workString);
					continue;
				}
				lengthString =
					workString.substring(colonIndex+1);
				sequenceLength=Integer.parseInt(lengthString);
				workString=workString.substring(0,colonIndex);
				sequenceInteger=Integer.parseInt(workString);
				res[i][j] = sequenceInteger;
				sequenceLength--;
			}
		}
		return res;
	}
	private int yy_acpt[] = {
		/* 0 */ YY_NOT_ACCEPT,
		/* 1 */ YY_NO_ANCHOR,
		/* 2 */ YY_NO_ANCHOR,
		/* 3 */ YY_NO_ANCHOR,
		/* 4 */ YY_NO_ANCHOR,
		/* 5 */ YY_NO_ANCHOR,
		/* 6 */ YY_NO_ANCHOR,
		/* 7 */ YY_NO_ANCHOR,
		/* 8 */ YY_NO_ANCHOR,
		/* 9 */ YY_NO_ANCHOR,
		/* 10 */ YY_NO_ANCHOR,
		/* 11 */ YY_NO_ANCHOR,
		/* 12 */ YY_NO_ANCHOR,
		/* 13 */ YY_NO_ANCHOR,
		/* 14 */ YY_NO_ANCHOR,
		/* 15 */ YY_NO_ANCHOR,
		/* 16 */ YY_NO_ANCHOR,
		/* 17 */ YY_NO_ANCHOR,
		/* 18 */ YY_NO_ANCHOR,
		/* 19 */ YY_NO_ANCHOR,
		/* 20 */ YY_NO_ANCHOR,
		/* 21 */ YY_NO_ANCHOR,
		/* 22 */ YY_NO_ANCHOR,
		/* 23 */ YY_NO_ANCHOR,
		/* 24 */ YY_NO_ANCHOR,
		/* 25 */ YY_NO_ANCHOR,
		/* 26 */ YY_NO_ANCHOR,
		/* 27 */ YY_NO_ANCHOR,
		/* 28 */ YY_NO_ANCHOR,
		/* 29 */ YY_NO_ANCHOR,
		/* 30 */ YY_NO_ANCHOR,
		/* 31 */ YY_NO_ANCHOR,
		/* 32 */ YY_NO_ANCHOR,
		/* 33 */ YY_NO_ANCHOR,
		/* 34 */ YY_NO_ANCHOR,
		/* 35 */ YY_NO_ANCHOR,
		/* 36 */ YY_NO_ANCHOR,
		/* 37 */ YY_NO_ANCHOR,
		/* 38 */ YY_NO_ANCHOR,
		/* 39 */ YY_NO_ANCHOR,
		/* 40 */ YY_NO_ANCHOR,
		/* 41 */ YY_NO_ANCHOR,
		/* 42 */ YY_NO_ANCHOR,
		/* 43 */ YY_NO_ANCHOR,
		/* 44 */ YY_NO_ANCHOR,
		/* 45 */ YY_NO_ANCHOR,
		/* 46 */ YY_NO_ANCHOR,
		/* 47 */ YY_NO_ANCHOR,
		/* 48 */ YY_NO_ANCHOR,
		/* 49 */ YY_NO_ANCHOR,
		/* 50 */ YY_NO_ANCHOR,
		/* 51 */ YY_NO_ANCHOR,
		/* 52 */ YY_NO_ANCHOR,
		/* 53 */ YY_NO_ANCHOR,
		/* 54 */ YY_NO_ANCHOR,
		/* 55 */ YY_NO_ANCHOR,
		/* 56 */ YY_NO_ANCHOR,
		/* 57 */ YY_NO_ANCHOR,
		/* 58 */ YY_NO_ANCHOR,
		/* 59 */ YY_NO_ANCHOR,
		/* 60 */ YY_NO_ANCHOR,
		/* 61 */ YY_NO_ANCHOR,
		/* 62 */ YY_NO_ANCHOR,
		/* 63 */ YY_NO_ANCHOR,
		/* 64 */ YY_NO_ANCHOR,
		/* 65 */ YY_NO_ANCHOR,
		/* 66 */ YY_NO_ANCHOR,
		/* 67 */ YY_NO_ANCHOR,
		/* 68 */ YY_NO_ANCHOR,
		/* 69 */ YY_NO_ANCHOR,
		/* 70 */ YY_NO_ANCHOR,
		/* 71 */ YY_NOT_ACCEPT,
		/* 72 */ YY_NO_ANCHOR,
		/* 73 */ YY_NO_ANCHOR,
		/* 74 */ YY_NO_ANCHOR,
		/* 75 */ YY_NO_ANCHOR,
		/* 76 */ YY_NO_ANCHOR,
		/* 77 */ YY_NO_ANCHOR,
		/* 78 */ YY_NO_ANCHOR,
		/* 79 */ YY_NO_ANCHOR,
		/* 80 */ YY_NO_ANCHOR,
		/* 81 */ YY_NO_ANCHOR,
		/* 82 */ YY_NO_ANCHOR,
		/* 83 */ YY_NO_ANCHOR,
		/* 84 */ YY_NO_ANCHOR,
		/* 85 */ YY_NO_ANCHOR,
		/* 86 */ YY_NO_ANCHOR,
		/* 87 */ YY_NO_ANCHOR,
		/* 88 */ YY_NO_ANCHOR,
		/* 89 */ YY_NO_ANCHOR,
		/* 90 */ YY_NO_ANCHOR,
		/* 91 */ YY_NO_ANCHOR,
		/* 92 */ YY_NO_ANCHOR,
		/* 93 */ YY_NO_ANCHOR,
		/* 94 */ YY_NOT_ACCEPT,
		/* 95 */ YY_NO_ANCHOR,
		/* 96 */ YY_NO_ANCHOR,
		/* 97 */ YY_NO_ANCHOR,
		/* 98 */ YY_NOT_ACCEPT,
		/* 99 */ YY_NO_ANCHOR,
		/* 100 */ YY_NO_ANCHOR,
		/* 101 */ YY_NOT_ACCEPT,
		/* 102 */ YY_NO_ANCHOR,
		/* 103 */ YY_NO_ANCHOR,
		/* 104 */ YY_NO_ANCHOR,
		/* 105 */ YY_NO_ANCHOR,
		/* 106 */ YY_NO_ANCHOR,
		/* 107 */ YY_NO_ANCHOR,
		/* 108 */ YY_NO_ANCHOR,
		/* 109 */ YY_NO_ANCHOR,
		/* 110 */ YY_NO_ANCHOR,
		/* 111 */ YY_NO_ANCHOR,
		/* 112 */ YY_NO_ANCHOR,
		/* 113 */ YY_NO_ANCHOR,
		/* 114 */ YY_NO_ANCHOR,
		/* 115 */ YY_NO_ANCHOR,
		/* 116 */ YY_NO_ANCHOR,
		/* 117 */ YY_NO_ANCHOR,
		/* 118 */ YY_NO_ANCHOR,
		/* 119 */ YY_NO_ANCHOR,
		/* 120 */ YY_NO_ANCHOR,
		/* 121 */ YY_NO_ANCHOR,
		/* 122 */ YY_NO_ANCHOR,
		/* 123 */ YY_NO_ANCHOR,
		/* 124 */ YY_NO_ANCHOR,
		/* 125 */ YY_NO_ANCHOR,
		/* 126 */ YY_NO_ANCHOR,
		/* 127 */ YY_NO_ANCHOR,
		/* 128 */ YY_NO_ANCHOR,
		/* 129 */ YY_NO_ANCHOR,
		/* 130 */ YY_NO_ANCHOR,
		/* 131 */ YY_NO_ANCHOR,
		/* 132 */ YY_NO_ANCHOR,
		/* 133 */ YY_NO_ANCHOR,
		/* 134 */ YY_NO_ANCHOR,
		/* 135 */ YY_NO_ANCHOR,
		/* 136 */ YY_NO_ANCHOR,
		/* 137 */ YY_NO_ANCHOR,
		/* 138 */ YY_NO_ANCHOR,
		/* 139 */ YY_NO_ANCHOR,
		/* 140 */ YY_NO_ANCHOR,
		/* 141 */ YY_NO_ANCHOR,
		/* 142 */ YY_NO_ANCHOR,
		/* 143 */ YY_NO_ANCHOR,
		/* 144 */ YY_NO_ANCHOR,
		/* 145 */ YY_NO_ANCHOR,
		/* 146 */ YY_NO_ANCHOR,
		/* 147 */ YY_NO_ANCHOR,
		/* 148 */ YY_NO_ANCHOR,
		/* 149 */ YY_NO_ANCHOR,
		/* 150 */ YY_NO_ANCHOR,
		/* 151 */ YY_NO_ANCHOR,
		/* 152 */ YY_NO_ANCHOR,
		/* 153 */ YY_NO_ANCHOR,
		/* 154 */ YY_NO_ANCHOR,
		/* 155 */ YY_NO_ANCHOR,
		/* 156 */ YY_NO_ANCHOR,
		/* 157 */ YY_NO_ANCHOR,
		/* 158 */ YY_NO_ANCHOR,
		/* 159 */ YY_NO_ANCHOR,
		/* 160 */ YY_NO_ANCHOR,
		/* 161 */ YY_NO_ANCHOR,
		/* 162 */ YY_NO_ANCHOR,
		/* 163 */ YY_NO_ANCHOR,
		/* 164 */ YY_NO_ANCHOR,
		/* 165 */ YY_NO_ANCHOR,
		/* 166 */ YY_NO_ANCHOR,
		/* 167 */ YY_NO_ANCHOR,
		/* 168 */ YY_NO_ANCHOR,
		/* 169 */ YY_NO_ANCHOR,
		/* 170 */ YY_NO_ANCHOR,
		/* 171 */ YY_NO_ANCHOR,
		/* 172 */ YY_NO_ANCHOR,
		/* 173 */ YY_NO_ANCHOR,
		/* 174 */ YY_NO_ANCHOR,
		/* 175 */ YY_NO_ANCHOR,
		/* 176 */ YY_NO_ANCHOR,
		/* 177 */ YY_NO_ANCHOR,
		/* 178 */ YY_NO_ANCHOR,
		/* 179 */ YY_NO_ANCHOR,
		/* 180 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,65538,
"61,40:8,37,38,37:2,39,40:18,37,40,60,40:5,29,30,23,25,35,26,20,24,41:10,33," +
"34,27,28,36,40,21,42,43,44,45,46,11,43,47,48,43:2,49,43,50,51,52,43,53,54,1" +
"0,55,56,57,43:3,40,62,40:2,58,40,3,63,1,16,8,19,59,7,5,59:2,2,59,6,13,14,59" +
",9,4,17,18,15,12,59:3,31,40,32,22,40:65409,0:2")[0];

	private int yy_rmap[] = unpackFromString(1,181,
"0,1,2,3,1:3,4,1:2,5,6,7,8,1:7,9,1,10,1,11,12,13,12,1:6,12:7,13,12:7,1:21,14" +
",15,16,17,13,12,13:8,12,13:5,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,3" +
"3,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,5" +
"8,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,8" +
"3,84,85,86,87,88,89,90,91,92,93,94,95,96,12,13,97,98,99,100,101,102,103,104" +
",105")[0];

	private int yy_nxt[][] = unpackFromString(106,64,
"1,2,130,170:2,72,132,170,172,170,3,73,174,95,176,170:2,178,170,99,4,5,6,7,8" +
",9,10,11,12,13,14,15,16,17,18,19,20,21,22,21,20,23,171:2,173,171,175,171,96" +
",131,133,100,177,171:4,179,20,170,24,20:2,170,-1:65,170,180,134,170:16,-1:2" +
"1,170,134,170:6,180,170:10,-1:3,170,-1,171:6,135,171:12,-1:21,171:6,135,171" +
":12,-1:3,171,-1:30,29,-1:59,30,-1:63,31,-1,32,-1:71,33,-1:50,34,-1:77,21,-1" +
",21,-1:65,23,-1:23,170:6,158,170:12,-1:21,170:6,158,170:12,-1:3,170,-1,170:" +
"19,-1:21,170:19,-1:3,170,-1,171:19,-1:21,171:19,-1:3,171,1,50:22,91,50:5,97" +
",50:8,51:2,50:24,-1,170:3,138,170,25,170:4,26,170:7,26,-1:21,170:9,25,170:3" +
",138,170:5,-1:3,170,-1,171:4,27,171:14,-1:21,171:7,27,171:11,-1:3,171,-1,17" +
"1:6,157,171:12,-1:21,171:6,157,171:12,-1:3,171,-1:30,52,-1:34,61:5,62,61:10" +
",63,61,64,61:18,65,-1,61:21,60,61,66,-1:38,70,-1:25,1,54:37,55:2,54:24,-1,1" +
"70:10,28,170:7,28,-1:21,170:19,-1:3,170,-1,171:3,145,171,74,171:4,75,171:7," +
"75,-1:21,171:9,74,171:3,145,171:5,-1:3,171,-1:23,53,-1:40,1,56:37,57,58,56:" +
"20,59,60,92,56,-1,170:2,152,170,76,170:14,-1:21,170,152,170:5,76,170:11,-1:" +
"3,170,-1,171:10,77,171:7,77,-1:21,171:19,-1:3,171,1,67:37,68:2,67:20,69,67," +
"93,67,-1,170:9,35,170:6,35,170:2,-1:21,170:19,-1:3,170,-1,171:9,78,171:6,78" +
",171:2,-1:21,171:19,-1:3,171,-1,170:11,36,170:7,-1:21,170:16,36,170:2,-1:3," +
"170,-1,171:11,79,171:7,-1:21,171:16,79,171:2,-1:3,171,-1,170:9,37,170:6,37," +
"170:2,-1:21,170:19,-1:3,170,-1,171:9,80,171:6,80,171:2,-1:21,171:19,-1:3,17" +
"1,-1,170:7,38,170:11,-1:21,170:5,38,170:13,-1:3,170,-1,171:5,42,171:13,-1:2" +
"1,171:9,42,171:9,-1:3,171,-1,170:13,39,170:5,-1:21,170:11,39,170:7,-1:3,170" +
",-1,171:7,81,171:11,-1:21,171:5,81,171:13,-1:3,171,-1,170:7,40,170:11,-1:21" +
",170:5,40,170:13,-1:3,170,-1,171:7,83,171:11,-1:21,171:5,83,171:13,-1:3,171" +
",-1,41,170:18,-1:21,170:3,41,170:15,-1:3,170,-1,84,171:18,-1:21,171:3,84,17" +
"1:15,-1:3,171,-1,170,43,170:17,-1:21,170:8,43,170:10,-1:3,170,-1,171:13,82," +
"171:5,-1:21,171:11,82,171:7,-1:3,171,-1,170:5,85,170:13,-1:21,170:9,85,170:" +
"9,-1:3,170,-1,171,86,171:17,-1:21,171:8,86,171:10,-1:3,171,-1,170:7,44,170:" +
"11,-1:21,170:5,44,170:13,-1:3,170,-1,171:3,87,171:15,-1:21,171:13,87,171:5," +
"-1:3,171,-1,170:3,45,170:15,-1:21,170:13,45,170:5,-1:3,170,-1,171:7,88,171:" +
"11,-1:21,171:5,88,171:13,-1:3,171,-1,170:7,46,170:11,-1:21,170:5,46,170:13," +
"-1:3,170,-1,171:15,89,171:3,-1:21,171:4,89,171:14,-1:3,171,-1,170:7,47,170:" +
"11,-1:21,170:5,47,170:13,-1:3,170,-1,171:3,90,171:15,-1:21,171:13,90,171:5," +
"-1:3,171,-1,170:15,48,170:3,-1:21,170:4,48,170:14,-1:3,170,-1,170:3,49,170:" +
"15,-1:21,170:13,49,170:5,-1:3,170,-1,170:7,102,170:4,136,170:6,-1:21,170:5," +
"102,170:4,136,170:8,-1:3,170,-1,171:7,103,171:4,147,171:6,-1:21,171:5,103,1" +
"71:4,147,171:8,-1:3,171,-1,170:7,104,170:4,106,170:6,-1:21,170:5,104,170:4," +
"106,170:8,-1:3,170,-1,171:7,105,171:4,107,171:6,-1:21,171:5,105,171:4,107,1" +
"71:8,-1:3,171,-1,170:3,108,170:15,-1:21,170:13,108,170:5,-1:3,170,-1,171:7," +
"109,171:11,-1:21,171:5,109,171:13,-1:3,171,-1,170:12,110,170:6,-1:21,170:10" +
",110,170:8,-1:3,170,-1,171:2,153,171:16,-1:21,171,153,171:17,-1:3,171,-1,17" +
"0:14,156,170:4,-1:21,170:15,156,170:3,-1:3,170,-1,171:3,111,171:15,-1:21,17" +
"1:13,111,171:5,-1:3,171,-1,170:3,112,170:15,-1:21,170:13,112,170:5,-1:3,170" +
",-1,171:3,113,171:15,-1:21,171:13,113,171:5,-1:3,171,-1,170:2,114,170:16,-1" +
":21,170,114,170:17,-1:3,170,-1,171:2,115,171:16,-1:21,171,115,171:17,-1:3,1" +
"71,-1,170:4,160,170:14,-1:21,170:7,160,170:11,-1:3,170,-1,171:14,155,171:4," +
"-1:21,171:15,155,171:3,-1:3,171,-1,170:12,116,170:6,-1:21,170:10,116,170:8," +
"-1:3,170,-1,171:12,117,171:6,-1:21,171:10,117,171:8,-1:3,171,-1,170:7,118,1" +
"70:11,-1:21,170:5,118,170:13,-1:3,170,-1,171:12,119,171:6,-1:21,171:10,119," +
"171:8,-1:3,171,-1,170:17,120,170,-1:21,170:14,120,170:4,-1:3,170,-1,171:4,1" +
"59,171:14,-1:21,171:7,159,171:11,-1:3,171,-1,170,162,170:17,-1:21,170:8,162" +
",170:10,-1:3,170,-1,171:3,121,171:15,-1:21,171:13,121,171:5,-1:3,171,-1,170" +
":3,122,170:15,-1:21,170:13,122,170:5,-1:3,170,-1,171:12,161,171:6,-1:21,171" +
":10,161,171:8,-1:3,171,-1,170:12,164,170:6,-1:21,170:10,164,170:8,-1:3,170," +
"-1,171:7,163,171:11,-1:21,171:5,163,171:13,-1:3,171,-1,170:7,166,170:11,-1:" +
"21,170:5,166,170:13,-1:3,170,-1,171,123,171:17,-1:21,171:8,123,171:10,-1:3," +
"171,-1,170,124,170:17,-1:21,170:8,124,170:10,-1:3,170,-1,171:4,125,171:14,-" +
"1:21,171:7,125,171:11,-1:3,171,-1,170:3,126,170:15,-1:21,170:13,126,170:5,-" +
"1:3,170,-1,171:8,165,171:10,-1:21,171:12,165,171:6,-1:3,171,-1,170:4,128,17" +
"0:14,-1:21,170:7,128,170:11,-1:3,170,-1,171:4,167,171:14,-1:21,171:7,167,17" +
"1:11,-1:3,171,-1,170:8,168,170:10,-1:21,170:12,168,170:6,-1:3,170,-1,171:9," +
"127,171:6,127,171:2,-1:21,171:19,-1:3,171,-1,170:4,169,170:14,-1:21,170:7,1" +
"69,170:11,-1:3,170,-1,170:9,129,170:6,129,170:2,-1:21,170:19,-1:3,170,-1,17" +
"0,140,170,142,170:15,-1:21,170:8,140,170:4,142,170:5,-1:3,170,-1,171,137,13" +
"9,171:16,-1:21,171,139,171:6,137,171:10,-1:3,171,-1,170:6,144,170:12,-1:21," +
"170:6,144,170:12,-1:3,170,-1,171,141,171,143,171:15,-1:21,171:8,141,171:4,1" +
"43,171:5,-1:3,171,-1,170:12,146,170:6,-1:21,170:10,146,170:8,-1:3,170,-1,17" +
"1:12,149,171:6,-1:21,171:10,149,171:8,-1:3,171,-1,170:6,148,170,150,170:10," +
"-1:21,170:6,148,170:5,150,170:6,-1:3,170,-1,171:6,151,171:12,-1:21,171:6,15" +
"1,171:12,-1:3,171,-1,170:2,154,170:16,-1:21,170,154,170:17,-1:3,170");

	public java_cup.runtime.Symbol next_token ()
		throws java.io.IOException {
		int yy_lookahead;
		int yy_anchor = YY_NO_ANCHOR;
		int yy_state = yy_state_dtrans[yy_lexical_state];
		int yy_next_state = YY_NO_STATE;
		int yy_last_accept_state = YY_NO_STATE;
		boolean yy_initial = true;
		int yy_this_accept;

		yy_mark_start();
		yy_this_accept = yy_acpt[yy_state];
		if (YY_NOT_ACCEPT != yy_this_accept) {
			yy_last_accept_state = yy_state;
			yy_mark_end();
		}
		while (true) {
			if (yy_initial && yy_at_bol) yy_lookahead = YY_BOL;
			else yy_lookahead = yy_advance();
			yy_next_state = YY_F;
			yy_next_state = yy_nxt[yy_rmap[yy_state]][yy_cmap[yy_lookahead]];
			if (YY_EOF == yy_lookahead && true == yy_initial) {

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
			}
			if (YY_F != yy_next_state) {
				yy_state = yy_next_state;
				yy_initial = false;
				yy_this_accept = yy_acpt[yy_state];
				if (YY_NOT_ACCEPT != yy_this_accept) {
					yy_last_accept_state = yy_state;
					yy_mark_end();
				}
			}
			else {
				if (YY_NO_STATE == yy_last_accept_state) {
					throw (new Error("Lexical Error: Unmatched Input."));
				}
				else {
					yy_anchor = yy_acpt[yy_last_accept_state];
					if (0 != (YY_END & yy_anchor)) {
						yy_move_end();
					}
					yy_to_mark();
					switch (yy_last_accept_state) {
					case 1:
						
					case -2:
						break;
					case 2:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -3:
						break;
					case 3:
						{return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -4:
						break;
					case 4:
						{return new Symbol(TokenConstants.DOT); }
					case -5:
						break;
					case 5:
						{return new Symbol(TokenConstants.AT); }
					case -6:
						break;
					case 6:
						{return new Symbol(TokenConstants.NEG); }
					case -7:
						break;
					case 7:
						{return new Symbol(TokenConstants.MULT); }
					case -8:
						break;
					case 8:
						{return new Symbol(TokenConstants.DIV); }
					case -9:
						break;
					case 9:
						{return new Symbol(TokenConstants.PLUS); }
					case -10:
						break;
					case 10:
						{return new Symbol(TokenConstants.MINUS); }
					case -11:
						break;
					case 11:
						{return new Symbol(TokenConstants.LT); }
					case -12:
						break;
					case 12:
						{return new Symbol(TokenConstants.EQ); }
					case -13:
						break;
					case 13:
						{return new Symbol(TokenConstants.LPAREN); }
					case -14:
						break;
					case 14:
						{return new Symbol(TokenConstants.RPAREN); }
					case -15:
						break;
					case 15:
						{return new Symbol(TokenConstants.LBRACE); }
					case -16:
						break;
					case 16:
						{return new Symbol(TokenConstants.RBRACE); }
					case -17:
						break;
					case 17:
						{return new Symbol(TokenConstants.COLON); }
					case -18:
						break;
					case 18:
						{return new Symbol(TokenConstants.SEMI); }
					case -19:
						break;
					case 19:
						{return new Symbol(TokenConstants.COMMA); }
					case -20:
						break;
					case 20:
						{return get_error(yytext()); }
					case -21:
						break;
					case 21:
						{ }
					case -22:
						break;
					case 22:
						{ curr_lineno++; }
					case -23:
						break;
					case 23:
						{return new Symbol(TokenConstants.INT_CONST, AbstractTable.inttable.addString(yytext()));}
					case -24:
						break;
					case 24:
						{ string_buf.setLength(0); yybegin(STRING); }
					case -25:
						break;
					case 25:
						{return new Symbol(TokenConstants.IN); }
					case -26:
						break;
					case 26:
						{return new Symbol(TokenConstants.IF); }
					case -27:
						break;
					case 27:
						{return new Symbol(TokenConstants.FI); }
					case -28:
						break;
					case 28:
						{return new Symbol(TokenConstants.OF); }
					case -29:
						break;
					case 29:
						{ return get_error(COMMENT_CLOSE_ERROR_MSG); }
					case -30:
						break;
					case 30:
						{ yybegin(COMMENT); }
					case -31:
						break;
					case 31:
						{return new Symbol(TokenConstants.ASSIGN); }
					case -32:
						break;
					case 32:
						{return new Symbol(TokenConstants.LE); }
					case -33:
						break;
					case 33:
						{return new Symbol(TokenConstants.DARROW); }
					case -34:
						break;
					case 34:
						{ comment_count++; yybegin(ML_COMMENT); }
					case -35:
						break;
					case 35:
						{return new Symbol(TokenConstants.LET); }
					case -36:
						break;
					case 36:
						{return new Symbol(TokenConstants.NEW); }
					case -37:
						break;
					case 37:
						{return new Symbol(TokenConstants.NOT); }
					case -38:
						break;
					case 38:
						{return new Symbol(TokenConstants.CASE); }
					case -39:
						break;
					case 39:
						{return new Symbol(TokenConstants.LOOP); }
					case -40:
						break;
					case 40:
						{return new Symbol(TokenConstants.ELSE); }
					case -41:
						break;
					case 41:
						{return new Symbol(TokenConstants.ESAC); }
					case -42:
						break;
					case 42:
						{return new Symbol(TokenConstants.THEN); }
					case -43:
						break;
					case 43:
						{return new Symbol(TokenConstants.POOL); }
					case -44:
						break;
					case 44:
						{return new Symbol(TokenConstants.BOOL_CONST, java.lang.Boolean.TRUE); }
					case -45:
						break;
					case 45:
						{return new Symbol(TokenConstants.CLASS); }
					case -46:
						break;
					case 46:
						{return new Symbol(TokenConstants.WHILE); }
					case -47:
						break;
					case 47:
						{return new Symbol(TokenConstants.BOOL_CONST, java.lang.Boolean.FALSE); }
					case -48:
						break;
					case 48:
						{return new Symbol(TokenConstants.ISVOID); }
					case -49:
						break;
					case 49:
						{return new Symbol(TokenConstants.INHERITS); }
					case -50:
						break;
					case 50:
						{}
					case -51:
						break;
					case 51:
						{ curr_lineno++; }
					case -52:
						break;
					case 52:
						{ comment_count--; if (comment_count == 0) {yybegin(YYINITIAL);}}
					case -53:
						break;
					case 53:
						{ comment_count++; }
					case -54:
						break;
					case 54:
						{ }
					case -55:
						break;
					case 55:
						{ curr_lineno++; yybegin(YYINITIAL); }
					case -56:
						break;
					case 56:
						{ string_buf.append(yytext()); }
					case -57:
						break;
					case 57:
						{ curr_lineno++; yybegin(YYINITIAL); return get_error(UNTERMINATED_STRING_ERROR_MSG); }
					case -58:
						break;
					case 58:
						{ curr_lineno++; string_buf.append('\r');}
					case -59:
						break;
					case 59:
						{ yybegin(YYINITIAL);
				  if(string_buf.length() >= MAX_STR_CONST){
					return get_error(MAX_SIZE_ERROR_MSG);
				  }
				  return new Symbol(TokenConstants.STR_CONST, AbstractTable.stringtable.addString(string_buf.toString())); 
				}
					case -60:
						break;
					case 60:
						{ yybegin(NULL_CHAR); return get_error(STRING_NULL_CHAR_ERROR_MSG); }
					case -61:
						break;
					case 61:
						{ string_buf.append(yytext().substring(1,yytext().length())); }
					case -62:
						break;
					case 62:
						{ string_buf.append('\n'); }
					case -63:
						break;
					case 63:
						{ string_buf.append('\t'); }
					case -64:
						break;
					case 64:
						{ string_buf.append('\f'); }
					case -65:
						break;
					case 65:
						{ string_buf.append(yytext().substring(1,yytext().length())); curr_lineno++; }
					case -66:
						break;
					case 66:
						{ string_buf.append('\b'); }
					case -67:
						break;
					case 67:
						{}
					case -68:
						break;
					case 68:
						{ curr_lineno++; yybegin(YYINITIAL); }
					case -69:
						break;
					case 69:
						{ yybegin(YYINITIAL); }
					case -70:
						break;
					case 70:
						{ curr_lineno++;}
					case -71:
						break;
					case 72:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -72:
						break;
					case 73:
						{return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -73:
						break;
					case 74:
						{return new Symbol(TokenConstants.IN); }
					case -74:
						break;
					case 75:
						{return new Symbol(TokenConstants.IF); }
					case -75:
						break;
					case 76:
						{return new Symbol(TokenConstants.FI); }
					case -76:
						break;
					case 77:
						{return new Symbol(TokenConstants.OF); }
					case -77:
						break;
					case 78:
						{return new Symbol(TokenConstants.LET); }
					case -78:
						break;
					case 79:
						{return new Symbol(TokenConstants.NEW); }
					case -79:
						break;
					case 80:
						{return new Symbol(TokenConstants.NOT); }
					case -80:
						break;
					case 81:
						{return new Symbol(TokenConstants.CASE); }
					case -81:
						break;
					case 82:
						{return new Symbol(TokenConstants.LOOP); }
					case -82:
						break;
					case 83:
						{return new Symbol(TokenConstants.ELSE); }
					case -83:
						break;
					case 84:
						{return new Symbol(TokenConstants.ESAC); }
					case -84:
						break;
					case 85:
						{return new Symbol(TokenConstants.THEN); }
					case -85:
						break;
					case 86:
						{return new Symbol(TokenConstants.POOL); }
					case -86:
						break;
					case 87:
						{return new Symbol(TokenConstants.CLASS); }
					case -87:
						break;
					case 88:
						{return new Symbol(TokenConstants.WHILE); }
					case -88:
						break;
					case 89:
						{return new Symbol(TokenConstants.ISVOID); }
					case -89:
						break;
					case 90:
						{return new Symbol(TokenConstants.INHERITS); }
					case -90:
						break;
					case 91:
						{}
					case -91:
						break;
					case 92:
						{ string_buf.append(yytext()); }
					case -92:
						break;
					case 93:
						{}
					case -93:
						break;
					case 95:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -94:
						break;
					case 96:
						{return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -95:
						break;
					case 97:
						{}
					case -96:
						break;
					case 99:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -97:
						break;
					case 100:
						{return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -98:
						break;
					case 102:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -99:
						break;
					case 103:
						{return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -100:
						break;
					case 104:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -101:
						break;
					case 105:
						{return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -102:
						break;
					case 106:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -103:
						break;
					case 107:
						{return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -104:
						break;
					case 108:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -105:
						break;
					case 109:
						{return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -106:
						break;
					case 110:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -107:
						break;
					case 111:
						{return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -108:
						break;
					case 112:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -109:
						break;
					case 113:
						{return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -110:
						break;
					case 114:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -111:
						break;
					case 115:
						{return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -112:
						break;
					case 116:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -113:
						break;
					case 117:
						{return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -114:
						break;
					case 118:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -115:
						break;
					case 119:
						{return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -116:
						break;
					case 120:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -117:
						break;
					case 121:
						{return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -118:
						break;
					case 122:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -119:
						break;
					case 123:
						{return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -120:
						break;
					case 124:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -121:
						break;
					case 125:
						{return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -122:
						break;
					case 126:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -123:
						break;
					case 127:
						{return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -124:
						break;
					case 128:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -125:
						break;
					case 129:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -126:
						break;
					case 130:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -127:
						break;
					case 131:
						{return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -128:
						break;
					case 132:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -129:
						break;
					case 133:
						{return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -130:
						break;
					case 134:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -131:
						break;
					case 135:
						{return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -132:
						break;
					case 136:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -133:
						break;
					case 137:
						{return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -134:
						break;
					case 138:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -135:
						break;
					case 139:
						{return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -136:
						break;
					case 140:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -137:
						break;
					case 141:
						{return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -138:
						break;
					case 142:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -139:
						break;
					case 143:
						{return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -140:
						break;
					case 144:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -141:
						break;
					case 145:
						{return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -142:
						break;
					case 146:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -143:
						break;
					case 147:
						{return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -144:
						break;
					case 148:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -145:
						break;
					case 149:
						{return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -146:
						break;
					case 150:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -147:
						break;
					case 151:
						{return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -148:
						break;
					case 152:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -149:
						break;
					case 153:
						{return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -150:
						break;
					case 154:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -151:
						break;
					case 155:
						{return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -152:
						break;
					case 156:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -153:
						break;
					case 157:
						{return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -154:
						break;
					case 158:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -155:
						break;
					case 159:
						{return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -156:
						break;
					case 160:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -157:
						break;
					case 161:
						{return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -158:
						break;
					case 162:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -159:
						break;
					case 163:
						{return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -160:
						break;
					case 164:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -161:
						break;
					case 165:
						{return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -162:
						break;
					case 166:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -163:
						break;
					case 167:
						{return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -164:
						break;
					case 168:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -165:
						break;
					case 169:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -166:
						break;
					case 170:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -167:
						break;
					case 171:
						{return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -168:
						break;
					case 172:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -169:
						break;
					case 173:
						{return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -170:
						break;
					case 174:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -171:
						break;
					case 175:
						{return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -172:
						break;
					case 176:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -173:
						break;
					case 177:
						{return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -174:
						break;
					case 178:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -175:
						break;
					case 179:
						{return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -176:
						break;
					case 180:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -177:
						break;
					default:
						yy_error(YY_E_INTERNAL,false);
					case -1:
					}
					yy_initial = true;
					yy_state = yy_state_dtrans[yy_lexical_state];
					yy_next_state = YY_NO_STATE;
					yy_last_accept_state = YY_NO_STATE;
					yy_mark_start();
					yy_this_accept = yy_acpt[yy_state];
					if (YY_NOT_ACCEPT != yy_this_accept) {
						yy_last_accept_state = yy_state;
						yy_mark_end();
					}
				}
			}
		}
	}
}
