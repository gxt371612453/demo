/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.uitil.contract.util;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.log.Logger;
import com.itextpdf.text.log.LoggerFactory;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

/**
 *
 * @author huang
 */
public class PdfContract {
    private static final Logger LOGGER = LoggerFactory.getLogger(PdfContract.class);
    
    /**
     * html文件转pdf文件(simsun)
     * @param htmlPath HTML文件路径
     * @param pdfPath PDF文件路径
     * @return 成功、失败
     */
    @Deprecated
    public static boolean html2pdf(final String htmlPath, final String pdfPath)
    {
        try
        {
            File htmlSource = new File(htmlPath);
            File pdfDest = new File(pdfPath);
            ConverterProperties converterProperties = new ConverterProperties();
            DefaultFontProvider fontProvider=new DefaultFontProvider(true, true, true);
            fontProvider.addFont("ttf/simsun.ttf");
            converterProperties.setFontProvider(fontProvider);
            HtmlConverter.convertToPdf(new FileInputStream(htmlSource), new FileOutputStream(pdfDest), converterProperties);
            return true;
        }
        catch(IOException ex)
        {
            LOGGER.error("pdf生成出错： {}", ex);
        }
        catch(Exception ex)
        {
            LOGGER.error("pdf生成出错： {}", ex);
        }
        return false;
    }
    
    @Deprecated
    public static boolean htmlText2pdf(final String htmlText, final String pdfPath)
    {
        try
        {
            File pdfDest = new File(pdfPath);
            ConverterProperties converterProperties = new ConverterProperties();
            DefaultFontProvider fontProvider=new DefaultFontProvider(false, false, true);
            fontProvider.addFont("ttf/simsun.ttf");
            converterProperties.setFontProvider(fontProvider);
            HtmlConverter.convertToPdf(htmlText, new FileOutputStream(pdfDest), converterProperties);
            return true;
        }
        catch(IOException ex)
        {
            LOGGER.error("pdf生成出错： {}", ex);
        }
        catch(Exception ex)
        {
            LOGGER.error("pdf生成出错： {}", ex);
        }
        return false;
    }
    
    /**
     * htmlText生成pdf文件(默认utf8编码)
     * @param htmlTest html文本
     * @param destPath 目标路径
     * @return
     */
    public static boolean htmlText2PdfByItextPdf(String htmlTest, String destPath) {
    	String encoding = "utf-8";
    	return htmlText2PdfByItextPdf(htmlTest, destPath, encoding);
    }
    
