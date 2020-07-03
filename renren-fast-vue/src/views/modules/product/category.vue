<template>
  <div>
    <el-tree
      :data="menus"
      :props="defaultProps"
      v-bind:expand-on-click-node="false"
      show-checkbox
      node-key="catId"
      :default-expanded-keys="expandedKey"
      draggable
      :allow-drop="allowDrop"
    >
      <span class="custom-tree-node" slot-scope="{ node, data }">
        <span>{{ node.label }}</span>
        <span>
          <el-button v-if="node.level<=2" type="text" size="mini" @click="() => append(data)">Append</el-button>
          <el-button type="text" size="mini" @click="() => edit(data)">Edit</el-button>
          <el-button
            v-if="node.childNodes.length==0"
            type="text"
            size="mini"
            @click="() => remove(node, data)"
          >Delete</el-button>
        </span>
      </span>
    </el-tree>
    <el-dialog
      :title="title"
      :visible.sync="dialogVisible"
      width="30%"
      :close-on-click-modal="false"
    >
      <el-form :model="category">
        <el-form-item label="分类名称">
          <el-input v-model="category.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="category.icon" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="计量单位">
          <el-input v-model="category.productUnit" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submit">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
// 这里可以导入其他文件（比如：组件，工具js，第三方插件js，json文件，图片文件等等）
// 例如：import 《组件名称》 from '《组件路径》'

export default {
  // import引入的组件需要注入到对象中才能使用
  components: {},
  props: {},
  data() {
    return {
      maxLevel: 0,
      title: '',
      dialogType: '', // edit,add
      category: { name: '', parentCid: 0, catLevel: 0, showStatus: 1, sort: 0, catId: null, productUnit: '', icon: '' },
      dialogVisible: false,
      menus: [],
      expandedKey: [],
      defaultProps: {
        children: 'children',
        label: 'name'
      }
    }
  },
  // 计算属性 类似于data概念
  computed: {},
  // 监控data中的数据变化
  watch: {},
  // 方法集合
  methods: {
    getMenus() {
      this.$http({
        url: this.$http.adornUrl('/product/category/list/tree'),
        method: 'get'
      }).then(({ data }) => {
        console.log('成功获取到菜单数据。。。', data.data)
        this.menus = data.data
      })
    },
    append(data) {
      console.log('append', data)
      this.dialogType = 'add'
      this.title = '添加'
      this.dialogVisible = true
      this.category.parentCid = data.catId
      this.category.catLevel = data.catLevel * 1 + 1
      // 为了让重新添加时的对话框是默认状态，所以在这里恢复默认设置
      this.category.catId = null
      this.category.name = ''
      this.category.icon = ''
      this.category.productUnit = ''
      this.category.sort = 0
      this.category.showStatus = 1
    },
    allowDrop(draggingNode, dropNode, type) {
      // 被拖动的当前节点及所在的父节点总层数不能大于3
      // 被拖动的当前节点总层数
      console.log(draggingNode, dropNode, type)
      this.countNodeLevel(draggingNode.data)
      // 当前正在拖动的节点+父节点所在的深度不大于3即可
      let deep = (this.maxLevel - draggingNode.data.catLevel) + 1
      console.log('深度', deep)
      if (type === 'inner') {
        return (deep + dropNode.level <= 3)
      } else {
        return (deep + dropNode.parent.level <= 3)
      }
    },
    countNodeLevel(node) {
      // 找到所有子节点，求出最大深度
      if (node.children != null && node.children.length > 0) {
        for (let i = 0; i < node.children.length; i++) {
          if (node.children[i].catLevel > this.maxLevel) {
            this.maxLevel = node.children[i].catLevel
          }
          this.countNodeLevel(node.children[i])
        }
      }
    },
    edit(data) {
      console.log('要修改的数据', data)
      this.dialogType = 'edit'
      this.title = '修改'
      this.dialogVisible = true

      // 发送请求获取当前节点最新的数据
      this.$http({
        url: this.$http.adornUrl(`/product/category/info/${data.catId}`),
        method: 'get'
      }).then(({ data }) => {
        // 请求成功
        console.log('要回显的数据', data)
        this.category.name = data.data.name
        this.category.catId = data.data.catId
        this.category.icon = data.data.icon
        this.category.productUnit = data.data.productUnit
        this.category.parentCid = data.data.parentCid
      })
    },
    remove(node, data) {
      var ids = [data.catId]

      this.$confirm(`是否删除【${data.name}】菜单？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          this.$http({
            url: this.$http.adornUrl('/product/category/delete'),
            method: 'post',
            data: this.$http.adornData(ids, false)
          }).then(({ data }) => {
            if (data && data.code === 0) {
              this.$message({
                message: '菜单删除成功',
                type: 'success'
              })
              // 刷新出新的菜单
              this.getMenus()
              // 设置默认展开的菜单
              this.expandedKey = [node.parent.data.catId]
            } else {
              this.$message.error(data.msg)
            }
          })
        })
        .catch(() => { })
      console.log('remove', node, data)
    },
    submit() {
      if (this.dialogType === 'add') {
        this.addCategory()
      }
      if (this.dialogType === 'edit') {
        this.editCategory()
      }
    },
    // 修改三级分类
    editCategory() {
      var { catId, name, icon, productUnit } = this.category
      var data = { catId, name, icon, productUnit }
      this.$http({
        url: this.$http.adornUrl('/product/category/update'),
        method: 'post',
        data: this.$http.adornData(data, false)
      }).then(({ data }) => {
        this.$message({
          message: '菜单修改成功',
          type: 'success'
        })
        // 关闭对话框
        this.dialogVisible = false
        // 刷新出新的菜单
        this.getMenus()
        // 设置默认展开的菜单
        this.expandedKey = [this.category.parentCid]
      })
    },
    // 添加三级分类
    addCategory() {
      console.log('三级表单提交的数据', this.category)
      this.$http({
        url: this.$http.adornUrl('/product/category/save'),
        method: 'post',
        data: this.$http.adornData(this.category, false)
      }).then(({ data }) => {
        this.$message({
          message: '菜单保存成功',
          type: 'success'
        })
        // 关闭对话框
        this.dialogVisible = false
        // 刷新出新的菜单
        this.getMenus()
        // 设置默认展开的菜单
        this.expandedKey = [this.category.parentCid]
      })
    }
  },
  // 生命周期 - 创建完成（可以访问当前this实例）
  created() {
    this.getMenus()
  },
  // 生命周期 - 挂载完成（可以访问DOM元素）
  mounted() { },
  // 生命周期 - 创建之前
  beforeCreate() { },
  // 生命周期 - 挂载之前
  beforeMount() { },
  // 生命周期 - 更新之前
  beforeUpdate() { },
  // 生命周期 - 更新之后
  updated() { },
  // 生命周期 - 销毁之前
  beforeDestroy() { },
  // 生命周期 - 销毁完成
  destroyed() { },
  // 如果页面有keep-alive缓存功能，这个函数会触发
  activated() { }
}
</script>
<style scoped>
</style>

