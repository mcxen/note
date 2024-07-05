
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

#### Git日志不乱码设置

这个问题通常是由于控制台的编码设置与Git的编码不匹配导致的。以下是一些可能的解决方法：

1. 设置控制台的编码

在Windows控制台中，执行以下命令来设置编码为UTF-8：

```
chcp 65001
```

2. 配置Git的编码

确保Git在提交信息和显示日志时使用UTF-8编码。可以通过以下命令设置：

```
git config --global i18n.commitencoding utf-8
git config --global i18n.logoutputencoding utf-8
```

3. 确保文本文件的编码为UTF-8

确保你使用的文本编辑器保存的文件编码为UTF-8，包括提交信息。

4. 使用Bash终端

你可以使用Git自带的Git Bash或Windows Subsystem for Linux (WSL)，这些终端默认支持UTF-8编码，通常不会出现乱码问题。

5. 设置控制台字体

有时问题出在控制台的字体上，确保使用支持UTF-8的字体，比如Consolas。