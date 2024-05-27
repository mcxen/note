Fork 自 Labuladong

2024/5/26 添加

找到[历史提交地址](https://github.com/labuladong/fucking-algorithm/commits/master/?since=2022-06-01&until=2022-08-31)

删除里面的广告图
```bash
find . -name "*.md" -exec sed -i '' '/!\[](https:\/\/labuladong.github.io\/pictures\/souyisou2.png)/d' {} +

```

```cmd
find . -name "*.md" -exec sed -i '' '/\*\*《labuladong 的算法小抄》已经出版，关注公众号查看详情；后台回复关键词「进群」可加入算法群；回复「PDF」可获取精华文章 PDF\*\*：/d' {} +

```

`find . -name "*.md" -exec sed -i '' '/\*\*通知：\[数据结构精品课 V1.8\](https:\/\/aep.h5.xeknow.com\/s\/1XJHEO) 持续更新中。\*\*/d' {} +
`
`find . -name "*.md" -exec sed -i '' '/!\[\](https:\/\/labuladong.github.io\/algo\/images\/souyisou1.png)/d' {} +
`
```cmd
find . -name "*.md" -exec sed -i '' '/\*\*《labuladong 的算法秘籍》、《labuladong 的刷题笔记》两本 PDF 和刷题插件 2.0 免费开放下载，详情见 \[labuladong 的刷题三件套正式发布\](https:\/\/mp.weixin.qq.com\/s\/yN4cHQRsFa5SWlacopHXYQ)\*\*~/d' {} +

```

