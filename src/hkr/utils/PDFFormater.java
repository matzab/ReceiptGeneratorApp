package hkr.utils;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Creator: matej
 * Date: 2019-05-08
 * Time: 13:16
 */

public class PDFFormater {


    public static void createRecipePDF(String path, String formattedReceipt) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document,new FileOutputStream(path));
            document.open();
            Paragraph paragraph= new Paragraph();
            paragraph.add(formattedReceipt);
            document.add(paragraph);
            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}