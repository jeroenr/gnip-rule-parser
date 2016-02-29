package com.github.jeroenr.gnip.rule.validation

import fastparse.WhitespaceApi
import scala.language.postfixOps

/**
 * Created by jero on 3-2-16.
 */
class GnipRuleParser {
  val White = WhitespaceApi.Wrapper {
    import fastparse.all._
    NoTrace(" ".rep)
  }
  import fastparse.noApi._
  import White._

  private val keyword = P(CharIn('a' to 'z')!)
  private val maybeNegatedKeyword = P((("-"?) ~~ keyword)!)

  private val keywordGroupWithoutOrClause = P((maybeNegatedKeyword | (("-"?) ~~ keywordsInParentheses))!)
  private val keywordGroup = P(orClause | keywordGroupWithoutOrClause)

  private def keywordsInParentheses = P("(" ~ gnipKeywordPhrase ~ ")")
  private def orClause = P(!(("-" ~~ (keywordGroupWithoutOrClause.rep(min = 1))) ~ "OR") ~ keywordGroupWithoutOrClause ~ ("OR"!) ~ gnipKeywordPhrase)
  private def gnipKeywordPhrase: Parser[String] = P((keywordGroup.rep(min = 1))!)

  def parse(rule: String) = P(Start ~ gnipKeywordPhrase ~ End).parse(rule)
}

object GnipRuleValidator {
  import fastparse.core.Parsed._
  import fastparse.core.ParseError

  def apply(rule: String) = (new GnipRuleParser).parse(rule) match {
    case Success(matched, index) => scala.util.Success(matched)
    case f:Failure => scala.util.Failure(ParseError(f))
  }
}
