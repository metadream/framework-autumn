package com.arraywork.vernal.helper;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import javax.swing.*;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEvent;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * PDF document encapsulation class
 * Depends on com.itextpdf
 *
 * @author Marco
 * @copyright ArrayWork Inc.
 * @since 2024/11/12
 */
public class PdfDocument extends Document {

    // 字体
    public static String fontSrc = "static/assets/Ali-PuHuiTi.ttf";
    private final Font titleFont;
    private final Font subtitleFont;
    private final Font headingFont;
    private final Font fieldFont;
    private final Font textFont;

    // 颜色
    private final BaseColor accentColor = new BaseColor(250, 80, 80);
    private final BaseColor textColor = new BaseColor(100, 100, 100);
    private final BaseColor borderColor = new BaseColor(235, 235, 235);
    private final BaseColor bgColor = new BaseColor(240, 240, 240);
    private final BaseFont baseFont;

    private final PdfWriter writer;

    /** 构造方法 */
    public PdfDocument(Rectangle pageSize, OutputStream outputStream) throws DocumentException, IOException {
        super(pageSize);
        writer = PdfWriter.getInstance(this, outputStream);

        // 使用 BaseFont.EMBEDDED 或 UNEMBEDDED 似乎没区别，结果都是嵌入子集
        baseFont = BaseFont.createFont(fontSrc, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        titleFont = new Font(baseFont, 18);
        subtitleFont = new Font(baseFont, 10, Font.NORMAL, accentColor);
        headingFont = new Font(baseFont, 12);
        fieldFont = new Font(baseFont, 10);
        textFont = new Font(baseFont, 10, Font.NORMAL, textColor);
    }

    /** 文档加密 (必须在open之前调用，依赖于org.bouncycastle.bcprov-jdk18on) */
    public void addEncryption(String userPassword, String ownerPassword) throws DocumentException {
        writer.setEncryption(userPassword.getBytes(), ownerPassword.getBytes(),
            PdfWriter.ALLOW_PRINTING, PdfWriter.ENCRYPTION_AES_128 | PdfWriter.DO_NOT_ENCRYPT_METADATA);
    }

    /** 添加文字水印 (必须在open之前调用) */
    public void addWatermark(String watermark) {
        writer.setPageEvent(new PdfWatermarkEvent(watermark));
    }

    /** 添加主标题 */
    public boolean addMainTitle(String title) throws DocumentException {
        Paragraph paragraph = new Paragraph(title, titleFont);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        return add(paragraph);
    }

    /** 添加副标题 */
    public boolean addSubtitle(String subtitle) throws DocumentException {
        Paragraph paragraph = new Paragraph(subtitle, subtitleFont);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.setSpacingBefore(6);
        return add(paragraph);
    }

    /** 添加小标题 (由于带前导竖线故使用表格生成) */
    public boolean addHeading(String heading) throws DocumentException {
        PdfTable table = new PdfTable(1);
        table.setWidthPercentage(100);
        table.setSpacingBefore(20);
        table.setSpacingAfter(10);

        PdfPCell cell = new PdfPCell();
        cell.disableBorderSide(11); // 仅显示左边框
        cell.setLeading(0, .5f);    // 行间距0.5倍
        cell.setPaddingTop(4);      // 消除文字不居中的上下偏差
        cell.setPaddingLeft(8);     // 消除文字不居中的上下偏差
        cell.setBorderWidth(1);
        cell.setBorderColor(accentColor);
        cell.setPhrase(new Phrase(heading, headingFont));
        table.addCell(cell);
        return add(table);
    }

    /** 创建表格 */
    public PdfTable createTable(int columns) throws DocumentException {
        return createTable(columns, null);
    }

    /** 创建表格 (widths为各列宽分配比例) */
    public PdfTable createTable(int columns, int[] widths) throws DocumentException {
        PdfTable table = new PdfTable(columns);
        table.setSplitLate(false);  // 默认如果单元格跨页会整个移动到下一个页面
        table.setSplitRows(true);   // 此两项设置会分割单元格到两个页面上
        table.setWidthPercentage(100);
        table.setSpacingBefore(5);
        table.setSpacingAfter(5);

        if (widths != null) table.setWidths(widths);
        return table;
    }

    /***
     * PDF表格扩展类
     * 设置默认单元格样式、提供快捷方法
     */
    public class PdfTable extends PdfPTable {

        public PdfTable() {
            super();
        }

        public PdfTable(int columns) {
            super(columns);
        }

        /** 添加域单元格 */
        public PdfPCell addFieldCell(String text) {
            return addCell(text, fieldFont, bgColor, Element.ALIGN_CENTER, null);
        }

        /** 添加文字单元格（水平居中） */
        public PdfPCell addTextCell(String text) {
            return addTextCell(text, null);
        }

        /** 添加文字单元格（水平居中，可合并列） */
        public PdfPCell addTextCell(String text, Integer colspan) {
            return addCell(text, textFont, null, Element.ALIGN_CENTER, colspan);
        }

        /** 添加单元格 */
        public PdfPCell addCell(String text, Font font, BaseColor bgColor, Integer hAlign, Integer colspan) {
            PdfPCell cell = new PdfPCell();
            cell.setPhrase(new Phrase(text, font));
            cell.setBorderColor(borderColor);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(6);         // 默认情况下文字位置偏下，在垂直方向上未能居中
            cell.setPaddingBottom(9);   // 故上下间隔设置有差异

            if (bgColor != null) cell.setBackgroundColor(bgColor);
            if (hAlign != null) cell.setHorizontalAlignment(hAlign);
            if (colspan != null) cell.setColspan(colspan);
            return super.addCell(cell);
        }

    }

    /**
     * 页面事件监听类
     * 添加文字水印
     */
    class PdfWatermarkEvent implements PdfPageEvent {

        private final String watermark;

        public PdfWatermarkEvent(String watermark) {
            this.watermark = watermark;
        }

        @Override
        public void onOpenDocument(PdfWriter pdfWriter, Document document) { }

        // 添加文字水印
        @Override
        public void onStartPage(PdfWriter pdfWriter, Document document) {
            JLabel label = new JLabel();
            label.setText(watermark);

            FontMetrics metrics = label.getFontMetrics(label.getFont());
            int textHeight = metrics.getHeight();
            int textWidth = metrics.stringWidth(label.getText());

            PdfGState gs = new PdfGState();
            gs.setFillOpacity(.1f);
            gs.setStrokeOpacity(.1f);

            Rectangle pageRect = pdfWriter.getPageSize();
            PdfContentByte under = pdfWriter.getDirectContentUnder();
            under.saveState();
            under.setGState(gs);
            under.beginText();
            under.setFontAndSize(baseFont, 20);
            under.setColorFill(textColor);

            // 填充水印
            for (int height = textHeight; height < pageRect.getHeight(); height = height + textHeight * 8) {
                for (int width = textWidth; width < pageRect.getWidth(); width = width + textWidth * 3) {
                    under.showTextAligned(Element.ALIGN_LEFT, watermark, width - textWidth, height - textHeight, 30);
                }
            }
            under.endText();
            under.restoreState();
        }

        @Override
        public void onEndPage(PdfWriter pdfWriter, Document document) { }

        @Override
        public void onCloseDocument(PdfWriter pdfWriter, Document document) { }

        @Override
        public void onParagraph(PdfWriter pdfWriter, Document document, float v) { }

        @Override
        public void onParagraphEnd(PdfWriter pdfWriter, Document document, float v) { }

        @Override
        public void onChapter(PdfWriter pdfWriter, Document document, float v, Paragraph paragraph) { }

        @Override
        public void onChapterEnd(PdfWriter pdfWriter, Document document, float v) { }

        @Override
        public void onSection(PdfWriter pdfWriter, Document document, float v, int i, Paragraph paragraph) { }

        @Override
        public void onSectionEnd(PdfWriter pdfWriter, Document document, float v) { }

        @Override
        public void onGenericTag(PdfWriter pdfWriter, Document document, Rectangle rectangle, String s) { }
    }

}