    /**
     * htmlText生成pdf文件
     * @param htmlText html文本
     * @param destPath 目标路径
     * @param encoding 编码格式：已失效
     * @return
     */
    public static boolean htmlText2PdfByItextPdf(String htmlText, String destPath , String encoding) {
        //step 1
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        try {
         // step 2
         PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(destPath));
         // step 3
         setFooter(writer);
         document.open();
         // step 4
    	 InputStream   in   =   new   ByteArrayInputStream(htmlText.getBytes());//采用系统默认的编码格式，不再依赖外部传入
         XMLWorkerHelper.getInstance().parseXHtml(writer, document, in);
         //step 5
         document.close();
         writer.close();
         } catch(Exception e) {
    		 LOGGER.error("htmlText 转pdf 出错{}", e);
            if(document.isOpen()) {
                document.close();
            }
            return false;
    	 }
    	 return true;
    }

    public static boolean html2PdfByItextPdf(String htmlText, String destPath , String encoding) {
        try {
            //step 1
            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            // step 2
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(destPath));
            // step 3
            setFooter(writer);
            document.open();
            // step 4
            XMLWorkerHelper workerHelper = XMLWorkerHelper.getInstance();
            workerHelper.getInstance().parseXHtml(writer, document, new FileInputStream(htmlText),
                    Charset.forName(encoding));
            //step 5
            document.close();
            writer.close();
        } catch(Exception e) {
            LOGGER.error("htmlText 转pdf 出错{}", e);
        }
        return true;
    }

    private static void setFooter(PdfWriter writer) throws DocumentException, IOException {
        // 更改事件，瞬间变身 第几页/共几页 模式。
        PdfReportM1HeaderFooter headerFooter = new PdfReportM1HeaderFooter();// 就是上面那个类
        writer.setBoxSize("art", PageSize.A4);
        writer.setPageEvent(headerFooter);
    }
    
    /**
     * pdf => png pdf多页合并一张图片
     * @param srcFile 源文件
     * @param destPath 目标路径
     * @param format 文件格式：默认jpg 
     * @param dpi 分辨率 默96 dpi越大越清晰，转化速度越慢
     * @param merge 合并多张图片为一张，默认不合并，
     * @return 
     * @throws IOException
     */
    public static boolean pdf2Png(String srcFile, String destPath, String format, Float dpi, Boolean merge) throws IOException {
    	File file = new File(srcFile);
        //默认输出路径为源文件所在文件夹
        if(destPath == null || destPath.isEmpty()){
            destPath = file.getParent();
        }
        List<BufferedImage> images = pdfToImage(file, dpi == null ? 96f : dpi);
        if(images == null || images.isEmpty()){
            return false;
        }
        //默认为jpg
        format = format == null || format.isEmpty() ? "jpg" : format;
        String pdfFileName = file.getName();
        pdfFileName = pdfFileName.substring(0, pdfFileName.indexOf('.'));
        StringBuilder sb = new StringBuilder();
        //合并多张图片为一张
        merge = merge == null ? false : merge;
        if(merge) {
            BufferedImage image = mergeImages(images);
            images.clear();
            images.add(image);
        }
        //保存到本地
        for (int i = 0, len = images.size(); i < len; i++) {
            //输出格式: [文件夹路径]/[pdf文件名].jpg
            ImageIO.write(images.get(i), format, new File(
                    sb.append(destPath).append(File.separator)
                            .append(pdfFileName).append("_").append(i + 1)
                            .append(".").append(format).toString()));
            sb.setLength(0);
        }
    	return true;
    }
    
    /**
     * pdf => png pdf每页生成一张图片
     * @param srcFile 源文件
     * @param destPath 目标路径
     * @param format 文件格式：默认jpg 
     * @param dpi 分辨率 默96 dpi越大越清晰，转化速度越慢
     * @return 
     * @throws IOException
     */
    public static Integer pdf2Pic(String srcFile, String destPath, String format, Float dpi) throws IOException {
        File file = new File(srcFile);
        List<BufferedImage> images = pdfToImage(file, dpi == null ? 96f : dpi);
        if(images == null || images.isEmpty()){
            return null;
        }
        //默认为jpg
        format = format == null || format.isEmpty() ? "jpg" : format;
        String pdfFileName = file.getName();
        pdfFileName = pdfFileName.substring(0, pdfFileName.indexOf('.'));
        StringBuilder sb = new StringBuilder();
        //保存到本地
        for (int i = 0, len = images.size(); i < len; i++) {
            //输出格式: [文件夹路径]/[pdf文件名]_1.jpg
            ImageIO.write(images.get(i), format, new File(
                    sb.append(destPath).append(pdfFileName).append("_").append(i + 1)
                            .append(".").append(format).toString()));
            sb.setLength(0);
        }
        return images.size();
    }
    
    /**
     * pdf => 固定分辨率的png图片
     * @param srcFile 源文件
     * @param destPath 目标路径
     * @return
     * @throws IOException 
     */
    public static Integer pdf2FixedDpiPng(String srcFile, String destPath) throws IOException {
    	String format = "png";
    	Float dpi = 72F;
    	return pdf2Pic(srcFile, destPath, format, dpi);
    }
    
    private static List<BufferedImage> pdfToImage(File file, float dpi) throws IOException {
        List<BufferedImage> imgList = null;
        PDDocument pdDocument = null;
        BufferedImage image;
        try {
            pdDocument = PDDocument.load(file);
            PDFRenderer renderer = new PDFRenderer(pdDocument);
            int numPages = pdDocument.getNumberOfPages();
            imgList = new ArrayList<BufferedImage>();
            for (int i = 0; i < numPages; i++) {
                image = renderer.renderImageWithDPI(i, dpi);
                if (null != image) {
                    imgList.add(image);
                }
            }
        } catch (IOException e) {
            LOGGER.error("convert pdf pages to images failed.", e);
            //FIXME
            throw e;
        } finally {
            try {
                if (null != pdDocument) {
                    pdDocument.close();
                }
            } catch (IOException e) {
                LOGGER.error("close IO failed when convert pdf pages to images.", e);
                //FIXME
                throw e;
            }
        }
        return imgList;
    }
    
    private static BufferedImage mergeImages(List<BufferedImage> images){
        int width = 0, height = 0;
        for(BufferedImage image : images){
            width = image.getWidth() > width ? image.getWidth() : width;
            height += image.getHeight();
        }
        BufferedImage pdfImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = pdfImage.createGraphics();
        height = 0;
        for(BufferedImage image :images){
            g2d.drawImage(image, (width - image.getWidth()) / 2, height, image.getWidth(), image.getHeight(), null);
            height += image.getHeight();
        }
        g2d.dispose();
        return pdfImage;
    }
    
    public static void main(String[] args) throws IOException {
    	
    	String homePath = "C:\\Users\\yylc\\Desktop\\爱建合同.html";
//    	String destPath = "C:\\Users\\yylc\\Desktop\\template\\abc.pdf";
//    	boolean a = html2pdf(homePath, destPath);
    	String destPath = "C:\\Users\\yylc\\Desktop\\dd.pdf";
        html2pdf(homePath, destPath);
    }

}
