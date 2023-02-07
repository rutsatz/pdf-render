import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.layout.font.FontProvider;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.StopWatch;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PDFRender {

    private static final String NotoSansRegularUrl = "https://es-backend.s3.amazonaws.com/fonts/NotoSans-Regular.ttf";
    private static final String NotoSansCJKjpRegularUrl = "https://es-backend.s3.amazonaws.com/fonts/NotoSansCJKjp-Regular.otf";
    private static final String NotoSansCJKjpBoldUrl = "https://es-backend.s3.amazonaws.com/fonts/NotoSansCJKjp-Bold.otf";

    public static void log(String msg) {
        System.out.println(String.format("%s %s - %s", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSS")), java.lang.Thread.currentThread().getName(), msg));
    }

    public static void log(String totalTime, String msg) {
        System.out.println(String.format("%s %s - total time: %s - %s", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSS")), java.lang.Thread.currentThread().getName(), totalTime, msg));
    }

    public static void main(String[] args) throws IOException {
        StopWatch sw = StopWatch.create();
        sw.reset();
        sw.start();

        String notoSansRegularPath = PDFRender.class.getClassLoader().getResource("fonts/NotoSans-Regular.ttf").getFile();
        String notoSansCJKjpRegularPath = PDFRender.class.getClassLoader().getResource("fonts/NotoSansCJKjp-Regular.otf").getFile();
        String notoSansCJKjpBoldPath = PDFRender.class.getClassLoader().getResource("fonts/NotoSansCJKjp-Bold.otf").getFile();

        File file = new File("/Users/rutsatz/Documents/domo/campaigns/render/campaign.html");
        String formattedHtml = FileUtils.readFileToString(file, "UTF-8");

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        FontProvider provider = new DefaultFontProvider();

        provider.addFont(notoSansRegularPath);
        provider.addFont(notoSansCJKjpRegularPath);
        provider.addFont(notoSansCJKjpBoldPath);

        ConverterProperties properties = new ConverterProperties();
        properties.setFontProvider(provider);

        log(sw.formatTime(), "tempo total de inicialização");

        sw.reset();
        sw.start();
        HtmlConverter.convertToPdf(formattedHtml, outputStream, properties);
        log(sw.formatTime(), "tempo total para convertToPdf");

        sw.reset();
        sw.start();
        byte[] bytes = outputStream.toByteArray();
        log(sw.formatTime(), "tempo total para toByteArray");



    }

}
