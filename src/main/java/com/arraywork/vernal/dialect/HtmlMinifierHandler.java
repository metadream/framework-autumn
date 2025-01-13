package com.arraywork.vernal.dialect;

import java.io.IOException;
import java.io.Writer;
import java.util.regex.Pattern;

import org.thymeleaf.engine.AbstractTemplateHandler;
import org.thymeleaf.model.IModelVisitor;
import org.thymeleaf.model.IText;
import org.thymeleaf.util.StringUtils;

/**
 * Html Minifier Handler
 *
 * @author Marco
 * @copyright ArrayWork Inc.
 * @since 2024/04/25
 */
public class HtmlMinifierHandler extends AbstractTemplateHandler {

    private static final Pattern TAB_OR_NEW_LINE = Pattern.compile("[\\t\\n]+\\s");

    /**
     * 重写文本处理方法
     * 局限：页面上所有以“//”开头的行将被忽略（以解决内联<script>不能使用行级注释问题）
     *
     * @param iText 模板渲染后的纯文本（包括内联JS代码）
     */
    @Override
    public void handleText(IText iText) {
        if (ignorable(iText)) return;

        StringBuilder buffer = new StringBuilder();
        String[] lines = iText.getText().replace("\r", "").split("\n"); // 将文本分割为行

        for (String line : lines) {
            line = line.replaceAll("\\s+", " "); // 如果有多个空格则保留一个
            if (line.trim().startsWith("//")) continue; // 如果单行以//开头则忽略
            buffer.append(line);
        }

        IText text = new SourceText(iText, buffer.toString());
        super.handleText(text);
    }

    /**
     * 判断是否为可忽略的文本（空格、缩进、换行等）
     *
     * @param iText
     * @return
     */
    private boolean ignorable(IText iText) {
        return StringUtils.isEmptyOrWhitespace(iText.getText()) || TAB_OR_NEW_LINE.matcher(iText.getText()).matches();
    }

    /**
     * 文本实体类接口实现
     *
     * @author Marco
     */
    class SourceText implements IText {

        private final IText iText;
        private final String overwriteText;

        public SourceText(IText itext, String overwriteText) {
            this.iText = itext;
            this.overwriteText = overwriteText;
        }

        @Override
        public boolean hasLocation() {
            return iText.hasLocation();
        }

        @Override
        public String getTemplateName() {
            return iText.getTemplateName();
        }

        @Override
        public int getLine() {
            return iText.getLine();
        }

        @Override
        public int getCol() {
            return iText.getCol();
        }

        @Override
        public void accept(IModelVisitor visitor) {
            visitor.visit(this);
        }

        @Override
        public void write(Writer writer) throws IOException {
            writer.write(overwriteText);
        }

        @Override
        public int length() {
            return overwriteText.length();
        }

        @Override
        public char charAt(int index) {
            return overwriteText.charAt(index);
        }

        @Override
        public CharSequence subSequence(int start, int end) {
            return overwriteText.subSequence(start, end);
        }

        @Override
        public String getText() {
            return overwriteText;
        }

    }

}