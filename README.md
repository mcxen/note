
每日笔记

### 笔记软件设置存档

#### Obsidian设置

安装插件：

(Obsidian Custom定制路径)[obsidian://show-plugin?id=obsidian-custom-attachment-location]

设置图片的路径为相对的，存储在./assets里面

#### Typora设置

设置为Vue的主题

图片路径设计：

```cmd
./assets/${filename}
```

### Git 设置

#### 一键Push

```sh
创建Git别名命令: 
git config --global alias.pa '!git add -A && git commit -m "Small Auto Commit" && git push'

每次只需要执行: 
git pa
```

