package hkr.utils;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.IOException;


public class PDFFormater {

    public static void createRecipePdf(String path, String formattedReceipt) throws DocumentException, IOException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(path));
        document.open();
        Paragraph paragraph = new Paragraph();
        paragraph.add(formattedReceipt);
        document.add(paragraph);
        document.close();
    }

    public static Document returnRecipePdfFile(String formattedReceipt, FileOutputStream fileOutputStream) throws DocumentException, IOException {
        Document document = new Document();
        PdfWriter.getInstance(document, fileOutputStream);
        document.open();
        Paragraph paragraph = new Paragraph();
        paragraph.add(formattedReceipt);
        document.add(paragraph);
        document.close();
        return document;
    }
}
