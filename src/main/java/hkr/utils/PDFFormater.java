package hkr.utils;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class PDFFormater {

    public static File returnRecipePdfFile(String formattedReceipt, String tmpLoc)  {
        try{
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(tmpLoc));
        document.open();
        Paragraph paragraph = new Paragraph();
        paragraph.add(formattedReceipt);
        document.add(paragraph);
        document.close();}
        catch (DocumentException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return new File(tmpLoc);
    }
}
