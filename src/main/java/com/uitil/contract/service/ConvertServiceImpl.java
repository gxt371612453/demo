package com.uitil.contract.service;

import com.uitil.contract.util.Docx2HtmlUtil;
import com.uitil.contract.util.PdfContract;
import com.uitil.contract.util.PlaceholderStrConstants;
import freemarker.cache.StringTemplateLoader;
import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import org.springframework.stereotype.Service;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

@Service
public class ConvertServiceImpl implements IConvertService {
    @Override
    public boolean convertDoc2Html(String srcFile) throws IOException {
        String htmlStr = Docx2HtmlUtil.dealDocxFile(srcFile);
        String pdfFile = changeSuffix(srcFile, "pdf");
        Map<String, Object> paramMap = Object2Map(new PlaceholderStrConstants());
        String replaceHtmlStr = replacePlaceHolder(htmlStr, paramMap);
        if (PdfContract.htmlText2PdfByItextPdf(replaceHtmlStr,pdfFile)){
            writeHtmlFile(htmlStr,changeSuffix(srcFile,"html"));
        } else {
            return false;
        }
        return true;
    }

    public  String changeSuffix(String path, String suffix) {
        if (path.contains("html") || path.contains("htm")
                || path.contains("pdf") || path.contains("docx")) {
            return path.substring(0, path.indexOf(".") + 1) + suffix;
        }
        return null;
    }

    public  Map<String, Object> Object2Map(Object object) {
        Map<String, Object> map = new HashMap<>();
        try {
            Field[] fields = object.getClass().getDeclaredFields();
            for (Field filed: fields) {
                filed.setAccessible(true);
                map.put(filed.getName(), filed.get(object));
            }
        } catch (Exception e) {
        }
        return map;
    }

    public  String replacePlaceHolder(final String fileStr, Map<String, Object> placeHolderMap) {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);
        configuration.setTemplateLoader(new StringTemplateLoader() {{putTemplate("contract", fileStr);}});
        StringWriter writer = new StringWriter();
        try {
            configuration.getTemplate("contract", "utf-8").process(placeHolderMap, writer);
        } catch (TemplateException e) {
           e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return writer.toString();
    }

    public  boolean writeHtmlFile(String content, String path) {
        File file = new File(path);
        Charset charset = Charset.forName("UTF-8");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            //文件通道
            FileChannel fileChannel = fos.getChannel();
            //创建缓存区
            ByteBuffer byteBuffer = ByteBuffer.allocate(content.getBytes(charset).length);
            //向缓存区写入数据
            byteBuffer.put(content.getBytes(charset));
            byteBuffer.flip();
            fileChannel.write(byteBuffer);
            fileChannel.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
