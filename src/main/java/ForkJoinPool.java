import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.layout.font.FontProvider;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.StopWatch;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ForkJoinPool {

    private static final int numberOfThreads = 4;
    private static final java.util.concurrent.ForkJoinPool THREAD_POOL = new java.util.concurrent.ForkJoinPool(numberOfThreads);

    public static void main(String[] args) throws IOException {

//        List<Integer> list = Arrays.asList(1);
        List<Integer> list = Arrays.asList(1, 2, 3, 4);
//        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);
        StopWatch globalSW = StopWatch.create();
        globalSW.reset();
        globalSW.start();
        Runnable runnable = () -> {
            list.parallelStream().forEach(data -> {
                StopWatch sw = StopWatch.create();
                sw.reset();
                sw.start();

                String notoSansRegularPath = ForkJoinPool.class.getClassLoader().getResource("fonts/NotoSans-Regular.ttf").getFile();
                String notoSansCJKjpRegularPath = ForkJoinPool.class.getClassLoader().getResource("fonts/NotoSansCJKjp-Regular.otf").getFile();
                String notoSansCJKjpBoldPath = ForkJoinPool.class.getClassLoader().getResource("fonts/NotoSansCJKjp-Bold.otf").getFile();

                File file = new File("/Users/rutsatz/Documents/domo/campaigns/render/campaign.html");
                String formattedHtml = null;
                try {
                    formattedHtml = FileUtils.readFileToString(file, "UTF-8");
                } catch (IOException e) {
                    e.printStackTrace();
                }

//                final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                BufferedOutputStream baos = new BufferedOutputStream(outputStream);

                FontProvider provider = new DefaultFontProvider();

                provider.addFont(notoSansRegularPath);
                provider.addFont(notoSansCJKjpRegularPath);
                provider.addFont(notoSansCJKjpBoldPath);

                ConverterProperties properties = new ConverterProperties();
                properties.setFontProvider(provider);

                log(sw.formatTime(), "tempo total de inicialização");

                sw.reset();
                sw.start();
                try {
                    HtmlConverter.convertToPdf(formattedHtml, outputStream, properties);
//                    HtmlConverter.convertToPdf(FileUtils.openInputStream(file), baos, properties);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                log(sw.formatTime(), "total time for convertToPdf");

                byte[] bytes = outputStream.toByteArray();
            });
        };

        try {
            // run and wait for results
            THREAD_POOL.submit(runnable).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        log(globalSW.formatTime(), "total runtime");

    }

    public static void log(String msg) {
        System.out.println(String.format("%s %s - %s", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSS")), Thread.currentThread().getName(), msg));
    }

    public static void log(String totalTime, String msg) {
        System.out.println(String.format("%s %s - total time: %s - %s", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSS")), Thread.currentThread().getName(), totalTime, msg));
    }

}
