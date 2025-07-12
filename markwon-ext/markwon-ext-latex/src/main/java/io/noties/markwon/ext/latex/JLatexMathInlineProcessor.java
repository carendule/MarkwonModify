package io.noties.markwon.ext.latex;

import android.util.Log;

import androidx.annotation.Nullable;

import org.commonmark.node.Node;

import java.util.regex.Pattern;

import io.noties.markwon.inlineparser.InlineProcessor;

/**
 * @since 4.3.0
 */
class JLatexMathInlineProcessor extends InlineProcessor {

    @LatexParseStyle
    private final int parseStyle;

    public JLatexMathInlineProcessor(@LatexParseStyle int parseStyle) {
        this.parseStyle = parseStyle;
    }
//    private static final Pattern RE4StyleDollar = Pattern.compile("(\\${2})(.+?)\\1");
    private static final Pattern RE4StyleDollar = Pattern.compile("(?<!\\\\)(\\$+)(.+?)(?<!\\\\)\\1");
    private static final Pattern RE4StyleBracket = Pattern.compile("\\\\\\(([\\s\\S]+?)\\\\\\)|\\\\\\[([\\s\\S]+?)\\\\\\]");

    @Override
    public char specialCharacter() {
        return mapStyle2SpecialChar();
    }

    @Nullable
    @Override
    protected Node parse() {

        final String latex = match(mapStyle2Pattern());
        int styleLength;
        if (parseStyle == LatexParseStyle.STYLE_DOLLAR) {
            styleLength = 1;
        }else {
            styleLength = 2;
        }
        if (latex == null || latex.length() < styleLength * 2) {
            return null;
        }
//        Log.d("conor.latexMatcher",latex + " ,after:" + after);
        return new JLatexMathNode(latex.substring(styleLength, latex.length() - styleLength));
    }

    private Pattern mapStyle2Pattern(){
        if (parseStyle == LatexParseStyle.STYLE_DOLLAR) {
            return RE4StyleDollar;
        }
        return RE4StyleBracket;
    }

    private char mapStyle2SpecialChar(){
        if (parseStyle == LatexParseStyle.STYLE_BRACKETS) {
            return '\\';
        }
        return '$';
    }
}
