<template>
  <div id="BasicLayout">
    <!-- 通用布局组件 -->
    <a-layout>
      <!--  导航栏  -->
      <a-layout-header class="layout-header">
        <!--  ant布局    -->
        <a-row :wrap="false">
          <a-col flex="300px">
            <!--   左边logo和文字     -->
            <div style="display: flex">
              <img src="../static/images/智能云.png" alt="智能协同云图库" style="height: 60px">
              <h3 style="margin-left: 20px">智能协同云图库</h3>
            </div>
          </a-col>
          <a-col flex="auto">
            <!--    中间导航栏    -->
            <a-menu v-model:selectedKeys="current" mode="horizontal" @click="menuClick">
              <a-menu-item key="/home">
                主页
              </a-menu-item>
              <a-menu-item key="/about">
                关于
              </a-menu-item>
              <a-menu-item key="/other">
                其它
              </a-menu-item>
            </a-menu>
          </a-col>
          <a-col flex="100px">
            <!--    右边登录注册/个人信息    -->
            <template v-if="loginUserStore.loginUser.userName">
              <a-avatar style="margin-right: 20px;">
                <template #icon>
                  <AntDesignOutlined />
                </template>
              </a-avatar>
              <span>{{ loginUserStore.loginUser.userName }}</span>
            </template>
            <template v-else>
              <a-button type="primary">登录</a-button>
            </template>
          </a-col>
        </a-row>
      </a-layout-header>
      <a-layout>
        <!--   左边菜单栏     -->
        <a-layout-sider class="layout-sider">
          <a-menu
            id="amenu"
            style="width: 100%"
            v-model:openKeys="openKeys"
            v-model:selectedKeys="selectedKeys"
            mode="inline"
            @click="handleClick"
          >
            <a-sub-menu key="sub1" @titleClick="titleClick">
              <template #title>菜单</template>
            </a-sub-menu>
          </a-menu>
        </a-layout-sider>
        <!--   内容-router-view   -->
        <a-layout-content class="latout-content">
          <router-view></router-view>
        </a-layout-content>
      </a-layout>
      <!--   底部关于我   -->
      <a-layout-footer class="layout-footer">
        <div>
          <a href="https://github.com/014-code">关于我：014-code</a>
        </div>
      </a-layout-footer>
    </a-layout>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import router from '@/router'
import { useRouter } from 'vue-router'
import { AntDesignOutlined } from '@ant-design/icons-vue'
import { useLoginUserStore } from '@/stores/counter.ts'
//当前选中菜单
const current = ref<string[]>(['home'])
//从pinia里面拿取到的用户信息
const loginUserStore = useLoginUserStore()

//发送获取用户信息接口?
// loginUserStore.fetchLoginUser()

/**
 * 监听路由变化-同步更新菜单选中
 */
router.afterEach((to, from, next) => {
  //to-现在所在页面地址
  current.value = [to.path]
})

/**
 * 菜单点击方法-不能直接拿组件方法里面的值，得结构确定是哪个
 */
function menuClick({ key }: { key: string }) {
  router.push({
    path: key
  })
}

</script>

<style lang="scss" scoped>

.layout-header {
  background-color: white;
}

.layout-sider {
  background-color: white;
}

.layout-footer {
  text-align: center;
  background-color: rgb(243, 243, 243);
}

.latout-content {
  height: 100vh;
}

</style>
