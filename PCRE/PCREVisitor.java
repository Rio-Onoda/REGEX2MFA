// Generated from PCRE.g4 by ANTLR 4.13.2
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link PCREParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface PCREVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link PCREParser#pcre}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPcre(PCREParser.PcreContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#alternation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAlternation(PCREParser.AlternationContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(PCREParser.ExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#element}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElement(PCREParser.ElementContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAtom(PCREParser.AtomContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#capture}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCapture(PCREParser.CaptureContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#atomic_group}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAtomic_group(PCREParser.Atomic_groupContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#lookaround}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLookaround(PCREParser.LookaroundContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#backreference}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBackreference(PCREParser.BackreferenceContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#subroutine_reference}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubroutine_reference(PCREParser.Subroutine_referenceContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#conditional_pattern}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConditional_pattern(PCREParser.Conditional_patternContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#comment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComment(PCREParser.CommentContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#quantifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQuantifier(PCREParser.QuantifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#option_setting}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOption_setting(PCREParser.Option_settingContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#option_setting_flag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOption_setting_flag(PCREParser.Option_setting_flagContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#backtracking_control}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBacktracking_control(PCREParser.Backtracking_controlContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#callout}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCallout(PCREParser.CalloutContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#newline_conventions}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNewline_conventions(PCREParser.Newline_conventionsContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#character}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCharacter(PCREParser.CharacterContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#character_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCharacter_type(PCREParser.Character_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#character_class}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCharacter_class(PCREParser.Character_classContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#character_class_atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCharacter_class_atom(PCREParser.Character_class_atomContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#character_class_range}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCharacter_class_range(PCREParser.Character_class_rangeContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#character_class_range_atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCharacter_class_range_atom(PCREParser.Character_class_range_atomContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#posix_character_class}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPosix_character_class(PCREParser.Posix_character_classContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#anchor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnchor(PCREParser.AnchorContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#match_point_reset}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMatch_point_reset(PCREParser.Match_point_resetContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#quoting}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQuoting(PCREParser.QuotingContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#digits}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDigits(PCREParser.DigitsContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#digit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDigit(PCREParser.DigitContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#hex}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHex(PCREParser.HexContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#letters}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLetters(PCREParser.LettersContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#letter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLetter(PCREParser.LetterContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitName(PCREParser.NameContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#other}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOther(PCREParser.OtherContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#utf}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUtf(PCREParser.UtfContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#ucp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUcp(PCREParser.UcpContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#no_auto_possess}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNo_auto_possess(PCREParser.No_auto_possessContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#no_start_opt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNo_start_opt(PCREParser.No_start_optContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#cr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCr(PCREParser.CrContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#lf}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLf(PCREParser.LfContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#crlf}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCrlf(PCREParser.CrlfContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#anycrlf}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnycrlf(PCREParser.AnycrlfContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#any}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAny(PCREParser.AnyContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#limit_match}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLimit_match(PCREParser.Limit_matchContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#limit_recursion}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLimit_recursion(PCREParser.Limit_recursionContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#bsr_anycrlf}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBsr_anycrlf(PCREParser.Bsr_anycrlfContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#bsr_unicode}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBsr_unicode(PCREParser.Bsr_unicodeContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#accept_}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAccept_(PCREParser.Accept_Context ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#fail}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFail(PCREParser.FailContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#mark}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMark(PCREParser.MarkContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#commit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCommit(PCREParser.CommitContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#prune}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrune(PCREParser.PruneContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#skip}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSkip(PCREParser.SkipContext ctx);
	/**
	 * Visit a parse tree produced by {@link PCREParser#then}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitThen(PCREParser.ThenContext ctx);
}