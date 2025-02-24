package com.arraywork.autumn.helper;

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
 * (Depends on com.itextpdf)
 *
 * @author Marco
 * @copyright ArrayWork Inc.
 * @since 2024/11/12
 */
public class PdfDocument extends Document {

    // Fonts
    public static String fontSrc = "static/assets/Ali-PuHuiTi.ttf";
    private final Font titleFont;
    private final Font subtitleFont;
    private final Font headingFont;
    private final Font fieldFont;
    private final Font textFont;

    // Colors
    private final BaseColor accentColor = new BaseColor(250, 80, 80);
    private final BaseColor textColor = new BaseColor(100, 100, 100);
    private final BaseColor borderColor = new BaseColor(235, 235, 235);
    private final BaseColor bgColor = new BaseColor(240, 240, 240);
    private final BaseFont baseFont;

    private final PdfWriter writer;

    /** Construction */
    public PdfDocument(Rectangle pageSize, OutputStream outputStream) throws DocumentException, IOException {
        super(pageSize);
        writer = PdfWriter.getInstance(this, outputStream);

        // There seems to be no difference between using BaseFont.EMBEDDED and BaseFont.UNEMBEDDED,
        // the result is always embedding a subset.
        baseFont = BaseFont.createFont(fontSrc, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        titleFont = new Font(baseFont, 18);
        subtitleFont = new Font(baseFont, 10, Font.NORMAL, accentColor);
        headingFont = new Font(baseFont, 12);
        fieldFont = new Font(baseFont, 10);
        textFont = new Font(baseFont, 10, Font.NORMAL, textColor);
    }

    /** Document encryption (must be called before opening, depends on org.bouncycastle.bcprov-jdk18on) */
    public void addEncryption(String userPassword, String ownerPassword) throws DocumentException {
        writer.setEncryption(userPassword.getBytes(), ownerPassword.getBytes(),
            PdfWriter.ALLOW_PRINTING, PdfWriter.ENCRYPTION_AES_128 | PdfWriter.DO_NOT_ENCRYPT_METADATA);
    }

    /** Add text watermark (must be called before opening) */
    public void addWatermark(String watermark) {
        writer.setPageEvent(new PdfWatermarkEvent(watermark));
    }

    /** Add main title */
    public boolean addMainTitle(String title) throws DocumentException {
        Paragraph paragraph = new Paragraph(title, titleFont);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        return add(paragraph);
    }

    /** Add subtitle */
    public boolean addSubtitle(String subtitle) throws DocumentException {
        Paragraph paragraph = new Paragraph(subtitle, subtitleFont);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.setSpacingBefore(6);
        return add(paragraph);
    }

    /** Add subheading (generated using a table due to the style of leading vertical bar) */
    public boolean addHeading(String heading) throws DocumentException {
        PdfTable table = new PdfTable(1);
        table.setWidthPercentage(100);
        table.setSpacingBefore(20);
        table.setSpacingAfter(10);

        PdfPCell cell = new PdfPCell();
        cell.disableBorderSide(11); // Display only the left border
        cell.setLeading(0, .5f);    // Line spacing of 0.5 times
        cell.setPaddingTop(4);      // Eliminate vertical deviation of text not being centered
        cell.setPaddingLeft(8);     // Eliminate vertical deviation of text not being centered
        cell.setBorderWidth(1);
        cell.setBorderColor(accentColor);
        cell.setPhrase(new Phrase(heading, headingFont));
        table.addCell(cell);
        return add(table);
    }

    /** Create a table */
    public PdfTable createTable(int columns) throws DocumentException {
        return createTable(columns, null);
    }

    /** Create a table (widths represent the allocation ratio of column widths) */
    public PdfTable createTable(int columns, int[] widths) throws DocumentException {
        PdfTable table = new PdfTable(columns);
        table.setSplitLate(false);  // By default, a cell that spans multiple pages will be moved entirely to the next page.
        table.setSplitRows(true);   // These two settings will split the cell across two pages.
        table.setWidthPercentage(100);
        table.setSpacingBefore(5);
        table.setSpacingAfter(5);

        if (widths != null) table.setWidths(widths);
        return table;
    }

    /***
     * PDF table extension class
     * to set default cell styles and provide shortcut methods.
     */
    public class PdfTable extends PdfPTable {

        public PdfTable() {
            super();
        }

        public PdfTable(int columns) {
            super(columns);
        }

        /** Add field cell */
        public PdfPCell addFieldCell(String text) {
            return addCell(text, fieldFont, bgColor, Element.ALIGN_CENTER, null);
        }

        /** Add text cell (horizontally centered) */
        public PdfPCell addTextCell(String text) {
            return addTextCell(text, null);
        }

        /** Add text cell (horizontally centered, can span columns) */
        public PdfPCell addTextCell(String text, Integer colspan) {
            return addCell(text, textFont, null, Element.ALIGN_CENTER, colspan);
        }

        /** Add a cell */
        public PdfPCell addCell(String text, Font font, BaseColor bgColor, Integer hAlign, Integer colspan) {
            PdfPCell cell = new PdfPCell();
            cell.setPhrase(new Phrase(text, font));
            cell.setBorderColor(borderColor);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(6);         // By default, the text is positioned lower and not vertically centered.
            cell.setPaddingBottom(9);   // Therefore, there is a difference in the vertical spacing settings here.

            if (bgColor != null) cell.setBackgroundColor(bgColor);
            if (hAlign != null) cell.setHorizontalAlignment(hAlign);
            if (colspan != null) cell.setColspan(colspan);
            return super.addCell(cell);
        }

    }

    /**
     * Page event listener class to add text watermark
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

            // Fill the watermark
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