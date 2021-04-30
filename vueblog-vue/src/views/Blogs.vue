<template>
  <div>
    <Header></Header>
    <p v-show="hastxt" style="color: #ff0000">请输入信息</p>
    <div style="float: left">
      <el-form >
        <el-form-item  prop="searchtxt">
          <el-input class="minput"   placeholder="请输入内容搜索" v-model="searchtxt"></el-input>

        </el-form-item>
      </el-form>
    </div>
    <div style="float: left">
      <el-button type="primary" @click="search(searchtxt)">搜索</el-button>
    </div>

    <div class="block" style="margin-top: 10%">




      <el-timeline>




        <el-timeline-item :timestamp="blog.created" placement="top" v-for="blog in blogs">
          <el-card>
            <h4>
              <router-link :to="{name: 'BlogDetail', params: {blogId: blog.id}}">
                {{blog.title}}
              </router-link>
            </h4>
            <p>{{blog.description}}</p>
          </el-card>
        </el-timeline-item>

      </el-timeline>
      <el-pagination v-show="!searchPage"
                     class="mpage"
                     background
                     layout="prev, pager, next"
                     :current-page="currentPage"
                     :page-size="pageSize"
                     :total="total"
                     @current-change=page>
      </el-pagination>
      <el-pagination v-show="searchPage"
                     class="mpage"
                     background
                     layout="prev, pager, next"
                     :current-page="currentPage"
                     :page-size="pageSize"
                     :total="total"
                     @current-change=searchpage>
      </el-pagination>
    </div>
  </div>
</template>

<script>
import Header from "../components/Header";

export default {
  name: "Blogs.vue",
  components: {Header},
  data() {
    return {
      blogs: {},
      currentPage: 1,
      total: 0,
      pageSize: 5,
      searchtxt: "",
      hastxt:false,
      searchPage:false
    }
  },
  methods: {
    page(currentPage) {
      const _this = this
      _this.$axios.get("/blogs?currentPage=" + currentPage).then(res => {
        console.log(res)
        _this.blogs = res.data.data.records
        _this.currentPage = res.data.data.current
        _this.total = res.data.data.total
        _this.pageSize = res.data.data.size

      })
    },
    searchpage(currentPage,searchtxt) {
      const _this = this
      _this.$axios.get("/blog/search/"+searchtxt+"?currentPage=" + currentPage).then(res => {
        console.log(res)
        _this.blogs = res.data.data.records
        _this.currentPage = res.data.data.current
        _this.total = res.data.data.total
        _this.pageSize = res.data.data.size

      })
    },
    search(searchtxt) {
      const _this = this
     if (searchtxt!=""){

       _this.hastxt=false
       _this.$axios.get("/blog/search/"+searchtxt).then(res => {
         console.log(res)
         _this.blogs = res.data.data.records
         _this.currentPage = res.data.data.current
         _this.total = res.data.data.total
         _this.pageSize = res.data.data.size
          _this.searchPage=true
         _this.searchtxt=searchtxt
       })
     }else {

      _this.hastxt=true
     }

    }

  },
  created() {
    this.page(1)
  }
}
</script>

<style scoped>
.mpage{
  margin: 0 auto;
  text-align: center;
}
.minput{


  max-width: 100%;
}
</style>