package trashsoftware.deepSearcher2.searcher;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public interface ContentReader {
    String read(File file);
}

class PdfReader implements ContentReader {

    @Override
    public  String read(File file) {
        try {
            PDDocument document = PDDocument.load(file);
            if (!document.isEncrypted()) {
                PDFTextStripper stripper = new PDFTextStripper();
                String text = stripper.getText(document);
                document.close();
                return text;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}

class DocReader implements ContentReader {

    @Override
    public String read(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
//            HWPFDocument doc = new HWPFDocument(fis);
//            String text = doc.getDocumentText();
//            fis.close();
//            return text;
            WordExtractor we = new WordExtractor(fis);

            String[] paragraphs = we.getParagraphText();
            StringBuilder builder = new StringBuilder();

            for (String para : paragraphs) {
                builder.append(para);
            }
            fis.close();
            we.close();
            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}

class DocxReader implements ContentReader {

    @Override
    public String read(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            XWPFDocument docx = new XWPFDocument(fis);
            List<XWPFParagraph> paragraphs = docx.getParagraphs();
            StringBuilder builder = new StringBuilder();
            for (XWPFParagraph paragraph : paragraphs) {
                builder.append(paragraph.getText());
            }
            fis.close();
            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
