<template>
  <div class="m-container">
    <Header></Header>
    <div class="mblog">
      <h2 style="color:whitesmoke">{{ blog.title }}</h2>
      <el-link style="color: red" icon="el-icon-edit" v-if="ownBlog"><router-link  style="color: red" :to="{name: 'BlogEdit', params: {blogId: blog.id}}">编辑</router-link></el-link>
      <el-link style="color: red" icon="el-icon-del" v-if="ownBlog"><a @click="delblog()">删除</a></el-link>
      <el-divider></el-divider>
      <div style="color:whitesmoke" class="content markdown-body" v-html="blog.content"></div>
    </div>
  </div>
</template>
<script>
import 'github-markdown-css/github-markdown.css' // 然后添加样式markdown-body
import Header from "@/components/Header";
import Qs from 'qs'
export default {
  name: "BlogDetail",
  components: {
    Header
  },
  data() {
    return {
      blog: {
        userId: null,
        title: "",
        description: "",
        content: ""
      },
      ownBlog: false
    }
  },
  methods: {

    getBlog() {
      const blogId = this.$route.params.blogId
      const _this = this
      this.$axios.get('/blog/' + blogId).then((res) => {
        console.log(res)
        console.log(res.data.data)
        _this.blog = res.data.data
        var MarkdownIt = require('markdown-it'),
            md = new MarkdownIt();
        var result = md.render(_this.blog.content);
        _this.blog.content = result
        // 判断是否是自己的文章，能否编辑
        _this.ownBlog =  (_this.blog.userId === _this.$store.getters.getUser.id)
      });
    },
    delblog() {
      const blogId = this.$route.params.blogId
      alert(blogId)
      this.$axios.post('/blog/del' ,{
        blogid: blogId

      }, {
        headers: {
          "Authorization": localStorage.getItem("token")
        }}).then((res) => {
        console.log(res)
        console.log(res.data.data)
        alert("删除成功")
        window.location.href="/blogs"
      });
    }
  },
  created() {
    this.getBlog()
  }
}
</script>
