package com.uitil.contract.util;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 处理合同的docx文件的辅助工具，使用前提：手动修改docx文件
 *
 * 仅仅用于处理docx文件，如果提供了doc文件，请手工修改为docx类型
 *
 * 其中@@@ 标记该段落要居左（顶行）
 * 其中### 标记段落为标题段落，需要居中
 * 其中$$$ 标记段落为居右段落
 *
 * 特殊处理签章内容的 %%% ，将标记放置在签章方中间，用于替换空格标签
 * eg:   甲方   %%%   乙方 -> 甲方（签章） &nbsp;&nbsp;乙方（签章）
 */
public class Docx2HtmlUtil {

    /**
     * 处理docx文件：将读取到的文件内容添加html标签
     * @param filePath 文件路径
     * @return 文本
     */
    public static String dealDocxFile(String filePath) throws IOException {
        XWPFDocument docx = new XWPFDocument(new FileInputStream(new File(filePath)));
        List<XWPFParagraph> paras = docx.getParagraphs();
        String html = "";

        for (int i = 0; i < paras.size(); i++) {
            //获取当前段落
            XWPFParagraph paragraph = paras.get(i);

            //特殊特殊标记：@@@ 居左，### 居中标题 $$$ 居右
            if (paragraph.getText().contains("$$$")) {

                //段落中包含###时，处理居右的段落，所以在docx文件中，需要对居右的段落，加$$$
                html += "<p style=\"text-align: right\">";
            } else if (paragraph.getText().contains("###")) {

                //段落中包含###时，处理居中的标题，所以在docx文件中，需要对居中的标题，加###
                html += "<p style=\"font-size: 21px; font-weight: bold; text-align: center\">";
            } else if (paragraph.getText().contains("@@@")) {

                //段落中包含@@@时，处理顶格的行，所以在docx文件中，需要对顶格的行，加@@@处理
                html += "<p>";
            } else {
                html += "<p>&nbsp;&nbsp;";
            }

            if (paragraph.getText().contains("%%%")) {

                //特殊处理签章
                String sealStr = paragraph.getText();
                if (paragraph.getText().contains("甲方")) {
                    sealStr = sealStr.replace("甲方", "甲方（签章）");
                }
                if (paragraph.getText().contains("乙方")) {
                    sealStr = sealStr.replace("乙方", "乙方（签章）");
                }
                if (paragraph.getText().contains("丙方")) {
                    sealStr = sealStr.replace("丙方", "丙方（签章）");
                }
                if (paragraph.getText().contains("丁方")) {
                    sealStr = sealStr.replace("丁方", "丁方（签章）");
                }
                if (paragraph.getText().contains("戊方")) {
                    sealStr = sealStr.replace("戊方", "戊方（签章）");
                }
                if (paragraph.getText().contains("己方")) {
                    sealStr = sealStr.replace("己方", "己方（签章）");
                }
                sealStr = sealStr.replace("%%%",
                        "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                html += sealStr;

            } else {

                //段落文本属性
                List<XWPFRun> runs = paragraph.getRuns();
                for (int j = 0 ; j < runs.size(); j++ ) {
                    XWPFRun r = runs.get(j);
                    if (r.isBold()) {
                        //文本加粗
                        html += "<strong>" + replaceSpecialChar(r.toString()) + "</strong>";
                    } else {
                        //文本非加粗
                        html += replaceSpecialChar(r.toString());
                    }
                }
            }

            //结束当前段落
            html += "</p>\n";

            //文档结尾处理
            if (i == paras.size() - 1) {
                html += "</body>\n" +
                        "</html>";
            }
        }
        return getComplateHtml(replaceLable(html));
    }

    public static String getComplateHtml(String html) {
        String stage = "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>\n" +
                "<html xmlns='http://www.w3.org/1999/xhtml'>\n" +
                "<head>\n" +
                "<meta http-equiv='Content-Type' content='text/html; charset=utf-8' />\n" +
                "<style type='text/css'> \n" +
                "body {\n" +
                "\tfont-family: SimSun;\n" +
                "\tfont-size: 18px;\n" +
                "}\n" +
                " \n" +
                "p {\n" +
                "\tline-height: 30px;\n" +
                "\tpadding-top: -16px;\n" +
                "\tmargin-top: 12px;\n" +
                "\tmargin-bottom: 12px;\n" +
                "}\n" +
                " \n" +
                "html {\n" +
                "\tcolor: #000;\n" +
                "}\n" +
                "</style>\n" +
                " \n" +
                "</head>\n" +
                " \n" +
                "<body>\n";
        return stage + html;
    }

    /**
     * 替代特殊标记
     * @param str
     * @return
     */
    public static String replaceSpecialChar(String str) {
        return str.replace("$$$", "")
                .replace("###", "")
                .replace("@@@", "");
    }



    /**
     * 处理strong u  标签
     * @param string
     * @return
     */
    public static String replateStrongULable(String string) {
        return string.replace("<strong><u></strong>", "<u>")
                .replace("<strong></u></strong>","</u>")
                .replace("<strong>$</strong>", "$")
                .replace("<strong>${</strong>", "${")
                .replace("<strong>}</strong>","}")
                ;
    }

    /**
     * 处理标签
     * @param content
     * @return
     */
    public static String replaceLable(String content) {
        String str = replateStrongULable(content);//去掉<strong><u></strong> 类标签
        str = replaceBlankStrongLable(str);//去掉空白的<strong></strong>
        str = addbotomLine(str);//加下划线
        return str;
    }

    /**
     * 处理空白的strong标签
     * @param b
     * @return
     */
    public static String replaceBlankStrongLable(String b) {
        String pat = "<strong>\\s*</strong>";
        Pattern pattern = Pattern.compile(pat);
        Matcher matcher = pattern.matcher(b);
        return matcher.replaceAll("");
    }

    /**
     * 添加下划线
     * @param content
     * @return
     */
    public static String addbotomLine(String content) {
        return content.replace("${", "<u>&nbsp;${").replace("}","}</u>");
    }

    /**
     * 占位符上前后添加空格
     * @param content
     * @return
     */
    public static String addNbsp(String content) {
        return content.replace("${", "&nbsp;${").replace("}","}&nbsp;");
    }

    public static void main(String[] args) {
        String aa = "hello<strong>zhangsan<strong>abcd<strong></strong>test haha ";
        System.out.println(aa);
        System.out.println(replaceBlankStrongLable(aa));
    }
}
