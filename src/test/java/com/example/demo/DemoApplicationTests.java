package com.example.demo;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.extra.mail.MailUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.example.demo.dao.UserMapper;
import com.example.demo.entity.Person;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.suggest.Suggester;
import lombok.extern.log4j.Log4j2;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@Log4j2
public class DemoApplicationTests {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void contextLoads() {
    }

    @Test
    public void get() {
        List<User> list = userMapper.getAllUser();
        for (User user : list) {
            log.info(user);
        }
    }

    //----------------------HanLP工具-------------------------//

    @Test
    public void testSegment() {
        String str = "https://www.codesheep.cn/2018/11/01/springbt-hanlp/我们不再需要这种指定data目录的方式";
        log.info(HanLP.segment(str));
    }

    @Test
    public void testSuggest() {
        Suggester suggester = new Suggester();
        String[] titleArray = ("威廉王子发表演说 呼吁保护野生动物\n" +
                "《时代》年度人物最终入围名单出炉 普京马云入选\n" +
                "“黑格比”横扫菲：菲吸取“海燕”经验及早疏散\n" +
                "日本保密法将正式生效 日媒指其损害国民知情权\n" +
                "人工智能如今是非常火热的一门技术”").split("\\n");
        for (String title : titleArray) {
            suggester.addSentence(title);
        }
        log.info(suggester.suggest("机器学习", 1));
        log.info(suggester.suggest("日本", 1));
        log.info(suggester.suggest("马云", 1));
    }

    @Test
    public void testKeyExtract() {
        String content = "体验一番我们发现其自带的模型、字典等数据给出的实验效果已经是非常不错了，而且用户还可以自定义或修改 data目录下的模型、字典等数据来满足特定需求，因此还是十分强大的。";
//        content = "自然语言处理工具包 HanLP在 Spring Boot中的应用";
        List<String> keywordList = HanLP.extractKeyword(content, 5);
        log.info(keywordList);
    }


    //----------------------HuTool工具-------------------------//

    @Test
    public void testDataUtil() {
        DateTime date = DateUtil.date(System.currentTimeMillis());
        log.info(date);
        String now = DateUtil.now();
        log.info(now);

        String dateStr1 = "2017-03-01 22:33:23";
        Date date1 = DateUtil.parse(dateStr1);

        String dateStr2 = "2017-04-01 23:33:23";
        Date date2 = DateUtil.parse(dateStr2);

        //相差一个月，31天
        long betweenDay = DateUtil.between(date1, date2, DateUnit.DAY);
        log.info(betweenDay);
    }

    @Test
    public void testValidator() {
        boolean email = Validator.isEmail("loolly@gmail.com");
        log.info(email);
    }

    @Test
    public void testImgPressText() {
        ImgUtil.pressText(//
                FileUtil.file("D:/login-bg.jpg"), //
                FileUtil.file("D:/login-bg-result.jpg"), //
                "版权所有", Color.WHITE, //文字
                new Font("黑体", Font.BOLD, 100), //字体
                0, //x坐标修正值。 默认在中间，偏移量相对于中间偏移
                0, //y坐标修正值。 默认在中间，偏移量相对于中间偏移
                0.8f//透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
        );
    }

    @Test
    public void testImgPressImage() throws IOException {
        //图片旋转
        BufferedImage image = ImgUtil.rotate(ImageIO.read(FileUtil.file("D:/avatar.jpg")), 325);
        ImgUtil.write(image, FileUtil.file("D:/result.png"));
        ImgUtil.pressImage(
                FileUtil.file("D:/login-bg.jpg"), //
                FileUtil.file("D:/login-bg-img.jpg"), //
                ImgUtil.read(FileUtil.file("D:/result.png")), //水印图片
//                ImgUtil.read(FileUtil.file("D:/avatar.jpg")), //水印图片
                0, //x坐标修正值。 默认在中间，偏移量相对于中间偏移
                0, //y坐标修正值。 默认在中间，偏移量相对于中间偏移
                0.1f
        );
    }

    @Test
    public void testMail() {
        MailUtil.send("guanripeng@126.com", "测试", "<h1>邮件来自Hutool测试</h1>", true, FileUtil.file("D:/login-bg-img.jpg"));
    }

    @Test
    public void testQrCode() {
        QrCodeUtil.generate(//
                "http://127.0.0.1:8080/exam/page/login", //二维码内容
                QrConfig.create().setImg("D:/logo.png"), //附带logo
                FileUtil.file("D:/qrcodeWithLogo.jpg")//写出到的文件
        );
    }

    @Test
    public void testExcelReader() {
        ExcelReader reader = ExcelUtil.getReader(FileUtil.file("classpath:person.xlsx"));
        List<List<Object>> read = reader.read();
        for (List<Object> objects : read) {
            log.info(objects);
        }
        List<Map<String, Object>> mapList = reader.readAll();
        for (Map<String, Object> map : mapList) {
            log.info(map);
        }

        log.info("----------------------");
        List<Person> personList = reader.readAll(Person.class);
        for (Person person : personList) {
            log.info(person);
        }
    }

    @Test
    public void testExcel() {
        ExcelReader reader = ExcelUtil.getReader(FileUtil.file("classpath:writeMapTest.xlsx"));
        List<List<Object>> read = reader.read();
        for (List<Object> objects : read) {
            log.info(objects);
        }

    }

    @Test
    public void testExcelWriter() {
        List<String> row1 = CollUtil.newArrayList("aa", "bb", "cc", "dd");
        List<String> row2 = CollUtil.newArrayList("aa1", "bb1", "cc1", "dd1");
        List<String> row3 = CollUtil.newArrayList("aa2", "bb2", "cc2", "dd2");
        List<String> row4 = CollUtil.newArrayList("aa3", "bb3", "cc3", "dd3");
        List<String> row5 = CollUtil.newArrayList("aa4", "bb4", "cc4", "dd4");

        List<List<String>> rows = CollUtil.newArrayList(row1, row2, row3, row4, row5);

        //通过工具类创建writer
        ExcelWriter writer = ExcelUtil.getWriter("D:/writeTest.xlsx");
        //通过构造方法创建writer
        //ExcelWriter writer = new ExcelWriter("d:/writeTest.xls");

        //跳过当前行，既第一行，非必须，在此演示用
        writer.passCurrentRow();

        //合并单元格后的标题行，使用默认标题样式
        writer.merge(row1.size() - 1, "测试标题");
        //一次性写出内容，强制输出标题
        writer.write(rows, true);
        //关闭writer，释放内存
        writer.close();
    }

    //RedisTemplate用法

}
