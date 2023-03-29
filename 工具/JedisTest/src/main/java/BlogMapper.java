import POJO.Blog;

public interface BlogMapper {
    /**
     * 插入文章
     * @param blog
     * @return
     */
    int addBlog(Blog blog);
}