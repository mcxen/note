package com.mcxgroup.testmybatisplus.config;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.fill.Column;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class CodeGenerator {
    public static final String PROJECT_NAME = "";   //生成的代码放到哪个工程中
    public static final String DATABASE_NAME = "pos";   //数据库名称
    public static final String PARENT_MODULE_NAME = "com.mcxgroup.testmybatisplus"; //父包名
    public static final String MODULE_NAME = ""; //子包名
    public static final String[] REMOVE_TABLE_PREFIX = {};//去掉表前缀
    public static final String MY_IP = "localhost";
    public static final String MY_PORT = "3306";
    public static final String MY_USERNAME = "root";
    public static final String MY_PASSWORD = "root";
    private static final String author = "mcxen";

    public static void main(String[] args) {
        String url = "jdbc:mysql://"+MY_IP+":"+MY_PORT+"/"+ DATABASE_NAME +"?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B8";
        String projectPath = System.getProperty("user.dir") + "/";

        FastAutoGenerator.create(url,MY_USERNAME,MY_PASSWORD)
                // 全局配置
                .globalConfig(( builder) -> builder.author("c").enableSwagger().outputDir(projectPath+ PROJECT_NAME +"/src/main/java"))
                // 包配置
                .packageConfig((builder) -> builder.parent(PARENT_MODULE_NAME).moduleName(MODULE_NAME))
                // 策略配置
                .strategyConfig((scanner, builder) -> builder.addInclude(getTables(scanner.apply("请输入表名，多个英文逗号分隔？所有输入 all")))
                        .addTablePrefix(REMOVE_TABLE_PREFIX)
                        .controllerBuilder().enableRestStyle().enableHyphenStyle()
                        .entityBuilder().enableLombok().addTableFills(new Column("create_time", FieldFill.INSERT))
                        .build())
                /*
                    模板引擎配置，默认 Velocity 可选模板引擎 Beetl 或 Freemarker
                   .templateEngine(new BeetlTemplateEngine())
                   .templateEngine(new FreemarkerTemplateEngine())
                 */
                .execute();


    }

    // 处理 all 情况
    protected static List<String> getTables(String tables) {
        return "all".equals(tables) ? Collections.emptyList() : Arrays.asList(tables.split(","));
    }
}