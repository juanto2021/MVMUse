// Generated from Simple.g4 by ANTLR 4.4
package org.tzi.use.gui.mvm;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class MVMInterpreteLexer extends org.antlr.v4.runtime.Lexer {
	static { RuntimeMetaData.checkVersion("4.4", RuntimeMetaData.VERSION); }

	protected static final org.antlr.v4.runtime.dfa.DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__11=1, T__10=2, T__9=3, T__8=4, T__7=5, T__6=6, T__5=7, T__4=8, T__3=9, 
		T__2=10, T__1=11, T__0=12, MODEL=13, ABSTRACT=14, CLASS=15, OPERATIONS=16, 
		ASSOCIATION=17, BETWEEN=18, CONSTRAINTS=19, ATTRIBUTES=20, END=21, CONTEXT=22, 
		INV=23, SINGLE_QUOTE=24, COMMENT=25, COLON=26, Integer=27, Boolean=28, 
		LESSEQUAL=29, GREATEREQUAL=30, LESS=31, GREATER=32, EQUAL=33, NOT_EQUAL=34, 
		XOR=35, PAR_OPEN=36, PAR_CLOSE=37, BRACKET_OPEN=38, BRACKET_CLOSE=39, 
		IMPLIES=40, AND=41, OR=42, SET=43, ASTERISK=44, ALGOPERATOR=45, Identifier=46, 
		NL=47, WS=48, TAB=49;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"'\\u0000'", "'\\u0001'", "'\\u0002'", "'\\u0003'", "'\\u0004'", "'\\u0005'", 
		"'\\u0006'", "'\\u0007'", "'\b'", "'\t'", "'\n'", "'\\u000B'", "'\f'", 
		"'\r'", "'\\u000E'", "'\\u000F'", "'\\u0010'", "'\\u0011'", "'\\u0012'", 
		"'\\u0013'", "'\\u0014'", "'\\u0015'", "'\\u0016'", "'\\u0017'", "'\\u0018'", 
		"'\\u0019'", "'\\u001A'", "'\\u001B'", "'\\u001C'", "'\\u001D'", "'\\u001E'", 
		"'\\u001F'", "' '", "'!'", "'\"'", "'#'", "'$'", "'%'", "'&'", "'''", 
		"'('", "')'", "'*'", "'+'", "','", "'-'", "'.'", "'/'", "'0'", "'1'"
	};
	public static final String[] ruleNames = {
		"T__11", "T__10", "T__9", "T__8", "T__7", "T__6", "T__5", "T__4", "T__3", 
		"T__2", "T__1", "T__0", "MODEL", "ABSTRACT", "CLASS", "OPERATIONS", "ASSOCIATION", 
		"BETWEEN", "CONSTRAINTS", "ATTRIBUTES", "END", "CONTEXT", "INV", "SINGLE_QUOTE", 
		"COMMENT", "COLON", "Integer", "Boolean", "LESSEQUAL", "GREATEREQUAL", 
		"LESS", "GREATER", "EQUAL", "NOT_EQUAL", "XOR", "PAR_OPEN", "PAR_CLOSE", 
		"BRACKET_OPEN", "BRACKET_CLOSE", "IMPLIES", "AND", "OR", "SET", "ASTERISK", 
		"ALGOPERATOR", "Identifier", "NL", "WS", "TAB", "CR", "LF"
	};


	public MVMInterpreteLexer(org.antlr.v4.runtime.CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Simple.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public org.antlr.v4.runtime.atn.ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\63\u0166\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t"+
		"+\4,\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64"+
		"\t\64\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\4"+
		"\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3"+
		"\7\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\13\3\13\3\13\3\13"+
		"\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16"+
		"\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20"+
		"\3\20\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\22\3\22"+
		"\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\23"+
		"\3\23\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24"+
		"\3\24\3\24\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\26"+
		"\3\26\3\26\3\26\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\30\3\30\3\30"+
		"\3\30\3\31\3\31\3\32\3\32\3\32\3\32\7\32\u0107\n\32\f\32\16\32\u010a\13"+
		"\32\3\32\3\32\3\33\3\33\3\34\6\34\u0111\n\34\r\34\16\34\u0112\3\35\3\35"+
		"\3\35\3\35\3\35\3\35\3\35\3\35\3\35\5\35\u011e\n\35\3\36\3\36\3\36\3\37"+
		"\3\37\3\37\3 \3 \3!\3!\3\"\3\"\3#\3#\3#\3$\3$\3$\3$\3%\3%\3&\3&\3\'\3"+
		"\'\3(\3(\3)\3)\3)\3)\3)\3)\3)\3)\3)\5)\u0144\n)\3*\3*\3*\3*\3+\3+\3+\3"+
		",\3,\3,\3,\3-\3-\3.\3.\3/\6/\u0156\n/\r/\16/\u0157\3\60\5\60\u015b\n\60"+
		"\3\60\3\60\3\61\3\61\3\62\3\62\3\63\3\63\3\64\3\64\2\2\65\3\3\5\4\7\5"+
		"\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23"+
		"%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37= ?!A\"C#E$G"+
		"%I&K\'M(O)Q*S+U,W-Y.[/]\60_\61a\62c\63e\2g\2\3\2\7\4\2\f\f\17\17\3\2\62"+
		";\3\2,\61\6\2\62;B\\aac|\5\2\13\13\"\"))\u0169\2\3\3\2\2\2\2\5\3\2\2\2"+
		"\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3"+
		"\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2"+
		"\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2"+
		"\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2"+
		"\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2"+
		"\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2"+
		"\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y"+
		"\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\3i\3\2"+
		"\2\2\5q\3\2\2\2\7v\3\2\2\2\t{\3\2\2\2\13\u0080\3\2\2\2\r\u0085\3\2\2\2"+
		"\17\u008b\3\2\2\2\21\u008e\3\2\2\2\23\u0095\3\2\2\2\25\u0097\3\2\2\2\27"+
		"\u009b\3\2\2\2\31\u00a3\3\2\2\2\33\u00a5\3\2\2\2\35\u00ab\3\2\2\2\37\u00b4"+
		"\3\2\2\2!\u00ba\3\2\2\2#\u00c5\3\2\2\2%\u00d1\3\2\2\2\'\u00d9\3\2\2\2"+
		")\u00e5\3\2\2\2+\u00f0\3\2\2\2-\u00f4\3\2\2\2/\u00fc\3\2\2\2\61\u0100"+
		"\3\2\2\2\63\u0102\3\2\2\2\65\u010d\3\2\2\2\67\u0110\3\2\2\29\u011d\3\2"+
		"\2\2;\u011f\3\2\2\2=\u0122\3\2\2\2?\u0125\3\2\2\2A\u0127\3\2\2\2C\u0129"+
		"\3\2\2\2E\u012b\3\2\2\2G\u012e\3\2\2\2I\u0132\3\2\2\2K\u0134\3\2\2\2M"+
		"\u0136\3\2\2\2O\u0138\3\2\2\2Q\u0143\3\2\2\2S\u0145\3\2\2\2U\u0149\3\2"+
		"\2\2W\u014c\3\2\2\2Y\u0150\3\2\2\2[\u0152\3\2\2\2]\u0155\3\2\2\2_\u015a"+
		"\3\2\2\2a\u015e\3\2\2\2c\u0160\3\2\2\2e\u0162\3\2\2\2g\u0164\3\2\2\2i"+
		"j\7D\2\2jk\7q\2\2kl\7q\2\2lm\7n\2\2mn\7g\2\2no\7c\2\2op\7p\2\2p\4\3\2"+
		"\2\2qr\7t\2\2rs\7q\2\2st\7n\2\2tu\7g\2\2u\6\3\2\2\2vw\7T\2\2wx\7g\2\2"+
		"xy\7c\2\2yz\7n\2\2z\b\3\2\2\2{|\7r\2\2|}\7q\2\2}~\7u\2\2~\177\7v\2\2\177"+
		"\n\3\2\2\2\u0080\u0081\7B\2\2\u0081\u0082\7r\2\2\u0082\u0083\7t\2\2\u0083"+
		"\u0084\7g\2\2\u0084\f\3\2\2\2\u0085\u0086\7B\2\2\u0086\u0087\7r\2\2\u0087"+
		"\u0088\7q\2\2\u0088\u0089\7u\2\2\u0089\u008a\7v\2\2\u008a\16\3\2\2\2\u008b"+
		"\u008c\7\60\2\2\u008c\u008d\7\60\2\2\u008d\20\3\2\2\2\u008e\u008f\7U\2"+
		"\2\u008f\u0090\7v\2\2\u0090\u0091\7t\2\2\u0091\u0092\7k\2\2\u0092\u0093"+
		"\7p\2\2\u0093\u0094\7i\2\2\u0094\22\3\2\2\2\u0095\u0096\7~\2\2\u0096\24"+
		"\3\2\2\2\u0097\u0098\7r\2\2\u0098\u0099\7t\2\2\u0099\u009a\7g\2\2\u009a"+
		"\26\3\2\2\2\u009b\u009c\7K\2\2\u009c\u009d\7p\2\2\u009d\u009e\7v\2\2\u009e"+
		"\u009f\7g\2\2\u009f\u00a0\7i\2\2\u00a0\u00a1\7g\2\2\u00a1\u00a2\7t\2\2"+
		"\u00a2\30\3\2\2\2\u00a3\u00a4\7\60\2\2\u00a4\32\3\2\2\2\u00a5\u00a6\7"+
		"o\2\2\u00a6\u00a7\7q\2\2\u00a7\u00a8\7f\2\2\u00a8\u00a9\7g\2\2\u00a9\u00aa"+
		"\7n\2\2\u00aa\34\3\2\2\2\u00ab\u00ac\7c\2\2\u00ac\u00ad\7d\2\2\u00ad\u00ae"+
		"\7u\2\2\u00ae\u00af\7v\2\2\u00af\u00b0\7t\2\2\u00b0\u00b1\7c\2\2\u00b1"+
		"\u00b2\7e\2\2\u00b2\u00b3\7v\2\2\u00b3\36\3\2\2\2\u00b4\u00b5\7e\2\2\u00b5"+
		"\u00b6\7n\2\2\u00b6\u00b7\7c\2\2\u00b7\u00b8\7u\2\2\u00b8\u00b9\7u\2\2"+
		"\u00b9 \3\2\2\2\u00ba\u00bb\7q\2\2\u00bb\u00bc\7r\2\2\u00bc\u00bd\7g\2"+
		"\2\u00bd\u00be\7t\2\2\u00be\u00bf\7c\2\2\u00bf\u00c0\7v\2\2\u00c0\u00c1"+
		"\7k\2\2\u00c1\u00c2\7q\2\2\u00c2\u00c3\7p\2\2\u00c3\u00c4\7u\2\2\u00c4"+
		"\"\3\2\2\2\u00c5\u00c6\7c\2\2\u00c6\u00c7\7u\2\2\u00c7\u00c8\7u\2\2\u00c8"+
		"\u00c9\7q\2\2\u00c9\u00ca\7e\2\2\u00ca\u00cb\7k\2\2\u00cb\u00cc\7c\2\2"+
		"\u00cc\u00cd\7v\2\2\u00cd\u00ce\7k\2\2\u00ce\u00cf\7q\2\2\u00cf\u00d0"+
		"\7p\2\2\u00d0$\3\2\2\2\u00d1\u00d2\7d\2\2\u00d2\u00d3\7g\2\2\u00d3\u00d4"+
		"\7v\2\2\u00d4\u00d5\7y\2\2\u00d5\u00d6\7g\2\2\u00d6\u00d7\7g\2\2\u00d7"+
		"\u00d8\7p\2\2\u00d8&\3\2\2\2\u00d9\u00da\7e\2\2\u00da\u00db\7q\2\2\u00db"+
		"\u00dc\7p\2\2\u00dc\u00dd\7u\2\2\u00dd\u00de\7v\2\2\u00de\u00df\7t\2\2"+
		"\u00df\u00e0\7c\2\2\u00e0\u00e1\7k\2\2\u00e1\u00e2\7p\2\2\u00e2\u00e3"+
		"\7v\2\2\u00e3\u00e4\7u\2\2\u00e4(\3\2\2\2\u00e5\u00e6\7c\2\2\u00e6\u00e7"+
		"\7v\2\2\u00e7\u00e8\7v\2\2\u00e8\u00e9\7t\2\2\u00e9\u00ea\7k\2\2\u00ea"+
		"\u00eb\7d\2\2\u00eb\u00ec\7w\2\2\u00ec\u00ed\7v\2\2\u00ed\u00ee\7g\2\2"+
		"\u00ee\u00ef\7u\2\2\u00ef*\3\2\2\2\u00f0\u00f1\7g\2\2\u00f1\u00f2\7p\2"+
		"\2\u00f2\u00f3\7f\2\2\u00f3,\3\2\2\2\u00f4\u00f5\7e\2\2\u00f5\u00f6\7"+
		"q\2\2\u00f6\u00f7\7p\2\2\u00f7\u00f8\7v\2\2\u00f8\u00f9\7g\2\2\u00f9\u00fa"+
		"\7z\2\2\u00fa\u00fb\7v\2\2\u00fb.\3\2\2\2\u00fc\u00fd\7k\2\2\u00fd\u00fe"+
		"\7p\2\2\u00fe\u00ff\7x\2\2\u00ff\60\3\2\2\2\u0100\u0101\7)\2\2\u0101\62"+
		"\3\2\2\2\u0102\u0103\7/\2\2\u0103\u0104\7/\2\2\u0104\u0108\3\2\2\2\u0105"+
		"\u0107\n\2\2\2\u0106\u0105\3\2\2\2\u0107\u010a\3\2\2\2\u0108\u0106\3\2"+
		"\2\2\u0108\u0109\3\2\2\2\u0109\u010b\3\2\2\2\u010a\u0108\3\2\2\2\u010b"+
		"\u010c\5_\60\2\u010c\64\3\2\2\2\u010d\u010e\7<\2\2\u010e\66\3\2\2\2\u010f"+
		"\u0111\t\3\2\2\u0110\u010f\3\2\2\2\u0111\u0112\3\2\2\2\u0112\u0110\3\2"+
		"\2\2\u0112\u0113\3\2\2\2\u01138\3\2\2\2\u0114\u0115\7v\2\2\u0115\u0116"+
		"\7t\2\2\u0116\u0117\7w\2\2\u0117\u011e\7g\2\2\u0118\u0119\7h\2\2\u0119"+
		"\u011a\7c\2\2\u011a\u011b\7n\2\2\u011b\u011c\7u\2\2\u011c\u011e\7g\2\2"+
		"\u011d\u0114\3\2\2\2\u011d\u0118\3\2\2\2\u011e:\3\2\2\2\u011f\u0120\7"+
		">\2\2\u0120\u0121\7?\2\2\u0121<\3\2\2\2\u0122\u0123\7@\2\2\u0123\u0124"+
		"\7?\2\2\u0124>\3\2\2\2\u0125\u0126\7>\2\2\u0126@\3\2\2\2\u0127\u0128\7"+
		"@\2\2\u0128B\3\2\2\2\u0129\u012a\7?\2\2\u012aD\3\2\2\2\u012b\u012c\7>"+
		"\2\2\u012c\u012d\7@\2\2\u012dF\3\2\2\2\u012e\u012f\7z\2\2\u012f\u0130"+
		"\7q\2\2\u0130\u0131\7t\2\2\u0131H\3\2\2\2\u0132\u0133\7*\2\2\u0133J\3"+
		"\2\2\2\u0134\u0135\7+\2\2\u0135L\3\2\2\2\u0136\u0137\7]\2\2\u0137N\3\2"+
		"\2\2\u0138\u0139\7_\2\2\u0139P\3\2\2\2\u013a\u013b\7/\2\2\u013b\u0144"+
		"\7@\2\2\u013c\u013d\7k\2\2\u013d\u013e\7o\2\2\u013e\u013f\7r\2\2\u013f"+
		"\u0140\7n\2\2\u0140\u0141\7k\2\2\u0141\u0142\7g\2\2\u0142\u0144\7u\2\2"+
		"\u0143\u013a\3\2\2\2\u0143\u013c\3\2\2\2\u0144R\3\2\2\2\u0145\u0146\7"+
		"c\2\2\u0146\u0147\7p\2\2\u0147\u0148\7f\2\2\u0148T\3\2\2\2\u0149\u014a"+
		"\7q\2\2\u014a\u014b\7t\2\2\u014bV\3\2\2\2\u014c\u014d\7U\2\2\u014d\u014e"+
		"\7g\2\2\u014e\u014f\7v\2\2\u014fX\3\2\2\2\u0150\u0151\7,\2\2\u0151Z\3"+
		"\2\2\2\u0152\u0153\t\4\2\2\u0153\\\3\2\2\2\u0154\u0156\t\5\2\2\u0155\u0154"+
		"\3\2\2\2\u0156\u0157\3\2\2\2\u0157\u0155\3\2\2\2\u0157\u0158\3\2\2\2\u0158"+
		"^\3\2\2\2\u0159\u015b\7\17\2\2\u015a\u0159\3\2\2\2\u015a\u015b\3\2\2\2"+
		"\u015b\u015c\3\2\2\2\u015c\u015d\7\f\2\2\u015d`\3\2\2\2\u015e\u015f\t"+
		"\6\2\2\u015fb\3\2\2\2\u0160\u0161\7\13\2\2\u0161d\3\2\2\2\u0162\u0163"+
		"\7\17\2\2\u0163f\3\2\2\2\u0164\u0165\7\f\2\2\u0165h\3\2\2\2\t\2\u0108"+
		"\u0112\u011d\u0143\u0157\u015a\2";
	public static final org.antlr.v4.runtime.atn.ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new org.antlr.v4.runtime.dfa.DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new org.antlr.v4.runtime.dfa.DFA(_ATN.getDecisionState(i), i);
		}
	}
}